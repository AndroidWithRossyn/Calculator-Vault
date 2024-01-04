package com.example.vault.wallet.model;

public class WalletEntryFileDB_Pojo {
    int EntriesFileSortBy;
    int categoriesFileIconIndex;
    int categoryId;
    String entryFileCreatedDate;
    int entryFileId;
    int entryFileIsDecoy;
    String entryFileLocation;
    String entryFileModifiedDate;
    String entryFileName;

    public int getEntryFileIsDecoy() {
        return this.entryFileIsDecoy;
    }

    public void setEntryFileIsDecoy(int i) {
        this.entryFileIsDecoy = i;
    }

    public int getEntryFileId() {
        return this.entryFileId;
    }

    public void setEntryFileId(int i) {
        this.entryFileId = i;
    }

    public int getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(int i) {
        this.categoryId = i;
    }

    public String getEntryFileName() {
        return this.entryFileName;
    }

    public void setEntryFileName(String str) {
        this.entryFileName = str;
    }

    public String getEntryFileLocation() {
        return this.entryFileLocation;
    }

    public void setEntryFileLocation(String str) {
        this.entryFileLocation = str;
    }

    public String getEntryFileCreatedDate() {
        return this.entryFileCreatedDate;
    }

    public void setEntryFileCreatedDate(String str) {
        this.entryFileCreatedDate = str;
    }

    public String getEntryFileModifiedDate() {
        return this.entryFileModifiedDate;
    }

    public void setEntryFileModifiedDate(String str) {
        this.entryFileModifiedDate = str;
    }

    public int getCategoryFileIconIndex() {
        return this.categoriesFileIconIndex;
    }

    public void setCategoryFileIconIndex(int i) {
        this.categoriesFileIconIndex = i;
    }

    public int getEntriesFileSortBy() {
        return this.EntriesFileSortBy;
    }

    public void setEntriesFileSortBy(int i) {
        this.EntriesFileSortBy = i;
    }
}
