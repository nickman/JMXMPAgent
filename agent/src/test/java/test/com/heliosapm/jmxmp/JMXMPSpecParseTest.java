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
package test.com.heliosapm.jmxmp;

import java.util.EnumMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import com.heliosapm.jmxmp.AgentCmdLine;
import com.heliosapm.jmxmp.spec.SpecField;
import com.heliosapm.jmxmp.spec.SpecParser;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * <p>Title: JMXMPSpecParseTest</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>test.com.heliosapm.jmxmp.JMXMPSpecParseTest</code></p>
 */
@RunWith(AllTests.class)
public class JMXMPSpecParseTest extends BaseTest {
	
	public static final String JSON_TEST = "test";
	public static final String JSON_SPEC = "spec";
	public static final String JSON_EXPECTED = "expected";
	public static final String JSON_PORT = "port";
	public static final String JSON_IFACE = "iface";
	public static final String JSON_BULK = "bulk";
	public static final String JSON_DOMAIN = "domain";
	
//  {
//    "port" : 8334,
//    "iface" : "127.0.0.1",
//    "domain" : "DefaultDomain"
//  }
	
	@Test
  public static TestSuite suite()  {
      final TestSuite suite = new TestSuite();
      final JSONArray arr = new JSONArray(AgentCmdLine.getTextFromURL(JMXMPSpecParseTest.class.getClassLoader().getResource("spectests.json")));
      for(int i = 0; i < arr.length(); i++) {
      	final JSONObject testDef = arr.getJSONObject(i);
      	final TestCase test = new TestCase(testDef.getString(JSON_TEST)){
					@Override
					public int countTestCases() {						
						return 1;
					}
					@Override
					public void run(final TestResult result) {
						try {
							result.startTest(this);
							test(testDef);
							result.endTest(this);
							
						} catch (AssertionError ae) {
							result.addFailure(this, new AssertionFailedError(ae.getMessage()));
						} catch (Throwable t) {
							result.addError(this, t);
						}						
					}
      	};       	
      	suite.addTest(test);
      }
      
      return suite;
   }	

	
		/**
		 * Tests a JSON provided positive spec test
		 * @param specTest The JSON test specification
		 * @throws Exception thrown on any error
		 */
		public static void test(final JSONObject specTest) throws Exception {
			log("test name: %s", specTest.getString(JSON_TEST));
			final String spec = specTest.getString(JSON_SPEC);
			final JSONArray expecteds = specTest.getJSONArray(JSON_EXPECTED);
			final Map<Integer, Map<SpecField, String>> parsed = SpecParser.parseSpecs(spec);
			Assert.assertEquals("Number of results",  expecteds.length(), parsed.size());
			for(int i = 0; i < expecteds.length(); i++) {
				final Map<SpecField, String> exp = parseJsonExpecteds(expecteds.getJSONObject(i));
				Assert.assertEquals("Number of expecteds",  SpecField.values().length, exp.size());
				final int port = Integer.parseInt(exp.get(SpecField.PORT));
				final Map<SpecField, String> actuals = parsed.get(port);
				Assert.assertNotNull("The actuals was null", actuals);
				for(final SpecField sf: SpecField.values()) {
					Assert.assertEquals("The spec " + sf.name(),  exp.get(sf), actuals.get(sf));
				}
			}
		}
		
		
		protected static Map<SpecField, String> parseJsonExpecteds(final JSONObject ex) throws Exception {
			final Map<SpecField, String> map = new EnumMap<SpecField, String>(SpecField.class);
			map.put(SpecField.PORT, "" + ex.getInt(JSON_PORT));
			map.put(SpecField.IFACE, ex.optString(JSON_IFACE, SpecField.IFACE.defaultValue));
			map.put(SpecField.DOMAIN, ex.optString(JSON_DOMAIN, SpecField.DOMAIN.defaultValue));
			map.put(SpecField.BULK, ex.optString(JSON_BULK, "false"));
			return map;
		}

}
