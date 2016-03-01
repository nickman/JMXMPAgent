/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.heliosapm.jmxmp.spec;

/**
 * <p>Title: NVP</p>
 * <p>Description: Name value pair. Copies from heliosutils to avoid the dependency</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmxmp.spec.NVP</code></p>
 */

public class NVP<K,V> {
	/** The NVP key */
	protected K key;
	/** The NVP value */
	protected V value;

	/**
	 * Creates a new NVP
	 * @param key The NVP key
	 * @param value The NVP value
	 */
	public NVP(final K key, final V value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Returns the NVP value
	 * @return the value
	 */
	public V getValue() {
		return value;
	}

	/**
	 * Returns the NVP key
	 * @return the key
	 */
	public K getKey() {
		return key;
	}
	
	/**
	 * Returns the key type
	 * @return the key type
	 */
	@SuppressWarnings("unchecked")
	public Class<K> getKeyType() {
		return (Class<K>) (key==null ? null : key.getClass());
	}
	
	/**
	 * Returns the value type
	 * @return the value type
	 */
	@SuppressWarnings("unchecked")
	public Class<V> getValueType() {
		return (Class<V>) (value==null ? null : value.getClass());
	}
	
	
	public String dump() {
		final StringBuilder b = new StringBuilder(getClass().getSimpleName()).append(" [");
		b.append(" key: ");
		if(key!=null) {
			b.append(key.getClass().getName()).append(": (").append(key).append(")");
		}
		else {
			b.append("null");
		}
		b.append(" value: ");
		if(value!=null) {
			b.append(value.getClass().getName()).append(": (").append(value).append(")");
		} else {
			b.append("null");		
		}
		return b.toString();
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder b = new StringBuilder(" [key:(");
		if(key!=null) {
			b.append(key).append("), ");
		}else {
			b.append("null), ");
		}
		
		b.append("value:(");
		if(value!=null) {
			b.append(value).append(")");
		}
		else {
			b.append("null)");
		}
		b.append("]");
		return b.toString();
	}
	
	
}
