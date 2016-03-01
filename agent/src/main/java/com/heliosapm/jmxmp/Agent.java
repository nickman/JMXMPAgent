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

import java.lang.instrument.Instrumentation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.heliosapm.jmxmp.spec.SpecField;
import com.heliosapm.jmxmp.spec.SpecParser;

/**
 * <p>Title: Agent</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.Agent</code></p>
 */

public class Agent {

	/** The agent provided instrumentation */
	public static Instrumentation INSTRUMENTATION = null;
	/** Static class logger */
	protected static final Logger log = Logger.getLogger(Agent.class.getName());
	
	
	/** Keep a reference to created connectors keyed by the listening port */
	private static final Map<Integer, JMXMPConnector> connectors = new ConcurrentHashMap<Integer, JMXMPConnector>();
	/** A scheduler in case we need to wait for an MBeanServer to show up */
	private static volatile ScheduledExecutorService scheduler = null;
	
	/**
	 * The agent premain
	 * @param agentArgs The agent arguments
	 * @param inst The agent instrumentation
	 */
	public static void premain(final String agentArgs, final Instrumentation inst) {
		INSTRUMENTATION = inst;
		try {			
			final Map<Integer, Map<SpecField, String>> parsedSpecs = SpecParser.parseSpecs(agentArgs);
			for(Map.Entry<Integer, Map<SpecField, String>> entry: parsedSpecs.entrySet()) {
				final Map<SpecField, String> spec = entry.getValue();
				final String domain = spec.get(SpecField.DOMAIN);
				if(JMXMPConnector.getMBeanServer(domain) != null) {
					JMXMPConnector connector = new JMXMPConnector(spec);
					connectors.put(entry.getKey(), connector);
					connector.start();
				} else {
					schedule(spec);
				}
			}			
		} catch (Throwable ex) {
			ex.printStackTrace(System.err);
		}
	}

	/**
	 * The agent premain with no instrumentation
	 * @param agentArgs The agent arguments
	 */
	public static void premain(final String agentArgs) {
		premain(agentArgs, null);
	}
	
	/**
	 * The agent main 
	 * @param agentArgs The agent arguments
	 * @param inst The agent instrumentation
	 */
	public static void agentmain(final String agentArgs, final Instrumentation inst) {
		premain(agentArgs, inst);
	}

	/**
	 * The agent main with no instrumentation
	 * @param agentArgs The agent arguments
	 */
	public static void agentmain(final String agentArgs) {
		premain(agentArgs, null);
	}
	
	/**
	 * The requested MBeanServer was not found, so we'll retry  for a while and see if it shows up
	 * @param spec The spec with the missing MBeanServer 
	 */
	private static void schedule(final Map<SpecField, String> spec) {
		final String domain = spec.get(SpecField.DOMAIN);
		final int port = Integer.parseInt(spec.get(SpecField.PORT));
		final ScheduledFuture<?>[] sf = new ScheduledFuture[1];  
		sf[0] = getScheduler().scheduleWithFixedDelay(new Runnable(){
			@Override
			public void run() {
				if(JMXMPConnector.getMBeanServer(domain)!=null) {
					sf[0].cancel(false);
					try {
						JMXMPConnector connector = new JMXMPConnector(spec);
						connectors.put(port, connector);
						connector.start();
					} catch (Exception ex) {
						log.log(Level.SEVERE, "Failed to start JMXMP server for spec [" + spec + "]. Retries are cancelled");
					}
				}
			}
		}, 5, 5, TimeUnit.SECONDS);
		
	}
	
	private static ScheduledExecutorService getScheduler() {
		if(scheduler==null) {
			synchronized(Agent.class) {
				if(scheduler==null) {
					scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory(){
						final AtomicInteger serial = new AtomicInteger();
						@Override
						public Thread newThread(final Runnable r) {
							final Thread t = new Thread("MBeanServerPollingThread#" + serial.incrementAndGet());
							t.setDaemon(true);
							return t;
						}
					});
				}
			}
		}
		return scheduler;
	}
	
	private Agent() {}
	
}
