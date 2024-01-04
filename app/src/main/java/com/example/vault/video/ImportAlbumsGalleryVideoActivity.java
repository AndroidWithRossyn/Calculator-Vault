package com.example.vault.video;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video.Media;
import android.provider.MediaStore.Video.Thumbnails;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.example.vault.R;
import com.example.vault.adapter.SelectAlbumInImportAdapter;
import com.example.vault.common.Constants;
import com.example.vault.gallery.GalleryActivity;
import com.example.vault.panicswitch.AccelerometerListener;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;
import com.example.vault.video.adapter.AlbumsImportAdapter;
import com.example.vault.video.adapter.GalleryVideoAdapter;
import com.example.vault.video.model.ImportAlbumEnt;
import com.example.vault.video.model.ImportEnt;
import com.example.vault.video.model.Video;
import com.example.vault.video.model.VideoAlbum;
import com.example.vault.video.util.VideoAlbumDAL;
import com.example.vault.video.util.VideoDAL;

public class ImportAlbumsGalleryVideoActivity extends Activity implements AccelerometerListener, SensorEventListener {

    public boolean IsExceptionInImportVideos = false;
    private boolean IsSelectAll = false;
    public ProgressBar Progress;
    Handler Progresshowhandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                ImportAlbumsGalleryVideoActivity.this.Progress.setVisibility(View.GONE);
                ImportAlbumsGalleryVideoActivity importAlbumsGalleryVideoActivity = ImportAlbumsGalleryVideoActivity.this;
                importAlbumsGalleryVideoActivity.galleryImagesAdapter = new GalleryVideoAdapter(importAlbumsGalleryVideoActivity.context, 1, ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow);
                ImportAlbumsGalleryVideoActivity.this.imagegrid.setAdapter(ImportAlbumsGalleryVideoActivity.this.galleryImagesAdapter);
                ImportAlbumsGalleryVideoActivity.this.galleryImagesAdapter.notifyDataSetChanged();
                ImportAlbumsGalleryVideoActivity.this.imagegrid.setVisibility(View.VISIBLE);
                if (ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.size() <= 0) {
                    ImportAlbumsGalleryVideoActivity.this.album_import_ListView.setVisibility(View.INVISIBLE);
                    ImportAlbumsGalleryVideoActivity.this.imagegrid.setVisibility(View.INVISIBLE);
                    ImportAlbumsGalleryVideoActivity.this.btnSelectAll.setVisibility(View.INVISIBLE);
                    ImportAlbumsGalleryVideoActivity.this.ll_photo_video_empty.setVisibility(View.VISIBLE);
                    ImportAlbumsGalleryVideoActivity.this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_video_empty_icon);
                    ImportAlbumsGalleryVideoActivity.this.lbl_photo_video_empty.setText(R.string.no_videos);
                }
            }
            super.handleMessage(message);
        }
    };

    public AlbumsImportAdapter adapter;
    ListView album_import_ListView;
    AppCompatImageView btnSelectAll;
    Context context = this;
    private int count;
    String dbFolderPath;
    int folderId;
    String folderName;
    String folderPath;

    public GalleryVideoAdapter galleryImagesAdapter;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            Intent intent;
            if (message.what == 1) {
                ImportAlbumsGalleryVideoActivity.this.hideProgress();
                if (ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.size() > 0) {
                    ImportAlbumsGalleryVideoActivity importAlbumsGalleryVideoActivity = ImportAlbumsGalleryVideoActivity.this;
                    importAlbumsGalleryVideoActivity.galleryImagesAdapter = new GalleryVideoAdapter(importAlbumsGalleryVideoActivity, 1, importAlbumsGalleryVideoActivity.videoImportEntListShow);
                    ImportAlbumsGalleryVideoActivity.this.imagegrid.setAdapter(ImportAlbumsGalleryVideoActivity.this.galleryImagesAdapter);
                } else {
                    ImportAlbumsGalleryVideoActivity.this.btnSelectAll.setEnabled(false);
                    ImportAlbumsGalleryVideoActivity.this.ll_Import_bottom_baar.setEnabled(false);
                }
            } else if (message.what == 4) {
                ImportAlbumsGalleryVideoActivity.this.Progress.setVisibility(View.GONE);
                ImportAlbumsGalleryVideoActivity.this.GetAlbumsFromGallery();
            } else if (message.what == 3) {
                if (Common.IsImporting) {
                    if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        ImportAlbumsGalleryVideoActivity importAlbumsGalleryVideoActivity2 = ImportAlbumsGalleryVideoActivity.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append(Constants.FILE);
                        sb.append(Environment.getExternalStorageDirectory());
                        importAlbumsGalleryVideoActivity2.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb.toString())));
                    } else {
                        ImportAlbumsGalleryVideoActivity.this.RefershGalleryforKitkat();
                    }
                    Common.IsImporting = false;
                    if (ImportAlbumsGalleryVideoActivity.this.IsExceptionInImportVideos) {
                        ImportAlbumsGalleryVideoActivity.this.IsExceptionInImportVideos = false;
                    } else {
                        ImportAlbumsGalleryVideoActivity importAlbumsGalleryVideoActivity3 = ImportAlbumsGalleryVideoActivity.this;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(ImportAlbumsGalleryVideoActivity.this.selectCount);
                        sb2.append(" video(s) imported successfully");
                        Toast.makeText(importAlbumsGalleryVideoActivity3, sb2.toString(), Toast.LENGTH_SHORT).show();
                    }
                    ImportAlbumsGalleryVideoActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        if (Common.IsCameFromPhotoAlbum) {
                            if (ImportAlbumsGalleryVideoActivity.this.isAlbumClick) {
                                intent = new Intent(ImportAlbumsGalleryVideoActivity.this, Videos_Gallery_Actitvity.class);
                            } else {
                                intent = new Intent(ImportAlbumsGalleryVideoActivity.this, VideosAlbumActivty.class);
                            }
                        } else if (Common.IsCameFromGalleryFeature) {
                            Common.IsCameFromGalleryFeature = false;
                            intent = new Intent(ImportAlbumsGalleryVideoActivity.this, GalleryActivity.class);
                        } else if (ImportAlbumsGalleryVideoActivity.this.isAlbumClick) {
                            intent = new Intent(ImportAlbumsGalleryVideoActivity.this, Videos_Gallery_Actitvity.class);
                        } else {
                            intent = new Intent(ImportAlbumsGalleryVideoActivity.this, VideosAlbumActivty.class);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ImportAlbumsGalleryVideoActivity.this.startActivity(intent);
                        ImportAlbumsGalleryVideoActivity.this.finish();
                    }
                }
            } else if (message.what == 2) {
                ImportAlbumsGalleryVideoActivity.this.hideProgress();
            }
            super.handleMessage(message);
        }
    };
    int image_column_index;
    GridView imagegrid;
    ArrayList<ImportAlbumEnt> importAlbumEnts = new ArrayList<>();
    boolean isAlbumClick = false;
    TextView lbl_import_photo_album_topbaar;
    TextView lbl_photo_video_empty;
    LinearLayout ll_Import_bottom_baar;
    LinearLayout ll_background;
    LinearLayout ll_photo_video_empty;
    LinearLayout ll_topbaar;
    ProgressDialog myProgressDialog = null;
    ImageView photo_video_empty_icon;
    int selectCount;
    private ArrayList<String> selectPath = new ArrayList<>();
    private SensorManager sensorManager;
    ArrayList<String> spinnerValues = new ArrayList<>();
    Cursor videoCursor;

    public ArrayList<ImportEnt> videoImportEntList = new ArrayList<>();

    public ArrayList<ImportEnt> videoImportEntListShow = new ArrayList<>();
    List<List<ImportEnt>> videoImportEntListShowList = new ArrayList();
    int video_column_index;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
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


    public void ShowImportProgress() {
        this.myProgressDialog = ProgressDialog.show(this, null, "Your data is being encrypted... this may take a few moments...", true);
    }


    public void hideProgress() {
        ProgressDialog progressDialog = this.myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            this.myProgressDialog.dismiss();
        }
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.import_album_list_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        Common.IsWorkInProgress = false;
        getWindow().addFlags(128);
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.album_import_ListView = (ListView) findViewById(R.id.album_import_ListView);
        this.imagegrid = (GridView) findViewById(R.id.customGalleryGrid);
        this.lbl_import_photo_album_topbaar = (TextView) findViewById(R.id.lbl_import_photo_album_topbaar);
        this.lbl_import_photo_album_topbaar.setText(R.string.lbl_import_photo_album_select_album_topbaar);
        this.btnSelectAll = (AppCompatImageView) findViewById(R.id.btnSelectAll);
        this.ll_Import_bottom_baar = (LinearLayout) findViewById(R.id.ll_Import_bottom_baar);
        this.ll_photo_video_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.photo_video_empty_icon = (ImageView) findViewById(R.id.photo_video_empty_icon);
        this.lbl_photo_video_empty = (TextView) findViewById(R.id.lbl_photo_video_empty);
        this.folderId = Common.FolderId;
        this.folderName = null;
        if (this.folderName == null) {
            this.folderId = Common.FolderId;
            VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(getApplicationContext());
            videoAlbumDAL.OpenRead();
            VideoAlbum GetAlbumById = videoAlbumDAL.GetAlbumById(Common.FolderId);
            videoAlbumDAL.close();
            this.folderName = GetAlbumById.getAlbumName();
        }
        this.dbFolderPath = (String) getIntent().getSerializableExtra("FOLDER_PATH");
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(4);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(3);
            } else {
                this.imagegrid.setNumColumns(2);
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(5);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(4);
            } else {
                this.imagegrid.setNumColumns(3);
            }
        }
        LoadData();
        this.ll_Import_bottom_baar.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ImportAlbumsGalleryVideoActivity.this.OnImportClick();
            }
        });
        this.album_import_ListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ImportAlbumsGalleryVideoActivity.this.imagegrid.setVisibility(View.VISIBLE);
                ImportAlbumsGalleryVideoActivity importAlbumsGalleryVideoActivity = ImportAlbumsGalleryVideoActivity.this;
                importAlbumsGalleryVideoActivity.isAlbumClick = true;
                importAlbumsGalleryVideoActivity.lbl_import_photo_album_topbaar.setText(R.string.lbl_import_video_album_select_video_topbaar);
                ImportAlbumsGalleryVideoActivity.this.album_import_ListView.setVisibility(View.INVISIBLE);
                ImportAlbumsGalleryVideoActivity.this.btnSelectAll.setVisibility(View.VISIBLE);
                ImportAlbumsGalleryVideoActivity importAlbumsGalleryVideoActivity2 = ImportAlbumsGalleryVideoActivity.this;
                AlbumsImportAdapter albumsImportAdapter = new AlbumsImportAdapter(importAlbumsGalleryVideoActivity2.context, 17367043, ImportAlbumsGalleryVideoActivity.this.importAlbumEnts, false, true);
                importAlbumsGalleryVideoActivity2.adapter = albumsImportAdapter;
                ImportAlbumsGalleryVideoActivity.this.album_import_ListView.setAdapter(ImportAlbumsGalleryVideoActivity.this.adapter);
                ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.clear();
                Iterator it = ImportAlbumsGalleryVideoActivity.this.videoImportEntList.iterator();
                while (it.hasNext()) {
                    ImportEnt importEnt = (ImportEnt) it.next();
                    if (((String) ImportAlbumsGalleryVideoActivity.this.spinnerValues.get(i)).equals(new File(importEnt.GetPath()).getParent())) {
                        importEnt.GetThumbnailSelection().booleanValue();
                        ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.add(importEnt);
                    }
                }
                ImportAlbumsGalleryVideoActivity importAlbumsGalleryVideoActivity3 = ImportAlbumsGalleryVideoActivity.this;
                importAlbumsGalleryVideoActivity3.galleryImagesAdapter = new GalleryVideoAdapter(importAlbumsGalleryVideoActivity3.context, 1, ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow);
                ImportAlbumsGalleryVideoActivity.this.imagegrid.setAdapter(ImportAlbumsGalleryVideoActivity.this.galleryImagesAdapter);
                ImportAlbumsGalleryVideoActivity.this.galleryImagesAdapter.notifyDataSetChanged();
                if (ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.size() <= 0) {
                    ImportAlbumsGalleryVideoActivity.this.album_import_ListView.setVisibility(View.INVISIBLE);
                    ImportAlbumsGalleryVideoActivity.this.imagegrid.setVisibility(View.INVISIBLE);
                    ImportAlbumsGalleryVideoActivity.this.btnSelectAll.setVisibility(View.INVISIBLE);
                    ImportAlbumsGalleryVideoActivity.this.ll_photo_video_empty.setVisibility(View.VISIBLE);
                    ImportAlbumsGalleryVideoActivity.this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_video_empty_icon);
                    ImportAlbumsGalleryVideoActivity.this.lbl_photo_video_empty.setText(R.string.no_videos);
                }
            }
        });
    }


    public int albumCheckCount() {
        int i = 0;
        for (int i2 = 0; i2 < this.importAlbumEnts.size(); i2++) {
            if (((ImportAlbumEnt) this.importAlbumEnts.get(i2)).GetAlbumFileCheck()) {
                i++;
            }
        }
        return i;
    }


    public void LoadData() {
        this.Progress.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                try {
                    ImportAlbumsGalleryVideoActivity.this.loadGallery();
                } catch (IOException e) {
                    try {
                        e.printStackTrace();
                    } catch (Exception unused) {
                        Message message = new Message();
                        message.what = 4;
                        ImportAlbumsGalleryVideoActivity.this.handle.sendMessage(message);
                        return;
                    }
                }
                Iterator it = ImportAlbumsGalleryVideoActivity.this.videoImportEntList.iterator();
                while (it.hasNext()) {
                    ImportEnt importEnt = (ImportEnt) it.next();
                    if (((String) ImportAlbumsGalleryVideoActivity.this.spinnerValues.get(0)).contains(new File(importEnt.GetPath()).getParent())) {
                        ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.add(importEnt);
                    }
                }
                Message message2 = new Message();
                message2.what = 4;
                ImportAlbumsGalleryVideoActivity.this.handle.sendMessage(message2);
            }
        }.start();
    }


    public void ShowAlbumData(final int i) {
        this.Progress.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                try {
                    ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.clear();
                    Iterator it = ImportAlbumsGalleryVideoActivity.this.videoImportEntList.iterator();
                    while (it.hasNext()) {
                        ImportEnt importEnt = (ImportEnt) it.next();
                        if (((String) ImportAlbumsGalleryVideoActivity.this.spinnerValues.get(i)).equals(new File(importEnt.GetPath()).getParent())) {
                            importEnt.GetThumbnailSelection().booleanValue();
                            ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.add(importEnt);
                        }
                    }
                    Message message = new Message();
                    message.what = 1;
                    ImportAlbumsGalleryVideoActivity.this.Progresshowhandler.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 1;
                    ImportAlbumsGalleryVideoActivity.this.Progresshowhandler.sendMessage(message2);
                }
            }
        }.start();
    }

    public void OnImportClick() {
        final StorageOptionSharedPreferences GetObject = StorageOptionSharedPreferences.GetObject(this);
        if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectvideomsg_import, Toast.LENGTH_LONG).show();
        } else if (Common.GetFileSize(this.selectPath) < Common.GetTotalFree()) {
            int albumCheckCount = albumCheckCount();
            if (albumCheckCount >= 2) {
                final Dialog dialog = new Dialog(this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.confirmation_message_box);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
                TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
                textView.setTypeface(Typeface.createFromAsset(getAssets(), "ebrima.ttf"));
                StringBuilder sb = new StringBuilder();
                sb.append("Are you sure you want to import ");
                sb.append(albumCheckCount);
                sb.append(" albums? Importing may take time according to the size of your data.");
                textView.setText(sb.toString());
                ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (VERSION.SDK_INT < 21 || VERSION.SDK_INT >= 23) {
                            ImportAlbumsGalleryVideoActivity.this.Import();
                        } else if (GetObject.GetSDCardUri().length() > 0) {
                            ImportAlbumsGalleryVideoActivity.this.Import();
                        } else if (!GetObject.GetISDAlertshow()) {
                            final Dialog dialog = new Dialog(ImportAlbumsGalleryVideoActivity.this, R.style.FullHeightDialog);
                            dialog.setContentView(R.layout.sdcard_permission_alert_msgbox);
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            ((TextView) dialog.findViewById(R.id.tvmessagedialogtitle)).setText(R.string.lblSdCardAlertMsgVideo);
                            ((CheckBox) dialog.findViewById(R.id.cbalertdialog)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
                                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                                    GetObject.SetISDAlertshow(Boolean.valueOf(z));
                                }
                            });
                            ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    ImportAlbumsGalleryVideoActivity.this.Import();
                                }
                            });
                            dialog.show();
                        } else {
                            ImportAlbumsGalleryVideoActivity.this.Import();
                        }
                    }
                });
                ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        for (int i = 0; i < ImportAlbumsGalleryVideoActivity.this.importAlbumEnts.size(); i++) {
                            ((ImportAlbumEnt) ImportAlbumsGalleryVideoActivity.this.importAlbumEnts.get(i)).SetAlbumFileCheck(false);
                        }
                        ImportAlbumsGalleryVideoActivity.this.GetAlbumsFromGallery();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            } else if (VERSION.SDK_INT < 21 || VERSION.SDK_INT >= 23) {
                Import();
            } else if (GetObject.GetSDCardUri().length() > 0) {
                Import();
            } else if (!GetObject.GetISDAlertshow()) {
                final Dialog dialog2 = new Dialog(this, R.style.FullHeightDialog);
                dialog2.setContentView(R.layout.sdcard_permission_alert_msgbox);
                dialog2.setCancelable(false);
                dialog2.setCanceledOnTouchOutside(false);
                ((TextView) dialog2.findViewById(R.id.tvmessagedialogtitle)).setText(R.string.lblSdCardAlertMsgVideo);
                ((CheckBox) dialog2.findViewById(R.id.cbalertdialog)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        GetObject.SetISDAlertshow(Boolean.valueOf(z));
                    }
                });
                ((LinearLayout) dialog2.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        dialog2.dismiss();
                        ImportAlbumsGalleryVideoActivity.this.Import();
                    }
                });
                ((LinearLayout) dialog2.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        dialog2.dismiss();
                    }
                });
                dialog2.show();
            } else {
                Import();
            }
        }
    }


    public void Import() {
        if (!Common.IsCameFromGalleryFeature || !this.isAlbumClick) {
            SelectedCount();
            ShowImportProgress();
            Common.IsWorkInProgress = true;
            new Thread() {
                public void run() {
                    try {
                        ImportAlbumsGalleryVideoActivity.this.ImportVideo();
                        Message message = new Message();
                        message.what = 3;
                        ImportAlbumsGalleryVideoActivity.this.handle.sendMessage(message);
                        Common.IsWorkInProgress = false;
                    } catch (Exception unused) {
                        Message message2 = new Message();
                        message2.what = 3;
                        ImportAlbumsGalleryVideoActivity.this.handle.sendMessage(message2);
                    }
                }
            }.start();
            return;
        }
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(getApplicationContext());
        videoAlbumDAL.OpenRead();
        final List GetAlbums = videoAlbumDAL.GetAlbums(0);
        videoAlbumDAL.close();
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.share_from_gallery);
        ((LinearLayout) dialog.findViewById(R.id.ll_bottom_bar)).setVisibility(View.GONE);
        ((TextView) dialog.findViewById(R.id.lbl_import_title)).setText("Select Album to Import Video(s)");
        GridView gridView = (GridView) dialog.findViewById(R.id.fileListgrid);
        gridView.setNumColumns(1);
        SelectAlbumInImportAdapter selectAlbumInImportAdapter = new SelectAlbumInImportAdapter(this, 1, GetAlbums, true);
        gridView.setAdapter(selectAlbumInImportAdapter);
        selectAlbumInImportAdapter.notifyDataSetChanged();
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ImportAlbumsGalleryVideoActivity.this.folderId = ((VideoAlbum) GetAlbums.get(i)).getId();
                dialog.dismiss();
                ImportAlbumsGalleryVideoActivity.this.SelectedCount();
                ImportAlbumsGalleryVideoActivity.this.ShowImportProgress();
                Common.IsWorkInProgress = true;
                new Thread() {
                    public void run() {
                        try {
                            ImportAlbumsGalleryVideoActivity.this.ImportOnlyVideosSDCard();
                            Message message = new Message();
                            message.what = 3;
                            ImportAlbumsGalleryVideoActivity.this.handle.sendMessage(message);
                            Common.IsWorkInProgress = false;
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 3;
                            ImportAlbumsGalleryVideoActivity.this.handle.sendMessage(message2);
                        }
                    }
                }.start();
            }
        });
        dialog.show();
    }

    public void ImportVideo() throws IOException {
        if (this.isAlbumClick) {
            ImportOnlyVideosSDCard();
        } else {
            importAlbum();
        }
    }


    public void importAlbum() {
        if (this.importAlbumEnts.size() > 0) {
            int i = 0;
            for (int i2 = 0; i2 < this.importAlbumEnts.size(); i2++) {
                if (((ImportAlbumEnt) this.importAlbumEnts.get(i2)).GetAlbumFileCheck()) {
                    File file = new File(((ImportAlbumEnt) this.importAlbumEnts.get(i2)).GetAlbumName());
                    StringBuilder sb = new StringBuilder();
                    sb.append(StorageOptionsCommon.STORAGEPATH);
                    sb.append(StorageOptionsCommon.VIDEOS);
                    sb.append(file.getName());
                    File file2 = new File(sb.toString());
                    this.folderName = file.getName();
                    if (file2.exists()) {
                        File file3 = file2;
                        int i3 = 1;
                        while (i3 < 100) {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append(file.getName());
                            sb2.append("(");
                            sb2.append(i3);
                            sb2.append(")");
                            this.folderName = sb2.toString();
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append(StorageOptionsCommon.STORAGEPATH);
                            sb3.append(StorageOptionsCommon.VIDEOS);
                            sb3.append(this.folderName);
                            file3 = new File(sb3.toString());
                            if (!file3.exists()) {
                                i3 = 100;
                            }
                            i3++;
                        }
                        file2 = file3;
                    }
                    AddAlbumToDatabase(this.folderName, file2.getAbsolutePath());
                    VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(getApplicationContext());
                    videoAlbumDAL.OpenRead();
                    this.folderId = videoAlbumDAL.GetLastAlbumId();
                    videoAlbumDAL.close();
                    try {
                        ImportAlbumsVideosSDCard(i);
                        i++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public void ImportAlbumsVideosSDCard(int i) throws IOException {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        List list = (List) this.videoImportEntListShowList.get(i);
        for (int i2 = 0; i2 < list.size(); i2++) {
            if (((ImportEnt) list.get(i2)).GetThumbnailSelection().booleanValue()) {
                File file = new File(((ImportEnt) list.get(i2)).GetPath());
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append(StorageOptionsCommon.VIDEOS);
                sb.append(this.folderName);
                sb.append("/");
                String sb2 = sb.toString();
                File file2 = new File(sb2);
                StringBuilder sb3 = new StringBuilder();
                sb3.append(sb2);
                sb3.append("VideoThumnails/");
                new File(sb3.toString()).mkdirs();
                String FileName = FileName(((ImportEnt) list.get(i2)).GetPath());
                StringBuilder sb4 = new StringBuilder();
                sb4.append(sb2);
                sb4.append("VideoThumnails/thumbnil-");
                sb4.append(FileName.substring(0, FileName.lastIndexOf(".")));
                sb4.append("#jpg");
                String sb5 = sb4.toString();
                File file3 = new File(sb5);
                file3.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file3);
                ((ImportEnt) list.get(i2)).SetThumbnail(Thumbnails.getThumbnail(getContentResolver(), (long) ((ImportEnt) list.get(i2)).GetId(), 3, null));
                ((ImportEnt) list.get(i2)).GetThumbnail().compress(CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                Utilities.NSEncryption(file3);
                try {
                    String NSHideFile = Utilities.NSHideFile(this, file, file2);
                    try {
                        Utilities.NSEncryption(new File(NSHideFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (NSHideFile.length() > 1) {
                        AddVideoToDatabase(FileName(((ImportEnt) list.get(i2)).GetPath()), ((ImportEnt) list.get(i2)).GetPath(), sb5, NSHideFile);
                        if (VERSION.SDK_INT >= 21 && VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                            Utilities.DeleteSDcardImageLollipop(this, file.getAbsolutePath());
                        }
                        try {
                            ContentResolver contentResolver = getContentResolver();
                            Uri uri = Media.EXTERNAL_CONTENT_URI;
                            StringBuilder sb6 = new StringBuilder();
                            sb6.append("_data='");
                            sb6.append(file.getPath());
                            sb6.append("'");
                            contentResolver.delete(uri, sb6.toString(), null);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    } else {
                        this.IsExceptionInImportVideos = true;
                    }
                } catch (Exception e3) {
                    this.IsExceptionInImportVideos = true;
                    e3.printStackTrace();
                }
            }
        }
    }

    public void ImportOnlyVideosSDCard() throws IOException {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        int size = this.videoImportEntListShow.size();
        for (int i = 0; i < size; i++) {
            if (((ImportEnt) this.videoImportEntListShow.get(i)).GetThumbnailSelection().booleanValue()) {
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append(StorageOptionsCommon.VIDEOS);
                sb.append(this.folderName);
                String sb2 = sb.toString();
                StringBuilder sb3 = new StringBuilder();
                sb3.append(StorageOptionsCommon.STORAGEPATH);
                sb3.append(StorageOptionsCommon.VIDEOS);
                sb3.append(this.folderName);
                sb3.append("/VideoThumnails/");
                new File(sb3.toString()).mkdirs();
                String FileName = FileName(((ImportEnt) this.videoImportEntListShow.get(i)).GetPath());
                StringBuilder sb4 = new StringBuilder();
                sb4.append(StorageOptionsCommon.STORAGEPATH);
                sb4.append(StorageOptionsCommon.VIDEOS);
                sb4.append(this.folderName);
                sb4.append("/VideoThumnails/thumbnil-");
                sb4.append(FileName.substring(0, FileName.lastIndexOf(".")));
                sb4.append("#jpg");
                String sb5 = sb4.toString();
                File file = new File(sb5);
                FileOutputStream fileOutputStream = new FileOutputStream(file, false);
                ((ImportEnt) this.videoImportEntListShow.get(i)).GetThumbnail().compress(CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                Utilities.NSEncryption(file);
                try {
                    File file2 = new File(((ImportEnt) this.videoImportEntListShow.get(i)).GetPath());
                    String NSHideFile = Utilities.NSHideFile(this, file2, new File(sb2));
                    try {
                        Utilities.NSEncryption(new File(NSHideFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (NSHideFile.length() > 1) {
                        AddVideoToDatabase(FileName(((ImportEnt) this.videoImportEntListShow.get(i)).GetPath()), ((ImportEnt) this.videoImportEntListShow.get(i)).GetPath(), sb5, NSHideFile);
                        if (VERSION.SDK_INT >= 21 && VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                            Utilities.DeleteSDcardImageLollipop(this, file2.getAbsolutePath());
                        }
                        try {
                            ContentResolver contentResolver = getContentResolver();
                            Uri uri = Media.EXTERNAL_CONTENT_URI;
                            StringBuilder sb6 = new StringBuilder();
                            sb6.append("_data='");
                            sb6.append(file2.getPath());
                            sb6.append("'");
                            contentResolver.delete(uri, sb6.toString(), null);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    } else {
                        this.IsExceptionInImportVideos = true;
                    }
                } catch (Exception e3) {
                    this.IsExceptionInImportVideos = true;
                    e3.printStackTrace();
                }
            }
        }
    }


    public void AddVideoToDatabase(String str, String str2, String str3, String str4) {
        Log.d("Path", str4);
        Video video = new Video();
        video.setVideoName(str);
        video.setFolderLockVideoLocation(str4);
        video.setOriginalVideoLocation(str2);
        video.setthumbnail_video_location(str3);
        video.setAlbumId(this.folderId);
        VideoDAL videoDAL = new VideoDAL(this.context);
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

    public void AddAlbumToDatabase(String str, String str2) {
        VideoAlbum videoAlbum = new VideoAlbum();
        videoAlbum.setAlbumName(str);
        videoAlbum.setAlbumLocation(str2);
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        try {
            videoAlbumDAL.OpenWrite();
            videoAlbumDAL.AddVideoAlbum(videoAlbum);
            Common.FolderId = videoAlbumDAL.GetLastAlbumId();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            videoAlbumDAL.close();
            throw th;
        }
        videoAlbumDAL.close();
    }

    private boolean IsFileCheck() {
        for (int i = 0; i < this.importAlbumEnts.size(); i++) {
            if (((ImportAlbumEnt) this.importAlbumEnts.get(i)).GetAlbumFileCheck()) {
                this.videoImportEntListShow = new ArrayList<>();
                Iterator it = this.videoImportEntList.iterator();
                while (it.hasNext()) {
                    ImportEnt importEnt = (ImportEnt) it.next();
                    if (((String) this.spinnerValues.get(i)).equals(new File(importEnt.GetPath()).getParent())) {
                        this.videoImportEntListShow.add(importEnt);
                    }
                    for (int i2 = 0; i2 < this.videoImportEntListShow.size(); i2++) {
                        ((ImportEnt) this.videoImportEntListShow.get(i2)).SetThumbnailSelection(Boolean.valueOf(true));
                    }
                }
                this.videoImportEntListShowList.add(this.videoImportEntListShow);
            }
        }
        this.selectPath.clear();
        for (int i3 = 0; i3 < this.videoImportEntListShow.size(); i3++) {
            if (((ImportEnt) this.videoImportEntListShow.get(i3)).GetThumbnailSelection().booleanValue()) {
                this.selectPath.add(((ImportEnt) this.videoImportEntListShow.get(i3)).GetPath());
                return true;
            }
        }
        return false;
    }

    public void GetAlbumsFromGallery() {
        AlbumsImportAdapter albumsImportAdapter = new AlbumsImportAdapter(this.context, 17367043, this.importAlbumEnts, false, true);
        this.adapter = albumsImportAdapter;
        this.album_import_ListView.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
        if (this.importAlbumEnts.size() <= 0) {
            this.album_import_ListView.setVisibility(View.INVISIBLE);
            this.imagegrid.setVisibility(View.INVISIBLE);
            this.btnSelectAll.setVisibility(View.INVISIBLE);
            this.ll_photo_video_empty.setVisibility(View.VISIBLE);
            this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_video_empty_icon);
            this.lbl_photo_video_empty.setText(R.string.no_videos);
        }
    }


    public void SelectedCount() {
        this.selectCount = 0;
        for (int i = 0; i < this.videoImportEntListShow.size(); i++) {
            if (((ImportEnt) this.videoImportEntListShow.get(i)).GetThumbnailSelection().booleanValue()) {
                this.selectCount++;
            }
        }
    }

    public void btnSelectAllonClick(View view) {
        SelectAllPhotos();
        this.galleryImagesAdapter = new GalleryVideoAdapter(this.context, 1, this.videoImportEntListShow);
        this.imagegrid.setAdapter(this.galleryImagesAdapter);
        this.galleryImagesAdapter.notifyDataSetChanged();
    }

    public void btnBackonClick(View view) {
        Back();
    }

    private void SelectAllPhotos() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.videoImportEntListShow.size(); i++) {
                ((ImportEnt) this.videoImportEntListShow.get(i)).SetThumbnailSelection(Boolean.valueOf(false));
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setImageResource(R.drawable.ic_unselectallicon);
            return;
        }
        for (int i2 = 0; i2 < this.videoImportEntListShow.size(); i2++) {
            ((ImportEnt) this.videoImportEntListShow.get(i2)).SetThumbnailSelection(Boolean.valueOf(true));
        }
        this.IsSelectAll = true;
        this.btnSelectAll.setImageResource(R.drawable.ic_selectallicon);
    }

    public void loadGallery() throws IOException {
        if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
            StringBuilder sb = new StringBuilder();
            sb.append(Constants.FILE);
            sb.append(Environment.getExternalStorageDirectory());
            sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse(sb.toString())));
            StringBuilder sb2 = new StringBuilder();
            sb2.append(Constants.FILE);
            sb2.append(Environment.getExternalStorageDirectory());
            sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb2.toString())));
        } else {
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new OnScanCompletedListener() {
                public void onScanCompleted(String str, Uri uri) {
                }
            });
        }
        this.videoCursor = managedQuery(Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id"}, null, null, "_id");
        this.video_column_index = this.videoCursor.getColumnIndex("_id");
        this.count = this.videoCursor.getCount();
        for (int i = 0; i < this.count; i++) {
            this.videoCursor.moveToPosition(i);
            int i2 = this.videoCursor.getInt(this.video_column_index);
            int columnIndex = this.videoCursor.getColumnIndex("_data");
            if (new File(this.videoCursor.getString(columnIndex)).exists()) {
                String string = this.videoCursor.getString(columnIndex);
                if (string.endsWith(".3gp") || string.endsWith(".mp4") || string.endsWith(".ts") || string.endsWith(".webm") || string.endsWith(".mkv") || string.endsWith(".wmv") || string.endsWith(".mov") || string.endsWith(".avi") || string.endsWith(".flv")) {
                    ImportEnt importEnt = new ImportEnt();
                    importEnt.SetId(i2);
                    importEnt.SetPath(this.videoCursor.getString(columnIndex));
                    importEnt.SetThumbnail(null);
                    importEnt.SetThumbnailSelection(Boolean.valueOf(false));
                    this.videoImportEntList.add(importEnt);
                    ImportAlbumEnt importAlbumEnt = new ImportAlbumEnt();
                    File file = new File(importEnt.GetPath());
                    if (this.spinnerValues.size() <= 0 || !this.spinnerValues.contains(file.getParent())) {
                        importAlbumEnt.SetId(i2);
                        importAlbumEnt.SetAlbumName(file.getParent());
                        importAlbumEnt.SetPath(this.videoCursor.getString(columnIndex));
                        this.importAlbumEnts.add(importAlbumEnt);
                        this.spinnerValues.add(file.getParent());
                    }
                }
            }
        }
        if (this.videoImportEntList.size() <= 0) {
            this.btnSelectAll.setEnabled(false);
            this.ll_Import_bottom_baar.setEnabled(false);
        }
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

    public void Back() {
        Intent intent;
        if (this.isAlbumClick) {
            this.isAlbumClick = false;
            this.lbl_import_photo_album_topbaar.setText("Import Albums");
            this.album_import_ListView.setVisibility(View.VISIBLE);
            this.imagegrid.setVisibility(View.INVISIBLE);
            this.btnSelectAll.setVisibility(View.INVISIBLE);
            for (int i = 0; i < this.videoImportEntListShow.size(); i++) {
                ((ImportEnt) this.videoImportEntListShow.get(i)).SetThumbnailSelection(Boolean.valueOf(false));
            }
            this.IsSelectAll = false;
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        if (Common.IsCameFromPhotoAlbum) {
            Common.IsCameFromPhotoAlbum = false;
            intent = new Intent(this, VideosAlbumActivty.class);
        } else if (Common.IsCameFromGalleryFeature) {
            Common.IsCameFromGalleryFeature = false;
            intent = new Intent(this, GalleryActivity.class);
        } else {
            intent = new Intent(this, Videos_Gallery_Actitvity.class);
        }
        startActivity(intent);
        finish();
    }

    public void onShake(float f) {
        if (PanicSwitchCommon.IsFlickOn || PanicSwitchCommon.IsShakeOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8 && sensorEvent.values[0] == 0.0f && PanicSwitchCommon.IsPalmOnFaceOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (configuration.orientation == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(5);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(4);
            } else {
                this.imagegrid.setNumColumns(3);
            }
        } else if (configuration.orientation != 1) {
        } else {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(4);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(3);
            } else {
                this.imagegrid.setNumColumns(2);
            }
        }
    }


    public void onPause() {
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive && !Common.IsWorkInProgress) {
            finish();
            System.exit(0);
        }
        super.onPause();
    }


    public void onResume() {
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        Intent intent;
        if (i == 4) {
            if (this.isAlbumClick) {
                this.isAlbumClick = false;
                this.lbl_import_photo_album_topbaar.setText("Import Albums");
                this.album_import_ListView.setVisibility(View.VISIBLE);
                this.imagegrid.setVisibility(View.INVISIBLE);
                this.btnSelectAll.setVisibility(View.INVISIBLE);
                for (int i2 = 0; i2 < this.videoImportEntListShow.size(); i2++) {
                    ((ImportEnt) this.videoImportEntListShow.get(i2)).SetThumbnailSelection(Boolean.valueOf(false));
                }
                this.IsSelectAll = false;
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            if (Common.IsCameFromPhotoAlbum) {
                Common.IsCameFromPhotoAlbum = false;
                intent = new Intent(this, VideosAlbumActivty.class);
            } else if (Common.IsCameFromGalleryFeature) {
                Common.IsCameFromGalleryFeature = false;
                intent = new Intent(this, GalleryActivity.class);
            } else {
                intent = new Intent(this, Videos_Gallery_Actitvity.class);
            }
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
