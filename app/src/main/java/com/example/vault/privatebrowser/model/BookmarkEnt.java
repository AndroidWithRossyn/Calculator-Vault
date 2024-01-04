package com.example.vault.privatebrowser.model;

public class BookmarkEnt {
    private String _CreateDate;
    private int _Id;
    private String _Url;

    public void SetId(int i) {
        this._Id = i;
    }

    public void SetURL(String str) {
        this._Url = str;
    }

    public void SetCreateDate(String str) {
        this._CreateDate = str;
    }

    public int GetId() {
        return this._Id;
    }

    public String GetURL() {
        return this._Url;
    }

    public String GetCreateDate() {
        return this._CreateDate;
    }
}
