
package com.banrossyn.imageloader.core;

import com.banrossyn.imageloader.core.listener.ImageLoadingListener;
import com.banrossyn.imageloader.core.listener.ImageLoadingProgressListener;
import com.banrossyn.imageloader.core.assist.ImageSize;
import com.banrossyn.imageloader.core.imageaware.ImageAware;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Information for load'n'display image task
 *
 * @author rossyn (banrossyn[at]gmail[dot]com)
 * @see com.banrossyn.imageloader.utils.MemoryCacheUtils
 * @see DisplayImageOptions
 * @see ImageLoadingListener
 * @see com.banrossyn.imageloader.core.listener.ImageLoadingProgressListener
 * @since 1.3.1
 */
final class ImageLoadingInfo {

	final String uri;
	final String memoryCacheKey;
	final ImageAware imageAware;
	final ImageSize targetSize;
	final DisplayImageOptions options;
	final ImageLoadingListener listener;
	final ImageLoadingProgressListener progressListener;
	final ReentrantLock loadFromUriLock;

	public ImageLoadingInfo(String uri, ImageAware imageAware, ImageSize targetSize, String memoryCacheKey,
			DisplayImageOptions options, ImageLoadingListener listener,
			ImageLoadingProgressListener progressListener, ReentrantLock loadFromUriLock) {
		this.uri = uri;
		this.imageAware = imageAware;
		this.targetSize = targetSize;
		this.options = options;
		this.listener = listener;
		this.progressListener = progressListener;
		this.loadFromUriLock = loadFromUriLock;
		this.memoryCacheKey = memoryCacheKey;
	}
}
