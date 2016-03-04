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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: BulkResponse</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.bulk.BulkResponse</code></p>
 */

public class BulkResponse implements Externalizable {
	/** The responses to include into this bulk response */
	protected final List<Response> responses = new ArrayList<Response>();
	

	
	/**
	 * Creates a new BulkResponse
	 */
	public BulkResponse() {
		
	}
	
	public void resp(final MBeanOp op, final int reqId, final Object value) {
		responses.add(new Response(op, reqId, value));
	}
	
	public String toString() {
		return "BulkResponse: " + responses.size();
	}
	
	public int getResponseCount() {
		return responses.size();
	}
	
	public List<Response> responses() {
		return responses;
	}

	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		final int size = in.readInt();
		byte[] payload = new byte[size];
		in.read(payload);
		Collections.addAll(responses, Util.deser(payload, Response.class));
	}

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		byte[] payload = Util.ser(true, 8192, responses);
		responses.clear();
		out.write(payload.length);
		out.write(payload);
		payload = null;
	}
	
	
	
	

}
