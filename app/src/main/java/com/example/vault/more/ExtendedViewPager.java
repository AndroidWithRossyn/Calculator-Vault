package com.example.vault.more;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ExtendedViewPager extends ViewPager {
    public ExtendedViewPager(Context context) {
        super(context);
    }

    public ExtendedViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }


    public boolean canScroll(View view, boolean z, int i, int i2, int i3) {
        if (view instanceof ImageView) {
            return ((ImageView) view).canScrollHorizontally(-i);
        }
        return super.canScroll(view, z, i, i2, i3);
    }
}
