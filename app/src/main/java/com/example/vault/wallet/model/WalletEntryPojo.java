package com.example.vault.wallet.model;

import com.example.vault.wallet.model.WalletCategoriesFieldPojo;

import java.util.List;

public class WalletEntryPojo {
    private String categoryName;
    private String entryName;
    private List<WalletCategoriesFieldPojo> fields;

    public WalletEntryPojo() {
    }

    public WalletEntryPojo(String str, String str2, List<WalletCategoriesFieldPojo> list) {
        this.categoryName = str;
        this.entryName = str2;
        this.fields = list;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String str) {
        this.categoryName = str;
    }

    public String getEntryName() {
        return this.entryName;
    }

    public void setEntryName(String str) {
        this.entryName = str;
    }

    public List<WalletCategoriesFieldPojo> getFields() {
        return this.fields;
    }

    public void setFields(List<WalletCategoriesFieldPojo> list) {
        this.fields = list;
    }
}
