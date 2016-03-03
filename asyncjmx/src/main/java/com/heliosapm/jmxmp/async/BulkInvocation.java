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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;



/**
 * <p>Title: BulkInvocation</p>
 * <p>Description: Represents a single JMX invocation containing some number (hopefully not zero) of serialized async JMX requests</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.BulkInvocation</code></p>
 */

public class BulkInvocation implements Serializable, Externalizable {
 
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
