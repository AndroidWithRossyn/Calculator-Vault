
package com.banrossyn.imageloader.utils;

import android.graphics.Bitmap;

import com.banrossyn.imageloader.cache.memory.MemoryCache;
import com.banrossyn.imageloader.core.ImageLoaderConfiguration;
import com.banrossyn.imageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utility for generating of keys for memory cache, key comparing and other work with memory cache
 *
 * @author rossyn (banrossyn[at]gmail[dot]com)
 * @since 1.6.3
 */
public final class MemoryCacheUtils {

	private static final String URI_AND_SIZE_SEPARATOR = "_";
	private static final String WIDTH_AND_HEIGHT_SEPARATOR = "x";

	private MemoryCacheUtils() {
	}

	/**
	 * Generates key for memory cache for incoming image (URI + size).<br />
	 * Pattern for cache key - <b>[imageUri]_[width]x[height]</b>.
	 */
	public static String generateKey(String imageUri, ImageSize targetSize) {
		return new StringBuilder(imageUri).append(URI_AND_SIZE_SEPARATOR).append(targetSize.getWidth()).append(WIDTH_AND_HEIGHT_SEPARATOR).append(targetSize.getHeight()).toString();
	}

	public static Comparator<String> createFuzzyKeyComparator() {
		return new Comparator<String>() {
			@Override
			public int compare(String key1, String key2) {
				String imageUri1 = key1.substring(0, key1.lastIndexOf(URI_AND_SIZE_SEPARATOR));
				String imageUri2 = key2.substring(0, key2.lastIndexOf(URI_AND_SIZE_SEPARATOR));
				return imageUri1.compareTo(imageUri2);
			}
		};
	}

	/**
	 * Searches all bitmaps in memory cache which are corresponded to incoming URI.<br />
	 * <b>Note:</b> Memory cache can contain multiple sizes of the same image if only you didn't set
	 * {@link ImageLoaderConfiguration.Builder#denyCacheImageMultipleSizesInMemory()
	 * denyCacheImageMultipleSizesInMemory()} option in {@linkplain ImageLoaderConfiguration configuration}
	 */
	public static List<Bitmap> findCachedBitmapsForImageUri(String imageUri, MemoryCache memoryCache) {
		List<Bitmap> values = new ArrayList<Bitmap>();
		for (String key : memoryCache.keys()) {
			if (key.startsWith(imageUri)) {
				values.add(memoryCache.get(key));
			}
		}
		return values;
	}

	/**
	 * Searches all keys in memory cache which are corresponded to incoming URI.<br />
	 * <b>Note:</b> Memory cache can contain multiple sizes of the same image if only you didn't set
	 * {@link ImageLoaderConfiguration.Builder#denyCacheImageMultipleSizesInMemory()
	 * denyCacheImageMultipleSizesInMemory()} option in {@linkplain ImageLoaderConfiguration configuration}
	 */
	public static List<String> findCacheKeysForImageUri(String imageUri, MemoryCache memoryCache) {
		List<String> values = new ArrayList<String>();
		for (String key : memoryCache.keys()) {
			if (key.startsWith(imageUri)) {
				values.add(key);
			}
		}
		return values;
	}

	/**
	 * Removes from memory cache all images for incoming URI.<br />
	 * <b>Note:</b> Memory cache can contain multiple sizes of the same image if only you didn't set
	 * {@link ImageLoaderConfiguration.Builder#denyCacheImageMultipleSizesInMemory()
	 * denyCacheImageMultipleSizesInMemory()} option in {@linkplain ImageLoaderConfiguration configuration}
	 */
	public static void removeFromCache(String imageUri, MemoryCache memoryCache) {
		List<String> keysToRemove = new ArrayList<String>();
		for (String key : memoryCache.keys()) {
			if (key.startsWith(imageUri)) {
				keysToRemove.add(key);
			}
		}
		for (String keyToRemove : keysToRemove) {
			memoryCache.remove(keyToRemove);
		}
	}
}
