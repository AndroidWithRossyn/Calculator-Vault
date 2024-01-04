package com.example.vault.documents.model;

public class ImportAlbumEnt {
    private String _activity_type;
    private String _albumName;
    private String _arrPath;
    private int _id;
    private boolean _isCheck;

    public void SetId(int i) {
        this._id = i;
    }

    public int GetId() {
        return this._id;
    }

    public String Get_Activity_type() {
        return this._activity_type;
    }

    public void Set_Activity_type(String str) {
        this._activity_type = str;
    }

    public void SetPath(String str) {
        this._arrPath = str;
    }

    public String GetPath() {
        return this._arrPath;
    }

    public void SetAlbumName(String str) {
        this._albumName = str;
    }

    public String GetAlbumName() {
        return this._albumName;
    }

    public boolean GetAlbumFileCheck() {
        return this._isCheck;
    }

    public void SetAlbumFileCheck(boolean z) {
        this._isCheck = z;
    }
}
