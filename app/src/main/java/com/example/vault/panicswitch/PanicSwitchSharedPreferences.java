package com.example.vault.panicswitch;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.example.vault.panicswitch.PanicSwitchCommon.SwitchApp;

public class PanicSwitchSharedPreferences {
    private static String _fileName = "PanicSwitchSettings";
    private static String _isFlickOn = "isFlickOn";
    private static String _isPalmOnScreenOn = "isPalmOnScreenOn";
    private static String _isShakeOn = "isShakeOn";
    private static String _switchApp = "SwitchApp";
    static Context context;
    static SharedPreferences myPrefs;
    private static PanicSwitchSharedPreferences panicSwitchSharedPreferences;

    private PanicSwitchSharedPreferences() {
    }

    public static PanicSwitchSharedPreferences GetObject(Context context2) {
        if (panicSwitchSharedPreferences == null) {
            panicSwitchSharedPreferences = new PanicSwitchSharedPreferences();
        }
        context = context2;
        myPrefs = context.getSharedPreferences(_fileName, 0);
        return panicSwitchSharedPreferences;
    }

    public void SetIsFlickOn(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_isFlickOn, bool.booleanValue());
        edit.commit();
    }

    public boolean GetIsFlickOn() {
        return myPrefs.getBoolean(_isFlickOn, false);
    }

    public void SetIsShakeOn(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_isShakeOn, bool.booleanValue());
        edit.commit();
    }

    public boolean GetIsShakeOn() {
        return myPrefs.getBoolean(_isShakeOn, false);
    }

    public void SetIsPalmOnScreenOn(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_isPalmOnScreenOn, bool.booleanValue());
        edit.commit();
    }

    public boolean GetIsPalmOnScreenOn() {
        return myPrefs.getBoolean(_isPalmOnScreenOn, false);
    }

    public void SetSwitchApp(String str) {
        Editor edit = myPrefs.edit();
        edit.putString(_switchApp, str);
        edit.commit();
    }

    public String GetSwitchApp() {
        return myPrefs.getString(_switchApp, SwitchApp.HomeScreen.toString());
    }
}
