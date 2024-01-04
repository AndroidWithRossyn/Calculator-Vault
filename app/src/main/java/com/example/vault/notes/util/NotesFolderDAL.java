package com.example.vault.notes.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import com.example.vault.common.Constants;
import com.example.vault.dbhelper.DatabaseHelper;
import com.example.vault.notes.model.NotesFolderDB_Pojo;

public class NotesFolderDAL {
    Constants constants = new Constants();
    SQLiteDatabase database;
    DatabaseHelper databaseHelper;

    public NotesFolderDAL(Context context) {
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

    public void addNotesFolderInfoInDatabase(NotesFolderDB_Pojo notesFolderDB_Pojo) {
        OpenWrite();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("NotesFolderName", notesFolderDB_Pojo.getNotesFolderName());
        this.constants.getClass();
        contentValues.put("NotesFolderLocation", notesFolderDB_Pojo.getNotesFolderLocation());
        this.constants.getClass();
        contentValues.put("NotesFolderCreatedDate", notesFolderDB_Pojo.getNotesFolderCreatedDate());
        this.constants.getClass();
        contentValues.put("NotesFolderModifiedDate", notesFolderDB_Pojo.getNotesFolderModifiedDate());
        this.constants.getClass();
        contentValues.put("NotesFolderSortBy", Integer.valueOf(notesFolderDB_Pojo.getNotesFolderFilesSortBy()));
        this.constants.getClass();
        contentValues.put("NotesFolderIsDecoy", Integer.valueOf(notesFolderDB_Pojo.getNotesFolderIsDecoy()));
        this.constants.getClass();
        contentValues.put("NotesFolderColor", notesFolderDB_Pojo.getNotesFolderColor());
        this.constants.getClass();
        contentValues.put("NotesFolderViewBy", Integer.valueOf(notesFolderDB_Pojo.getNotesFolderFilesViewBy()));
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        sQLiteDatabase.insert("TableNotesFolder", null, contentValues);
        close();
    }

    public NotesFolderDB_Pojo getNotesFolderInfoFromDatabase(String str) {
        OpenRead();
        NotesFolderDB_Pojo notesFolderDB_Pojo = new NotesFolderDB_Pojo();
        Cursor rawQuery = this.database.rawQuery(str, null);
        while (rawQuery.moveToNext()) {
            notesFolderDB_Pojo.setNotesFolderId(rawQuery.getInt(0));
            notesFolderDB_Pojo.setNotesFolderName(rawQuery.getString(1));
            notesFolderDB_Pojo.setNotesFolderLocation(rawQuery.getString(2));
            notesFolderDB_Pojo.setNotesFolderCreatedDate(rawQuery.getString(3));
            notesFolderDB_Pojo.setNotesFolderModifiedDate(rawQuery.getString(4));
            notesFolderDB_Pojo.setNotesFolderFilesSortBy(rawQuery.getInt(5));
            this.constants.getClass();
            notesFolderDB_Pojo.setNotesFolderIsDecoy(rawQuery.getInt(rawQuery.getColumnIndex("NotesFolderIsDecoy")));
            this.constants.getClass();
            notesFolderDB_Pojo.setNotesFolderColor(rawQuery.getString(rawQuery.getColumnIndex("NotesFolderColor")));
            this.constants.getClass();
            notesFolderDB_Pojo.setNotesFolderFilesViewBy(rawQuery.getInt(rawQuery.getColumnIndex("NotesFolderViewBy")));
        }
        close();
        return notesFolderDB_Pojo;
    }

    public List<NotesFolderDB_Pojo> getAllNotesFolderInfoFromDatabase(String str) {
        OpenRead();
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery(str, null);
        if (rawQuery.moveToFirst()) {
            do {
                NotesFolderDB_Pojo notesFolderDB_Pojo = new NotesFolderDB_Pojo();
                notesFolderDB_Pojo.setNotesFolderId(rawQuery.getInt(0));
                notesFolderDB_Pojo.setNotesFolderName(rawQuery.getString(1));
                notesFolderDB_Pojo.setNotesFolderLocation(rawQuery.getString(2));
                notesFolderDB_Pojo.setNotesFolderCreatedDate(rawQuery.getString(3));
                notesFolderDB_Pojo.setNotesFolderModifiedDate(rawQuery.getString(4));
                notesFolderDB_Pojo.setNotesFolderFilesSortBy(rawQuery.getInt(5));
                this.constants.getClass();
                notesFolderDB_Pojo.setNotesFolderIsDecoy(rawQuery.getInt(rawQuery.getColumnIndex("NotesFolderIsDecoy")));
                this.constants.getClass();
                notesFolderDB_Pojo.setNotesFolderColor(rawQuery.getString(rawQuery.getColumnIndex("NotesFolderColor")));
                this.constants.getClass();
                notesFolderDB_Pojo.setNotesFolderFilesViewBy(rawQuery.getInt(rawQuery.getColumnIndex("NotesFolderViewBy")));
                arrayList.add(notesFolderDB_Pojo);
            } while (rawQuery.moveToNext());
        }
        close();
        return arrayList;
    }

    public String GetNotesFolderStringEntity(String str) {
        Cursor rawQuery = this.database.rawQuery(str, null);
        String str2 = "";
        while (rawQuery.moveToNext()) {
            str2 = rawQuery.getString(0);
        }
        rawQuery.close();
        return str2;
    }

    public int GetNotesFolderIntegerEntity(String str) {
        Cursor rawQuery = this.database.rawQuery(str, null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i = rawQuery.getInt(0);
        }
        rawQuery.close();
        return i;
    }

    public boolean IsFolderAlreadyExist(String str) {
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

    public void updateNotesFolderFromDatabase(NotesFolderDB_Pojo notesFolderDB_Pojo, String str, String str2) {
        OpenWrite();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("NotesFolderName", notesFolderDB_Pojo.getNotesFolderName());
        this.constants.getClass();
        contentValues.put("NotesFolderLocation", notesFolderDB_Pojo.getNotesFolderLocation());
        this.constants.getClass();
        contentValues.put("NotesFolderModifiedDate", notesFolderDB_Pojo.getNotesFolderModifiedDate());
        this.constants.getClass();
        contentValues.put("NotesFolderIsDecoy", Integer.valueOf(notesFolderDB_Pojo.getNotesFolderIsDecoy()));
        this.constants.getClass();
        contentValues.put("NotesFolderSortBy", Integer.valueOf(notesFolderDB_Pojo.getNotesFolderFilesSortBy()));
        this.constants.getClass();
        contentValues.put("NotesFolderColor", notesFolderDB_Pojo.getNotesFolderColor());
        this.constants.getClass();
        contentValues.put("NotesFolderViewBy", Integer.valueOf(notesFolderDB_Pojo.getNotesFolderFilesViewBy()));
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.update("TableNotesFolder", contentValues, sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }

    public void updateNotesFolderLocationInDatabase(NotesFolderDB_Pojo notesFolderDB_Pojo, String str, String str2) {
        OpenWrite();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("NotesFolderLocation", notesFolderDB_Pojo.getNotesFolderLocation());
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.update("TableNotesFolder", contentValues, sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }

    public void deleteNotesFolderFromDatabase(String str, String str2) {
        OpenWrite();
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.delete("TableNotesFolder", sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }
}
