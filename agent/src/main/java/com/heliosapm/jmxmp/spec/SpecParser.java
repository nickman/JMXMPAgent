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

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.heliosapm.utils.lang.StringHelper;
import com.heliosapm.utils.tuples.NVP;

/**
 * <p>Title: SpecParser</p>
 * <p>Description: Parses and validates an array of specs</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.spec.SpecParser</code></p>
 */
public class SpecParser {

	public static Map<Integer, Map<SpecField, String>> parseSpecs(final String jmxmpSpecs) throws Exception {
		final Map<Integer, Map<SpecField, String>> pspecs = new LinkedHashMap<Integer, Map<SpecField, String>>();
		final String[] specs = StringHelper.splitString(jmxmpSpecs, SpecField.SPEC_DELIM);
		for(String spec: specs) {
			final String[] specFields = StringHelper.splitString(spec, SpecField.SPEC_FIELD_DELIM);
			if(specFields.length==0) throw new Exception("Invalid JMXMP install spec. Field count was 0");
			final Map<SpecField, String> decodeds = new EnumMap<SpecField, String>(SpecField.class);			
			for(int i = 0; i < specFields.length; i++) {
				final NVP<SpecField, String> nvp = parseSpecField(i, specFields[i]);
				decodeds.put(nvp.getKey(), nvp.getValue());
			}
			if(!decodeds.containsKey(SpecField.PORT)) throw new Exception("Invalid JMXMP install spec. No port defined");
			final int port = Integer.parseInt(decodeds.get(SpecField.PORT));
			if(pspecs.containsKey(port)) throw new Exception("Invalid JMXMP install spec. Duplicate ports defined [" + port + "]");
			for(SpecField sf: SpecField.values()) {
				if(!decodeds.containsKey(sf)) {
					decodeds.put(sf, sf.defaultValue);
				}
			}
			pspecs.put(port, decodeds);
		}
		return pspecs;
	}
	
	private static NVP<SpecField, String> parseSpecField(final int order, final String specField) throws Exception {
		final String[] splitSpecField = StringHelper.splitString(specField, SpecField.SPEC_FIELD_PREFIX_DELIM);
		NVP<SpecField, String> parsed = null;
		SpecField sf = null;			
		String specValue = null;
		if(splitSpecField.length==2) {
			sf = SpecField.decode(splitSpecField[0].trim());
			specValue = splitSpecField[1].trim();
			parsed = new NVP<SpecField, String>(sf, specValue);
		} else {
			parsed = new NVP<SpecField, String>(SpecField.decode(order), splitSpecField[0].trim());
		}		
		if(parsed.getKey()==SpecField.PORT) {
			try {
				Integer.parseInt(parsed.getValue());
			} catch (Exception ex) {
				throw new Exception("Invalid port: [" + parsed.getValue() + "]");
			}
		}
		return parsed;
	}
	
	private SpecParser() {}

}
