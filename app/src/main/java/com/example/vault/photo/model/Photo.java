package com.example.vault.photo.model;

import android.graphics.Bitmap;

public class Photo {
    private String _DateTime;
    private int _ISDCard;
    private String _Photo_Thumbnil_Location;
    private int _albumId;
    private String _folderLockPhotoLocation;
    private int _id;
    private Boolean _isCheck;
    private String _modifiedDateTime;
    private String _originalPhotoLocation;
    private String _photoName;
    private Bitmap _thumbImage;

    public int getId() {
        return this._id;
    }

    public void setId(int i) {
        this._id = i;
    }

    public String getPhotoName() {
        return this._photoName;
    }

    public void setPhotoName(String str) {
        this._photoName = str;
    }

    public String getFolderLockPhotoLocation() {
        return this._folderLockPhotoLocation;
    }

    public void setFolderLockPhotoLocation(String str) {
        this._folderLockPhotoLocation = str;
    }

    public String getOriginalPhotoLocation() {
        return this._originalPhotoLocation;
    }

    public void setOriginalPhotoLocation(String str) {
        this._originalPhotoLocation = str;
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

    public String getPhoto_Thumbnil_Location() {
        return this._Photo_Thumbnil_Location;
    }

    public void setPhoto_Thumbnil_Location(String str) {
        this._Photo_Thumbnil_Location = str;
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
