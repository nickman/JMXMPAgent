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

import java.util.Properties;

import javax.management.remote.JMXServiceURL;

import com.heliosapm.attachme.VirtualMachine;
import com.heliosapm.attachme.VirtualMachineDescriptor;

/**
 * <p>Title: Command</p>
 * <p>Description: Enumerates the commands and their associated command class</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.Command</code></p>
 */

public enum Command implements ICommand {
	LISTJVMS(new ListJVMsCommand()),
	INSTALL(new InstallCommand());
	
	private Command(final ICommand command) {
		this.command = command;
	}
	
	final ICommand command;
	
	@Override
	public String execute(String... args) {
		return command.execute(args);
	}
	
	
	public static class ListJVMsCommand implements ICommand {
		@Override
		public String execute(String... args) {
			final StringBuilder b = new StringBuilder();
			try {
				for(VirtualMachineDescriptor vmd: VirtualMachine.list()) {
					b.append(vmd.id()).append(" : ").append(vmd.displayName()).append(" : ");
					try {
						VirtualMachine vm = null;
						try {
							vm = vmd.provider().attachVirtualMachine(vmd.id());
							final Properties sysProps = vm.getSystemProperties();
							for(String key: sysProps.stringPropertyNames()) {
								if(key.startsWith("com.heliosapm.jmx")) {
									b.append("\n\t").append(key).append(":").append(sysProps.get(key));
								}
							}
							try {
								JMXServiceURL jurl = vm.getJMXServiceURL();
								b.append("\n\t").append("jmxserviceurl").append(":").append(jurl);
							} catch (Exception x) {
								/* No Op */
							}
						} finally {
							if(vm!=null) try { vm.detach(); } catch (Exception x) {/* No Op */}
						}
					} catch (Exception ex) {
						b.append("Failed to attach");
					}
				}
			} catch (Exception ex) {
				return "ListJVMs failed:" + ex;
			}
			return b.toString();
		}
	}
	
	public static class InstallCommand implements ICommand {
		@Override
		public String execute(String... args) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
}
