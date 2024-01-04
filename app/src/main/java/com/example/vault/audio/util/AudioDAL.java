package com.example.vault.audio.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.vault.audio.AudioActivity.SortBy;
import com.example.vault.audio.model.AudioEnt;
import com.example.vault.dbhelper.DatabaseHelper;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.utilities.Utilities;

public class AudioDAL {
    Context con;
    SQLiteDatabase database;
    DatabaseHelper helper;

    public AudioDAL(Context context) {
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

    public void AddAudio(AudioEnt audioEnt, String str) {
        File file = new File(audioEnt.getFolderLockAudioLocation());
        ContentValues contentValues = new ContentValues();
        contentValues.put("AudioName", audioEnt.getAudioName());
        contentValues.put("FlAudioLocation", str);
        contentValues.put("OriginalAudioLocation", audioEnt.getFolderLockAudioLocation());
        contentValues.put("PlayListId", Integer.valueOf(audioEnt.getPlayListId()));
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        contentValues.put("FileSize", Long.valueOf(file.length()));
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        long i = database.insert("tbl_Audio", null, contentValues);

        Log.e("insertaudio", "" + i);
    }

    public List<AudioEnt> GetAllAudios() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Audio ORDER BY Id", null);
        while (rawQuery.moveToNext()) {
            AudioEnt audioEnt = new AudioEnt();
            audioEnt.setId(rawQuery.getInt(0));
            audioEnt.setAudioName(rawQuery.getString(1));
            audioEnt.setFolderLockAudioLocation(rawQuery.getString(2));
            audioEnt.setOriginalAudioLocation(rawQuery.getString(3));
            audioEnt.setPlayListId(rawQuery.getInt(6));
            audioEnt.SetFileCheck(false);
            arrayList.add(audioEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public int GetTotalCount() {
        OpenRead();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Audio", null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i++;
        }
        rawQuery.close();
        close();
        return i;
    }

    public List<AudioEnt> GetAudiosByAlbumId(int i, int i2) {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_Audio where PlayListId = ");
        sb.append(i);
        String sb2 = sb.toString();
        if (SortBy.Time.ordinal() == i2) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("SELECT * FROM tbl_Audio where PlayListId = ");
            sb3.append(i);
            sb3.append(" ORDER BY ModifiedDateTime DESC");
            sb2 = sb3.toString();
        } else if (SortBy.Name.ordinal() == i2) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append("SELECT * FROM tbl_Audio where PlayListId = ");
            sb4.append(i);
            sb4.append(" ORDER BY AudioName COLLATE NOCASE ASC");
            sb2 = sb4.toString();
        } else if (SortBy.Size.ordinal() == i2) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append("SELECT * FROM tbl_Audio where PlayListId = ");
            sb5.append(i);
            sb5.append(" ORDER BY FileSize ASC");
            sb2 = sb5.toString();
        }
        Cursor rawQuery = this.database.rawQuery(sb2, null);
        while (rawQuery.moveToNext()) {
            AudioEnt audioEnt = new AudioEnt();
            audioEnt.setId(rawQuery.getInt(0));
            audioEnt.setAudioName(rawQuery.getString(1));
            audioEnt.setFolderLockAudioLocation(rawQuery.getString(2));
            audioEnt.setOriginalAudioLocation(rawQuery.getString(3));
            audioEnt.setPlayListId(rawQuery.getInt(6));
            audioEnt.SetFileCheck(false);
            audioEnt.set_modifiedDateTime(rawQuery.getString(8));
            arrayList.add(audioEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public List<AudioEnt> GetAudios(int i) {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_Audio Where PlayListId =");
        sb.append(i);
        sb.append(" AND IsFakeAccount =");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" ORDER BY Id");
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            AudioEnt audioEnt = new AudioEnt();
            audioEnt.setId(rawQuery.getInt(0));
            audioEnt.setAudioName(rawQuery.getString(1));
            audioEnt.setFolderLockAudioLocation(rawQuery.getString(2));
            audioEnt.setOriginalAudioLocation(rawQuery.getString(3));
            audioEnt.setPlayListId(rawQuery.getInt(6));
            audioEnt.SetFileCheck(false);
            arrayList.add(audioEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public String[] GetPlayListNames(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tblAudioPlayList where Id != ");
        sb.append(i);
        sb.append(" AND IsFakeAccount =");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        String[] strArr = new String[rawQuery.getCount()];
        int i2 = 0;
        while (rawQuery.moveToNext()) {
            strArr[i2] = rawQuery.getString(1);
            i2++;
        }
        rawQuery.close();
        return strArr;
    }

    public List<String> GetMovePlayListNames(int i) {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tblAudioPlayList where Id != ");
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

    public AudioEnt GetAudio(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_Audio Where Id = ");
        sb.append(str);
        sb.append(" AND IsFakeAccount = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        AudioEnt audioEnt = new AudioEnt();
        while (rawQuery.moveToNext()) {
            audioEnt.setId(rawQuery.getInt(0));
            audioEnt.setAudioName(rawQuery.getString(1));
            audioEnt.setFolderLockAudioLocation(rawQuery.getString(2));
            audioEnt.setOriginalAudioLocation(rawQuery.getString(3));
            audioEnt.setPlayListId(rawQuery.getInt(6));
            audioEnt.SetFileCheck(false);
        }
        rawQuery.close();
        return audioEnt;
    }

    public void DeleteAllAudios() {
        OpenWrite();
        this.database.execSQL("delete from tbl_Audio");
        close();
    }

    public void DeleteAudio(AudioEnt audioEnt) {
        OpenWrite();
        this.database.delete("tbl_Audio", "Id = ?", new String[]{String.valueOf(audioEnt.getId())});
        close();
    }

    public void DeleteAudioById(int i) {
        OpenWrite();
        this.database.delete("tbl_Audio", "Id = ?", new String[]{String.valueOf(i)});
        close();
    }

    public void DeleteAudios(int i) {
        for (AudioEnt audioEnt : GetAudiosByPlayListId(i)) {
            this.database.delete("tbl_Audio", "Id = ?", new String[]{String.valueOf(audioEnt.getId())});
            File file = new File(audioEnt.getFolderLockAudioLocation());
            if (file.exists()) {
                file.delete();
            }
        }
        close();
    }

    public List<AudioEnt> GetAudiosByPlayListId(int i) {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_Audio where PlayListId = ");
        sb.append(i);
        sb.append(" ORDER BY Id");
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        while (rawQuery.moveToNext()) {
            AudioEnt audioEnt = new AudioEnt();
            audioEnt.setId(rawQuery.getInt(0));
            audioEnt.setAudioName(rawQuery.getString(1));
            audioEnt.setFolderLockAudioLocation(rawQuery.getString(2));
            audioEnt.setOriginalAudioLocation(rawQuery.getString(3));
            audioEnt.setPlayListId(rawQuery.getInt(6));
            audioEnt.SetFileCheck(false);
            arrayList.add(audioEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public int GetAudiosCountByFolderId(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_Audio where PlayListId = ");
        sb.append(i);
        Cursor rawQuery = this.database.rawQuery(sb.toString(), null);
        int i2 = 0;
        while (rawQuery.moveToNext()) {
            i2++;
        }
        rawQuery.close();
        return i2;
    }

    public void UpdateAudioPlayListLocation(int i, String str) {
        String str2;
        for (AudioEnt audioEnt : GetAudiosByPlayListId(i)) {
            ContentValues contentValues = new ContentValues();
            if (audioEnt.getAudioName().contains("#")) {
                str2 = audioEnt.getAudioName();
            } else {
                str2 = Utilities.ChangeFileExtention(audioEnt.getAudioName());
            }
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("/");
            sb.append(str2);
            contentValues.put("FlAudioLocation", sb.toString());
            this.database.update("tbl_Audio", contentValues, "Id = ?", new String[]{String.valueOf(audioEnt.getId())});
        }
        close();
    }

    public void UpdateAudioLocationById(AudioEnt audioEnt) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("FlAudioLocation", audioEnt.getFolderLockAudioLocation());
        contentValues.put("PlayListId", Integer.valueOf(audioEnt.getPlayListId()));
        this.database.update("tbl_Audio", contentValues, "Id = ?", new String[]{String.valueOf(audioEnt.getId())});
        close();
    }

    public void UpdateAudiosLocation(int i, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("FlAudioLocation", str);
        this.database.update("tbl_Audio", contentValues, "Id = ?", new String[]{String.valueOf(i)});
        close();
    }

    public void UpdateAudiosLocationById(AudioEnt audioEnt) {
        String str;
        ContentValues contentValues = new ContentValues();
        contentValues.put("FlAudioLocation", audioEnt.getFolderLockAudioLocation());
        if (audioEnt.getAudioName().contains("#")) {
            str = Utilities.ChangeFileExtentionToOrignal(audioEnt.getAudioName());
        } else {
            str = audioEnt.getAudioName();
        }
        contentValues.put("AudioName", str);
        this.database.update("tbl_Audio", contentValues, "Id = ?", new String[]{String.valueOf(audioEnt.getId())});
        close();
    }

    public void UpdateAudiosLocation(AudioEnt audioEnt) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("FlAudioLocation", audioEnt.getFolderLockAudioLocation());
        contentValues.put("PlayListId", Integer.valueOf(audioEnt.getPlayListId()));
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.update("tbl_Audio", contentValues, "Id = ?", new String[]{String.valueOf(audioEnt.getId())});
        close();
    }

    public boolean CheckAllFilesIsInSDCard() {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Audio where IsSDCard == 0", null);
        if (rawQuery.moveToNext()) {
            return false;
        }
        rawQuery.close();
        return true;
    }

    public boolean CheckAllFilesIsInPhone() {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Audio where IsSDCard == 1", null);
        if (rawQuery.moveToNext()) {
            return true;
        }
        rawQuery.close();
        return false;
    }

    public boolean IsFileAlreadyExist(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM tbl_Audio where FlAudioLocation ='");
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
