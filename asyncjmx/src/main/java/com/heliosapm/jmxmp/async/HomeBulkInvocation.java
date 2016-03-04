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

import java.io.ObjectStreamException;
import java.util.List;

import org.cliffc.high_scale_lib.NonBlockingHashMapLong;

import com.heliosapm.jmxmp.async.server.JMXBulkServiceMBean;
import com.heliosapm.utils.tuples.NVP;

import co.paralleluniverse.fibers.Fiber;

/**
 * <p>Title: HomeBulkInvocation</p>
 * <p>Description: The built bulk invocation, called such because it stays at home, sending the simpler/smaller {@link BulkInvocation} when serialized</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.HomeBulkInvocation</code></p>
 */

public class HomeBulkInvocation extends BulkInvocation {
	/** The registered handlers, one for each op */
	protected transient NonBlockingHashMapLong<AsyncJMXResponseHandler> handlers = null;
	protected final JMXBulkServiceMBean bulkService;
	
	/**
	 * Creates a new HomeBulkInvocation
	 * @param opCount The number of serialized ops
	 * @param payload The serialized ops
	 * @param gzipped Indicates if the op payload is gzipped
	 */
	HomeBulkInvocation(final int opCount, final byte[] payload, final boolean gzipped, final JMXBulkServiceMBean bulkService) {
		super(opCount, payload, gzipped);
		this.bulkService = bulkService;
	}
	
	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HomeBulkInvocation [handlers=" + handlers.size() + ", opCount=" + opCount + ", payload="
				+ payload.length + ", gzip=" + gzipped + "]";
	}

	HomeBulkInvocation setHandlers(final NonBlockingHashMapLong<AsyncJMXResponseHandler> handlers) {
		this.handlers = handlers;
		return this;
	}
	
	public static void log(final Object fmt, final Object...args) {
		final Fiber f = Fiber.currentFiber();
		if(f!=null) {
			System.out.println("f:[" + f.getName() + "]" + String.format(fmt.toString(), args));
		} else {
			System.out.println("t:[" + Thread.currentThread().getName() + "]" + String.format(fmt.toString(), args));
		}
	}
	
	
	public void send() {
		if(opCount < 1) return;
		final BulkResponse br = bulkService.invoke(this);
		int key = 0;
		final List<NVP<MBeanOp, Object>> results = br.getResponses();
		for(final NVP<MBeanOp, Object> r: results) {
			final MBeanOp op = r.getKey();
			final Object result = r.getValue();
			final AsyncJMXResponseHandler handler = handlers.remove(key);
			log("Calling back on response [op:%s, key:%s, handler:%s, value:%s]", op, key, handler, result);
			if(handler!=null) {
				op.handleResponse(result, handler);
			}
			key++;			
		}
	}
	
	
	/**
	 * When this invocation is serialized, it goes out as a simple {@link BulkInvocation}
	 * @return a {@link BulkInvocation} containing the op count and serialized ops of this home
	 * @throws ObjectStreamException
	 */
	Object writeReplace() throws ObjectStreamException {
		return new BulkInvocation(opCount, payload, gzipped);
	}

}
