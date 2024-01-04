package com.example.vault.privatebrowser.util;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import com.example.vault.R;

public class AnimatedProgressBar extends LinearLayout {
    private boolean mBidirectionalAnimate = true;

    public int mDrawWidth = 0;
    private final Paint mPaint = new Paint();

    public int mProgress = 0;
    private int mProgressColor;
    private final Rect mRect = new Rect();

    public AnimatedProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public AnimatedProgressBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet);
    }


    private void init(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.AnimatedProgressBar, 0, 0);
        try {
            int color = obtainStyledAttributes.getColor(0, 4342338);
            this.mProgressColor = obtainStyledAttributes.getColor(2, 2201331);
            this.mBidirectionalAnimate = obtainStyledAttributes.getBoolean(1, false);
            obtainStyledAttributes.recycle();
            ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.animated_progress_bar, this, true);
            setBackgroundColor(color);
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    public int getProgress() {
        return this.mProgress;
    }


    public void onDraw(Canvas canvas) {
        this.mPaint.setColor(this.mProgressColor);
        this.mPaint.setStrokeWidth(10.0f);
        Rect rect = this.mRect;
        rect.right = rect.left + this.mDrawWidth;
        canvas.drawRect(this.mRect, this.mPaint);
    }

    public void setProgress(int i) {
        if (i > 100) {
            i = 100;
        } else if (i < 0) {
            i = 0;
        }
        if (getAlpha() < 1.0f) {
            fadeIn();
        }
        int measuredWidth = getMeasuredWidth();
        Rect rect = this.mRect;
        rect.left = 0;
        rect.top = 0;
        rect.bottom = getBottom() - getTop();
        if (i < this.mProgress && !this.mBidirectionalAnimate) {
            this.mDrawWidth = 0;
        } else if (i == this.mProgress) {
            if (i == 100) {
                fadeOut();
            }
            return;
        }
        this.mProgress = i;
        int i2 = (this.mProgress * measuredWidth) / 100;
        int i3 = this.mDrawWidth;
        animateView(i3, measuredWidth, i2 - i3);
    }

    private void animateView(final int i, final int i2, final int i3) {
        Animation r0 = new Animation() {
            public boolean willChangeBounds() {
                return false;
            }


            public void applyTransformation(float f, Transformation transformation) {
                int hi = i + ((int) (((float) i3) * f));
                if (hi <= i2) {
                    AnimatedProgressBar.this.mDrawWidth = i;
                    AnimatedProgressBar.this.invalidate();
                }
                if (((double) (1.0f - f)) < 5.0E-4d && AnimatedProgressBar.this.mProgress >= 100) {
                    AnimatedProgressBar.this.fadeOut();
                }
            }
        };
        r0.setDuration(500);
        r0.setInterpolator(new DecelerateInterpolator());
        startAnimation(r0);
    }

    private void fadeIn() {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "alpha", new float[]{1.0f});
        ofFloat.setDuration(200);
        ofFloat.setInterpolator(new DecelerateInterpolator());
        ofFloat.start();
    }


    public void fadeOut() {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f});
        ofFloat.setDuration(200);
        ofFloat.setInterpolator(new DecelerateInterpolator());
        ofFloat.start();
    }


    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            Bundle bundle = (Bundle) parcelable;
            this.mProgress = bundle.getInt("progressState");
            parcelable = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(parcelable);
    }


    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("progressState", this.mProgress);
        return bundle;
    }
}
