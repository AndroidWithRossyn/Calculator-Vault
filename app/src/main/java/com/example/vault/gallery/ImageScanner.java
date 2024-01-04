package com.example.vault.gallery;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import java.util.List;
import com.example.vault.photo.model.Photo;
import com.example.vault.photo.util.PhotoDAL;
import com.example.vault.video.model.Video;
import com.example.vault.video.util.VideoDAL;

public class ImageScanner {
    List<Photo> PhotosList;
    List<Video> VideoList;

    public Context mContext;

    public interface ScanCompleteCallBack {
        void scanComplete(List<Photo> list, List<Video> list2);
    }

    public ImageScanner(Context context) {
        this.mContext = context;
    }

    public void scanImages(final ScanCompleteCallBack scanCompleteCallBack) {
        final Handler r0 = new Handler() {
            public void handleMessage(Message message) {
                super.handleMessage(message);
                scanCompleteCallBack.scanComplete(ImageScanner.this.PhotosList, ImageScanner.this.VideoList);
            }
        };
        new Thread(new Runnable() {
            public void run() {
                PhotoDAL photoDAL = new PhotoDAL(ImageScanner.this.mContext);
                photoDAL.OpenRead();
                ImageScanner.this.PhotosList = photoDAL.GetPhotos();
                photoDAL.close();
                VideoDAL videoDAL = new VideoDAL(ImageScanner.this.mContext);
                videoDAL.OpenRead();
                ImageScanner.this.VideoList = videoDAL.GetVideos();
                videoDAL.close();
                r0.sendMessage(r0.obtainMessage());
            }
        }).start();
    }
}
