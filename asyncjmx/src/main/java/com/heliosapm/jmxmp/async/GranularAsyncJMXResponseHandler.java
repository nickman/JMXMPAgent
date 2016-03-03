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

/**
 * <p>Title: GranularAsyncJMXResponseHandler</p>
 * <p>Description: Defines a callback handler for async JMX invocation responses
 * where each generalized operation has a completion and failure callback</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.GranularAsyncJMXResponseHandler</code></p>
 */

public interface GranularAsyncJMXResponseHandler {
	/**
	 * Asynch response handler for {@link MBeanServerConnection#isInstanceOf(javax.management.ObjectName,java.lang.String)} 
	 * @param rId The serial number of the request
	 * @param ret The return value of the remote call
	 */ 
	public void onIsInstanceOf(long rId, boolean ret);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#isInstanceOf(javax.management.ObjectName,java.lang.String)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onIsInstanceOfFail(int rId, Throwable t);

	/**
	 * Asynch response handler for {@link MBeanServerConnection#invoke(javax.management.ObjectName,java.lang.String,java.lang.Object[],java.lang.String[])} 
	 * @param rId The serial number of the request
	 * @param ret The return value of the remote call
	 */ 
	public void onInvoke(long rId, Object ret);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#invoke(javax.management.ObjectName,java.lang.String,java.lang.Object[],java.lang.String[])} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onInvokeFail(int rId, Throwable t);

	/**
	 * Asynch response handler for {@link MBeanServerConnection#isRegistered(javax.management.ObjectName)} 
	 * @param rId The serial number of the request
	 * @param ret The return value of the remote call
	 */ 
	public void onIsRegistered(long rId, boolean ret);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#isRegistered(javax.management.ObjectName)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onIsRegisteredFail(int rId, Throwable t);

	/**
	 * Asynch response handler for {@link MBeanServerConnection#getAttributes(javax.management.ObjectName,java.lang.String[])} 
	 * @param rId The serial number of the request
	 * @param ret The return value of the remote call
	 */ 
	public void onGetAttributes(long rId, AttributeList ret);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#getAttributes(javax.management.ObjectName,java.lang.String[])} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onGetAttributesFail(int rId, Throwable t);

	/**
	 * Asynch response handler for {@link MBeanServerConnection#getAttribute(javax.management.ObjectName,java.lang.String)} 
	 * @param rId The serial number of the request
	 * @param ret The return value of the remote call
	 */ 
	public void onGetAttribute(long rId, Object ret);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#getAttribute(javax.management.ObjectName,java.lang.String)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onGetAttributeFail(int rId, Throwable t);

	/**
	 * Asynch completion handler for {@link MBeanServerConnection#setAttribute(javax.management.ObjectName,javax.management.Attribute)} 
	 * @param rId The serial number of the request
	 */
	public void onSetAttribute(long rId);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#setAttribute(javax.management.ObjectName,javax.management.Attribute)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onSetAttributeFail(int rId, Throwable t);

	/**
	 * Asynch response handler for {@link MBeanServerConnection#setAttributes(javax.management.ObjectName,javax.management.AttributeList)} 
	 * @param rId The serial number of the request
	 * @param ret The return value of the remote call
	 */ 
	public void onSetAttributes(long rId, AttributeList ret);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#setAttributes(javax.management.ObjectName,javax.management.AttributeList)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onSetAttributesFail(int rId, Throwable t);

	/**
	 * Asynch completion handler for {@link MBeanServerConnection#addNotificationListener(javax.management.ObjectName,javax.management.NotificationListener,javax.management.NotificationFilter,java.lang.Object)} 
	 * @param rId The serial number of the request
	 */
	public void onAddNotificationListener(long rId);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#addNotificationListener(javax.management.ObjectName,javax.management.NotificationListener,javax.management.NotificationFilter,java.lang.Object)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onAddNotificationListenerFail(int rId, Throwable t);


	/**
	 * Asynch response handler for {@link MBeanServerConnection#createMBean(java.lang.String,javax.management.ObjectName,javax.management.ObjectName,java.lang.Object[],java.lang.String[])} 
	 * @param rId The serial number of the request
	 * @param ret The return value of the remote call
	 */ 
	public void onCreateMBean(long rId, ObjectInstance ret);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#createMBean(java.lang.String,javax.management.ObjectName,javax.management.ObjectName,java.lang.Object[],java.lang.String[])} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onCreateMBeanFail(int rId, Throwable t);


	/**
	 * Asynch response handler for {@link MBeanServerConnection#getDefaultDomain()} 
	 * @param rId The serial number of the request
	 * @param ret The return value of the remote call
	 */ 
	public void onGetDefaultDomain(long rId, String ret);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#getDefaultDomain()} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onGetDefaultDomainFail(int rId, Throwable t);

	/**
	 * Asynch response handler for {@link MBeanServerConnection#getDomains()} 
	 * @param rId The serial number of the request
	 * @param ret The return value of the remote call
	 */ 
	public void onGetDomains(long rId, String[] ret);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#getDomains()} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onGetDomainsFail(int rId, Throwable t);

	/**
	 * Asynch response handler for {@link MBeanServerConnection#getMBeanCount()} 
	 * @param rId The serial number of the request
	 * @param ret The return value of the remote call
	 */ 
	public void onGetMBeanCount(long rId, Integer ret);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#getMBeanCount()} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onGetMBeanCountFail(int rId, Throwable t);

	/**
	 * Asynch response handler for {@link MBeanServerConnection#getMBeanInfo(javax.management.ObjectName)} 
	 * @param rId The serial number of the request
	 * @param ret The return value of the remote call
	 */ 
	public void onGetMBeanInfo(long rId, MBeanInfo ret);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#getMBeanInfo(javax.management.ObjectName)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onGetMBeanInfoFail(int rId, Throwable t);

	/**
	 * Asynch response handler for {@link MBeanServerConnection#getObjectInstance(javax.management.ObjectName)} 
	 * @param rId The serial number of the request
	 * @param ret The return value of the remote call
	 */ 
	public void onGetObjectInstance(long rId, ObjectInstance ret);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#getObjectInstance(javax.management.ObjectName)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onGetObjectInstanceFail(int rId, Throwable t);

	/**
	 * Asynch response handler for {@link MBeanServerConnection#queryMBeans(javax.management.ObjectName,javax.management.QueryExp)} 
	 * @param rId The serial number of the request
	 * @param ret The return value of the remote call
	 */ 
	public void onQueryMBeans(long rId, Set<ObjectInstance> ret);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#queryMBeans(javax.management.ObjectName,javax.management.QueryExp)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onQueryMBeansFail(int rId, Throwable t);

	/**
	 * Asynch response handler for {@link MBeanServerConnection#queryNames(javax.management.ObjectName,javax.management.QueryExp)} 
	 * @param rId The serial number of the request
	 * @param ret The return value of the remote call
	 */ 
	public void onQueryNames(long rId, Set<ObjectName> ret);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#queryNames(javax.management.ObjectName,javax.management.QueryExp)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onQueryNamesFail(int rId, Throwable t);

	/**
	 * Asynch completion handler for {@link MBeanServerConnection#removeNotificationListener(javax.management.ObjectName,javax.management.NotificationListener,javax.management.NotificationFilter,java.lang.Object)} 
	 * @param rId The serial number of the request
	 */
	public void onRemoveNotificationListener(long rId);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#removeNotificationListener(javax.management.ObjectName,javax.management.NotificationListener,javax.management.NotificationFilter,java.lang.Object)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onRemoveNotificationListenerFail(int rId, Throwable t);

	/**
	 * Asynch completion handler for {@link MBeanServerConnection#unregisterMBean(javax.management.ObjectName)} 
	 * @param rId The serial number of the request
	 */
	public void onUnregisterMBean(long rId);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#unregisterMBean(javax.management.ObjectName)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onUnregisterMBeanFail(int rId, Throwable t);


}
