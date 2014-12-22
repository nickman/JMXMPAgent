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

import java.lang.instrument.Instrumentation;

/**
 * <p>Title: Agent</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.Agent</code></p>
 */

public class Agent {

	
	
	public static void main(final String[] args) {
		// Commands
			// List running JVMs
			//install to other JVM
	}
	
	public static void premain(final String agentArgs, final Instrumentation inst) {
		// install JMXMP server
		// 
	}

	public static void premain(final String agentArgs) {
		
	}
	
	public static void agentmain(final String agentArgs, final Instrumentation inst) {
		
	}

	public static void agentmain(final String agentArgs) {
		
	}
	
	private Agent() {}
	
}
