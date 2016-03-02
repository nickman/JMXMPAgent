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

import java.lang.reflect.Method;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.QueryExp;

/**
 * <p>Title: MBeanOp</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.bulk.MBeanOp</code></p>
 */

public enum MBeanOp implements OpInvoker {
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#isInstanceOf(ObjectName, String)} */
	ISINSTANCEOF(ClassMeta.getMethod("isInstanceOf", MBeanServerConnection.class, ObjectName.class, String.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#invoke(ObjectName, String, Object[], String[])} */
	INVOKE(ClassMeta.getMethod("invoke", MBeanServerConnection.class, ObjectName.class, String.class, Object[].class, String[].class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#isRegistered(ObjectName)} */
	ISREGISTERED(ClassMeta.getMethod("isRegistered", MBeanServerConnection.class, ObjectName.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#getAttributes(ObjectName, String[])} */
	GETATTRIBUTES(ClassMeta.getMethod("getAttributes", MBeanServerConnection.class, ObjectName.class, String[].class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#getAttribute(ObjectName, String)} */
	GETATTRIBUTE(ClassMeta.getMethod("getAttribute", MBeanServerConnection.class, ObjectName.class, String.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#setAttribute(ObjectName, Attribute)} */
	SETATTRIBUTE(ClassMeta.getMethod("setAttribute", MBeanServerConnection.class, ObjectName.class, Attribute.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#setAttributes(ObjectName, AttributeList)} */
	SETATTRIBUTES(ClassMeta.getMethod("setAttributes", MBeanServerConnection.class, ObjectName.class, AttributeList.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#addNotificationListener(ObjectName, NotificationListener, NotificationFilter, Object)} */
	ADDNOTIFICATIONLISTENER(ClassMeta.getMethod("addNotificationListener", MBeanServerConnection.class, ObjectName.class, NotificationListener.class, NotificationFilter.class, Object.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#addNotificationListener(ObjectName, ObjectName, NotificationFilter, Object)} */
	ADDNOTIFICATIONLISTENER1(ClassMeta.getMethod("addNotificationListener", MBeanServerConnection.class, ObjectName.class, ObjectName.class, NotificationFilter.class, Object.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#createMBean(String, ObjectName, ObjectName, Object[], String[])} */
	CREATEMBEAN(ClassMeta.getMethod("createMBean", MBeanServerConnection.class, String.class, ObjectName.class, ObjectName.class, Object[].class, String[].class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#createMBean(String, ObjectName, Object[], String[])} */
	CREATEMBEAN1(ClassMeta.getMethod("createMBean", MBeanServerConnection.class, String.class, ObjectName.class, Object[].class, String[].class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#createMBean(String, ObjectName)} */
	CREATEMBEAN2(ClassMeta.getMethod("createMBean", MBeanServerConnection.class, String.class, ObjectName.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#createMBean(String, ObjectName, ObjectName)} */
	CREATEMBEAN3(ClassMeta.getMethod("createMBean", MBeanServerConnection.class, String.class, ObjectName.class, ObjectName.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#getDefaultDomain()} */
	GETDEFAULTDOMAIN(ClassMeta.getMethod("getDefaultDomain", MBeanServerConnection.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#getDomains()} */
	GETDOMAINS(ClassMeta.getMethod("getDomains", MBeanServerConnection.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#getMBeanCount()} */
	GETMBEANCOUNT(ClassMeta.getMethod("getMBeanCount", MBeanServerConnection.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#getMBeanInfo(ObjectName)} */
	GETMBEANINFO(ClassMeta.getMethod("getMBeanInfo", MBeanServerConnection.class, ObjectName.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#getObjectInstance(ObjectName)} */
	GETOBJECTINSTANCE(ClassMeta.getMethod("getObjectInstance", MBeanServerConnection.class, ObjectName.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#queryMBeans(ObjectName, QueryExp)} */
	QUERYMBEANS(ClassMeta.getMethod("queryMBeans", MBeanServerConnection.class, ObjectName.class, QueryExp.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#queryNames(ObjectName, QueryExp)} */
	QUERYNAMES(ClassMeta.getMethod("queryNames", MBeanServerConnection.class, ObjectName.class, QueryExp.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#removeNotificationListener(ObjectName, NotificationListener, NotificationFilter, Object)} */
	REMOVENOTIFICATIONLISTENER(ClassMeta.getMethod("removeNotificationListener", MBeanServerConnection.class, ObjectName.class, NotificationListener.class, NotificationFilter.class, Object.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#removeNotificationListener(ObjectName, NotificationListener)} */
	REMOVENOTIFICATIONLISTENER1(ClassMeta.getMethod("removeNotificationListener", MBeanServerConnection.class, ObjectName.class, NotificationListener.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#removeNotificationListener(ObjectName, ObjectName, NotificationFilter, Object)} */
	REMOVENOTIFICATIONLISTENER2(ClassMeta.getMethod("removeNotificationListener", MBeanServerConnection.class, ObjectName.class, ObjectName.class, NotificationFilter.class, Object.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#removeNotificationListener(ObjectName, ObjectName)} */
	REMOVENOTIFICATIONLISTENER3(ClassMeta.getMethod("removeNotificationListener", MBeanServerConnection.class, ObjectName.class, ObjectName.class)),
	/** MBeanServerConnection op for {@link javax.management.MBeanServerConnection#unregisterMBean(ObjectName)} */
	UNREGISTERMBEAN(ClassMeta.getMethod("unregisterMBean", MBeanServerConnection.class, ObjectName.class));
	
	
	private MBeanOp(final Method method) {
		this.method = method;
		byteOrdinal = (byte)ordinal();
	}
	
	/** The MBeanServerConnection method represented by this member */
	public final Method method;
	
	/** The ordinal as a byte */
	public final byte byteOrdinal;
	
	private static final MBeanOp[] values = values();
	private static final int MAX_INDEX = values.length-1;

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.OpInvoker#invoke(javax.management.MBeanServerConnection, java.lang.Object[])
	 */
	@Override
	public Object invoke(final MBeanServerConnection server, Object... args) {
		try {
			return method.invoke(server, args);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to invoke op [" + name() + "]", ex);
		}
	}
	
	/**
	 * Returns the MBeanOp for the passed ordinal
	 * @param ordinal The MBeanOp ordinal
	 * @return the MBeanOp
	 */
	public static MBeanOp decode(final int ordinal) {
		if(ordinal < 0 || ordinal > MAX_INDEX) throw new IllegalArgumentException("Invalid ordinal [" + ordinal + "]");
		return values[ordinal];
	}
	
	/**
	 * Returns the MBeanOp for the passed ordinal
	 * @param ordinal The MBeanOp ordinal
	 * @return the MBeanOp
	 */
	public static MBeanOp decode(final byte ordinal) {
		if(ordinal < 0 || ordinal > MAX_INDEX) throw new IllegalArgumentException("Invalid ordinal [" + ordinal + "]");
		return values[ordinal];
	}
	
	/**
	 * Returns the ordinal as a byte
	 * @return the ordinal as a byte
	 */
	public byte byteOrdinal() {
		return byteOrdinal;
	}
	
	
}
