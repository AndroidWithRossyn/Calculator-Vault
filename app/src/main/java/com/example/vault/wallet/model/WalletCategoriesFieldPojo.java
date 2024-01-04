package com.example.vault.wallet.model;

public class WalletCategoriesFieldPojo {
    private String fieldName;
    private String fieldValue;
    private boolean isSecured;

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String str) {
        this.fieldName = str;
    }

    public String getFieldValue() {
        return this.fieldValue;
    }

    public void setFieldValue(String str) {
        this.fieldValue = str;
    }

    public boolean isSecured() {
        return this.isSecured;
    }

    public void setSecured(boolean z) {
        this.isSecured = z;
    }
}
