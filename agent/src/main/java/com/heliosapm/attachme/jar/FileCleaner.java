/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2007, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package com.heliosapm.attachme.jar;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeEvent;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.Preferences;


/**
 * <p>Title: FileCleaner</p>
 * <p>Description: Background cleaner service to clean up temporary files, especially in Windows
 * where agent files cannot be deleted until the loading JVM terminates.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.attachme.jar.FileCleaner</code></p>
 * FIXME: need a way to lock the prefs node to avoid clashes with other instances of FileCleaner in other VMs
 * TODO: add mbean interface and register 
 */

public class FileCleaner implements Runnable {
	/** The singleton instance */
	private static volatile FileCleaner instance = null;
	/** The singleton instance ctor lock */
	private static final Object lock = new Object();
	
	/** The cleaner scheduler */
	private final ScheduledExecutorService scheduler;
	/** The user prefs for this class */
	private final Preferences classPrefs = Preferences.userNodeForPackage(getClass());
	/** The user prefs to store pending file deletes */
	private Preferences pendingPrefs;
	/** The pending id serial */
	private final AtomicInteger pendingSerial = new AtomicInteger(0);
	/** The current pending files */
	private final Map<File, Integer> pendingFiles = new ConcurrentHashMap<File, Integer>();
	/** The current pending ids */
	private final Map<Integer, File> pendingIds = new ConcurrentHashMap<Integer, File>();
	/** A set of delete on exit files which will be converted to pending files if the delete fails */
	private final Set<File> deleteOnExitFiles = new CopyOnWriteArraySet<File>();
	/** The shutdown hook to execute deleteOnExitFiles and register as pending if they fail */
	private final Thread shutdownHook = new Thread() {
		public void run() {
			if(!deleteOnExitFiles.isEmpty()) {
				log("DeleteOnExit Shutdown Hook Running for [%s] files", deleteOnExitFiles.size());
				int deleted = 0;
				int notfound = 0;
				int pended = 0;
				int errors = 0;
				for(final File file: deleteOnExitFiles) {
					try {
						if(!file.exists()) {
							notfound++;
							continue;
						}
						if(!file.delete()) {
							addPendingFile(file);
							pended++;
						} else {
							deleted++;
						}
					} catch (Exception ex) {
						errors++;
						loge("Failed to delete or register delete on exit file [%s]. Stack trace follows...");
						ex.printStackTrace(System.err);
					}
				}
				log("DeleteOnExit Shutdown Hook Complete. Deleted: %s, Not Found: %s, Pending: %s, Errors: %s", deleted, notfound, pended, errors);
			} else {
				log("DeleteOnExit Shutdown Hook: No files");
			}
		}
	};
	
	/** The relative node name where we'll store pending deletes */
	public static final String PENDING_NODE = "pending-deletes";
	/** The file name key in the prefs node */
	public static final String FILE_NAME_KEY = "file-name";
	/** The file id key in the prefs node */
	public static final String FILE_ID_KEY = "file-id";
	
	/**
	 * Acquires the FileCleaner singleton instance
	 * @return the FileCleaner singleton instance
	 */
	public static FileCleaner getInstance() {
		if(instance==null) {
			synchronized(lock) {
				if(instance==null) {
					instance = new FileCleaner();
				}
			}
		}
		return instance;
	}
	
	private static void log(final Object format, final Object...args) {
		System.out.println(String.format("[FileCleaner]" + format.toString(), args));
	}
	private static void loge(final Object format, final Object...args) {
		System.err.println(String.format("[FileCleaner]" + format.toString(), args));
	}
	
	/**
	 * Removes a pending file from cache and the prefs store queue
	 * @param key The prefs store queue key
	 * @return true if a file was removed, false otherwise
	 */
	private boolean removePendingFile(final String key) {
		if(key==null || key.trim().isEmpty()) throw new IllegalArgumentException("The passed key was null or empty");
		log("Removing Pending File with ID: [%s]", key);
		try {
			final int id = parseKey(key);
			if(id != -1) {
				File f = pendingIds.remove(id);
				if(f==null) return false;
				f.delete();
				log("Removing Pending File with ID: [%s] and File Name: [%s]", key, f);
				pendingFiles.remove(f);
				pendingPrefs.node(key).removeNode();
				pendingPrefs.flush();
				return true;
			} else {
				// try and clean up anyways
				final String fileName = pendingPrefs.node(key).get(FILE_NAME_KEY, null);
				if(fileName!=null) {
					final File f = new File(fileName.trim());
					f.delete();
					Integer xid = pendingFiles.remove(f);
					if(xid!=null) {
						pendingIds.remove(xid);
					}
					pendingPrefs.node(key).removeNode();
				}
			}
			return false;
		} catch (Exception ex) {
			throw new RuntimeException("Failed to remove pending file with key [" + key + "]", ex);
		}
	}
	
	
	/**
	 * Adds a file to the pending queue
	 * @param pendingFile The file to add
	 */
	private void addPendingFile(final File pendingFile) {
		if(pendingFile==null) throw new IllegalArgumentException("The passed file was null");
		if(!pendingFile.exists() || pendingFiles.containsKey(pendingFile)) return;
		try {
			final int id = pendingSerial.incrementAndGet();
			final String key = String.format("%s", id); // toKey(pendingFile)
			Preferences p = pendingPrefs.node(key);
			p.put(FILE_NAME_KEY, pendingFile.getAbsolutePath());
			p.putInt(FILE_ID_KEY, id);
			p.flush();
			classPrefs.sync();
			log("Added Pending File. Re-read file name: [%s]", p.get(FILE_NAME_KEY, "<error>"));
			pendingFiles.put(pendingFile, id);
			pendingIds.put(id, pendingFile);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to add pending file [" + pendingFile + "]", ex);
		}
	}
	
	/**
	 * Creates a new FileCleaner
	 */
	private FileCleaner() {
//		reset();
		try {			
			if(!classPrefs.nodeExists(PENDING_NODE)) {
				classPrefs.node(PENDING_NODE);
			}
			pendingPrefs = classPrefs.node(PENDING_NODE);
			int maxId = getMaxKey();
			pendingSerial.set(maxId);
			log("Pending Prefs Root: [%s], Max Id: [%s]", pendingPrefs.absolutePath(), maxId);
		} catch (BackingStoreException e) {
			throw new RuntimeException("Failed to get pending node from Prefs", e);
		}
		Runtime.getRuntime().addShutdownHook(shutdownHook);
		log("Registered DeleteOnExit ShutdownHook");
		scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {			
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "FileCleanerThread");
				t.setDaemon(true);
				return t;
			}
		});
		scheduler.scheduleAtFixedRate(this, 10, 10, TimeUnit.SECONDS);		
	}
	
	/**
	 * Finds the max key in the store so we can start assigning values after that id.
	 * Also populates the pending file cache
	 * @return the max key in the store
	 * @throws BackingStoreException
	 */
	private int getMaxKey() throws BackingStoreException {
		int maxPending = -1;
		pendingFiles.clear();
		pendingIds.clear();
		String[] keys = pendingPrefs.childrenNames();
		if(keys!=null && keys.length>0) {
			for(final String key: keys) {
				final int id = parseKey(key);
				if(id == -1) {
					pendingPrefs.node(key).removeNode();
					continue;
				}
				final String fileName = pendingPrefs.node(key).get(FILE_NAME_KEY, null);
				if(fileName==null) {
					pendingPrefs.node(key).removeNode();
					continue;					
				}
				final File pendingFile = new File(fileName.trim());
				if(!pendingFile.exists()) {
					pendingPrefs.node(key).removeNode();
					continue;										
				}
				if(id>maxPending) {
					maxPending = id;
				}
				pendingFiles.put(pendingFile, id);
				pendingIds.put(id, pendingFile);
				log("Startup caching pending file [%s]/[%s]", id, pendingFile);
			}
		}
		return maxPending;
	}
	
	public static void main(String args[]) {
		createTestDir();
		try {
			log("FileCleaner Test");
			FileCleaner fc = getInstance();

			fc.deleteFile(new File("/etc/hosts"));
			fc.deleteFile(new File("/tmp/d.txt"));
			fc.deleteFile(new File("/tmp/newDir.0"));
			Thread.currentThread().join();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
	
	static void createTestDir() {
		getInstance().reset();
		File f = new File(System.getProperty("java.io.tmpdir"));
		for(int i = 0; i < 10; i++) {
			f = new File(f, "newDir." + i);
			f.mkdir();
		}
		log("Bottom Dir: [%s], EXISTS: %s", f, f.exists());
	}
	/**
	 * Parses the full prefs key. Returns the id as an int
	 * Returns -1 unless the parse is successful
	 * @param key The internal prefs key
	 * @return the file id
	 */
	private int parseKey(final String key) {
		if(key==null || key.trim().isEmpty()) return -1;
		try {
			return Integer.parseInt(key.trim());
		} catch (Exception ex) {
			return -1;
		}
	}
	
	/**
	 * Resets the pending file deletion repository and cache.
	 */
	public void reset() {
		try {
			pendingPrefs = classPrefs.node(PENDING_NODE);
			for(final String key: pendingPrefs.childrenNames()) {
				log("Resetting node: [%s]", key);
				removePendingFile(key);
			}			
			pendingPrefs.flush();
			classPrefs.remove(PENDING_NODE);
			pendingPrefs = classPrefs.node(PENDING_NODE);
			pendingFiles.clear();
			pendingIds.clear();
			pendingSerial.set(-1);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to reset pending prefs", ex);
		}
	}
	
	/**
	 * The file cleaner task
	 */
	public void run() {
		try {
			int cnt = 0;
			int ne = 0;
			int failed = 0;
			String[] keys = pendingPrefs.childrenNames();
			log("Running FileCleaner Task. Keys: %s", Arrays.toString(keys));
			if(keys!=null && keys.length>0) {
				for(final String key: keys) {
					final int id = parseKey(key);					
					if(id == -1) {
						log("Failed to parse [%s]", key);
						pendingPrefs.node(key).removeNode();
						ne++;
						continue;
					}
					final Preferences fileNode = pendingPrefs.node(key);
					String fileName = fileNode.get(FILE_NAME_KEY, null);
					if(fileName==null) {
						log("No file name for key [%s]", key);
						pendingPrefs.node(key).removeNode();
						ne++;
						continue;						
					}
					final File pendingFile = new File(fileName.trim());
					log("Processing Deletion File [%s]", pendingFile);					
					if(pendingFile.exists()) {
						if(pendingFile.delete()) {
							removePendingFile(key);
							cnt++;							
						} else {
							failed++;
						}
					} else {
						removePendingFile(key);
						ne++;
					}
				}
			}
			log("Cleaner Task: Deleted [%s] files, Not found files: [%s], Deletion Failed: [%s]", cnt, ne, failed);
		} catch (Exception ex) {
			loge("Cleaner task failed: %s", ex.toString());
		}
	}
	
	
//	/**
//	 * Returns the key for the passed file
//	 * @param file The file to get the key for
//	 * @return the file key
//	 */
//	public static String toKey(final File file) {
//		if(file==null) throw new IllegalArgumentException("The passed file was null");
//		return file.getAbsolutePath().replace(FILE_DELIM, KEY_DELIM);
//	}
	
//	/**
//	 * Converts the prefs key back into the File it represents
//	 * @param key The prefs key for a file
//	 * @return the represented file
//	 */
//	public static File toFile(final String key) {
//		if(key==null || key.trim().isEmpty()) throw new IllegalArgumentException("The passed key was null or empty");
//		return new File(key.replace(KEY_DELIM, FILE_DELIM));
//	}
	
	/**
	 * Adds a file to the delete on exit queue
	 * @param f The file to delete on exit
	 */
	public void deleteOnExit(final File f) {
		if(f==null || !f.exists()) return;
		deleteOnExitFiles.add(f);
		log("Registered DeleteOnExit File [%s]", f);
	}
	
	/**
	 * Deletes a file, or if it cannot be deleted, adds it to the pending queue.
	 * If the file is null, or does not exist, it is ignored.
	 * @param f The file to be deleted.
	 */
	public void deleteFile(final File f) {
		if(f==null) return;
		if(!f.exists()) return;
		if(f.isFile()) {
			if(f.delete()) return;
		} else {
			if(deleteDir(f)) return;
		}
		addPendingFile(f);
		log("Added file to pending queue: [%s]", f.getAbsolutePath());
	}
	
	
	/**
	 * Deletes a directory tree
	 * @param dir The file directory to delete
	 * @param recursive Recursion indicator
	 * @return true if successful, false otherwise
	 */
	private boolean deleteDir(final File dir, boolean...recursive) {
		final boolean recursing = recursive.length>0;
		if(dir==null) return true;
		if(!dir.exists()) return true;
		if(!dir.isDirectory()) {
			return dir.delete();
		}
		try {
			for(File f: dir.listFiles()) {
				if(f.isDirectory()) {
					deleteDir(f, true);
//						if(!f.delete()) return false;
				} else {
					if(!deleteDir(f, true)) return false;
				}
			}
			if(!dir.delete()) {
					if(!recursing) log("Failed to Delete dir tree: [%s]", dir);
				return false;
			} else {
				if(!recursing) log("Deleted dir tree: [%s]", dir);
				return true;
			}
		} catch (Exception ex) {
			throw new RuntimeException("Failed to delete dir [" + dir.getAbsolutePath() + "]", ex);
		}
	}
}
