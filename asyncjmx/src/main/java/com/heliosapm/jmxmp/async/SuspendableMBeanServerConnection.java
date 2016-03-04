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

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;

import com.heliosapm.jmxmp.async.AsyncJMXResponseHandler.MBeanServerConnectionAsync;
import com.heliosapm.utils.time.SystemClock;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberExecutorScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.SuspendableCallable;

/**
 * <p>Title: SuspendableMBeanServerConnection</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.bulk.SuspendableMBeanServerConnection</code></p>
 */
@SuppressWarnings("serial")
public class SuspendableMBeanServerConnection implements MBeanServerConnection {
	public static final int CORES = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
	final BulkMBeanServerConnection bmc;
	final BulkInvocationBuilder invBuilder;
	final ExecutorService taskExecutor = Executors.newFixedThreadPool(1, new ThreadFactory(){
		@Override
		public Thread newThread(final Runnable r) {
			final Thread t = new Thread(r, "JMXTaskExecutor");
			t.setDaemon(true);
			return t;
		}
	});  
	final FiberScheduler scheduler = new FiberExecutorScheduler("asyncjmx", Executors.newFixedThreadPool(CORES, new ThreadFactory(){
		@Override
		public Thread newThread(final Runnable r) {
			final Thread t = new Thread(r, "JMXFiberExecutor");
			t.setDaemon(true);
			return t;
		}
	})); 
	
	
	/**
	 * Creates a new BulkMBeanServerConnection
	 * @param invBuilder The invocation builder
	 */
	public SuspendableMBeanServerConnection(final BulkInvocationBuilder invBuilder) {
		this.invBuilder = invBuilder;
		bmc = new BulkMBeanServerConnection(invBuilder);		
	}

	
	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#addNotificationListener(javax.management.ObjectName, javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	@Override @Suspendable
	public void addNotificationListener(final ObjectName name, final NotificationListener listener, final NotificationFilter filter, final Object handback) throws InstanceNotFoundException, IOException {
		new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.addNotificationListener(name, listener, filter, handback, this);
			}
		}.inv();
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#addNotificationListener(javax.management.ObjectName, javax.management.ObjectName, javax.management.NotificationFilter, java.lang.Object)
	 */
	
	@Override @Suspendable
	public void addNotificationListener(final ObjectName name, final ObjectName listener, final NotificationFilter filter, final Object handback) throws InstanceNotFoundException, IOException {
		new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.addNotificationListener(name, listener, filter, handback, this);
			}
		}.inv();
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName)
	 */
	
	@Override @Suspendable
	public ObjectInstance createMBean(final String className, final ObjectName name) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.createMBean(className, name, this);
				
			}			
		}.get(ObjectInstance.class);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, javax.management.ObjectName)
	 */
	
	@Override @Suspendable
	public ObjectInstance createMBean(final String className, final ObjectName name, final ObjectName loaderName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.createMBean(className, name, loaderName, this);
				
			}			
		}.get(ObjectInstance.class);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, java.lang.Object[], java.lang.String[])
	 */
	
	@Override @Suspendable
	public ObjectInstance createMBean(final String className, final ObjectName name, final Object[] params, final String[] signature) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.createMBean(className, name, params, signature, this);
				
			}			
		}.get(ObjectInstance.class);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#createMBean(java.lang.String, javax.management.ObjectName, javax.management.ObjectName, java.lang.Object[], java.lang.String[])
	 */
	
	@Override @Suspendable
	public ObjectInstance createMBean(final String className, final ObjectName name, final ObjectName loaderName, final Object[] params, final String[] signature) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.createMBean(className, name, loaderName, params, signature, this);
				
			}			
		}.get(ObjectInstance.class);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#getAttribute(javax.management.ObjectName, java.lang.String)
	 */
	
	@Override @Suspendable
	public Object getAttribute(final ObjectName name, final String attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.getAttribute(name, attribute, this);
				
			}			
		}.get(Object.class);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#getAttributes(javax.management.ObjectName, java.lang.String[])
	 */
	
	@Override @Suspendable
	public AttributeList getAttributes(final ObjectName name, final String[] attributes) throws InstanceNotFoundException, ReflectionException, IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.getAttributes(name, attributes, this);
				
			}			
		}.get(AttributeList.class);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#getDefaultDomain()
	 */
	
	@Override @Suspendable
	public String getDefaultDomain() throws IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.getDefaultDomain(this);
				
			}			
		}.get(String.class);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#getDomains()
	 */
	
	@Override @Suspendable
	public String[] getDomains() throws IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.getDomains(this);
				
			}			
		}.get(String[].class);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#getMBeanCount()
	 */
	
	@Override @Suspendable
	public Integer getMBeanCount() throws IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.getMBeanCount(this);
				
			}			
		}.get(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#getMBeanInfo(javax.management.ObjectName)
	 */
	
	@Override @Suspendable
	public MBeanInfo getMBeanInfo(final ObjectName name) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.getMBeanInfo(name, this);
				
			}			
		}.get(MBeanInfo.class);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#getObjectInstance(javax.management.ObjectName)
	 */
	
	@Override @Suspendable
	public ObjectInstance getObjectInstance(final ObjectName name) throws InstanceNotFoundException, IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.getObjectInstance(name, this);
				
			}			
		}.get(ObjectInstance.class);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#invoke(javax.management.ObjectName, java.lang.String, java.lang.Object[], java.lang.String[])
	 */
	
	@Override @Suspendable
	public Object invoke(final ObjectName name, final String operationName, final Object[] params, final String[] signature) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.invoke(name, operationName, params, signature, this);
				
			}			
		}.get(Object.class);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#isInstanceOf(javax.management.ObjectName, java.lang.String)
	 */
	
	@Override @Suspendable
	public boolean isInstanceOf(final ObjectName name, final String className) throws InstanceNotFoundException, IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.isInstanceOf(name, className, this);
				
			}			
		}.get(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#isRegistered(javax.management.ObjectName)
	 */
	
	@Override @Suspendable
	public boolean isRegistered(final ObjectName name) throws IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.isRegistered(name, this);
				
			}			
		}.get(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#queryMBeans(javax.management.ObjectName, javax.management.QueryExp)
	 */
	
	@Override @Suspendable
	public Set<ObjectInstance> queryMBeans(final ObjectName name, final QueryExp query) throws IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.queryMBeans(name, query, this);
				
			}			
		}.get(Set.class);
	}
	
	private final AtomicLong fiberTask = new AtomicLong();
	private final ConcurrentSkipListMap<Long, Fiber<?>> fibers = new ConcurrentSkipListMap<Long, Fiber<?>>(); 
	
	public void reset() {
		int cnt = 0;
		log("Waiting on [" + fibers.size() + "] Fibers....");
		for(Fiber<?> f: fibers.values()) {
			try { 
				while(f.getState()==Strand.State.RUNNING) {
					SystemClock.sleep(100);
				}
				log("Testing State: %s : %s", f.getName(), f.getState());
				
				cnt++;
			} catch (Exception ex) { 
				ex.printStackTrace(System.err);
			//throw new RuntimeException(ex); 
			}
		}
		fiberTask.set(0);
		log("Reset Complete. Tasks:" + cnt);
		invBuilder.build().send();
		log("BulkRequest Dispatched");
	}
	
	public static void log(final Object fmt, final Object...args) {
		final Fiber f = Fiber.currentFiber();
		if(f!=null) {
			System.out.println("f:[" + f.getName() + "]" + String.format(fmt.toString(), args));
		} else {
			System.out.println("t:[" + Thread.currentThread().getName() + "]" + String.format(fmt.toString(), args));
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#queryNames(javax.management.ObjectName, javax.management.QueryExp)
	 */
	
	@Override @Suspendable
	public Set<ObjectName> queryNames(final ObjectName name, final QueryExp query) throws IOException {
		if(Fiber.isCurrentFiber()) {
			log("Calling In Fiber: queryNames");
			try {
				
				final Fiber<Void> f = new Fiber<Void>(scheduler, new SuspendableCallable<Void>() {
					@Override
					public Void run() throws SuspendExecution, InterruptedException {   
						log("Starting MBeanServerConnectionAsync...");
						
						try {
							bmc.queryNames(name, query, new MBeanServerConnectionAsync(){
								@Override
								protected void requestAsync() {
									log("Yo. There's no action here....");
									// No Op
								}
							});
							log("------------------> Op Written");								
							
						} catch (Exception ex) {
							ex.printStackTrace(System.err);
							throw new RuntimeException(ex);
						}
						return null;
					}
				});
				fibers.put(fiberTask.incrementAndGet(), f);
				f.start();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			return null;
		}
		try {
			log("Calling In Thread: queryNames");
			return (Set<ObjectName>) fibers.remove(fiberTask.incrementAndGet()).get();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	
	public Set<ObjectName> queryNamesX(final ObjectName name, final QueryExp query) throws IOException {
		if(Fiber.isCurrentFiber()) {
			log("Calling In Fiber: queryNames");
			try {
				final Fiber<Void> f = new Fiber<Void>(scheduler, new SuspendableCallable<Void>() {
					@Override
					public Void run() throws SuspendExecution, InterruptedException {   
						log("Starting MBeanServerConnectionAsync...");
						try {
							taskExecutor.submit(new Runnable(){
								public void run() {
									bmc.queryNames(name, query, new MBeanServerConnectionAsync(){
										@Override
										protected void requestAsync() {
											// No Op
										}
									});
									log("------------------> Op Written");								
								}
							}).get();
						} catch (Exception ex) {
							ex.printStackTrace(System.err);
							throw new RuntimeException(ex);
						}
						return null;
					}
				});
				fibers.put(fiberTask.incrementAndGet(), f);
				f.start();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			return null;
		}
		try {
			log("Calling In Thread: queryNames");
			return (Set<ObjectName>) fibers.remove(fiberTask.incrementAndGet()).get();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}


	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.ObjectName)
	 */
	
	@Override @Suspendable
	public void removeNotificationListener(final ObjectName name, final ObjectName listener) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
		new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.removeNotificationListener(name, listener, this);
				
			}			
		}.inv();
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.NotificationListener)
	 */
	
	@Override @Suspendable
	public void removeNotificationListener(final ObjectName name, final NotificationListener listener) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
		new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.removeNotificationListener(name, listener, this);
				
			}			
		}.inv();		
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.ObjectName, javax.management.NotificationFilter, java.lang.Object)
	 */
	
	@Override @Suspendable
	public void removeNotificationListener(final ObjectName name, final ObjectName listener, final NotificationFilter filter, final Object handback) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
		new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.removeNotificationListener(name, listener, filter, handback, this);
				
			}			
		}.inv();		
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#removeNotificationListener(javax.management.ObjectName, javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	
	@Override @Suspendable
	public void removeNotificationListener(final ObjectName name, final NotificationListener listener, final NotificationFilter filter,	final Object handback) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
		new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.removeNotificationListener(name, listener, filter, handback, this);
				
			}			
		}.inv();		
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#setAttribute(javax.management.ObjectName, javax.management.Attribute)
	 */
	
	@Override @Suspendable
	public void setAttribute(final ObjectName name, final Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException {
		new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.setAttribute(name, attribute, this);
				
			}			
		}.inv();		
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#setAttributes(javax.management.ObjectName, javax.management.AttributeList)
	 */
	
	@Override @Suspendable
	public AttributeList setAttributes(final ObjectName name, final AttributeList attributes) throws InstanceNotFoundException, ReflectionException, IOException {
		return new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.setAttributes(name, attributes, this);
				
			}			
		}.get(AttributeList.class);		
	}

	/**
	 * {@inheritDoc}
	 * @see javax.management.MBeanServerConnection#unregisterMBean(javax.management.ObjectName)
	 */
	
	@Override @Suspendable 
	public void unregisterMBean(final ObjectName name) throws InstanceNotFoundException, MBeanRegistrationException, IOException {
		new MBeanServerConnectionAsync() {
			@Override
			protected void requestAsync() {
				bmc.unregisterMBean(name, this);
				
			}			
		}.inv();			
	}
	
	

}
