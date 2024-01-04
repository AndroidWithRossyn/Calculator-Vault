package com.example.vault.utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class CeraProBoldTextView extends AppCompatTextView {

    /*
     * Caches typefaces based on their file path and name, so that they don't have to be created every time when they are referenced.
     */
    private static Typeface mTypeface;

    public CeraProBoldTextView(final Context context) {
        this(context, null);
    }

    public CeraProBoldTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CeraProBoldTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "Cera Pro Bold.otf");
        }
        setTypeface(mTypeface);
    }

}
