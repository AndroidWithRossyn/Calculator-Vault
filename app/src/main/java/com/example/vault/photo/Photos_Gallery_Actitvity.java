package com.example.vault.photo;

import static com.example.vault.utilities.Common.AppPackageName;

import android.annotation.SuppressLint;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.vault.R;
import com.example.vault.adapter.ExpandableListAdapter1;
import com.example.vault.BaseActivity;
import com.example.vault.common.Constants;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.photo.adapter.MoveAlbumAdapter;
import com.example.vault.photo.adapter.PhoneGalleryPhotoAdapter;
import com.example.vault.photo.model.ImportEnt;
import com.example.vault.photo.model.Photo;
import com.example.vault.photo.model.PhotoAlbum;
import com.example.vault.photo.util.AlbumsGalleryPhotosMethods;
import com.example.vault.photo.util.PhotoAlbumDAL;
import com.example.vault.photo.util.PhotoDAL;
import com.example.vault.privatebrowser.SecureBrowserActivity;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.securitylocks.SecurityLocksSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.banrossyn.imageloader.utils.LibCommonAppClass;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Photos_Gallery_Actitvity extends BaseActivity {
    private static int RESULT_LOAD_CAMERA = 1;
    public static int _ViewBy = 1;
    public static ProgressDialog myProgressDialog;
    private boolean IsSelectAll = false;
    boolean IsSortingDropdown = false;
    int _SortBy = 1;
    private String[] _albumNameArray;
    public List<String> _albumNameArrayForMove = null;
    ImageButton _btnSortingDropdown;
    String albumName;
    private FloatingActionButton btnAdd;
    ImageButton btnSelectAll;
    File cacheDir;
    private FloatingActionButton fabImpBrowser;
    private FloatingActionButton fabImpCam;
    private FloatingActionButton fabImpGallery;
    private FloatingActionButton fabImpPcMac;
    FloatingActionsMenu fabMenu;
    private ArrayList<String> files = new ArrayList<>();
    FrameLayout fl_bottom_baar;
    protected String folderLocation;

    public PhoneGalleryPhotoAdapter galleryImagesAdapter;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 2) {
                hideProgress();
                if (Common.isUnHide) {
                    Common.isUnHide = false;
                    Toast.makeText(Photos_Gallery_Actitvity.this, R.string.Unhide_error, Toast.LENGTH_SHORT).show();
                } else if (Common.isMove) {
                    Common.isMove = false;
                    Toast.makeText(Photos_Gallery_Actitvity.this, R.string.Move_error, Toast.LENGTH_SHORT).show();
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(Photos_Gallery_Actitvity.this, R.string.Delete_error, Toast.LENGTH_SHORT).show();
                }
            } else if (message.what == 4) {
                hideProgress();
                Toast.makeText(Photos_Gallery_Actitvity.this, R.string.toast_share, Toast.LENGTH_LONG).show();
            } else if (message.what == 3) {
                if (Common.isUnHide) {
                    if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        Photos_Gallery_Actitvity photos_Gallery_Actitvity = Photos_Gallery_Actitvity.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append(Constants.FILE);
                        sb.append(Environment.getExternalStorageDirectory());
                        photos_Gallery_Actitvity.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb.toString())));
                    } else {
                        RefershGalleryforKitkat();
                    }
                    Common.isUnHide = false;
                    Toast.makeText(Photos_Gallery_Actitvity.this, R.string.toast_unhide, Toast.LENGTH_LONG).show();
                    hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent = new Intent(Photos_Gallery_Actitvity.this, Photos_Gallery_Actitvity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(Photos_Gallery_Actitvity.this, R.string.toast_delete, Toast.LENGTH_SHORT).show();
                    hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent2 = new Intent(Photos_Gallery_Actitvity.this, Photos_Gallery_Actitvity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent2);
                        finish();
                    }
                } else if (Common.isMove) {
                    Common.isMove = false;
                    Toast.makeText(Photos_Gallery_Actitvity.this, R.string.toast_move, Toast.LENGTH_SHORT).show();
                    hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent3 = new Intent(Photos_Gallery_Actitvity.this, Photos_Gallery_Actitvity.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent3);
                        finish();
                    }
                }
            }
            super.handleMessage(message);
        }
    };
    GridView imagegrid;
    boolean isAddbarvisible = false;
    boolean isEditMode = false;
    TextView lbl_album_name_topbaar;
    TextView lbl_photo_video_empty;
    LinearLayout ll_AddPhotos_Bottom_Baar;
    LayoutParams ll_EditAlbum_Hide_Params;
    LayoutParams ll_EditAlbum_Show_Params;
    LinearLayout ll_EditPhotos;
    RelativeLayout.LayoutParams ll_GridviewParams;
    LayoutParams ll_Hide_Params;
    LayoutParams ll_Show_Params;
    LinearLayout ll_anchor;
    LinearLayout ll_background;
    LinearLayout ll_delete_btn;
    LinearLayout ll_import_from_camera_btn;
    LinearLayout ll_import_from_gallery_btn;
    LinearLayout ll_import_intenet_btn;
    LinearLayout ll_move_btn;
    LinearLayout ll_photo_video_empty;
    LinearLayout ll_photo_video_grid;
    LinearLayout ll_share_btn;
    LinearLayout ll_topbaar;
    LinearLayout ll_unhide_btn;
    private PhotoAlbum m_photoAlbum;
    public String moveToFolderLocation;
    private Uri outputFileUri;
    private PhotoAlbumDAL photoAlbumDAL;
    private int photoCount = 0;
    private PhotoDAL photoDAL;
    private ArrayList<ImportEnt> photoImportEntListShow = new ArrayList<>();
    ImageView photo_video_empty_icon;

    public List<Photo> photos;
    int selectCount;
    private SensorManager sensorManager;
    Toolbar toolbar;

    public enum SortBy {
        Time, Name, Size
    }

    public enum ViewBy {
        LargeTiles, Tiles, List
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


    public void showMoveProgress() {
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
        setContentView((int) R.layout.photos_videos_gallery_activity);
        LibCommonAppClass.IsPhoneGalleryLoad = true;
        Log.d("TAG", "PhotoGalleryActivity");
        SecurityLocksCommon.IsAppDeactive = true;

//        ImageView bgmainImg=findViewById(R.id.bgmainImg);
//        bgmainImg.setImageResource(R.drawable.bg_ic_photos);


        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Back();
            }
        });
        TextView title9 = findViewById(R.id.title9);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_Show_Params = new LayoutParams(-1, -2);
        this.ll_Hide_Params = new LayoutParams(-2, 0);
        this.ll_GridviewParams = new RelativeLayout.LayoutParams(-1, -1);
        this.fl_bottom_baar = (FrameLayout) findViewById(R.id.fl_bottom_baar);
        this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_AddPhotos_Bottom_Baar = (LinearLayout) findViewById(R.id.ll_AddPhotos_Bottom_Baar);
        this.ll_EditPhotos = (LinearLayout) findViewById(R.id.ll_EditPhotos);
        this.imagegrid = (GridView) findViewById(R.id.customGalleryGrid);
        this.btnSelectAll = (ImageButton) findViewById(R.id.btnSelectAll);
        this.fabMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu);
        this.fabImpCam = (FloatingActionButton) findViewById(R.id.btn_impCam);
        this.fabImpGallery = (FloatingActionButton) findViewById(R.id.btn_impGallery);
        this.fabImpBrowser = (FloatingActionButton) findViewById(R.id.btn_impBrowser);
        this.fabImpPcMac = (FloatingActionButton) findViewById(R.id.btn_impPcMac);
        this.fabImpGallery.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                startActivity(new Intent(Photos_Gallery_Actitvity.this, ImportAlbumsGalleryPhotoActivity.class));
                finish();
            }
        });

        this.fabImpCam.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Common.isOpenCameraorGalleryFromApp = true;
                openCameraIntent();
            }
        });


        this.fabImpBrowser.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Photos_Gallery_Actitvity photos_Gallery_Actitvity = Photos_Gallery_Actitvity.this;
                Common.CurrentWebBrowserActivity = photos_Gallery_Actitvity;
                startActivity(new Intent(photos_Gallery_Actitvity, SecureBrowserActivity.class));
                finish();
            }
        });
        this.ll_import_from_gallery_btn = (LinearLayout) findViewById(R.id.ll_import_from_gallery_btn);
        this.ll_import_from_camera_btn = (LinearLayout) findViewById(R.id.ll_import_from_camera_btn);
        this.ll_import_intenet_btn = (LinearLayout) findViewById(R.id.ll_import_intenet_btn);
        this.ll_delete_btn = (LinearLayout) findViewById(R.id.ll_delete_btn);
        this.ll_unhide_btn = (LinearLayout) findViewById(R.id.ll_unhide_btn);
        this.ll_move_btn = (LinearLayout) findViewById(R.id.ll_move_btn);
        this.ll_share_btn = (LinearLayout) findViewById(R.id.ll_share_btn);
        this._btnSortingDropdown = (ImageButton) findViewById(R.id.btnSort);
        this.ll_photo_video_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.ll_photo_video_grid = (LinearLayout) findViewById(R.id.ll_photo_video_grid);
        this.photo_video_empty_icon = (ImageView) findViewById(R.id.photo_video_empty_icon);
        this.lbl_photo_video_empty = (TextView) findViewById(R.id.lbl_photo_video_empty);
        this.ll_photo_video_grid.setVisibility(View.VISIBLE);
        this.ll_photo_video_empty.setVisibility(View.INVISIBLE);
        this.btnSelectAll.setVisibility(View.INVISIBLE);
        this.lbl_album_name_topbaar = (TextView) findViewById(R.id.lbl_album_name_topbaar);
        PhotoAlbumDAL photoAlbumDAL2 = new PhotoAlbumDAL(this);
        photoAlbumDAL2.OpenRead();
        PhotoAlbum GetAlbumById = photoAlbumDAL2.GetAlbumById(Integer.toString(Common.FolderId));
        this._SortBy = photoAlbumDAL2.GetSortByAlbumId(Common.FolderId);
        photoAlbumDAL2.close();
        this.albumName = GetAlbumById.getAlbumName();
        Common.PhotoFolderName = this.albumName;
        this.folderLocation = GetAlbumById.getAlbumLocation();
        this.lbl_album_name_topbaar.setText(this.albumName);
//        getSupportActionBar().setTitle((CharSequence) this.albumName);
        // getSupportActionBar().setTitle("");

        title9.setText(this.albumName);

        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(3);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(3);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(2);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(2);
            } else {
                setUIforlistView();
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(3);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(7);
            } else {
                setUIforlistView();
            }
        }
        this.ll_background.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (IsSortingDropdown) {
                    IsSortingDropdown = false;
                }
                if (IsSortingDropdown) {
                    IsSortingDropdown = false;
                }
                return false;
            }
        });
        this.imagegrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (!isEditMode) {
                    Common.PhotoThumbnailCurrentPosition = imagegrid.getFirstVisiblePosition();
                    SecurityLocksCommon.IsAppDeactive = false;
                    Common.IsCameFromAppGallery = true;
                    Intent intent = new Intent(Photos_Gallery_Actitvity.this, NewFullScreenViewActivity.class);
                    intent.putExtra("IMAGE_URL", ((Photo) photos.get(i)).getFolderLockPhotoLocation());
                    intent.putExtra("IMAGE_POSITION", i);
                    intent.putExtra("ALBUM_ID", ((Photo) photos.get(i)).getAlbumId());
                    intent.putExtra("ALBUM_NAME", albumName);
                    intent.putExtra("ALBUM_LOCATION", folderLocation);
                    intent.putExtra("_SortBy", _SortBy);
                    startActivity(intent);
                    finish();
                }
            }
        });
        this.imagegrid.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                Common.PhotoThumbnailCurrentPosition = imagegrid.getFirstVisiblePosition();
                Photos_Gallery_Actitvity photos_Gallery_Actitvity = Photos_Gallery_Actitvity.this;
                photos_Gallery_Actitvity.isEditMode = true;
                photos_Gallery_Actitvity.fl_bottom_baar.setLayoutParams(ll_Show_Params);
                ll_AddPhotos_Bottom_Baar.setVisibility(View.INVISIBLE);
                ll_EditPhotos.setVisibility(View.VISIBLE);
                _btnSortingDropdown.setVisibility(View.INVISIBLE);
                btnSelectAll.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
                ((Photo) photos.get(i)).SetFileCheck(true);
                Photos_Gallery_Actitvity photos_Gallery_Actitvity2 = Photos_Gallery_Actitvity.this;
                PhoneGalleryPhotoAdapter phoneGalleryPhotoAdapter = new PhoneGalleryPhotoAdapter(photos_Gallery_Actitvity2, 1, photos_Gallery_Actitvity2.photos, true, Photos_Gallery_Actitvity._ViewBy);
                photos_Gallery_Actitvity2.galleryImagesAdapter = phoneGalleryPhotoAdapter;
                imagegrid.setAdapter(galleryImagesAdapter);
                galleryImagesAdapter.notifyDataSetChanged();
                if (Common.PhotoThumbnailCurrentPosition != 0) {
                    imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
                }
                return true;
            }
        });
        this.btnSelectAll.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SelectOrUnSelectAll();
            }
        });
        this.ll_import_from_gallery_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                startActivity(new Intent(Photos_Gallery_Actitvity.this, ImportAlbumsGalleryPhotoActivity.class));
                finish();
            }
        });
        this.ll_import_from_camera_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Common.isOpenCameraorGalleryFromApp = true;
                openCameraIntent();
            }
        });
        this.ll_import_intenet_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Photos_Gallery_Actitvity photos_Gallery_Actitvity = Photos_Gallery_Actitvity.this;
                Constants.CurrentWebBrowserActivity = photos_Gallery_Actitvity;
                startActivity(new Intent(photos_Gallery_Actitvity, SecureBrowserActivity.class));
                finish();
            }
        });
        this.ll_delete_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DeletePhotos();
            }
        });
        this.ll_unhide_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                UnhidePhotos();
            }
        });
        this.ll_move_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MovePhotos();
            }
        });
        this.ll_share_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!IsFileCheck()) {
                    Toast.makeText(Photos_Gallery_Actitvity.this, R.string.toast_unselectphotomsg_share, Toast.LENGTH_SHORT).show();
                } else {
                    SharePhotos();
                }
            }
        });
        if (Common.IsImporting) {
            try {
                showIsImportingProgress();
            } catch (Exception unused) {
            }
        } else if (Common.isUnHide) {
            try {
                showUnhideProgress();
            } catch (Exception unused2) {
            }
        } else if (Common.isDelete) {
            try {
                showDeleteProgress();
            } catch (Exception unused3) {
            }
        } else if (Common.isMove) {
            try {
                showMoveProgress();
            } catch (Exception unused4) {
            }
        } else {
            LoadPhotosFromDB(this._SortBy);
        }
        if (Common.PhotoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
            Common.PhotoThumbnailCurrentPosition = 0;
        }
    }


    public void SelectOrUnSelectAll() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.photos.size(); i++) {
                ((Photo) this.photos.get(i)).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_unselectallicon);
        } else {
            for (int i2 = 0; i2 < this.photos.size(); i2++) {
                ((Photo) this.photos.get(i2)).SetFileCheck(true);
            }
            this.IsSelectAll = true;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_selectallicon);
        }
        PhoneGalleryPhotoAdapter phoneGalleryPhotoAdapter = new PhoneGalleryPhotoAdapter(this, 1, this.photos, true, _ViewBy);
        this.galleryImagesAdapter = phoneGalleryPhotoAdapter;
        this.imagegrid.setAdapter(this.galleryImagesAdapter);
        this.galleryImagesAdapter.notifyDataSetChanged();
    }

    public void setUIforlistView() {
        this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 1));
        this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 0));
        this.ll_GridviewParams.setMargins(0, 0, 0, 0);
        this.ll_photo_video_grid.setLayoutParams(this.ll_GridviewParams);
        this.imagegrid.setNumColumns(1);
    }

    private void SetcheckFlase() {
        for (int i = 0; i < this.photos.size(); i++) {
            ((Photo) this.photos.get(i)).SetFileCheck(false);
        }
        PhoneGalleryPhotoAdapter phoneGalleryPhotoAdapter = new PhoneGalleryPhotoAdapter(this, 1, this.photos, false, _ViewBy);
        this.galleryImagesAdapter = phoneGalleryPhotoAdapter;
        this.imagegrid.setAdapter(this.galleryImagesAdapter);
        this.galleryImagesAdapter.notifyDataSetChanged();
        if (Common.PhotoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
            Common.PhotoThumbnailCurrentPosition = 0;
        }
    }

    public void btnBackonClick(View view) {
        Back();
    }

    public void Back() {
        if (this.isEditMode) {
            SetcheckFlase();
            this.isEditMode = false;
            this.IsSortingDropdown = false;
            this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
            this.ll_AddPhotos_Bottom_Baar.setVisibility(View.INVISIBLE);
            this.ll_EditPhotos.setVisibility(View.INVISIBLE);
            this.IsSelectAll = false;
            this.btnSelectAll.setVisibility(View.INVISIBLE);
            this._btnSortingDropdown.setVisibility(View.VISIBLE);
            invalidateOptionsMenu();
        } else if (this.isAddbarvisible) {
            this.isAddbarvisible = false;
            this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
            this.ll_AddPhotos_Bottom_Baar.setVisibility(View.INVISIBLE);
            this.ll_EditPhotos.setVisibility(View.INVISIBLE);
            this.IsSortingDropdown = false;
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            Common.FolderId = 0;
            Common.PhotoFolderName = "My Photos";
            startActivity(new Intent(this, PhotosAlbumActivty.class));
            finish();
        }
        DeleteTemporaryPhotos();
    }

    public void UnhidePhotos() {
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
            LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
            TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
            textView.setTypeface(Typeface.createFromAsset(getAssets(), "ebrima.ttf"));
            StringBuilder sb = new StringBuilder();
            sb.append("Are you sure you want to restore (");
            sb.append(this.selectCount);
            sb.append(") photo(s)?");
            textView.setText(sb.toString());
            ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    showUnhideProgress();
                    new Thread() {
                        public void run() {
                            try {
                                dialog.dismiss();
                                Common.isUnHide = true;
                                Unhide();
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 3;
                                handle.sendMessage(message2);
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
        for (int i = 0; i < this.photos.size(); i++) {
            if (((Photo) this.photos.get(i)).GetFileCheck()) {
                if (Utilities.NSUnHideFile(this, ((Photo) this.photos.get(i)).getFolderLockPhotoLocation(), ((Photo) this.photos.get(i)).getOriginalPhotoLocation())) {
                    DeleteFromDatabase(((Photo) this.photos.get(i)).getId());
                } else {
                    Toast.makeText(this, R.string.Unhide_error, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void DeletePhotos() {
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
        sb.append(") photo(s)?");
        textView.setText(sb.toString());
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                showDeleteProgress();
                new Thread() {
                    public void run() {
                        try {
                            Common.isDelete = true;
                            dialog.dismiss();
                            Delete();
                            Common.IsWorkInProgress = true;
                            Message message = new Message();
                            message.what = 3;
                            handle.sendMessage(message);
                            Common.IsWorkInProgress = false;
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 3;
                            handle.sendMessage(message2);
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


    public void Delete() {
        for (int i = 0; i < this.photos.size(); i++) {
            if (((Photo) this.photos.get(i)).GetFileCheck()) {
                new File(((Photo) this.photos.get(i)).getFolderLockPhotoLocation()).delete();
                DeleteFromDatabase(((Photo) this.photos.get(i)).getId());
            }
        }
    }

    public void DeleteFromDatabase(int i) {

        this.photoDAL = new PhotoDAL(this);
        try {
            this.photoDAL.OpenWrite();
            this.photoDAL.DeletePhotoById(i);

            if (photoDAL == null) {
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());

            if (photoDAL == null) {
                return;
            }
        } catch (Throwable th) {

            if (photoDAL != null) {
                photoDAL.close();
            }
            throw th;
        }
        photoDAL.close();
    }


    public void SelectedCount() {
        this.files.clear();
        this.selectCount = 0;
        for (int i = 0; i < this.photos.size(); i++) {
            if (((Photo) this.photos.get(i)).GetFileCheck()) {
                this.files.add(((Photo) this.photos.get(i)).getFolderLockPhotoLocation());
                this.selectCount++;
            }
        }
    }


    public boolean IsFileCheck() {
        for (int i = 0; i < this.photos.size(); i++) {
            if (((Photo) this.photos.get(i)).GetFileCheck()) {
                return true;
            }
        }
        return false;
    }

    public void MovePhotos() {
        this.photoDAL = new PhotoDAL(this);
        this.photoDAL.OpenWrite();
        this._albumNameArray = this.photoDAL.GetAlbumNames(Common.FolderId);
        if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectphotomsg_move, Toast.LENGTH_SHORT).show();
        } else if (this._albumNameArray.length > 0) {
            GetAlbumNameFromDB();
        } else {
            Toast.makeText(this, R.string.toast_OneAlbum, Toast.LENGTH_SHORT).show();
        }
    }


    public void Move(String str, String str2, String str3) {
        String str4;
        PhotoAlbum GetAlbum = GetAlbum(str3);
        for (int i = 0; i < this.photos.size(); i++) {
            if (((Photo) this.photos.get(i)).GetFileCheck()) {
                if (((Photo) this.photos.get(i)).getPhotoName().contains("#")) {
                    str4 = ((Photo) this.photos.get(i)).getPhotoName();
                } else {
                    str4 = Utilities.ChangeFileExtention(((Photo) this.photos.get(i)).getPhotoName());
                }
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append("/");
                sb.append(str4);
                String sb2 = sb.toString();
                try {
                    if (Utilities.MoveFileWithinDirectories(((Photo) this.photos.get(i)).getFolderLockPhotoLocation(), sb2)) {
                        UpdatePhotoLocationInDatabase((Photo) this.photos.get(i), sb2, GetAlbum.getId());
                        Common.FolderId = GetAlbum.getId();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void UpdatePhotoLocationInDatabase(Photo photo, String str, int i) {
        PhotoDAL photoDAL2;
        photo.setFolderLockPhotoLocation(str);
        photo.setAlbumId(i);
        try {
            PhotoDAL photoDAL3 = new PhotoDAL(this);
            photoDAL3.OpenWrite();
            photoDAL3.UpdatePhotoLocation(photo);
            photoDAL2 = this.photoDAL;
            if (photoDAL2 == null) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            photoDAL2 = this.photoDAL;
            if (photoDAL2 == null) {
                return;
            }
        } catch (Throwable th) {
            PhotoDAL photoDAL4 = this.photoDAL;
            if (photoDAL4 != null) {
                photoDAL4.close();
            }
            throw th;
        }
        photoDAL2.close();
    }


    public PhotoAlbum GetAlbum(String str) {
        PhotoAlbumDAL photoAlbumDAL2;
        this.photoAlbumDAL = new PhotoAlbumDAL(this);
        try {
            this.photoAlbumDAL.OpenRead();
            this.m_photoAlbum = this.photoAlbumDAL.GetAlbum(str);
            photoAlbumDAL2 = this.photoAlbumDAL;

            return m_photoAlbum;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            photoAlbumDAL2 = this.photoAlbumDAL;
        } catch (Throwable th) {
            PhotoAlbumDAL photoAlbumDAL3 = this.photoAlbumDAL;
            if (photoAlbumDAL3 != null) {
                photoAlbumDAL3.close();
            }
            throw th;
        }
        return null;
    }

    private void GetAlbumNameFromDB() {
        PhotoDAL photoDAL2;
        this.photoDAL = new PhotoDAL(this);
        try {
            this.photoDAL.OpenWrite();
            this._albumNameArrayForMove = this.photoDAL.GetMoveAlbumNames(Common.FolderId);
            MovePhotoDialog();
            photoDAL2 = this.photoDAL;
            if (photoDAL2 == null) {
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            photoDAL2 = this.photoDAL;
            if (photoDAL2 == null) {
                return;
            }
        } catch (Throwable th) {
            PhotoDAL photoDAL3 = this.photoDAL;
            if (photoDAL3 != null) {
                photoDAL3.close();
            }
            throw th;
        }
        photoDAL2.close();
    }


    public void MovePhotoDialog() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.move_customlistview);
        ListView listView = (ListView) dialog.findViewById(R.id.ListViewfolderslist);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        listView.setAdapter(new MoveAlbumAdapter(this, 17367043, this._albumNameArrayForMove, R.drawable.empty_folder_album_icon));
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long j) {
                if (_albumNameArrayForMove != null) {
                    SelectedCount();
                    showMoveProgress();
                    new Thread() {
                        public void run() {
                            try {
                                Common.isMove = true;
                                dialog.dismiss();
                                Photos_Gallery_Actitvity photos_Gallery_Actitvity = Photos_Gallery_Actitvity.this;
                                StringBuilder sb = new StringBuilder();
                                sb.append(StorageOptionsCommon.STORAGEPATH);
                                sb.append(StorageOptionsCommon.PHOTOS);
                                sb.append((String) _albumNameArrayForMove.get(i));
                                photos_Gallery_Actitvity.moveToFolderLocation = sb.toString();
                                Move(folderLocation, moveToFolderLocation, (String) _albumNameArrayForMove.get(i));
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 3;
                                handle.sendMessage(message2);
                            }
                        }
                    }.start();
                }
            }
        });
        dialog.show();
    }

    public void SharePhotos() {
        showCopyFilesProcessForShareProgress();
        new Thread() {
            public void run() {
                try {
                    SecurityLocksCommon.IsAppDeactive = false;
                    ArrayList arrayList = new ArrayList();
                    Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
                    intent.setType("image/*");
                    for (ResolveInfo resolveInfo : getPackageManager().queryIntentActivities(intent, 0)) {
                        String str = resolveInfo.activityInfo.packageName;
                        if (!str.equals(AppPackageName) && !str.equals("com.dropbox.android") && !str.equals("com.facebook.katana")) {
                            Intent intent2 = new Intent("android.intent.action.SEND_MULTIPLE");
                            intent2.setType("image/*");
                            intent2.setPackage(str);
                            arrayList.add(intent2);
                            StringBuilder sb = new StringBuilder();
                            sb.append(StorageOptionsCommon.STORAGEPATH);
                            sb.append(StorageOptionsCommon.PHOTOS);
                            String sb2 = sb.toString();
                            ArrayList arrayList2 = new ArrayList();
                            ArrayList arrayList3 = new ArrayList();
                            String str2 = sb2;
                            for (int i = 0; i < photos.size(); i++) {
                                if (((Photo) photos.get(i)).GetFileCheck()) {
                                    try {
                                        str2 = Utilities.CopyTemporaryFile(Photos_Gallery_Actitvity.this, ((Photo) photos.get(i)).getFolderLockPhotoLocation(), str2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    arrayList2.add(str2);
                                    arrayList3.add(FileProvider.getUriForFile(Photos_Gallery_Actitvity.this, AppPackageName, new File(str2)));
                                }
                            }
                            intent2.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList3);
                        }
                    }
                    Intent createChooser = Intent.createChooser((Intent) arrayList.remove(0), "Share Via");
                    createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) arrayList.toArray(new Parcelable[0]));
                    startActivity(createChooser);
                    Message message = new Message();
                    message.what = 4;
                    handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 4;
                    handle.sendMessage(message2);
                }
            }
        }.start();
    }

    public void DeleteTemporaryPhotos() {
        File[] listFiles;
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.PHOTOS);
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

    @SuppressLint({"SimpleDateFormat"})
    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        String str;
        SecurityLocksCommon.IsAppDeactive = true;
        if (i == RESULT_LOAD_CAMERA && i2 == -1 && this.cacheDir != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(StorageOptionsCommon.STORAGEPATH);
            sb.append(StorageOptionsCommon.PHOTOS);
            sb.append(this.albumName);
            String str2 = null;
            try {

                str2 = Utilities.NSHideFile(this, this.cacheDir, new File(sb.toString()));
                Utilities.NSEncryption(new File(str2));
                str = str2;

            } catch (IOException e) {

                Log.e("TAG", "onActivityResult: " + e.toString());
                e.printStackTrace();
                str = str2;

            }
            if (!str.equals("")) {
                new AlbumsGalleryPhotosMethods().AddPhotoToDatabase(this, Common.FolderId, this.cacheDir.getName(), str, this.cacheDir.getAbsolutePath());
                Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show();
                start();
                LoadPhotosFromDB(this._SortBy);
            }
        }
    }
    private void start() {
        startActivity(new Intent(Photos_Gallery_Actitvity.this, Photos_Gallery_Actitvity.class));
        finish();
    }


    public void LoadPhotosFromDB(int i) {
        this.photos = new ArrayList();
        PhotoDAL photoDAL2 = new PhotoDAL(this);
        photoDAL2.OpenRead();
        this.photoCount = photoDAL2.GetPhotoCountByAlbumId(Common.FolderId);
        this.photos = photoDAL2.GetPhotoByAlbumId(Common.FolderId, i);

        Log.e("loadfromdb", "" + photos);
        photoDAL2.close();
        PhoneGalleryPhotoAdapter phoneGalleryPhotoAdapter = new PhoneGalleryPhotoAdapter(this, 1, this.photos, false, _ViewBy);
        this.galleryImagesAdapter = phoneGalleryPhotoAdapter;
        this.imagegrid.setAdapter(this.galleryImagesAdapter);
        this.galleryImagesAdapter.notifyDataSetChanged();
        if (this.photos.size() < 1) {
            this.ll_photo_video_grid.setVisibility(View.INVISIBLE);
            this.ll_photo_video_empty.setVisibility(View.VISIBLE);
            this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_photo_empty_icon);
            this.lbl_photo_video_empty.setText(R.string.lbl_No_Photos);
            return;
        }
        this.ll_photo_video_grid.setVisibility(View.VISIBLE);
        this.ll_photo_video_empty.setVisibility(View.INVISIBLE);
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
        expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {
            public void onGroupExpand(int i) {
                Log.v("", "yes");
            }
        });
        expandableListView.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
                if (i != 0) {
                    if (i == 1) {
                        switch (i2) {
                            case 0:
                                _SortBy = SortBy.Name.ordinal();
                                Photos_Gallery_Actitvity photos_Gallery_Actitvity = Photos_Gallery_Actitvity.this;
                                photos_Gallery_Actitvity.LoadPhotosFromDB(photos_Gallery_Actitvity._SortBy);
                                AddSortInDB();
                                popupWindow.dismiss();
                                IsSortingDropdown = false;
                                break;
                            case 1:
                                _SortBy = SortBy.Time.ordinal();
                                Photos_Gallery_Actitvity photos_Gallery_Actitvity2 = Photos_Gallery_Actitvity.this;
                                photos_Gallery_Actitvity2.LoadPhotosFromDB(photos_Gallery_Actitvity2._SortBy);
                                AddSortInDB();
                                popupWindow.dismiss();
                                IsSortingDropdown = false;
                                break;
                            case 2:
                                _SortBy = SortBy.Size.ordinal();
                                Photos_Gallery_Actitvity photos_Gallery_Actitvity3 = Photos_Gallery_Actitvity.this;
                                photos_Gallery_Actitvity3.LoadPhotosFromDB(photos_Gallery_Actitvity3._SortBy);
                                AddSortInDB();
                                popupWindow.dismiss();
                                IsSortingDropdown = false;
                                break;
                        }
                    }
                } else {
                    switch (i2) {
                        case 0:
                            Photos_Gallery_Actitvity._ViewBy = ViewBy.List.ordinal();
                            ViewBy();
                            popupWindow.dismiss();
                            IsSortingDropdown = false;
                            break;
                        case 1:
                            Photos_Gallery_Actitvity._ViewBy = ViewBy.Tiles.ordinal();
                            ViewBy();
                            popupWindow.dismiss();
                            IsSortingDropdown = false;
                            break;
                        case 2:
                            Photos_Gallery_Actitvity._ViewBy = ViewBy.LargeTiles.ordinal();
                            ViewBy();
                            popupWindow.dismiss();
                            IsSortingDropdown = false;
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


    public void AddSortInDB() {
        PhotoAlbumDAL photoAlbumDAL2 = new PhotoAlbumDAL(this);
        photoAlbumDAL2.OpenWrite();
        photoAlbumDAL2.AddSortByInPhotoAlbum(this._SortBy);
        photoAlbumDAL2.close();
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
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(3);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(7);
            } else {
                setUIforlistView();
            }
        } else if (configuration.orientation != 1) {
        } else {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(3);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(3);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(2);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(4);
            } else {
                setUIforlistView();
            }
        }
    }

    public void ViewBy() {
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(3);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(3);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(2);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(4);
            } else {
                setUIforlistView();
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(3);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(7);
            } else {
                setUIforlistView();
            }
        }
        PhoneGalleryPhotoAdapter phoneGalleryPhotoAdapter = new PhoneGalleryPhotoAdapter(this, 1, this.photos, false, _ViewBy);
        this.galleryImagesAdapter = phoneGalleryPhotoAdapter;
        this.imagegrid.setAdapter(this.galleryImagesAdapter);
        this.galleryImagesAdapter.notifyDataSetChanged();
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
            SecurityLocksSharedPreferences.GetObject(this).SetIsCameraOpenFromInApp(Boolean.valueOf(false));
            Common.isOpenCameraorGalleryFromApp = false;
            finish();
            System.exit(0);
        }
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        SetcheckFlase();
        this.IsSortingDropdown = false;
        this.isEditMode = false;
        this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_AddPhotos_Bottom_Baar.setVisibility(View.INVISIBLE);
        this.ll_EditPhotos.setVisibility(View.INVISIBLE);
        this.IsSelectAll = false;
        this.btnSelectAll.setVisibility(View.INVISIBLE);
        this._btnSortingDropdown.setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
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
                this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
                this.ll_AddPhotos_Bottom_Baar.setVisibility(View.INVISIBLE);
                this.ll_EditPhotos.setVisibility(View.INVISIBLE);
                this.IsSelectAll = false;
                this.btnSelectAll.setVisibility(View.INVISIBLE);
                this._btnSortingDropdown.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
                return true;
            } else if (this.isAddbarvisible) {
                this.isAddbarvisible = false;
                this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
                this.ll_AddPhotos_Bottom_Baar.setVisibility(View.INVISIBLE);
                this.ll_EditPhotos.setVisibility(View.INVISIBLE);
                this.IsSortingDropdown = false;
                return true;
            } else {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.FolderId = 0;
                Common.PhotoFolderName = "My Photos";
                startActivity(new Intent(this, PhotosAlbumActivty.class));
                finish();
                DeleteTemporaryPhotos();
            }
        }
        return super.onKeyDown(i, keyEvent);
    }


    public void openCameraIntent() {
        SecurityLocksSharedPreferences.GetObject(this).SetIsCameraOpenFromInApp(Boolean.valueOf(true));
        Common.isOpenCameraorGalleryFromApp = true;
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(getApplication().getPackageManager()) != null) {
            try {
                this.cacheDir = createImageFile();
                StringBuilder sb = new StringBuilder();
                sb.append(this.cacheDir);
                sb.append("");
                Log.e("photoFile", sb.toString());
                intent.putExtra("output", FileProvider.getUriForFile(getApplicationContext(), AppPackageName, this.cacheDir));
                SecurityLocksCommon.IsAppDeactive = false;
                startActivityForResult(intent, RESULT_LOAD_CAMERA);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("IMG_");
        sb.append(format);
        sb.append("_");
        File createTempFile = File.createTempFile(sb.toString(), ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        createTempFile.getAbsolutePath();
        return createTempFile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more, menu);
        menu.findItem(R.id.action_more);
        return true;
    }

    @Override
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
                for (int i = 0; i < this.photos.size(); i++) {
                    ((Photo) this.photos.get(i)).SetFileCheck(false);
                }
                this.IsSelectAll = false;
                menuItem.setIcon(R.drawable.ic_unselectallicon);
                invalidateOptionsMenu();
            } else {
                for (int i2 = 0; i2 < this.photos.size(); i2++) {
                    ((Photo) this.photos.get(i2)).SetFileCheck(true);
                }
                this.IsSelectAll = true;
                menuItem.setIcon(R.drawable.ic_selectallicon);
            }
            PhoneGalleryPhotoAdapter phoneGalleryPhotoAdapter = new PhoneGalleryPhotoAdapter(this, 1, this.photos, true, _ViewBy);
            this.galleryImagesAdapter = phoneGalleryPhotoAdapter;
            this.imagegrid.setAdapter(this.galleryImagesAdapter);
            this.galleryImagesAdapter.notifyDataSetChanged();
            return true;
        }
    }

    @Override
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
