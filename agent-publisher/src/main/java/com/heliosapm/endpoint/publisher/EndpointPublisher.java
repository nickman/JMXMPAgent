/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.heliosapm.endpoint.publisher;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.json.JSONException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heliosapm.utils.config.ConfigurationHelper;
import com.heliosapm.utils.io.CloseableService;
import com.heliosapm.utils.io.StdInCommandHandler;

/**
 * <p>Title: EndpointPublisher</p>
 * <p>Description: Publishes a JMX monitoring discovery endpoint to zookeeper</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.endpoint.publisher.EndpointPublisher</code></p>
 */

public class EndpointPublisher implements ConnectionStateListener, Closeable {
	/** Shared ObjectMapper instance */
	private static final ObjectMapper jsonMapper = new ObjectMapper();
	
	/** The singleton instance */
	private static volatile EndpointPublisher instance = null;
	/** The singleton instance ctor lock */
	private static final Object lock = new Object();
	
	/** The config key for the ZooKeeper connect string */
	public static final String ZK_CONNECT_CONF = "streamhub.discovery.zookeeper.connect";
	/** The default ZooKeeper connect string */
	public static final String ZK_CONNECT_DEFAULT = "localhost:2181";

	/** The config key for the advertised service type */
	public static final String SERVICE_TYPE_CONF = "streamhub.discovery.servicetype";
	/** The default advertised service type */
	public static final String SERVICE_TYPE_DEFAULT = "/monitoring-endpoints";

	/** The config key for the zookeeper session timeout */
	public static final String DISC_SESS_TO_CONF = "streamhub.discovery.timeout.session";
	/** The default zookeeper session timeout in ms. */
	public static final int DISC_SESS_TO_DEFAULT = 60 * 1000;
	
	/** The config key for the zookeeper connect timeout */
	public static final String DISC_CONN_TO_CONF = "streamhub.discovery.timeout.connection";
	/** The default zookeeper session timeout in ms. */
	public static final int DISC_CONN_TO_DEFAULT = 15 * 1000;

	/** The connect string for the zookeep ensemble */
	protected final String zkConnect;
	/** The endpoint service type */
	protected final String serviceType;
	/** The zookeeper connect timeout in ms. */
	protected final int connectionTimeout;
	/** The zookeeper session timeout in ms. */
	protected final int sessionTimeout;

	/** The zookeeper client instance to publish with */
	protected CuratorZookeeperClient zooClient;
	/** The zookeeper curator framework instance to publish with */
	protected final CuratorFramework curator;
	/** The raw zookeeper client instance to publish with */
	protected ZooKeeper zoo;
	
	/** Indicates if the curator client is currently connected */
	protected final AtomicBoolean connected = new AtomicBoolean(false);
	/** Indicates if a curator client disconnect is intended */
	protected final AtomicBoolean intendToClose = new AtomicBoolean(false);
	/** The service cache thread factory */
	protected final ThreadFactory threadFactory = new ThreadFactory() {
		final AtomicInteger serial = new AtomicInteger();
		@Override
		public Thread newThread(final Runnable r) {
			final Thread t = new Thread(r, "EndpointPublisherThread#" + serial.incrementAndGet());
			t.setDaemon(true);
			return t;
		}
	};
	/** The service cache callback executor */
	protected final ExecutorService executor = Executors.newCachedThreadPool(threadFactory);
	
	
	/** A set of unregistered endpoints */
	protected final ConcurrentHashMap<String, AdvertisedEndpoint> unregistered = new ConcurrentHashMap<String, AdvertisedEndpoint>(); 
	/** A set of registered endpoints */
	protected final ConcurrentHashMap<String, AdvertisedEndpoint> registered = new ConcurrentHashMap<String, AdvertisedEndpoint>(); 
	/** A set of registered AdvertisedEndpoint event listeners */
	protected final Set<AdvertisedEndpointListener> listeners = new LinkedHashSet<AdvertisedEndpointListener>();
	/**
	 * Acquires the publisher instance
	 * @return the publisher instance
	 */
	public static EndpointPublisher getInstance() {
		if(instance == null) {
			synchronized(lock) {
				if(instance == null) {
					instance = new EndpointPublisher();
				}
			}
		}
		return instance;
	}
	
	
	/**
	 * Creates a new EndpointPubSub
	 */
	private EndpointPublisher() {
		CloseableService.getInstance().register(this);
		zkConnect = ConfigurationHelper.getSystemThenEnvProperty(ZK_CONNECT_CONF, ZK_CONNECT_DEFAULT);
		serviceType = ConfigurationHelper.getSystemThenEnvProperty(SERVICE_TYPE_CONF, SERVICE_TYPE_DEFAULT);
		connectionTimeout = ConfigurationHelper.getIntSystemThenEnvProperty(DISC_CONN_TO_CONF, DISC_CONN_TO_DEFAULT);
		sessionTimeout = ConfigurationHelper.getIntSystemThenEnvProperty(DISC_SESS_TO_CONF, DISC_SESS_TO_DEFAULT);
		curator = CuratorFrameworkFactory.newClient(zkConnect, sessionTimeout, connectionTimeout, new ExponentialBackoffRetry( 1000, 3 ));
		curator.getConnectionStateListenable().addListener(this);
		curator.start();
		connectClients();
	}
	
	/**
	 * Connects the curator zookeeper client and the raw zookeeper client
	 */
	protected void connectClients() {
		try {
			zooClient = curator.getZookeeperClient();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to reconnect curator zookeeper client", ex);
		}
		try {
			zoo = zooClient.getZooKeeper();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to reconnect raw zookeeper client", ex);
		}
		
	}

	/**
	 * Stops and closes the publisher
	 * @throws IOException won't be thrown, but required
	 */
	public void close() throws IOException {
		intendToClose.set(true);		
		try { curator.close(); } catch (Exception x) {/* No Op */}
		unregistered.clear();
		registered.clear();		
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.apache.curator.framework.state.ConnectionStateListener#stateChanged(org.apache.curator.framework.CuratorFramework, org.apache.curator.framework.state.ConnectionState)
	 */
	@Override
	public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
		log("ZK Connection State Change to [%s]", newState.name());
		connected.set(newState.isConnected());
		switch(newState) {			
			case CONNECTED:							
				registerPending();
				break;
			case LOST:
				setAllPending();
				break;
			case READ_ONLY:
				break;
			case RECONNECTED:
				registerPending();
				break;
			case SUSPENDED:
				break;
			default:
				break;
		
		}		
	}

	
	/**
	 * Attempts to register the passed end point.
	 * If registration fails or the client is disconnected, will retry on connection resumption.
	 * @param endpoint The endpoint to register
	 */
	public void register(final AdvertisedEndpoint endpoint) {
		if(endpoint==null) throw new IllegalArgumentException("The passed endpoint was null");
		unregistered.put(endpoint.getId(), endpoint);
		if(connected.get()) {
			try {
				try {
					try { zoo.delete(endpoint.getZkPath(serviceType), -1); } catch (Exception x) {/* No Op */}
					final String zkPath = endpoint.getZkPath(serviceType);
					curator.create()
						.creatingParentContainersIfNeeded()
						.withMode(CreateMode.EPHEMERAL)
						.inBackground(new BackgroundCallback() {
							@Override
							public void processResult(final CuratorFramework client, final CuratorEvent event) throws Exception {								
								log("Register callback. rc:%s, path:[%s], ctx:[%s], name:[%s]", event.getResultCode(), event.getPath(), event.getContext(), event.getName());
								if(event.getResultCode() >= 0) {									
									registered.put(endpoint.getId(), endpoint);
									unregistered.remove(endpoint.getId());																
								}
							}
						}, executor).forPath(zkPath, endpoint.toByteArray());
				} catch (Exception ex) {
					elog("Failed to register endpoint [%s]",  ex, endpoint.getId());
				}
				
			} catch (Exception ex) {
				elog("Failed to register endpoint [%s]",  ex, endpoint);
			}
		}
	}
	
	/**
	 * Attempts to register the passed end point.
	 * If registration fails or the client is disconnected, will retry on connection resumption.
	 * @param jmxUrl The advertised JMXMP URL
	 * @param endPoints The advertised monitoring categories
	 */
	public void register(final String jmxUrl, final String...endPoints) {
		final String app = AgentName.getInstance().getAppName();
		final String host = AgentName.getInstance().getHostName();
		final AdvertisedEndpoint ae = new AdvertisedEndpoint(jmxUrl, app, host, endPoints);
		register(ae);
	}
	
	/**
	 * Registers pending or disconnected endpoints
	 */
	protected void registerPending() {
		LinkedHashSet<AdvertisedEndpoint> toRegister = new LinkedHashSet<AdvertisedEndpoint>(unregistered.values());
		for(AdvertisedEndpoint si: toRegister) {
			register(si);
		}
	}
	
	/**
	 * Sets all registered endpoints to unregistered
	 */
	protected void setAllPending() {
		unregistered.putAll(registered);
		registered.clear();
	}


	/**
	 * Returns 
	 * @return the serviceType
	 */
	public String getServiceType() {
		return serviceType;
	}
	
	/**
	 * Returns 
	 * @return the connectionTimeout
	 */
	public int getConnectionTimeout() {
		return connectionTimeout;
	}


	/**
	 * Returns 
	 * @return the sessionTimeout
	 */
	public int getSessionTimeout() {
		return sessionTimeout;
	}


	/**
	 * Returns 
	 * @return the connected
	 */
	public AtomicBoolean getConnected() {
		return connected;
	}
	
	/**
	 * Deserializes a JSON formatted string to a specific class type
	 * <b>Note:</b> If you get mapping exceptions you may need to provide a 
	 * TypeReference
	 * @param json The string to deserialize
	 * @param pojo The class type of the object used for deserialization
	 * @return An object of the {@code pojo} type
	 * @throws IllegalArgumentException if the data or class was null or parsing 
	 * failed
	 * @throws JSONException if the data could not be parsed
	 */
	public static final <T> T parseToObject(final String json, final Class<T> pojo) {
		if (json == null || json.isEmpty())
			throw new IllegalArgumentException("Incoming data was null or empty");
		if (pojo == null)
			throw new IllegalArgumentException("Missing class type");

		try {
			return jsonMapper.readValue(json, pojo);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	public static void main(String[] args) {
		log("PublisherTest");
		final String template = "{ \"jmx\" : \"service:jmx:jmxmp://localhost:%s\", \"host\" : \"%s\", \"app\" : \"%s\",  \"port\" : \"%s\"," + 
				"\"endpoints\" : [\"kafka\", \"jvm\"] }";
		final EndpointPublisher p = EndpointPublisher.getInstance();
		//final String[] hosts = {"hostA", "hostB", "hostC"};
		final String[] hosts = {"hostE", "hostF", "hostG"};
		final String[] apps = {"appX", "appY", "appZ"};
		int portCounter = 1420;
		for(String host: hosts) {
			for(String app: apps) {
				String s = String.format(template, portCounter, host, app, portCounter);
				final AdvertisedEndpoint ae = parseToObject(s, AdvertisedEndpoint.class);
				p.register(ae);
				portCounter++;
			}
		}
//		for(int i = 0; i < 20; i++) {
//			String s = String.format(template, 1420 + i, "FooApp", 1420 + i);
//			log("  ---- REG:" + s);
//			final AdvertisedEndpoint ae = JSONOps.parseToObject(s, AdvertisedEndpoint.class);
//			p.register(ae);
//		}
		StdInCommandHandler.getInstance()
		.registerCommand("stop", new Runnable(){
			public void run() {
				try {
					p.close();
				} catch (Exception x) {/* No Op */}
				System.exit(0);
			}
		})
		.run();

		
	}
	
	/**
	 * Low maintenance formatted out logger
	 * @param fmt The message format
	 * @param args The message fillins
	 */
	public static void log(final Object fmt, final Object...args) {
		System.out.println(String.format(fmt.toString(), args));
	}
	
	/**
	 * Low maintenance formatted err logger
	 * @param fmt The message format
	 * @param t An optional throwable
	 * @param args The message fillins
	 */
	public static void elog(final Object fmt, final Throwable t, final Object...args) {
		System.err.println(String.format(fmt.toString(), args));
		if(t!=null) t.printStackTrace(System.err);
	}

	/**
	 * Low maintenance formatted err logger
	 * @param fmt The message format
	 * @param args The message fillins
	 */
	public static void elog(final Object fmt, final Object...args) {
		elog(fmt, null, args);
	}
	
}
