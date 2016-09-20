/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.heliosapm.jmxmp;

import java.io.Closeable;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import com.heliosapm.jmxmp.spec.SpecField;
import com.heliosapm.utils.jmx.JMXHelper;

/**
 * <p>Title: JMXMPConnector</p>
 * <p>Description: JMXMP Connector Server installer</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.JMXMPConnector</code></p>
 */

public class JMXMPConnector implements Closeable {
	/** The connector server bind address */
	protected final String bindAddress;
	/** The connector server listening port */
	protected int port;
	/** The MBeanServer the connector is exposing */
	protected final MBeanServer server;
	/** The connector server JMXServiceURL */
	protected final JMXServiceURL jmxUrl;
	/** The MBeanServer default domain */
	protected final String domain;
	/** Instance logger */
	protected final Logger log;
	
	/** The connector server instance */
	protected final JMXConnectorServer connectorServer;
	
	/** The connector server JMXServiceURL template */
	public static final String JMX_URL = "service:jmx:jmxmp://%s:%s";
	
	/** The system and agent properties key prefix where the jmxmp url will be published */
	public static final String URL_PROPERTY = "javax.management.remote.jmxmp.url.";
	
	/**
	 * Creates a new JMXMPConnector
	 * @param specs The command line supplied JMXMP parameters 
	 * @throws Exception thrown if the connector server cannot be created 
	 */
	public JMXMPConnector(final Map<SpecField, String> specs) throws Exception {
		this(specs.get(SpecField.IFACE), Integer.parseInt(specs.get(SpecField.PORT)), getMBeanServer(specs.get(SpecField.DOMAIN)));
	}

	/**
	 * Returns the MBeanServer matching the passed domain, or null if one is not found
	 * @param domain The JMX domain
	 * @return the MBeanServer or null
	 */
	public static MBeanServer getMBeanServer(final String domain) {
		if(domain.equals("DefaultDomain")) return ManagementFactory.getPlatformMBeanServer();
		for(MBeanServer server: MBeanServerFactory.findMBeanServer(null)) {			
			final String serverDomain = server.getDefaultDomain();
			if(serverDomain==null && domain.equals("null")) return server;  // some custom MBeanServers create the platform with a null domain
			if(server.getDefaultDomain().equals(domain)) return server;
		}
		return null;
	}
	
	/**
	 * Creates a new JMXMPConnector
	 * @param bindAddress The connector server bind address
	 * @param port The connector server listening port
	 * @param server The MBeanServer the connector is exposing
	 * @throws Exception thrown if the connector server cannot be created 
	 */
	public JMXMPConnector(final String bindAddress, final int port, final MBeanServer server) throws Exception {
		String tmp = server.getDefaultDomain();
		domain = (tmp==null || tmp.trim().isEmpty()) ? "DefaultDomain" : tmp;		
		log = Logger.getLogger("jmxmp." + bindAddress + "." + port + "." + domain);
		this.bindAddress = bindAddress;
		this.port = port;
		this.server = server;
		try {
			jmxUrl = new JMXServiceURL(String.format(JMX_URL, bindAddress, port));
			connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(jmxUrl, null, server);
			log.info("Created JMXMPServer on [" + bindAddress + ":" + port + "] for MBeanServer [" + domain + "]");
		} catch (Exception ex) {
			log.log(Level.SEVERE, "Failed to create JMXMPServer on [" + bindAddress + ":" + port + "] for MBeanServer [" + domain + "]", ex);
			throw ex;
		}
	}
	
	/**
	 * Starts the connector server if it is not started 
	 * @throws Exception thrown if the connector server cannot be started
	 */
	public void start() throws Exception {
		if(!connectorServer.isActive()) {
			final Throwable[] exRef = new Throwable[1];
			final String[] jmxUrl = new String[1];
			final boolean[] complete = new boolean[]{false};
			final Thread starterThread = new Thread("JMXMPServer-Starter-Thread") {
				@Override
				public void run() {
					try {
						connectorServer.start();
						jmxUrl[0] = connectorServer.getAddress().toString();
						log.info("Started JMXMPServer on [" + jmxUrl[0] + "] for MBeanServer [" + domain + "]");
						complete[0] = true;
					} catch (Exception ex) {
						log.log(Level.SEVERE, "Failed to start JMXMPServer on [" + bindAddress + ":" + port + "] for MBeanServer [" + domain + "]", ex);
						exRef[0] = ex;
					}					
				}
			};
			starterThread.setDaemon(true);
			starterThread.start();
			try {
				starterThread.join(10000);
				if(!complete[0]) {
					if(exRef[0] != null) {
						throw new Exception("Failed to start JMXMP server", exRef[0]);
					} 
					throw new Exception("JMXMP server not started after 10 secs.");
				}
				final String install = jmxUrl[0];
				final String key = URL_PROPERTY + domain;
				System.setProperty(key, install);
				JMXHelper.getAgentProperties().setProperty(key, install);
			} catch (InterruptedException iex) {
				// should not happen
				throw new Exception("Thread interrupted while waiting for JMXMP server to start", iex);
			}		
		}
	}
	
	/**
	 * Stops the connector server if it is started
	 */
	public void stop() {
		if(connectorServer.isActive()) {
			try { 
				connectorServer.stop();
				log.info("Stopped JMXMPServer on [" + bindAddress + ":" + port + "] for MBeanServer [" + domain + "]");
			} catch (Exception ex) {
				log.warning("Failure stopping JMXMPServer on [" + bindAddress + ":" + port + "] for MBeanServer [" + domain + "]:" + ex);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		stop();		
	}

	/**
	 * Returns the bind address
	 * @return the bindAddress
	 */
	public String getBindAddress() {
		return bindAddress;
	}

	/**
	 * Returns the listening port
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the exposed MBeanServer
	 * @return the MBeanServer
	 */
	public MBeanServer getServer() {
		return server;
	}

	/**
	 * Returns the JMXServiceURL
	 * @return the JMXServiceURL
	 */
	public JMXServiceURL getJmxUrl() {
		return jmxUrl;
	}

	/**
	 * Returns the MBeanServer domain
	 * @return the MBeanServer domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * Returns the JMXConnectorServer
	 * @return the connectorServer
	 */
	public JMXConnectorServer getConnectorServer() {
		return connectorServer;
	}
	
	
}
