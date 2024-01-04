
package com.banrossyn.imageloader.core.display;

import android.graphics.*;
import com.banrossyn.imageloader.core.assist.LoadedFrom;
import com.banrossyn.imageloader.core.imageaware.ImageAware;
import com.banrossyn.imageloader.core.imageaware.ImageViewAware;

/**
 * Can display bitmap with rounded corners and vignette effect. This implementation works only with ImageViews wrapped
 * in ImageViewAware.
 * <br />
 * This implementation is inspired by
 * <a href="http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/">
 * Romain Guy's article</a>. It rounds images using custom drawable drawing. Original bitmap isn't changed.
 * <br />
 * <br />
 * If this implementation doesn't meet your needs then consider
 * <a href="https://github.com/vinc3m1/RoundedImageView">this project</a> for usage.
 *
 * @author rossyn (banrossyn[at]gmail[dot]com)
 * @since 1.9.1
 */
public class RoundedVignetteBitmapDisplayer extends RoundedBitmapDisplayer {

	public RoundedVignetteBitmapDisplayer(int cornerRadiusPixels, int marginPixels) {
		super(cornerRadiusPixels, marginPixels);
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		if (!(imageAware instanceof ImageViewAware)) {
			throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
		}

		imageAware.setImageDrawable(new RoundedVignetteDrawable(bitmap, cornerRadius, margin));
	}

	protected static class RoundedVignetteDrawable extends RoundedDrawable {

		RoundedVignetteDrawable(Bitmap bitmap, int cornerRadius, int margin) {
			super(bitmap, cornerRadius, margin);
		}

		@Override
		protected void onBoundsChange(Rect bounds) {
			super.onBoundsChange(bounds);
			RadialGradient vignette = new RadialGradient(
					mRect.centerX(), mRect.centerY() * 1.0f / 0.7f, mRect.centerX() * 1.3f,
					new int[]{0, 0, 0x7f000000}, new float[]{0.0f, 0.7f, 1.0f},
					Shader.TileMode.CLAMP);

			Matrix oval = new Matrix();
			oval.setScale(1.0f, 0.7f);
			vignette.setLocalMatrix(oval);

			paint.setShader(new ComposeShader(bitmapShader, vignette, PorterDuff.Mode.SRC_OVER));
		}
	}
}
