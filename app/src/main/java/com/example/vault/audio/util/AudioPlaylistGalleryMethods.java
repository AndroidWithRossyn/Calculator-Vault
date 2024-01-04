package com.example.vault.audio.util;

import android.content.Context;

import com.example.vault.audio.model.AudioPlayListEnt;
import com.example.vault.audio.util.AudioPlayListDAL;
import com.example.vault.storageoption.StorageOptionsCommon;

public class AudioPlaylistGalleryMethods {
    public void AddPlaylistToDatabase(Context context, String str) {
        AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
        audioPlayListEnt.setPlayListName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.AUDIOS);
        sb.append(str);
        audioPlayListEnt.setPlayListLocation(sb.toString());
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(context);
        try {
            audioPlayListDAL.OpenWrite();
            audioPlayListDAL.AddAudioPlayList(audioPlayListEnt);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            audioPlayListDAL.close();
            throw th;
        }
        audioPlayListDAL.close();
    }

    public void UpdatePlaylistInDatabase(Context context, int i, String str) {
        AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
        audioPlayListEnt.setId(i);
        audioPlayListEnt.setPlayListName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.AUDIOS);
        sb.append(str);
        audioPlayListEnt.setPlayListLocation(sb.toString());
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(context);
        try {
            audioPlayListDAL.OpenWrite();
            audioPlayListDAL.UpdatePlayListName(audioPlayListEnt);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            audioPlayListDAL.close();
            throw th;
        }
        audioPlayListDAL.close();
    }

    public void UpdatePlaylistLocation(AudioPlayListEnt audioPlayListEnt, Context context, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.AUDIOS);
        sb.append(str);
        audioPlayListEnt.setPlayListLocation(sb.toString());
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(context);
        try {
            audioPlayListDAL.OpenWrite();
            audioPlayListDAL.UpdatePlayListLocationOnly(audioPlayListEnt);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            audioPlayListDAL.close();
            throw th;
        }
        audioPlayListDAL.close();
    }
}
