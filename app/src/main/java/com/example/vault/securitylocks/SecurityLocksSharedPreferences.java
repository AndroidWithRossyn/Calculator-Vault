package com.example.vault.securitylocks;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.example.vault.securitylocks.SecurityLocksCommon.LoginOptions;

public class SecurityLocksSharedPreferences {
    private static String _decoyPassword = "DecoyPassword";
    private static String _email = "Email";
    private static String _fileName = "SecurityLock";
    private static String _isAppRated = "IsAppRated";
    private static String _isCameraOpenFromInApp = "IsCameraOpenFromInApp";
    private static String _isFirstLogin = "IsFirstLogin";
    private static String _isFingerprint = "IsFigerprint";
    private static String _isIconChanged = "IsIconChanged";
    private static String _lastUnInstalledPackageName = "LastUnInstalledPackageName";
    private static String _loginType = "LoginType";
    private static String _password = "Password";
    private static String _rateCount = "RateCount";
    private static String _rateCountForRateAndReview = "RateCountForRateAndReview";
    private static String _showFirstTimeEmailPopup = "ShowFirstTimeEmailPopup";
    private static String _showFirstTimeTutorial = "ShowFirstTimeTutorial";
    private static String _showTwoTimePopup = "_showTwoTimePopup";
    private static String checkCalMode = "checkCalMode";
    static Context context;
    static SharedPreferences myPrefs;
    private static SecurityLocksSharedPreferences securityCredentialsSharedPreferences;

    private SecurityLocksSharedPreferences() {
    }

    public static SecurityLocksSharedPreferences GetObject(Context context2) {
        if (securityCredentialsSharedPreferences == null) {
            securityCredentialsSharedPreferences = new SecurityLocksSharedPreferences();
        }
        context = context2;
        myPrefs = context.getSharedPreferences(_fileName, Context.MODE_MULTI_PROCESS);
        return securityCredentialsSharedPreferences;
    }

    public void SetShowFirstTimeTutorial(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_showFirstTimeTutorial, bool.booleanValue());
        edit.commit();
    }

    public boolean GetShowFirstTimeTutorial() {
        return myPrefs.getBoolean(_showFirstTimeTutorial, true);
    }

    public void SetShowFirstTimeEmailPopup(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_showFirstTimeEmailPopup, bool.booleanValue());
        edit.commit();
    }

    public boolean GetShowFirstTimeEmailPopup() {
        return myPrefs.getBoolean(_showFirstTimeEmailPopup, true);
    }

    public void SetIsFirstLogin(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_isFirstLogin, bool.booleanValue());
        edit.commit();
    }

    public boolean GetIsFirstLogin() {
        return myPrefs.getBoolean(_isFirstLogin, true);
    }

    //---

    public void SetFingerPrint(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_isFingerprint, bool.booleanValue());
        edit.commit();
    }

    public boolean GetFingerprint() {
        return myPrefs.getBoolean(_isFingerprint, false);
    }

    public void SetSecurityCredential(String str) {
        Editor edit = myPrefs.edit();
        edit.putString(_password, str);
        edit.commit();
    }

    public String GetSecurityCredential() {
        return myPrefs.getString(_password, "");
    }

    public String GetDefaultSecurityCredential() {
        return "0000";
    }

    public void SetDecoySecurityCredential(String str) {
        Editor edit = myPrefs.edit();
        edit.putString(_decoyPassword, str);
        edit.commit();
    }

    public String GetDecoySecurityCredential() {
        return myPrefs.getString(_decoyPassword, "");
    }

    public void SetLoginType(String str) {
        Editor edit = myPrefs.edit();
        edit.putString(_loginType, str);
        edit.commit();
    }

    public String GetLoginType() {
        return myPrefs.getString(_loginType, LoginOptions.None.toString());
    }

    public void SetEmail(String str) {
        Editor edit = myPrefs.edit();
        edit.putString(_email, str);
        edit.commit();
    }

    public String GetEmail() {
        return myPrefs.getString(_email, "");
    }

    public boolean IsPasswordCorrect(String str) {
        return str.equals(myPrefs.getString(_password, ""));
    }

    public void SetIsAppRated(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_isAppRated, bool.booleanValue());
        edit.commit();
    }

    public boolean GetIsAppRated() {
        return myPrefs.getBoolean(_isAppRated, false);
    }

    public int GetRateCount() {
        return myPrefs.getInt(_rateCount, 0);
    }

    public void SetRateCount(int i) {
        Editor edit = myPrefs.edit();
        edit.putInt(_rateCount, i);
        edit.commit();
    }

    public int GetRateCountForRateAndReview() {
        return myPrefs.getInt(_rateCountForRateAndReview, 0);
    }

    public void SetRateCountForRateAndReview(int i) {
        Editor edit = myPrefs.edit();
        edit.putInt(_rateCountForRateAndReview, i);
        edit.commit();
    }

    public void SetLastUnInstalledPackageName(String str) {
        Editor edit = myPrefs.edit();
        edit.putString(_lastUnInstalledPackageName, str);
        edit.commit();
    }

    public String GetLastUnInstalledPackageName() {
        return myPrefs.getString(_lastUnInstalledPackageName, "");
    }

    public boolean GetIsCameraOpenFromInApp() {
        return myPrefs.getBoolean(_isCameraOpenFromInApp, false);
    }

    public void SetIsCameraOpenFromInApp(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_isCameraOpenFromInApp, bool.booleanValue());
        edit.commit();
    }

    public void SetIconChanged(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(_isIconChanged, bool.booleanValue());
        edit.commit();
    }

    public boolean GetIconChanged() {
        return myPrefs.getBoolean(_isIconChanged, false);
    }

    public void isSetCalModeEnable(Boolean bool) {
        Editor edit = myPrefs.edit();
        edit.putBoolean(checkCalMode, bool.booleanValue());
        edit.commit();
    }

    public boolean isGetCalModeEnable() {
        return myPrefs.getBoolean(checkCalMode, false);
    }

    public int GetWhatNewDialog() {
        return myPrefs.getInt(_showTwoTimePopup, 0);
    }

    public void SetUpdatePrefWhatNewDialog(int i) {
        Editor edit = context.getSharedPreferences(_fileName, 0).edit();
        edit.putInt(_showTwoTimePopup, i);
        edit.commit();
    }

    public void RemoveDecoySecurityCredential() {
        Editor edit = myPrefs.edit();
        edit.remove(_decoyPassword);
        edit.commit();
    }
}
