package com.example.vault.privatebrowser.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.example.vault.common.Constants;
import com.example.vault.dbhelper.DatabaseHelper;
import com.example.vault.privatebrowser.model.BrowserHistoryEnt;
import com.example.vault.securitylocks.SecurityLocksCommon;

public class BrowserHistoryDAL {
    SQLiteDatabase database;
    DatabaseHelper helper;

    public BrowserHistoryDAL(Context context) {
        this.helper = new DatabaseHelper(context);
    }

    public void OpenRead() throws SQLException {
        this.database = this.helper.getReadableDatabase();
    }

    public void OpenWrite() throws SQLException {
        this.database = this.helper.getWritableDatabase();
    }

    public void close() {
        this.database.close();
    }

    public Boolean AddBrowserHistory(String str) {
        OpenWrite();
        if (!IsUrlAlreadyExist(str).booleanValue()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("Url", str);
            contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
            this.database.insert("tbl_BrowserHistory", null, contentValues);
            return Boolean.valueOf(true);
        }
        UpdateBrowserHistory(str);
        return Boolean.valueOf(false);
    }

    public Boolean IsUrlAlreadyExist(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_BrowserHistory Where Url ='");
        sb.append(str);
        sb.append("' AND IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        Boolean valueOf = Boolean.valueOf(false);
        while (rawQuery.moveToNext()) {
            valueOf = Boolean.valueOf(true);
        }
        rawQuery.close();
        return valueOf;
    }

    public List<BrowserHistoryEnt> GetBrowserHistories() {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_BrowserHistory Where IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" ORDER BY CreateDate");
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            BrowserHistoryEnt browserHistoryEnt = new BrowserHistoryEnt();
            browserHistoryEnt.SetId(rawQuery.getInt(0));
            browserHistoryEnt.SetURL(rawQuery.getString(1));
            browserHistoryEnt.SetCreateDate(rawQuery.getString(2));
            arrayList.add(browserHistoryEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public void UpdateBrowserHistory(String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Url", str);
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        contentValues.put("CreateDate", new Timestamp(Calendar.getInstance().getTime().getTime()).toGMTString());
        this.database.update("tbl_BrowserHistory", contentValues, "Url = ? AND IsFakeAccount = ?", new String[]{String.valueOf(str), String.valueOf(SecurityLocksCommon.IsFakeAccount)});
        close();
    }

    public List<String> GetBrowserUrlHistories() {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_BrowserHistory Where IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" ORDER BY CreateDate desc");
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            arrayList.add(rawQuery.getString(1));
        }
        rawQuery.close();
        return arrayList;
    }

    public List<String> GetBrowserAutoCompletedHistories() {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_BrowserHistory Where IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" ORDER BY CreateDate desc");
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            String replace = rawQuery.getString(1).replace(Constants.HTTP, "").replace(Constants.HTTPS, "");
            if (replace.length() > 30) {
                arrayList.add(replace.substring(0, 29));
            } else {
                arrayList.add(replace);
            }
        }
        rawQuery.close();
        return arrayList;
    }

    public void DeleteHistories() {
        OpenWrite();
        this.database.delete("tbl_BrowserHistory", "IsFakeAccount = ?", new String[]{String.valueOf(SecurityLocksCommon.IsFakeAccount)});
        close();
    }
}
