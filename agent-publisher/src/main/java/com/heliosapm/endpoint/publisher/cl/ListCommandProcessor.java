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
package com.heliosapm.endpoint.publisher.cl;

import com.heliosapm.endpoint.publisher.agent.AgentCommandProcessor;

/**
 * <p>Title: ListCommandProcessor</p>
 * <p>Description: Command processor to list running JVMs and any installed JMXMP services</p> 
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.endpoint.publisher.ListCommandProcessor</code></p>
 */

public class ListCommandProcessor implements AgentCommandProcessor {

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.endpoint.publisher.agent.AgentCommandProcessor#processCommand(com.heliosapm.endpoint.publisher.cl.CommandLine)
	 */
	@Override
	public String processCommand(final CommandLine cmdLine) {
		final StringBuilder b = new StringBuilder("[ListCommandProcessor]:");
		return b.toString();
	}

}
