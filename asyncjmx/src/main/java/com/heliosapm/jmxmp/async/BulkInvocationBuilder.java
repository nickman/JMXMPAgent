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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.cliffc.high_scale_lib.NonBlockingHashMapLong;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.CompactObjectInputStream;
import org.jboss.netty.handler.codec.serialization.CompactObjectOutputStream;

import com.heliosapm.utils.tuples.NVP;

/**
 * <p>Title: BulkInvocationBuilder</p>
 * <p>Description: Builder for bulk invocations to be passed to remote MBeanServers for execution</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.bulk.BulkInvocationBuilder</code></p>
 */

public class BulkInvocationBuilder {
	protected final CompactObjectOutputStream oos;
	protected final GZIPOutputStream gos;
	protected final ByteArrayOutputStream baos;
	protected int opsWritten = 0;
	
	final NonBlockingHashMapLong<AsyncJMXResponseHandler> handlers = new NonBlockingHashMapLong<AsyncJMXResponseHandler>();
	
	public static final int GZIP_MAGIC_1 = 31;
	public static final int GZIP_MAGIC_2 = 139;
	
	/**
	 * Creates a new BulkInvocation
	 * @param gzip true to enable gzip, false otherwise
	 * @param estimatedSize The estimated size of the final content
	 */
	public BulkInvocationBuilder(final boolean gzip, final int estimatedSize) {
		try {
			baos = new ByteArrayOutputStream(estimatedSize);
			gos = gzip ? new GZIPOutputStream(baos) : null;
			oos = new CompactObjectOutputStream(gzip ? gos : baos);			
		} catch (Exception ex) {
			throw new RuntimeException("Failed to initialize BulkInvocation", ex);
		}
	}
	
	public static void main(String[] args) {
		BulkInvocationBuilder bi = new BulkInvocationBuilder(true, 1024)
			.op(MBeanOp.QUERYNAMES, null, "Hello", "World")
			.op(MBeanOp.CREATEMBEAN, null, "Hello", "Venus", 1, 6)
			.op(MBeanOp.CREATEMBEAN, null, "Hello", null, "Venus", 1, 6);
		final Random r = new Random(System.currentTimeMillis());
		for(int i = 0; i < 100; i++) {
			final int argCount = Math.abs(r.nextInt(20));
			final Object[] arx = new Object[argCount];
			for(int x = 0; x < argCount; x++) {
				arx[x] = new String[]{UUID.randomUUID().toString()};
			}
			bi.op(MBeanOp.decode(Math.abs(r.nextInt(20))), null, arx);
		}
		HomeBulkInvocation bt = bi.build();
		log("Payload:" + bt.payload.length);
//		List<NVP<MBeanOp, Object[]>> results = unmarshall(bt);
//		for(NVP<MBeanOp, Object[]> nvp: results) {
//			log("Op: [" + nvp.getKey() + "], Args: " + Arrays.toString(nvp.getValue()));
//		}
		
	}
	
	public static void log(Object msg) {
		System.out.println(msg);
	}
	
	
	/**
	 * Adds an op to the builder
	 * @param op The op
	 * @param handler the async response handler
	 * @param args The arguments
	 * @return this builder
	 */
	public BulkInvocationBuilder op(final MBeanOp op, final AsyncJMXResponseHandler handler, final Object...args) {
		try {			
			System.out.println("Stored op [" + op + "]");
			oos.writeByte(op.byteOrdinal);
			final int argCount = args==null ? 0 : args.length;
			oos.writeInt(argCount);
			if(argCount > 0) {
				for(int i = 0; i < argCount; i++) {
					if(args[i]==null) oos.writeByte(0);
					else {						
						oos.writeByte(1);
						oos.writeObject(args[0]);
					}
				}
			}		
			if(handler!=null) {
				handlers.put(opsWritten, handler);							
			}
			opsWritten++;
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
			final HomeBulkInvocation hbi = new HomeBulkInvocation(opsWritten, payload, gos!=null);
			hbi.setHandlers(handlers);
			return hbi;
		} catch (Exception ex) {			
			throw new RuntimeException("Failed to complete payload", ex);			
		} finally {
			invalidate();
		}
	}
	
	public static List<NVP<MBeanOp, Object[]>> unmarshall(final BulkTransport b) {
		CompactObjectInputStream ois = null;
		GZIPInputStream gis = null;
		ByteArrayInputStream bais = null;		
		List<NVP<MBeanOp, Object[]>> nvps = null;
		try {
			bais = new ByteArrayInputStream(b.payload);
			nvps = new ArrayList<NVP<MBeanOp, Object[]>>(b.opCount);
			final byte[] opCountBytes = new byte[4];
			System.arraycopy(b.payload, 0, opCountBytes, 0, 4);
			if(isGzipped(opCountBytes)) {
				gis = new GZIPInputStream(bais);
				ois = new CompactObjectInputStream(gis, ClassResolvers.softCachingConcurrentResolver(BulkInvocationBuilder.class.getClassLoader()));
			} else {
				gis = null;
				ois = new CompactObjectInputStream(bais, ClassResolvers.softCachingConcurrentResolver(BulkInvocationBuilder.class.getClassLoader()));
			}
			for(int i = 0; i < b.opCount; i++) {
				MBeanOp mbeanOp = MBeanOp.decode(ois.readByte());
				final int argCount = ois.readInt();
				final Object[] args = new Object[argCount];
				for(int x = 0; x < argCount; x++) {
					if(ois.readByte()==0) continue;
					args[x] = ois.readObject();
				}
				nvps.add(new NVP<MBeanOp, Object[]>(mbeanOp, args));
			}
			return nvps;
		} catch (Exception ex) {
			throw new RuntimeException("Failed to unmarshall payload", ex);
		} finally {
			if(ois!=null) try { ois.close(); } catch (Exception x) {/* No Op */}
			if(gis!=null) try { gis.close(); } catch (Exception x) {/* No Op */}
			if(bais!=null) try { bais.close(); } catch (Exception x) {/* No Op */}
		}
	}
	
	protected void invalidate() {
		if(oos!=null) try { oos.close(); } catch (Exception x) {/* No Op */} 
		if(gos!=null) try { gos.close(); } catch (Exception x) {/* No Op */}
		if(baos!=null) try { baos.close(); } catch (Exception x) {/* No Op */}
	}
	
	public static byte[] asBytes(final int value) {
		final byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).asIntBuffer().put(value);
		return bytes;
	}
	
	public static int asInt(final byte[] bytes) {
		return ByteBuffer.wrap(bytes).asIntBuffer().get();
	}
	
	public static boolean isGzipped(final byte[] sample) {
		ByteArrayInputStream bais = null;
		DataInputStream dais = null;
		try {
			bais = new ByteArrayInputStream(sample);
			dais = new DataInputStream(bais);
			int magic1 = dais.readUnsignedByte();
			int magic2 = dais.readUnsignedByte();
			return (magic1==GZIP_MAGIC_1 && magic2==GZIP_MAGIC_2);
		} catch (Exception ex) {
			throw new RuntimeException("Unexpected exception reading GZip marker", ex);
		} finally {
			if(dais!=null) try { dais.close(); } catch (Exception x) {/* No Op */}
			if(bais!=null) try { bais.close(); } catch (Exception x) {/* No Op */}
		}
	}
	

}
