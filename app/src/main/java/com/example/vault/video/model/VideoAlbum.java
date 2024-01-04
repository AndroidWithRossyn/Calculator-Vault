package com.example.vault.video.model;

public class VideoAlbum {
    private String _albumCoverLocation;
    private String _albumLocation;
    private String _albumName;
    private int _id;
    private String _modifiedDateTime;
    private int _videoCount;

    public int getId() {
        return this._id;
    }

    public void setId(int i) {
        this._id = i;
    }

    public String getAlbumName() {
        return this._albumName;
    }

    public void setAlbumName(String str) {
        this._albumName = str;
    }

    public String getAlbumLocation() {
        return this._albumLocation;
    }

    public void setAlbumLocation(String str) {
        this._albumLocation = str;
    }

    public String getAlbumCoverLocation() {
        return this._albumCoverLocation;
    }

    public void setAlbumCoverLocation(String str) {
        this._albumCoverLocation = str;
    }

    public int getVideoCount() {
        return this._videoCount;
    }

    public void setVideoCount(int i) {
        this._videoCount = i;
    }

    public String get_modifiedDateTime() {
        return this._modifiedDateTime;
    }

    public void set_modifiedDateTime(String str) {
        this._modifiedDateTime = str;
    }
}
