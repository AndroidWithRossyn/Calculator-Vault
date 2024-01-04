
package com.banrossyn.imageloader.cache.disc.impl;

import com.banrossyn.imageloader.cache.disc.naming.FileNameGenerator;

import java.io.File;

/**
 * Default implementation of {@linkplain com.banrossyn.imageloader.cache.disc.DiskCache disk cache}.
 * Cache size is unlimited.
 *
 */
public class UnlimitedDiskCache extends BaseDiskCache {
	/** @param cacheDir Directory for file caching */
	public UnlimitedDiskCache(File cacheDir) {
		super(cacheDir);
	}

	/**
	 * @param cacheDir        Directory for file caching
	 * @param reserveCacheDir null-ok; Reserve directory for file caching. It's used when the primary directory isn't available.
	 */
	public UnlimitedDiskCache(File cacheDir, File reserveCacheDir) {
		super(cacheDir, reserveCacheDir);
	}

	/**
	 * @param cacheDir          Directory for file caching
	 * @param reserveCacheDir   null-ok; Reserve directory for file caching. It's used when the primary directory isn't available.
	 * @param fileNameGenerator {@linkplain com.banrossyn.imageloader.cache.disc.naming.FileNameGenerator
	 *                          Name generator} for cached files
	 */
	public UnlimitedDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator) {
		super(cacheDir, reserveCacheDir, fileNameGenerator);
	}
}
