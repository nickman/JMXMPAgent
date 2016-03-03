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

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.QueryExp;

import com.heliosapm.jmxmp.async.AsyncJMXResponseHandler.MBeanServerConnectionAsync;
import com.heliosapm.shorthand.attach.vm.agent.LocalAgentInstaller;
import com.heliosapm.utils.lang.StringHelper;
import com.heliosapm.utils.time.SystemClock;
import com.heliosapm.utils.url.URLHelper;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.instrument.JavaAgent;
import co.paralleluniverse.fibers.instrument.QuasarInstrumentor;
import co.paralleluniverse.strands.SuspendableRunnable;

/**
 * <p>Title: Test</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.Test</code></p>
 */

public class Test {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log("TestBulk");
		JavaAgent.premain("", LocalAgentInstaller.getInstrumentation());
		//System.setProperty("co.paralleluniverse.fibers.verifyInstrumentation", "true");
		//initInstr();
		try {
			final Method m = MBeanServerConnection.class.getDeclaredMethod("queryNames", ObjectName.class, QueryExp.class);
			log("Sig: [%s]", StringHelper.getMethodDescriptor(m));
			final BulkInvocationBuilder bib = new BulkInvocationBuilder(true, 8192);
			final MBeanServerConnection msc = new SuspendableMBeanServerConnection(bib);
			//new SR(msc).start();//.join();
			msc.queryMBeans(new ObjectName("*:*"), null);
			SystemClock.sleep(2000);
			final HomeBulkInvocation home = bib.build();
			log("Home: %s", home);
			
			
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}

	}
	
	public static class SR extends Fiber<Void> {
		final MBeanServerConnection msc;

		public SR(final MBeanServerConnection msc) {
			super();
			this.msc = msc;
		}
		
		/**
		 * {@inheritDoc}
		 * @see co.paralleluniverse.fibers.Fiber#run()
		 */
		@Override
		protected Void run() throws SuspendExecution, InterruptedException {
			try {
				log("Running...");
				msc.queryNames(new ObjectName("*:*"), null);
				log("Submitted");
			} catch (Exception ex) {
				log("Query Failed: %s", ex);
			}					

			return super.run();
		}
		
	}
	
	static void initInstr() {
		final Instrumentation instr = LocalAgentInstaller.getInstrumentation();
//		final Transformer tran = new Transformer(new MethodDatabase(TestCont.class.getClassLoader()), false);
		final QTransformer tran = new QTransformer(new QuasarInstrumentor(Test.class.getClassLoader()));
		instr.addTransformer(tran, true);
		try {
			instr.retransformClasses(
					MBeanServerConnection.class,
					MBeanServerConnectionAsync.class,
					SR.class,
//					TaskFiber.class,
					Test.class,
					MBeanServerConnectionAsync.class,
					SuspendableMBeanServerConnection.class);
			log("Classes transformed");
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			throw new RuntimeException(ex);
		} finally {
			instr.removeTransformer(tran);
		}
		
	}

	public static void log(final Object fmt, final Object...args) {
		System.out.println("[" + Thread.currentThread().getName() + "]" + String.format(fmt.toString(), args));
	}
	
	
	public static TaskFiber runAsFiber(final SuspendableRunnable r) {
		return new TaskFiber(r);
	}
	
	public static class TaskFiber extends Fiber<Void> {
		final SuspendableRunnable r;
		
		
		public TaskFiber(SuspendableRunnable r) {
			super();
			this.r = r;
		}


		protected Void run() throws co.paralleluniverse.fibers.SuspendExecution, InterruptedException {
			r.run();			
			return null;
		}						
	}
	
	
	
	  public static class QTransformer implements ClassFileTransformer {
	      private final QuasarInstrumentor instrumentor;

	      public QTransformer(QuasarInstrumentor instrumentor) {
	          this.instrumentor = instrumentor;
	      }

	      @Override
	      public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
	          if (!instrumentor.shouldInstrument(className)) {
	              return null;
	          }
	          try {
	              final byte[] transformed = instrumentor.instrumentClass(className, classfileBuffer);
	              File file = new File("/tmp/" + className + ".class");
	              file.getParentFile().mkdirs();
	              if(!file.exists()) {
	              	file.createNewFile();
	              }
	              
	              URLHelper.writeToURL(URLHelper.toURL(file), transformed, false);
	              log("Wrote class file [" + file + "]");
	              return transformed;
	          } catch (Throwable t) {
	        	  t.printStackTrace(System.err);
	              instrumentor.error("while transforming " + className + ": " + t.getMessage(), t);
	              return null;
	          }
	      }
	  }
	
	
}
