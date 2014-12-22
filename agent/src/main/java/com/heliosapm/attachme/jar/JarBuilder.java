/**
 * 
 */
package com.heliosapm.attachme.jar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.heliosapm.attachme.agent.LocalAgentInstaller;

/**
 * <p>Title: JarBuilder</p>
 * <p>Description: Fluent style dynamic jar archive builder</p>
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><b><code>com.heliosapm.attachme.jar.JarBuilder</code></b>
 */

public class JarBuilder {
	/** The jar's manifest entries */
	private final Map<String, String> manifestEntries = new LinkedHashMap<String, String>();
	/** Additional resources to add to the jar, keyed by the jar archive path */
	private final Map<String, byte[]> resources = new LinkedHashMap<String, byte[]>();
	/** Classes to add to the jar */
	private final Map<Class<?>, Boolean> jarClasses = new LinkedHashMap<Class<?>, Boolean>();
	
	/** A reference to the JVM's {@link java.lang.instrument.Instrumentation} */
	private static final AtomicReference<Instrumentation> instrumentation = new AtomicReference<Instrumentation>(null); 
	
	/** The compression level which defaults to BEST_SPEED */
	private final int compressionLevel;
	
	
	/** The platform mbean server */
	public static final MBeanServer PLATFORM_MBEANSERVER = ManagementFactory.getPlatformMBeanServer();
	
	
	/**
	 * Creates and returns a new JarBuilder instance that will use the specified compression level
	 * @param compressionLevel the compressionLevel to set. Must be between 0 and 9 inclusive.
	 * @return a new JarBuilder instance
	 */
	public static JarBuilder newInstance(int compressionLevel) {	
		if(compressionLevel < 0 || compressionLevel > 9) throw new IllegalArgumentException("Invalid compression level [" + compressionLevel + "]");
		return new JarBuilder(compressionLevel);
	}
	
	/**
	 * Creates and returns a new JarBuilder instance that will use the default compression level of 1
	 * @return a new JarBuilder instance
	 */
	public static JarBuilder newInstance() {	
		return new JarBuilder(1);
	}
	
	
	/**
	 * Creates a new JarBuilder
	 */
	private JarBuilder(int compressionLevel) {
		this.compressionLevel = compressionLevel;
	}
	
	/**
	 * Writes the built jar to the passed file. 
	 * @param deleteExisting If true, will delete the existing file before writing it again. 
	 * Otherwise throws a RuntimeException if the file exists.
	 * @param file The file to write jar to
	 * @return the JarFile representation of the written file
	 */
	public JarFile writeJar(final boolean deleteExisting, final File file) {
		if(file==null) throw new IllegalArgumentException("The passed file was null");
		if(file.exists()) {
			if(deleteExisting) {
				if(!file.delete()) {
					throw new RuntimeException("Failed to delete existing file [" + file + "]");
				}
			}
			else throw new IllegalStateException("The passed file [" + file + "] already exists");
		}
		
		try {
			if(!file.createNewFile()) {
				throw new Exception("Create file [" + file + "] returned false");
			}
		} catch (Exception ex) {
			throw new RuntimeException("Failed to create new file for [" + file + "]", ex);
		}
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			writeJar(bos);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to stream out jar to [" + file + "]", ex);
		} finally {
			if(fos!=null) {
				try { fos.flush(); } catch (Exception x) {/* No Op */}
				try { fos.close(); } catch (Exception x) {/* No Op */}
			}
			if(bos!=null) {
				try { bos.flush(); } catch (Exception x) {/* No Op */}
				try { bos.close(); } catch (Exception x) {/* No Op */}
			}			
		}
		try {
			return new JarFile(file);
		} catch (Exception e) {
			throw new RuntimeException("Failed to create JarFile for file [" + file + "]", e);
		}
	}

	
	/**
	 * Writes the built jar to the passed output stream
	 * @param os The output stream to write the jar to
	 * @return the number of bytes written
	 */
	public int writeJar(final OutputStream os) {
		int totalBytes = 0;
		JarOutputStream jos = null;
		try {
			StringBuilder manifest = new StringBuilder("Manifest-Version: 1.0\n");
			for(Map.Entry<String, String> entry: manifestEntries.entrySet()) {
				manifest.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(manifest.toString().getBytes());
			Manifest mf = new Manifest(bais);
			jos = new JarOutputStream(os, mf);
			jos.setLevel(compressionLevel);
			TreeMap<String, byte[]> outItems = new TreeMap<String, byte[]>(resources);
			for(Map.Entry<Class<?>, Boolean> entry: jarClasses.entrySet()) {
				outItems.put(entry.getKey().getName().replace('.', '/') + ".class", getClassBytes(entry.getValue(), entry.getKey()));
			}
			addItemsToJar(jos, outItems);
			jos.flush();
			jos.close();
			jos = null;
		} catch (Exception ex) {
			throw new RuntimeException("Failed to write out agent jar", ex);
		} finally {
			if(jos!=null) try { jos.close(); } catch (Exception e) {/* No Op */}
		}		
		return totalBytes;
	}
	
	
	/**
	 * Appends the passed map entries to the jar's manifest declarations
	 * @param entries A map of manifest entries
	 * @return this JarBuilder
	 */
	public JarBuilder addManifestEntries(final Map<String, ? extends Object> entries) {
		if(entries!=null && !entries.isEmpty()) {
			for(Map.Entry<String, ? extends Object> entry: entries.entrySet()) {
				addManifestEntry(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}
	
	/**
	 * Adds the passd key/value pair to the jar's manifest declarations
	 * @param key The manifest entry key
	 * @param value The manifest entry value
	 * @return this JarBuilder
	 */
	public <T extends Object> JarBuilder addManifestEntry(final String key, final T value) {
		if(key!=null && !key.trim().isEmpty() && value!=null) {
			String v = value.toString().trim();
			if(!v.isEmpty()) {
				manifestEntries.put(key.trim(), v);
			}
		}
		return this;
	}
	
	/**
	 * Adds the passed classes to the built jar
	 * Silently ignores:<ol>
	 * 	<li>Primitive types</li>
	 *  <li>Core classes (i.e. with package names starting with <b><code>java.</code></b>)</li>
	 * </ol>
	 * @param transformed If true, will acquire the class bytecode from the java.lang.Instrument so that the [re-]transformed bytecode is saved to the jar.
	 * @param classes The classes to add
	 * @return this JarBuilder
	 */
	public JarBuilder addClasses(final boolean transformed, final Class<?>...classes) {
		if(classes!=null) {
			for(Class<?> clazz: classes) {
				if(clazz.isPrimitive() || clazz.getPackage().getName().startsWith("java.")) continue;
				if(clazz!=null) {
					jarClasses.put(clazz, transformed);
				}
			}
		}
		return this;
	}
	
	/**
	 * Adds the passed classes to the built jar in their original (non-transformed) bytecode state
	 * Silently ignores:<ol>
	 * 	<li>Primitive types</li>
	 *  <li>Core classes (i.e. with package names starting with <b><code>java.</code></b>)</li>
	 * </ol>
	 * @param classes The classes to add
	 * @return this JarBuilder
	 */
	public JarBuilder addClasses(final Class<?>...classes) {
		return addClasses(false, classes);
	}
	
	
	/**
	 * Resolves the passed class names and adds the resolved classes to the built jar. 
	 * Silently ignores:<ol>
	 * 	<li>Primitive types</li>
	 *  <li>Core classes (i.e. with package names starting with <b><code>java.</code></b>)</li>
	 * </ol>
	 * @param transformed If true, will acquire the class bytecode from the java.lang.Instrument so that the [re-]transformed bytecode is saved to the jar.
	 * @param cl The optional class loader to use. If supplied, the classloader will be used to resolve the class names. Ignored if null.
	 * @param classNames The classnames to resolve
	 * @return this JarBuilder
	 * @throws ClassNotFoundException thrown if a classname cannot be resolved. If this occurs, no classes will be added.
	 */
	public JarBuilder addClasses(final boolean transformed, final ClassLoader cl, final String...classNames) throws ClassNotFoundException {
		if(classNames!=null) {
			final Set<Class<?>> classesToAdd = new LinkedHashSet<Class<?>>();
			for(String className: classNames) {
				if(className!=null && !className.trim().isEmpty()) {
					Class<?> clazz = null;
					if(cl==null) {
						clazz = Class.forName(className.trim());
					} else {
						clazz = Class.forName(className.trim(), false, cl);						
					}
					if(clazz.isPrimitive() || clazz.getPackage().getName().startsWith("java.")) continue;
					classesToAdd.add(clazz);
				}
			}
			for(Class<?> clazz: classesToAdd) {
				jarClasses.put(clazz, transformed);
			}			
		}
		return this;
	}
	
	/**
	 * Resolves the passed class names and adds the resolved classes to the built jar in their original (non-transformed) bytecode state. 
	 * Silently ignores:<ol>
	 * 	<li>Primitive types</li>
	 *  <li>Core classes (i.e. with package names starting with <b><code>java.</code></b>)</li>
	 * </ol>
	 * @param cl The optional class loader to use. If supplied, the classloader will be used to resolve the class names. Ignored if null.
	 * @param classNames The classnames to resolve
	 * @return this JarBuilder
	 * @throws ClassNotFoundException thrown if a classname cannot be resolved. If this occurs, no classes will be added.
	 */
	public JarBuilder addClasses(final ClassLoader cl, final String...classNames) throws ClassNotFoundException {
		return addClasses(false, cl, classNames);
	}
	
	/**
	 * Resolves the passed class names and adds the resolved classes to the built jar. 
	 * Silently ignores:<ol>
	 * 	<li>Primitive types</li>
	 *  <li>Core classes (i.e. with package names starting with <b><code>java.</code></b>)</li>
	 * </ol>
	 * @param transformed If true, will acquire the class bytecode from the java.lang.Instrument so that the [re-]transformed bytecode is saved to the jar.
	 * @param mbeanServer The optional MBeanServer to resolve the classloader from. If null, will use the platform mbean server
	 * @param objectName The ObjectName to resolve a classloader to use. The resulting classloader will be used to resolve the class names.
	 * @param classNames The classnames to resolve
	 * @return this JarBuilder
	 * @throws ClassNotFoundException thrown if a classname cannot be resolved. If this occurs, no classes will be added.
	 */
	public JarBuilder addClasses(final boolean transformed, final MBeanServer mbeanServer, final ObjectName objectName, final String...classNames) throws ClassNotFoundException {
		addClasses(transformed, getClassLoader(mbeanServer, objectName), classNames);
		return this;
	}
	
	/**
	 * Resolves the passed class names and adds the resolved classes to the built jar in their original (non-transformed) bytecode state.
	 * Silently ignores:<ol>
	 * 	<li>Primitive types</li>
	 *  <li>Core classes (i.e. with package names starting with <b><code>java.</code></b>)</li>
	 * </ol>
	 * @param mbeanServer The optional MBeanServer to resolve the classloader from. If null, will use the platform mbean server
	 * @param objectName The ObjectName to resolve a classloader to use. The resulting classloader will be used to resolve the class names.
	 * @param classNames The classnames to resolve
	 * @return this JarBuilder
	 * @throws ClassNotFoundException thrown if a classname cannot be resolved. If this occurs, no classes will be added.
	 */
	public JarBuilder addClasses(final MBeanServer mbeanServer, final ObjectName objectName, final String...classNames) throws ClassNotFoundException {
		return addClasses(false, mbeanServer, objectName, classNames);
	}
	
	
	/**
	 * Resolves the passed class names and adds the resolved classes to the built jar, resolving the passed ObjectName into a ClassLoader from the platform MBeanServer.
	 * Silently ignores:<ol>
	 * 	<li>Primitive types</li>
	 *  <li>Core classes (i.e. with package names starting with <b><code>java.</code></b>)</li>
	 * </ol>
	 * @param transformed If true, will acquire the class bytecode from the java.lang.Instrument so that the [re-]transformed bytecode is saved to the jar.
	 * @param objectName The ObjectName to resolve a classloader to use. The resulting classloader will be used to resolve the class names.
	 * @param classNames The classnames to resolve
	 * @return this JarBuilder
	 * @throws ClassNotFoundException thrown if a classname cannot be resolved. If this occurs, no classes will be added.
	 */
	public JarBuilder addClasses(final boolean transformed, final ObjectName objectName, final String...classNames) throws ClassNotFoundException {
		addClasses(transformed, getClassLoader(PLATFORM_MBEANSERVER, objectName), classNames);
		return this;
	}
	
	/**
	 * Resolves the passed class names and adds the resolved classes to the built jar in their original (non-transformed) bytecode state, resolving the passed ObjectName into a ClassLoader from the platform MBeanServer.
	 * Silently ignores:<ol>
	 * 	<li>Primitive types</li>
	 *  <li>Core classes (i.e. with package names starting with <b><code>java.</code></b>)</li>
	 * </ol>
	 * @param objectName The ObjectName to resolve a classloader to use. The resulting classloader will be used to resolve the class names.
	 * @param classNames The classnames to resolve
	 * @return this JarBuilder
	 * @throws ClassNotFoundException thrown if a classname cannot be resolved. If this occurs, no classes will be added.
	 */
	public JarBuilder addClasses(final ObjectName objectName, final String...classNames) throws ClassNotFoundException {
		return addClasses(false, objectName, classNames);
	}
	
	
	
	/**
	 * Resolves the passed class names and adds the resolved classes to the built jar. 
	 * Silently ignores:<ol>
	 * 	<li>Primitive types</li>
	 *  <li>Core classes (i.e. with package names starting with <b><code>java.</code></b>)</li>
	 * </ol>
	 * @param transformed If true, will acquire the class bytecode from the java.lang.Instrument so that the [re-]transformed bytecode is saved to the jar.
	 * @param classNames The classnames to resolve
	 * @return this JarBuilder
	 * @throws ClassNotFoundException thrown if a classname cannot be resolved. If this occurs, no classes will be added.
	 */
	public JarBuilder addClasses(final boolean transformed, final String...classNames) throws ClassNotFoundException {
		addClasses(transformed, (ClassLoader)null, classNames);
		return this;
	}
	
	/**
	 * Resolves the passed class names and adds the resolved classes to the built jar in their original (non-transformed) bytecode state. 
	 * Silently ignores:<ol>
	 * 	<li>Primitive types</li>
	 *  <li>Core classes (i.e. with package names starting with <b><code>java.</code></b>)</li>
	 * </ol>
	 * @param classNames The classnames to resolve
	 * @return this JarBuilder
	 * @throws ClassNotFoundException thrown if a classname cannot be resolved. If this occurs, no classes will be added.
	 */
	public JarBuilder addClasses(final String...classNames) throws ClassNotFoundException {
		return addClasses(false, classNames);
	}
	
	
	/**
	 * Returns the compression level
	 * @return the compressionLevel
	 */
	public int getCompressionLevel() {
		return compressionLevel;
	}


	
	/**
	 * Adds a resource to be written to the jar
	 * @param name The name of the resource. Should be a <b><code>/</code></b> delimeted path.
	 * @param resource The resource in byte array form
	 * @return this JarBuilder
	 */
	public JarBuilder addResource(final String name, final byte[] resource) {
		if(name==null || name.trim().isEmpty()) throw new IllegalArgumentException("Resource name was null or empty");
		if(resource==null || resource.length==0) throw new IllegalArgumentException("Resource was null or empty");
		resources.put(name.trim(), resource);
		return this;
	}
	
	/**
	 * Adds a resource to be written to the jar
	 * @param resource The resource in byte array form
	 * @param names The path of the resource which will be build by concatenating all the passed values with a <b><code>/</code></b> character 
	 * @return this JarBuilder
	 */
	public JarBuilder addResource(final byte[] resource, final String...names) {
		if(resource==null || resource.length==0) throw new IllegalArgumentException("Resource was null or empty");
		if(names==null || names.length==0) throw new IllegalArgumentException("Resource names was null or empty");
		String name = buildPath(names);
		resources.put(name, resource);
		return this;
	}

	/**
	 * Adds a serializable object as a resource to be written to the jar
	 * @param resource The serializable resource to write
	 * @param names The path of the resource which will be build by concatenating all the passed values with a <b><code>/</code></b> character
	 * @return this JarBuilder
	 */
	public JarBuilder addResource(final Serializable resource, final String...names) {
		return addResource(buildPath(names), resource);
	}
	
	/**
	 * Adds a serializable object as a resource to be written to the jar
	 * @param name The path of the resource
	 * @param resource The serializable resource to write
	 * @return this JarBuilder
	 */
	public JarBuilder addResource(final String name, final Serializable resource) {
		if(name==null || name.trim().isEmpty()) throw new IllegalArgumentException("Resource name was null or empty");	
		if(resource==null) throw new IllegalArgumentException("Resource was null");
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream(256);
			oos = new ObjectOutputStream(baos);
			oos.writeObject(resource);
			oos.flush();
			baos.flush();
			resources.put(name, baos.toByteArray());
		} catch (Exception ex) {
			throw new RuntimeException("Failed to write the serializable to a byte stream", ex);
		} finally {
			if(baos!=null) try { baos.close(); } catch (Exception x) {/* No Op */}
		}		
		return this;
	}
	
	/**
	 * Adds the stream content of a URL resource as a resource  to be written to the jar
	 * @param name The path of the resource
	 * @param resource The URL of the resource to read from
	 * @return this JarBuilder
	 */
	public JarBuilder addResource(final String name, final URL resource) {
		if(name==null || name.trim().isEmpty()) throw new IllegalArgumentException("Resource name was null or empty");	
		if(resource==null) throw new IllegalArgumentException("URL resource was null");		
		ByteArrayOutputStream baos = null;
		InputStream is = null;
		BufferedInputStream bis = null;
		
		try {
			is = resource.openStream();
			int available = is.available();
			if(available>1) {
				baos = new ByteArrayOutputStream(available);
			} else {
				baos = new ByteArrayOutputStream(1024);
			}
			bis = new BufferedInputStream(is);
			byte[] buff = new byte[1024];
			int bytesRead = -1;
			while((bytesRead = bis.read(buff))!=-1) {
				baos.write(buff, 0, bytesRead);
			}
			baos.flush();
			resources.put(name, baos.toByteArray());
		} catch (Exception ex) {
			throw new RuntimeException("Failed to write the serializable to a byte stream", ex);
		} finally {
			if(baos!=null) try { baos.close(); } catch (Exception x) {/* No Op */}
			if(bis!=null) try { bis.close(); } catch (Exception x) {/* No Op */}
			if(is!=null) try { is.close(); } catch (Exception x) {/* No Op */}
		}		
		
		return this;
	}
	
	/**
	 * Adds the stream content of a URL resource as a resource  to be written to the jar
	 * @param resource The URL of the resource to read from
	 * @param names The path of the resource which will be build by concatenating all the passed values with a <b><code>/</code></b> character
	 * @return this JarBuilder
	 */
	public JarBuilder addResource(final URL resource, final String...names) {
		return addResource(buildPath(names), resource);		
	}
	
	/**
	 * Adds the stream content of a file resource as a resource  to be written to the jar
	 * @param name The path of the resource
	 * @param resource The file to read from
	 * @return this JarBuilder
	 */
	public JarBuilder addResource(final String name, final File resource) {
		if(name==null || name.trim().isEmpty()) throw new IllegalArgumentException("Resource name was null or empty");	
		if(resource==null) throw new IllegalArgumentException("File resource was null");
		if(!resource.canRead()) throw new IllegalArgumentException("Cannot read the file [" + resource + "]");
		URL url = null;
		try {
			url = resource.getAbsoluteFile().toURI().toURL();
		} catch (MalformedURLException mue) {
			throw new RuntimeException("Unexpected error converting file name [" + resource.getAbsolutePath() + "] to a URL", mue);
		}
		return addResource(name, url);
	}
	
	/**
	 * Adds the stream content of a File resource as a resource  to be written to the jar
	 * @param resource The file to read from
	 * @param names The path of the resource which will be build by concatenating all the passed values with a <b><code>/</code></b> character
	 * @return this JarBuilder
	 */
	public JarBuilder addResource(final File resource, final String...names) {
		return addResource(buildPath(names), resource);		
	}
	
	
	
	


	/**
	 * Writes the passed classes to the passed JarOutputStream
	 * @param jos the JarOutputStream
	 * @param items The map of items to write
	 * @throws IOException on an IOException
	 */
	protected static void addItemsToJar(final JarOutputStream jos, final TreeMap<String, byte[]> items) throws IOException {
		for(Map.Entry<String, byte[]> entry: items.entrySet()) {
			jos.putNextEntry(new ZipEntry(entry.getKey()));
			jos.write(entry.getValue());
			jos.flush();
			jos.closeEntry();
		}
	}
	
	/**
	 * Builds a jar path from the passed names
	 * @param names The names which will be concatenated with a <b><code>/</code></b> character
	 * @return the built name
	 */
	public static String buildPath(final String...names) {
		if(names==null || names.length==0) throw new IllegalArgumentException("Resource names was null or empty");
		StringBuilder b = new StringBuilder();		
		for(String s: names) {
			if(s==null || s.trim().isEmpty()) continue;
			b.append(s).append("/");
		}
		if(b.length()==0) throw new IllegalArgumentException("Resource name concat was null or empty");
		return b.deleteCharAt(b.length()-1).toString();		
	}
	
	
	/**
	 * Returns the bytecode bytes for the passed class
	 * @param transformed If true, will acquire the byte code from java lang Instrumentation so that the transformed byte code will be saved to the jar
	 * @param clazz The class to get the bytecode for
	 * @return a byte array of bytecode for the passed class
	 */
	public static byte[] getClassBytes(final boolean transformed, final Class<?> clazz) {
		if(transformed) {
			final byte[][] byteCode = new byte[1][1];
			final ClassFileTransformer byteCodeGrabber = byteCodeGetter(clazz, byteCode);
			final Instrumentation instr = getInstrumentation();
			try {
				instr.addTransformer(byteCodeGrabber, true);
				instr.retransformClasses(clazz);
			} catch (Exception ex) {
				throw new RuntimeException("Failed to get transformed bytecode for class [" + clazz.getName() + "]", ex);
			} finally {
				instr.removeTransformer(byteCodeGrabber);
			}
			return byteCode[0];
		}
		InputStream is = null;
		try {
			is = clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + ".class");
			ByteArrayOutputStream baos = new ByteArrayOutputStream(is.available());
			byte[] buffer = new byte[8092];
			int bytesRead = -1;
			while((bytesRead = is.read(buffer))!=-1) {
				baos.write(buffer, 0, bytesRead);
			}
			baos.flush();
			return baos.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("Failed to read class bytes for [" + clazz.getName() + "]", e);
		} finally {
			if(is!=null) { try { is.close(); } catch (Exception e) {/* No Op */} }
		}
	}
	
	/**
	 * Returns a class byte code capturing ClassFileTransformer for the passed class
	 * @param clazz The class to get the byte code for
	 * @param byteCode A 2D byte array that the bytecode will be inserted into.
	 * @return the byte code capturing ClassFileTransformer
	 */
	private static ClassFileTransformer byteCodeGetter(final Class<?> clazz, final byte[][] byteCode) {		
		return new ClassFileTransformer() {
			final String internalFormClassName = clazz.getName().replace('.', '/');
			
			/**
			 * {@inheritDoc}
			 * @see java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader, java.lang.String, java.lang.Class, java.security.ProtectionDomain, byte[])
			 */
			@Override
			public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
				if(internalFormClassName.equals(className)) {
					byteCode[0] = classfileBuffer;
				}
				return classfileBuffer;
			}
		};
	}

	/**
	 * Acquires the JVM's {@link java.lang.instrument.Instrumentation} instance
	 * @return the Instrumentation instance
	 */
	private static Instrumentation getInstrumentation() {
		if(instrumentation.get()==null) {
			synchronized(instrumentation) {
				if(instrumentation.get()==null) {
					final Instrumentation instr = LocalAgentInstaller.getInstrumentation();
					instrumentation.set(instr);
				}
			}
		}
		return instrumentation.get();
	}
	
	
	/**
	 * Returns the classloader associated with the passed object name. If the represented MBean is a classloader, then that classloader is returned.
	 * Otherwise, the classloader for the represented MBean is returned.
	 * @param mbeanServer The optional MBeanServer to get the classloader from. If null, will use the platform MBeanServer
	 * @param objectName The ObjectName to get the associated classloader for
	 * @return the classloader
	 */
	public static ClassLoader getClassLoader(final MBeanServer mbeanServer, final ObjectName objectName) {
		final MBeanServer mbs = mbeanServer==null ? PLATFORM_MBEANSERVER : mbeanServer; 
		if(objectName==null) throw new IllegalArgumentException("The passed ObjectName was null");
		if(mbs.isRegistered(objectName)) throw new IllegalArgumentException("The passed ObjectName is not registered in the passed MBeanServer");
		try {
			if(mbs.isInstanceOf(objectName, "java.lang.ClassLoader")) {
				return mbs.getClassLoader(objectName);
			}
			return mbs.getClassLoaderFor(objectName);			
		} catch (Exception ex) {
			throw new RuntimeException("Failed to get ClassLoader from ObjectName [" + objectName + "]", ex);
		}
	}
	
}
