
package com.banrossyn.imageloader.core.imageaware;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import com.banrossyn.imageloader.core.assist.ViewScaleType;

/**
 * Wrapper for Android {@link android.widget.ImageView ImageView}. Keeps weak reference of ImageView to prevent memory
 * leaks.
 *
 * @author rossyn (banrossyn[at]gmail[dot]com)
 * @since 1.9.0
 */
public class ImageViewAware extends ViewAware {

	/**
	 * Constructor. <br />
	 * References {@link #ImageViewAware(android.widget.ImageView, boolean) ImageViewAware(imageView, true)}.
	 *
	 * @param imageView {@link android.widget.ImageView ImageView} to work with
	 */
	public ImageViewAware(ImageView imageView) {
		super(imageView);
	}

	/**
	 * Constructor
	 *
	 * @param imageView           {@link android.widget.ImageView ImageView} to work with
	 * @param checkActualViewSize <b>true</b> - then {@link #getWidth()} and {@link #getHeight()} will check actual
	 *                            size of ImageView. It can cause known issues like
	 *                            <a href="https://github.com/nostra13/Android-Universal-Image-Loader/issues/376">this</a>.
	 *                            But it helps to save memory because memory cache keeps bitmaps of actual (less in
	 *                            general) size.
	 *                            <p/>
	 *                            <b>false</b> - then {@link #getWidth()} and {@link #getHeight()} will <b>NOT</b>
	 *                            consider actual size of ImageView, just layout parameters. <br /> If you set 'false'
	 *                            it's recommended 'android:layout_width' and 'android:layout_height' (or
	 *                            'android:maxWidth' and 'android:maxHeight') are set with concrete values. It helps to
	 *                            save memory.
	 *                            <p/>
	 */
	public ImageViewAware(ImageView imageView, boolean checkActualViewSize) {
		super(imageView, checkActualViewSize);
	}

	/**
	 * {@inheritDoc}
	 * <br />
	 * 3) Get <b>maxWidth</b>.
	 */
	@Override
	public int getWidth() {
		int width = super.getWidth();
		if (width <= 0) {
			ImageView imageView = (ImageView) viewRef.get();
			if (imageView != null) {
				width = imageView.getMaxWidth();
			}
		}
		return width;
	}

	/**
	 * {@inheritDoc}
	 * <br />
	 * 3) Get <b>maxHeight</b>
	 */
	@Override
	public int getHeight() {
		int height = super.getHeight();
		if (height <= 0) {
			ImageView imageView = (ImageView) viewRef.get();
			if (imageView != null) {
				height = imageView.getMaxHeight();
			}
		}
		return height;
	}

	@Override
	public ViewScaleType getScaleType() {
		ImageView imageView = (ImageView) viewRef.get();
		if (imageView != null) {
			return ViewScaleType.fromImageView(imageView);
		}
		return super.getScaleType();
	}

	@Override
	public ImageView getWrappedView() {
		return (ImageView) super.getWrappedView();
	}

	@Override
	protected void setImageDrawableInto(Drawable drawable, View view) {
		((ImageView) view).setImageDrawable(drawable);
		if (drawable instanceof AnimationDrawable) {
			((AnimationDrawable)drawable).start();
		}
	}

	@Override
	protected void setImageBitmapInto(Bitmap bitmap, View view) {
		((ImageView) view).setImageBitmap(bitmap);
	}
}
