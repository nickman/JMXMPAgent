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

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;

import org.cliffc.high_scale_lib.NonBlockingHashMapLong;

import com.heliosapm.jmxmp.async.server.JMXBulkServiceMBean;
import com.heliosapm.utils.jmx.JMXHelper;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.Suspendable;

/**
 * <p>Title: BulkResponseBuilder</p>
 * <p>Description: Builder for bulk responses</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.BulkResponseBuilder</code></p>
 */

public class BulkResponseBuilder {
	//protected final CompactObjectOutputStream oos;
	protected final ObjectOutputStream oos;
	protected final GZIPOutputStream gos;
	protected final ByteArrayOutputStream baos;
	protected int opsWritten = 0;
	
	/**
	 * Creates a new BulkResponse
	 * @param gzip true to enable gzip, false otherwise
	 * @param estimatedSize The estimated size of the final content
	 */
	public BulkResponseBuilder(final boolean gzip, final int estimatedSize) {
		try {
			baos = new ByteArrayOutputStream(estimatedSize);
			gos = gzip ? new GZIPOutputStream(baos) : null;
			oos = new ObjectOutputStream(gzip ? gos : baos);
					//new CompactObjectOutputStream(gzip ? gos : baos);			
		} catch (Exception ex) {
			throw new RuntimeException("Failed to initialize BulkResponse", ex);
		}
	}
	

	
	public static void log(final Object fmt, final Object...args) {
		final Fiber f = Fiber.currentFiber();
		if(f!=null) {
			System.out.println("f:[" + f.getName() + "]" + String.format(fmt.toString(), args));
		} else {
			System.out.println("t:[" + Thread.currentThread().getName() + "]" + String.format(fmt.toString(), args));
		}
	}
	
	
	/**
	 * Adds a response to the builder
	 * @param op The op
	 * @param reqId The request id
	 * @param result The result of the op
	 * @return this builder
	 */
	@Suspendable
	public BulkResponseBuilder op(final MBeanOp op, final int reqId, final Object result) {
		try {			
			oos.writeByte(op.byteOrdinal);
//			oos.writeInt(reqId);
			if(result==null) {
				oos.writeByte(0);
			} else {
				oos.writeByte(1);
				oos.writeObject(result);
			}
			opsWritten++;
		} catch (Exception ex) {
			invalidate();
			throw new RuntimeException("Failed to store response [" + op + "]", ex);
		}
		return this;
	}
	
	/**
	 * Builds the bulk response.
	 * The builder will be invalidated and cannot be reused. 
	 * @return the bulk response.
	 */
	public final HomeBulkResponse build() {
		try {
			oos.flush();
			if(gos!=null) {
				gos.flush();
				gos.finish();
			}
			baos.flush();
			final byte[] payload = baos.toByteArray();
			final HomeBulkResponse hbr = new HomeBulkResponse(opsWritten, payload, gos!=null);			
			return hbr;
		} catch (Exception ex) {			
			throw new RuntimeException("Failed to complete response payload", ex);			
		} finally {
			invalidate();
		}
	}
	

	
	protected void invalidate() {
		if(oos!=null) try { oos.close(); } catch (Exception x) {/* No Op */} 
		if(gos!=null) try { gos.close(); } catch (Exception x) {/* No Op */}
		if(baos!=null) try { baos.close(); } catch (Exception x) {/* No Op */}
	}
	

}
