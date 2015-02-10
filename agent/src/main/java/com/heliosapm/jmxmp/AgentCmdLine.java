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

import java.util.ArrayList;
import java.util.logging.Logger;

import com.beust.jcommander.IDefaultProvider;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

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
	
	public static JCommander jc = new JCommander();
	
	/*
	 * JMXDomain,port,iface
	 * Supress Batch Service Install
	 */
	
//	@Argument(index=0, metaVar="command", usage="The command to execute (required)", required=true, handler=SubCommandHandler.class)
//	@SubCommands({
//		@SubCommand(name="help", impl=HelpCommand.class),
//		@SubCommand(name="listjvms", impl=ListJVMsCommand.class),
//		@SubCommand(name="install", impl=InstallCommand.class)
//	})
	
	
	
	

	/**
	 * Command line entry point
	 * @param args See usage
	 */
	public static void main(String[] args) {
		if(args==null || args.length==0) {
			args = new String[]{"HELP"};
		}
		try {
			jc.setCaseSensitiveOptions(false);
			for(Command c: Command.values()) {
				jc.addCommand(c.command);
			}
			jc.parse(args);
//			command.execute(passOnArgs);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
	
//	public static String generateHelp(final String command) {
//		final StringBuilder b = new StringBuilder();
//		if(command==null || command.trim().isEmpty() || !Command.isCommand(command)) {
//			b.append("\nCommands:");
//			for(Command c: Command.values()) {
//				b.append("\n\t").append(c.name()).append(":").append(c.getDescription());
//			}
//		} else {
//			final Command comm = Command.command(command);
//			b.append("Command: ").append(comm.name()).append("\n");
////			final CmdLineParser parser = new CmdLineParser(comm);
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			parser.printUsage(baos);
//			try {
//				baos.flush();
//				b.append(baos.toString(Charset.defaultCharset().name())).append("\n");
//			} catch (Exception ex) {
//				ex.printStackTrace(System.err);
//				throw new RuntimeException(ex);
//			}			
//		}
//		return b.toString();
//	}
	
//	protected void run(final String...args) {
//		try {
//			if(level!=null) {
//				Level l = Level.parse(level);
//				AgentBoot.setLoggerLevel(l);
//				log.info("Agent Logger set to [" + l + "]");
//			}
//		} catch (Exception ex) {
//			System.err.println("Invalid Logging Level [" + level + "]. Ignoring.");
//		}
//		Command com = null;
//		try {
//			com = Command.command(command.toUpperCase());
//		} catch (Exception ex) {
//			System.err.println("Invalid Command [" + command + "]");
//			System.err.println(generateHelp(null));
//			return;
//		}
//		final int nal = args.length-1;
//		log.log(Level.FINE, "Invoking Command: {0}, NonComArgCount: {1}, Args: {2}", new Object[]{com.name(), nal, Arrays.toString(args)});
//		String[] noCmdArgs = new String[nal];
//		if(nal>0) System.arraycopy(args, 1, noCmdArgs, 0, nal);
//		System.out.println(com.execute(noCmdArgs));
//	}

}
