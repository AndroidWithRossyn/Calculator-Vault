package com.example.vault.tryattempt.model;

public class TryAttemptEntity {
    private String _LoginOption;
    private String _tryAttemptTime;
    private String _imagePath;
    private boolean _isCheck;
    private String _wrongPassword;

    public String GetLoginOption() {
        return this._LoginOption;
    }

    public void SetLoginOption(String str) {
        this._LoginOption = str;
    }

    public String GetWrongPassword() {
        return this._wrongPassword;
    }

    public void SetWrongPassword(String str) {
        this._wrongPassword = str;
    }

    public String GetImagePath() {
        return this._imagePath;
    }

    public void SetImagePath(String str) {
        this._imagePath = str;
    }

    public String GetTryAttemptTime() {
        return this._tryAttemptTime;
    }

    public void SetTryAttemptTime(String str) {
        this._tryAttemptTime = str;
    }

    public boolean GetIsCheck() {
        return this._isCheck;
    }

    public void SetIsCheck(Boolean bool) {
        this._isCheck = bool.booleanValue();
    }
}
