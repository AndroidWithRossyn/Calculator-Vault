package com.example.vault.documents.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import com.example.vault.dbhelper.DatabaseHelper;
import com.example.vault.documents.model.DocumentFolder;
import com.example.vault.documents.util.DocumentDAL;
import com.example.vault.photo.PhotosAlbumActivty.SortBy;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;

public class DocumentFolderDAL {
    Context con;
    SQLiteDatabase database;
    DatabaseHelper helper;

    public DocumentFolderDAL(Context context) {
        this.helper = new DatabaseHelper(context);
        this.con = context;
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

    public void AddDocumentFolder(DocumentFolder documentFolder) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("folder_name", documentFolder.getFolderName());
        contentValues.put("fl_folder_location", documentFolder.getFolderLocation());
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        contentValues.put("SortBy", Integer.valueOf(0));
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.insert("tbl_document_folders", null, contentValues);
    }

    public List<DocumentFolder> GetFolders(int i) {
        ArrayList arrayList = new ArrayList();
        DocumentDAL documentDAL = new DocumentDAL(this.con);
        documentDAL.OpenRead();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_document_folders Where IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" ORDER BY _id");
        String sb2 = sb.toString();
        if (SortBy.Time.ordinal() == i) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("SELECT * FROM tbl_document_folders Where IsFakeAccount = ");
            sb3.append(SecurityLocksCommon.IsFakeAccount);
            sb3.append(" ORDER BY ModifiedDateTime DESC");
            sb2 = sb3.toString();
        } else if (SortBy.Name.ordinal() == i) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append("SELECT * FROM tbl_document_folders Where IsFakeAccount = ");
            sb4.append(SecurityLocksCommon.IsFakeAccount);
            sb4.append(" ORDER BY folder_name COLLATE NOCASE ASC");
            sb2 = sb4.toString();
        }
        Cursor rawQuery = this.database.rawQuery(sb2, null);
        while (rawQuery.moveToNext()) {
            DocumentFolder documentFolder = new DocumentFolder();
            documentFolder.setId(rawQuery.getInt(0));
            documentFolder.setFolderName(rawQuery.getString(1));
            documentFolder.setFolderLocation(rawQuery.getString(2));
            documentFolder.set_modifiedDateTime(rawQuery.getString(6));
            documentFolder.set_fileCount(documentDAL.GetDocumentCountByFolderId(rawQuery.getInt(0)));
            arrayList.add(documentFolder);
        }
        rawQuery.close();
        return arrayList;
    }

    public int GetSortByFolderId(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT SortBy FROM tbl_document_folders where _id = ");
        sb.append(i);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        int i2 = 0;
        while (rawQuery.moveToNext()) {
            i2 = rawQuery.getInt(0);
        }
        rawQuery.close();
        return i2;
    }

    public void AddSortByInDocumentFolder(int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("SortBy", Integer.valueOf(i));
        this.database.update("tbl_document_folders", contentValues, "_id = ?", new String[]{String.valueOf(Common.FolderId)});
        close();
    }

    public DocumentFolder GetFolder(String str) {
        DocumentFolder documentFolder = new DocumentFolder();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_document_folders where folder_name = '");
        sb.append(str);
        sb.append("' AND IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            documentFolder.setId(rawQuery.getInt(0));
            documentFolder.setFolderName(rawQuery.getString(1));
            documentFolder.setFolderLocation(rawQuery.getString(2));
            documentFolder.set_modifiedDateTime(rawQuery.getString(6));
        }
        rawQuery.close();
        return documentFolder;
    }

    public DocumentFolder GetFolderById(int i) {
        DocumentFolder documentFolder = new DocumentFolder();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_document_folders where _id = ");
        sb.append(i);
        sb.append(" AND IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            documentFolder.setId(rawQuery.getInt(0));
            documentFolder.setFolderName(rawQuery.getString(1));
            documentFolder.setFolderLocation(rawQuery.getString(2));
            documentFolder.set_modifiedDateTime(rawQuery.getString(6));
        }
        rawQuery.close();
        return documentFolder;
    }

    public DocumentFolder GetFolderById(String str) {
        DocumentFolder documentFolder = new DocumentFolder();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_document_folders where _id = '");
        sb.append(str);
        sb.append("' AND IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            documentFolder.setId(rawQuery.getInt(0));
            documentFolder.setFolderName(rawQuery.getString(1));
            documentFolder.setFolderLocation(rawQuery.getString(2));
            documentFolder.set_modifiedDateTime(rawQuery.getString(6));
        }
        rawQuery.close();
        return documentFolder;
    }

    public void UpdateFolderName(DocumentFolder documentFolder) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("folder_name", documentFolder.getFolderName());
        contentValues.put("fl_folder_location", documentFolder.getFolderLocation());
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.update("tbl_document_folders", contentValues, "_id = ?", new String[]{String.valueOf(documentFolder.getId())});
        close();
        DocumentDAL documentDAL = new DocumentDAL(this.con);
        documentDAL.OpenWrite();
        documentDAL.UpdateFolderDocumentLocation(documentFolder.getId(), documentFolder.getFolderLocation());
        documentDAL.close();
    }

    public String GetFolderName(String str) {
        String str2 = "";
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_document_folders where _id =");
        sb.append(str);
        sb.append(" AND IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            str2 = rawQuery.getString(1);
        }
        rawQuery.close();
        return str2;
    }

    public boolean IsFolderNameExist(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_document_folders where folder_name ='");
        sb.append(str);
        sb.append("'");
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        boolean z = false;
        while (rawQuery.moveToNext()) {
            z = true;
        }
        rawQuery.close();
        return z;
    }

    public int IfFolderNameExistReturnId(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_document_folders where folder_name ='");
        sb.append(str);
        sb.append("'");
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i = rawQuery.getInt(0);
        }
        rawQuery.close();
        return i;
    }

    public void DeleteFolderById(int i) {
        this.database.delete("tbl_document_folders", "_id = ?", new String[]{String.valueOf(i)});
        close();
        DocumentDAL documentDAL = new DocumentDAL(this.con);
        documentDAL.OpenWrite();
        documentDAL.DeleteDocuments(i);
        documentDAL.close();
    }

    public int GetLastFolderId() {
        Cursor rawQuery = this.database.rawQuery("SELECT _id FROM tbl_document_folders WHERE _id = (SELECT MAX(_id)  FROM tbl_document_folders)", null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i = rawQuery.getInt(0);
        }
        rawQuery.close();
        return i;
    }

    public void UpdateFolderLocation(int i, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("fl_folder_location", str);
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.update("tbl_document_folders", contentValues, "_id = ?", new String[]{String.valueOf(i)});
        close();
    }
}
