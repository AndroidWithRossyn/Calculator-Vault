package com.example.vault.calculator;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class OnAppStart extends Application {
    public void onCreate() {
        super.onCreate();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (defaultSharedPreferences.getInt("launch_count", 5) > 0) {
            Editor edit = defaultSharedPreferences.edit();
            edit.putInt("launch_count", defaultSharedPreferences.getInt("launch_count", 5) - 1);
            edit.apply();
        }
    }
}
