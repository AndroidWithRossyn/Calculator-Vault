package com.example.vault.securitylocks;

public class SecurityLocksEnt {
    private int _LoginOption;
    private int _drawable;
    private boolean _isCheck;

    public void SetLoginOption(int i) {
        this._LoginOption = i;
    }

    public int GetLoginOption() {
        return this._LoginOption;
    }

    public boolean GetisCheck() {
        return this._isCheck;
    }

    public void SetisCheck(boolean z) {
        this._isCheck = z;
    }

    public void SetDrawable(int i) {
        this._drawable = i;
    }

    public int GetDrawable() {
        return this._drawable;
    }
}
