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
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * <p>Title: EmptyMBSCOpCallback</p>
 * <p>Description: An empty {@link MBSCOpCallback} for selective extension</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.bulk.EmptyMBSCOpCallback</code></p>
 */

public class EmptyMBSCOpCallback implements MBSCOpCallback {

	/**
	 * Creates a new EmptyMBSCOpCallback
	 */
	public EmptyMBSCOpCallback() {
		/* No Op */
	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onCreateMBean(long, javax.management.ObjectInstance)
	 */
	@Override
	public void onCreateMBean(final long rId, final ObjectInstance ret) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onCreateMBeanFail(long, java.lang.Throwable)
	 */
	@Override
	public void onCreateMBeanFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onUnregisterMBean(int)
	 */
	@Override
	public void onUnregisterMBean(int rId) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onUnregisterMBeanFail(long, java.lang.Throwable)
	 */
	@Override
	public void onUnregisterMBeanFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onGetObjectInstance(long, javax.management.ObjectInstance)
	 */
	@Override
	public void onGetObjectInstance(final long rId, final ObjectInstance ret) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onGetObjectInstanceFail(long, java.lang.Throwable)
	 */
	@Override
	public void onGetObjectInstanceFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onQueryMBeans(long, java.util.Set)
	 */
	@Override
	public void onQueryMBeans(final long rId, final Set<ObjectInstance> ret) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onQueryMBeansFail(long, java.lang.Throwable)
	 */
	@Override
	public void onQueryMBeansFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onQueryNames(long, java.util.Set)
	 */
	@Override
	public void onQueryNames(final long rId, final Set<ObjectName> ret) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onQueryNamesFail(long, java.lang.Throwable)
	 */
	@Override
	public void onQueryNamesFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onGetMBeanCount(long, java.lang.Integer)
	 */
	@Override
	public void onGetMBeanCount(final long rId, final Integer ret) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onGetMBeanCountFail(long, java.lang.Throwable)
	 */
	@Override
	public void onGetMBeanCountFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onSetAttributes(long, javax.management.AttributeList)
	 */
	@Override
	public void onSetAttributes(final long rId, final AttributeList ret) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onSetAttributesFail(long, java.lang.Throwable)
	 */
	@Override
	public void onSetAttributesFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onGetDefaultDomain(long, java.lang.String)
	 */
	@Override
	public void onGetDefaultDomain(final long rId, final String ret) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onGetDefaultDomainFail(long, java.lang.Throwable)
	 */
	@Override
	public void onGetDefaultDomainFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onGetDomains(long, java.lang.String[])
	 */
	@Override
	public void onGetDomains(final long rId, final String[] ret) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onGetDomainsFail(long, java.lang.Throwable)
	 */
	@Override
	public void onGetDomainsFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onAddNotificationListener(int)
	 */
	@Override
	public void onAddNotificationListener(int rId) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onAddNotificationListenerFail(long, java.lang.Throwable)
	 */
	@Override
	public void onAddNotificationListenerFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onRemoveNotificationListener(int)
	 */
	@Override
	public void onRemoveNotificationListener(int rId) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onRemoveNotificationListenerFail(long, java.lang.Throwable)
	 */
	@Override
	public void onRemoveNotificationListenerFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onGetMBeanInfo(long, javax.management.MBeanInfo)
	 */
	@Override
	public void onGetMBeanInfo(final long rId, final MBeanInfo ret) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onGetMBeanInfoFail(long, java.lang.Throwable)
	 */
	@Override
	public void onGetMBeanInfoFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onIsInstanceOf(long, boolean)
	 */
	@Override
	public void onIsInstanceOf(final long rId, final boolean ret) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onIsInstanceOfFail(long, java.lang.Throwable)
	 */
	@Override
	public void onIsInstanceOfFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onGetAttribute(long, java.lang.Object)
	 */
	@Override
	public void onGetAttribute(final long rId, final Object ret) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onGetAttributeFail(long, java.lang.Throwable)
	 */
	@Override
	public void onGetAttributeFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onSetAttribute(int)
	 */
	@Override
	public void onSetAttribute(int rId) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onSetAttributeFail(long, java.lang.Throwable)
	 */
	@Override
	public void onSetAttributeFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onInvoke(long, java.lang.Object)
	 */
	@Override
	public void onInvoke(final long rId, final Object ret) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onInvokeFail(long, java.lang.Throwable)
	 */
	@Override
	public void onInvokeFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onIsRegistered(long, boolean)
	 */
	@Override
	public void onIsRegistered(final long rId, final boolean ret) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onIsRegisteredFail(long, java.lang.Throwable)
	 */
	@Override
	public void onIsRegisteredFail(final long rId, final Throwable t) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onGetAttributes(long, javax.management.AttributeList)
	 */
	@Override
	public void onGetAttributes(final long rId, final AttributeList ret) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.bulk.MBSCOpCallback#onGetAttributesFail(long, java.lang.Throwable)
	 */
	@Override
	public void onGetAttributesFail(final long rId, final Throwable t) {
		/* No Op */

	}

}
