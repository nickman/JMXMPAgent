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
package test.com.heliosapm.jmxmp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import com.sun.management.OperatingSystemMXBean;

import sun.management.ManagementFactoryHelper;
/**
 * <p>Title: CMDLineCpuPOC</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>test.com.heliosapm.jmxmp.CMDLineCpuPOC</code></p>
 */

public class CMDLineCpuPOC {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		startCmdListener();
		for(int i = 0; i < 10; i++) {
			
			try { Thread.currentThread().join(3000); } catch (Exception ex) {}
		}
		

	}
	
public static double getCpu(OutputStream processIn, InputStream processOut) {
	PrintStream ps = null;
	BufferedReader br = null;
	InputStreamReader isr = null;

	try {
		ps = new PrintStream(processIn);
		isr = new InputStreamReader(processOut);
		br = new BufferedReader(isr);
		ps.println("cpu");
		ps.flush();			
		return Double.parseDouble(br.readLine());
	} catch (Exception ex) {
		throw new RuntimeException(ex);
	} finally {
		if(ps!=null) try { ps.close(); } catch (Exception x) {}
		if(br!=null) try { br.close(); } catch (Exception x) {}
		if(isr!=null) try { isr.close(); } catch (Exception x) {}
	}
}
	
public static void startCmdListener() {
	try {
		Thread t = new Thread("CmdListener") {
			BufferedReader br = null;
			InputStreamReader isr = null;
			final OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactoryHelper.getOperatingSystemMXBean();
			public void run() {
				try {
					isr = new InputStreamReader(System.in);
					br = new BufferedReader(isr);
				} catch (Exception ex) {
					ex.printStackTrace(System.err);
					return;
				} 
				try {
					String cmd = null;
					while(true) {
						cmd = br.readLine();
						if("cpu".equalsIgnoreCase(cmd)) {	// cpu command, print the process load
							System.out.println(os.getProcessCpuLoad());
						} else if("exit".equalsIgnoreCase(cmd)) {	// exit command, break
							break;
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace(System.err);
					return;
				} 
				
			}
		};
		t.setDaemon(true);
		t.start();
	} catch (Exception ex) {
		ex.printStackTrace(System.err);
	}
}

}
