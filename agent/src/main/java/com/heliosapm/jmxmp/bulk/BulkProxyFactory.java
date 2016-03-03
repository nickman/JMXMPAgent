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
package com.heliosapm.jmxmp.bulk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;

import org.json.JSONObject;

/**
 * <p>Title: BulkProxyFactory</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.bulk.BulkProxyFactory</code></p>
 */

public class BulkProxyFactory {
	/** The queued commands */
	protected final List<JSONObject> commands = new ArrayList<JSONObject>();
	/** The command serial number factory */
	protected final AtomicLong commandSerial = new AtomicLong();
	
	final BulkInvocationBuilder invBuilder;
	
	/**
	 * Creates a new BulkProxyFactory
	 */
	public BulkProxyFactory(final boolean gzip) {
		invBuilder = new BulkInvocationBuilder(gzip, 8192);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#addNotificationListener(javax.management.ObjectName, javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	
	public void addNotificationListener(final ObjectName name, final NotificationListener listener, final NotificationFilter filter, final Object handback, final MBSCOpCallback cb) throws InstanceNotFoundException, IOException {
		invBuilder.op(MBeanOp.ADDNOTIFICATIONLISTENER, name, listener, filter, handback);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#addNotificationListener(javax.management.ObjectName, javax.management.ObjectName, javax.management.NotificationFilter, java.lang.Object)
	 */
	
	public void addNotificationListener(final ObjectName name, final ObjectName listener, final NotificationFilter filter, final Object handback, final MBSCOpCallback cb) throws InstanceNotFoundException, IOException {
		invBuilder.op(MBeanOp.ADDNOTIFICATIONLISTENER1, name, listener, filter, handback);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName)
	 */
	
	public void createMBean(final String className, final ObjectName name, final MBSCOpCallback cb) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
		invBuilder.op(MBeanOp.CREATEMBEAN2, className, name);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, javax.management.ObjectName)
	 */
	
	public void createMBean(final String className, final ObjectName name, final ObjectName loaderName, final MBSCOpCallback cb) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
		invBuilder.op(MBeanOp.CREATEMBEAN3, className, name, loaderName);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, java.lang.Object[], java.lang.String[])
	 */
	
	public void createMBean(final String className, final ObjectName name, final Object[] params, final String[] signature, final MBSCOpCallback cb) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
		invBuilder.op(MBeanOp.CREATEMBEAN1, className, name, params, signature);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, javax.management.ObjectName, java.lang.Object[], java.lang.String[])
	 */
	
	public void createMBean(final String className, final ObjectName name, final ObjectName loaderName, final Object[] params, final String[] signature, final MBSCOpCallback cb) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
		invBuilder.op(MBeanOp.CREATEMBEAN, className, name, loaderName, params, signature);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#getAttribute(javax.management.ObjectName, java.lang.String)
	 */
	
	public void getAttribute(final ObjectName name, final String attribute, final MBSCOpCallback cb) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
		invBuilder.op(MBeanOp.GETATTRIBUTE, name, attribute);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#getAttributes(javax.management.ObjectName, java.lang.String[])
	 */
	
	public void getAttributes(final ObjectName name, final String[] attributes, final MBSCOpCallback cb) throws InstanceNotFoundException, ReflectionException, IOException {
		invBuilder.op(MBeanOp.GETATTRIBUTES, name, attributes);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#getDefaultDomain()
	 */
	
	public void getDefaultDomain(final MBSCOpCallback cb) throws IOException {
		invBuilder.op(MBeanOp.GETDEFAULTDOMAIN);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#getDomains()
	 */
	
	public void getDomains(final MBSCOpCallback cb) throws IOException {
		invBuilder.op(MBeanOp.GETDOMAINS);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#getMBeanCount()
	 */
	
	public void getMBeanCount(final MBSCOpCallback cb) throws IOException {
		invBuilder.op(MBeanOp.GETMBEANCOUNT);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#getMBeanInfo(javax.management.ObjectName)
	 */
	
	public void getMBeanInfo(final ObjectName name, final MBSCOpCallback cb) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
		invBuilder.op(MBeanOp.GETMBEANINFO, name);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#getObjectInstance(javax.management.ObjectName)
	 */
	
	public void getObjectInstance(final ObjectName name, final MBSCOpCallback cb) throws InstanceNotFoundException, IOException {
		invBuilder.op(MBeanOp.GETOBJECTINSTANCE, name);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#invoke(javax.management.ObjectName, java.lang.String, java.lang.Object[], java.lang.String[])
	 */
	
	public void invoke(final ObjectName name, final String operationName, final Object[] params, final String[] signature, final MBSCOpCallback cb) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		invBuilder.op(MBeanOp.INVOKE, name, operationName, params, signature);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#isInstanceOf(javax.management.ObjectName, java.lang.String)
	 */
	
	public void isInstanceOf(final ObjectName name, final String className, final MBSCOpCallback cb) throws InstanceNotFoundException, IOException {
		invBuilder.op(MBeanOp.ISINSTANCEOF, name, className);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#isRegistered(javax.management.ObjectName)
	 */
	
	public void isRegistered(final ObjectName name, final MBSCOpCallback cb) throws IOException {
		invBuilder.op(MBeanOp.ISREGISTERED, name);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#queryMBeans(javax.management.ObjectName, javax.management.QueryExp)
	 */
	
	public void queryMBeans(final ObjectName name, final QueryExp query, final MBSCOpCallback cb) throws IOException {
		invBuilder.op(MBeanOp.QUERYMBEANS, name, query);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#queryNames(javax.management.ObjectName, javax.management.QueryExp)
	 */
	
	public void queryNames(final ObjectName name, final QueryExp query, final MBSCOpCallback cb) throws IOException {
		invBuilder.op(MBeanOp.QUERYNAMES, name, query);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.ObjectName)
	 */
	
	public void removeNotificationListener(final ObjectName name, final ObjectName listener, final MBSCOpCallback cb) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
		invBuilder.op(MBeanOp.REMOVENOTIFICATIONLISTENER3, name, listener);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.NotificationListener)
	 */
	
	public void removeNotificationListener(final ObjectName name, final NotificationListener listener, final MBSCOpCallback cb) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
		invBuilder.op(MBeanOp.REMOVENOTIFICATIONLISTENER1, name, listener);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.ObjectName, javax.management.NotificationFilter, java.lang.Object)
	 */
	
	public void removeNotificationListener(final ObjectName name, final ObjectName listener, final NotificationFilter filter, final Object handback, final MBSCOpCallback cb) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
		invBuilder.op(MBeanOp.REMOVENOTIFICATIONLISTENER2, name, listener, filter, handback);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	
	public void removeNotificationListener(final ObjectName name, final NotificationListener listener, final NotificationFilter filter,	final Object handback, final MBSCOpCallback cb) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
		invBuilder.op(MBeanOp.REMOVENOTIFICATIONLISTENER, name, listener, filter, handback);
		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#setAttribute(javax.management.ObjectName, javax.management.Attribute)
	 */
	
	public void setAttribute(final ObjectName name, final Attribute attribute, final MBSCOpCallback cb) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException {
		invBuilder.op(MBeanOp.SETATTRIBUTE, name, attribute);		
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#setAttributes(javax.management.ObjectName, javax.management.AttributeList)
	 */
	
	public void setAttributes(final ObjectName name, final AttributeList attributes, final MBSCOpCallback cb) throws InstanceNotFoundException, ReflectionException, IOException {
		invBuilder.op(MBeanOp.SETATTRIBUTES, name, attributes);
	}

	/**
	 * 
	 * @see javax.management.MBeanServerConnection#unregisterMBean(javax.management.ObjectName)
	 */
	
	public void unregisterMBean(final ObjectName name, final MBSCOpCallback cb) throws InstanceNotFoundException, MBeanRegistrationException, IOException {
		invBuilder.op(MBeanOp.UNREGISTERMBEAN, name);		
	}
	

}
