package com.example.vault.notes.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

public class UIElementsHelper {

    public static Drawable getGeneralActionBarBackground(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context);
        return new ColorDrawable(-14142061);
    }
}
