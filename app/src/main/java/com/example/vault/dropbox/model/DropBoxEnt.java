package com.example.vault.dropbox.model;

import java.io.File;

public class DropBoxEnt {
    private File _file;
    private String _fileName;
    private int _id;
    private Boolean _isCheck;

    public int getId() {
        return this._id;
    }

    public void setId(int i) {
        this._id = i;
    }

    public String getFileName() {
        return this._fileName;
    }

    public void setFileName(String str) {
        this._fileName = str;
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
}
