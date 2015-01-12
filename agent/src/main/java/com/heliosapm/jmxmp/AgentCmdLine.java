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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;

/**
 * <p>Title: AgentCmdLine</p>
 * <p>Description: Command line processor for the agent</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.AgentCmdLine</code></p>
 */

public class AgentCmdLine {	
	/** Static class logger */
	protected static final Logger log = Logger.getLogger(AgentCmdLine.class.getName());
	protected static final Set<String> COMMANDS = new HashSet<String>(Arrays.asList(
			"listjvms", 				// Lists the JVMs that are currently running
			"install"
	));
	@Argument(index=0, usage="The command to execute")
	/** The command to execute */
	protected String command = null;
	
	

	/**
	 * Command line entry point
	 * @param args See usage
	 */
	public static void main(String[] args) {
		AgentCmdLine cl = new AgentCmdLine();		
		CmdLineParser parser = new CmdLineParser(cl);
		try {
			if(args.length==0) {
				parser.printUsage(System.out);
				return;
			}
			parser.parseArgument(args);
			cl.run(args);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			parser.printUsage(System.err);
		}
	}
	
	protected void run(final String...args) {
		if(COMMANDS.contains(command.toLowerCase())) {
			Command com = Command.valueOf(command.toUpperCase());
			System.out.println(com.execute(args));
		}
	}

}
