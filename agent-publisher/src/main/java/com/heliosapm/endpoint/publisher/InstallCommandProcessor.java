/**
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */
package com.heliosapm.endpoint.publisher;

import java.util.Properties;

import com.heliosapm.shorthand.attach.vm.VirtualMachine;

/**
 * <p>Title: InstallCommandProcessor</p>
 * <p>Description: Command processor to install the endpoint publisher and JMXMP Connector Server</p> 
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.endpoint.publisher.InstallCommandProcessor</code></p>
 */

public class InstallCommandProcessor implements AgentCommandProcessor {

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.endpoint.publisher.AgentCommandProcessor#processCommand(com.heliosapm.endpoint.publisher.CommandLine)
	 */
	@Override
	public String processCommand(final CommandLine cmdLine) {
		final StringBuilder b = new StringBuilder("[InstallCommandProcessor]:");
		VirtualMachine vm = null;
		try {
			vm = VirtualMachine.attach("" + cmdLine.getPid());
			final Properties sysProps = vm.getSystemProperties();
			final Properties agentProps = vm.getAgentProperties();
			
		} catch (Exception ex) {
			b.insert(0, "ERROR ");
			b.append("Install failed:").append(ex);
		} finally {
			if(vm!=null) try { vm.detach(); } catch (Exception x) {/* No Op */}
		}
//		/** The parsed out JMXSpecs */
//		protected JMXMPSpec[] specs = {};
//		/** The Zookeeper connection string */
//		protected String zookeep = null;
//		/** The agent command to execute */
//		protected AgentCommand command = null;
//		/** The PID of the JVM process to install into */
//		protected long pid = -1;
//		/** The overridden host name that should be published for the target JVM */
//		protected String host = null;
//		/** The overridden app name that should be published for the target JVM */
//		protected String app = null;
		
		
		return b.toString();
	}

}
