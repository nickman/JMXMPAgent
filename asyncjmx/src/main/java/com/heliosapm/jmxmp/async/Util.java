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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jboss.netty.handler.codec.serialization.CompactObjectOutputStream;

/**
 * <p>Title: ClassMeta</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.bulk.ClassMeta</code></p>
 */

public class Util {

	public static final int GZIP_MAGIC_1 = 31;
	public static final int GZIP_MAGIC_2 = 139;
	
	
	/**
	 * Deserializes the passed byte array to an array of objects
	 * @param payload The byte array to deserialize
	 * @param type The expected type to decode
	 * @return the array of decoded objects
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] deser(final byte[] payload, final Class<T> type) {		
//		CompactObjectInputStream ois = null;
		ObjectInputStream ois = null;
		GZIPInputStream gis = null;
		ByteArrayInputStream bais = null;		
		try {
			bais = new ByteArrayInputStream(payload);
			if(isGzipped(payload)) {
				gis = new GZIPInputStream(bais);
				ois = new ObjectInputStream(gis);
						//new CompactObjectInputStream(gis, ClassResolvers.softCachingConcurrentResolver(BulkInvocationBuilder.class.getClassLoader()));
			} else {
				gis = null;
				ois = new ObjectInputStream(bais);
						//new CompactObjectInputStream(bais, ClassResolvers.softCachingConcurrentResolver(BulkInvocationBuilder.class.getClassLoader()));
			}
			final int cnt = ois.readInt();
			final T[] arr = (T[])Array.newInstance(type, cnt);
			for(int i = 0; i < cnt; i++) {
				if(ois.readByte()==0) {
					arr[i] = null;
				} else {
					arr[i] = (T)ois.readObject();
				}
			}
			return arr;			
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			if(ois!=null) try { ois.close(); } catch (Exception x) {/* No Op */}
			if(gis!=null) try { gis.close(); } catch (Exception x) {/* No Op */}
			if(bais!=null) try { bais.close(); } catch (Exception x) {/* No Op */}			
		}
	}
	
	/**
	 * Serializes an array of objects to a byte array
	 * @param gzip true to enable gzip
	 * @param estimatedSize The initial size of the byte array output stream
	 * @param objs The objects to serialize
	 * @return the byte array
	 */
	public static byte[] ser(final boolean gzip, final int estimatedSize, final Object... objs) {
//		CompactObjectOutputStream oos = null;
		ObjectOutputStream oos = null;
		GZIPOutputStream gos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream(estimatedSize);
			gos = gzip ? new GZIPOutputStream(baos) : null;
			oos = new ObjectOutputStream(gzip ? gos : baos);
					//new CompactObjectOutputStream(gzip ? gos : baos);
			oos.writeInt(objs.length);
			for(Object o: objs) {
				if(o==null) {
					oos.writeByte(0);
				} else {
					oos.writeByte(1);
					oos.writeObject(o);
				}
			}
			oos.flush();
			if(gzip) gos.flush();
			baos.flush();
			return baos.toByteArray();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			if(oos!=null) try { oos.close(); } catch (Exception x) {/* No Op */} 
			if(gos!=null) try { gos.close(); } catch (Exception x) {/* No Op */}
			if(baos!=null) try { baos.close(); } catch (Exception x) {/* No Op */}
		}
	}
	
	/**
	 * Serializes collection of objects to a byte array
	 * @param gzip true to enable gzip
	 * @param estimatedSize The initial size of the byte array output stream
	 * @param objs The objects to serialize
	 * @return the byte array
	 */
	public static byte[] ser(final boolean gzip, final int estimatedSize, final Collection<?> objs) {
		return ser(gzip, estimatedSize, objs.toArray());
	}
	/**
	 * Reflects out the named method with the provided signature from the passed class
	 * @param makeAccessible true to force the method to be accessible, false otherwise
	 * @param name The name of the method
	 * @param clazz The class to get the method from
	 * @param parameterTypes The method signature
	 * @return the method
	 */
	public static Method getMethod(final boolean makeAccessible, final String name, final Class<?> clazz, final Class<?>...parameterTypes) {
		Method m = null;
		try {
			m = clazz.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException nsme) {
			try {
				m =  clazz.getMethod(name, parameterTypes);
			} catch (NoSuchMethodException nsme2) {
				throw new RuntimeException("Failed to find method [" + name + "] in class [" + clazz.getName() + "]");
			}
		}
		if(makeAccessible) {
			m.setAccessible(true);
		}
		return m;
	}
	
	/**
	 * Reflects out the named method with the provided signature from the passed class making no accessibility change
	 * @param name The name of the method
	 * @param clazz The class to get the method from
	 * @param parameterTypes The method signature
	 * @return the method
	 */
	public static Method getMethod(final String name, final Class<?> clazz, final Class<?>...parameterTypes) {
		return getMethod(false, name, clazz, parameterTypes);
	}
	
	public static boolean isGzipped(final byte[] sample) {
		if(sample==null || sample.length < 4) return false;
		ByteArrayInputStream bais = null;
		DataInputStream dais = null;
		try {
			bais = new ByteArrayInputStream(sample, 0, 4);
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
	
	
	
	private Util() {}

}
