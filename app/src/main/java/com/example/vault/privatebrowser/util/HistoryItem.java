package com.example.vault.privatebrowser.util;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;

public class HistoryItem implements Comparable<HistoryItem> {
    private Bitmap mBitmap = null;
    private String mFolder = "";
    private int mId = 0;
    private int mImageId = 0;
    private int mOrder = 0;
    private String mTitle = "";
    private String mUrl = "";

    public HistoryItem() {
    }

    public HistoryItem(int i, String str, String str2) {
        this.mId = i;
        this.mUrl = str;
        this.mTitle = str2;
        this.mBitmap = null;
    }

    public HistoryItem(String str, String str2) {
        this.mUrl = str;
        this.mTitle = str2;
        this.mBitmap = null;
    }

    public HistoryItem(String str, String str2, int i) {
        this.mUrl = str;
        this.mTitle = str2;
        this.mBitmap = null;
        this.mImageId = i;
    }

    public int getId() {
        return this.mId;
    }

    public int getImageId() {
        return this.mImageId;
    }

    public void setID(int i) {
        this.mId = i;
    }

    public void setImageId(int i) {
        this.mImageId = i;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public void setFolder(String str) {
        if (str == null) {
            str = "";
        }
        this.mFolder = str;
    }

    public void setOrder(int i) {
        this.mOrder = i;
    }

    public int getOrder() {
        return this.mOrder;
    }

    public String getFolder() {
        return this.mFolder;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public void setUrl(String str) {
        if (str == null) {
            str = "";
        }
        this.mUrl = str;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String str) {
        if (str == null) {
            str = "";
        }
        this.mTitle = str;
    }

    public String toString() {
        return this.mTitle;
    }

    public int compareTo(@NonNull HistoryItem historyItem) {
        return this.mTitle.compareTo(historyItem.mTitle);
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HistoryItem historyItem = (HistoryItem) obj;
        if (this.mId != historyItem.mId || this.mImageId != historyItem.mImageId) {
            return false;
        }
        Bitmap bitmap = this.mBitmap;
        if (bitmap == null ? historyItem.mBitmap != null : !bitmap.equals(historyItem.mBitmap)) {
            return false;
        }
        if (!this.mTitle.equals(historyItem.mTitle) || !this.mUrl.equals(historyItem.mUrl)) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int hashCode = ((((this.mId * 31) + this.mUrl.hashCode()) * 31) + this.mTitle.hashCode()) * 31;
        Bitmap bitmap = this.mBitmap;
        return ((hashCode + (bitmap != null ? bitmap.hashCode() : 0)) * 31) + this.mImageId;
    }
}
