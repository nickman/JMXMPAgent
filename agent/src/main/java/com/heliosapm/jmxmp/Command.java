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

import com.beust.jcommander.IStringConverter;
import com.heliosapm.jmxmp.commands.HelpCommand;
import com.heliosapm.jmxmp.commands.InstallCommand;
import com.heliosapm.jmxmp.commands.ListJVMsCommand;

/**
 * <p>Title: Command</p>
 * <p>Description: Enumerates the commands and their associated command class</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.Command</code></p>
 */

public enum Command implements ICommand, IStringConverter<Command> {
	LISTJVMS(new ListJVMsCommand()),
	INSTALL(new InstallCommand()),
	HELP(new HelpCommand());
	
	
	private Command(final ICommand command) {
		this.command = command;
	}
	
	final ICommand command;
	
	/**
	 * Returns the Command for the passed command name
	 * @param commandName The command name which will be trimmed and upper-cased
	 * @return The decoded command
	 */
	public static Command command(final CharSequence commandName) {
		if(commandName==null) throw new IllegalArgumentException("The passed command name was null");
		final String command = commandName.toString().toUpperCase();
		if(command.isEmpty()) throw new IllegalArgumentException("The passed command name was empty");
		try {
			return Command.valueOf(command);
		} catch (Exception ex) {
			throw new IllegalArgumentException("The passed command [" + command + "] was not a valid Command");
		}
	}
	
	/**
	 * Indicates if the passed command name can be decoded to a Command
	 * @param commandName The command name to test
	 * @return true if it was a valid command, false otherwise
	 */
	public static boolean isCommand(final CharSequence commandName) {
		if(commandName==null) return false;
		final String command = commandName.toString().toUpperCase();
		try {
			Command.valueOf(command);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.ICommand#execute(java.lang.String[])
	 */
	@Override
	public String execute(String... args) {
		return command.execute(args);
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.ICommand#getDescription()
	 */
	@Override
	public String getDescription() {		
		return command.getDescription();
	}

	@Override
	public Command convert(final String value) {
		try {
			return Command.valueOf(value.trim().toUpperCase());
		} catch (Exception ex) {
			throw new IllegalArgumentException("Invalid Command: [" + value + "]");
		}
	}
	
	
}
