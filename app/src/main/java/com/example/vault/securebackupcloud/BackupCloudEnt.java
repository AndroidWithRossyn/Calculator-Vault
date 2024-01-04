package com.example.vault.securebackupcloud;

import com.dropbox.core.v2.files.Metadata;
import java.util.ArrayList;
import java.util.Hashtable;

public class BackupCloudEnt {
    private boolean _downloadCompleteStatus = false;
    private int _downloadCount;
    private Hashtable<String, Boolean> _downloadpath;
    private int _dropboxType;
    private ArrayList<String> _fileNames;
    private String _folderName;
    private String _id;
    private int _imageStatus;
    private boolean _isInProgress = false;
    private Metadata _metadata;
    private String _mimeType;
    private String _path;
    private String _root;
    private String _size;
    private int _status;
    private int _syncVisibility = 0;
    private boolean _uploadCompleteStatus = false;
    private int _uploadCount;
    private Hashtable<String, Boolean> _uploadpath;

    public String GetId() {
        return this._id;
    }

    public void SetId(String str) {
        this._id = str;
    }

    public Metadata GetMetadata() {
        return this._metadata;
    }

    public void SetMetadata(Metadata metadata) {
        this._metadata = metadata;
    }

    public String GetPath() {
        return this._path;
    }

    public void SetPath(String str) {
        this._path = str;
    }

    public String GetMimeType() {
        return this._mimeType;
    }

    public void SetMimeType(String str) {
        this._mimeType = str;
    }

    public String GetRoot() {
        return this._root;
    }

    public void SetRoot(String str) {
        this._root = str;
    }

    public String GetSize() {
        return this._size;
    }

    public void SetSize(String str) {
        this._size = str;
    }

    public String GetFolderName() {
        return this._folderName;
    }

    public void SetFolderName(String str) {
        this._folderName = str;
    }

    public ArrayList<String> GetFileNames() {
        return this._fileNames;
    }

    public void SetFileNames(ArrayList<String> arrayList) {
        this._fileNames = arrayList;
    }

    public int GetDownloadCount() {
        return this._downloadCount;
    }

    public void SetDownloadCount(int i) {
        this._downloadCount = i;
    }

    public int GetUploadCount() {
        return this._uploadCount;
    }

    public void SetUploadCount(int i) {
        this._uploadCount = i;
    }

    public Hashtable<String, Boolean> GetUploadPath() {
        return this._uploadpath;
    }

    public void SetUploadPath(Hashtable<String, Boolean> hashtable) {
        this._uploadpath = hashtable;
    }

    public Hashtable<String, Boolean> GetDownloadPath() {
        return this._downloadpath;
    }

    public void SetDownloadPath(Hashtable<String, Boolean> hashtable) {
        this._downloadpath = hashtable;
    }

    public int GetStatus() {
        return this._status;
    }

    public void SetStatus(int i) {
        this._status = i;
    }

    public int GetDropboxType() {
        return this._dropboxType;
    }

    public void SetDropboxType(int i) {
        this._dropboxType = i;
    }

    public boolean GetDownloadCompleteStatus() {
        return this._downloadCompleteStatus;
    }

    public void SetDownloadCompleteStatus(boolean z) {
        this._downloadCompleteStatus = z;
    }

    public boolean GetUploadCompleteStatus() {
        return this._uploadCompleteStatus;
    }

    public void SetUploadCompleteStatus(boolean z) {
        this._uploadCompleteStatus = z;
    }

    public boolean GetIsInProgress() {
        return this._isInProgress;
    }

    public void SetIsInProgress(boolean z) {
        this._isInProgress = z;
    }

    public int GetImageStatus() {
        return this._imageStatus;
    }

    public void SetImageStatus(int i) {
        this._imageStatus = i;
    }

    public int GetSyncVisibility() {
        return this._syncVisibility;
    }

    public void SetSyncVisibility(int i) {
        this._syncVisibility = i;
    }
}
