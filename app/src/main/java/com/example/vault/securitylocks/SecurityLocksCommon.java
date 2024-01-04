package com.example.vault.securitylocks;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;


public class SecurityLocksCommon {
    public static String AppName = "/Calculator Vault Try Attempts/";
    public static Activity CurrentActivity = null;
    public static String TryAttempts = "/TryAttempts/";
    public static boolean IsAppDeactive = false;
    public static boolean IsCancel = false;
    public static boolean IsConfirmPatternActivity = false;
    public static int IsFakeAccount = 0;
    public static boolean IsFirstLogin = false;
    public static boolean IsPreviewStarted = false;
    public static boolean IsRateReview = false;
    public static boolean IsSiginPattern = false;
    public static boolean IsSiginPatternConfirm = false;
    public static boolean IsSiginPatternContinue = false;
    public static boolean IsStealthModeOn = false;
    public static boolean Isfreshlogin = false;
    public static boolean IsnewloginforAd = false;
    public static String PatternPassword = "";
    public static final String ServerAddress = "https://inoxsolution.website/AforAndroid/recovery.php";
    public static String StoragePath;
    public static boolean isBackupPasswordPin = false;
    public static boolean isBackupPattern = false;
    public static boolean isSettingDecoy = false;
    public static List<Point> mSiginPattern = new ArrayList();
    public static List<Point> mSiginPatternConfirm = new ArrayList();
    public static boolean showDialogWhatsNew = false;

    public enum LoginOptions {
        None,
        Password,
        Pattern,
        Pin,
        Calculator
    }

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath());
        sb.append("/data");
        sb.append(AppName);
        StoragePath = sb.toString();
    }
}
