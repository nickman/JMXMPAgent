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
package com.heliosapm.jmxmp.async.server;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.heliosapm.jmxmp.async.BulkInvocation;
import com.heliosapm.jmxmp.async.BulkResponse;
import com.heliosapm.jmxmp.async.MBeanOp;
import com.heliosapm.utils.tuples.NVP;

/**
 * <p>Title: JMXBulkService</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.server.JMXBulkService</code></p>
 */

public class JMXBulkService implements JMXBulkServiceMBean, MBeanRegistration {
	protected MBeanServer server = null;
	/**
	 * Creates a new JMXBulkService
	 */
	public JMXBulkService() {

	}
	
	public BulkResponse invoke(final BulkInvocation invocation) {
		if(server == null) throw new IllegalStateException("The JMXService is not registered and has a null MBeanServer reference");
		final BulkResponse response = new BulkResponse();
		int key = 0;
		for(final NVP<MBeanOp, Object[]> inv : invocation.getInvocations()) {
			Object returnValue = null;
			try {
				returnValue = inv.getKey().invoke(server, inv.getValue());
			} catch (Throwable t) {
				returnValue = t;
			}
			response.resp(inv.getKey(), key, returnValue);
			key++;
		}
		return response;
	}

	@Override
	public void postDeregister() {
		
	}

	@Override
	public void postRegister(final Boolean registered) {
		
	}

	@Override
	public void preDeregister() throws Exception {
		
	}

	@Override
	public ObjectName preRegister(final MBeanServer server, final ObjectName name) throws Exception {
		this.server = server;
		return name;
	}

}
