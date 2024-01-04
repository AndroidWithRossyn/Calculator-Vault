package com.example.vault.utilities;

public class Globals {
    private String _albumName;
    private boolean[] _checkedArray;

    public String GetAlbumName() {
        return this._albumName;
    }

    public void SetAlbumName(String str) {
        this._albumName = str;
    }

    public boolean[] getCheckdedArray() {
        return this._checkedArray;
    }

    public void setCheckdedArray(boolean[] zArr) {
        this._checkedArray = zArr;
        System.out.println(this._checkedArray);
    }
}
