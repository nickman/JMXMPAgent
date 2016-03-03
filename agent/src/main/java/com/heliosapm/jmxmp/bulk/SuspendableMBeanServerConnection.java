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

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;

import com.heliosapm.jmxmp.spec.NVP;

/**
 * <p>Title: SuspendableMBeanServerConnection</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.bulk.SuspendableMBeanServerConnection</code></p>
 */

public class SuspendableMBeanServerConnection implements MBeanServerConnection {
	protected BulkInvocationBuilder invBuilder;
	protected List<NVP<MBeanOp, Object[]>> result = null;
	
	/**
	 * Creates a new SuspendableMBeanServerConnection
	 */
	public SuspendableMBeanServerConnection(final boolean gzip) {
		invBuilder = new BulkInvocationBuilder(gzip, 8192);
	}
	
	
	
	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#addNotificationListener(javax.management.ObjectName, javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	
	@Override
	public void addNotificationListener(final ObjectName name, final NotificationListener listener, final NotificationFilter filter, final Object handback) throws InstanceNotFoundException, IOException {
		invBuilder.op(MBeanOp.ADDNOTIFICATIONLISTENER, name, listener, filter, handback);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#addNotificationListener(javax.management.ObjectName, javax.management.ObjectName, javax.management.NotificationFilter, java.lang.Object)
	 */
	
	@Override
	public void addNotificationListener(final ObjectName name, final ObjectName listener, final NotificationFilter filter, final Object handback) throws InstanceNotFoundException, IOException {
		invBuilder.op(MBeanOp.ADDNOTIFICATIONLISTENER1, name, listener, filter, handback);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName)
	 */
	
	@Override
	public ObjectInstance createMBean(final String className, final ObjectName name) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
		invBuilder.op(MBeanOp.CREATEMBEAN2, className, name);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, javax.management.ObjectName)
	 */
	
	@Override
	public ObjectInstance createMBean(final String className, final ObjectName name, final ObjectName loaderName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
		invBuilder.op(MBeanOp.CREATEMBEAN3, className, name, loaderName);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, java.lang.Object[], java.lang.String[])
	 */
	
	@Override
	public ObjectInstance createMBean(final String className, final ObjectName name, final Object[] params, final String[] signature) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
		invBuilder.op(MBeanOp.CREATEMBEAN1, className, name, params, signature);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, javax.management.ObjectName, java.lang.Object[], java.lang.String[])
	 */
	
	@Override
	public ObjectInstance createMBean(final String className, final ObjectName name, final ObjectName loaderName, final Object[] params, final String[] signature) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
		invBuilder.op(MBeanOp.CREATEMBEAN, className, name, loaderName, params, signature);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#getAttribute(javax.management.ObjectName, java.lang.String)
	 */
	
	@Override
	public Object getAttribute(final ObjectName name, final String attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
		invBuilder.op(MBeanOp.GETATTRIBUTE, name, attribute);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#getAttributes(javax.management.ObjectName, java.lang.String[])
	 */
	
	@Override
	public AttributeList getAttributes(final ObjectName name, final String[] attributes) throws InstanceNotFoundException, ReflectionException, IOException {
		invBuilder.op(MBeanOp.GETATTRIBUTES, name, attributes);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#getDefaultDomain()
	 */
	
	@Override
	public String getDefaultDomain() throws IOException {
		invBuilder.op(MBeanOp.GETDEFAULTDOMAIN);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#getDomains()
	 */
	
	@Override
	public String[] getDomains() throws IOException {
		invBuilder.op(MBeanOp.GETDOMAINS);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#getMBeanCount()
	 */
	
	@Override
	public Integer getMBeanCount() throws IOException {
		invBuilder.op(MBeanOp.GETMBEANCOUNT);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#getMBeanInfo(javax.management.ObjectName)
	 */
	
	@Override
	public MBeanInfo getMBeanInfo(final ObjectName name) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
		invBuilder.op(MBeanOp.GETMBEANINFO, name);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#getObjectInstance(javax.management.ObjectName)
	 */
	
	@Override
	public ObjectInstance getObjectInstance(final ObjectName name) throws InstanceNotFoundException, IOException {
		invBuilder.op(MBeanOp.GETOBJECTINSTANCE, name);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#invoke(javax.management.ObjectName, java.lang.String, java.lang.Object[], java.lang.String[])
	 */
	
	@Override
	public Object invoke(final ObjectName name, final String operationName, final Object[] params, final String[] signature) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		invBuilder.op(MBeanOp.INVOKE, name, operationName, params, signature);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#isInstanceOf(javax.management.ObjectName, java.lang.String)
	 */
	
	@Override
	public boolean isInstanceOf(final ObjectName name, final String className) throws InstanceNotFoundException, IOException {
		invBuilder.op(MBeanOp.ISINSTANCEOF, name, className);
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#isRegistered(javax.management.ObjectName)
	 */
	
	@Override
	public boolean isRegistered(final ObjectName name) throws IOException {
		invBuilder.op(MBeanOp.ISREGISTERED, name);
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#queryMBeans(javax.management.ObjectName, javax.management.QueryExp)
	 */
	
	@Override
	public Set<ObjectInstance> queryMBeans(final ObjectName name, final QueryExp query) throws IOException {
		invBuilder.op(MBeanOp.QUERYMBEANS, name, query);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#queryNames(javax.management.ObjectName, javax.management.QueryExp)
	 */
	
	@Override
	public Set<ObjectName> queryNames(final ObjectName name, final QueryExp query) throws IOException {
		invBuilder.op(MBeanOp.QUERYNAMES, name, query);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.ObjectName)
	 */
	
	@Override
	public void removeNotificationListener(final ObjectName name, final ObjectName listener) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
		invBuilder.op(MBeanOp.REMOVENOTIFICATIONLISTENER3, name, listener);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.NotificationListener)
	 */
	
	@Override
	public void removeNotificationListener(final ObjectName name, final NotificationListener listener) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
		invBuilder.op(MBeanOp.REMOVENOTIFICATIONLISTENER1, name, listener);
		
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.ObjectName, javax.management.NotificationFilter, java.lang.Object)
	 */
	
	@Override
	public void removeNotificationListener(final ObjectName name, final ObjectName listener, final NotificationFilter filter, final Object handback) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
		invBuilder.op(MBeanOp.REMOVENOTIFICATIONLISTENER2, name, listener, filter, handback);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	
	@Override
	public void removeNotificationListener(final ObjectName name, final NotificationListener listener, final NotificationFilter filter,	final Object handback) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
		invBuilder.op(MBeanOp.REMOVENOTIFICATIONLISTENER, name, listener, filter, handback);
		
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#setAttribute(javax.management.ObjectName, javax.management.Attribute)
	 */
	
	@Override
	public void setAttribute(final ObjectName name, final Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException {
		invBuilder.op(MBeanOp.SETATTRIBUTE, name, attribute);		
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#setAttributes(javax.management.ObjectName, javax.management.AttributeList)
	 */
	
	@Override
	public AttributeList setAttributes(final ObjectName name, final AttributeList attributes) throws InstanceNotFoundException, ReflectionException, IOException {
		invBuilder.op(MBeanOp.SETATTRIBUTES, name, attributes);
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#unregisterMBean(javax.management.ObjectName)
	 */
	
	@Override
	public void unregisterMBean(final ObjectName name) throws InstanceNotFoundException, MBeanRegistrationException, IOException {
		invBuilder.op(MBeanOp.UNREGISTERMBEAN, name);		
	}
	
	

}
