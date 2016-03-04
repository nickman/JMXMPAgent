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
 * <p>Title: BulkInvocationBuilder</p>
 * <p>Description: Builder for bulk invocations to be passed to remote MBeanServers for execution</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.bulk.BulkInvocationBuilder</code></p>
 */

public class BulkInvocationBuilder {
	//protected final CompactObjectOutputStream oos;
	protected final ObjectOutputStream oos;
	protected final GZIPOutputStream gos;
	protected final ByteArrayOutputStream baos;
	protected int opsWritten = 0;
	
	protected final MBeanServerConnection conn;
	protected final JMXBulkServiceMBean bulkService;
	
	final NonBlockingHashMapLong<AsyncJMXResponseHandler> handlers = new NonBlockingHashMapLong<AsyncJMXResponseHandler>();
	
	public static final ObjectName BULK = JMXHelper.objectName("com.heliosapm.jmx:service=BulkAPI");
	/**
	 * Creates a new BulkInvocation
	 * @param gzip true to enable gzip, false otherwise
	 * @param estimatedSize The estimated size of the final content
	 */
	public BulkInvocationBuilder(final boolean gzip, final int estimatedSize, final MBeanServerConnection conn) {
		try {
			baos = new ByteArrayOutputStream(estimatedSize);
			gos = gzip ? new GZIPOutputStream(baos) : null;
			oos = new ObjectOutputStream(gzip ? gos : baos);
					//new CompactObjectOutputStream(gzip ? gos : baos);			
			this.conn = conn;
			bulkService = MBeanServerInvocationHandler.newProxyInstance(conn, BULK, JMXBulkServiceMBean.class, false);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to initialize BulkInvocation", ex);
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
	 * Adds an op to the builder
	 * @param op The op
	 * @param handler the async response handler
	 * @param args The arguments
	 * @return this builder
	 */
	@Suspendable
	public synchronized BulkInvocationBuilder op(final MBeanOp op, final AsyncJMXResponseHandler handler, final Object...args) {
		try {			
			log("Storing op [" + op + "]");
			oos.writeByte(op.byteOrdinal);
			final int argCount = args==null ? 0 : args.length;
			oos.writeInt(argCount);
			if(argCount > 0) {
				for(int i = 0; i < argCount; i++) {
					if(args[i]==null) {
						oos.writeByte(0);
					} else {						
						oos.writeByte(1);
						oos.writeObject(args[i]);
					}
				}
			}		
//			MBeanOp mbeanOp = MBeanOp.decode(ois.readByte());
//			final int argCount = ois.readInt();
//			final Object[] args = new Object[argCount];
//			for(int x = 0; x < argCount; x++) {
//				if(ois.readByte()==0) continue;
//				args[x] = ois.readObject();
//			}
			
			if(handler!=null) {
				handlers.put(opsWritten, handler);							
			}
			opsWritten++;
			log("Stored op [" + op + "]");
		} catch (Exception ex) {
			invalidate();
			throw new RuntimeException("Failed to store op [" + op + "]", ex);
		}
		return this;
	}
	
	/**
	 * Builds the bulk transport.
	 * The builder will be invalidated and cannot be reused. // FIXME ?
	 * @return the bulk transport.
	 */
	public final HomeBulkInvocation build() {
		try {
			oos.flush();
			if(gos!=null) {
				gos.flush();
				gos.finish();
			}
			baos.flush();
			final byte[] payload = baos.toByteArray();
			final HomeBulkInvocation hbi = new HomeBulkInvocation(opsWritten, payload, gos!=null, bulkService);
			hbi.setHandlers(handlers);
			return hbi;
		} catch (Exception ex) {			
			throw new RuntimeException("Failed to complete payload", ex);			
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
