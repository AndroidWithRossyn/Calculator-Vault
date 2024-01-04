package com.example.vault.notes.model;

public class NotesFileDB_Pojo {
    private String NotesFileCreatedDate;
    private int NotesFileFolderId;
    private int NotesFileFromCloud;
    private int NotesFileId;
    private int NotesFileIsDecoy;
    private String NotesFileLocation;
    private String NotesFileModifiedDate;
    private String NotesFileName;
    private double NotesFileSize;
    private String NotesFileText;
    private String NotesfileColor = "#33b5e5";

    public int getNotesFileIsDecoy() {
        return this.NotesFileIsDecoy;
    }

    public void setNotesFileIsDecoy(int i) {
        this.NotesFileIsDecoy = i;
    }

    public int getNotesFileId() {
        return this.NotesFileId;
    }

    public void setNotesFileId(int i) {
        this.NotesFileId = i;
    }

    public int getNotesFileFolderId() {
        return this.NotesFileFolderId;
    }

    public void setNotesFileFolderId(int i) {
        this.NotesFileFolderId = i;
    }

    public String getNotesFileName() {
        return this.NotesFileName;
    }

    public void setNotesFileName(String str) {
        this.NotesFileName = str;
    }

    public String getNotesFileLocation() {
        return this.NotesFileLocation;
    }

    public void setNotesFileLocation(String str) {
        this.NotesFileLocation = str;
    }

    public String getNotesFileCreatedDate() {
        return this.NotesFileCreatedDate;
    }

    public void setNotesFileCreatedDate(String str) {
        this.NotesFileCreatedDate = str;
    }

    public String getNotesFileModifiedDate() {
        return this.NotesFileModifiedDate;
    }

    public void setNotesFileModifiedDate(String str) {
        this.NotesFileModifiedDate = str;
    }

    public double getNotesFileSize() {
        return this.NotesFileSize;
    }

    public void setNotesFileSize(double d) {
        this.NotesFileSize = d;
    }

    public String getNotesFileText() {
        return this.NotesFileText;
    }

    public void setNotesFileText(String str) {
        this.NotesFileText = str;
    }

    public int getNotesFileFromCloud() {
        return this.NotesFileFromCloud;
    }

    public void setNotesFileFromCloud(int i) {
        this.NotesFileFromCloud = i;
    }

    public String getNotesfileColor() {
        return this.NotesfileColor;
    }

    public void setNotesfileColor(String str) {
        this.NotesfileColor = str;
    }
}
