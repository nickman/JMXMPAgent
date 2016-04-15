/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.heliosapm.jmxmp.domain;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

/**
 * <p>Title: Domain</p>
 * <p>Description: Service to provide the names of all visible JMX domains</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.domain.Domain</code></p>
 */

public class Domain implements DomainMBean {
	/** The ObjectName of the Domain service, not null when it is registered */
	private static volatile ObjectName objectName = null;
	/** The installation lock */
	private static final Object lock = new Object();
	
	/**
	 * Installs the domain service
	 */
	public static void install() {
		if(objectName==null) {
			synchronized(lock) {
				if(objectName==null) {
					objectName = objectName();
					try {
						ManagementFactory.getPlatformMBeanServer().registerMBean(new Domain(), objectName);
					} catch (Exception ex) {
						ex.printStackTrace(System.err);
						objectName = null;
						throw new RuntimeException("Failed to register Domain Service", ex);
					}
				}
			}
		}
	}

	/**
	 * Removes the domain service
	 */
	private static void _remove() {
		if(objectName!=null) {
			synchronized (lock) {
				if(objectName!=null) {
					try {
						if(ManagementFactory.getPlatformMBeanServer().isRegistered(objectName)) {
							ManagementFactory.getPlatformMBeanServer().unregisterMBean(objectName);
						}
						objectName = null;
					} catch (Exception ex) {
						ex.printStackTrace(System.err);
						throw new RuntimeException("Failed to unregister Domain Service", ex);
					}
				}
			}
		}
	}
	
	/**
	 * Creates a new Domain
	 */
	private Domain() {
		
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.domain.DomainMBean#getDefaultDomains()
	 */
	@Override
	public String[] getDefaultDomains() {
		final List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
		final int size = servers.size();
		final Set<String> domains = new HashSet<String>(size);
		for(MBeanServer server: servers) {
			String dd = server.getDefaultDomain();
			if(dd==null || dd.trim().isEmpty()) dd = "DefaultDomain";
			domains.add(dd);
		}
		return domains.toArray(new String[0]);
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmxmp.domain.DomainMBean#remove()
	 */
	@Override
	public void remove() {
		_remove();			
	}	
	
	/**
	 * Creates the Domain ObjectName
	 * @return the Domain ObjectName
	 */
	private static ObjectName objectName() {
		try {
			return new ObjectName(OBJECT_NAME);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
