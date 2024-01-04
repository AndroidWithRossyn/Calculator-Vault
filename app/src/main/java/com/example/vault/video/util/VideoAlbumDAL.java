package com.example.vault.video.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import com.example.vault.dbhelper.DatabaseHelper;
import com.example.vault.photo.PhotosAlbumActivty.SortBy;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;
import com.example.vault.video.model.VideoAlbum;

public class VideoAlbumDAL {
    Context con;
    SQLiteDatabase database;
    DatabaseHelper helper;

    public VideoAlbumDAL(Context context) {
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

    public void AddVideoAlbum(VideoAlbum videoAlbum) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("album_name", videoAlbum.getAlbumName());
        contentValues.put("fl_album_location", videoAlbum.getAlbumLocation());
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        contentValues.put("SortBy", Integer.valueOf(0));
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.insert("tbl_video_albums", null, contentValues);
    }

    public List<VideoAlbum> GetAlbums(int i) {
        ArrayList arrayList = new ArrayList();
        VideoDAL videoDAL = new VideoDAL(this.con);
        videoDAL.OpenRead();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_video_albums Where IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" ORDER BY _id");
        String sb2 = sb.toString();
        if (SortBy.Time.ordinal() == i) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("SELECT * FROM tbl_video_albums Where IsFakeAccount = ");
            sb3.append(SecurityLocksCommon.IsFakeAccount);
            sb3.append(" ORDER BY ModifiedDateTime DESC");
            sb2 = sb3.toString();
        } else if (SortBy.Name.ordinal() == i) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append("SELECT * FROM tbl_video_albums Where IsFakeAccount = ");
            sb4.append(SecurityLocksCommon.IsFakeAccount);
            sb4.append(" ORDER BY album_name COLLATE NOCASE ASC");
            sb2 = sb4.toString();
        }
        Cursor rawQuery = this.database.rawQuery(sb2, null);
        while (rawQuery.moveToNext()) {
            VideoAlbum videoAlbum = new VideoAlbum();
            videoAlbum.setId(rawQuery.getInt(0));
            videoAlbum.setAlbumName(rawQuery.getString(1));
            videoAlbum.setAlbumLocation(rawQuery.getString(2));
            videoAlbum.set_modifiedDateTime(rawQuery.getString(6));
            videoAlbum.setVideoCount(videoDAL.GetVideoCountByAlbumId(rawQuery.getInt(0)));
            arrayList.add(videoAlbum);
        }
        rawQuery.close();
        videoDAL.close();
        return arrayList;
    }

    public int GetSortByAlbumId(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT SortBy FROM tbl_video_albums where _id = ");
        sb.append(i);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        int i2 = 0;
        while (rawQuery.moveToNext()) {
            i2 = rawQuery.getInt(0);
        }
        rawQuery.close();
        return i2;
    }

    public void AddSortByInVideoAlbum(int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("SortBy", Integer.valueOf(i));
        this.database.update("tbl_video_albums", contentValues, "_id = ?", new String[]{String.valueOf(Common.FolderId)});
        close();
    }

    public int GetAlbumsCount() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_video_albums Where IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" ORDER BY _id");
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i++;
        }
        rawQuery.close();
        return i;
    }

    public VideoAlbum GetAlbum(String str) {
        VideoAlbum videoAlbum = new VideoAlbum();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_video_albums where album_name = '");
        sb.append(str);
        sb.append("' AND IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            videoAlbum.setId(rawQuery.getInt(0));
            videoAlbum.setAlbumName(rawQuery.getString(1));
            videoAlbum.setAlbumLocation(rawQuery.getString(2));
            videoAlbum.set_modifiedDateTime(rawQuery.getString(6));
        }
        rawQuery.close();
        return videoAlbum;
    }

    public VideoAlbum GetAlbumById(int i) {
        VideoAlbum videoAlbum = new VideoAlbum();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_video_albums where _id = '");
        sb.append(i);
        sb.append("' AND IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            videoAlbum.setId(rawQuery.getInt(0));
            videoAlbum.setAlbumName(rawQuery.getString(1));
            videoAlbum.setAlbumLocation(rawQuery.getString(2));
            videoAlbum.set_modifiedDateTime(rawQuery.getString(6));
        }
        rawQuery.close();
        return videoAlbum;
    }

    public VideoAlbum GetAlbumById(String str) {
        VideoAlbum videoAlbum = new VideoAlbum();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_video_albums where _id = '");
        sb.append(str);
        sb.append("' AND IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            videoAlbum.setId(rawQuery.getInt(0));
            videoAlbum.setAlbumName(rawQuery.getString(1));
            videoAlbum.setAlbumLocation(rawQuery.getString(2));
            videoAlbum.set_modifiedDateTime(rawQuery.getString(6));
        }
        rawQuery.close();
        return videoAlbum;
    }

    public String GetAlbumName(String str) {
        String str2 = "My Videos";
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_video_albums where album_name = '");
        sb.append(str2);
        sb.append("' AND IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            str2 = rawQuery.getString(1);
        }
        rawQuery.close();
        return str2;
    }

    public void UpdateAlbumLocation(int i, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("fl_album_location", str);
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.update("tbl_video_albums", contentValues, "_id = ?", new String[]{String.valueOf(i)});
        close();
    }

    public void UpdateAlbumPath(int i, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("fl_album_location", str);
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.update("tbl_video_albums", contentValues, "_id = ?", new String[]{String.valueOf(i)});
        close();
        VideoDAL videoDAL = new VideoDAL(this.con);
        videoDAL.OpenWrite();
        videoDAL.UpdateAlbumVideoLocation(i, str);
        videoDAL.close();
    }

    public void UpdateAlbumName(VideoAlbum videoAlbum) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("album_name", videoAlbum.getAlbumName());
        contentValues.put("fl_album_location", videoAlbum.getAlbumLocation());
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.update("tbl_video_albums", contentValues, "_id = ?", new String[]{String.valueOf(videoAlbum.getId())});
        close();
        VideoDAL videoDAL = new VideoDAL(this.con);
        videoDAL.OpenWrite();
        videoDAL.UpdateAlbumVideoLocation(videoAlbum.getId(), videoAlbum.getAlbumLocation());
        videoDAL.close();
    }

    public void DeleteAlbum(VideoAlbum videoAlbum) {
        this.database.delete("tbl_video_albums", "_id = ?", new String[]{String.valueOf(videoAlbum.getId())});
        close();
    }

    public void DeleteAlbumById(int i) {
        this.database.delete("tbl_video_albums", "_id = ?", new String[]{String.valueOf(i)});
        close();
        VideoDAL videoDAL = new VideoDAL(this.con);
        videoDAL.OpenWrite();
        videoDAL.DeleteVideoByAlbumId(i);
        videoDAL.close();
    }

    public String GetFolderName(String str) {
        String str2 = "";
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_video_albums where _id =");
        sb.append(str);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            str2 = rawQuery.getString(1);
        }
        rawQuery.close();
        return str2;
    }

    public int GetLastAlbumId() {
        Cursor rawQuery = this.database.rawQuery("SELECT _id FROM tbl_video_albums WHERE _id = (SELECT MAX(_id)  FROM tbl_video_albums)", null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i = rawQuery.getInt(0);
        }
        rawQuery.close();
        return i;
    }

    public int IfAlbumNameExistReturnId(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_video_albums where album_name ='");
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
}
