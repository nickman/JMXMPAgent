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
package com.heliosapm.jmxmp.spec;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: SpecField</p>
 * <p>Description: Enumerates the fields in a spec</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.spec.SpecField</code></p>
 */

public enum SpecField {
	/** The listening port */
	PORT("-p", null),
	/** The binding interface */
	IFACE("-i", "127.0.0.1"),
	/** The JMX domain */
	DOMAIN("-d", "DefaultDomain"),
	/** The Bulk JMX MBean registration (true/false) */
	BULK("-b", "false");
	
	
	/** The JMXMP spec delimiter */
	public static final char SPEC_DELIM = ',';
	/** The JMXMP spec field delimiter */
	public static final char SPEC_FIELD_DELIM = ':';
	/** The JMXMP spec field prefix delimiter */
	public static final char SPEC_FIELD_PREFIX_DELIM = '=';

	
	private static final SpecField[] values = values();
	
	private SpecField(final String prefId, final String defaultValue) {
		this.prefId = prefId;
		this.defaultValue = defaultValue;
	}
	
	public static SpecField decode(final int order) {
		if(order < 0 || order > values.length-1) throw new IllegalArgumentException("Invalid order: [" + order + "]");
		return values[order];
	}
	
	public static SpecField decode(final String s) {			
		final String pref;
		if(s.indexOf(SPEC_FIELD_PREFIX_DELIM)!=-1) {
			pref = splitString(s, SPEC_FIELD_PREFIX_DELIM, true)[0].trim().toLowerCase();
		} else {
			pref = s.trim().toLowerCase();
		}
		for(SpecField sf: values()) {
			if(sf.prefId.equals(pref)) return sf;
		}
		throw new RuntimeException("Unrecognized SpecField Prefix: [" + pref + "]");
	}
	
	public final String prefId;
	public final String defaultValue;
	
	/**
	 * Optimized version of {@code String#split} that doesn't use regexps.
	 * This function works in O(5n) where n is the length of the string to
	 * split.
	 * @param s The string to split.
	 * @param c The separator to use to split the string.
	 * @param trimBlanks true to not return any whitespace only array items
	 * @return A non-null, non-empty array.
	 * <p>Copied from <a href="http://opentsdb.net">OpenTSDB</a>.
	 */
	public static String[] splitString(final String s, final char c, final boolean trimBlanks) {
		final char[] chars = s.toCharArray();
		int num_substrings = 1;
		final int last = chars.length-1;
		for(int i = 0; i <= last; i++) {
			char x = chars[i];
			if (x == c) {
				num_substrings++;
			}
		}
		final String[] result = new String[num_substrings];
		final int len = chars.length;
		int start = 0;  // starting index in chars of the current substring.
		int pos = 0;    // current index in chars.
		int i = 0;      // number of the current substring.
		for (; pos < len; pos++) {
			if (chars[pos] == c) {
				result[i++] = new String(chars, start, pos - start);
				start = pos + 1;
			}
		}
		result[i] = new String(chars, start, pos - start);
		if(trimBlanks) {
			int blanks = 0;
			final List<String> strs = new ArrayList<String>(result.length);
			for(int x = 0; x < result.length; x++) {
				if(result[x].trim().isEmpty()) {
					blanks++;
				} else {
					strs.add(result[x]);
				}
			}
			if(blanks==0) return result;
			return strs.toArray(new String[result.length - blanks]);
		}
		return result;
	}
	

}
