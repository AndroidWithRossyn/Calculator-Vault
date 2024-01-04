package com.example.vault.video.model;

import android.graphics.Bitmap;

public class Video {
    private String _DateTime;
    private int _ISDCard;
    private int _albumId;
    private String _folderLockVideoLocation;
    private int _id;
    private Boolean _isCheck;
    private String _modifiedDateTime;
    private String _originalVideoLocation;
    private Bitmap _thumbImage;
    private String _thumbnail_video_location;
    private String _videoName;

    public int getId() {
        return this._id;
    }

    public void setId(int i) {
        this._id = i;
    }

    public String getVideoName() {
        return this._videoName;
    }

    public void setVideoName(String str) {
        this._videoName = str;
    }

    public String getFolderLockVideoLocation() {
        return this._folderLockVideoLocation;
    }

    public void setFolderLockVideoLocation(String str) {
        this._folderLockVideoLocation = str;
    }

    public String getOriginalVideoLocation() {
        return this._originalVideoLocation;
    }

    public void setOriginalVideoLocation(String str) {
        this._originalVideoLocation = str;
    }

    public String getthumbnail_video_location() {
        return this._thumbnail_video_location;
    }

    public void setthumbnail_video_location(String str) {
        this._thumbnail_video_location = str;
    }

    public int getAlbumId() {
        return this._albumId;
    }

    public void setAlbumId(int i) {
        this._albumId = i;
    }

    public boolean GetFileCheck() {
        return this._isCheck.booleanValue();
    }

    public void SetFileCheck(boolean z) {
        this._isCheck = Boolean.valueOf(z);
    }

    public Bitmap GetThumbImage() {
        return this._thumbImage;
    }

    public void SetThumbImage(Bitmap bitmap) {
        this._thumbImage = bitmap;
    }

    public int getISDCard() {
        return this._ISDCard;
    }

    public void setISDCard(int i) {
        this._ISDCard = i;
    }

    public String getDateTime() {
        return this._DateTime;
    }

    public void setDateTime(String str) {
        this._DateTime = str;
    }

    public String get_modifiedDateTime() {
        return this._modifiedDateTime;
    }

    public void set_modifiedDateTime(String str) {
        this._modifiedDateTime = str;
    }
}
