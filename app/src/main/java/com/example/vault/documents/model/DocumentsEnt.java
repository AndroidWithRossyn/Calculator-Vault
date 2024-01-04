package com.example.vault.documents.model;

import java.io.File;

public class DocumentsEnt {
    private int _ISDCard;
    private String _documentName;
    private File _file;
    private int _folderId;
    private String _folderLockDocumentLocation;
    private int _id;
    private Boolean _isCheck;
    private String _modifiedDateTime;
    private String _originalDocumentLocation;

    public int getId() {
        return this._id;
    }

    public void setId(int i) {
        this._id = i;
    }

    public String getDocumentName() {
        return this._documentName;
    }

    public void setDocumentName(String str) {
        this._documentName = str;
    }

    public String getFolderLockDocumentLocation() {
        return this._folderLockDocumentLocation;
    }

    public void setFolderLockDocumentLocation(String str) {
        this._folderLockDocumentLocation = str;
    }

    public String getOriginalDocumentLocation() {
        return this._originalDocumentLocation;
    }

    public void setOriginalDocumentLocation(String str) {
        this._originalDocumentLocation = str;
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

    public int getFolderId() {
        return this._folderId;
    }

    public void setFolderId(int i) {
        this._folderId = i;
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
