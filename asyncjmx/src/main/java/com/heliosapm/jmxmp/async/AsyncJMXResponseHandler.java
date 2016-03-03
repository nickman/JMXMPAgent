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

import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import com.heliosapm.utils.unsafe.UnsafeAdapter;

import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;

/**
 * <p>Title: AsyncJMXResponseHandler</p>
 * <p>Description: Defines a callback handler for async JMX invocation responses.
 * There is a callback for failed invocations and one callback for each return type
 * in the {@link MBeanServerConnection} which are:<ul>
 *     <li><b><code>{@link javax.management.ObjectInstance}</code></b></li>
 *     <li><b><code>boolean</code></b></li>
 *     <li><b><code>{@link javax.management.AttributeList}</code></b></li>
 *     <li><b><code>{@link javax.management.MBeanInfo}</code></b></li>
 *     <li><b><code>{@link java.lang.Integer}</code></b></li>
 *     <li><b><code>{@link java.lang.Object}</code></b></li>
 *     <li><b><code>{@link java.util.Set}&lt;ObjectInstance&gt;</code></b></li>
 *     <li><b><code>{@link java.util.Set}&lt;ObjectName&gt;</code></b></li>
 *     <li><b><code>{@link java.lang.String}</code></b></li>
 *     <li><b><code>{@link java.lang.String}[]</code></b></li>
 *     <li><b><code>void</code></b></li>
 * </ul>
 * </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.AsyncJMXResponseHandler</code></p>
 */

public interface AsyncJMXResponseHandler {
	public void onObjectInstance(final ObjectInstance result);
	public void onBoolean(final boolean result);
	public void onAttributeList(final AttributeList result);
	public void onMBeanInfo(final MBeanInfo result);
	public void onInteger(final Integer result);
	public void onObject(final Object result);
	public void onObjectInstances(final Set<ObjectInstance> result);
	public void onObjectNames(final Set<ObjectName> result);
	public void onString(final String result);
	public void onStrings(final String[] result);
	public void onComplete();
	public void onFail(final Throwable t);
	
	
	/**
	 * <p>Title: MBeanServerConnectionAsync</p>
	 * <p>Description: </p> 
	 * <p>Company: Helios Development Group LLC</p>
	 * @author Whitehead (nwhitehead AT heliosdev DOT org)
	 * <p><code>com.heliosapm.jmxmp.async.MBeanServerConnectionAsync</code></p>
	 */
	static abstract class MBeanServerConnectionAsync extends FiberAsync<Object, Throwable> implements AsyncJMXResponseHandler {
		

		/**  */
		private static final long serialVersionUID = 3907028016808383752L;
		
		@SuppressWarnings("unchecked")
		@Suspendable
		public <T> T get(final Class<?> type) {
			try {
				return (T)run();			
			} catch (Throwable e) {			
				System.out.println("Async Ex:" + e);
				e.printStackTrace();
				if(e.getClass()!=SuspendExecution.class) {
					UnsafeAdapter.throwException(e);
					throw new RuntimeException(); // won't get called
				} else {
					return null; // won't get called 
				}
			}
		}
		
		@Suspendable
		public void inv() {
			try {
				run();			
			} catch (Throwable e) {				
				if(e.getClass()!=SuspendExecution.class) {
					UnsafeAdapter.throwException(e);
					throw new RuntimeException(); // won't get called
				}
			}			
		}

		/**
		 * {@inheritDoc}
		 * @see com.heliosapm.jmxmp.async.AsyncJMXResponseHandler#onObjectInstance(javax.management.ObjectInstance)
		 */
		@Override
		public void onObjectInstance(final ObjectInstance result) {
			asyncCompleted(result);
			
		}

		/**
		 * {@inheritDoc}
		 * @see com.heliosapm.jmxmp.async.AsyncJMXResponseHandler#onBoolean(boolean)
		 */
		@Override
		public void onBoolean(final boolean result) {
			asyncCompleted(result);
			
		}

		/**
		 * {@inheritDoc}
		 * @see com.heliosapm.jmxmp.async.AsyncJMXResponseHandler#onAttributeList(javax.management.AttributeList)
		 */
		@Override
		public void onAttributeList(final AttributeList result) {
			asyncCompleted(result);
			
		}

		/**
		 * {@inheritDoc}
		 * @see com.heliosapm.jmxmp.async.AsyncJMXResponseHandler#onMBeanInfo(javax.management.MBeanInfo)
		 */
		@Override
		public void onMBeanInfo(final MBeanInfo result) {
			asyncCompleted(result);
			
		}

		/**
		 * {@inheritDoc}
		 * @see com.heliosapm.jmxmp.async.AsyncJMXResponseHandler#onInteger(java.lang.Integer)
		 */
		@Override
		public void onInteger(final Integer result) {
			asyncCompleted(result);
			
		}

		/**
		 * {@inheritDoc}
		 * @see com.heliosapm.jmxmp.async.AsyncJMXResponseHandler#onObject(java.lang.Object)
		 */
		@Override
		public void onObject(final Object result) {
			asyncCompleted(result);
			
		}

		/**
		 * {@inheritDoc}
		 * @see com.heliosapm.jmxmp.async.AsyncJMXResponseHandler#onObjectInstances(java.util.Set)
		 */
		@Override
		public void onObjectInstances(final Set<ObjectInstance> result) {
			asyncCompleted(result);
			
		}

		/**
		 * {@inheritDoc}
		 * @see com.heliosapm.jmxmp.async.AsyncJMXResponseHandler#onObjectNames(java.util.Set)
		 */
		@Override
		public void onObjectNames(final Set<ObjectName> result) {
			asyncCompleted(result);
			
		}

		/**
		 * {@inheritDoc}
		 * @see com.heliosapm.jmxmp.async.AsyncJMXResponseHandler#onString(java.lang.String)
		 */
		@Override
		public void onString(final String result) {
			asyncCompleted(result);
			
		}

		/**
		 * {@inheritDoc}
		 * @see com.heliosapm.jmxmp.async.AsyncJMXResponseHandler#onStrings(java.lang.String[])
		 */
		@Override
		public void onStrings(final String[] result) {
			asyncCompleted(result);
			
		}

		/**
		 * {@inheritDoc}
		 * @see com.heliosapm.jmxmp.async.AsyncJMXResponseHandler#onComplete()
		 */
		@Override
		public void onComplete() {
			asyncCompleted(null);
			
		}

		/**
		 * {@inheritDoc}
		 * @see com.heliosapm.jmxmp.async.AsyncJMXResponseHandler#onFail(java.lang.Throwable)
		 */
		@Override
		public void onFail(final Throwable t) {
			asyncFailed(t);
			
		}

		
	}
	
//	 
}
