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
package com.heliosapm.jmxmp.bulk;

import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * <p>Title: MBeanServerConnectionOpCallback</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.bulk.MBeanServerConnectionOpCallback</code></p>
 */

public interface MBSCOpCallback {
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
	public void onCreateMBeanFail(long rId, Throwable t);


	/**
	 * Asynch completion handler for {@link MBeanServerConnection#unregisterMBean(javax.management.ObjectName)} 
	 * @param rId The serial number of the request
	 */
	public void onUnregisterMBean(int rId);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#unregisterMBean(javax.management.ObjectName)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onUnregisterMBeanFail(long rId, Throwable t);

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
	public void onGetObjectInstanceFail(long rId, Throwable t);

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
	public void onQueryMBeansFail(long rId, Throwable t);

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
	public void onQueryNamesFail(long rId, Throwable t);

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
	public void onGetMBeanCountFail(long rId, Throwable t);

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
	public void onSetAttributesFail(long rId, Throwable t);

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
	public void onGetDefaultDomainFail(long rId, Throwable t);

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
	public void onGetDomainsFail(long rId, Throwable t);

	/**
	 * Asynch completion handler for {@link MBeanServerConnection#addNotificationListener(javax.management.ObjectName,javax.management.ObjectName,javax.management.NotificationFilter,java.lang.Object)} 
	 * @param rId The serial number of the request
	 */
	public void onAddNotificationListener(int rId);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#addNotificationListener(javax.management.ObjectName,javax.management.ObjectName,javax.management.NotificationFilter,java.lang.Object)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onAddNotificationListenerFail(long rId, Throwable t);


	/**
	 * Asynch completion handler for {@link MBeanServerConnection#removeNotificationListener(javax.management.ObjectName,javax.management.NotificationListener,javax.management.NotificationFilter,java.lang.Object)} 
	 * @param rId The serial number of the request
	 */
	public void onRemoveNotificationListener(int rId);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#removeNotificationListener(javax.management.ObjectName,javax.management.NotificationListener,javax.management.NotificationFilter,java.lang.Object)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onRemoveNotificationListenerFail(long rId, Throwable t);


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
	public void onGetMBeanInfoFail(long rId, Throwable t);

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
	public void onIsInstanceOfFail(long rId, Throwable t);

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
	public void onGetAttributeFail(long rId, Throwable t);

	/**
	 * Asynch completion handler for {@link MBeanServerConnection#setAttribute(javax.management.ObjectName,javax.management.Attribute)} 
	 * @param rId The serial number of the request
	 */
	public void onSetAttribute(int rId);

	/**
	 * Asynch exception handler for {@link MBeanServerConnection#setAttribute(javax.management.ObjectName,javax.management.Attribute)} 
	 * @param rId The serial number of the request
	 * @param t The thrown exception from the remote call
	 */ 
	public void onSetAttributeFail(long rId, Throwable t);

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
	public void onInvokeFail(long rId, Throwable t);

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
	public void onIsRegisteredFail(long rId, Throwable t);

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
	public void onGetAttributesFail(long rId, Throwable t);

}
