
package com.banrossyn.imageloader.cache.memory;

import android.graphics.Bitmap;

import java.lang.ref.Reference;
import java.util.*;

/**
 * Base memory cache. Implements common functionality for memory cache. Provides object references (
 * {@linkplain Reference not strong}) storing.
 *
 * @author rossyn (banrossyn[at]gmail[dot]com)
 * @since 1.0.0
 */
public abstract class BaseMemoryCache implements MemoryCache {

	/** Stores not strong references to objects */
	private final Map<String, Reference<Bitmap>> softMap = Collections.synchronizedMap(new HashMap<String, Reference<Bitmap>>());

	@Override
	public Bitmap get(String key) {
		Bitmap result = null;
		Reference<Bitmap> reference = softMap.get(key);
		if (reference != null) {
			result = reference.get();
		}
		return result;
	}

	@Override
	public boolean put(String key, Bitmap value) {
		softMap.put(key, createReference(value));
		return true;
	}

	@Override
	public Bitmap remove(String key) {
		Reference<Bitmap> bmpRef = softMap.remove(key);
		return bmpRef == null ? null : bmpRef.get();
	}

	@Override
	public Collection<String> keys() {
		synchronized (softMap) {
			return new HashSet<String>(softMap.keySet());
		}
	}

	@Override
	public void clear() {
		softMap.clear();
	}

	/** Creates {@linkplain Reference not strong} reference of value */
	protected abstract Reference<Bitmap> createReference(Bitmap value);
}
