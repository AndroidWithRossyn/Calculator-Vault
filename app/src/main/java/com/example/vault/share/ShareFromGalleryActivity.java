package com.example.vault.share;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import com.example.vault.R;
import com.example.vault.common.Constants;
import com.example.vault.photo.model.Photo;
import com.example.vault.photo.model.PhotoAlbum;
import com.example.vault.photo.util.PhotoAlbumDAL;
import com.example.vault.photo.util.PhotoDAL;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.AppSettingsSharedPreferences;
import com.example.vault.storageoption.StorageOptionSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;
import com.example.vault.video.model.VideoAlbum;
import com.example.vault.video.util.VideoAlbumDAL;
import com.example.vault.video.util.VideoDAL;
import org.apache.http.protocol.HTTP;

public class ShareFromGalleryActivity extends Activity {
    protected int AlbumId = 1;
    Intent ImageAndVideosMixintent;
    boolean IsImageAndVideosMix = false;
    boolean IsImageMix = false;
    boolean IsMultipVideoleFile = false;
    boolean IsMultipleFile = false;
    boolean IsSingleFile = false;
    boolean IsSingleVideoFile = false;
    boolean IsVideoMix = false;
    int ListPosition = 0;
    Intent MultipleFileintent;
    Intent MultipleVideoFileintent;
    Intent SingleFileintent;
    Intent SingleVideoFileintent;

    public ShareFromGalleryAdapter adapter;
    GridView albumGridView;
    String dbFolderPath;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 3) {
                if (Common.IsImporting) {
                    if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        ShareFromGalleryActivity shareFromGalleryActivity = ShareFromGalleryActivity.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append(Constants.FILE);
                        sb.append(Environment.getExternalStorageDirectory());
                        shareFromGalleryActivity.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb.toString())));
                    } else {
                        ShareFromGalleryActivity.this.RefershGalleryforKitkat();
                    }
                    Common.IsImporting = false;
                    ShareFromGalleryActivity.this.hideProgress();
                    SecurityLocksCommon.IsAppDeactive = false;
                    if (!ShareFromGalleryActivity.this.IsImageAndVideosMix) {
                        if (ShareFromGalleryActivity.this.IsSingleFile || ShareFromGalleryActivity.this.IsMultipleFile) {
                            Toast.makeText(ShareFromGalleryActivity.this, R.string.toast_Share_from_gallery_sucess_photo, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ShareFromGalleryActivity.this, R.string.toast_Share_from_gallery_sucess_video, Toast.LENGTH_SHORT).show();
                        }
                        ShareFromGalleryActivity.this.finish();
                    } else {
                        Toast.makeText(ShareFromGalleryActivity.this, R.string.toast_Share_from_gallery_sucess_photo, Toast.LENGTH_SHORT).show();
                        ShareFromGalleryActivity shareFromGalleryActivity2 = ShareFromGalleryActivity.this;
                        shareFromGalleryActivity2.ListPosition = 0;
                        shareFromGalleryActivity2.textfoldername.setText(R.string.lbl_Share_from_gallery_selectvideo);
                        ShareFromGalleryActivity.this.GetVideoAlbumsFromDatabase();
                        ShareFromGalleryActivity shareFromGalleryActivity3 = ShareFromGalleryActivity.this;
                        shareFromGalleryActivity3.AlbumId = ((VideoAlbum) shareFromGalleryActivity3.videoAlbums.get(0)).getId();
                    }
                }
            } else if (message.what == 2) {
                ShareFromGalleryActivity.this.hideProgress();
            }
            super.handleMessage(message);
        }
    };
    Uri imageUri;
    ArrayList<Uri> imageUris;
    LinearLayout ll_background;
    ProgressDialog myProgressDialog = null;
    private PhotoAlbumDAL photoAlbumDAL;

    public ArrayList<PhotoAlbum> photoAlbums;
    TextView textfoldername;
    private VideoAlbumDAL videoAlbumDAL;

    public ArrayList<VideoAlbum> videoAlbums;
    Uri videoUri;
    ArrayList<Uri> videoUris;

    private void ShowImportProgress() {
        this.myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }


    public void hideProgress() {
        ProgressDialog progressDialog = this.myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            this.myProgressDialog.dismiss();
        }
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.share_from_gallery);
        Common.applyKitKatTranslucency(this);
        this.albumGridView = (GridView) findViewById(R.id.fileListgrid);
        this.textfoldername = (TextView) findViewById(R.id.lbl_import_title);
        AppSettingsSharedPreferences.GetObject(this);
        Utilities.CheckDeviceStoragePaths(this);
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(4);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(3);
            } else {
                this.albumGridView.setNumColumns(2);
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(5);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(4);
            } else {
                this.albumGridView.setNumColumns(3);
            }
        }
        StorageOptionsCommon.STORAGEPATH = StorageOptionSharedPreferences.GetObject(this).GetStoragePath();
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if ("android.intent.action.SEND".equals(action) && type != null) {
            if (HTTP.PLAIN_TEXT_TYPE.equals(type)) {
                handleSendText(intent);
            }
            if (type.startsWith("image/")) {
                handleSendImage(intent);
            }
            if (type.startsWith("video/")) {
                handleSendVideo(intent);
            }
        }
        if ("android.intent.action.SEND_MULTIPLE".equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent);
            }
            if (type.startsWith("video/")) {
                handleSendMultipleVideos(intent);
            }
            if (type.startsWith("*/*")) {
                handleSendImageAndVideosMix(intent);
            }
        }
        if ("android.intent.action.PICK".equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent);
            }
            if (type.startsWith("video/")) {
                handleSendMultipleVideos(intent);
            }
        }
        this.albumGridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ShareFromGalleryActivity shareFromGalleryActivity = ShareFromGalleryActivity.this;
                shareFromGalleryActivity.ListPosition = i;
                if (shareFromGalleryActivity.IsSingleFile || ShareFromGalleryActivity.this.IsMultipleFile) {
                    ShareFromGalleryActivity shareFromGalleryActivity2 = ShareFromGalleryActivity.this;
                    shareFromGalleryActivity2.AlbumId = ((PhotoAlbum) shareFromGalleryActivity2.photoAlbums.get(i)).getId();
                    ShareFromGalleryActivity shareFromGalleryActivity3 = ShareFromGalleryActivity.this;
                    shareFromGalleryActivity3.adapter = new ShareFromGalleryAdapter(shareFromGalleryActivity3, 17367043, shareFromGalleryActivity3.photoAlbums, ShareFromGalleryActivity.this.ListPosition);
                } else if (ShareFromGalleryActivity.this.IsSingleVideoFile || ShareFromGalleryActivity.this.IsMultipVideoleFile) {
                    ShareFromGalleryActivity shareFromGalleryActivity4 = ShareFromGalleryActivity.this;
                    shareFromGalleryActivity4.AlbumId = ((VideoAlbum) shareFromGalleryActivity4.videoAlbums.get(i)).getId();
                    ShareFromGalleryActivity shareFromGalleryActivity5 = ShareFromGalleryActivity.this;
                    ShareFromGalleryAdapter shareFromGalleryAdapter = new ShareFromGalleryAdapter(shareFromGalleryActivity5, 17367043, shareFromGalleryActivity5.videoAlbums, ShareFromGalleryActivity.this.ListPosition, true);
                    shareFromGalleryActivity5.adapter = shareFromGalleryAdapter;
                } else if (ShareFromGalleryActivity.this.IsImageMix) {
                    ShareFromGalleryActivity shareFromGalleryActivity6 = ShareFromGalleryActivity.this;
                    shareFromGalleryActivity6.AlbumId = ((PhotoAlbum) shareFromGalleryActivity6.photoAlbums.get(i)).getId();
                    ShareFromGalleryActivity shareFromGalleryActivity7 = ShareFromGalleryActivity.this;
                    shareFromGalleryActivity7.adapter = new ShareFromGalleryAdapter(shareFromGalleryActivity7, 17367043, shareFromGalleryActivity7.photoAlbums, ShareFromGalleryActivity.this.ListPosition);
                } else if (ShareFromGalleryActivity.this.IsVideoMix) {
                    ShareFromGalleryActivity shareFromGalleryActivity8 = ShareFromGalleryActivity.this;
                    shareFromGalleryActivity8.AlbumId = ((VideoAlbum) shareFromGalleryActivity8.videoAlbums.get(i)).getId();
                    ShareFromGalleryActivity shareFromGalleryActivity9 = ShareFromGalleryActivity.this;
                    ShareFromGalleryAdapter shareFromGalleryAdapter2 = new ShareFromGalleryAdapter(shareFromGalleryActivity9, 17367043, shareFromGalleryActivity9.videoAlbums, ShareFromGalleryActivity.this.ListPosition, true);
                    shareFromGalleryActivity9.adapter = shareFromGalleryAdapter2;
                }
                ShareFromGalleryActivity.this.albumGridView.setAdapter(ShareFromGalleryActivity.this.adapter);
            }
        });
        if (this.IsImageAndVideosMix) {
            GetPhotoAlbumsFromDatabase();
            this.textfoldername.setText(R.string.lbl_Share_from_gallery_selectphoto);
            return;
        }
        GetAlbumsFromDatabase();
    }

    public void onFileMoveClick(View view) {
        ShowImportProgress();
        new Thread() {
            public void run() {
                try {
                    if (ShareFromGalleryActivity.this.IsImageAndVideosMix) {
                        ShareFromGalleryActivity.this.ImportMix();
                    } else {
                        ShareFromGalleryActivity.this.Import();
                    }
                    Message message = new Message();
                    message.what = 3;
                    ShareFromGalleryActivity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 3;
                    ShareFromGalleryActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }

    public void Import() {
        if (this.IsSingleFile) {
            Common.IsImporting = true;
            this.imageUri = (Uri) this.SingleFileintent.getParcelableExtra("android.intent.extra.STREAM");
            if (this.imageUri != null) {
                PhotoAlbumDAL photoAlbumDAL2 = new PhotoAlbumDAL(getApplicationContext());
                photoAlbumDAL2.OpenRead();
                PhotoAlbum GetAlbumById = photoAlbumDAL2.GetAlbumById(Integer.toString(this.AlbumId));
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append(StorageOptionsCommon.PHOTOS);
                sb.append(GetAlbumById.getAlbumName());
                this.dbFolderPath = sb.toString();
                try {
                    String NSHideFile = Utilities.NSHideFile(this, new File(getRealPathFromURI(this.imageUri)), new File(this.dbFolderPath));
                    Utilities.NSEncryption(new File(NSHideFile));
                    if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(Constants.FILE);
                        sb2.append(Environment.getExternalStorageDirectory());
                        sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb2.toString())));
                    }
                    String realPathFromURI = getRealPathFromURI(this.imageUri);
                    if (NSHideFile.length() > 0) {
                        AddPhotoToDatabase(FileName(realPathFromURI), realPathFromURI, NSHideFile);
                    }
                    if (VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                        intent.setData(Uri.fromFile(new File(realPathFromURI)));
                        sendBroadcast(intent);
                    }
                    File file = new File(realPathFromURI);
                    try {
                        ContentResolver contentResolver = getContentResolver();
                        Uri uri = Media.EXTERNAL_CONTENT_URI;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("_data='");
                        sb3.append(file.getPath());
                        sb3.append("'");
                        contentResolver.delete(uri, sb3.toString(), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        } else {
            if (this.IsMultipleFile) {
                Common.IsImporting = true;
                this.imageUris = this.MultipleFileintent.getParcelableArrayListExtra("android.intent.extra.STREAM");
                this.MultipleFileintent.getDataString();
                if (this.imageUris != null) {
                    PhotoAlbumDAL photoAlbumDAL3 = new PhotoAlbumDAL(getApplicationContext());
                    photoAlbumDAL3.OpenRead();
                    PhotoAlbum GetAlbumById2 = photoAlbumDAL3.GetAlbumById(Integer.toString(this.AlbumId));
                    for (int i = 0; i < this.imageUris.size(); i++) {
                        ((Uri) this.imageUris.get(i)).getPathSegments();
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append(StorageOptionsCommon.STORAGEPATH);
                        sb4.append(StorageOptionsCommon.PHOTOS);
                        sb4.append(GetAlbumById2.getAlbumName());
                        sb4.append("/");
                        this.dbFolderPath = sb4.toString();
                        try {
                            String NSHideFile2 = Utilities.NSHideFile(this, new File(getRealPathFromURI((Uri) this.imageUris.get(i))), new File(this.dbFolderPath));
                            Utilities.NSEncryption(new File(NSHideFile2));
                            if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                                StringBuilder sb5 = new StringBuilder();
                                sb5.append(Constants.FILE);
                                sb5.append(Environment.getExternalStorageDirectory());
                                sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb5.toString())));
                            }
                            String realPathFromURI2 = getRealPathFromURI((Uri) this.imageUris.get(i));
                            if (NSHideFile2.length() > 0) {
                                AddPhotoToDatabase(FileName(realPathFromURI2), realPathFromURI2, NSHideFile2);
                            }
                            if (VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                                Intent intent2 = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                intent2.setData(Uri.fromFile(new File(realPathFromURI2)));
                                sendBroadcast(intent2);
                            }
                            File file2 = new File(realPathFromURI2);
                            try {
                                ContentResolver contentResolver2 = getContentResolver();
                                Uri uri2 = Media.EXTERNAL_CONTENT_URI;
                                StringBuilder sb6 = new StringBuilder();
                                sb6.append("_data='");
                                sb6.append(file2.getPath());
                                sb6.append("'");
                                contentResolver2.delete(uri2, sb6.toString(), null);
                            } catch (Exception e3) {
                                e3.printStackTrace();
                            }
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                    }
                }
            } else if (this.IsSingleVideoFile) {
                Common.IsImporting = true;
                this.videoUri = (Uri) this.SingleVideoFileintent.getParcelableExtra("android.intent.extra.STREAM");
                if (this.videoUri != null) {
                    VideoAlbumDAL videoAlbumDAL2 = new VideoAlbumDAL(getApplicationContext());
                    videoAlbumDAL2.OpenRead();
                    VideoAlbum GetAlbumById3 = videoAlbumDAL2.GetAlbumById(this.AlbumId);
                    StringBuilder sb7 = new StringBuilder();
                    sb7.append(StorageOptionsCommon.STORAGEPATH);
                    sb7.append(StorageOptionsCommon.VIDEOS);
                    sb7.append(GetAlbumById3.getAlbumName());
                    this.dbFolderPath = sb7.toString();
                    StringBuilder sb8 = new StringBuilder();
                    sb8.append(this.dbFolderPath);
                    sb8.append("/VideoThumnails/");
                    File file3 = new File(sb8.toString());
                    if (!file3.exists()) {
                        file3.mkdir();
                    }
                    String realPathFromURI3 = getRealPathFromURI(this.videoUri);
                    String FileName = FileName(realPathFromURI3);
                    StringBuilder sb9 = new StringBuilder();
                    sb9.append(this.dbFolderPath);
                    sb9.append("/VideoThumnails/thumbnil-");
                    sb9.append(FileName.substring(0, FileName.lastIndexOf(".")));
                    sb9.append("#jpg");
                    String sb10 = sb9.toString();
                    File file4 = new File(sb10);
                    Bitmap createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(realPathFromURI3, 3);
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file4);
                        createVideoThumbnail.compress(CompressFormat.JPEG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        Utilities.NSEncryption(file4);
                    } catch (IOException e5) {
                        e5.printStackTrace();
                    }
                    try {
                        String NSHideFile3 = Utilities.NSHideFile(this, new File(getRealPathFromURI(this.videoUri)), new File(this.dbFolderPath));
                        Utilities.NSEncryption(new File(NSHideFile3));
                        if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                            StringBuilder sb11 = new StringBuilder();
                            sb11.append(Constants.FILE);
                            sb11.append(Environment.getExternalStorageDirectory());
                            sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb11.toString())));
                        }
                        String realPathFromURI4 = getRealPathFromURI(this.videoUri);
                        if (NSHideFile3.length() > 0) {
                            AddVideoToDatabase(FileName(realPathFromURI4), realPathFromURI4, NSHideFile3, sb10);
                        }
                        if (VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                            Intent intent3 = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                            intent3.setData(Uri.fromFile(new File(realPathFromURI4)));
                            sendBroadcast(intent3);
                        }
                        File file5 = new File(realPathFromURI4);
                        try {
                            ContentResolver contentResolver3 = getContentResolver();
                            Uri uri3 = Video.Media.EXTERNAL_CONTENT_URI;
                            StringBuilder sb12 = new StringBuilder();
                            sb12.append("_data='");
                            sb12.append(file5.getPath());
                            sb12.append("'");
                            contentResolver3.delete(uri3, sb12.toString(), null);
                        } catch (Exception e6) {
                            e6.printStackTrace();
                        }
                    } catch (IOException e7) {
                        e7.printStackTrace();
                    }
                }
            } else if (this.IsMultipVideoleFile) {
                Common.IsImporting = true;
                this.videoUris = this.MultipleVideoFileintent.getParcelableArrayListExtra("android.intent.extra.STREAM");
                if (this.videoUris != null) {
                    VideoAlbumDAL videoAlbumDAL3 = new VideoAlbumDAL(getApplicationContext());
                    videoAlbumDAL3.OpenRead();
                    VideoAlbum GetAlbumById4 = videoAlbumDAL3.GetAlbumById(this.AlbumId);
                    StringBuilder sb13 = new StringBuilder();
                    sb13.append(StorageOptionsCommon.STORAGEPATH);
                    sb13.append(StorageOptionsCommon.VIDEOS);
                    sb13.append(GetAlbumById4.getAlbumName());
                    this.dbFolderPath = sb13.toString();
                    StringBuilder sb14 = new StringBuilder();
                    sb14.append(this.dbFolderPath);
                    sb14.append("/VideoThumnails/");
                    File file6 = new File(sb14.toString());
                    if (!file6.exists()) {
                        file6.mkdir();
                    }
                    for (int i2 = 0; i2 < this.videoUris.size(); i2++) {
                        String realPathFromURI5 = getRealPathFromURI((Uri) this.videoUris.get(i2));
                        String FileName2 = FileName(realPathFromURI5);
                        StringBuilder sb15 = new StringBuilder();
                        sb15.append(this.dbFolderPath);
                        sb15.append("/VideoThumnails/thumbnil-");
                        sb15.append(FileName2.substring(0, FileName2.lastIndexOf(".")));
                        sb15.append("#jpg");
                        String sb16 = sb15.toString();
                        File file7 = new File(sb16);
                        Bitmap createVideoThumbnail2 = ThumbnailUtils.createVideoThumbnail(realPathFromURI5, 3);
                        try {
                            FileOutputStream fileOutputStream2 = new FileOutputStream(file7);
                            createVideoThumbnail2.compress(CompressFormat.JPEG, 100, fileOutputStream2);
                            fileOutputStream2.flush();
                            fileOutputStream2.close();
                            Utilities.NSEncryption(file7);
                        } catch (IOException e8) {
                            e8.printStackTrace();
                        }
                        try {
                            String NSHideFile4 = Utilities.NSHideFile(this, new File(getRealPathFromURI((Uri) this.videoUris.get(i2))), new File(this.dbFolderPath));
                            Utilities.NSEncryption(new File(NSHideFile4));
                            if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                                StringBuilder sb17 = new StringBuilder();
                                sb17.append(Constants.FILE);
                                sb17.append(Environment.getExternalStorageDirectory());
                                sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb17.toString())));
                            }
                            String realPathFromURI6 = getRealPathFromURI((Uri) this.videoUris.get(i2));
                            if (NSHideFile4.length() > 0) {
                                AddVideoToDatabase(FileName(realPathFromURI6), realPathFromURI6, NSHideFile4, sb16);
                            }
                            if (VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                                Intent intent4 = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                intent4.setData(Uri.fromFile(new File(realPathFromURI6)));
                                sendBroadcast(intent4);
                            }
                            File file8 = new File(realPathFromURI6);
                            try {
                                ContentResolver contentResolver4 = getContentResolver();
                                Uri uri4 = Video.Media.EXTERNAL_CONTENT_URI;
                                StringBuilder sb18 = new StringBuilder();
                                sb18.append("_data='");
                                sb18.append(file8.getPath());
                                sb18.append("'");
                                contentResolver4.delete(uri4, sb18.toString(), null);
                            } catch (Exception e9) {
                                e9.printStackTrace();
                            }
                        } catch (IOException e10) {
                            e10.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void ImportMix() {
        if (this.IsImageMix) {
            this.IsImageMix = false;
            Common.IsImporting = true;
            this.imageUris = this.ImageAndVideosMixintent.getParcelableArrayListExtra("android.intent.extra.STREAM");
            if (this.imageUris != null) {
                PhotoAlbumDAL photoAlbumDAL2 = new PhotoAlbumDAL(getApplicationContext());
                photoAlbumDAL2.OpenRead();
                PhotoAlbum GetAlbumById = photoAlbumDAL2.GetAlbumById(Integer.toString(this.AlbumId));
                for (int i = 0; i < this.imageUris.size(); i++) {
                    if (((String) ((Uri) this.imageUris.get(i)).getPathSegments().get(1)).equals("images")) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(StorageOptionsCommon.STORAGEPATH);
                        sb.append(StorageOptionsCommon.PHOTOS);
                        sb.append(GetAlbumById.getAlbumName());
                        this.dbFolderPath = sb.toString();
                        try {
                            String NSHideFile = Utilities.NSHideFile(this, new File(getRealPathFromURI((Uri) this.imageUris.get(i))), new File(this.dbFolderPath));
                            Utilities.NSEncryption(new File(NSHideFile));
                            if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append(Constants.FILE);
                                sb2.append(Environment.getExternalStorageDirectory());
                                sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb2.toString())));
                            }
                            String realPathFromURI = getRealPathFromURI((Uri) this.imageUris.get(i));
                            if (NSHideFile.length() > 0) {
                                AddPhotoToDatabase(FileName(realPathFromURI), realPathFromURI, NSHideFile);
                            }
                            if (VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                                Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                intent.setData(Uri.fromFile(new File(realPathFromURI)));
                                sendBroadcast(intent);
                            }
                            File file = new File(realPathFromURI);
                            try {
                                ContentResolver contentResolver = getContentResolver();
                                Uri uri = Media.EXTERNAL_CONTENT_URI;
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("_data='");
                                sb3.append(file.getPath());
                                sb3.append("'");
                                contentResolver.delete(uri, sb3.toString(), null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        }
        if (this.IsVideoMix) {
            this.IsVideoMix = false;
            this.IsImageAndVideosMix = false;
            Common.IsImporting = true;
            this.videoUris = this.ImageAndVideosMixintent.getParcelableArrayListExtra("android.intent.extra.STREAM");
            if (this.videoUris != null) {
                VideoAlbumDAL videoAlbumDAL2 = new VideoAlbumDAL(getApplicationContext());
                videoAlbumDAL2.OpenRead();
                VideoAlbum GetAlbumById2 = videoAlbumDAL2.GetAlbumById(this.AlbumId);
                StringBuilder sb4 = new StringBuilder();
                sb4.append(StorageOptionsCommon.STORAGEPATH);
                sb4.append(StorageOptionsCommon.VIDEOS);
                sb4.append(GetAlbumById2.getAlbumName());
                this.dbFolderPath = sb4.toString();
                StringBuilder sb5 = new StringBuilder();
                sb5.append(this.dbFolderPath);
                sb5.append("/VideoThumnails/");
                File file2 = new File(sb5.toString());
                if (!file2.exists()) {
                    file2.mkdir();
                }
                for (int i2 = 0; i2 < this.videoUris.size(); i2++) {
                    if (((String) ((Uri) this.videoUris.get(i2)).getPathSegments().get(1)).equals("video")) {
                        String realPathFromURI2 = getRealPathFromURI((Uri) this.videoUris.get(i2));
                        String FileName = FileName(realPathFromURI2);
                        StringBuilder sb6 = new StringBuilder();
                        sb6.append(this.dbFolderPath);
                        sb6.append("/VideoThumnails/thumbnil-");
                        sb6.append(FileName.substring(0, FileName.lastIndexOf(".")));
                        sb6.append("#jpg");
                        String sb7 = sb6.toString();
                        File file3 = new File(sb7);
                        Bitmap createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(realPathFromURI2, 3);
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file3);
                            createVideoThumbnail.compress(CompressFormat.JPEG, 100, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            Utilities.NSEncryption(file3);
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                        try {
                            String NSHideFile2 = Utilities.NSHideFile(this, new File(getRealPathFromURI((Uri) this.videoUris.get(i2))), new File(this.dbFolderPath));
                            Utilities.NSEncryption(new File(NSHideFile2));
                            if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                                StringBuilder sb8 = new StringBuilder();
                                sb8.append(Constants.FILE);
                                sb8.append(Environment.getExternalStorageDirectory());
                                sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb8.toString())));
                            }
                            String realPathFromURI3 = getRealPathFromURI((Uri) this.videoUris.get(i2));
                            if (NSHideFile2.length() > 0) {
                                AddVideoToDatabase(FileName(realPathFromURI3), realPathFromURI3, NSHideFile2, sb7);
                            }
                            if (VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                                Intent intent2 = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                intent2.setData(Uri.fromFile(new File(realPathFromURI3)));
                                sendBroadcast(intent2);
                            }
                            File file4 = new File(realPathFromURI3);
                            try {
                                ContentResolver contentResolver2 = getContentResolver();
                                Uri uri2 = Video.Media.EXTERNAL_CONTENT_URI;
                                StringBuilder sb9 = new StringBuilder();
                                sb9.append("_data='");
                                sb9.append(file4.getPath());
                                sb9.append("'");
                                contentResolver2.delete(uri2, sb9.toString(), null);
                            } catch (Exception e4) {
                                e4.printStackTrace();
                            }
                        } catch (IOException e5) {
                            e5.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    public void handleSendText(Intent intent) {
        intent.getStringExtra("android.intent.extra.TEXT");
    }


    public void handleSendImage(Intent intent) {
        this.IsSingleFile = true;
        this.SingleFileintent = intent;
    }


    public void handleSendVideo(Intent intent) {
        this.IsSingleVideoFile = true;
        this.SingleVideoFileintent = intent;
    }


    public void handleSendMultipleVideos(Intent intent) {
        this.IsMultipVideoleFile = true;
        this.MultipleVideoFileintent = intent;
    }


    public void handleSendImageAndVideosMix(Intent intent) {
        this.IsImageAndVideosMix = true;
        this.ImageAndVideosMixintent = intent;
    }


    public void handleSendMultipleImages(Intent intent) {
        this.IsMultipleFile = true;
        this.MultipleFileintent = intent;
    }


    public void RefershGalleryforKitkat() {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.FILE);
        sb.append(Environment.getExternalStorageDirectory());
        Uri contentUri = Uri.fromFile(new File(sb.toString()));
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor query = getContentResolver().query(uri, null, null, null, null);
        query.moveToFirst();
        return query.getString(query.getColumnIndex("_data"));
    }


    public void AddPhotoToDatabase(String str, String str2, String str3) {
        Log.d("Path", str3);
        Photo photo = new Photo();
        photo.setPhotoName(str);
        photo.setFolderLockPhotoLocation(str3);
        photo.setOriginalPhotoLocation(str2);
        photo.setAlbumId(this.AlbumId);
        PhotoDAL photoDAL = new PhotoDAL(getApplicationContext());
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


    public void AddVideoToDatabase(String str, String str2, String str3, String str4) {
        Log.d("Path", str3);
        com.example.vault.video.model.Video video = new com.example.vault.video.model.Video();
        video.setVideoName(str);
        video.setFolderLockVideoLocation(str3);
        video.setOriginalVideoLocation(str2);
        video.setthumbnail_video_location(str4);
        video.setAlbumId(this.AlbumId);
        VideoDAL videoDAL = new VideoDAL(getApplicationContext());
        try {
            videoDAL.OpenWrite();
            videoDAL.AddVideos(video);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            videoDAL.close();
            throw th;
        }
        videoDAL.close();
    }

    public String FileName(String str) {
        String str2 = " /";
        for (int length = str.length() - 1; length > 0; length--) {
            if (str.charAt(length) == str2.charAt(1)) {
                return str.substring(length + 1, str.length());
            }
        }
        return "";
    }

    public void GetAlbumsFromDatabase() {
        PhotoAlbumDAL photoAlbumDAL2;
        VideoAlbumDAL videoAlbumDAL2;
        if (this.IsSingleFile || this.IsMultipleFile) {
            this.textfoldername.setText("Select photo album");
            this.photoAlbumDAL = new PhotoAlbumDAL(getApplicationContext());
            try {
                this.photoAlbumDAL.OpenRead();
                this.photoAlbums = (ArrayList) this.photoAlbumDAL.GetAlbums(0);
                this.adapter = new ShareFromGalleryAdapter(this, 17367043, this.photoAlbums, this.ListPosition);
                this.albumGridView.setAdapter(this.adapter);
                photoAlbumDAL2 = this.photoAlbumDAL;
                if (photoAlbumDAL2 == null) {
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                photoAlbumDAL2 = this.photoAlbumDAL;
                if (photoAlbumDAL2 == null) {
                    return;
                }
            } catch (Throwable th) {
                PhotoAlbumDAL photoAlbumDAL3 = this.photoAlbumDAL;
                if (photoAlbumDAL3 != null) {
                    photoAlbumDAL3.close();
                }
                throw th;
            }
            photoAlbumDAL2.close();
        } else if (this.IsSingleVideoFile || this.IsMultipVideoleFile) {
            this.textfoldername.setText("Select video album");
            this.videoAlbumDAL = new VideoAlbumDAL(getApplicationContext());
            try {
                this.videoAlbumDAL.OpenRead();
                this.videoAlbums = (ArrayList) this.videoAlbumDAL.GetAlbums(0);
                ShareFromGalleryAdapter shareFromGalleryAdapter = new ShareFromGalleryAdapter(this, 17367043, this.videoAlbums, this.ListPosition, true);
                this.adapter = shareFromGalleryAdapter;
                this.albumGridView.setAdapter(this.adapter);
                videoAlbumDAL2 = this.videoAlbumDAL;
                if (videoAlbumDAL2 == null) {
                    return;
                }
            } catch (Exception e2) {
                System.out.println(e2.getMessage());
                videoAlbumDAL2 = this.videoAlbumDAL;
                if (videoAlbumDAL2 == null) {
                    return;
                }
            } catch (Throwable th2) {
                VideoAlbumDAL videoAlbumDAL3 = this.videoAlbumDAL;
                if (videoAlbumDAL3 != null) {
                    videoAlbumDAL3.close();
                }
                throw th2;
            }
            videoAlbumDAL2.close();
        }
    }

    public void btnCancelClick(View view) {
        finish();
    }

    public void GetPhotoAlbumsFromDatabase() {
        PhotoAlbumDAL photoAlbumDAL2;
        this.IsImageMix = true;
        this.photoAlbumDAL = new PhotoAlbumDAL(getApplicationContext());
        try {
            this.photoAlbumDAL.OpenRead();
            this.photoAlbums = (ArrayList) this.photoAlbumDAL.GetAlbums(0);
            this.adapter = new ShareFromGalleryAdapter(this, 17367043, this.photoAlbums, this.ListPosition);
            this.albumGridView.setAdapter(this.adapter);
            photoAlbumDAL2 = this.photoAlbumDAL;
            if (photoAlbumDAL2 == null) {
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            photoAlbumDAL2 = this.photoAlbumDAL;
            if (photoAlbumDAL2 == null) {
                return;
            }
        } catch (Throwable th) {
            PhotoAlbumDAL photoAlbumDAL3 = this.photoAlbumDAL;
            if (photoAlbumDAL3 != null) {
                photoAlbumDAL3.close();
            }
            throw th;
        }
        photoAlbumDAL2.close();
    }

    public void GetVideoAlbumsFromDatabase() {
        VideoAlbumDAL videoAlbumDAL2;
        this.IsVideoMix = true;
        this.videoAlbumDAL = new VideoAlbumDAL(getApplicationContext());
        try {
            this.videoAlbumDAL.OpenRead();
            this.videoAlbums = (ArrayList) this.videoAlbumDAL.GetAlbums(0);
            ShareFromGalleryAdapter shareFromGalleryAdapter = new ShareFromGalleryAdapter(this, 17367043, this.videoAlbums, this.ListPosition, true);
            this.adapter = shareFromGalleryAdapter;
            this.albumGridView.setAdapter(this.adapter);
            videoAlbumDAL2 = this.videoAlbumDAL;
            if (videoAlbumDAL2 == null) {
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            videoAlbumDAL2 = this.videoAlbumDAL;
            if (videoAlbumDAL2 == null) {
                return;
            }
        } catch (Throwable th) {
            VideoAlbumDAL videoAlbumDAL3 = this.videoAlbumDAL;
            if (videoAlbumDAL3 != null) {
                videoAlbumDAL3.close();
            }
            throw th;
        }
        videoAlbumDAL2.close();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (configuration.orientation == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(5);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(4);
            } else {
                this.albumGridView.setNumColumns(3);
            }
        } else if (configuration.orientation != 1) {
        } else {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(4);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(3);
            } else {
                this.albumGridView.setNumColumns(2);
            }
        }
    }
}
