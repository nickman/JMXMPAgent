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
package com.heliosapm.jmxmp.async;

import java.lang.reflect.Method;

/**
 * <p>Title: ClassMeta</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.bulk.ClassMeta</code></p>
 */

public class ClassMeta {

	/**
	 * Reflects out the named method with the provided signature from the passed class
	 * @param makeAccessible true to force the method to be accessible, false otherwise
	 * @param name The name of the method
	 * @param clazz The class to get the method from
	 * @param parameterTypes The method signature
	 * @return the method
	 */
	public static Method getMethod(final boolean makeAccessible, final String name, final Class<?> clazz, final Class<?>...parameterTypes) {
		Method m = null;
		try {
			m = clazz.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException nsme) {
			try {
				m =  clazz.getMethod(name, parameterTypes);
			} catch (NoSuchMethodException nsme2) {
				throw new RuntimeException("Failed to find method [" + name + "] in class [" + clazz.getName() + "]");
			}
		}
		if(makeAccessible) {
			m.setAccessible(true);
		}
		return m;
	}
	
	/**
	 * Reflects out the named method with the provided signature from the passed class making no accessibility change
	 * @param name The name of the method
	 * @param clazz The class to get the method from
	 * @param parameterTypes The method signature
	 * @return the method
	 */
	public static Method getMethod(final String name, final Class<?> clazz, final Class<?>...parameterTypes) {
		return getMethod(false, name, clazz, parameterTypes);
	}
	
	
	private ClassMeta() {}

}
