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
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

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
	protected final int port;
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
		log.setLevel(AgentBoot.LOG_LEVEL);
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
			try {
				connectorServer.start();
				log.info("Started JMXMPServer on [" + bindAddress + ":" + port + "] for MBeanServer [" + domain + "]");				
			} catch (Exception ex) {
				log.log(Level.SEVERE, "Failed to start JMXMPServer on [" + bindAddress + ":" + port + "] for MBeanServer [" + domain + "]", ex);
				throw ex;
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
