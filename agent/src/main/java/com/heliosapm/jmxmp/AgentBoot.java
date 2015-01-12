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
import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

/**
 * <p>Title: AgentBoot</p>
 * <p>Description: Reflectively invoked agent bootstrap</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.AgentBoot</code></p>
 */

public class AgentBoot {
	/** The global instrumentation instance */
	public static Instrumentation INSTRUMENTATION = null;
	
	/** The default config */
	public static final String DEFAULT_CONFIG = "JMXMP:DefaultDomain,1295,127.0.0.1";
	/** The command delimeter */
	public static final String COMMAND_DELIM = "|";
	/** The command splitter pattern */
	public static final Pattern COMMAND_SPLITTER = Pattern.compile("\\" + COMMAND_DELIM);
	/** The command splitter pattern */
	public static final Pattern COMMAND_ARG_SPLITTER = Pattern.compile(":|,");
	
	/** Empty string arr const */
	public static final String[] EMPTY_ARR = {};
	/** JMXMP Connector Server Command */
	public static final String JMXMPSERVER = "JMXMP";
	/** Logger level Command */
	public static final String LOGLEVEL = "LOGLEVEL";
	
	/** The classloader supplied by the agent */
	public static ClassLoader AGENT_CL = null;
	
	/** The agent logging level */
	public static Level LOG_LEVEL = Level.FINER;
	
	/** A list of closeable to kill on shutdown */
	private static final List<Closeable> CLOSEABLES = new ArrayList<Closeable>();
	
	/** Static class logger */
	protected static final Logger log = Logger.getLogger(AgentBoot.class.getName());
	
	static {
		if(log.getHandlers().length==0) {
			log.addHandler(new ConsoleHandler(){
				@Override
				protected synchronized void setOutputStream(OutputStream out) throws SecurityException {
					super.setOutputStream(System.out);
					setLevel(Level.ALL);
				}
			});
		}
	}
	
	public static void boot(final ClassLoader classLoader, final String agentArgs, final Instrumentation instrumentation) {
		AGENT_CL = classLoader;
		// install JMXMP server
		// Params:  port, iface, mbeanserver names
		// Batch JMX Service
		// Set system props
		// Register MBeans
		
		
		final String[] commands = trimSplit(agentArgs, COMMAND_SPLITTER);
		// Do the logging level command first
		for(String command: commands) {
			if(command.startsWith(LOGLEVEL)) {
				execLoggerLevelCommand(COMMAND_ARG_SPLITTER.split(command));
			}
		}
		for(String command: commands) {
			String[] commandArgs = COMMAND_ARG_SPLITTER.split(command);
			if(commandArgs.length>0) {
				if(LOGLEVEL.equalsIgnoreCase(commandArgs[0])) continue;
				if(JMXMPSERVER.equalsIgnoreCase(commandArgs[0])) execJMXMPCommand(commandArgs);
			}
		}

	}
	
	/**
	 * Sets the logger level and updates the level of the handlers to the same
	 * @param level the logger level
	 */
	protected static void setLoggerLevel(final Level level) {
		if(level!=null) {
			LOG_LEVEL = level;
			log.setLevel(LOG_LEVEL);
		}
	}
	
	protected static String[] trimSplit(final String value, final Pattern splitter) {
		if(value==null || value.trim().isEmpty()) return EMPTY_ARR;
		final String[] values = splitter.split(value.trim());
		final List<String> nonBlankValues = new ArrayList<String>();
		for(String v: values) {
			if(v==null || v.trim().isEmpty()) continue;
			nonBlankValues.add(v.trim());
		}
		return nonBlankValues.toArray(new String[0]);
	}
	
	/**
	 * Installs and starts a JMXMP server
	 * @param args The command arguments
	 */
	protected static void execJMXMPCommand(final String...args) {
		if(args.length != 4) {
			log.warning("Invalid JMXMP Command " + Arrays.toString(args));
			return;
		}
		MBeanServer server = findMBeanServer(args[1]);
		if(server==null) {
			log.info("MBeanServer for [" + args[1] + " not found. Will poll for it.");
			return;
		}
		int port = -1;
		try {
			port = Integer.parseInt(args[2]);
			if(port < 0) throw new Exception();
		} catch (Exception ex) {
			log.warning("Invalid port value in JMXMPCommand " + Arrays.toString(args));
			return;
		}
		try {
			JMXMPConnector connector = new JMXMPConnector(args[3], port, server);
			connector.start();
			CLOSEABLES.add(connector);
		} catch (Exception ex) {
			log.log(Level.SEVERE, "Failed to start JMXMP server " + Arrays.toString(args), ex);
		}
		// JMXMP:DefaultDomain,1295,127.0.0.1
		
	}
	
	/**
	 * Finds the MBeanServer with the passed default domain.
	 * If the domain is null, empty or equal to <b><code>"DefaultDomain"</code></b>, 
	 * will return the platform MBeanServer.
	 * @param defaultDomain The default domain to find the MBeanServer for
	 * @return the located MBeanServer or null if one was not found
	 */
	protected static MBeanServer findMBeanServer(String defaultDomain) {
		if(defaultDomain==null || defaultDomain.trim().isEmpty() || "DefaultDomain".equals(defaultDomain)) return ManagementFactory.getPlatformMBeanServer();
		for(MBeanServer server: MBeanServerFactory.findMBeanServer(null)) {
			if(defaultDomain.equals(defaultDomain)) return server;
		}
		return null;
	}
	
	/**
	 * Executes the logging level command
	 * @param args The command arguments
	 */
	protected static void execLoggerLevelCommand(final String...args) {
		if(args.length>1) {
			String levelName = args[1];
			try {
				Level level = Level.parse(levelName);
				LOG_LEVEL = level;
				log.log(LOG_LEVEL, "Logging level set to [" + level.getName() + "]");
			} catch (Exception ex) {
				log.severe("Unrecognized logging level [" + levelName + "]");
			}
		}
	}
	
}
