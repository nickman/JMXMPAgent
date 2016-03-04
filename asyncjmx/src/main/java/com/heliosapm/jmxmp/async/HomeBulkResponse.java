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
package com.heliosapm.jmxmp.async;

import java.io.ObjectStreamException;

import org.cliffc.high_scale_lib.NonBlockingHashMapLong;

import com.heliosapm.jmxmp.async.server.JMXBulkServiceMBean;

import co.paralleluniverse.fibers.Fiber;

/**
 * <p>Title: HomeBulkResponse</p>
 * <p>Description: The built bulk response, called such because it stays at home, sending the simpler/smaller {@link BulkResponse} when serialized</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.HomeBulkResponse</code></p>
 */

public class HomeBulkResponse extends BulkResponse {
	
	/**
	 * Creates a new HomeBulkResponse
	 * @param responseCount The number of serialized ops
	 * @param payload The serialized ops
	 * @param gzipped Indicates if the response payload is gzipped
	 */
	HomeBulkResponse(final int responseCount, final byte[] payload, final boolean gzipped) {
		super(responseCount, payload, gzipped);
	}
	
	
	
	
	
	/**
	 * When this invocation is serialized, it goes out as a simple {@link BulkResponse}
	 * @return a {@link BulkResponse} containing the response count and serialized response of this home
	 * @throws ObjectStreamException
	 */
	Object writeReplace() throws ObjectStreamException {
		return new BulkResponse(responseCount, payload, gzipped);
	}

}
