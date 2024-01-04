package com.example.vault.photo.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.example.vault.dbhelper.DatabaseHelper;
import com.example.vault.photo.Photos_Gallery_Actitvity.SortBy;
import com.example.vault.photo.model.Photo;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.utilities.Globals;
import com.example.vault.utilities.Utilities;

public class PhotoDAL {
    Context con;
    SQLiteDatabase database;
    DatabaseHelper helper;

    public PhotoDAL(Context context) {
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

    public void AddPhotos(Photo photo) {
        File file = new File(photo.getFolderLockPhotoLocation());
        ContentValues contentValues = new ContentValues();
        contentValues.put("photo_name", photo.getPhotoName());
        contentValues.put("fl_photo_location", photo.getFolderLockPhotoLocation());
        contentValues.put("original_photo_location", photo.getOriginalPhotoLocation());
        contentValues.put("album_id", Integer.valueOf(photo.getAlbumId()));
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        contentValues.put("FileSize", Long.valueOf(file.length()));
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.insert("tbl_photos", null, contentValues);
    }

    public List<Photo> GetPhotos() {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_photos where IsFakeAccount =");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" ORDER BY CreatedTime DESC");
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            Photo photo = new Photo();
            photo.setId(rawQuery.getInt(0));
            photo.setPhotoName(rawQuery.getString(1));
            photo.setFolderLockPhotoLocation(rawQuery.getString(2));
            photo.setOriginalPhotoLocation(rawQuery.getString(3));
            photo.setAlbumId(rawQuery.getInt(4));
            photo.setDateTime(rawQuery.getString(6));
            photo.set_modifiedDateTime(rawQuery.getString(8));
            arrayList.add(photo);
        }
        rawQuery.close();
        return arrayList;
    }

    public List<Photo> GetPhotoByAlbumId(int i, int i2) {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_photos where album_id = ");
        sb.append(i);
        String sb2 = sb.toString();
        if (SortBy.Time.ordinal() == i2) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("SELECT * FROM tbl_photos where album_id = ");
            sb3.append(i);
            sb3.append(" ORDER BY ModifiedDateTime DESC");
            sb2 = sb3.toString();
        } else if (SortBy.Name.ordinal() == i2) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append("SELECT * FROM tbl_photos where album_id = ");
            sb4.append(i);
            sb4.append(" ORDER BY photo_name COLLATE NOCASE ASC");
            sb2 = sb4.toString();
        } else if (SortBy.Size.ordinal() == i2) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append("SELECT * FROM tbl_photos where album_id = ");
            sb5.append(i);
            sb5.append(" ORDER BY FileSize ASC");
            sb2 = sb5.toString();
        }
        Cursor rawQuery = this.database.rawQuery(sb2, null);
        while (rawQuery.moveToNext()) {
            Photo photo = new Photo();
            photo.setId(rawQuery.getInt(0));
            photo.setPhotoName(rawQuery.getString(1));
            photo.setFolderLockPhotoLocation(rawQuery.getString(2));
            photo.setOriginalPhotoLocation(rawQuery.getString(3));
            photo.setAlbumId(rawQuery.getInt(4));
            photo.set_modifiedDateTime(rawQuery.getString(8));
            photo.SetFileCheck(false);
            arrayList.add(photo);
        }
        rawQuery.close();
        return arrayList;
    }

    public List<Photo> GetPhotoWithouThumbnailByAlbumId(int i) {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_photos where album_id = ");
        sb.append(i);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            Photo photo = new Photo();
            photo.setId(rawQuery.getInt(0));
            photo.setPhotoName(rawQuery.getString(1));
            photo.setFolderLockPhotoLocation(rawQuery.getString(2));
            photo.setOriginalPhotoLocation(rawQuery.getString(3));
            photo.setAlbumId(rawQuery.getInt(4));
            photo.SetFileCheck(false);
            arrayList.add(photo);
        }
        rawQuery.close();
        return arrayList;
    }

    public int GetPhotoCountByAlbumId(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_photos where album_id = ");
        sb.append(i);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        int i2 = 0;
        while (rawQuery.moveToNext()) {
            i2++;
        }
        rawQuery.close();
        return i2;
    }

    public boolean CheckAllPhotoIsInSDCard() {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_photos where IsSDCard == 0", null);
        if (rawQuery.moveToNext()) {
            return false;
        }
        rawQuery.close();
        return true;
    }

    public boolean CheckAllPhotoIsInPhone() {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_photos where IsSDCard == 1", null);
        if (rawQuery.moveToNext()) {
            return true;
        }
        rawQuery.close();
        return false;
    }

    public String[] GetPhotoPaths(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_photos where album_id = ");
        sb.append(i);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        String[] strArr = new String[rawQuery.getCount()];
        int i2 = 0;
        while (rawQuery.moveToNext()) {
            strArr[i2] = rawQuery.getString(2);
            i2++;
        }
        rawQuery.close();
        return strArr;
    }

    public Photo GetCoverPhoto(int i) {
        Photo photo = new Photo();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_photos where album_id = ");
        sb.append(i);
        sb.append(" ORDER BY RANDOM() limit 1");
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            photo.setId(rawQuery.getInt(0));
            photo.setPhotoName(rawQuery.getString(1));
            photo.setFolderLockPhotoLocation(rawQuery.getString(2));
            photo.setOriginalPhotoLocation(rawQuery.getString(3));
            photo.setAlbumId(rawQuery.getInt(4));
        }
        rawQuery.close();
        return photo;
    }

    public boolean IsPhotoInAlbum(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_photos where album_id = ");
        sb.append(i);
        sb.append(" ORDER BY RANDOM() limit 1");
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        if (rawQuery.moveToNext()) {
            return true;
        }
        rawQuery.close();
        return false;
    }

    public Photo GetPhoto(String str) {
        Photo photo = new Photo();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_photos where album_id = ");
        sb.append(str);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            photo.setId(rawQuery.getInt(0));
            photo.setPhotoName(rawQuery.getString(1));
            photo.setFolderLockPhotoLocation(rawQuery.getString(2));
            photo.setOriginalPhotoLocation(rawQuery.getString(3));
            photo.setAlbumId(rawQuery.getInt(4));
        }
        rawQuery.close();
        return photo;
    }

    public Photo GetPhotoToUnhide(String str) {
        Photo photo = new Photo();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_photos where photo_name = '");
        sb.append(str);
        sb.append("'");
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            photo.setId(rawQuery.getInt(0));
            photo.setPhotoName(rawQuery.getString(1));
            photo.setFolderLockPhotoLocation(rawQuery.getString(2));
            photo.setOriginalPhotoLocation(rawQuery.getString(3));
            photo.setAlbumId(rawQuery.getInt(4));
        }
        rawQuery.close();
        return photo;
    }

    public void DeletePhoto(Photo photo) {
        this.database.delete("tbl_photos", "_id = ?", new String[]{String.valueOf(photo.getId())});
        close();
    }

    public void DeleteAllPhotos() {
        OpenWrite();
        this.database.execSQL("delete from tbl_photos");
        close();
    }

    public void DeletePhotoById(int i) {
        this.database.delete("tbl_photos", "_id = ?", new String[]{String.valueOf(i)});
        close();
    }

    public void DeletePhotoByAlbumId(int i) {
        for (Photo photo : GetPhotoByAlbumId(i, 4)) {
            this.database.delete("tbl_photos", "_id = ?", new String[]{String.valueOf(photo.getId())});
            File file = new File(photo.getFolderLockPhotoLocation());
            if (file.exists()) {
                file.delete();
            }
        }
        close();
    }

    public String[] GetAlbumNames(int i) {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_photo_albums where _id != ");
        sb.append(i);
        sb.append(" AND IsFakeAccount =");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        String[] strArr = new String[rawQuery.getCount()];
        int i2 = 0;
        while (rawQuery.moveToNext()) {
            strArr[i2] = rawQuery.getString(1);
            i2++;
            Globals globals = new Globals();
            globals.SetAlbumName(rawQuery.getString(1));
            arrayList.add(globals);
        }
        rawQuery.close();
        return strArr;
    }

    public List<String> GetMoveAlbumNames(int i) {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_photo_albums where _id != ");
        sb.append(i);
        sb.append(" AND IsFakeAccount =");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            arrayList.add(rawQuery.getString(1));
        }
        rawQuery.close();
        return arrayList;
    }

    public void UpdatePhotoLocation(Photo photo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("fl_photo_location", photo.getFolderLockPhotoLocation());
        contentValues.put("album_id", Integer.valueOf(photo.getAlbumId()));
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.update("tbl_photos", contentValues, "photo_name = ?", new String[]{String.valueOf(photo.getPhotoName())});
        close();
    }

    public void UpdateAlbumPhotoLocation(int i, String str) {
        String str2;
        for (Photo photo : GetPhotoByAlbumId(i, 4)) {
            ContentValues contentValues = new ContentValues();
            if (photo.getPhotoName().contains("#")) {
                str2 = photo.getPhotoName();
            } else {
                str2 = Utilities.ChangeFileExtention(photo.getPhotoName());
            }
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("/");
            sb.append(str2);
            contentValues.put("fl_photo_location", sb.toString());
            contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
            this.database.update("tbl_photos", contentValues, "_id = ?", new String[]{String.valueOf(photo.getId())});
        }
        close();
    }

    public void UpdatePhotosLocation(int i, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("fl_photo_location", str);
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.update("tbl_photos", contentValues, "_id = ?", new String[]{String.valueOf(i)});
        close();
    }

    public boolean IsFileAlreadyExist(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_photos where fl_photo_location ='");
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
}
