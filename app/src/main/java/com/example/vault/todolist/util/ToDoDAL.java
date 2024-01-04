package com.example.vault.todolist.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import com.example.vault.common.Constants;
import com.example.vault.dbhelper.DatabaseHelper;
import com.example.vault.todolist.model.ToDoDB_Pojo;

public class ToDoDAL {
    Constants constants = new Constants();
    SQLiteDatabase database;
    DatabaseHelper databaseHelper;

    public ToDoDAL(Context context) {
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

    public void addToDoInfoInDatabase(ToDoDB_Pojo toDoDB_Pojo) {
        OpenRead();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("ToDoName", toDoDB_Pojo.getToDoFileName());
        this.constants.getClass();
        contentValues.put("ToDoFileLocation", toDoDB_Pojo.getToDoFileLocation());
        this.constants.getClass();
        contentValues.put("ToDoTask1", toDoDB_Pojo.getToDoFileTask1());
        this.constants.getClass();
        contentValues.put("ToDoTask1IsChecked", Boolean.valueOf(toDoDB_Pojo.isToDoFileTask1IsChecked()));
        this.constants.getClass();
        contentValues.put("ToDoTask2", toDoDB_Pojo.getToDoFileTask2());
        this.constants.getClass();
        contentValues.put("ToDoTask2IsChecked", Boolean.valueOf(toDoDB_Pojo.isToDoFileTask2IsChecked()));
        this.constants.getClass();
        contentValues.put("ToDoCreatedDate", toDoDB_Pojo.getToDoFileCreatedDate());
        this.constants.getClass();
        contentValues.put("ToDoModifiedDate", toDoDB_Pojo.getToDoFileModifiedDate());
        this.constants.getClass();
        contentValues.put("ToDoColor", toDoDB_Pojo.getToDoFileColor());
        this.constants.getClass();
        contentValues.put("ToDoIsDecoy", Integer.valueOf(toDoDB_Pojo.getToDoFileIsDecoy()));
        this.constants.getClass();
        contentValues.put("ToDoFinished", Boolean.valueOf(toDoDB_Pojo.isToDoFinished()));
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        sQLiteDatabase.insert("TableToDo", null, contentValues);
        close();
    }

    public ToDoDB_Pojo getToDoInfoFromDatabase(String str) {
        ToDoDB_Pojo toDoDB_Pojo = new ToDoDB_Pojo();
        OpenRead();
        Cursor rawQuery = this.database.rawQuery(str, null);
        while (rawQuery.moveToNext()) {
            boolean z = false;
            toDoDB_Pojo.setToDoId(rawQuery.getInt(0));
            toDoDB_Pojo.setToDoFileName(rawQuery.getString(1));
            toDoDB_Pojo.setToDoFileLocation(rawQuery.getString(2));
            toDoDB_Pojo.setToDoFileTask1(rawQuery.getString(3));
            this.constants.getClass();
            toDoDB_Pojo.setToDoFileTask1IsChecked(rawQuery.getInt(rawQuery.getColumnIndex("ToDoTask1IsChecked")) != 0);
            toDoDB_Pojo.setToDoFileTask2(rawQuery.getString(5));
            this.constants.getClass();
            toDoDB_Pojo.setToDoFileTask2IsChecked(rawQuery.getInt(rawQuery.getColumnIndex("ToDoTask2IsChecked")) != 0);
            toDoDB_Pojo.setToDoFileCreatedDate(rawQuery.getString(7));
            toDoDB_Pojo.setToDoFileModifiedDate(rawQuery.getString(8));
            toDoDB_Pojo.setToDoFileColor(rawQuery.getString(9));
            toDoDB_Pojo.setToDoFileIsDecoy(rawQuery.getInt(10));
            this.constants.getClass();
            if (rawQuery.getInt(rawQuery.getColumnIndex("ToDoFinished")) != 0) {
                z = true;
            }
            toDoDB_Pojo.setToDoFinished(z);
        }
        close();
        return toDoDB_Pojo;
    }

    public ArrayList<ToDoDB_Pojo> getAllToDoInfoFromDatabase(String str) {
        OpenRead();
        ArrayList<ToDoDB_Pojo> arrayList = new ArrayList<>();
        Cursor rawQuery = this.database.rawQuery(str, null);
        if (rawQuery.moveToFirst()) {
            do {
                ToDoDB_Pojo toDoDB_Pojo = new ToDoDB_Pojo();
                boolean z = false;
                toDoDB_Pojo.setToDoId(rawQuery.getInt(0));
                toDoDB_Pojo.setToDoFileName(rawQuery.getString(1));
                toDoDB_Pojo.setToDoFileLocation(rawQuery.getString(2));
                toDoDB_Pojo.setToDoFileTask1(rawQuery.getString(3));
                this.constants.getClass();
                toDoDB_Pojo.setToDoFileTask1IsChecked(rawQuery.getInt(rawQuery.getColumnIndex("ToDoTask1IsChecked")) != 0);
                toDoDB_Pojo.setToDoFileTask2(rawQuery.getString(5));
                this.constants.getClass();
                toDoDB_Pojo.setToDoFileTask2IsChecked(rawQuery.getInt(rawQuery.getColumnIndex("ToDoTask2IsChecked")) != 0);
                toDoDB_Pojo.setToDoFileCreatedDate(rawQuery.getString(7));
                toDoDB_Pojo.setToDoFileModifiedDate(rawQuery.getString(8));
                toDoDB_Pojo.setToDoFileColor(rawQuery.getString(9));
                toDoDB_Pojo.setToDoFileIsDecoy(rawQuery.getInt(10));
                this.constants.getClass();
                if (rawQuery.getInt(rawQuery.getColumnIndex("ToDoFinished")) != 0) {
                    z = true;
                }
                toDoDB_Pojo.setToDoFinished(z);
                arrayList.add(toDoDB_Pojo);
            } while (rawQuery.moveToNext());
        }
        close();
        return arrayList;
    }

    public String GetToDoDbFileStringEntity(String str) {
        Cursor rawQuery = this.database.rawQuery(str, null);
        String str2 = "";
        while (rawQuery.moveToNext()) {
            str2 = rawQuery.getString(0);
        }
        rawQuery.close();
        return str2;
    }

    public int GetToDoDbFileIntegerEntity(String str) {
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

    public double GetToDoDbFileRealEntity(String str) {
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

    public int GetToDoDbFilesCount(String str) {
        OpenRead();
        Cursor rawQuery = this.database.rawQuery(str, null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i = rawQuery.getInt(0);
        }
        close();
        return i;
    }

    public void updateToDoFileInfoInDatabase(ToDoDB_Pojo toDoDB_Pojo, String str, String str2) {
        OpenWrite();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("ToDoName", toDoDB_Pojo.getToDoFileName());
        this.constants.getClass();
        contentValues.put("ToDoFileLocation", toDoDB_Pojo.getToDoFileLocation());
        this.constants.getClass();
        contentValues.put("ToDoTask1", toDoDB_Pojo.getToDoFileTask1());
        this.constants.getClass();
        contentValues.put("ToDoTask1IsChecked", Boolean.valueOf(toDoDB_Pojo.isToDoFileTask1IsChecked()));
        this.constants.getClass();
        contentValues.put("ToDoTask2", toDoDB_Pojo.getToDoFileTask2());
        this.constants.getClass();
        contentValues.put("ToDoTask2IsChecked", Boolean.valueOf(toDoDB_Pojo.isToDoFileTask2IsChecked()));
        this.constants.getClass();
        contentValues.put("ToDoModifiedDate", toDoDB_Pojo.getToDoFileModifiedDate());
        this.constants.getClass();
        contentValues.put("ToDoColor", toDoDB_Pojo.getToDoFileColor());
        this.constants.getClass();
        contentValues.put("ToDoIsDecoy", Integer.valueOf(toDoDB_Pojo.getToDoFileIsDecoy()));
        this.constants.getClass();
        contentValues.put("ToDoFinished", Boolean.valueOf(toDoDB_Pojo.isToDoFinished()));
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.update("TableToDo", contentValues, sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }

    public void updateToDoFileLocationInDatabase(ToDoDB_Pojo toDoDB_Pojo, String str, String str2) {
        OpenWrite();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("ToDoFileLocation", toDoDB_Pojo.getToDoFileLocation());
        this.constants.getClass();
        contentValues.put("ToDoModifiedDate", toDoDB_Pojo.getToDoFileModifiedDate());
        this.constants.getClass();
        contentValues.put("ToDoColor", toDoDB_Pojo.getToDoFileColor());
        this.constants.getClass();
        contentValues.put("ToDoIsDecoy", Integer.valueOf(toDoDB_Pojo.getToDoFileIsDecoy()));
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.update("TableToDo", contentValues, sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }

    public void updateToDoFileNameInDatabase(ToDoDB_Pojo toDoDB_Pojo, String str, String str2) {
        OpenWrite();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("ToDoFileLocation", toDoDB_Pojo.getToDoFileLocation());
        this.constants.getClass();
        contentValues.put("ToDoModifiedDate", toDoDB_Pojo.getToDoFileModifiedDate());
        this.constants.getClass();
        contentValues.put("ToDoName", toDoDB_Pojo.getToDoFileName());
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.update("TableToDo", contentValues, sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }

    public void updateToDoFileTasksInDatabase(ToDoDB_Pojo toDoDB_Pojo, String str, String str2) {
        OpenWrite();
        ContentValues contentValues = new ContentValues();
        this.constants.getClass();
        contentValues.put("ToDoModifiedDate", toDoDB_Pojo.getToDoFileModifiedDate());
        this.constants.getClass();
        contentValues.put("ToDoTask1", toDoDB_Pojo.getToDoFileTask1());
        this.constants.getClass();
        contentValues.put("ToDoTask2", toDoDB_Pojo.getToDoFileTask2());
        this.constants.getClass();
        contentValues.put("ToDoTask1IsChecked", Boolean.valueOf(toDoDB_Pojo.isToDoFileTask1IsChecked()));
        this.constants.getClass();
        contentValues.put("ToDoTask2IsChecked", Boolean.valueOf(toDoDB_Pojo.isToDoFileTask2IsChecked()));
        this.constants.getClass();
        contentValues.put("ToDoFinished", Boolean.valueOf(toDoDB_Pojo.isToDoFinished()));
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.update("TableToDo", contentValues, sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }

    public void deleteToDoFileFromDatabase(String str, String str2) {
        OpenWrite();
        SQLiteDatabase sQLiteDatabase = this.database;
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" = ? ");
        sQLiteDatabase.delete("TableToDo", sb.toString(), new String[]{String.valueOf(str2)});
        close();
    }
}
