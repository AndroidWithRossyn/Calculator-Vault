
package com.banrossyn.imageloader.core.display;

import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import com.banrossyn.imageloader.core.assist.LoadedFrom;
import com.banrossyn.imageloader.core.imageaware.ImageAware;

/**
 * Displays image with "fade in" animation
 *
 * @author rossyn (banrossyn[at]gmail[dot]com), Daniel Martí
 * @since 1.6.4
 */
public class FadeInBitmapDisplayer implements BitmapDisplayer {

	private final int durationMillis;

	private final boolean animateFromNetwork;
	private final boolean animateFromDisk;
	private final boolean animateFromMemory;

	/**
	 * @param durationMillis Duration of "fade-in" animation (in milliseconds)
	 */
	public FadeInBitmapDisplayer(int durationMillis) {
		this(durationMillis, true, true, true);
	}

	/**
	 * @param durationMillis     Duration of "fade-in" animation (in milliseconds)
	 * @param animateFromNetwork Whether animation should be played if image is loaded from network
	 * @param animateFromDisk    Whether animation should be played if image is loaded from disk cache
	 * @param animateFromMemory  Whether animation should be played if image is loaded from memory cache
	 */
	public FadeInBitmapDisplayer(int durationMillis, boolean animateFromNetwork, boolean animateFromDisk,
								 boolean animateFromMemory) {
		this.durationMillis = durationMillis;
		this.animateFromNetwork = animateFromNetwork;
		this.animateFromDisk = animateFromDisk;
		this.animateFromMemory = animateFromMemory;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		imageAware.setImageBitmap(bitmap);

		if ((animateFromNetwork && loadedFrom == LoadedFrom.NETWORK) ||
				(animateFromDisk && loadedFrom == LoadedFrom.DISC_CACHE) ||
				(animateFromMemory && loadedFrom == LoadedFrom.MEMORY_CACHE)) {
			animate(imageAware.getWrappedView(), durationMillis);
		}
	}

	/**
	 * Animates {@link ImageView} with "fade-in" effect
	 *
	 * @param imageView      {@link ImageView} which display image in
	 * @param durationMillis The length of the animation in milliseconds
	 */
	public static void animate(View imageView, int durationMillis) {
		if (imageView != null) {
			AlphaAnimation fadeImage = new AlphaAnimation(0, 1);
			fadeImage.setDuration(durationMillis);
			fadeImage.setInterpolator(new DecelerateInterpolator());
			imageView.startAnimation(fadeImage);
		}
	}
}
