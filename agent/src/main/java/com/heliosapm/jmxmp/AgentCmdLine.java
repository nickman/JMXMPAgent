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

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.heliosapm.shorthand.attach.vm.VirtualMachine;
import com.heliosapm.shorthand.attach.vm.VirtualMachineDescriptor;
import com.heliosapm.utils.lang.StringHelper;
import com.heliosapm.utils.url.URLHelper;

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
	/** This JVM's PID */
	public static final String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	/** The command name for list */
	public static final String LIST_CMD = "-list";
	/** The command name for install */
	public static final String INSTALL_CMD = "-install";
	
	/** The agent property set when the JMXMP agent has been installed */
	public static final String JMXMP_INSTALLED_PROP = "com.heliosapm.jmxmp.installed";
	

	/**
	 * Command line entry point
	 * @param args See usage
	 */
	public static void main(String[] args) {
		if(args==null || args.length==0) {
			printHelp();
			return;
		}
		try {
			if(args[0].equalsIgnoreCase(LIST_CMD)) {
				list(args.length > 1 ? args[1] : null);
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	private static void printHelp() {
		System.out.println(URLHelper.getTextFromURL(AgentCmdLine.class.getClassLoader().getResource("help.txt")));
	}
	
	private static void list(final String regex) {
		Pattern p = null;
		final Map<String, String> vms = new HashMap<String, String>();
		try {
			if(regex!=null && !regex.trim().isEmpty()) {
				p = Pattern.compile(regex.trim(), Pattern.CASE_INSENSITIVE);
			}
		} catch (Exception ex) {
			System.err.println("Invalid Regex [" + regex + "]. Exiting...");
			System.exit(-3);
		} 
		for(final VirtualMachineDescriptor vmd: VirtualMachine.list()) {
			if(PID.equals(vmd.id())) continue;
			final String desc = vmd.displayName();
			if(p!=null) {
				if(!p.matcher(desc).matches()) continue;
			}
			VirtualMachine vm = null;
			boolean installed = false;
			try {
				vm = vmd.provider().attachVirtualMachine(vmd);
				final Properties ap = vm.getAgentProperties();
				final Properties sp = vm.getSystemProperties();
				installed = (ap.containsKey(JMXMP_INSTALLED_PROP) || sp.containsKey(JMXMP_INSTALLED_PROP));
			} catch (Exception ex) {
				/* No Op for now ? */
			} finally {
				if(vm!=null) try { vm.detach(); } catch (Exception x) {/* No Op */}
			}
			vms.put(vmd.id(), (installed ? "(JMXMP Agent Installed) " : "") + desc);
		}
		System.out.println(StringHelper.printBeanNames(vms));
	}

}
