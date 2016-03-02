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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.heliosapm.jmxmp.spec.NVP;

/**
 * <p>Title: BulkInvocation</p>
 * <p>Description: Represents a bulk invocation to be passed to a remote MBeanServer</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.bulk.BulkInvocation</code></p>
 */

public class BulkInvocation {
	protected final ObjectOutputStream oos;
	protected final GZIPOutputStream gos;
	protected final ByteArrayOutputStream baos;
	protected int opsWritten = 0;
	
	public static final int GZIP_MAGIC_1 = 31;
	public static final int GZIP_MAGIC_2 = 139;
	
	/**
	 * Creates a new BulkInvocation
	 * @param gzip true to enable gzip, false otherwise
	 * @param estimatedSize The estimated size of the final content
	 */
	public BulkInvocation(final boolean gzip, final int estimatedSize) {
		try {
			baos = new ByteArrayOutputStream(estimatedSize);
			gos = gzip ? new GZIPOutputStream(baos) : null;
			oos = new ObjectOutputStream(gzip ? gos : baos);			
		} catch (Exception ex) {
			throw new RuntimeException("Failed to initialize BulkInvocation", ex);
		}
	}
	
	public static void main(String[] args) {
		BulkInvocation bi = new BulkInvocation(true, 1024)
			.op(MBeanOp.QUERYNAMES, "Hello", "World")
			.op(MBeanOp.CREATEMBEAN, "Hello", "Venus", 1, 6)
			.op(MBeanOp.CREATEMBEAN, "Hello", null, "Venus", 1, 6);
		final Random r = new Random(System.currentTimeMillis());
		for(int i = 0; i < 100; i++) {
			final int argCount = Math.abs(r.nextInt(20));
			final Object[] arx = new Object[argCount];
			for(int x = 0; x < argCount; x++) {
				arx[x] = UUID.randomUUID().toString();
			}
			bi.op(MBeanOp.decode(Math.abs(r.nextInt(20))), arx);
		}
		byte[] payload = bi.done();
		log("Payload:" + payload.length);
		unmarshall(payload, bi.getOpCount());
		
	}
	
	public static void log(Object msg) {
		System.out.println(msg);
	}
	
	public int getOpCount() {
		return opsWritten;
	}
	
	public BulkInvocation op(final MBeanOp op, final Object...args) {
		try {			
			oos.writeByte(op.byteOrdinal);
			oos.writeObject(args);
			opsWritten++;
		} catch (Exception ex) {
			invalidate();
			throw new RuntimeException("Failed to store op [" + op + "]", ex);
		}
		return this;
	}
	
	public final byte[] done() {
		try {
			oos.flush();
			if(gos!=null) {
				gos.flush();
				gos.finish();
			}
			baos.flush();
			final byte[] payload = baos.toByteArray();
			return payload;
		} catch (Exception ex) {			
			throw new RuntimeException("Failed to complete payload", ex);			
		} finally {
			invalidate();
		}
	}
	
	public static List<NVP<MBeanOp, Object[]>> unmarshall(final byte[] payload, final int ops) {
		ObjectInputStream ois = null;
		GZIPInputStream gis;
		ByteArrayInputStream bais;		
		List<NVP<MBeanOp, Object[]>> nvps = null;
		try {
			bais = new ByteArrayInputStream(payload);
			nvps = new ArrayList<NVP<MBeanOp, Object[]>>(ops);
			final byte[] opCountBytes = new byte[4];
			System.arraycopy(payload, 0, opCountBytes, 0, 4);
			if(isGzipped(opCountBytes)) {
				gis = new GZIPInputStream(bais);
				ois = new ObjectInputStream(gis);
			} else {
				gis = null;
				ois = new ObjectInputStream(bais);
			}
			for(int i = 0; i < ops; i++) {
				MBeanOp mbeanOp = MBeanOp.decode(ois.readByte());
				Object[] args = (Object[])ois.readObject();
				nvps.add(new NVP<MBeanOp, Object[]>(mbeanOp, args));
			}
			return nvps;
		} catch (Exception ex) {
			throw new RuntimeException("Failed to unmarshall payload", ex);
		} finally {
			
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
