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
package com.heliosapm.jmxmp.bulk;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * <p>Title: BulkTransport</p>
 * <p>Description: The transport object for a bulk invocation</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.bulk.BulkTransport</code></p>
 */

public class BulkTransport implements Externalizable {
	/**  */
	private static final long serialVersionUID = -3317137908750485854L;
	/** The op count in the payload */
	protected int opCount;
	/** The payload */
	protected byte[] payload = null;

	/**
	 * Creates a new BulkTransport. Ctor for externalization only.
	 */
	public BulkTransport() {
		
	}
	
	/**
	 * Creates a new BulkTransport
	 * @param opCount The op count in the payload
	 * @param payload The payload
	 */
	BulkTransport(final int opCount, final byte[] payload) {
		this.opCount = opCount;
		this.payload = payload;
	}
	
	

	/**
	 * {@inheritDoc}
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeInt(opCount);
		out.writeInt(payload.length);
		out.write(payload);
	}

	/**
	 * {@inheritDoc}
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		opCount = in.readInt();		
		payload = new byte[in.readInt()];
		in.read(payload);
	}

}
