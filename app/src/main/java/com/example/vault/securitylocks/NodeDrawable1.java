package com.example.vault.securitylocks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

public class NodeDrawable1 extends Drawable {
    public static final int CIRCLE_COUNT = 3;
    public static final int CIRCLE_INNER = 2;
    public static final int CIRCLE_MIDDLE = 1;
    public static final int[] CIRCLE_ORDER = {0, 1, 2};
    public static final int CIRCLE_OUTER = 0;
    public static final float[] CIRCLE_RATIOS = {0.6f, 0.59f, 0.11f};
    public static final int[] DEFAULT_CIRCLE_COLORS;
    public static final int[] DEFAULT_STATE_COLORS;
    public static final int STATE_CORRECT = 3;
    public static final int STATE_CUSTOM = 5;
    public static final int STATE_HIGHLIGHTED = 2;
    public static final int STATE_INCORRECT = 4;
    public static final int STATE_SELECTED = 1;
    public static final int STATE_UNSELECTED = 0;
    public static int gray = -6710887;
    public static int default_pattern_color = gray;

    public static int green = -9322680;
    public static int red = -1094836;
    public static int seagreen = -16724788;
    public static int text1color = 6710886;
    public static int white = -1;
    Context con;
    protected float mArrowBaseRad;
    protected float mArrowHalfBase;
    protected float mArrowTipRad;
    protected Point mCenter;
    protected ShapeDrawable[] mCircles = new ShapeDrawable[3];
    protected int mCustomColor;
    protected float mDiameter;
    protected float mExitAngle;
    protected Path mExitIndicator;
    protected Paint mExitPaint;
    protected int mState;

    static {
        int i = default_pattern_color;
        int i2 = green;
        DEFAULT_STATE_COLORS = new int[]{i, i2, seagreen, i2, red, i};
        DEFAULT_CIRCLE_COLORS = new int[]{DEFAULT_STATE_COLORS[0], white, i};
    }

    public NodeDrawable1(float f, Point point, Context context) {
        this.con = context;
        this.mCenter = point;
        this.mDiameter = f;
        this.mState = 0;
        this.mExitAngle = Float.NaN;
        setCustomColor(DEFAULT_STATE_COLORS[5]);
        this.mExitPaint = new Paint();
        this.mExitPaint.setColor(3);
        this.mExitPaint.setStyle(Style.FILL);
        this.mExitPaint.setFlags(1);
        buildShapes(f, point);
    }

    public void draw(Canvas canvas) {
        for (int i = 0; i < 3; i++) {
            this.mCircles[CIRCLE_ORDER[i]].draw(canvas);
        }
        if (!Float.isNaN(this.mExitAngle)) {
            canvas.drawPath(this.mExitIndicator, this.mExitPaint);
        }
    }

    private void buildShapes(float f, Point point) {
        for (int i = 0; i < 3; i++) {
            this.mCircles[i] = new ShapeDrawable(new OvalShape());
            Paint paint = this.mCircles[i].getPaint();
            paint.setStyle(Style.FILL);
            paint.setStrokeWidth(6.0f);
            paint.setColor(DEFAULT_CIRCLE_COLORS[i]);
            paint.setFlags(1);
            int i2 = (int) ((CIRCLE_RATIOS[i] * f) / 2.0f);
            this.mCircles[i].setBounds(point.x - i2, point.y - i2, point.x + i2, point.y + i2);
        }
        Paint paint2 = this.mCircles[0].getPaint();
        paint2.setColor(default_pattern_color);
        paint2.setStyle(Style.STROKE);
        paint2.setStrokeWidth(2.0f);
        this.mCircles[1].getPaint().setColor(default_pattern_color);
        this.mCircles[1].setAlpha(50);
        this.mCircles[2].getPaint().setColor(default_pattern_color);
        float f2 = (f * CIRCLE_RATIOS[1]) / 2.0f;
        this.mArrowTipRad = 0.9f * f2;
        this.mArrowBaseRad = 0.6f * f2;
        this.mArrowHalfBase = f2 * 0.3f;
    }

    public void setNodeState(int i) {
        int i2 = this.mCustomColor;
        if (i != 5) {
            i2 = DEFAULT_STATE_COLORS[i];
        }
        Paint paint = this.mCircles[0].getPaint();
        paint.setColor(i2);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(3.0f);
        this.mCircles[1].getPaint().setColor(i2);
        this.mCircles[1].setAlpha(50);
        this.mCircles[2].getPaint().setColor(i2);
        this.mExitPaint.setColor(i2);
        if (i == 4) {
            Paint paint2 = this.mCircles[0].getPaint();
            paint2.setColor(i2);
            paint2.setStyle(Style.STROKE);
            paint2.setStrokeWidth(3.0f);
            this.mCircles[1].getPaint().setColor(i2);
            this.mCircles[1].setAlpha(50);
            this.mCircles[2].getPaint().setColor(i2);
            this.mExitPaint.setColor(i2);
        }
        if (i == 0) {
            Paint paint3 = this.mCircles[0].getPaint();
            paint3.setColor(default_pattern_color);
            paint3.setStyle(Style.STROKE);
            paint3.setStrokeWidth(2.0f);
            this.mCircles[1].getPaint().setColor(default_pattern_color);
            this.mCircles[1].setAlpha(50);
            this.mCircles[2].getPaint().setColor(i2);
            this.mExitPaint.setColor(i2);
            setExitAngle(Float.NaN);
        }
        this.mState = i;
    }

    public int getNodeState() {
        return this.mState;
    }

    public void setExitAngle(float f) {
        if (!Float.isNaN(f)) {
            double d = (double) f;
            float cos = ((float) this.mCenter.x) - (((float) Math.cos(d)) * this.mArrowTipRad);
            float sin = ((float) this.mCenter.y) - (((float) Math.sin(d)) * this.mArrowTipRad);
            float cos2 = ((float) this.mCenter.x) - (((float) Math.cos(d)) * this.mArrowBaseRad);
            float sin2 = ((float) this.mCenter.y) - (((float) Math.sin(d)) * this.mArrowBaseRad);
            float f2 = this.mArrowHalfBase;
            Double.isNaN(d);
            double d2 = d + 1.5707963267948966d;
            float cos3 = cos2 - (f2 * ((float) Math.cos(d2)));
            float sin3 = sin2 - (this.mArrowHalfBase * ((float) Math.sin(d2)));
            float f3 = this.mArrowHalfBase;
            Double.isNaN(d);
            double d3 = d - 1.5707963267948966d;
            float cos4 = cos2 - (f3 * ((float) Math.cos(d3)));
            float sin4 = sin2 - (this.mArrowHalfBase * ((float) Math.sin(d3)));
            Path path = new Path();
            path.moveTo(cos, sin);
            path.lineTo(cos3, sin3);
            path.lineTo(cos4, sin4);
            path.lineTo(cos, sin);
            this.mExitIndicator = path;
        }
        this.mExitAngle = f;
    }

    public float getExitAngle() {
        return this.mExitAngle;
    }

    public Point getCenter() {
        return this.mCenter;
    }

    public void setCustomColor(int i) {
        this.mCustomColor = i;
    }

    public int getCustomColor() {
        return this.mCustomColor;
    }

    public int getOpacity() {
        return this.mCircles[1].getOpacity();
    }

    public void setAlpha(int i) {
        this.mCircles[0].setAlpha(i);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        for (int i = 0; i < 3; i++) {
            this.mCircles[i].setColorFilter(colorFilter);
        }
    }
}
