package com.example.vault.gallery;

public class GalleryEnt {
    private String _DateTime;
    private int _albumId;
    private String _folderLockgalleryfileLocation;
    private String _galleryfileName;
    private int _id;
    private Boolean _isCheck;
    private Boolean _isVideo;
    private String _modifiedDateTime;
    private String _originalgalleryfileLocation;
    private String _thumbnail_video_location;

    public int get_id() {
        return this._id;
    }

    public void set_id(int i) {
        this._id = i;
    }

    public String get_galleryfileName() {
        return this._galleryfileName;
    }

    public void set_galleryfileName(String str) {
        this._galleryfileName = str;
    }

    public String get_folderLockgalleryfileLocation() {
        return this._folderLockgalleryfileLocation;
    }

    public void set_folderLockgalleryfileLocation(String str) {
        this._folderLockgalleryfileLocation = str;
    }

    public String get_originalgalleryfileLocation() {
        return this._originalgalleryfileLocation;
    }

    public void set_originalgalleryfileLocation(String str) {
        this._originalgalleryfileLocation = str;
    }

    public int get_albumId() {
        return this._albumId;
    }

    public void set_albumId(int i) {
        this._albumId = i;
    }

    public Boolean get_isCheck() {
        return this._isCheck;
    }

    public void set_isCheck(Boolean bool) {
        this._isCheck = bool;
    }

    public Boolean get_isVideo() {
        return this._isVideo;
    }

    public void set_isVideo(Boolean bool) {
        this._isVideo = bool;
    }

    public String get_thumbnail_video_location() {
        return this._thumbnail_video_location;
    }

    public void set_thumbnail_video_location(String str) {
        this._thumbnail_video_location = str;
    }

    public String get_DateTime() {
        return this._DateTime;
    }

    public void set_DateTime(String str) {
        this._DateTime = str;
    }

    public String get_modifiedDateTime() {
        return this._modifiedDateTime;
    }

    public void set_modifiedDateTime(String str) {
        this._modifiedDateTime = str;
    }
}
