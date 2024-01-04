package com.example.vault.documents.model;

public class ImportEnt {
    private String _arrPath;
    private int _id;
    private Boolean _ischeck;
    private Boolean _thumbnailsselection;

    public void SetId(int i) {
        this._id = i;
    }

    public void SetPath(String str) {
        this._arrPath = str;
    }

    public void SetThumbnailSelection(Boolean bool) {
        this._thumbnailsselection = bool;
    }

    public int GetId() {
        return this._id;
    }

    public String GetPath() {
        return this._arrPath;
    }

    public Boolean GetThumbnailSelection() {
        return this._thumbnailsselection;
    }
}
