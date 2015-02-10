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
package com.heliosapm.jmxmp.commands;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.heliosapm.jmxmp.AgentCmdLine;
import com.heliosapm.jmxmp.ICommand;

/**
 * <p>Title: HelpCommand</p>
 * <p>Description: Command to print help</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.commands.HelpCommand</code></p>
 */
@Parameters(commandDescription = "Prints Command Line Help", commandNames="help")
public class HelpCommand implements ICommand {
	
	public HelpCommand() {}

	@Parameter(names="-command", description="The name of the command to print help for, or none for all", help=true )
	/** The command to get help for */
	private List<String> commands = null;

	
	
	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.ICommand#execute(java.lang.String[])
	 */
	@Override
	public String execute(String... args) {
		if(commands==null) AgentCmdLine.jc.usage();
		else AgentCmdLine.jc.usage(commands.get(0));
		return "Help";
	}
	
	public String main(String...args) {
		return execute(args);
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.ICommand#getDescription()
	 */
	@Override
	public String getDescription() {		
		return "Provides help on individual commands";
	}

}
