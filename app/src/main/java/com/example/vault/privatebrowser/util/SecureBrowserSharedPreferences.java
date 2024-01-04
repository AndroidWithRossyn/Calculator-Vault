package com.example.vault.privatebrowser.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SecureBrowserSharedPreferences {
    private static String _clearCache = "clearCache";
    private static String _clearCookies = "clearCookies";
    private static String _clearFormData = "clearFormData";
    private static String _clearHistory = "clearHistory";
    private static String _fileName = "SecureBrowser";
    static Context context;
    static SharedPreferences myPrefs;
    private static SecureBrowserSharedPreferences secureBrowserSharedPreferences;

    public static SecureBrowserSharedPreferences GetObject(Context context2) {
        if (secureBrowserSharedPreferences == null) {
            secureBrowserSharedPreferences = new SecureBrowserSharedPreferences();
        }
        context = context2;
        myPrefs = context.getSharedPreferences(_fileName, 0);
        return secureBrowserSharedPreferences;
    }

    public void setClearCache(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_clearCache, bool.booleanValue());
        edit.commit();
    }

    public Boolean getClearCache() {
        return Boolean.valueOf(myPrefs.getBoolean(_clearCache, false));
    }

    public void setClearHistory(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_clearHistory, bool.booleanValue());
        edit.commit();
    }

    public Boolean getClearHistory() {
        return Boolean.valueOf(myPrefs.getBoolean(_clearHistory, false));
    }

    public void setClearCookies(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_clearCookies, bool.booleanValue());
        edit.commit();
    }

    public Boolean getClearCookies() {
        return Boolean.valueOf(myPrefs.getBoolean(_clearCookies, false));
    }

    public void setSaveFormData(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_clearFormData, bool.booleanValue());
        edit.commit();
    }

    public Boolean getSaveFormData() {
        return Boolean.valueOf(myPrefs.getBoolean(_clearFormData, false));
    }
}
