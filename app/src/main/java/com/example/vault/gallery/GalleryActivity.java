package com.example.vault.gallery;

import static com.example.vault.utilities.Common.AppPackageName;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import androidx.core.content.FileProvider;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.example.vault.R;
import com.example.vault.adapter.ExpandableListAdapter1;
import com.example.vault.BaseActivity;
import com.example.vault.common.Constants;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.photo.ImportAlbumsGalleryPhotoActivity;
import com.example.vault.photo.NewFullScreenViewActivity;
import com.example.vault.photo.model.Photo;
import com.example.vault.photo.util.PhotoDAL;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.AppSettingsSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;
import com.example.vault.video.ImportAlbumsGalleryVideoActivity;
import com.example.vault.video.model.Video;
import com.example.vault.video.util.VideoDAL;

public class GalleryActivity extends BaseActivity {
    private static int RESULT_LOAD_CAMERA = 1;

    public static int _ViewBy = 1;
    public static ProgressDialog myProgressDialog;
    private boolean IsSelectAll = false;

    public boolean IsSortingDropdown = false;

    public int _SortBy = 1;

    public ImageButton _btnSortingDropdown;
    AppSettingsSharedPreferences appSettingsSharedPreferences;

    public ImageButton btnSelectAll;
    private ArrayList<String> files = new ArrayList<>();

    public FrameLayout fl_top_baar;
    GalleryFeatureAdapter galleryFeatureAdapter;

    public GridView galleryGrid;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 2) {
                GalleryActivity.this.hideProgress();
                if (Common.isUnHide) {
                    Common.isUnHide = false;
                    Toast.makeText(GalleryActivity.this, R.string.Unhide_error, Toast.LENGTH_SHORT).show();
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(GalleryActivity.this, R.string.Delete_error, Toast.LENGTH_SHORT).show();
                }
            } else if (message.what == 4) {
                Toast.makeText(GalleryActivity.this, R.string.toast_share, Toast.LENGTH_LONG).show();
            } else if (message.what == 3) {
                if (Common.isUnHide) {
                    if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        GalleryActivity galleryActivity = GalleryActivity.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append(Constants.FILE);
                        sb.append(Environment.getExternalStorageDirectory());
                        galleryActivity.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb.toString())));
                    } else {
                        GalleryActivity.this.RefershGalleryforKitkat();
                    }
                    Common.isUnHide = false;
                    Toast.makeText(GalleryActivity.this, R.string.toast_unhide, Toast.LENGTH_LONG).show();
                    GalleryActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent = new Intent(GalleryActivity.this, GalleryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        GalleryActivity.this.startActivity(intent);
                        GalleryActivity.this.finish();
                    }
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(GalleryActivity.this, R.string.toast_delete, Toast.LENGTH_SHORT).show();
                    GalleryActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent2 = new Intent(GalleryActivity.this, GalleryActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        GalleryActivity.this.startActivity(intent2);
                        GalleryActivity.this.finish();
                    }
                }
            }
            super.handleMessage(message);
        }
    };

    public boolean isAddbarvisible = false;

    public boolean isEditMode = false;
    private TextView lbl_photo_video_empty;

    public LinearLayout ll_AddInGallery_Baar;

    public LinearLayout ll_Edit;
    private LayoutParams ll_GridviewParams;
    private LinearLayout.LayoutParams ll_Hide_Params;

    public LinearLayout.LayoutParams ll_Show_Params;
    private LinearLayout ll_anchor;
    private LinearLayout ll_background;
    private LinearLayout ll_delete_btn;
    private LinearLayout ll_import_Photos_from_gallery_btn;
    private LinearLayout ll_import_videos_from_gallery_btn;
    private LinearLayout ll_photo_video_empty;
    private LinearLayout ll_photo_video_grid;
    private LinearLayout ll_share_btn;
    private LinearLayout ll_topbaar;
    private LinearLayout ll_unhide_btn;

    public List<GalleryEnt> mGalleryGirdList = new ArrayList();

    public ArrayList<String> mPhotosList = new ArrayList<>();
    private Uri outputFileUri;
    private ImageView photo_video_empty_icon;
    int selectCount;
    private SensorManager sensorManager;
    private Toolbar toolbar;

    private enum SortBy {
        Name,
        Time,
        Size
    }

    private enum ViewBy {
        LargeTiles,
        Tiles,
        List
    }

    private class galleryListners implements OnClickListener, OnItemClickListener, OnItemLongClickListener, OnTouchListener {
        private galleryListners() {
        }

        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.btnAdd) { /*2131296330*/
                GalleryActivity.this.isAddbarvisible = true;
                GalleryActivity.this.fl_top_baar.setLayoutParams(GalleryActivity.this.ll_Show_Params);
                GalleryActivity.this.ll_AddInGallery_Baar.setVisibility(View.VISIBLE);
                GalleryActivity.this.ll_Edit.setVisibility(View.INVISIBLE);
                GalleryActivity.this.IsSortingDropdown = true;

            } else if (id == R.id.btnSelectAll) { /*2131296334*/
                GalleryActivity.this.SelectOrUnSelectAll();

            } else if (id == R.id.ll_delete_btn) { /*2131296745*/
                GalleryActivity.this.DeleteGalleryFiles();

            } else if (id == R.id.ll_import_from_Photo_gallery_btn) { /*2131296760*/
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                Common.IsCameFromGalleryFeature = true;
                GalleryActivity.this.startActivity(new Intent(GalleryActivity.this, ImportAlbumsGalleryPhotoActivity.class));
                GalleryActivity.this.finish();

            } else if (id == R.id.ll_import_from_video_gallery_btn) { /*2131296763*/
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                Common.IsCameFromGalleryFeature = true;
                GalleryActivity.this.startActivity(new Intent(GalleryActivity.this, ImportAlbumsGalleryVideoActivity.class));
                GalleryActivity.this.finish();

            } else if (id == R.id.ll_share_btn) { /*2131296802*/
                if (!GalleryActivity.this.IsFileCheck()) {
                    Toast.makeText(GalleryActivity.this, R.string.toast_unselectphotovideomsg_share, Toast.LENGTH_SHORT).show();

                } else {
                    GalleryActivity.this.ShareGalleryFiles();

                }
            } else if (id == R.id.ll_unhide_btn) { /*2131296816*/
                GalleryActivity.this.UnhideGalleryFiles();

            }
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            Common.GalleryThumbnailCurrentPosition = GalleryActivity.this.galleryGrid.getFirstVisiblePosition();
            GalleryActivity.this.isEditMode = true;
            GalleryActivity.this.fl_top_baar.setLayoutParams(GalleryActivity.this.ll_Show_Params);
            GalleryActivity.this.ll_AddInGallery_Baar.setVisibility(View.INVISIBLE);
            GalleryActivity.this.ll_Edit.setVisibility(View.VISIBLE);
            GalleryActivity.this._btnSortingDropdown.setVisibility(View.INVISIBLE);
            GalleryActivity.this.btnSelectAll.setVisibility(View.VISIBLE);
            GalleryActivity.this.invalidateOptionsMenu();
            ((GalleryEnt) GalleryActivity.this.mGalleryGirdList.get(i)).set_isCheck(Boolean.valueOf(true));
            GalleryActivity galleryActivity = GalleryActivity.this;
            GalleryFeatureAdapter galleryFeatureAdapter = new GalleryFeatureAdapter(galleryActivity, 1, galleryActivity.mGalleryGirdList, true, GalleryActivity._ViewBy);
            galleryActivity.galleryFeatureAdapter = galleryFeatureAdapter;
            GalleryActivity.this.galleryGrid.setAdapter(GalleryActivity.this.galleryFeatureAdapter);
            GalleryActivity.this.galleryFeatureAdapter.notifyDataSetChanged();
            if (Common.GalleryThumbnailCurrentPosition != 0) {
                GalleryActivity.this.galleryGrid.setSelection(Common.GalleryThumbnailCurrentPosition);
            }
            return true;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            Common.GalleryThumbnailCurrentPosition = GalleryActivity.this.galleryGrid.getFirstVisiblePosition();
            if (GalleryActivity.this.isEditMode) {
                return;
            }
            if (((GalleryEnt) GalleryActivity.this.mGalleryGirdList.get(i)).get_isVideo().booleanValue()) {
                GalleryActivity galleryActivity = GalleryActivity.this;
                galleryActivity.PlayVideo(((GalleryEnt) galleryActivity.mGalleryGirdList.get(i)).get_folderLockgalleryfileLocation());
                return;
            }
            int i2 = 0;
            while (true) {
                if (i2 >= GalleryActivity.this.mPhotosList.size()) {
                    i2 = 0;
                    break;
                } else if (((GalleryEnt) GalleryActivity.this.mGalleryGirdList.get(i)).get_folderLockgalleryfileLocation().endsWith((String) GalleryActivity.this.mPhotosList.get(i2))) {
                    break;
                } else {
                    i2++;
                }
            }
            SecurityLocksCommon.IsAppDeactive = false;
            Common.IsCameFromGalleryFeature = true;
            Intent intent = new Intent(GalleryActivity.this, NewFullScreenViewActivity.class);
            intent.putExtra("IMAGE_URL", ((GalleryEnt) GalleryActivity.this.mGalleryGirdList.get(i)).get_folderLockgalleryfileLocation());
            intent.putExtra("IMAGE_POSITION", i2);
            intent.putStringArrayListExtra("mPhotosList", GalleryActivity.this.mPhotosList);
            GalleryActivity.this.startActivity(intent);
            GalleryActivity.this.finish();
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (view.getId() == R.id.ll_background && GalleryActivity.this.IsSortingDropdown) {
                GalleryActivity.this.IsSortingDropdown = false;
            }
            return false;
        }
    }

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void showUnhideProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }


    public void showDeleteProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    private void showMoveProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    private void showIsImportingProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }


    public void hideProgress() {
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
    }

    private void showCopyFilesProcessForShareProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
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


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.gallery_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SecurityLocksCommon.IsAppDeactive = true;
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        setSupportActionBar(this.toolbar);
        getSupportActionBar();
        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                GalleryActivity.this.Back();
            }
        });
        this.galleryGrid = (GridView) findViewById(R.id.customGalleryGrid);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.ll_Show_Params = new LinearLayout.LayoutParams(-1, -2);
        this.ll_Hide_Params = new LinearLayout.LayoutParams(-2, 0);
        this.ll_GridviewParams = new LayoutParams(-1, -1);
        this.appSettingsSharedPreferences = AppSettingsSharedPreferences.GetObject(this);
        this._SortBy = this.appSettingsSharedPreferences.GetGallerySortBy();
        _ViewBy = this.appSettingsSharedPreferences.GetGalleryViewBy();
        this.fl_top_baar = (FrameLayout) findViewById(R.id.fl_top_baar);
        this.fl_top_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_Edit = (LinearLayout) findViewById(R.id.ll_Edit);
        this.ll_AddInGallery_Baar = (LinearLayout) findViewById(R.id.ll_AddInGallery_Baar);
        this.btnSelectAll = (ImageButton) findViewById(R.id.btnSelectAll);
        this.ll_photo_video_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.ll_photo_video_grid = (LinearLayout) findViewById(R.id.ll_photo_video_grid);
        this.photo_video_empty_icon = (ImageView) findViewById(R.id.photo_video_empty_icon);
        this.lbl_photo_video_empty = (TextView) findViewById(R.id.lbl_photo_video_empty);
        this.ll_photo_video_grid.setVisibility(View.VISIBLE);
        this.ll_photo_video_empty.setVisibility(View.INVISIBLE);
        this.ll_import_Photos_from_gallery_btn = (LinearLayout) findViewById(R.id.ll_import_from_Photo_gallery_btn);
        this.ll_import_videos_from_gallery_btn = (LinearLayout) findViewById(R.id.ll_import_from_video_gallery_btn);
        this.ll_delete_btn = (LinearLayout) findViewById(R.id.ll_delete_btn);
        this.ll_unhide_btn = (LinearLayout) findViewById(R.id.ll_unhide_btn);
        this.ll_share_btn = (LinearLayout) findViewById(R.id.ll_share_btn);
        this._btnSortingDropdown = (ImageButton) findViewById(R.id.btnSort);
        this.galleryGrid.setOnItemClickListener(new galleryListners());
        this.galleryGrid.setOnItemLongClickListener(new galleryListners());
        this.ll_import_Photos_from_gallery_btn.setOnClickListener(new galleryListners());
        this.ll_import_videos_from_gallery_btn.setOnClickListener(new galleryListners());
        this.ll_delete_btn.setOnClickListener(new galleryListners());
        this.ll_unhide_btn.setOnClickListener(new galleryListners());
        this.ll_share_btn.setOnClickListener(new galleryListners());
        this.ll_background.setOnTouchListener(new galleryListners());
        this.btnSelectAll.setOnClickListener(new galleryListners());
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(3);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(3);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(2);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(4);
            } else {
                setUIforlistView();
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(3);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(7);
            } else {
                setUIforlistView();
            }
        }
        LoadGalleryFilesFromDB(this._SortBy);
    }

    private void LoadGalleryFilesFromDB(int i) {
        new ArrayList();
        PhotoDAL photoDAL = new PhotoDAL(this);
        photoDAL.OpenRead();
        List<Photo> GetPhotos = photoDAL.GetPhotos();
        photoDAL.close();
        for (Photo photo : GetPhotos) {
            GalleryEnt galleryEnt = new GalleryEnt();
            galleryEnt.set_albumId(photo.getAlbumId());
            galleryEnt.set_folderLockgalleryfileLocation(photo.getFolderLockPhotoLocation());
            galleryEnt.set_galleryfileName(photo.getPhotoName());
            galleryEnt.set_id(photo.getId());
            galleryEnt.set_isCheck(Boolean.valueOf(false));
            galleryEnt.set_isVideo(Boolean.valueOf(false));
            galleryEnt.set_modifiedDateTime(photo.get_modifiedDateTime());
            galleryEnt.set_originalgalleryfileLocation(photo.getOriginalPhotoLocation());
            galleryEnt.set_thumbnail_video_location("");
            this.mGalleryGirdList.add(galleryEnt);
        }
        new ArrayList();
        VideoDAL videoDAL = new VideoDAL(this);
        videoDAL.OpenRead();
        List<Video> GetVideos = videoDAL.GetVideos();
        videoDAL.close();
        for (Video video : GetVideos) {
            GalleryEnt galleryEnt2 = new GalleryEnt();
            galleryEnt2.set_albumId(video.getAlbumId());
            galleryEnt2.set_folderLockgalleryfileLocation(video.getFolderLockVideoLocation());
            galleryEnt2.set_galleryfileName(video.getVideoName());
            galleryEnt2.set_id(video.getId());
            galleryEnt2.set_isCheck(Boolean.valueOf(false));
            galleryEnt2.set_isVideo(Boolean.valueOf(true));
            galleryEnt2.set_modifiedDateTime(video.get_modifiedDateTime());
            galleryEnt2.set_originalgalleryfileLocation(video.getOriginalVideoLocation());
            galleryEnt2.set_thumbnail_video_location(video.getthumbnail_video_location());
            this.mGalleryGirdList.add(galleryEnt2);
        }
        Collections.sort(this.mGalleryGirdList, new GalleryFileSort(i));
        Collections.reverse(this.mGalleryGirdList);
        for (GalleryEnt galleryEnt3 : this.mGalleryGirdList) {
            if (!galleryEnt3.get_isVideo().booleanValue()) {
                this.mPhotosList.add(galleryEnt3.get_folderLockgalleryfileLocation());
            }
        }
        GalleryFeatureAdapter galleryFeatureAdapter2 = new GalleryFeatureAdapter(this, 1, this.mGalleryGirdList, false, _ViewBy);
        this.galleryFeatureAdapter = galleryFeatureAdapter2;
        this.galleryGrid.setAdapter(this.galleryFeatureAdapter);
        this.galleryFeatureAdapter.notifyDataSetChanged();
        if (this.mGalleryGirdList.size() < 1) {
            this.ll_photo_video_grid.setVisibility(View.INVISIBLE);
            this.ll_photo_video_empty.setVisibility(View.VISIBLE);
            this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_gallery_empty_icon);
            this.lbl_photo_video_empty.setText(R.string.lbl_No_Gallery_File);
            return;
        }
        this.ll_photo_video_grid.setVisibility(View.VISIBLE);
        this.ll_photo_video_empty.setVisibility(View.INVISIBLE);
    }

    public void UnhideGalleryFiles() {
        if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectphotomsg_unhide, Toast.LENGTH_SHORT).show();
            return;
        }
        SelectedCount();
        if (Common.GetTotalFree() > Common.GetFileSize(this.files)) {
            final Dialog dialog = new Dialog(this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.confirmation_message_box);
            dialog.setCancelable(true);
            TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
            textView.setTypeface(Typeface.createFromAsset(getAssets(), "ebrima.ttf"));
            StringBuilder sb = new StringBuilder();
            sb.append("Are you sure you want to restore (");
            sb.append(this.selectCount);
            sb.append(") photo(s) or video(s)?");
            textView.setText(sb.toString());
            ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    GalleryActivity.this.showUnhideProgress();
                    new Thread() {
                        public void run() {
                            try {
                                dialog.dismiss();
                                Common.isUnHide = true;
                                Log.e("before","before");
                                GalleryActivity.this.Unhide();

                                Log.e("after","after");
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                GalleryActivity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 3;

                                Log.e("unused","==="+unused.toString());
                                GalleryActivity.this.handle.sendMessage(message2);

                            }
                        }
                    }.start();
                    dialog.dismiss();
                }
            });
            ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }


    public void Unhide() throws IOException {
        for (int i = 0; i < this.mGalleryGirdList.size(); i++) {
            if (((GalleryEnt) this.mGalleryGirdList.get(i)).get_isCheck().booleanValue()) {
                if (!Utilities.NSUnHideFile(this, ((GalleryEnt) this.mGalleryGirdList.get(i)).get_folderLockgalleryfileLocation(), ((GalleryEnt) this.mGalleryGirdList.get(i)).get_originalgalleryfileLocation())) {
                    Toast.makeText(this, R.string.Unhide_error, Toast.LENGTH_SHORT).show();
                } else if (((GalleryEnt) this.mGalleryGirdList.get(i)).get_isVideo().booleanValue()) {

                    Log.e("Gounhide","Gounhide");
                    DeleteVideosFromDatabase(((GalleryEnt) this.mGalleryGirdList.get(i)).get_id());
                } else {
                    DeletePhotosFromDatabase(((GalleryEnt) this.mGalleryGirdList.get(i)).get_id());
                }
            }
        }
    }

    public void DeleteGalleryFiles() {
        if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectphotomsg_delete, Toast.LENGTH_SHORT).show();
            return;
        }
        SelectedCount();
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.confirmation_message_box);
        dialog.setCancelable(true);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
        textView.setTypeface(Typeface.createFromAsset(getAssets(), "ebrima.ttf"));
        StringBuilder sb = new StringBuilder();
        sb.append("Are you sure you want to delete (");
        sb.append(this.selectCount);
        sb.append(") photo(s) or video(s)?");
        textView.setText(sb.toString());
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                GalleryActivity.this.showDeleteProgress();
                new Thread() {
                    public void run() {
                        try {
                            Common.isDelete = true;
                            dialog.dismiss();
                            GalleryActivity.this.Delete();
                            Common.IsWorkInProgress = true;
                            Message message = new Message();
                            message.what = 3;
                            GalleryActivity.this.handle.sendMessage(message);
                            Common.IsWorkInProgress = false;
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 3;
                            GalleryActivity.this.handle.sendMessage(message2);
                        }
                    }
                }.start();
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void SelectedCount() {
        this.files.clear();
        this.selectCount = 0;
        for (int i = 0; i < this.mGalleryGirdList.size(); i++) {
            if (((GalleryEnt) this.mGalleryGirdList.get(i)).get_isCheck().booleanValue()) {
                this.files.add(((GalleryEnt) this.mGalleryGirdList.get(i)).get_folderLockgalleryfileLocation());
                this.selectCount++;
            }
        }
    }


    public boolean IsFileCheck() {
        for (int i = 0; i < this.mGalleryGirdList.size(); i++) {
            if (((GalleryEnt) this.mGalleryGirdList.get(i)).get_isCheck().booleanValue()) {
                return true;
            }
        }
        return false;
    }


    public void Delete() {
        for (int i = 0; i < this.mGalleryGirdList.size(); i++) {
            if (((GalleryEnt) this.mGalleryGirdList.get(i)).get_isCheck().booleanValue()) {
                new File(((GalleryEnt) this.mGalleryGirdList.get(i)).get_folderLockgalleryfileLocation()).delete();
                new File(((GalleryEnt) this.mGalleryGirdList.get(i)).get_thumbnail_video_location()).delete();
                if (((GalleryEnt) this.mGalleryGirdList.get(i)).get_isVideo().booleanValue()) {
                    DeleteVideosFromDatabase(((GalleryEnt) this.mGalleryGirdList.get(i)).get_id());
                } else {
                    DeletePhotosFromDatabase(((GalleryEnt) this.mGalleryGirdList.get(i)).get_id());
                }
            }
        }
    }

    public void DeletePhotosFromDatabase(int i) {
        PhotoDAL photoDAL = new PhotoDAL(this);
        try {
            photoDAL.OpenWrite();
            photoDAL.DeletePhotoById(i);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            photoDAL.close();
            throw th;
        }
        photoDAL.close();
    }

    public void DeleteVideosFromDatabase(int i) {
        VideoDAL videoDAL = new VideoDAL(this);
        try {
            videoDAL.OpenWrite();
            videoDAL.DeleteVideoById(i);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            videoDAL.close();
            throw th;
        }
        videoDAL.close();
    }

    public void ShareGalleryFiles() {
        showCopyFilesProcessForShareProgress();
        new Thread() {
            public void run() {
                try {
                    SecurityLocksCommon.IsAppDeactive = false;
                    ArrayList arrayList = new ArrayList();
                    Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
                    intent.setType("image/*");
                    for (ResolveInfo resolveInfo : GalleryActivity.this.getPackageManager().queryIntentActivities(intent, 0)) {
                        String str = resolveInfo.activityInfo.packageName;
                        if (!str.equals(AppPackageName) && !str.equals("com.dropbox.android") && !str.equals("com.facebook.katana")) {
                            Intent intent2 = new Intent("android.intent.action.SEND_MULTIPLE");
                            intent2.setType("image/*");
                            intent2.setPackage(str);
                            arrayList.add(intent2);
                            StringBuilder sb = new StringBuilder();
                            sb.append(StorageOptionsCommon.STORAGEPATH);
                            sb.append(StorageOptionsCommon.TEMPFILES);
                            String sb2 = sb.toString();
                            ArrayList arrayList2 = new ArrayList();
                            ArrayList arrayList3 = new ArrayList();
                            String str2 = sb2;
                            for (int i = 0; i < GalleryActivity.this.mGalleryGirdList.size(); i++) {
                                if (((GalleryEnt) GalleryActivity.this.mGalleryGirdList.get(i)).get_isCheck().booleanValue()) {
                                    try {
                                        str2 = Utilities.CopyTemporaryFile(GalleryActivity.this, ((GalleryEnt) GalleryActivity.this.mGalleryGirdList.get(i)).get_folderLockgalleryfileLocation(), str2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    arrayList2.add(str2);
                                    arrayList3.add(FileProvider.getUriForFile(GalleryActivity.this, AppPackageName, new File(str2)));
                                }
                            }
                            intent2.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList3);
                        }
                    }
                    Intent createChooser = Intent.createChooser((Intent) arrayList.remove(0), "Share Via");
                    createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) arrayList.toArray(new Parcelable[0]));
                    GalleryActivity.this.startActivity(createChooser);
                    Message message = new Message();
                    message.what = 4;
                    GalleryActivity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 4;
                    GalleryActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }

    public void DeleteTemporaryGalleryFiles() {
        File[] listFiles;
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.TEMPFILES);
        sb.append("/");
        File file = new File(sb.toString());
        if (file.exists()) {
            for (File file2 : file.listFiles()) {
                if (file2.isFile()) {
                    file2.delete();
                }
            }
        }
    }

    private void SetcheckFlase() {
        for (int i = 0; i < this.mGalleryGirdList.size(); i++) {
            ((GalleryEnt) this.mGalleryGirdList.get(i)).set_isCheck(Boolean.valueOf(false));
        }
        GalleryFeatureAdapter galleryFeatureAdapter2 = new GalleryFeatureAdapter(this, 1, this.mGalleryGirdList, false, _ViewBy);
        this.galleryFeatureAdapter = galleryFeatureAdapter2;
        this.galleryGrid.setAdapter(this.galleryFeatureAdapter);
        this.galleryFeatureAdapter.notifyDataSetChanged();
        if (Common.GalleryThumbnailCurrentPosition != 0) {
            this.galleryGrid.setSelection(Common.GalleryThumbnailCurrentPosition);
            Common.GalleryThumbnailCurrentPosition = 0;
        }
    }


    public void SelectOrUnSelectAll() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.mGalleryGirdList.size(); i++) {
                ((GalleryEnt) this.mGalleryGirdList.get(i)).set_isCheck(Boolean.valueOf(false));
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_unselectallicon);
        } else {
            for (int i2 = 0; i2 < this.mGalleryGirdList.size(); i2++) {
                ((GalleryEnt) this.mGalleryGirdList.get(i2)).set_isCheck(Boolean.valueOf(true));
            }
            this.IsSelectAll = true;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_selectallicon);
        }
        GalleryFeatureAdapter galleryFeatureAdapter2 = new GalleryFeatureAdapter(this, 1, this.mGalleryGirdList, true, _ViewBy);
        this.galleryFeatureAdapter = galleryFeatureAdapter2;
        this.galleryGrid.setAdapter(this.galleryFeatureAdapter);
        this.galleryFeatureAdapter.notifyDataSetChanged();
    }

    public void setUIforlistView() {
        this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 1));
        this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 0));
        this.ll_GridviewParams.setMargins(0, 0, 0, 0);
        this.ll_photo_video_grid.setLayoutParams(this.ll_GridviewParams);
        this.galleryGrid.setNumColumns(1);
    }

    public void btnSortonClick(View view) {
        this.IsSortingDropdown = false;
        showPopupWindow();
    }

    public void showPopupWindow() {
        View inflate = ((LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_window_expandable, null);
        final PopupWindow popupWindow = new PopupWindow(inflate, -2, -2, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        ArrayList arrayList = new ArrayList();
        HashMap hashMap = new HashMap();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ExpandableListView expandableListView = (ExpandableListView) inflate.findViewById(R.id.expListview);
        arrayList.add("View by");
        arrayList2.add("List");
        arrayList2.add("Tiles");
        arrayList2.add("Large tiles");
        hashMap.put(arrayList.get(0), arrayList2);
        arrayList.add("Sort by");
        arrayList3.add("Name");
        arrayList3.add("Date");
        arrayList3.add("Size");
        hashMap.put(arrayList.get(1), arrayList3);
        expandableListView.setAdapter(new ExpandableListAdapter1(this, arrayList, hashMap));
        expandableListView.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
                if (i != 0) {
                    if (i == 1) {
                        switch (i2) {
                            case 0:
                                GalleryActivity.this._SortBy = SortBy.Name.ordinal();
                                GalleryActivity.this.SortGalleryFiles();
                                GalleryActivity.this.AddSortInSharedPerference();
                                popupWindow.dismiss();
                                GalleryActivity.this.IsSortingDropdown = false;
                                break;
                            case 1:
                                GalleryActivity.this._SortBy = SortBy.Time.ordinal();
                                GalleryActivity.this.SortGalleryFiles();
                                GalleryActivity.this.AddSortInSharedPerference();
                                popupWindow.dismiss();
                                GalleryActivity.this.IsSortingDropdown = false;
                                break;
                            case 2:
                                GalleryActivity.this._SortBy = SortBy.Size.ordinal();
                                GalleryActivity.this.SortGalleryFiles();
                                GalleryActivity.this.AddSortInSharedPerference();
                                popupWindow.dismiss();
                                GalleryActivity.this.IsSortingDropdown = false;
                                break;
                        }
                    }
                } else {
                    switch (i2) {
                        case 0:
                            GalleryActivity._ViewBy = ViewBy.List.ordinal();
                            GalleryActivity.this.ViewBy();
                            popupWindow.dismiss();
                            GalleryActivity.this.IsSortingDropdown = false;
                            GalleryActivity.this.appSettingsSharedPreferences.SetGalleryViewBy(GalleryActivity._ViewBy);
                            break;
                        case 1:
                            GalleryActivity._ViewBy = ViewBy.Tiles.ordinal();
                            GalleryActivity.this.ViewBy();
                            popupWindow.dismiss();
                            GalleryActivity.this.IsSortingDropdown = false;
                            GalleryActivity.this.appSettingsSharedPreferences.SetGalleryViewBy(GalleryActivity._ViewBy);
                            break;
                        case 2:
                            GalleryActivity._ViewBy = ViewBy.LargeTiles.ordinal();
                            GalleryActivity.this.ViewBy();
                            popupWindow.dismiss();
                            GalleryActivity.this.IsSortingDropdown = false;
                            GalleryActivity.this.appSettingsSharedPreferences.SetGalleryViewBy(GalleryActivity._ViewBy);
                            break;
                    }
                }
                return false;
            }
        });
        if (!this.IsSortingDropdown) {
            LinearLayout linearLayout = this.ll_anchor;
            popupWindow.showAsDropDown(linearLayout, linearLayout.getWidth(), 0);
            this.IsSortingDropdown = true;
            return;
        }
        popupWindow.dismiss();
        this.IsSortingDropdown = false;
    }


    public void SortGalleryFiles() {
        Collections.sort(this.mGalleryGirdList, new GalleryFileSort(this._SortBy));
        Collections.reverse(this.mGalleryGirdList);
        this.galleryFeatureAdapter.SetGalleryFilesList(this.mGalleryGirdList);
        this.galleryFeatureAdapter.notifyDataSetChanged();
        for (GalleryEnt galleryEnt : this.mGalleryGirdList) {
            if (!galleryEnt.get_isVideo().booleanValue()) {
                this.mPhotosList.add(galleryEnt.get_folderLockgalleryfileLocation());
            }
        }
    }


    public void AddSortInSharedPerference() {
        this.appSettingsSharedPreferences.SetGallerySortBy(this._SortBy);
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (configuration.orientation == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(3);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(7);
            } else {
                setUIforlistView();
            }
        } else if (configuration.orientation != 1) {
        } else {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(3);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(3);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(2);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(4);
            } else {
                setUIforlistView();
            }
        }
    }

    public void ViewBy() {
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(3);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(3);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(2);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(4);
            } else {
                setUIforlistView();
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(3);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(7);
            } else {
                setUIforlistView();
            }
        }
        GalleryFeatureAdapter galleryFeatureAdapter2 = new GalleryFeatureAdapter(this, 1, this.mGalleryGirdList, false, _ViewBy);
        this.galleryFeatureAdapter = galleryFeatureAdapter2;
        this.galleryGrid.setAdapter(this.galleryFeatureAdapter);
        this.galleryFeatureAdapter.notifyDataSetChanged();
    }

    public void btnBackonClick(View view) {
        Back();
    }

    public void Back() {
        if (this.isEditMode) {
            SetcheckFlase();
            this.isEditMode = false;
            this.IsSortingDropdown = false;
            this.fl_top_baar.setLayoutParams(this.ll_Hide_Params);
            this.ll_AddInGallery_Baar.setVisibility(View.INVISIBLE);
            this.ll_Edit.setVisibility(View.INVISIBLE);
            this.IsSelectAll = false;
            this.btnSelectAll.setVisibility(View.INVISIBLE);
            this._btnSortingDropdown.setVisibility(View.VISIBLE);
            invalidateOptionsMenu();
        } else if (this.isAddbarvisible) {
            this.isAddbarvisible = false;
            this.fl_top_baar.setLayoutParams(this.ll_Hide_Params);
            this.ll_AddInGallery_Baar.setVisibility(View.INVISIBLE);
            this.ll_Edit.setVisibility(View.INVISIBLE);
            this.IsSortingDropdown = false;
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            Common.FolderId = 0;
            Common.PhotoFolderName = "My Photos";
            startActivity(new Intent(this, MainiFeaturesActivity.class));
            finish();
        }
        DeleteTemporaryGalleryFiles();
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


    public void onPause() {
        Common.IsWorkInProgress = true;
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
        this.handle.removeCallbacksAndMessages(null);
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive) {
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
        SetcheckFlase();
        Utilities.changeFileExtention(StorageOptionsCommon.VIDEOS);
        this.isEditMode = false;
        this.fl_top_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_AddInGallery_Baar.setVisibility(View.INVISIBLE);
        this.ll_Edit.setVisibility(View.INVISIBLE);
        this.IsSelectAll = false;
        this.btnSelectAll.setVisibility(View.INVISIBLE);
        invalidateOptionsMenu();
        super.onResume();
    }


    public void onStop() {
        Common.imageLoader.clearMemoryCache();
        Common.imageLoader.clearDiskCache();
        super.onStop();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            Common.isOpenCameraorGalleryFromApp = false;
            if (this.isEditMode) {
                SetcheckFlase();
                this.IsSortingDropdown = false;
                this.isEditMode = false;
                this.fl_top_baar.setLayoutParams(this.ll_Hide_Params);
                this.ll_AddInGallery_Baar.setVisibility(View.INVISIBLE);
                this.ll_Edit.setVisibility(View.INVISIBLE);
                this.IsSelectAll = false;
                this.btnSelectAll.setVisibility(View.INVISIBLE);
                this._btnSortingDropdown.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
                return true;
            } else if (this.isAddbarvisible) {
                this.isAddbarvisible = false;
                this.fl_top_baar.setLayoutParams(this.ll_Hide_Params);
                this.ll_AddInGallery_Baar.setVisibility(View.INVISIBLE);
                this.ll_Edit.setVisibility(View.INVISIBLE);
                this.IsSortingDropdown = false;
                return true;
            } else {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.FolderId = 0;
                Common.PhotoFolderName = "My Photos";
                startActivity(new Intent(this, MainiFeaturesActivity.class));
                finish();
                DeleteTemporaryGalleryFiles();
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void PlayVideo(String str) {
        File file = new File(str);
        String str2 = "";
        SecurityLocksCommon.IsAppDeactive = false;
        if (file.exists()) {
            try {
                str2 = Utilities.NSVideoDecryptionDuringPlay(new File(str));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(file.getParent());
            sb.append(File.separator);
            sb.append(Utilities.ChangeFileExtentionToOrignal(Utilities.FileName(file.getAbsolutePath())));
            str2 = sb.toString();
        }
        String substring = str2.substring(str2.lastIndexOf(".") + 1);
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            File file2 = new File(str2);
            intent.setDataAndType(FileProvider.getUriForFile(this, AppPackageName, file2), MimeTypeMap.getSingleton().getMimeTypeFromExtension(substring));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e2) {
            e2.getMessage();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more, menu);
        menu.findItem(R.id.action_more);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.action_more) {
            this.IsSortingDropdown = false;
            showPopupWindow();
            return true;
        } else if (itemId != R.id.action_select) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            if (this.IsSelectAll) {
                for (int i = 0; i < this.mGalleryGirdList.size(); i++) {
                    ((GalleryEnt) this.mGalleryGirdList.get(i)).set_isCheck(Boolean.valueOf(false));
                }
                this.IsSelectAll = false;
                menuItem.setIcon(R.drawable.ic_unselectallicon);
                invalidateOptionsMenu();
            } else {
                for (int i2 = 0; i2 < this.mGalleryGirdList.size(); i2++) {
                    ((GalleryEnt) this.mGalleryGirdList.get(i2)).set_isCheck(Boolean.valueOf(true));
                }
                this.IsSelectAll = true;
                menuItem.setIcon(R.drawable.ic_selectallicon);
            }
            GalleryFeatureAdapter galleryFeatureAdapter2 = new GalleryFeatureAdapter(this, 1, this.mGalleryGirdList, true, _ViewBy);
            this.galleryFeatureAdapter = galleryFeatureAdapter2;
            this.galleryGrid.setAdapter(this.galleryFeatureAdapter);
            this.galleryFeatureAdapter.notifyDataSetChanged();
            return true;
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.isEditMode) {
            menu.findItem(R.id.action_more).setVisible(false);
            getMenuInflater().inflate(R.menu.menu_selection, menu);
        } else {
            menu.findItem(R.id.action_more).setVisible(true);
            if (this.IsSelectAll && this.isEditMode) {
                menu.findItem(R.id.action_select).setIcon(R.drawable.ic_unselectallicon);
            } else if (!this.IsSelectAll && this.isEditMode) {
                menu.findItem(R.id.action_select).setIcon(R.drawable.ic_selectallicon);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
