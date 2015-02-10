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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
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
@Parameters(commandDescription = "Lists the currently running JVMs", commandNames="list")
public class ListJVMsCommand implements ICommand {

	/** Option to print the full VM display */
	@Parameter(names="-full", arity=0, description="Include to print the full VM display", required=false)
	protected boolean full = false;
	/** Option to print the JMX/RMI URL */
	@Parameter(names="-jmxconn", arity=0, description="Include to print the JMX/RMI URL", required=false)
	protected boolean jmxUrl = false;
	@Parameter(names="-match", arity=1, description="Specify a regex to filter JVMs by JVM display", required=false)
	protected String match = ".*";
	
	
	/** The options parser */
//	final CmdLineParser parser = new CmdLineParser(this);

	/**
	 * Creates a new ListJVMsCommand
	 */
	public ListJVMsCommand() {

	}
	
	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.ICommand#getDescription()
	 */
	@Override
	public String getDescription() {		
		return "Lists accessible running JVMs";
	}

	
	private void reset() {
		full = false;
		jmxUrl = false;
	}

	@Override
	public String execute(String... args) {
		reset();
//		try {
//			parser.parseArgument(args);
//		} catch (Exception ex) {
//			ex.printStackTrace(System.err);
//			throw new RuntimeException("Failed to parse options in [" + getClass().getName() + "]", ex);
//		}
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
