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
package com.heliosapm.jmxmp.async;

import java.io.ByteArrayInputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.heliosapm.utils.tuples.NVP;

import co.paralleluniverse.fibers.Fiber;

/**
 * <p>Title: BulkResponse</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.bulk.BulkResponse</code></p>
 */

public class BulkResponse implements Externalizable {
	/** The number of serialized responses */
	protected int responseCount;
	/** The serialized responses */
	protected byte[] payload;
	/** Indicates if the response payload is gzipped */
	protected boolean gzipped;
	
	/**
	 * Creates a new BulkResponse
	 * @param responseCount The number of serialized responses
	 * @param payload The serialized responses
	 * @param gzipped Indicates if the response payload is gzipped
	 */	
	public BulkResponse(final int responseCount, final byte[] payload, final boolean gzipped) {
		this.responseCount = responseCount;
		this.payload = payload;
		this.gzipped = gzipped;
	}
	
	/**
	 * Creates a new BulkResponse.
	 * For extern only.
	 */
	public BulkResponse() {
		
	}
	
	public static void log(final Object fmt, final Object...args) {
		final Fiber f = Fiber.currentFiber();
		if(f!=null) {
			System.out.println("f:BulkResponse[" + f.getName() + "]" + String.format(fmt.toString(), args));
		} else {
			System.out.println("t:BulkResponse[" + Thread.currentThread().getName() + "]" + String.format(fmt.toString(), args));
		}
	}

	
	/**
	 * Returns a list of the unmarshalled responses
	 * @return a list of the unmarshalled responses
	 */
	public List<NVP<MBeanOp, Object>> getResponses() {
//		CompactObjectInputStream ois = null;
		ObjectInputStream ois = null;
		GZIPInputStream gis = null;
		ByteArrayInputStream bais = null;		
		List<NVP<MBeanOp, Object>> responses = null;
		try {
			bais = new ByteArrayInputStream(payload);
			responses = new ArrayList<NVP<MBeanOp, Object>>(responseCount);
			if(gzipped) {
				gis = new GZIPInputStream(bais);
				ois = new ObjectInputStream(gis);
						//new CompactObjectInputStream(gis, ClassResolvers.softCachingConcurrentResolver(Thread.currentThread().getContextClassLoader()));
			} else {
				ois = new ObjectInputStream(bais);
					//new CompactObjectInputStream(bais, ClassResolvers.softCachingConcurrentResolver(Thread.currentThread().getContextClassLoader()));
			}
			for(int i = 0; i < responseCount; i++) {
				Object result = null;
				MBeanOp mbeanOp = MBeanOp.decode(ois.readByte());
				if(ois.readByte()!=0) {
					result = ois.readObject();					
				}
				log("Read Response [op:%s, result:%s]", mbeanOp, result);
				responses.add(new NVP<MBeanOp, Object>(mbeanOp, result));
			}
			return responses;
		} catch (Exception ex) {
			throw new RuntimeException("Failed to unmarshall responses", ex);
		} finally {
			if(ois!=null) try { ois.close(); } catch (Exception x) {/* No Op */}
			if(gis!=null) try { gis.close(); } catch (Exception x) {/* No Op */}
			if(bais!=null) try { bais.close(); } catch (Exception x) {/* No Op */}
		}
		
	}

	
	/**
	 * {@inheritDoc}
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		responseCount = in.readInt();
		gzipped = in.readBoolean();
		payload = new byte[in.readInt()];
		in.read(payload);
	}

	/**
	 * {@inheritDoc}
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeInt(responseCount);
		out.writeBoolean(gzipped);
		out.writeInt(payload.length);
		out.write(payload);		
	}

}
