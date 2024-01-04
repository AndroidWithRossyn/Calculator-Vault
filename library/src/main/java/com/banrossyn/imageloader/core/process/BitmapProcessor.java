
package com.banrossyn.imageloader.core.process;

import android.graphics.Bitmap;
import com.banrossyn.imageloader.core.DisplayImageOptions;

/**
 * Makes some processing on {@link Bitmap}. Implementations can apply any changes to original {@link Bitmap}.<br />
 * Implementations have to be thread-safe.
 *
 * @author rossyn (banrossyn[at]gmail[dot]com)
 * @since 1.8.0
 */
public interface BitmapProcessor {
	/**
	 * Makes some processing of incoming bitmap.<br />
	 * This method is executing on additional thread (not on UI thread).<br />
	 * <b>Note:</b> If this processor is used as {@linkplain DisplayImageOptions.Builder#preProcessor(BitmapProcessor)
	 * pre-processor} then don't forget {@linkplain Bitmap#recycle() to recycle} incoming bitmap if you return a new
	 * created one.
	 *
	 * @param bitmap Original {@linkplain Bitmap bitmap}
	 * @return Processed {@linkplain Bitmap bitmap}
	 */
	Bitmap process(Bitmap bitmap);
}
