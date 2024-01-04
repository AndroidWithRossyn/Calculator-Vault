package com.example.vault.audio.model;

import android.graphics.Bitmap;
import java.io.File;

public class AudioEnt {
    private int _ISDCard, _folderId, _id;
    private String _audioName, _folderLockAudioLocation, _modifiedDateTime, _originalAudioLocation;
    private File _file;
    private Bitmap _image;
    private Boolean _isCheck;


    public int getId() {
        return this._id;
    }

    public void setId(int i) {
        this._id = i;
    }

    public String getAudioName() {
        return this._audioName;
    }

    public void setAudioName(String str) {
        this._audioName = str;
    }

    public String getFolderLockAudioLocation() {
        return this._folderLockAudioLocation;
    }

    public void setFolderLockAudioLocation(String str) {
        this._folderLockAudioLocation = str;
    }

    public String getOriginalAudioLocation() {
        return this._originalAudioLocation;
    }

    public void setOriginalAudioLocation(String str) {
        this._originalAudioLocation = str;
    }

    public boolean GetFileCheck() {
        return this._isCheck.booleanValue();
    }

    public void SetFileCheck(boolean z) {
        this._isCheck = Boolean.valueOf(z);
    }

    public File GetFile() {
        return this._file;
    }

    public void SetFile(File file) {
        this._file = file;
    }

    public int getPlayListId() {
        return this._folderId;
    }

    public void setPlayListId(int i) {
        this._folderId = i;
    }

    public Bitmap GetFileImage() {
        return this._image;
    }

    public void SetFileImage(Bitmap bitmap) {
        this._image = bitmap;
    }

    public int getISDCard() {
        return this._ISDCard;
    }

    public void setISDCard(int i) {
        this._ISDCard = i;
    }

    public String get_modifiedDateTime() {
        return this._modifiedDateTime;
    }

    public void set_modifiedDateTime(String str) {
        this._modifiedDateTime = str;
    }
}
