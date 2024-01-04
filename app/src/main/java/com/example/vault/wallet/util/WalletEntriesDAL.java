package com.example.vault.wallet.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import com.example.vault.common.Constants;
import com.example.vault.dbhelper.DatabaseHelper;
import com.example.vault.wallet.model.WalletEntryFileDB_Pojo;

public class WalletEntriesDAL {
    Constants constants = new Constants();
    SQLiteDatabase database;
    DatabaseHelper databaseHelper;

    public WalletEntriesDAL(Context context) {
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

    public void addWalletEntriesInfoInDatabase(WalletEntryFileDB_Pojo walletEntryFileDB_Pojo) {
        OpenWrite();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("WalletEntryFileName", walletEntryFileDB_Pojo.getEntryFileName());
        this.constants.getClass();
        contentValues.put("WalletCategoriesFileId", Integer.valueOf(walletEntryFileDB_Pojo.getCategoryId()));
        this.constants.getClass();
        contentValues.put("WalletEntryFileLocation", walletEntryFileDB_Pojo.getEntryFileLocation());
        this.constants.getClass();
        contentValues.put("WalletEntryFileCreatedDate", walletEntryFileDB_Pojo.getEntryFileCreatedDate());
        this.constants.getClass();
        contentValues.put("WalletEntryFileModifiedDate", walletEntryFileDB_Pojo.getEntryFileModifiedDate());
        this.constants.getClass();
        contentValues.put("WalletCategoriesFileIconIndex", Integer.valueOf(walletEntryFileDB_Pojo.getCategoryFileIconIndex()));
        this.constants.getClass();
        contentValues.put("WalletEntryFilesSortBy", Integer.valueOf(walletEntryFileDB_Pojo.getEntriesFileSortBy()));
        this.constants.getClass();
        contentValues.put("WalletEntryFileIsDecoy", Integer.valueOf(walletEntryFileDB_Pojo.getEntryFileIsDecoy()));
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        sQLiteDatabase.insert("TableWalletEntries", null, contentValues);
        close();
    }

    public WalletEntryFileDB_Pojo getEntryInfoFromDatabase(String str) {
        OpenRead();
        WalletEntryFileDB_Pojo walletEntryFileDB_Pojo = new WalletEntryFileDB_Pojo();
        Cursor rawQuery = this.database.rawQuery(str, null);
        while (rawQuery.moveToNext()) {
            walletEntryFileDB_Pojo.setEntryFileId(rawQuery.getInt(0));
            walletEntryFileDB_Pojo.setCategoryId(rawQuery.getInt(1));
            walletEntryFileDB_Pojo.setEntryFileName(rawQuery.getString(2));
            walletEntryFileDB_Pojo.setEntryFileLocation(rawQuery.getString(3));
            walletEntryFileDB_Pojo.setEntryFileCreatedDate(rawQuery.getString(4));
            walletEntryFileDB_Pojo.setEntryFileModifiedDate(rawQuery.getString(5));
            walletEntryFileDB_Pojo.setCategoryFileIconIndex(rawQuery.getInt(6));
            walletEntryFileDB_Pojo.setEntriesFileSortBy(rawQuery.getInt(7));
            walletEntryFileDB_Pojo.setEntryFileIsDecoy(rawQuery.getInt(8));
        }
        close();
        return walletEntryFileDB_Pojo;
    }

    public List<WalletEntryFileDB_Pojo> getAllEntriesInfoFromDatabase(String str) {
        OpenRead();
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery(str, null);
        if (rawQuery.moveToFirst()) {
            do {
                WalletEntryFileDB_Pojo walletEntryFileDB_Pojo = new WalletEntryFileDB_Pojo();
                walletEntryFileDB_Pojo.setEntryFileId(rawQuery.getInt(0));
                walletEntryFileDB_Pojo.setCategoryId(rawQuery.getInt(1));
                walletEntryFileDB_Pojo.setEntryFileName(rawQuery.getString(2));
                walletEntryFileDB_Pojo.setEntryFileLocation(rawQuery.getString(3));
                walletEntryFileDB_Pojo.setEntryFileCreatedDate(rawQuery.getString(4));
                walletEntryFileDB_Pojo.setEntryFileModifiedDate(rawQuery.getString(5));
                walletEntryFileDB_Pojo.setCategoryFileIconIndex(rawQuery.getInt(6));
                walletEntryFileDB_Pojo.setEntriesFileSortBy(rawQuery.getInt(7));
                walletEntryFileDB_Pojo.setEntryFileIsDecoy(rawQuery.getInt(8));
                arrayList.add(walletEntryFileDB_Pojo);
            } while (rawQuery.moveToNext());
        }
        close();
        return arrayList;
    }

    public String GetWalletEntriesStringEntity(String str) {
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

    public int GetWalletEntriesIntegerEntity(String str) {
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

    public boolean IsWalletEntryAlreadyExist(String str) {
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

    public int getEntriesCount(String str) {
        OpenRead();
        Cursor rawQuery = this.database.rawQuery(str, null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i = rawQuery.getInt(0);
        }
        close();
        return i;
    }

    public void updateEntryInDatabase(WalletEntryFileDB_Pojo walletEntryFileDB_Pojo, String str, String str2) {
        OpenWrite();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("WalletEntryFileLocation", walletEntryFileDB_Pojo.getEntryFileLocation());
        this.constants.getClass();
        contentValues.put("WalletEntryFileModifiedDate", walletEntryFileDB_Pojo.getEntryFileModifiedDate());
        this.constants.getClass();
        contentValues.put("WalletEntryFilesSortBy", Integer.valueOf(walletEntryFileDB_Pojo.getEntriesFileSortBy()));
        this.constants.getClass();
        contentValues.put("WalletEntryFileIsDecoy", Integer.valueOf(walletEntryFileDB_Pojo.getEntryFileIsDecoy()));
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.update("TableWalletEntries", contentValues, sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }

    public void updateEntryLocationInDatabase(WalletEntryFileDB_Pojo walletEntryFileDB_Pojo, String str, String str2) {
        OpenWrite();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("WalletEntryFileLocation", walletEntryFileDB_Pojo.getEntryFileLocation());
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.update("TableWalletEntries", contentValues, sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }

    public void deleteEntryFromDatabase(String str, String str2) {
        OpenWrite();
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.delete("TableWalletEntries", sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }
}
