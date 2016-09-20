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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.heliosapm.jmxmp.domain.DomainMBean;
import com.heliosapm.jmxmp.domain.DomainServiceInstallerAgent;
import com.heliosapm.shorthand.attach.vm.VirtualMachine;
import com.heliosapm.shorthand.attach.vm.VirtualMachineDescriptor;
import com.heliosapm.utils.jar.JarBuilder;
import com.heliosapm.utils.jmx.JMXHelper;

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
	/** This JVM's PID */
	public static final String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	/** The command name for list */
	public static final String LIST_CMD = "-list";
	/** The command name for install */
	public static final String INSTALL_CMD = "-install";
	/** The command name for list domains */
	public static final String LISTDOMAINS_CMD = "-domains";	
	
	/** The default binding interface */
	public static final String DEFAULT_IFACE = "127.0.0.1";
	/** The default JMX domain */
	public static final String DEFAULT_DOMAIN = "DefaultDomain";
	
	static File domainAgentJar = null;
	

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
			} else if(args[0].equalsIgnoreCase(INSTALL_CMD)) {
				if(args.length < 3) {
					System.err.println("Insufficient number of arguments for install command in argument list " + Arrays.toString(args));
					System.exit(-4);					
				}
				install(args[1], args[2]);
			} else if(args[0].equalsIgnoreCase(LISTDOMAINS_CMD)) {
				listDomains(null);
			} else {
				System.err.println("Unrecognized command in argument list " + Arrays.toString(args));
				System.exit(-2);
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	private static void printHelp() {
		System.out.println(getTextFromURL(AgentCmdLine.class.getClassLoader().getResource("help.txt")));
	}
	
	/**
	 * Installs the JMXMPAgent to the JVM identified by the passed {@link VirtualMachineDescriptor#id()}
	 * @param pid The JVM id (the PID)
	 * @param specs The JMXMP spec
	 */
	private static void install(final String pid, final String specs) {
		final VirtualMachine vm;
//		final Map<Integer, Map<SpecField, String>> parsedSpecs;
		try {
			vm = attach(pid);
			final File f = new File(AgentCmdLine.class.getProtectionDomain().getCodeSource().getLocation().getFile());
			vm.loadAgent(f.getAbsolutePath(), specs);
//			parsedSpecs = SpecParser.parseSpecs(specs);
			final Properties p = vm.getSystemProperties();
			for(final String key: p.stringPropertyNames()) {
				if(key.startsWith(JMXMPConnector.URL_PROPERTY)) {
					System.out.println(key + "=" + p.getProperty(key));
				}
			}
		} catch (Exception ex) {
			System.err.println("Failed to attach to JVM [" + pid + "]: " + ex.getMessage());
			ex.printStackTrace(System.err);
			System.exit(-5);
		}
	}
	
	
	/**
	 * Attaches to the JVM with the passed {@link VirtualMachineDescriptor#id()}
	 * @param pid The JVM id (the PID)
	 * @return the VirtualMachine
	 */
	private static VirtualMachine attach(final String pid) throws Exception {
		return VirtualMachine.attach(pid);
	}
	
	/**
	 * Lists the accessible JVMs on this host (not including this one)
	 * @param regex An optional inclusive filtering regex applied to a JVM's {@link VirtualMachineDescriptor#displayName()} 
	 */
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
			String installed = null;
			try {
				vm = vmd.provider().attachVirtualMachine(vmd);
				final Properties ap = vm.getAgentProperties();
				final Properties sp = vm.getSystemProperties();
				installed = getJMXMPUrl(ap, sp);
				if(installed!=null) {
					System.out.println(vmd.id());
					System.out.println(installed);
				}
			} catch (Exception ex) {
				/* No Op for now ? */
			} finally {
				if(vm!=null) try { vm.detach(); } catch (Exception x) {/* No Op */}
			}
		}
	}
	
	private static String getJMXMPUrl(final Properties... props) {
		final StringBuilder b = new StringBuilder();
		final Map<String, String> map = new HashMap<String, String>();
		for(final Properties p: props) {
			for(final String key: p.stringPropertyNames()) {
				if(key.startsWith(JMXMPConnector.URL_PROPERTY)) {
					map.put(key, p.getProperty(key));				
				}
			}
		}
		if(map.isEmpty()) return null;
		for(Map.Entry<String, String> entry: map.entrySet()) {
			if(b.length() > 0) {
				b.append("\n");
			}		
			b.append(entry.getKey() + "=" + entry.getValue());
		}
		return b.toString();
	}
	
	private static void listDomains(final String regex) {
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
				if(domainAgentJar==null) {
					domainAgentJar = createDomainAgentJar();
				}
				vm = vmd.provider().attachVirtualMachine(vmd);
				vm.loadAgent(domainAgentJar.getAbsolutePath());
				final String[] domains = (String[])JMXHelper.getAttribute(vm.getMBeanServerConnection(), DomainMBean.OBJECT_NAME, "DefaultDomains");
				System.out.println(String.format("PID: %s, Name: %s\n\tDomains: %s", vm.id(), vmd.displayName(), Arrays.toString(domains)));
				JMXHelper.invoke(DomainMBean.OBJECT_NAME, vm.getMBeanServerConnection(), "remove");
			} catch (Exception ex) {
				/* No Op for now ? */
			} finally {
				if(vm!=null) try { vm.detach(); } catch (Exception x) {/* No Op */}
			}
			vms.put(vmd.id(), (installed ? "(JMXMP Agent Installed) " : "") + desc);
		}
	}
	
	
	private static File createDomainAgentJar() {
		try {
			final File f = new JarBuilder(File.createTempFile("helios-jmxmp-domainagent-", ".jar"), true)
					.manifestBuilder()
					.agentClass(DomainServiceInstallerAgent.class.getName())
					.createdBy("HeliosJMXMPAgent")
					.specTitle("HeliosJMXMPAgent")
					.done()				
					.res("com.heliosapm.jmxmp.domain").classLoader(com.heliosapm.jmxmp.domain.Domain.class).apply()
					.build();
			f.deleteOnExit();
			return f;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Reads the content of a URL as text.
	 * Copied from helios utils to avoid the dependency
	 * @param url The url to get the text from
	 * @return a string representing the text read from the passed URL
	 */
	public static String getTextFromURL(URL url) {
		StringBuilder b = new StringBuilder();
		InputStreamReader isr = null;
		BufferedReader br = null;
		InputStream is = null;
		URLConnection connection = null;
		try {
			connection = url.openConnection();
			connection.connect();
			is = connection.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String line = null;
			while((line=br.readLine())!=null) {
				b.append(line).append("\n");
			}
			return b.toString();			
		} catch (Exception e) {
			throw new RuntimeException("Failed to read source of [" + url + "]", e);
		} finally {
			if(br!=null) try { br.close(); } catch (Exception e) {/* No Op */}
			if(isr!=null) try { isr.close(); } catch (Exception e) {/* No Op */}
			if(is!=null) try { is.close(); } catch (Exception e) {/* No Op */}
		}
	}


}
