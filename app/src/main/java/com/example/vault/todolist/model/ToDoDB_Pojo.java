package com.example.vault.todolist.model;

public class ToDoDB_Pojo {
    private String ToDoFileColor = "";
    private String ToDoFileCreatedDate;
    private boolean ToDoFileHasReminder = false;
    private int ToDoFileIsDecoy;
    private String ToDoFileLocation = "";
    private String ToDoFileModifiedDate;
    private String ToDoFileName = "";
    private double ToDoFileSize;
    private String ToDoFileTask1 = "";
    private boolean ToDoFileTask1IsChecked = false;
    private String ToDoFileTask2 = "";
    private boolean ToDoFileTask2IsChecked = false;
    private boolean ToDoFinished = false;
    private int ToDoId;

    public int getToDoId() {
        return this.ToDoId;
    }

    public void setToDoId(int i) {
        this.ToDoId = i;
    }

    public String getToDoFileName() {
        return this.ToDoFileName;
    }

    public void setToDoFileName(String str) {
        this.ToDoFileName = str;
    }

    public String getToDoFileLocation() {
        return this.ToDoFileLocation;
    }

    public void setToDoFileLocation(String str) {
        this.ToDoFileLocation = str;
    }

    public String getToDoFileCreatedDate() {
        return this.ToDoFileCreatedDate;
    }

    public void setToDoFileCreatedDate(String str) {
        this.ToDoFileCreatedDate = str;
    }

    public String getToDoFileModifiedDate() {
        return this.ToDoFileModifiedDate;
    }

    public void setToDoFileModifiedDate(String str) {
        this.ToDoFileModifiedDate = str;
    }

    public String getToDoFileColor() {
        return this.ToDoFileColor;
    }

    public void setToDoFileColor(String str) {
        this.ToDoFileColor = str;
    }

    public double getToDoFileSize() {
        return this.ToDoFileSize;
    }

    public void setToDoFileSize(double d) {
        this.ToDoFileSize = d;
    }

    public int getToDoFileIsDecoy() {
        return this.ToDoFileIsDecoy;
    }

    public void setToDoFileIsDecoy(int i) {
        this.ToDoFileIsDecoy = i;
    }

    public boolean isToDoFileHasReminder() {
        return this.ToDoFileHasReminder;
    }

    public void setToDoFileHasReminder(boolean z) {
        this.ToDoFileHasReminder = z;
    }

    public String getToDoFileTask1() {
        return this.ToDoFileTask1;
    }

    public void setToDoFileTask1(String str) {
        this.ToDoFileTask1 = str;
    }

    public String getToDoFileTask2() {
        return this.ToDoFileTask2;
    }

    public void setToDoFileTask2(String str) {
        this.ToDoFileTask2 = str;
    }

    public boolean isToDoFileTask1IsChecked() {
        return this.ToDoFileTask1IsChecked;
    }

    public void setToDoFileTask1IsChecked(boolean z) {
        this.ToDoFileTask1IsChecked = z;
    }

    public boolean isToDoFileTask2IsChecked() {
        return this.ToDoFileTask2IsChecked;
    }

    public void setToDoFileTask2IsChecked(boolean z) {
        this.ToDoFileTask2IsChecked = z;
    }

    public boolean isToDoFinished() {
        return this.ToDoFinished;
    }

    public void setToDoFinished(boolean z) {
        this.ToDoFinished = z;
    }
}
