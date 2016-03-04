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
package com.heliosapm.jmxmp.async.util;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import javax.management.MBeanServerConnection;

import com.heliosapm.utils.lang.StringHelper;

/**
 * <p>Title: GenJMXSuspendables</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.util.GenJMXSuspendables</code></p>
 */

public class GenJMXSuspendables {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Class<?>[] GENCLASSES = {
				MBeanServerConnection.class,
				CountDownLatch.class
		};
		
		for(Class<?> clazz: GENCLASSES) {
			try {
				for(Method m: clazz.getDeclaredMethods()) {
					log(clazz.getName() + "." + m.getName() + StringHelper.getMethodDescriptor(m));
				}
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}
		}

	}
	
	public static void log(Object msg) {
		System.out.println(msg);
	}

}
