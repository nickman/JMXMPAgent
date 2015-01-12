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

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * <p>Title: AgentCmdLine</p>
 * <p>Description: Command line processor for the agent</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.AgentCmdLine</code></p>
 */

public class AgentCmdLine {	
	/** Static class logger */
	protected static final Logger log = Logger.getLogger(AgentBoot.class.getName());
	protected static final Set<String> COMMANDS = new HashSet<String>(Arrays.asList(
			"listjvms", 				// Lists the JVMs that are currently running
			"install"
	));
	
	@Argument(index=0, usage="The command to execute (required)", required=true)
	/** The command to execute */
	protected String command = null;
	
	@Option(name="-level", usage="-level <JUL Logging Level> (optional)")
	/** Option to set the agent logger level */
	protected String level= null;
	
	
	protected static String USAGE = null;

	/**
	 * Command line entry point
	 * @param args See usage
	 */
	public static void main(String[] args) {
		AgentCmdLine cl = new AgentCmdLine();		
		CmdLineParser parser = new CmdLineParser(cl);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			parser.printUsage(baos);
			baos.flush();
			USAGE = new String(baos.toByteArray(), Charset.defaultCharset());
			parser.parseArgument(args);
			if(args.length==0) {
				System.out.println(USAGE);
				return;
			}			
			cl.run(args);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			System.err.println(USAGE);
		}
	}
	
	protected void run(final String...args) {
		try {
			if(level!=null) {
				Level l = Level.parse(level);
				AgentBoot.setLoggerLevel(l);
				log.info("Agent Logger set to [" + l + "]");
			}
		} catch (Exception ex) {
			System.err.println("Invalid Logging Level [" + level + "]. Ignoring.");
		}
		Command com = null;
		try {
			com = Command.valueOf(command.toUpperCase());
		} catch (Exception ex) {
			System.err.println("Invalid Command [" + command + "]");
			System.err.println(USAGE);
			return;
		}
		if(COMMANDS.contains(command.toLowerCase())) {			
			// New args without the command
			final int nal = args.length-1;
			log.log(Level.FINE, "Invoking Command: {0}, NonComArgCount: {1}, Args: {2}", new Object[]{com.name(), nal, Arrays.toString(args)});
			String[] noCmdArgs = new String[nal];
			if(nal>0) System.arraycopy(args, 1, noCmdArgs, 0, nal);
			System.out.println(com.execute(noCmdArgs));
		}
	}

}
