package com.example.vault.notes.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class LinedEditText extends AppCompatEditText {
    private static final float DASHED_LINE_OFF_SCALE_FACTOR = 0.0125f;
    private static final float DASHED_LINE_ON_SCALE_FACTOR = 0.008f;
    private static final float VERTICAL_OFFSET_SCALING_FACTOR = 0.2f;
    private Paint dashedLinePaint;
    private Rect reuseableRect;

    public LinedEditText(Context context) {
        super(context);
        init();
    }

    public LinedEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public LinedEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        this.reuseableRect = new Rect();
        this.dashedLinePaint = new Paint();
        this.dashedLinePaint.setARGB(200, 0, 0, 0);
        this.dashedLinePaint.setStyle(Style.STROKE);
    }


    public void onDraw(Canvas canvas) {
        int height = getHeight();
        int lineHeight = getLineHeight();
        int i = (int) (((float) lineHeight) * VERTICAL_OFFSET_SCALING_FACTOR);
        int i2 = height / lineHeight;
        if (getLineCount() > i2) {
            i2 = getLineCount();
        }
        int lineBounds = getLineBounds(0, this.reuseableRect);
        for (int i3 = 0; i3 < i2; i3++) {
            float f = (float) (lineBounds + i);
            Canvas canvas2 = canvas;
            canvas2.drawLine((float) this.reuseableRect.left, f, (float) this.reuseableRect.right, f, this.dashedLinePaint);
            lineBounds += lineHeight;
        }
        super.onDraw(canvas);
    }
}
