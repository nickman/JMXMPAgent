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
package com.heliosapm.jmxmp.async.instrument;

import java.lang.annotation.Annotation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import co.paralleluniverse.fibers.Suspendable;

/**
 * <p>Title: FibonnoitreClassFileTransformer</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.async.instrument.FibonnoitreClassFileTransformer</code></p>
 */

public class FibonnoitreClassFileTransformer implements ClassFileTransformer {
	final ClassPool classPool = new ClassPool();
	final Class<? extends Annotation> fibAnn = FiberExecuteAhead.class;
	final Class<? extends Annotation> susAnn = Suspendable.class;
	
	/**
	 * Creates a new FibonnoitreClassFileTransformer
	 */
	public FibonnoitreClassFileTransformer() {
		classPool.appendSystemPath();
	}
	
	public static String bin(final String className) {
		return className.replace('/', '.');
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader, java.lang.String, java.lang.Class, java.security.ProtectionDomain, byte[])
	 */
	@Override
	public byte[] transform(final ClassLoader classLoader, final String className, final Class<?> clazz, final ProtectionDomain protectionDomain, final byte[] byteCode) throws IllegalClassFormatException {
		final LoaderClassPath lcp = new LoaderClassPath(classLoader);
		try {
			final CtClass cc = classPool.get(bin(className));
			final ClassFile ccFile = cc.getClassFile();
			final ConstPool constpool = ccFile.getConstPool();			
			for(CtMethod ctm: cc.getMethods()) {
				if(ctm.getAnnotation(fibAnn)==null) continue;
				instrumentMethod(ctm, constpool);
			}
		} catch (Exception ex) {
			return null;
		} finally {
			classPool.removeClassPath(lcp);
		}
		
		return null;
	}
	
	protected void instrumentMethod(final CtMethod ctm, final ConstPool constpool) {
		try {
			if(ctm.getAnnotation(susAnn)==null) {
				AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
				javassist.bytecode.annotation.Annotation annot = new javassist.bytecode.annotation.Annotation(susAnn.getName(), constpool);
				attr.addAnnotation(annot);
				ctm.getMethodInfo().addAttribute(attr);
			}
			/*
			 * Install latch. Needs to be accessible in finally block, so we need a singleton registry.
			 * Spin up fiber and invoke this method in fiber
			 * 	-- static method
			 *  -- non static method
			 * Fiber execution: accumulate all pending sub-fibers
			 * finally block:
			 *  -- if running in thread, skip.
			 * 	-- send batch
			 *  -- wait for all sub-fibers to complete
			 *  -- drop thread latch
			 *  
			 */
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			throw new RuntimeException(ex);
		}
	}

}
