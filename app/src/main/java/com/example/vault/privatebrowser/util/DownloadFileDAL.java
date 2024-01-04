package com.example.vault.privatebrowser.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.example.vault.Flaes;
import com.example.vault.R;
import com.example.vault.audio.util.AudioDAL;
import com.example.vault.audio.model.AudioEnt;
import com.example.vault.dbhelper.DatabaseHelper;
import com.example.vault.documents.util.DocumentDAL;
import com.example.vault.documents.model.DocumentsEnt;
import com.example.vault.photo.model.Photo;
import com.example.vault.photo.util.PhotoDAL;
import com.example.vault.privatebrowser.model.DownloadFileEnt;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Common.DownloadStatus;
import com.example.vault.utilities.Common.DownloadType;
import com.example.vault.utilities.Utilities;
import com.example.vault.video.model.Video;
import com.example.vault.video.util.VideoDAL;

public class DownloadFileDAL {
    Context con;
    SQLiteDatabase database;
    DatabaseHelper helper;

    public DownloadFileDAL(Context context) {
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

    public void AddDownloadFile(DownloadFileEnt downloadFileEnt) {
        if (downloadFileEnt.GetFileName().contains(".")) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("FileDownloadPath", downloadFileEnt.GetFileDownloadPath());
            contentValues.put("FileName", downloadFileEnt.GetFileName());
            contentValues.put("ReferenceId", downloadFileEnt.GetReferenceId());
            contentValues.put("Status", Integer.valueOf(downloadFileEnt.GetStatus()));
            contentValues.put("DownloadFileUrl", downloadFileEnt.GetDownloadFileUrl());
            contentValues.put("DownloadType", Integer.valueOf(downloadFileEnt.GetDownloadType()));
            contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
            this.database.insert("tbl_DownloadFile", null, contentValues);
            return;
        }
        Toast.makeText(this.con, R.string.toast_browser_filenotdownload, Toast.LENGTH_LONG).show();
        new File(downloadFileEnt.GetFileDownloadPath()).delete();
    }

    public List<DownloadFileEnt> GetDownloadFiles() {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_DownloadFile WHERE Status = ");
        sb.append(DownloadStatus.InProgress.ordinal());
        sb.append(" AND IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            DownloadFileEnt downloadFileEnt = new DownloadFileEnt();
            downloadFileEnt.SetId(rawQuery.getInt(0));
            downloadFileEnt.SetFileDownloadPath(rawQuery.getString(1));
            downloadFileEnt.SetFileName(rawQuery.getString(2));
            downloadFileEnt.SetReferenceId(rawQuery.getString(3));
            downloadFileEnt.SetStatus(rawQuery.getInt(4));
            downloadFileEnt.SetDownloadFileUrl(rawQuery.getString(5));
            downloadFileEnt.SetDownloadType(rawQuery.getInt(6));
            arrayList.add(downloadFileEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public DownloadFileEnt GetDownloadFile(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_DownloadFile Where Id = ");
        sb.append(str);
        sb.append(" AND IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        DownloadFileEnt downloadFileEnt = new DownloadFileEnt();
        while (rawQuery.moveToNext()) {
            downloadFileEnt.SetId(rawQuery.getInt(0));
            downloadFileEnt.SetFileDownloadPath(rawQuery.getString(1));
            downloadFileEnt.SetFileName(rawQuery.getString(2));
            downloadFileEnt.SetReferenceId(rawQuery.getString(3));
            downloadFileEnt.SetStatus(rawQuery.getInt(4));
            downloadFileEnt.SetDownloadFileUrl(rawQuery.getString(5));
            downloadFileEnt.SetDownloadType(rawQuery.getInt(6));
        }
        rawQuery.close();
        return downloadFileEnt;
    }

    public void DeleteDownloadFile(DownloadFileEnt downloadFileEnt) {
        OpenWrite();
        this.database.delete("tbl_DownloadFile", "Id = ?", new String[]{String.valueOf(downloadFileEnt.GetId())});
        close();
    }

    public void DeleteDownloadFile() {
        OpenWrite();
        this.database.delete("tbl_DownloadFile", "Status = ?", new String[]{String.valueOf(DownloadStatus.Completed.ordinal())});
        close();
    }

    public List<String> GetDownloadFileName() {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_DownloadFile Where IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            arrayList.add(rawQuery.getString(2));
        }
        rawQuery.close();
        return arrayList;
    }

    public void DeleteDownloadFileAll() {
        OpenWrite();
        this.database.delete("tbl_DownloadFile", "IsFakeAccount = ?", new String[]{String.valueOf(SecurityLocksCommon.IsFakeAccount)});
        close();
    }

    public void UpdateDownloadFile(DownloadFileEnt downloadFileEnt) throws IOException {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Status", Integer.valueOf(DownloadStatus.Completed.ordinal()));
        this.database.update("tbl_DownloadFile", contentValues, "Id = ?", new String[]{String.valueOf(downloadFileEnt.GetId())});
        close();
        String GetFileDownloadPath = downloadFileEnt.GetFileDownloadPath();
        if (!downloadFileEnt.GetFileDownloadPath().contains(".")) {
            return;
        }
        if (downloadFileEnt.GetDownloadType() == DownloadType.Photo.ordinal()) {
            String MovePhotoFile = MovePhotoFile(downloadFileEnt.GetFileDownloadPath(), downloadFileEnt.GetFileName());
            if (MovePhotoFile.length() > 0) {
                AddPhotoToDatabase(downloadFileEnt.GetFileName(), MovePhotoFile);
            }
        } else if (downloadFileEnt.GetDownloadType() == DownloadType.Video.ordinal()) {
            String GetThumnil = GetThumnil(downloadFileEnt.GetFileDownloadPath(), downloadFileEnt.GetFileName());
            String MoveVideoFile = MoveVideoFile(downloadFileEnt.GetFileDownloadPath(), downloadFileEnt.GetFileName());
            if (MoveVideoFile.length() > 0) {
                AddVideoToDatabase(downloadFileEnt.GetFileName(), MoveVideoFile, GetThumnil);
            }
        } else if (downloadFileEnt.GetDownloadType() == DownloadType.Music.ordinal()) {
            String MoveMusicFile = MoveMusicFile(downloadFileEnt.GetFileDownloadPath(), downloadFileEnt.GetFileName());
            if (MoveMusicFile.length() > 0) {
                AddAudioToDatabase(downloadFileEnt.GetFileName(), MoveMusicFile);
            }
        } else if (downloadFileEnt.GetDownloadType() == DownloadType.Document.ordinal()) {
            String MoveDocumentFile = MoveDocumentFile(downloadFileEnt.GetFileDownloadPath(), downloadFileEnt.GetFileName());
            if (MoveDocumentFile.length() > 0) {
                AddDocumentToDatabase(downloadFileEnt.GetFileName(), MoveDocumentFile);
            }
        } else {
            Toast.makeText(this.con, R.string.toast_browser_filenotdownload, Toast.LENGTH_LONG).show();
            new File(GetFileDownloadPath).delete();
        }
    }

    public String MovePhotoFile(String str, String str2) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.PHOTOS);
        sb.append("My Photos");
        File file = new File(sb.toString());
        String NSHideFile = Utilities.NSHideFile(this.con, new File(str), file);
        Utilities.NSEncryption(new File(NSHideFile));
        return NSHideFile;
    }

    private String MoveVideoFile(String str, String str2) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.VIDEOS);
        sb.append("My Videos");
        File file = new File(sb.toString());
        return Utilities.NSHideFile(this.con, new File(str), file);
    }

    public String MoveMusicFile(String str, String str2) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.AUDIOS);
        File file = new File(sb.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(file, Utilities.ChangeFileExtention(str2));
        File file3 = new File(str);
        Flaes.encryptUsingCipherStream_AES128(file3, file2);
        if (file3.exists()) {
            file3.delete();
        }
        return file2.getAbsolutePath();
    }

    public String MoveDocumentFile(String str, String str2) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.DOCUMENTS);
        sb.append("My Documents");
        File file = new File(sb.toString());
        String NSHideFile = Utilities.NSHideFile(this.con, new File(str), file);
        Utilities.NSEncryption(new File(NSHideFile));
        return NSHideFile;
    }

    public void AddPhotoToDatabase(String str, String str2) {
        Photo photo = new Photo();
        photo.setPhotoName(str);
        photo.setFolderLockPhotoLocation(str2);
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append("/");
        sb.append(str);
        photo.setOriginalPhotoLocation(sb.toString());
        photo.setAlbumId(Common.FolderId);
        PhotoDAL photoDAL = new PhotoDAL(this.con);
        try {
            photoDAL.OpenWrite();
            photoDAL.AddPhotos(photo);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            photoDAL.close();
            throw th;
        }
        photoDAL.close();
    }

    private String GetThumnil(String str, String str2) {
        Bitmap createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(str, 1);
        StringBuilder sb = new StringBuilder();
        sb.append(this.con.getFilesDir().getAbsoluteFile());
        sb.append("/videos_gallery/VideoThumnails/");
        new File(sb.toString()).mkdirs();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.con.getFilesDir().getAbsoluteFile());
        sb2.append("/videos_gallery/VideoThumnails/thumbnil-");
        sb2.append(str2.substring(0, str2.lastIndexOf(".")));
        sb2.append("#jpg");
        String sb3 = sb2.toString();
        File file = new File(sb3);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
        try {
            createVideoThumbnail.compress(CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        return sb3;
    }

    private void AddVideoToDatabase(String str, String str2, String str3) {
        Video video = new Video();
        video.setVideoName(str);
        video.setFolderLockVideoLocation(str2);
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append("/");
        sb.append(str);
        String sb2 = sb.toString();
        video.setthumbnail_video_location(str3);
        try {
            Utilities.NSEncryption(new File(str2));
            Utilities.NSEncryption(new File(str3));
        } catch (IOException e) {
            e.printStackTrace();
        }
        video.setOriginalVideoLocation(sb2);
        video.setAlbumId(Common.FolderId);
        VideoDAL videoDAL = new VideoDAL(this.con);
        try {
            videoDAL.OpenWrite();
            videoDAL.AddVideos(video);
        } catch (Exception e2) {
            System.out.println(e2.getMessage());
        } catch (Throwable th) {
            videoDAL.close();
            throw th;
        }
        videoDAL.close();
    }

    public void AddAudioToDatabase(String str, String str2) {
        AudioEnt audioEnt = new AudioEnt();
        audioEnt.setAudioName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append("/");
        sb.append(str);
        audioEnt.setOriginalAudioLocation(sb.toString());
        audioEnt.setFolderLockAudioLocation(str2);
        audioEnt.setPlayListId(Common.FolderId);
        AudioDAL audioDAL = new AudioDAL(this.con);
        try {
            audioDAL.OpenWrite();
            audioDAL.AddAudio(audioEnt, str2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            audioDAL.close();
            throw th;
        }
        audioDAL.close();
    }

    public void AddDocumentToDatabase(String str, String str2) {
        DocumentsEnt documentsEnt = new DocumentsEnt();
        documentsEnt.setDocumentName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append("/");
        sb.append(str);
        documentsEnt.setOriginalDocumentLocation(sb.toString());
        documentsEnt.setFolderId(Common.FolderId);
        documentsEnt.setFolderLockDocumentLocation(str2);
        DocumentDAL documentDAL = new DocumentDAL(this.con);
        try {
            documentDAL.OpenWrite();
            documentDAL.AddDocuments(documentsEnt, str2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            documentDAL.close();
            throw th;
        }
        documentDAL.close();
    }
}
