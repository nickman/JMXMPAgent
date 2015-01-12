/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2015, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package com.heliosapm.jmxmp.commands;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.heliosapm.attachme.VirtualMachine;
import com.heliosapm.attachme.VirtualMachineDescriptor;
import com.heliosapm.jmxmp.ICommand;

/**
 * <p>Title: ListJVMsCommand</p>
 * <p>Description: Command to print details of JVMs running on current platform</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.commands.ListJVMsCommand</code></p>
 */

public class ListJVMsCommand implements ICommand {

	/** Option to print the full VM display */
	@Option(name="-full", usage="Include to print the full VM display")	
	protected boolean full = false;
	/** Option to print the JMX/RMI URL */
	@Option(name="-jmxconn", usage="Include to print the JMX/RMI URL")	
	protected boolean jmxUrl = false;
	
	/** The options parser */
	final CmdLineParser parser = new CmdLineParser(this);

	/**
	 * Creates a new ListJVMsCommand
	 */
	public ListJVMsCommand() {

	}
	
	private void reset() {
		full = false;
		jmxUrl = false;
	}

	@Override
	public String execute(String... args) {
		reset();
		try {
			parser.parseArgument(args);
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			throw new RuntimeException("Failed to parse options in [" + getClass().getName() + "]", ex);
		}
		final StringBuilder b = new StringBuilder();
		try {
			for(VirtualMachineDescriptor vmd: VirtualMachine.list()) {
				if(PID.equals(vmd.id())) continue;
				String displayName = vmd.displayName();
				if(!full) {
					if(displayName==null || displayName.trim().isEmpty()) {
						displayName = "???";
						//System.out.println(String.format("No Displ for [%s], Props: [%s]", vmd.id(), vmd.provider().attachVirtualMachine(vmd).getAgentProperties()));
					} else {
						int index = displayName.indexOf(' ');
						if(index!=-1) displayName = displayName.substring(0, index);
					}
				} else {
					if(displayName==null || displayName.trim().isEmpty()) {
						displayName = "???";
					}
				}				
				b.append("\n\t").append(vmd.id()).append(" : ").append(displayName);
				if(jmxUrl) {
					b.append("\n\t\tJMXServiceURL:").append(vmd.provider().attachVirtualMachine(vmd).getJMXServiceURL().toString());
				}
			}
		} catch (Exception ex) {
			return "ListJVMs failed:" + ex;
		}
		return b.toString();
	}


}
