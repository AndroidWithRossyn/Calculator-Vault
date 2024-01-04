package com.example.vault.notes.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import com.example.vault.common.Constants;
import com.example.vault.dbhelper.DatabaseHelper;
import com.example.vault.notes.model.NotesFileDB_Pojo;

public class NotesFilesDAL {
    Constants constants = new Constants();
    SQLiteDatabase database;
    DatabaseHelper databaseHelper;

    public NotesFilesDAL(Context context) {
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

    public void addNotesFilesInfoInDatabase(NotesFileDB_Pojo notesFileDB_Pojo) {
        OpenRead();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("NotesFolderId", Integer.valueOf(notesFileDB_Pojo.getNotesFileFolderId()));
        this.constants.getClass();
        contentValues.put("NotesFileName", notesFileDB_Pojo.getNotesFileName());
        this.constants.getClass();
        contentValues.put("NotesFileLocation", notesFileDB_Pojo.getNotesFileLocation());
        this.constants.getClass();
        contentValues.put("NotesFileCreatedDate", notesFileDB_Pojo.getNotesFileCreatedDate());
        this.constants.getClass();
        contentValues.put("NotesFileModifiedDate", notesFileDB_Pojo.getNotesFileModifiedDate());
        this.constants.getClass();
        contentValues.put("NotesFileFromCloud", Integer.valueOf(notesFileDB_Pojo.getNotesFileFromCloud()));
        this.constants.getClass();
        contentValues.put("NotesFileSize", Double.valueOf(notesFileDB_Pojo.getNotesFileSize()));
        this.constants.getClass();
        contentValues.put("NotesFileText", notesFileDB_Pojo.getNotesFileText());
        this.constants.getClass();
        contentValues.put("NotesFileIsDecoy", Integer.valueOf(notesFileDB_Pojo.getNotesFileIsDecoy()));
        this.constants.getClass();
        contentValues.put("NotesFileColor", notesFileDB_Pojo.getNotesfileColor());
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        sQLiteDatabase.insert("TableNotesFile", null, contentValues);
        close();
    }

    public NotesFileDB_Pojo getNotesFileInfoFromDatabase(String str) {
        NotesFileDB_Pojo notesFileDB_Pojo = new NotesFileDB_Pojo();
        OpenRead();
        Cursor rawQuery = this.database.rawQuery(str, null);
        while (rawQuery.moveToNext()) {
            notesFileDB_Pojo.setNotesFileId(rawQuery.getInt(0));
            notesFileDB_Pojo.setNotesFileFolderId(rawQuery.getInt(1));
            notesFileDB_Pojo.setNotesFileName(rawQuery.getString(2));
            notesFileDB_Pojo.setNotesFileLocation(rawQuery.getString(3));
            notesFileDB_Pojo.setNotesFileCreatedDate(rawQuery.getString(4));
            notesFileDB_Pojo.setNotesFileModifiedDate(rawQuery.getString(5));
            notesFileDB_Pojo.setNotesFileFromCloud(rawQuery.getInt(6));
            notesFileDB_Pojo.setNotesFileSize(rawQuery.getDouble(7));
            notesFileDB_Pojo.setNotesFileText(rawQuery.getString(8));
            notesFileDB_Pojo.setNotesFileIsDecoy(rawQuery.getInt(9));
            notesFileDB_Pojo.setNotesfileColor(rawQuery.getString(10));
        }
        close();
        return notesFileDB_Pojo;
    }

    public List<NotesFileDB_Pojo> getAllNotesFileInfoFromDatabase(String str) {
        OpenRead();
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery(str, null);
        if (rawQuery.moveToFirst()) {
            do {
                NotesFileDB_Pojo notesFileDB_Pojo = new NotesFileDB_Pojo();
                notesFileDB_Pojo.setNotesFileId(rawQuery.getInt(0));
                notesFileDB_Pojo.setNotesFileFolderId(rawQuery.getInt(1));
                notesFileDB_Pojo.setNotesFileName(rawQuery.getString(2));
                notesFileDB_Pojo.setNotesFileLocation(rawQuery.getString(3));
                notesFileDB_Pojo.setNotesFileCreatedDate(rawQuery.getString(4));
                notesFileDB_Pojo.setNotesFileModifiedDate(rawQuery.getString(5));
                notesFileDB_Pojo.setNotesFileFromCloud(rawQuery.getInt(6));
                notesFileDB_Pojo.setNotesFileSize(rawQuery.getDouble(7));
                notesFileDB_Pojo.setNotesFileText(rawQuery.getString(8));
                notesFileDB_Pojo.setNotesFileIsDecoy(rawQuery.getInt(9));
                notesFileDB_Pojo.setNotesfileColor(rawQuery.getString(10));
                arrayList.add(notesFileDB_Pojo);
            } while (rawQuery.moveToNext());
        }
        close();
        return arrayList;
    }

    public String GetNotesFileStringEntity(String str) {
        Cursor rawQuery = this.database.rawQuery(str, null);
        String str2 = "";
        while (rawQuery.moveToNext()) {
            str2 = rawQuery.getString(0);
        }
        rawQuery.close();
        return str2;
    }

    public int GetNotesFileIntegerEntity(String str) {
        Cursor rawQuery = this.database.rawQuery(str, null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i = rawQuery.getInt(0);
        }
        rawQuery.close();
        return i;
    }

    public double GetNotesFileRealEntity(String str) {
        OpenRead();
        Cursor rawQuery = this.database.rawQuery(str, null);
        double d = 0.0d;
        while (rawQuery.moveToNext()) {
            d = rawQuery.getDouble(0);
        }
        rawQuery.close();
        close();
        return d;
    }

    public boolean IsFileAlreadyExist(String str) {
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

    public int getNotesFilesCount(String str) {
        OpenRead();
        Cursor rawQuery = this.database.rawQuery(str, null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i = rawQuery.getInt(0);
        }
        close();
        return i;
    }

    public void updateNotesFileInfoInDatabase(NotesFileDB_Pojo notesFileDB_Pojo, String str, String str2) {
        OpenWrite();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("NotesFolderId", Integer.valueOf(notesFileDB_Pojo.getNotesFileFolderId()));
        this.constants.getClass();
        contentValues.put("NotesFileName", notesFileDB_Pojo.getNotesFileName());
        this.constants.getClass();
        contentValues.put("NotesFileLocation", notesFileDB_Pojo.getNotesFileLocation());
        this.constants.getClass();
        contentValues.put("NotesFileModifiedDate", notesFileDB_Pojo.getNotesFileModifiedDate());
        this.constants.getClass();
        contentValues.put("NotesFileFromCloud", Integer.valueOf(notesFileDB_Pojo.getNotesFileFromCloud()));
        this.constants.getClass();
        contentValues.put("NotesFileSize", Double.valueOf(notesFileDB_Pojo.getNotesFileSize()));
        this.constants.getClass();
        contentValues.put("NotesFileIsDecoy", Integer.valueOf(notesFileDB_Pojo.getNotesFileIsDecoy()));
        this.constants.getClass();
        contentValues.put("NotesFileColor", notesFileDB_Pojo.getNotesfileColor());
        this.constants.getClass();
        contentValues.put("NotesFileText", notesFileDB_Pojo.getNotesFileText());
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.update("TableNotesFile", contentValues, sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }

    public void updateNotesFileLocationInDatabase(NotesFileDB_Pojo notesFileDB_Pojo, String str, String str2) {
        OpenWrite();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("NotesFileLocation", notesFileDB_Pojo.getNotesFileLocation());
        this.constants.getClass();
        contentValues.put("NotesFileModifiedDate", notesFileDB_Pojo.getNotesFileModifiedDate());
        this.constants.getClass();
        contentValues.put("NotesFileIsDecoy", Integer.valueOf(notesFileDB_Pojo.getNotesFileIsDecoy()));
        this.constants.getClass();
        contentValues.put("NotesFileColor", notesFileDB_Pojo.getNotesfileColor());
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.update("TableNotesFile", contentValues, sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }

    public void deleteNotesFileFromDatabase(String str, String str2) {
        OpenWrite();
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.delete("TableNotesFile", sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }
}
