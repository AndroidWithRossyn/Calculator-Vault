
package com.banrossyn.imageloader.utils;

import com.banrossyn.imageloader.cache.disc.DiskCache;

import java.io.File;

/**
 * Utility for convenient work with disk cache.<br />
 * <b>NOTE:</b> This utility works with file system so avoid using it on application main thread.
 *
 * @author rossyn (banrossyn[at]gmail[dot]com)
 * @since 1.8.0
 */
public final class DiskCacheUtils {

	private DiskCacheUtils() {
	}

	/** Returns {@link File} of cached image or <b>null</b> if image was not cached in disk cache */
	public static File findInCache(String imageUri, DiskCache diskCache) {
		File image = diskCache.get(imageUri);
		return image != null && image.exists() ? image : null;
	}

	/**
	 * Removed cached image file from disk cache (if image was cached in disk cache before)
	 *
	 * @return <b>true</b> - if cached image file existed and was deleted; <b>false</b> - otherwise.
	 */
	public static boolean removeFromCache(String imageUri, DiskCache diskCache) {
		File image = diskCache.get(imageUri);
		return image != null && image.exists() && image.delete();
	}
}
