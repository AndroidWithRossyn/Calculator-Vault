
package com.banrossyn.imageloader.cache.memory;

import android.graphics.Bitmap;

import java.util.Collection;

/**
 * Interface for memory cache
 *
 * @author rossyn (banrossyn[at]gmail[dot]com)
 * @since 1.9.2
 */
public interface MemoryCache {
	/**
	 * Puts value into cache by key
	 *
	 * @return <b>true</b> - if value was put into cache successfully, <b>false</b> - if value was <b>not</b> put into
	 * cache
	 */
	boolean put(String key, Bitmap value);

	/** Returns value by key. If there is no value for key then null will be returned. */
	Bitmap get(String key);

	/** Removes item by key */
	Bitmap remove(String key);

	/** Returns all keys of cache */
	Collection<String> keys();

	/** Remove all items from cache */
	void clear();
}
