package com.example.vault.notes.model;

public class NotesFolderDB_Pojo {
    private String NotesFolderColor = "";
    private String NotesFolderCreatedDate;
    private int NotesFolderFilesPresent;
    private int NotesFolderFilesSortBy;
    private int NotesFolderFilesViewBy;
    private int NotesFolderId;
    private int NotesFolderIsDecoy;
    private String NotesFolderLocation;
    private String NotesFolderModifiedDate;
    private String NotesFolderName;

    public int getNotesFolderId() {
        return this.NotesFolderId;
    }

    public void setNotesFolderId(int i) {
        this.NotesFolderId = i;
    }

    public String getNotesFolderName() {
        return this.NotesFolderName;
    }

    public void setNotesFolderName(String str) {
        this.NotesFolderName = str;
    }

    public String getNotesFolderLocation() {
        return this.NotesFolderLocation;
    }

    public void setNotesFolderLocation(String str) {
        this.NotesFolderLocation = str;
    }

    public String getNotesFolderCreatedDate() {
        return this.NotesFolderCreatedDate;
    }

    public void setNotesFolderCreatedDate(String str) {
        this.NotesFolderCreatedDate = str;
    }

    public String getNotesFolderModifiedDate() {
        return this.NotesFolderModifiedDate;
    }

    public void setNotesFolderModifiedDate(String str) {
        this.NotesFolderModifiedDate = str;
    }

    public int getNotesFolderFilesPresent() {
        return this.NotesFolderFilesPresent;
    }

    public void setNotesFolderFilesPresent(int i) {
        this.NotesFolderFilesPresent = i;
    }

    public int getNotesFolderIsDecoy() {
        return this.NotesFolderIsDecoy;
    }

    public void setNotesFolderIsDecoy(int i) {
        this.NotesFolderIsDecoy = i;
    }

    public int getNotesFolderFilesSortBy() {
        return this.NotesFolderFilesSortBy;
    }

    public void setNotesFolderFilesSortBy(int i) {
        this.NotesFolderFilesSortBy = i;
    }

    public String getNotesFolderColor() {
        return this.NotesFolderColor;
    }

    public void setNotesFolderColor(String str) {
        this.NotesFolderColor = str;
    }

    public int getNotesFolderFilesViewBy() {
        return this.NotesFolderFilesViewBy;
    }

    public void setNotesFolderFilesViewBy(int i) {
        this.NotesFolderFilesViewBy = i;
    }
}
