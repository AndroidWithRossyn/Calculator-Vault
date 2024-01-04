
package com.banrossyn.imageloader.core.decode;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * Provide decoding image to result {@link Bitmap}.
 *
 * @author rossyn (banrossyn[at]gmail[dot]com)
 * @see ImageDecodingInfo
 * @since 1.8.3
 */
public interface ImageDecoder {

	/**
	 * Decodes image to {@link Bitmap} according target size and other parameters.
	 *
	 * @param imageDecodingInfo
	 * @return
	 * @throws IOException
	 */
	Bitmap decode(ImageDecodingInfo imageDecodingInfo) throws IOException;
}
