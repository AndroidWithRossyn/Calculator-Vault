package com.example.vault.storageoption;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class StorageOptionSharedPreferences {
    private static String _fileName = "StorageOptionSettings";
    private static String _iSAppLockAlertDialogShow = "ISAppLockAlertDialogShow";
    private static String _iSDAlertshow = "ISDAlertshow";
    private static String _isStorageSDCard = "IsStorageSDCard";
    private static String _sdcardUri = "SDCardUri";
    private static String _storagePath = "StoragePath";
    static Context context;
    static SharedPreferences myPrefs;
    private static StorageOptionSharedPreferences storageOptionSharedPreferences;

    private StorageOptionSharedPreferences() {
    }

    public static StorageOptionSharedPreferences GetObject(Context context2) {
        if (storageOptionSharedPreferences == null) {
            storageOptionSharedPreferences = new StorageOptionSharedPreferences();
        }
        context = context2;
        myPrefs = context.getSharedPreferences(_fileName, 0);
        return storageOptionSharedPreferences;
    }

    public void SetIsStorageSDCard(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_isStorageSDCard, bool.booleanValue());
        edit.commit();
    }

    public boolean GetIsStorageSDCard() {
        return myPrefs.getBoolean(_isStorageSDCard, false);
    }

    public void SetStoragePath(String str) {
        Editor edit = myPrefs.edit();
        edit.putString(_storagePath, str);
        edit.commit();
    }

    public String GetStoragePath() {
        return myPrefs.getString(_storagePath, StorageOptionsCommon.STORAGEPATH_1);
    }

    public String GetFirstTimeStoragePath() {
        return myPrefs.getString(_storagePath, "");
    }

    public void SetSDCardUri(String str) {
        Editor edit = myPrefs.edit();
        edit.putString(_sdcardUri, str);
        edit.commit();
    }

    public String GetSDCardUri() {
        return myPrefs.getString(_sdcardUri, "");
    }

    public void SetISDAlertshow(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_iSDAlertshow, bool.booleanValue());
        edit.commit();
    }

    public boolean GetISDAlertshow() {
        return myPrefs.getBoolean(_iSDAlertshow, false);
    }

    public void SetISAppLockAlertDialogShow(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_iSAppLockAlertDialogShow, bool.booleanValue());
        edit.commit();
    }

    public boolean GetISAppLockAlertDialogShow() {
        return myPrefs.getBoolean(_iSAppLockAlertDialogShow, false);
    }
}
