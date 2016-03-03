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

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.QueryExp;

/**
 * <p>Title: BulkMBeanServerConnection</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.BulkMBeanServerConnection</code></p>
 */

public class BulkMBeanServerConnection {
	protected BulkInvocationBuilder invBuilder;
	
	/**
	 * Creates a new BulkMBeanServerConnection
	 * @param invBuilder The invocation builder
	 */
	public BulkMBeanServerConnection(final BulkInvocationBuilder invBuilder) {
		this.invBuilder = invBuilder;
	}


	/**
	 * 
	 * @see javax.management.MBeanServerConnection#addNotificationListener(javax.management.ObjectName, javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	public void addNotificationListener(final ObjectName name, final NotificationListener listener, final NotificationFilter filter, final Object handback, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.ADDNOTIFICATIONLISTENER, handler, name, listener, filter, handback);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#addNotificationListener(javax.management.ObjectName, javax.management.ObjectName, javax.management.NotificationFilter, java.lang.Object)
	 */
	public void addNotificationListener(final ObjectName name, final ObjectName listener, final NotificationFilter filter, final Object handback, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.ADDNOTIFICATIONLISTENER1, handler, name, listener, filter, handback);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName)
	 */
	public void createMBean(final String className, final ObjectName name, final AsyncJMXResponseHandler handler)  {
		invBuilder.op(MBeanOp.CREATEMBEAN2, handler, className, name);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, javax.management.ObjectName)
	 */
	public void createMBean(final String className, final ObjectName name, final ObjectName loaderName, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.CREATEMBEAN3, handler, className, name, loaderName);		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, java.lang.Object[], java.lang.String[])
	 */
	public void createMBean(final String className, final ObjectName name, final Object[] params, final String[] signature, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.CREATEMBEAN1, handler, className, name, params, signature);		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, javax.management.ObjectName, java.lang.Object[], java.lang.String[])
	 */
	public void createMBean(final String className, final ObjectName name, final ObjectName loaderName, final Object[] params, final String[] signature, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.CREATEMBEAN, handler, className, name, loaderName, params, signature);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#getAttribute(javax.management.ObjectName, java.lang.String)
	 */
	public void getAttribute(final ObjectName name, final String attribute, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.GETATTRIBUTE, handler, name, attribute);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#getAttributes(javax.management.ObjectName, java.lang.String[])
	 */
	public void getAttributes(final ObjectName name, final String[] attributes, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.GETATTRIBUTES, handler, name, attributes);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#getDefaultDomain()
	 */
	public void getDefaultDomain(final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.GETDEFAULTDOMAIN, handler);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#getDomains()
	 */
	
	
	public void getDomains(final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.GETDOMAINS, handler);

	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#getMBeanCount()
	 */
	public void getMBeanCount(final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.GETMBEANCOUNT, handler);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#getMBeanInfo(javax.management.ObjectName)
	 */
	public void getMBeanInfo(final ObjectName name, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.GETMBEANINFO, handler, name);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#getObjectInstance(javax.management.ObjectName)
	 */
	public void getObjectInstance(final ObjectName name, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.GETOBJECTINSTANCE, handler, name);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#invoke(javax.management.ObjectName, java.lang.String, java.lang.Object[], java.lang.String[])
	 */
	public void invoke(final ObjectName name, final String operationName, final Object[] params, final String[] signature, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.INVOKE, handler, name, operationName, params, signature);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#isInstanceOf(javax.management.ObjectName, java.lang.String)
	 */
	public void isInstanceOf(final ObjectName name, final String className, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.ISINSTANCEOF, handler, name, className);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#isRegistered(javax.management.ObjectName)
	 */
	public void isRegistered(final ObjectName name, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.ISREGISTERED, handler, name);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#queryMBeans(javax.management.ObjectName, javax.management.QueryExp)
	 */
	public void queryMBeans(final ObjectName name, final QueryExp query, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.QUERYMBEANS, handler, name, query);		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#queryNames(javax.management.ObjectName, javax.management.QueryExp)
	 */
	public void queryNames(final ObjectName name, final QueryExp query, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.QUERYNAMES, handler, name, query);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.ObjectName)
	 */
	public void removeNotificationListener(final ObjectName name, final ObjectName listener, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.REMOVENOTIFICATIONLISTENER3, handler, name, listener);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.NotificationListener)
	 */
	public void removeNotificationListener(final ObjectName name, final NotificationListener listener, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.REMOVENOTIFICATIONLISTENER1, handler, name, listener);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.ObjectName, javax.management.NotificationFilter, java.lang.Object)
	 */
	public void removeNotificationListener(final ObjectName name, final ObjectName listener, final NotificationFilter filter, final Object handback, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.REMOVENOTIFICATIONLISTENER2, handler, name, listener, filter, handback);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	public void removeNotificationListener(final ObjectName name, final NotificationListener listener, final NotificationFilter filter,	final Object handback, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.REMOVENOTIFICATIONLISTENER, handler, name, listener, filter, handback);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#setAttribute(javax.management.ObjectName, javax.management.Attribute)
	 */
	public void setAttribute(final ObjectName name, final Attribute attribute, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.SETATTRIBUTE, handler, name, attribute);		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#setAttributes(javax.management.ObjectName, javax.management.AttributeList)
	 */
	public void setAttributes(final ObjectName name, final AttributeList attributes, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.SETATTRIBUTES, handler, name, attributes);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#unregisterMBean(javax.management.ObjectName)
	 */
	public void unregisterMBean(final ObjectName name, final AsyncJMXResponseHandler handler) {
		invBuilder.op(MBeanOp.UNREGISTERMBEAN, handler, name);		
	}
	

}
