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
package com.heliosapm.endpoint.publisher;

/**
 * <p>Title: PublisherAgent</p>
 * <p>Description: </p> 
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.endpoint.publisher.PublisherAgent</code></p>
 */

public class PublisherAgent {

	/**
	 * Creates a new PublisherAgent
	 */
	public PublisherAgent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Low maintenance formatted out logger
	 * @param fmt The message format
	 * @param args The message tokens
	 */
	public static void log(final Object fmt, final Object...args) {
		System.out.println(String.format(fmt.toString(), args));
	}

	/**
	 * Low maintenance formatted err logger
	 * @param fmt The message format
	 * @param args The message tokens
	 */
	public static void log(final Object fmt, final Throwable t, final Object...args) {
		System.err.println(String.format(fmt.toString(), args));
		t.printStackTrace(System.err);
	}

}
