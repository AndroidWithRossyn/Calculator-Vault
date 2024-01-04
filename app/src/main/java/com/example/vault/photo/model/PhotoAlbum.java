package com.example.vault.photo.model;

import android.graphics.Bitmap;

public class PhotoAlbum {
    private String _albumCoverLocation;
    private String _albumLocation;
    private String _albumName;
    private int _id;
    private String _modifiedDateTime;
    private Bitmap _photoBitmap;
    private int _photoCount;

    public int getId() {
        return this._id;
    }

    public void setId(int i) {
        this._id = i;
    }

    public String getAlbumName() {
        return this._albumName;
    }

    public void setAlbumName(String str) {
        this._albumName = str;
    }

    public String getAlbumLocation() {
        return this._albumLocation;
    }

    public void setAlbumLocation(String str) {
        this._albumLocation = str;
    }

    public String getAlbumCoverLocation() {
        return this._albumCoverLocation;
    }

    public void setAlbumCoverLocation(String str) {
        this._albumCoverLocation = str;
    }

    public int getPhotoCount() {
        return this._photoCount;
    }

    public void setPhotoCount(int i) {
        this._photoCount = i;
    }

    public Bitmap getPhotoBitmap() {
        return this._photoBitmap;
    }

    public void setPhotoBitmap(Bitmap bitmap) {
        this._photoBitmap = bitmap;
    }

    public String get_modifiedDateTime() {
        return this._modifiedDateTime;
    }

    public void set_modifiedDateTime(String str) {
        this._modifiedDateTime = str;
    }
}
