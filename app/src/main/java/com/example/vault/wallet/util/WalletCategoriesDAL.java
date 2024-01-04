package com.example.vault.wallet.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import com.example.vault.common.Constants;
import com.example.vault.dbhelper.DatabaseHelper;
import com.example.vault.wallet.model.WalletCategoriesFileDB_Pojo;

public class WalletCategoriesDAL {
    Constants constants = new Constants();
    SQLiteDatabase database;
    DatabaseHelper databaseHelper;

    public WalletCategoriesDAL(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    public void OpenRead() {
        this.database = this.databaseHelper.getReadableDatabase();
    }

    public void OpenWrite() {
        this.database = this.databaseHelper.getWritableDatabase();
    }

    public void close() {
        this.database.close();
    }

    public void addWalletCategoriesInfoInDatabase(WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo) {
        OpenWrite();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("WalletCategoriesFileName", walletCategoriesFileDB_Pojo.getCategoryFileName());
        this.constants.getClass();
        contentValues.put("WalletCategoriesFileLocation", walletCategoriesFileDB_Pojo.getCategoryFileLocation());
        this.constants.getClass();
        contentValues.put("WalletCategoriesFileCreatedDate", walletCategoriesFileDB_Pojo.getCategoryFileCreatedDate());
        this.constants.getClass();
        contentValues.put("WalletCategoriesFileModifiedDate", walletCategoriesFileDB_Pojo.getCategoryFileModifiedDate());
        this.constants.getClass();
        contentValues.put("WalletCategoriesFileIconIndex", Integer.valueOf(walletCategoriesFileDB_Pojo.getCategoryFileIconIndex()));
        this.constants.getClass();
        contentValues.put("WalletCategoriesFileSortBy", Integer.valueOf(walletCategoriesFileDB_Pojo.getCategoryFileSortBy()));
        this.constants.getClass();
        contentValues.put("WalletCategoriesFileIsDecoy", Integer.valueOf(walletCategoriesFileDB_Pojo.getCategoryFileIsDecoy()));
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        sQLiteDatabase.insert("TableWalletCategories", null, contentValues);
        close();
    }

    public WalletCategoriesFileDB_Pojo getCategoryInfoFromDatabase(String str) {
        OpenRead();
        WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo = new WalletCategoriesFileDB_Pojo();
        Cursor rawQuery = this.database.rawQuery(str, null);
        while (rawQuery.moveToNext()) {
            walletCategoriesFileDB_Pojo.setCategoryFileId(rawQuery.getInt(0));
            walletCategoriesFileDB_Pojo.setCategoryFileName(rawQuery.getString(1));
            walletCategoriesFileDB_Pojo.setCategoryFileLocation(rawQuery.getString(2));
            walletCategoriesFileDB_Pojo.setCategoryFileCreatedDate(rawQuery.getString(3));
            walletCategoriesFileDB_Pojo.setCategoryFileModifiedDate(rawQuery.getString(4));
            walletCategoriesFileDB_Pojo.setCategoryFileIconIndex(rawQuery.getInt(5));
            walletCategoriesFileDB_Pojo.setCategoryFileSortBy(rawQuery.getInt(6));
            walletCategoriesFileDB_Pojo.setCategoryFileIsDecoy(rawQuery.getInt(7));
        }
        close();
        return walletCategoriesFileDB_Pojo;
    }

    public List<WalletCategoriesFileDB_Pojo> getAllCategoriesInfoFromDatabase(String str) {
        OpenRead();
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery(str, null);
        if (rawQuery.moveToFirst()) {
            do {
                WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo = new WalletCategoriesFileDB_Pojo();
                walletCategoriesFileDB_Pojo.setCategoryFileId(rawQuery.getInt(0));
                walletCategoriesFileDB_Pojo.setCategoryFileName(rawQuery.getString(1));
                walletCategoriesFileDB_Pojo.setCategoryFileLocation(rawQuery.getString(2));
                walletCategoriesFileDB_Pojo.setCategoryFileCreatedDate(rawQuery.getString(3));
                walletCategoriesFileDB_Pojo.setCategoryFileModifiedDate(rawQuery.getString(4));
                walletCategoriesFileDB_Pojo.setCategoryFileIconIndex(rawQuery.getInt(5));
                walletCategoriesFileDB_Pojo.setCategoryFileSortBy(rawQuery.getInt(6));
                walletCategoriesFileDB_Pojo.setCategoryFileIsDecoy(rawQuery.getInt(7));
                arrayList.add(walletCategoriesFileDB_Pojo);
            } while (rawQuery.moveToNext());
        }
        close();
        return arrayList;
    }

    public String GetWalletCategoriesStringEntity(String str) {
        OpenRead();
        Cursor rawQuery = this.database.rawQuery(str, null);
        String str2 = "";
        while (rawQuery.moveToNext()) {
            str2 = rawQuery.getString(0);
        }
        rawQuery.close();
        close();
        return str2;
    }

    public int GetWalletCategoriesIntegerEntity(String str) {
        OpenRead();
        Cursor rawQuery = this.database.rawQuery(str, null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i = rawQuery.getInt(0);
        }
        rawQuery.close();
        close();
        return i;
    }

    public boolean IsWalletCategoryAlreadyExist(String str) {
        OpenRead();
        Cursor rawQuery = this.database.rawQuery(str, null);
        boolean z = false;
        while (rawQuery.moveToNext()) {
            z = true;
        }
        rawQuery.close();
        close();
        return z;
    }

    public void updateCategoryFromDatabase(WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo, String str, String str2) {
        OpenWrite();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("WalletCategoriesFileName", walletCategoriesFileDB_Pojo.getCategoryFileName());
        this.constants.getClass();
        contentValues.put("WalletCategoriesFileModifiedDate", walletCategoriesFileDB_Pojo.getCategoryFileModifiedDate());
        this.constants.getClass();
        contentValues.put("WalletCategoriesFileSortBy", Integer.valueOf(walletCategoriesFileDB_Pojo.getCategoryFileSortBy()));
        this.constants.getClass();
        contentValues.put("WalletCategoriesFileIsDecoy", Integer.valueOf(walletCategoriesFileDB_Pojo.getCategoryFileIsDecoy()));
        this.constants.getClass();
        contentValues.put("WalletCategoriesFileLocation", walletCategoriesFileDB_Pojo.getCategoryFileLocation());
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.update("TableWalletCategories", contentValues, sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }

    public void deleteCategoryFromDatabase(String str, String str2) {
        OpenWrite();
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.delete("TableWalletCategories", sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }
}
