package com.example.vault.photo.model;

public class GridItem {
    private String Day;
    private String Month;
    private String Year;
    private boolean isVideo;
    private String path;
    private int section;
    private String time;
    private String videoThumnailPath;

    public GridItem(String str, String str2, boolean z, String str3) {
        this.path = str;
        this.time = str2;
        this.isVideo = z;
        this.videoThumnailPath = str3;
        String[] split = str2.split("-");
        this.Year = split[0];
        this.Month = split[1];
        this.Day = split[2];
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String str) {
        this.path = str;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String str) {
        this.time = str;
    }

    public int getSection() {
        return this.section;
    }

    public void setSection(int i) {
        this.section = i;
    }

    public String getMonth() {
        return this.Month;
    }

    public void setMonth(String str) {
        this.Month = str;
    }

    public String getDay() {
        return this.Day;
    }

    public void setDay(String str) {
        this.Day = str;
    }

    public String getYear() {
        return this.Year;
    }

    public void setYear(String str) {
        this.Year = str;
    }

    public boolean getIsVideo() {
        return this.isVideo;
    }

    public void setIsVideo(boolean z) {
        this.isVideo = z;
    }

    public String getVideoThumnailPath() {
        return this.videoThumnailPath;
    }

    public void setVideoThumnailPath(String str) {
        this.videoThumnailPath = str;
    }
}
