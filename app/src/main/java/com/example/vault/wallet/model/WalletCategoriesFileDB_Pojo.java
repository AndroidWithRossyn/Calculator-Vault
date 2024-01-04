package com.example.vault.wallet.model;

public class WalletCategoriesFileDB_Pojo {
    private String categoriesFileCreatedDate;
    private int categoriesFileIconIndex;
    private int categoriesFileId;
    private int categoriesFileIsDecoy;
    private String categoriesFileLocation;
    private String categoriesFileModifiedDate;
    private String categoriesFileName;
    private int categoriesFileSortBy;

    public int getCategoryFileId() {
        return this.categoriesFileId;
    }

    public void setCategoryFileId(int i) {
        this.categoriesFileId = i;
    }

    public int getCategoryFileSortBy() {
        return this.categoriesFileSortBy;
    }

    public void setCategoryFileSortBy(int i) {
        this.categoriesFileSortBy = i;
    }

    public int getCategoryFileIsDecoy() {
        return this.categoriesFileIsDecoy;
    }

    public void setCategoryFileIsDecoy(int i) {
        this.categoriesFileIsDecoy = i;
    }

    public int getCategoryFileIconIndex() {
        return this.categoriesFileIconIndex;
    }

    public void setCategoryFileIconIndex(int i) {
        this.categoriesFileIconIndex = i;
    }

    public String getCategoryFileName() {
        return this.categoriesFileName;
    }

    public void setCategoryFileName(String str) {
        this.categoriesFileName = str;
    }

    public String getCategoryFileLocation() {
        return this.categoriesFileLocation;
    }

    public void setCategoryFileLocation(String str) {
        this.categoriesFileLocation = str;
    }

    public String getCategoryFileCreatedDate() {
        return this.categoriesFileCreatedDate;
    }

    public void setCategoryFileCreatedDate(String str) {
        this.categoriesFileCreatedDate = str;
    }

    public String getCategoryFileModifiedDate() {
        return this.categoriesFileModifiedDate;
    }

    public void setCategoryFileModifiedDate(String str) {
        this.categoriesFileModifiedDate = str;
    }
}
