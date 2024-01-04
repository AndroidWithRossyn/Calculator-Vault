package com.example.vault.documents.model;

public class DocumentFolder {
    private int _fileCount;
    private String _folderLocation;
    private String _folderName;
    private int _id;
    private String _modifiedDateTime;

    public int getId() {
        return this._id;
    }

    public void setId(int i) {
        this._id = i;
    }

    public String getFolderName() {
        return this._folderName;
    }

    public void setFolderName(String str) {
        this._folderName = str;
    }

    public String getFolderLocation() {
        return this._folderLocation;
    }

    public void setFolderLocation(String str) {
        this._folderLocation = str;
    }

    public int get_fileCount() {
        return this._fileCount;
    }

    public void set_fileCount(int i) {
        this._fileCount = i;
    }

    public String get_modifiedDateTime() {
        return this._modifiedDateTime;
    }

    public void set_modifiedDateTime(String str) {
        this._modifiedDateTime = str;
    }
}
