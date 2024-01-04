
package com.banrossyn.imageloader.core.display;

import android.graphics.Bitmap;
import com.banrossyn.imageloader.core.assist.LoadedFrom;
import com.banrossyn.imageloader.core.imageaware.ImageAware;

/**
 * Just displays {@link Bitmap} in {@link com.banrossyn.imageloader.core.imageaware.ImageAware}
 *
 * @author rossyn (banrossyn[at]gmail[dot]com)
 * @since 1.5.6
 */
public final class SimpleBitmapDisplayer implements BitmapDisplayer {
	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		imageAware.setImageBitmap(bitmap);
	}
}