package com.example.vault.video.util;

import android.content.Context;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.video.model.VideoAlbum;

public class AlbumsGalleryVideosMethods {
    public void AddAlbumToDatabase(Context context, String str) {
        VideoAlbum videoAlbum = new VideoAlbum();
        videoAlbum.setAlbumName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.VIDEOS);
        sb.append(str);
        videoAlbum.setAlbumLocation(sb.toString());
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(context);
        try {
            videoAlbumDAL.OpenWrite();
            videoAlbumDAL.AddVideoAlbum(videoAlbum);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            videoAlbumDAL.close();
            throw th;
        }
        videoAlbumDAL.close();
    }

    public void UpdateAlbumInDatabase(Context context, int i, String str) {
        VideoAlbum videoAlbum = new VideoAlbum();
        videoAlbum.setId(i);
        videoAlbum.setAlbumName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.VIDEOS);
        sb.append(str);
        videoAlbum.setAlbumLocation(sb.toString());
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(context);
        try {
            videoAlbumDAL.OpenWrite();
            videoAlbumDAL.UpdateAlbumName(videoAlbum);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            videoAlbumDAL.close();
            throw th;
        }
        videoAlbumDAL.close();
    }
}
