/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.heliosapm.jmxmp.domain;

import com.heliosapm.utils.instrumentation.Instrumentation;

/**
 * <p>Title: DomainServiceInstallerAgent</p>
 * <p>Description: JavaAgent to install and remove the Domain service</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.domain.DomainServiceInstallerAgent</code></p>
 */

public class DomainServiceInstallerAgent {

	/**
	 * Agent premain
	 * @param agentArgs Not used
	 * @param inst Not used
	 */
	public static void premain(final String agentArgs, final Instrumentation inst) {
		try {
			Domain.install();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
	
	/**
	 * Agent premain
	 * @param agentArgs Not used
	 */
	public static void premain(final String agentArgs) {
		premain(agentArgs, null);
	}
	
	/**
	 * Agent premain
	 * @param agentArgs Not used
	 * @param inst Not used
	 */
	public static void agentmain(final String agentArgs, final Instrumentation inst) {
		premain(agentArgs, inst);
	}
	
	/**
	 * Agent premain
	 * @param agentArgs Not used
	 */
	public static void agentmain(final String agentArgs) {
		premain(agentArgs, null);
	}

}
