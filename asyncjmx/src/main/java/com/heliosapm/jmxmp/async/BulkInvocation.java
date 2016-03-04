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
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.CompactObjectInputStream;

import com.heliosapm.utils.tuples.NVP;



/**
 * <p>Title: BulkInvocation</p>
 * <p>Description: Represents a single JMX invocation containing some number (hopefully not zero) of serialized async JMX requests</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.BulkInvocation</code></p>
 */

public class BulkInvocation implements Externalizable {
 
	/** The number of serialized ops */
	protected int opCount;
	/** The serialized ops */
	protected byte[] payload;
	/** Indicates if the op payload is gzipped */
	protected boolean gzipped;
	
	/**
	 * Creates a new HomeBulkInvocation
	 * @param opCount The number of serialized ops
	 * @param payload The serialized ops
	 * @param gzipped Indicates if the op payload is gzipped
	 */	
	public BulkInvocation(final int opCount, final byte[] payload, final boolean gzipped) {
		this.opCount = opCount;
		this.payload = payload;
		this.gzipped = gzipped;
	}
	
	/**
	 * Creates a new BulkInvocation.
	 * For extern only.
	 */
	public BulkInvocation() {
		
	}
	
	/**
	 * Returns a list of the unmarshalled invocations
	 * @return a list of the unmarshalled invocations
	 */
	public List<NVP<MBeanOp, Object[]>> getInvocations() {
		CompactObjectInputStream ois = null;
		GZIPInputStream gis = null;
		ByteArrayInputStream bais = null;		
		List<NVP<MBeanOp, Object[]>> nvps = null;
		try {
			bais = new ByteArrayInputStream(payload);
			nvps = new ArrayList<NVP<MBeanOp, Object[]>>(opCount);
			if(gzipped) {
				gis = new GZIPInputStream(bais);
				ois = new CompactObjectInputStream(gis, ClassResolvers.softCachingConcurrentResolver(BulkInvocationBuilder.class.getClassLoader()));
			} else {
				gis = null;
				ois = new CompactObjectInputStream(bais, ClassResolvers.softCachingConcurrentResolver(BulkInvocationBuilder.class.getClassLoader()));
			}
			for(int i = 0; i < opCount; i++) {
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

	
	/**
	 * {@inheritDoc}
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		opCount = in.readInt();
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
		out.writeInt(opCount);
		out.writeBoolean(gzipped);
		out.writeInt(payload.length);
		out.write(payload);		
	}

}
