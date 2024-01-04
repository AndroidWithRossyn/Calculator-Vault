package com.example.vault.photo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vault.photo.adapter.AlbumsImportAdapter;
import com.example.vault.photo.adapter.GalleryPhotoAdapter;
import com.example.vault.photo.model.ImportAlbumEnt;
import com.example.vault.photo.model.ImportEnt;
import com.example.vault.photo.model.Photo;
import com.example.vault.photo.model.PhotoAlbum;
import com.example.vault.photo.util.PhotoAlbumDAL;
import com.example.vault.photo.util.PhotoDAL;
import com.banrossyn.imageloader.utils.LibCommonAppClass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.example.vault.R;
import com.example.vault.adapter.SelectAlbumInImportAdapter;
import com.example.vault.BaseActivity;
import com.example.vault.common.Constants;
import com.example.vault.gallery.GalleryActivity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;

public class ImportAlbumsGalleryPhotoActivity extends BaseActivity {

    public boolean IsExceptionInImportPhotos = false;
    private boolean IsSelectAll = false;
    public ProgressBar Progress;
    Handler Progresshowhandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                ImportAlbumsGalleryPhotoActivity.this.Progress.setVisibility(View.GONE);
                ImportAlbumsGalleryPhotoActivity importAlbumsGalleryPhotoActivity = ImportAlbumsGalleryPhotoActivity.this;
                importAlbumsGalleryPhotoActivity.galleryImagesAdapter = new GalleryPhotoAdapter(importAlbumsGalleryPhotoActivity.context, 1, ImportAlbumsGalleryPhotoActivity.this.photoImportEntListShow);
                ImportAlbumsGalleryPhotoActivity.this.imagegrid.setAdapter(ImportAlbumsGalleryPhotoActivity.this.galleryImagesAdapter);
                ImportAlbumsGalleryPhotoActivity.this.galleryImagesAdapter.notifyDataSetChanged();
                if (ImportAlbumsGalleryPhotoActivity.this.photoImportEntListShow.size() <= 0) {
                    ImportAlbumsGalleryPhotoActivity.this.album_import_ListView.setVisibility(View.INVISIBLE);
                    ImportAlbumsGalleryPhotoActivity.this.imagegrid.setVisibility(View.INVISIBLE);
                    ImportAlbumsGalleryPhotoActivity.this.btnSelectAll.setVisibility(View.INVISIBLE);
                    ImportAlbumsGalleryPhotoActivity.this.ll_photo_video_empty.setVisibility(View.VISIBLE);
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

    public GalleryPhotoAdapter galleryImagesAdapter;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            Intent intent;
            if (message.what == 1) {
                ImportAlbumsGalleryPhotoActivity.this.hideProgress();
                if (ImportAlbumsGalleryPhotoActivity.this.photoImportEntListShow.size() > 0) {
                    ImportAlbumsGalleryPhotoActivity importAlbumsGalleryPhotoActivity = ImportAlbumsGalleryPhotoActivity.this;
                    importAlbumsGalleryPhotoActivity.galleryImagesAdapter = new GalleryPhotoAdapter(importAlbumsGalleryPhotoActivity, 1, importAlbumsGalleryPhotoActivity.photoImportEntListShow);
                    ImportAlbumsGalleryPhotoActivity.this.imagegrid.setAdapter(ImportAlbumsGalleryPhotoActivity.this.galleryImagesAdapter);
                } else {
                    ImportAlbumsGalleryPhotoActivity.this.btnSelectAll.setEnabled(false);
                    ImportAlbumsGalleryPhotoActivity.this.ll_Import_bottom_baar.setEnabled(false);
                }
            } else if (message.what == 4) {
                ImportAlbumsGalleryPhotoActivity.this.Progress.setVisibility(View.GONE);
                ImportAlbumsGalleryPhotoActivity.this.GetAlbumsFromGallery();
            } else if (message.what == 3) {
                if (Common.IsImporting) {
                    if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        ImportAlbumsGalleryPhotoActivity importAlbumsGalleryPhotoActivity2 = ImportAlbumsGalleryPhotoActivity.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append(Constants.FILE);
                        sb.append(Environment.getExternalStorageDirectory());
                        importAlbumsGalleryPhotoActivity2.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb.toString())));
                    } else {
                        ImportAlbumsGalleryPhotoActivity.this.RefershGalleryforKitkat();
                    }
                    Common.IsImporting = false;
                    if (ImportAlbumsGalleryPhotoActivity.this.IsExceptionInImportPhotos) {
                        ImportAlbumsGalleryPhotoActivity.this.IsExceptionInImportPhotos = false;
                    } else {
                        ImportAlbumsGalleryPhotoActivity importAlbumsGalleryPhotoActivity3 = ImportAlbumsGalleryPhotoActivity.this;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(ImportAlbumsGalleryPhotoActivity.this.selectCount);
                        sb2.append(" photo(s) imported successfully");
                        Toast.makeText(importAlbumsGalleryPhotoActivity3, sb2.toString(), Toast.LENGTH_SHORT).show();
                    }
                    ImportAlbumsGalleryPhotoActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                       LibCommonAppClass.IsPhoneGalleryLoad = true;
                        if (Common.IsCameFromPhotoAlbum) {
                            if (ImportAlbumsGalleryPhotoActivity.this.isAlbumClick) {
                                intent = new Intent(ImportAlbumsGalleryPhotoActivity.this, Photos_Gallery_Actitvity.class);
                            } else {
                                intent = new Intent(ImportAlbumsGalleryPhotoActivity.this, PhotosAlbumActivty.class);
                            }
                        } else if (Common.IsCameFromGalleryFeature) {
                            Common.IsCameFromGalleryFeature = false;
                            intent = new Intent(ImportAlbumsGalleryPhotoActivity.this, GalleryActivity.class);
                        } else if (ImportAlbumsGalleryPhotoActivity.this.isAlbumClick) {
                            intent = new Intent(ImportAlbumsGalleryPhotoActivity.this, Photos_Gallery_Actitvity.class);
                        } else {
                            intent = new Intent(ImportAlbumsGalleryPhotoActivity.this, PhotosAlbumActivty.class);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ImportAlbumsGalleryPhotoActivity.this.startActivity(intent);
                        ImportAlbumsGalleryPhotoActivity.this.finish();
                    }
                }
            } else if (message.what == 2) {
                ImportAlbumsGalleryPhotoActivity.this.hideProgress();
            }
            super.handleMessage(message);
        }
    };
    int image_column_index;
    Cursor imagecursor;
    GridView imagegrid;
    ArrayList<ImportAlbumEnt> importAlbumEnts = new ArrayList<>();
    boolean isAlbumClick = false;
    TextView lbl_import_photo_album_topbaar;
    LinearLayout ll_Import_bottom_baar;
    LinearLayout ll_anchor;
    LinearLayout ll_background;
    LinearLayout ll_photo_video_empty;
    LinearLayout ll_topbaar;
    ProgressDialog myProgressDialog = null;

    public ArrayList<ImportEnt> photoImportEntList = new ArrayList<>();

    public ArrayList<ImportEnt> photoImportEntListShow = new ArrayList<>();
    List<List<ImportEnt>> photoImportEntListShowList = new ArrayList();
    int selectCount;
    private ArrayList<String> selectPath = new ArrayList<>();
    private SensorManager sensorManager;
    ArrayList<String> spinnerValues = new ArrayList<>();
    Toolbar toolbar;

    private class DataLoadTask extends AsyncTask<Void, Void, Void> {
        private DataLoadTask() {
        }


        public void onPreExecute() {
            super.onPreExecute();
            ImportAlbumsGalleryPhotoActivity.this.Progress.setVisibility(View.VISIBLE);
        }


        public Void doInBackground(Void... voidArr) {
            ImportAlbumsGalleryPhotoActivity.this.loadGallery();
            Iterator it = ImportAlbumsGalleryPhotoActivity.this.photoImportEntList.iterator();
            while (it.hasNext()) {
                ImportEnt importEnt = (ImportEnt) it.next();
                if (((String) ImportAlbumsGalleryPhotoActivity.this.spinnerValues.get(0)).contains(new File(importEnt.GetPath()).getParent())) {
                    ImportAlbumsGalleryPhotoActivity.this.photoImportEntListShow.add(importEnt);
                }
            }
            return null;
        }


        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            ImportAlbumsGalleryPhotoActivity.this.Progress.setVisibility(View.GONE);
            ImportAlbumsGalleryPhotoActivity.this.GetAlbumsFromGallery();
        }
    }

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
        this.myProgressDialog = ProgressDialog.show(this, null, "Your data is being encrypted... this may take a few moments... ", true);
    }


    public void hideProgress() {
        ProgressDialog progressDialog = this.myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            this.myProgressDialog.dismiss();
        }
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.import_album_list_activity);
        Log.d("TAG", "ImportAlbumsGalleryPhotoActivity");
        SecurityLocksCommon.IsAppDeactive = true;
        Common.IsWorkInProgress = false;
        LibCommonAppClass.IsPhoneGalleryLoad = false;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.album_import_ListView = (ListView) findViewById(R.id.album_import_ListView);
        this.imagegrid = (GridView) findViewById(R.id.customGalleryGrid);
        this.lbl_import_photo_album_topbaar = (TextView) findViewById(R.id.lbl_import_photo_album_topbaar);
        this.lbl_import_photo_album_topbaar.setText(R.string.lbl_import_photo_album_select_album_topbaar);
        this.btnSelectAll = (AppCompatImageView) findViewById(R.id.btnSelectAll);
        this.ll_Import_bottom_baar = (LinearLayout) findViewById(R.id.ll_Import_bottom_baar);
        this.ll_photo_video_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.folderId = Common.FolderId;
        this.folderName = null;
        if (this.folderName == null) {
            this.folderId = Common.FolderId;
            PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
            photoAlbumDAL.OpenRead();
            PhotoAlbum GetAlbumById = photoAlbumDAL.GetAlbumById(Integer.toString(Common.FolderId));
            photoAlbumDAL.close();
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
        new DataLoadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        this.ll_Import_bottom_baar.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ImportAlbumsGalleryPhotoActivity.this.OnImportClick();
            }
        });
        this.album_import_ListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ImportAlbumsGalleryPhotoActivity.this.Progress.setVisibility(View.VISIBLE);
                ImportAlbumsGalleryPhotoActivity importAlbumsGalleryPhotoActivity = ImportAlbumsGalleryPhotoActivity.this;
                importAlbumsGalleryPhotoActivity.isAlbumClick = true;
                importAlbumsGalleryPhotoActivity.lbl_import_photo_album_topbaar.setText(R.string.lbl_import_photo_album_select_photo_topbaar);
                ImportAlbumsGalleryPhotoActivity importAlbumsGalleryPhotoActivity2 = ImportAlbumsGalleryPhotoActivity.this;
                AlbumsImportAdapter albumsImportAdapter = new AlbumsImportAdapter(importAlbumsGalleryPhotoActivity2.context, 17367043, ImportAlbumsGalleryPhotoActivity.this.importAlbumEnts, false, false);
                importAlbumsGalleryPhotoActivity2.adapter = albumsImportAdapter;
                ImportAlbumsGalleryPhotoActivity.this.album_import_ListView.setAdapter(ImportAlbumsGalleryPhotoActivity.this.adapter);
                ImportAlbumsGalleryPhotoActivity.this.ShowAlbumData(i);
                ImportAlbumsGalleryPhotoActivity.this.imagegrid.setVisibility(View.VISIBLE);
                ImportAlbumsGalleryPhotoActivity.this.btnSelectAll.setVisibility(View.VISIBLE);
                ImportAlbumsGalleryPhotoActivity.this.album_import_ListView.setVisibility(View.INVISIBLE);
            }
        });
    }


    public void LoadData() {
        new Thread() {
            public void run() {
                try {
                    ImportAlbumsGalleryPhotoActivity.this.loadGallery();
                    Message message = new Message();
                    message.what = 4;
                    ImportAlbumsGalleryPhotoActivity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 4;
                    ImportAlbumsGalleryPhotoActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
        Iterator it = this.photoImportEntList.iterator();
        while (it.hasNext()) {
            ImportEnt importEnt = (ImportEnt) it.next();
            if (((String) this.spinnerValues.get(0)).contains(new File(importEnt.GetPath()).getParent())) {
                this.photoImportEntListShow.add(importEnt);
            }
        }
    }


    public void ShowAlbumData(final int i) {
        new Thread() {
            public void run() {
                try {
                    ImportAlbumsGalleryPhotoActivity.this.photoImportEntListShow.clear();
                    Iterator it = ImportAlbumsGalleryPhotoActivity.this.photoImportEntList.iterator();
                    while (it.hasNext()) {
                        ImportEnt importEnt = (ImportEnt) it.next();
                        if (((String) ImportAlbumsGalleryPhotoActivity.this.spinnerValues.get(i)).equals(new File(importEnt.GetPath()).getParent())) {
                            importEnt.GetThumbnailSelection().booleanValue();
                            ImportAlbumsGalleryPhotoActivity.this.photoImportEntListShow.add(importEnt);
                        }
                    }
                    Message message = new Message();
                    message.what = 1;
                    ImportAlbumsGalleryPhotoActivity.this.Progresshowhandler.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 1;
                    ImportAlbumsGalleryPhotoActivity.this.Progresshowhandler.sendMessage(message2);
                }
            }
        }.start();
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

    public void OnImportClick() {
        final StorageOptionSharedPreferences GetObject = StorageOptionSharedPreferences.GetObject(this);
        if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectphotomsg_import, Toast.LENGTH_SHORT).show();
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
                            ImportAlbumsGalleryPhotoActivity.this.Import();
                        } else if (GetObject.GetSDCardUri().length() > 0) {
                            ImportAlbumsGalleryPhotoActivity.this.Import();
                        } else if (!GetObject.GetISDAlertshow()) {
                            final Dialog dialog = new Dialog(ImportAlbumsGalleryPhotoActivity.this, R.style.FullHeightDialog);
                            dialog.setContentView(R.layout.sdcard_permission_alert_msgbox);
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            ((CheckBox) dialog.findViewById(R.id.cbalertdialog)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
                                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                                    GetObject.SetISDAlertshow(Boolean.valueOf(z));
                                }
                            });
                            ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    ImportAlbumsGalleryPhotoActivity.this.Import();
                                }
                            });
                            dialog.show();
                        } else {
                            ImportAlbumsGalleryPhotoActivity.this.Import();
                        }
                    }
                });
                ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        for (int i = 0; i < ImportAlbumsGalleryPhotoActivity.this.importAlbumEnts.size(); i++) {
                            ((ImportAlbumEnt) ImportAlbumsGalleryPhotoActivity.this.importAlbumEnts.get(i)).SetAlbumFileCheck(false);
                        }
                        ImportAlbumsGalleryPhotoActivity.this.GetAlbumsFromGallery();
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
                ((CheckBox) dialog2.findViewById(R.id.cbalertdialog)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        GetObject.SetISDAlertshow(Boolean.valueOf(z));
                    }
                });
                ((LinearLayout) dialog2.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        dialog2.dismiss();
                        ImportAlbumsGalleryPhotoActivity.this.Import();
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
                        ImportAlbumsGalleryPhotoActivity.this.ImportPhotos();
                        Message message = new Message();
                        message.what = 3;
                        ImportAlbumsGalleryPhotoActivity.this.handle.sendMessage(message);
                        Common.IsWorkInProgress = false;
                    } catch (Exception unused) {
                        Message message2 = new Message();
                        message2.what = 3;
                        ImportAlbumsGalleryPhotoActivity.this.handle.sendMessage(message2);
                    }
                }
            }.start();
            return;
        }
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenRead();
        final List GetAlbums = photoAlbumDAL.GetAlbums(0);
        photoAlbumDAL.close();
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.share_from_gallery);
        ((LinearLayout) dialog.findViewById(R.id.ll_bottom_bar)).setVisibility(View.GONE);
        ((TextView) dialog.findViewById(R.id.lbl_import_title)).setText("Select Album to Import Photo(s)");
        GridView gridView = (GridView) dialog.findViewById(R.id.fileListgrid);
        gridView.setNumColumns(1);
        SelectAlbumInImportAdapter selectAlbumInImportAdapter = new SelectAlbumInImportAdapter(this, 1, GetAlbums);
        gridView.setAdapter(selectAlbumInImportAdapter);
        selectAlbumInImportAdapter.notifyDataSetChanged();
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ImportAlbumsGalleryPhotoActivity.this.folderId = ((PhotoAlbum) GetAlbums.get(i)).getId();
                dialog.dismiss();
                ImportAlbumsGalleryPhotoActivity.this.SelectedCount();
                ImportAlbumsGalleryPhotoActivity.this.ShowImportProgress();
                Common.IsWorkInProgress = true;
                new Thread() {
                    public void run() {
                        try {
                            ImportAlbumsGalleryPhotoActivity.this.ImportOnlyPhotosSDCard();
                            Message message = new Message();
                            message.what = 3;
                            ImportAlbumsGalleryPhotoActivity.this.handle.sendMessage(message);
                            Common.IsWorkInProgress = false;
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 3;
                            ImportAlbumsGalleryPhotoActivity.this.handle.sendMessage(message2);
                        }
                    }
                }.start();
            }
        });
        dialog.show();
    }

    public void ImportPhotos() {
        if (this.isAlbumClick) {
            ImportOnlyPhotosSDCard();
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
                    sb.append(StorageOptionsCommon.PHOTOS);
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
                            sb3.append(StorageOptionsCommon.PHOTOS);
                            sb3.append(this.folderName);
                            file3 = new File(sb3.toString());
                            if (!file3.exists()) {
                                i3 = 100;
                            }
                            i3++;
                        }
                        file2 = file3;
                    }
                    AddAlbumToDatabase(this.folderName);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this);
                    photoAlbumDAL.OpenRead();
                    this.folderId = photoAlbumDAL.GetLastAlbumId();
                    Common.FolderId = this.folderId;
                    photoAlbumDAL.close();
                    ImportAlbumsPhotosSDCard(i);
                    i++;
                }
            }
        }
    }


    public void ImportAlbumsPhotosSDCard(int i) {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        List list = (List) this.photoImportEntListShowList.get(i);
        for (int i2 = 0; i2 < list.size(); i2++) {
            if (((ImportEnt) list.get(i2)).GetThumbnailSelection().booleanValue()) {
                File file = new File(((ImportEnt) list.get(i2)).GetPath());
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append(StorageOptionsCommon.PHOTOS);
                sb.append(this.folderName);
                sb.append("/");
                try {
                    String NSHideFile = Utilities.NSHideFile(this, file, new File(sb.toString()));
                    Utilities.NSEncryption(new File(NSHideFile));
                    if (NSHideFile.length() > 0) {
                        AddPhotoToDatabase(FileName(((ImportEnt) list.get(i2)).GetPath()), ((ImportEnt) list.get(i2)).GetPath(), NSHideFile);
                    }
                    if (VERSION.SDK_INT >= 21 && VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                        Utilities.DeleteSDcardImageLollipop(this, file.getAbsolutePath());
                    }
                    try {
                        ContentResolver contentResolver = getContentResolver();
                        Uri uri = Media.EXTERNAL_CONTENT_URI;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("_data='");
                        sb2.append(file.getPath());
                        sb2.append("'");
                        contentResolver.delete(uri, sb2.toString(), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (IOException e2) {
                    this.IsExceptionInImportPhotos = true;
                    e2.printStackTrace();
                }
            }
        }
    }


    public void ImportOnlyPhotosSDCard() {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        int size = this.photoImportEntListShow.size();
        for (int i = 0; i < size; i++) {
            if (((ImportEnt) this.photoImportEntListShow.get(i)).GetThumbnailSelection().booleanValue()) {
                File file = new File(((ImportEnt) this.photoImportEntListShow.get(i)).GetPath());
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append(StorageOptionsCommon.PHOTOS);
                sb.append(this.folderName);
                sb.append("/");
                try {
                    String NSHideFile = Utilities.NSHideFile(this, file, new File(sb.toString()));
                    Utilities.NSEncryption(new File(NSHideFile));
                    if (VERSION.SDK_INT >= 21 && VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                        Utilities.DeleteSDcardImageLollipop(this, file.getAbsolutePath());
                    }
                    try {
                        ContentResolver contentResolver = getContentResolver();
                        Uri uri = Media.EXTERNAL_CONTENT_URI;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("_data='");
                        sb2.append(file.getPath());
                        sb2.append("'");
                        contentResolver.delete(uri, sb2.toString(), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (NSHideFile.length() > 0) {
                        AddPhotoToDatabase(FileName(((ImportEnt) this.photoImportEntListShow.get(i)).GetPath()), ((ImportEnt) this.photoImportEntListShow.get(i)).GetPath(), NSHideFile);
                    }
                } catch (IOException e2) {
                    this.IsExceptionInImportPhotos = true;
                    e2.printStackTrace();
                }
            }
        }
    }


    public void AddPhotoToDatabase(String str, String str2, String str3) {
        Log.d("Path", str3);
        Photo photo = new Photo();
        photo.setPhotoName(str);
        photo.setFolderLockPhotoLocation(str3);
        photo.setOriginalPhotoLocation(str2);
        photo.setAlbumId(this.folderId);
        PhotoDAL photoDAL = new PhotoDAL(this.context);
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

    public void AddAlbumToDatabase(String str) {
        PhotoAlbum photoAlbum = new PhotoAlbum();
        photoAlbum.setAlbumName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.PHOTOS);
        sb.append(str);
        photoAlbum.setAlbumLocation(sb.toString());
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this);
        try {
            photoAlbumDAL.OpenWrite();
            photoAlbumDAL.AddPhotoAlbum(photoAlbum);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            photoAlbumDAL.close();
            throw th;
        }
        photoAlbumDAL.close();
    }

    private boolean IsFileCheck() {
        for (int i = 0; i < this.importAlbumEnts.size(); i++) {
            if (((ImportAlbumEnt) this.importAlbumEnts.get(i)).GetAlbumFileCheck()) {
                this.photoImportEntListShow = new ArrayList<>();
                Iterator it = this.photoImportEntList.iterator();
                while (it.hasNext()) {
                    ImportEnt importEnt = (ImportEnt) it.next();
                    if (((String) this.spinnerValues.get(i)).equals(new File(importEnt.GetPath()).getParent())) {
                        this.photoImportEntListShow.add(importEnt);
                    }
                    for (int i2 = 0; i2 < this.photoImportEntListShow.size(); i2++) {
                        ((ImportEnt) this.photoImportEntListShow.get(i2)).SetThumbnailSelection(Boolean.valueOf(true));
                    }
                }
                this.photoImportEntListShowList.add(this.photoImportEntListShow);
            }
        }
        this.selectPath.clear();
        for (int i3 = 0; i3 < this.photoImportEntListShow.size(); i3++) {
            if (((ImportEnt) this.photoImportEntListShow.get(i3)).GetThumbnailSelection().booleanValue()) {
                this.selectPath.add(((ImportEnt) this.photoImportEntListShow.get(i3)).GetPath());
                return true;
            }
        }
        return false;
    }

    public void GetAlbumsFromGallery() {
        AlbumsImportAdapter albumsImportAdapter = new AlbumsImportAdapter(this.context, 17367043, this.importAlbumEnts, false, false);
        this.adapter = albumsImportAdapter;
        this.album_import_ListView.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
        if (this.importAlbumEnts.size() <= 0) {
            this.album_import_ListView.setVisibility(View.INVISIBLE);
            this.imagegrid.setVisibility(View.INVISIBLE);
            this.btnSelectAll.setVisibility(View.INVISIBLE);
            this.ll_photo_video_empty.setVisibility(View.VISIBLE);
        }
    }


    public void SelectedCount() {
        this.selectCount = 0;
        for (int i = 0; i < this.photoImportEntListShow.size(); i++) {
            if (((ImportEnt) this.photoImportEntListShow.get(i)).GetThumbnailSelection().booleanValue()) {
                this.selectCount++;
            }
        }
    }

    public void btnSelectAllonClick(View view) {
        SelectAllPhotos();
        this.galleryImagesAdapter = new GalleryPhotoAdapter(this.context, 1, this.photoImportEntListShow);
        this.imagegrid.setAdapter(this.galleryImagesAdapter);
        this.galleryImagesAdapter.notifyDataSetChanged();
    }

    public void btnBackonClick(View view) {
        Back();
    }

    private void SelectAllPhotos() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.photoImportEntListShow.size(); i++) {
                ((ImportEnt) this.photoImportEntListShow.get(i)).SetThumbnailSelection(Boolean.valueOf(false));
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setImageResource(R.drawable.ic_unselectallicon);
            return;
        }
        for (int i2 = 0; i2 < this.photoImportEntListShow.size(); i2++) {
            ((ImportEnt) this.photoImportEntListShow.get(i2)).SetThumbnailSelection(Boolean.valueOf(true));
        }
        this.IsSelectAll = true;
        this.btnSelectAll.setImageResource(R.drawable.ic_selectallicon);
    }

    public void loadGallery() {
        try {
            this.imagecursor = managedQuery(Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id"}, null, null, "_id");
            this.image_column_index = this.imagecursor.getColumnIndex("_id");
            this.count = this.imagecursor.getCount();
            for (int i = 0; i < this.count; i++) {
                this.imagecursor.moveToPosition(i);
                int columnIndex = this.imagecursor.getColumnIndex("_data");
                if (new File(this.imagecursor.getString(columnIndex)).exists()) {
                    ImportEnt importEnt = new ImportEnt();
                    importEnt.SetPath(this.imagecursor.getString(columnIndex));
                    importEnt.SetThumbnailSelection(Boolean.valueOf(false));
                    importEnt.SetThumbnail(null);
                    this.photoImportEntList.add(importEnt);
                    ImportAlbumEnt importAlbumEnt = new ImportAlbumEnt();
                    File file = new File(importEnt.GetPath());
                    if (this.spinnerValues.size() <= 0 || !this.spinnerValues.contains(file.getParent())) {
                        importAlbumEnt.SetAlbumName(file.getParent());
                        importAlbumEnt.SetPath(this.imagecursor.getString(columnIndex));
                        this.importAlbumEnts.add(importAlbumEnt);
                        this.spinnerValues.add(file.getParent());
                    }
                }
            }
        } catch (Exception unused) {
        }
        if (this.photoImportEntList.size() <= 0) {
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
            for (int i = 0; i < this.photoImportEntListShow.size(); i++) {
                ((ImportEnt) this.photoImportEntListShow.get(i)).SetThumbnailSelection(Boolean.valueOf(false));
            }
            this.IsSelectAll = false;
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        if (Common.IsCameFromPhotoAlbum) {
            Common.IsCameFromPhotoAlbum = false;
            intent = new Intent(this, PhotosAlbumActivty.class);
        } else if (Common.IsCameFromGalleryFeature) {
            Common.IsCameFromGalleryFeature = false;
            intent = new Intent(this, GalleryActivity.class);
        } else {
            intent = new Intent(this, Photos_Gallery_Actitvity.class);
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
                for (int i2 = 0; i2 < this.photoImportEntListShow.size(); i2++) {
                    ((ImportEnt) this.photoImportEntListShow.get(i2)).SetThumbnailSelection(Boolean.valueOf(false));
                }
                this.IsSelectAll = false;
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            if (Common.IsCameFromPhotoAlbum) {
                intent = new Intent(this, PhotosAlbumActivty.class);
            } else if (Common.IsCameFromGalleryFeature) {
                Common.IsCameFromGalleryFeature = false;
                intent = new Intent(this, GalleryActivity.class);
            } else {
                intent = new Intent(this, Photos_Gallery_Actitvity.class);
            }
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
