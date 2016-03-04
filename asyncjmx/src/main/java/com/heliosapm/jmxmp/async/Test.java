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
import java.lang.management.ManagementFactory;
import java.security.ProtectionDomain;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberExecutorScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.FiberUtil;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.fibers.instrument.JavaAgent;
import co.paralleluniverse.fibers.instrument.QuasarInstrumentor;
import co.paralleluniverse.strands.SuspendableCallable;
import co.paralleluniverse.strands.SuspendableRunnable;

import com.heliosapm.jmxmp.async.AsyncJMXResponseHandler.MBeanServerConnectionAsync;
import com.heliosapm.jmxmp.async.server.JMXBulkService;
import com.heliosapm.jmxmp.async.server.JMXBulkServiceMBean;
import com.heliosapm.shorthand.attach.vm.agent.LocalAgentInstaller;
import com.heliosapm.utils.concurrency.ExtendedThreadManager;
import com.heliosapm.utils.jmx.JMXHelper;
import com.heliosapm.utils.time.SystemClock;
import com.heliosapm.utils.url.URLHelper;

/**
 * <p>Title: Test</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.Test</code></p>
 */

public class Test {
	public static final int CORES = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
	public static final ObjectName BULK = JMXHelper.objectName("com.heliosapm.jmx:service=BulkAPI");
	static final FiberScheduler scheduler = new FiberExecutorScheduler("main", Executors.newFixedThreadPool(CORES, new ThreadFactory(){
		final AtomicInteger serial = new AtomicInteger();
		@Override
		public Thread newThread(final Runnable r) {
			final Thread t = new Thread(r, "MainFiberExecutor#" + serial.incrementAndGet());
			t.setDaemon(true);
			return t;
		}
	})); 

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log("TestBulk");
		JMXHelper.fireUpJMXMPServer("0.0.0.0", 7774, JMXHelper.getHeliosMBeanServer());
		ExtendedThreadManager.install();
		JavaAgent.premain("", LocalAgentInstaller.getInstrumentation());
		System.setProperty("co.paralleluniverse.fibers.verifyInstrumentation", "true");
		//initInstr();
		try {
			final MBeanServerConnection conn = JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:jmxmp://localhost:7774")).getMBeanServerConnection();
			final JMXBulkServiceMBean bulkService = MBeanServerInvocationHandler.newProxyInstance(conn, BULK, JMXBulkServiceMBean.class, false);
			JMXHelper.registerMBean(BULK, new JMXBulkService());
			final BulkInvocationBuilder bib = new BulkInvocationBuilder(true, 8192, conn);
			final MBeanServerConnection msc = new SuspendableMBeanServerConnection(bib);
//			new SR(msc).start();//.join();
			//msc.queryNames(new ObjectName("*:*"), null);
//			SystemClock.sleep(2000);
//			final HomeBulkInvocation home = bib.build();
//			log("Home: %s", home);
//			BulkResponse br = bulkService.invoke(home);
//			log("Response: %s", br);
			
			fiberGoAhead(msc);
			
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}

	}
	
	@Suspendable
	public static void fiberGoAhead(final MBeanServerConnection msc) throws Exception {
		System.out.println("----> GOAHEAD: InFiber:" + Fiber.isCurrentFiber());
		try {
			final CountDownLatch latch = new CountDownLatch(1);
			if(!Fiber.isCurrentFiber()) {
				FiberUtil.runInFiberRuntime(scheduler, new SuspendableCallable<Void>(){
					@Override
					public Void run() throws SuspendExecution, InterruptedException {
						try { 
							fiberGoAhead(msc); 
							latch.countDown();
						} catch (Exception ex) { ex.printStackTrace(System.out); }
						return null;
					}
				});
				log("Waiting on continue latch....");
				latch.await();
				log("continue latch dropped");
			}
			final Set<ObjectName> a = msc.queryNames(new ObjectName("*:*"), null);
			final Set<ObjectName> b = msc.queryNames(new ObjectName("*:*"), null);
			final Set<ObjectName> c = msc.queryNames(new ObjectName("*:*"), null);
			if(!Fiber.isCurrentFiber()) {
				log("Result A: %s", a);
				log("Result B: %s", b);
				log("Result C: %s", c);
				
			}
		} finally {
			if(Fiber.isCurrentFiber()) {
				log("Resetting MSC. Will trigger bulk request send.....");
				((SuspendableMBeanServerConnection)msc).reset();
			}
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
				msc.queryNames(new ObjectName("*:*"), null);
				msc.queryNames(new ObjectName("*:*"), null);
				((SuspendableMBeanServerConnection)msc).reset();
				log("Submitted");
				if(Fiber.currentFiber()==null) return null;
				final SR me = this;
				Thread t = new Thread("ReplaySR") {
					public void run() {
						try {
							me.run();
						} catch (Exception ex) {
							ex.printStackTrace(System.err);
						}
					}
				};
				t.setDaemon(true);
				t.start();
				
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
		final Fiber f = Fiber.currentFiber();
		if(f!=null) {
			System.out.println("f:[" + f.getName() + "]" + String.format(fmt.toString(), args));
		} else {
			System.out.println("t:[" + Thread.currentThread().getName() + "]" + String.format(fmt.toString(), args));
		}
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
