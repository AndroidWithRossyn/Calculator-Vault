package com.example.vault.privatebrowser.model;

public class DownloadFileEnt {
    private String _downloadFileUrl;
    private int _downloadType;
    private String _fileDownloadPath;
    private String _fileName;
    private int _id;
    private String _referenceId;
    private int _status;

    public void SetId(int i) {
        this._id = i;
    }

    public void SetFileName(String str) {
        this._fileName = str;
    }

    public void SetFileDownloadPath(String str) {
        this._fileDownloadPath = str;
    }

    public void SetReferenceId(String str) {
        this._referenceId = str;
    }

    public void SetDownloadFileUrl(String str) {
        this._downloadFileUrl = str;
    }

    public void SetStatus(int i) {
        this._status = i;
    }

    public void SetDownloadType(int i) {
        this._downloadType = i;
    }

    public int GetId() {
        return this._id;
    }

    public String GetFileName() {
        return this._fileName;
    }

    public String GetFileDownloadPath() {
        return this._fileDownloadPath;
    }

    public String GetReferenceId() {
        return this._referenceId;
    }

    public String GetDownloadFileUrl() {
        return this._downloadFileUrl;
    }

    public int GetStatus() {
        return this._status;
    }

    public int GetDownloadType() {
        return this._downloadType;
    }
}
