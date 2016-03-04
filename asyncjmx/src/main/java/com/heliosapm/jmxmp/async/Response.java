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

/**
 * <p>Title: Response</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.Response</code></p>
 */

public class Response implements Externalizable {
	/** The op type that generated this response */
	protected MBeanOp op = null;
	/** The id of the request */
	protected int reqId = -1;
	/** The response value */
	protected Object value = null;
	
	

	/**
	 * Creates a new Response
	 * @param op The op type that generated this response
	 * @param reqId The id of the request
	 * @param value  The response value
	 */
	public Response(final MBeanOp op, final int reqId, final Object value) {
		this.op = op;
		this.reqId = reqId;
		this.value = value;
	}
	
	/**
	 * Creates a new Response
	 * @param op The op type that generated this response
	 * @param reqId The id of the request
	 */
	public Response(final MBeanOp op, final int reqId) {
		this(op, reqId, null);
	}


	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		op = MBeanOp.decode(in.readByte());
		reqId = in.readInt();
		if(in.readByte()!=0) {
			value = in.readObject();
		}
	}

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeByte(op.byteOrdinal);
		out.writeInt(reqId);
		if(value==null) {
			out.writeByte(0);
		} else {
			out.writeByte(0);
			out.writeObject(value);
		}
	}

	/**
	 * Returns 
	 * @return the op
	 */
	private MBeanOp getOp() {
		return op;
	}

	/**
	 * Returns 
	 * @return the reqId
	 */
	private int getReqId() {
		return reqId;
	}

	/**
	 * Returns 
	 * @return the value
	 */
	private Object getValue() {
		return value;
	}

}
