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

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;

/**
 * <p>Title: MBeanOp</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.MBeanOp</code></p>
 */

public enum MBeanOp implements OpInvoker {
	ISINSTANCEOF{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.isInstanceOf((ObjectName)args[0], (String)args[1]);
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onBoolean((Boolean)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	INVOKE{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.invoke((ObjectName)args[0], (String)args[1], (Object[])args[2], (String[])args[3]);
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onObject((Object)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	ISREGISTERED{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.isRegistered((ObjectName)args[0]);
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onBoolean((Boolean)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	GETATTRIBUTES{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.getAttributes((ObjectName)args[0], (String[])args[1]);
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onAttributeList((AttributeList)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	GETATTRIBUTE{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.getAttribute((ObjectName)args[0], (String)args[1]);
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onObject((Object)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	SETATTRIBUTE{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            server.setAttribute((ObjectName)args[0], (Attribute)args[1]);
            return null;
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onComplete();
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	SETATTRIBUTES{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.setAttributes((ObjectName)args[0], (AttributeList)args[1]);
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onAttributeList((AttributeList)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	ADDNOTIFICATIONLISTENER{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            server.addNotificationListener((ObjectName)args[0], (NotificationListener)args[1], (NotificationFilter)args[2], (Object)args[3]);
            return null;
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onComplete();
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	ADDNOTIFICATIONLISTENER1{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            server.addNotificationListener((ObjectName)args[0], (ObjectName)args[1], (NotificationFilter)args[2], (Object)args[3]);
            return null;
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onComplete();
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	CREATEMBEAN{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.createMBean((String)args[0], (ObjectName)args[1], (ObjectName)args[2], (Object[])args[3], (String[])args[4]);
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onObjectInstance((ObjectInstance)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	CREATEMBEAN1{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.createMBean((String)args[0], (ObjectName)args[1], (Object[])args[2], (String[])args[3]);
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onObjectInstance((ObjectInstance)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	CREATEMBEAN2{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.createMBean((String)args[0], (ObjectName)args[1]);
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onObjectInstance((ObjectInstance)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	CREATEMBEAN3{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.createMBean((String)args[0], (ObjectName)args[1], (ObjectName)args[2]);
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onObjectInstance((ObjectInstance)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	GETDEFAULTDOMAIN{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.getDefaultDomain();
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onString((String)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	GETDOMAINS{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.getDomains();
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onStrings((String[])result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	GETMBEANCOUNT{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.getMBeanCount();
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onInteger((Integer)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	GETMBEANINFO{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.getMBeanInfo((ObjectName)args[0]);
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onMBeanInfo((MBeanInfo)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	GETOBJECTINSTANCE{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.getObjectInstance((ObjectName)args[0]);
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onObjectInstance((ObjectInstance)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	QUERYMBEANS{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.queryMBeans((ObjectName)args[0], (QueryExp)args[1]);
        }
        @SuppressWarnings("unchecked")
		@Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onObjectInstances((Set<ObjectInstance>)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	QUERYNAMES{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            return server.queryNames((ObjectName)args[0], (QueryExp)args[1]);
        }
        @SuppressWarnings("unchecked")
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
        	handler.onObjectNames((Set<ObjectName>)result);
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	REMOVENOTIFICATIONLISTENER{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            server.removeNotificationListener((ObjectName)args[0], (NotificationListener)args[1], (NotificationFilter)args[2], (Object)args[3]);
            return null;
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onComplete();
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	REMOVENOTIFICATIONLISTENER1{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            server.removeNotificationListener((ObjectName)args[0], (NotificationListener)args[1]);
            return null;
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onComplete();
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	REMOVENOTIFICATIONLISTENER2{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            server.removeNotificationListener((ObjectName)args[0], (ObjectName)args[1], (NotificationFilter)args[2], (Object)args[3]);
            return null;
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onComplete();
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	REMOVENOTIFICATIONLISTENER3{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            server.removeNotificationListener((ObjectName)args[0], (ObjectName)args[1]);
            return null;
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onComplete();
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    },
	UNREGISTERMBEAN{
        @Override
        public Object invoke(final MBeanServerConnection server, final Object...args) throws Exception {
            server.unregisterMBean((ObjectName)args[0]);
            return null;
        }
        @Override
        public void handleResponse(final Object result, final AsyncJMXResponseHandler handler) {
            handler.onComplete();
        }
        public void handleFail(final Throwable t, final AsyncJMXResponseHandler handler) {
            handler.onFail(t);
        }
    };
	
	
	private MBeanOp() {
		byteOrdinal = (byte)ordinal();
	}
	
	/** The ordinal as a byte */
	public final byte byteOrdinal;
	
	private static final MBeanOp[] values = values();
	private static final int MAX_INDEX = values.length-1;

	
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
