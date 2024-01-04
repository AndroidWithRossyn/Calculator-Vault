
package com.banrossyn.imageloader.cache.disc.naming;

/**
 * Generates names for files at disk cache
 *
 * @author rossyn (banrossyn[at]gmail[dot]com)
 * @since 1.3.1
 */
public interface FileNameGenerator {

	/** Generates unique file name for image defined by URI */
	String generate(String imageUri);
}
