
package com.banrossyn.imageloader.core.listener;

import android.graphics.Bitmap;
import android.view.View;
import com.banrossyn.imageloader.core.assist.FailReason;

/**
 * Listener for image loading process.<br />
 * You can use {@link SimpleImageLoadingListener} for implementing only needed methods.
 *
 * @author rossyn (banrossyn[at]gmail[dot]com)
 * @see SimpleImageLoadingListener
 * @see com.banrossyn.imageloader.core.assist.FailReason
 * @since 1.0.0
 */
public interface ImageLoadingListener {

	/**
	 * Is called when image loading task was started
	 *
	 * @param imageUri Loading image URI
	 * @param view     View for image
	 */
	void onLoadingStarted(String imageUri, View view);

	/**
	 * Is called when an error was occurred during image loading
	 *
	 * @param imageUri   Loading image URI
	 * @param view       View for image. Can be <b>null</b>.
	 * @param failReason {@linkplain com.banrossyn.imageloader.core.assist.FailReason The reason} why image
	 *                   loading was failed
	 */
	void onLoadingFailed(String imageUri, View view, FailReason failReason);

	/**
	 * Is called when image is loaded successfully (and displayed in View if one was specified)
	 *
	 * @param imageUri    Loaded image URI
	 * @param view        View for image. Can be <b>null</b>.
	 * @param loadedImage Bitmap of loaded and decoded image
	 */
	void onLoadingComplete(String imageUri, View view, Bitmap loadedImage);

	/**
	 * Is called when image loading task was cancelled because View for image was reused in newer task
	 *
	 * @param imageUri Loading image URI
	 * @param view     View for image. Can be <b>null</b>.
	 */
	void onLoadingCancelled(String imageUri, View view);
}
