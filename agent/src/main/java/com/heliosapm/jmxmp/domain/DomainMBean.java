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

/**
 * <p>Title: DomainMBean</p>
 * <p>Description: JMX MBean interface for {@link Domain}</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.domain.DomainMBean</code></p>
 */

public interface DomainMBean {
	
	/** The Domain service ObjectName */
	public static final String OBJECT_NAME = "com.heliosapm.jmxmp:service=Domain";
	
	/**
	 * Returns the default domain name of all visible MBeanServers
	 * @return the default domain name of all visible MBeanServers
	 */
	public String[] getDefaultDomains();
	
	/**
	 * Unregisters this MBean
	 */
	public void remove();
}
