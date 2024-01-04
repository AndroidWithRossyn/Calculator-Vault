
package com.banrossyn.imageloader.core.display;

import android.graphics.Bitmap;
import com.banrossyn.imageloader.core.assist.LoadedFrom;
import com.banrossyn.imageloader.core.imageaware.ImageAware;

/**
 * Displays {@link Bitmap} in {@link com.banrossyn.imageloader.core.imageaware.ImageAware}. Implementations can
 * apply some changes to Bitmap or any animation for displaying Bitmap.<br />
 * Implementations have to be thread-safe.
 *
 * @author rossyn (banrossyn[at]gmail[dot]com)
 * @see com.banrossyn.imageloader.core.imageaware.ImageAware
 * @see com.banrossyn.imageloader.core.assist.LoadedFrom
 * @since 1.5.6
 */
public interface BitmapDisplayer {
	/**
	 * Displays bitmap in {@link com.banrossyn.imageloader.core.imageaware.ImageAware}.
	 * <b>NOTE:</b> This method is called on UI thread so it's strongly recommended not to do any heavy work in it.
	 *
	 * @param bitmap     Source bitmap
	 * @param imageAware {@linkplain com.banrossyn.imageloader.core.imageaware.ImageAware Image aware view} to
	 *                   display Bitmap
	 * @param loadedFrom Source of loaded image
	 */
	void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom);
}
