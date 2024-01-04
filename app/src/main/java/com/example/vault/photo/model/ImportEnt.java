package com.example.vault.photo.model;

import android.graphics.Bitmap;

public class ImportEnt {
    private String _arrPath;
    private int _id;
    private Boolean _ischeck;
    private Bitmap _thumbnails;
    private Boolean _thumbnailsselection;

    public void SetId(int i) {
        this._id = i;
    }

    public void SetPath(String str) {
        this._arrPath = str;
    }

    public void SetThumbnail(Bitmap bitmap) {
        this._thumbnails = bitmap;
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

    public Bitmap GetThumbnail() {
        return this._thumbnails;
    }

    public Boolean GetThumbnailSelection() {
        return this._thumbnailsselection;
    }
}
