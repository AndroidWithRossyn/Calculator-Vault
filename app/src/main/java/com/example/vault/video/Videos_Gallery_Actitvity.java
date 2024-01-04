package com.example.vault.video;

import static com.example.vault.utilities.Common.AppPackageName;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore.Video.Media;
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
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vault.video.adapter.AppGalleryVideoAdapter;
import com.example.vault.video.model.Video;
import com.example.vault.video.model.VideoAlbum;
import com.example.vault.video.util.VideoAlbumDAL;
import com.example.vault.video.util.VideoDAL;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.banrossyn.imageloader.utils.LibCommonAppClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.example.vault.R;
import com.example.vault.adapter.ExpandableListAdapter1;
import com.example.vault.BaseActivity;
import com.example.vault.common.Constants;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.photo.adapter.MoveAlbumAdapter;
import com.example.vault.privatebrowser.SecureBrowserActivity;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.securitylocks.SecurityLocksSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;

public class Videos_Gallery_Actitvity extends BaseActivity {
    private static final int ACTION_TAKE_VIDEO = 2;
    private static final int RESULT_LOAD_IMAGE = 1;
    public static int _ViewBy;
    public static ProgressDialog myProgressDialog;
    private boolean IsSelectAll = false;
    boolean IsSortingDropdown = false;
    int _SortBy = 1;
    private String[] _albumNameArray;

    public List<String> _albumNameArrayForMove = null;
    ImageButton _btnSortingDropdown;
    int albumId;
    String albumName;
    private FloatingActionButton btnAdd;

    public ImageButton btnSelectAll;
    private FloatingActionButton fabImpBrowser;
    private FloatingActionButton fabImpCam;
    private FloatingActionButton fabImpGallery;
    private FloatingActionButton fabImpPcMac;
    FloatingActionsMenu fabMenu;
    private ArrayList<String> files = new ArrayList<>();
    FrameLayout fl_bottom_baar;
    protected String folderLocation;

    public AppGalleryVideoAdapter galleryVideosAdapter;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 2) {
                Videos_Gallery_Actitvity.this.hideProgress();
                if (Common.isUnHide) {
                    Common.isUnHide = false;
                    Toast.makeText(Videos_Gallery_Actitvity.this, R.string.Unhide_error, Toast.LENGTH_SHORT).show();
                } else if (Common.isMove) {
                    Common.isMove = false;
                    Toast.makeText(Videos_Gallery_Actitvity.this, R.string.Move_error, Toast.LENGTH_SHORT).show();
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(Videos_Gallery_Actitvity.this, R.string.Delete_error, Toast.LENGTH_SHORT).show();
                }
            } else if (message.what == 4) {
                Toast.makeText(Videos_Gallery_Actitvity.this, R.string.toast_share, Toast.LENGTH_LONG).show();
            } else if (message.what == 3) {
                if (Common.isUnHide) {
                    if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        Videos_Gallery_Actitvity videos_Gallery_Actitvity = Videos_Gallery_Actitvity.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append(Constants.FILE);
                        sb.append(Environment.getExternalStorageDirectory());
                        videos_Gallery_Actitvity.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb.toString())));
                    } else {
                        Videos_Gallery_Actitvity.this.RefershGalleryforKitkat();
                    }
                    Common.isUnHide = false;
                    Toast.makeText(Videos_Gallery_Actitvity.this, R.string.toast_unhide, Toast.LENGTH_LONG).show();
                    Videos_Gallery_Actitvity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent = new Intent(Videos_Gallery_Actitvity.this, Videos_Gallery_Actitvity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Videos_Gallery_Actitvity.this.startActivity(intent);
                        Videos_Gallery_Actitvity.this.finish();
                    }
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(Videos_Gallery_Actitvity.this, R.string.toast_delete, Toast.LENGTH_SHORT).show();
                    Videos_Gallery_Actitvity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent2 = new Intent(Videos_Gallery_Actitvity.this, Videos_Gallery_Actitvity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Videos_Gallery_Actitvity.this.startActivity(intent2);
                        Videos_Gallery_Actitvity.this.finish();
                    }
                } else if (Common.isMove) {
                    Common.isMove = false;
                    Toast.makeText(Videos_Gallery_Actitvity.this, R.string.toast_move, Toast.LENGTH_SHORT).show();
                    Videos_Gallery_Actitvity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent3 = new Intent(Videos_Gallery_Actitvity.this, Videos_Gallery_Actitvity.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Videos_Gallery_Actitvity.this.startActivity(intent3);
                        Videos_Gallery_Actitvity.this.finish();
                    }
                }
            }
            super.handleMessage(message);
        }
    };
    GridView imagegrid;
    boolean isEditMode = false;
    TextView lbl_album_name_topbaar;
    TextView lbl_photo_video_empty;
    LinearLayout ll_AddPhotos_Bottom_Baar;
    LinearLayout ll_EditPhotos;
    LayoutParams ll_GridviewParams;
    LinearLayout.LayoutParams ll_Hide_Params;
    LinearLayout.LayoutParams ll_Show_Params;
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
    private Uri mVideoUri;
    private VideoAlbum m_videoAlbum;

    public String moveToFolderLocation;
    ImageView photo_video_empty_icon;
    int selectCount;
    private SensorManager sensorManager;
    Toolbar toolbar;
    private VideoAlbumDAL videoAlbumDAL;
    private int videoCount = 0;
    private VideoDAL videoDAL;

    public List<Video> videos;

    public enum SortBy {
        Time,
        Name,
        Size
    }

    public enum ViewBy {
        LargeTiles,
        Tiles,
        List
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
        SecurityLocksCommon.IsAppDeactive = true;
        LibCommonAppClass.IsPhoneGalleryLoad = true;
        getWindow().addFlags(128);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        TextView title9 = findViewById(R.id.title9);
//        ImageView bgmainImg=findViewById(R.id.bgmainImg);
//        bgmainImg.setImageResource(R.drawable.bg_ic_video);

        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.Back();
            }
        });
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_Show_Params = new LinearLayout.LayoutParams(-1, -2);
        this.ll_Hide_Params = new LinearLayout.LayoutParams(-1, 0);
        this.ll_GridviewParams = new LayoutParams(-1, -1);
        this.fl_bottom_baar = (FrameLayout) findViewById(R.id.fl_bottom_baar);
        this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_AddPhotos_Bottom_Baar = (LinearLayout) findViewById(R.id.ll_AddPhotos_Bottom_Baar);
        this.ll_EditPhotos = (LinearLayout) findViewById(R.id.ll_EditPhotos);
        this.imagegrid = (GridView) findViewById(R.id.customGalleryGrid);
        this.btnSelectAll = (ImageButton) findViewById(R.id.btnSelectAll);
        this.fabMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu);
        this.fabImpCam = (FloatingActionButton) findViewById(R.id.btn_impCam);
        this.fabImpCam.setVisibility(View.GONE);
        this.fabImpGallery = (FloatingActionButton) findViewById(R.id.btn_impGallery);
        this.fabImpBrowser = (FloatingActionButton) findViewById(R.id.btn_impBrowser);
        this.fabImpPcMac = (FloatingActionButton) findViewById(R.id.btn_impPcMac);
        this.fabImpGallery.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                Videos_Gallery_Actitvity.this.startActivity(new Intent(Videos_Gallery_Actitvity.this, ImportAlbumsGalleryVideoActivity.class));
                Videos_Gallery_Actitvity.this.finish();
            }
        });
//        this.fabImpCam.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) {
//                Videos_Gallery_Actitvity.this.dispatchTakeVideoIntent();
//            }
//        });
        this.fabImpBrowser.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Videos_Gallery_Actitvity videos_Gallery_Actitvity = Videos_Gallery_Actitvity.this;
                Common.CurrentWebBrowserActivity = videos_Gallery_Actitvity;
                Videos_Gallery_Actitvity.this.startActivity(new Intent(videos_Gallery_Actitvity, SecureBrowserActivity.class));
                Videos_Gallery_Actitvity.this.finish();
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
        VideoAlbumDAL videoAlbumDAL2 = new VideoAlbumDAL(this);
        videoAlbumDAL2.OpenRead();
        VideoAlbum GetAlbumById = videoAlbumDAL2.GetAlbumById(Integer.toString(Common.FolderId));
        this._SortBy = videoAlbumDAL2.GetSortByAlbumId(Common.FolderId);
        videoAlbumDAL2.close();
        this.albumName = GetAlbumById.getAlbumName();
        Common.VideoFolderName = this.albumName;
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
        this.ll_background.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (Videos_Gallery_Actitvity.this.IsSortingDropdown) {
                    Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
                }
                if (Videos_Gallery_Actitvity.this.IsSortingDropdown) {
                    Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
                }
                return false;
            }
        });
        this.imagegrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (!Videos_Gallery_Actitvity.this.isEditMode) {
                    Common.VideoThumbnailCurrentPosition = Videos_Gallery_Actitvity.this.imagegrid.getFirstVisiblePosition();
                    File file = new File(((Video) Videos_Gallery_Actitvity.this.videos.get(i)).getFolderLockVideoLocation());
                    String str = "";
                    SecurityLocksCommon.IsAppDeactive = false;
                    if (file.exists()) {
                        try {
                            str = Utilities.NSVideoDecryptionDuringPlay(new File(((Video) Videos_Gallery_Actitvity.this.videos.get(i)).getFolderLockVideoLocation()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append(file.getParent());
                        sb.append(File.separator);
                        sb.append(Utilities.ChangeFileExtentionToOrignal(Utilities.FileName(file.getAbsolutePath())));
                        str = sb.toString();
                    }
                    String substring = str.substring(str.lastIndexOf(".") + 1);
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        File file2 = new File(str);
                        intent.setDataAndType(FileProvider.getUriForFile(Videos_Gallery_Actitvity.this, AppPackageName, file2), MimeTypeMap.getSingleton().getMimeTypeFromExtension(substring));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Videos_Gallery_Actitvity.this.startActivity(intent);
                    } catch (Exception e2) {
                        e2.getMessage();
                    }
                }
            }
        });
        this.imagegrid.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                Common.VideoThumbnailCurrentPosition = Videos_Gallery_Actitvity.this.imagegrid.getFirstVisiblePosition();
                Videos_Gallery_Actitvity videos_Gallery_Actitvity = Videos_Gallery_Actitvity.this;
                videos_Gallery_Actitvity.isEditMode = true;
                videos_Gallery_Actitvity.fl_bottom_baar.setLayoutParams(Videos_Gallery_Actitvity.this.ll_Show_Params);
                Videos_Gallery_Actitvity.this.ll_AddPhotos_Bottom_Baar.setVisibility(View.INVISIBLE);
                Videos_Gallery_Actitvity.this.ll_EditPhotos.setVisibility(View.VISIBLE);
                Videos_Gallery_Actitvity.this._btnSortingDropdown.setVisibility(View.INVISIBLE);
                Videos_Gallery_Actitvity.this.btnSelectAll.setVisibility(View.VISIBLE);
                Videos_Gallery_Actitvity.this.invalidateOptionsMenu();
                ((Video) Videos_Gallery_Actitvity.this.videos.get(i)).SetFileCheck(true);
                Videos_Gallery_Actitvity videos_Gallery_Actitvity2 = Videos_Gallery_Actitvity.this;
                AppGalleryVideoAdapter appGalleryVideoAdapter = new AppGalleryVideoAdapter(videos_Gallery_Actitvity2, 1, videos_Gallery_Actitvity2.videos, true, Videos_Gallery_Actitvity._ViewBy);
                videos_Gallery_Actitvity2.galleryVideosAdapter = appGalleryVideoAdapter;
                Videos_Gallery_Actitvity.this.imagegrid.setAdapter(Videos_Gallery_Actitvity.this.galleryVideosAdapter);
                Videos_Gallery_Actitvity.this.galleryVideosAdapter.notifyDataSetChanged();
                if (Common.VideoThumbnailCurrentPosition != 0) {
                    Videos_Gallery_Actitvity.this.imagegrid.setSelection(Common.VideoThumbnailCurrentPosition);
                }
                return true;
            }
        });
        this.btnSelectAll.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.SelectOrUnSelectAll();
            }
        });
        this.ll_import_from_gallery_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                Videos_Gallery_Actitvity.this.startActivity(new Intent(Videos_Gallery_Actitvity.this, ImportAlbumsGalleryVideoActivity.class));
                Videos_Gallery_Actitvity.this.finish();
            }
        });
        this.ll_import_from_camera_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.dispatchTakeVideoIntent();
            }
        });
        this.ll_import_intenet_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Videos_Gallery_Actitvity videos_Gallery_Actitvity = Videos_Gallery_Actitvity.this;
                Constants.CurrentWebBrowserActivity = videos_Gallery_Actitvity;
                Videos_Gallery_Actitvity.this.startActivity(new Intent(videos_Gallery_Actitvity, SecureBrowserActivity.class));
                Videos_Gallery_Actitvity.this.finish();
            }
        });
        this.ll_delete_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.DeleteVideos();
            }
        });
        this.ll_unhide_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.UnhideVideos();
            }
        });
        this.ll_move_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.MoveVideos();
            }
        });
        this.ll_share_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!Videos_Gallery_Actitvity.this.IsFileCheck()) {
                    Toast.makeText(Videos_Gallery_Actitvity.this, R.string.toast_unselectvideomsg_share, Toast.LENGTH_SHORT).show();
                } else {
                    Videos_Gallery_Actitvity.this.ShareVideos();
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
            LoadVideosFromDB(this._SortBy);
        }
        if (Common.VideoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.VideoThumbnailCurrentPosition);
            Common.VideoThumbnailCurrentPosition = 0;
        }
    }

    public void setUIforlistView() {
        this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 1));
        this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 0));
        this.ll_GridviewParams.setMargins(0, 0, 0, 0);
        this.ll_photo_video_grid.setLayoutParams(this.ll_GridviewParams);
        this.imagegrid.setNumColumns(1);
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
            invalidateOptionsMenu();
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            Common.FolderId = 0;
            Common.VideoFolderName = StorageOptionsCommon.VIDEOS_DEFAULT_ALBUM;
            startActivity(new Intent(this, VideosAlbumActivty.class));
            finish();
        }
        DeleteTemporaryVideos();
    }

    public void UnhideVideos() {
        if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectvideomsg_unhide, Toast.LENGTH_SHORT).show();
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
            sb.append(") video(s)?");
            textView.setText(sb.toString());
            ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Videos_Gallery_Actitvity.this.showUnhideProgress();
                    new Thread() {
                        public void run() {
                            try {
                                dialog.dismiss();
                                Common.isUnHide = true;
                                Videos_Gallery_Actitvity.this.Unhide();
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                Videos_Gallery_Actitvity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 2;
                                Videos_Gallery_Actitvity.this.handle.sendMessage(message2);
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
        for (int i = 0; i < this.videos.size(); i++) {
            if (((Video) this.videos.get(i)).GetFileCheck()) {
                if (Utilities.NSUnHideFile(this, ((Video) this.videos.get(i)).getFolderLockVideoLocation(), ((Video) this.videos.get(i)).getOriginalVideoLocation())) {
                    new File(((Video) this.videos.get(i)).getthumbnail_video_location()).delete();
                    DeleteFromDatabase(((Video) this.videos.get(i)).getId());
                } else {
                    Toast.makeText(this, R.string.Unhide_error, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void DeleteVideos() {
        if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectvideomsg_delete, Toast.LENGTH_SHORT).show();
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
        sb.append(") video(s)?");
        textView.setText(sb.toString());
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.showDeleteProgress();
                new Thread() {
                    public void run() {
                        try {
                            Common.isDelete = true;
                            dialog.dismiss();
                            Videos_Gallery_Actitvity.this.Delete();
                            Common.IsWorkInProgress = true;
                            Message message = new Message();
                            message.what = 3;
                            Videos_Gallery_Actitvity.this.handle.sendMessage(message);
                            Common.IsWorkInProgress = false;
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 2;
                            Videos_Gallery_Actitvity.this.handle.sendMessage(message2);
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
        for (int i = 0; i < this.videos.size(); i++) {
            if (((Video) this.videos.get(i)).GetFileCheck()) {
                new File(((Video) this.videos.get(i)).getFolderLockVideoLocation()).delete();
                new File(((Video) this.videos.get(i)).getthumbnail_video_location()).delete();
                DeleteFromDatabase(((Video) this.videos.get(i)).getId());
            }
        }
    }

    public void DeleteFromDatabase(int i) {
        VideoDAL videoDAL2;
        this.videoDAL = new VideoDAL(this);
        try {
            this.videoDAL.OpenWrite();
            this.videoDAL.DeleteVideoById(i);
            videoDAL2 = this.videoDAL;
            if (videoDAL2 == null) {
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            videoDAL2 = this.videoDAL;
            if (videoDAL2 == null) {
                return;
            }
        } catch (Throwable th) {
            VideoDAL videoDAL3 = this.videoDAL;
            if (videoDAL3 != null) {
                videoDAL3.close();
            }
            throw th;
        }
        videoDAL2.close();
    }


    public void SelectedCount() {
        this.files.clear();
        this.selectCount = 0;
        for (int i = 0; i < this.videos.size(); i++) {
            if (((Video) this.videos.get(i)).GetFileCheck()) {
                this.files.add(((Video) this.videos.get(i)).getFolderLockVideoLocation());
                this.selectCount++;
            }
        }
    }


    public boolean IsFileCheck() {
        for (int i = 0; i < this.videos.size(); i++) {
            if (((Video) this.videos.get(i)).GetFileCheck()) {
                return true;
            }
        }
        return false;
    }

    public void MoveVideos() {
        this.videoDAL = new VideoDAL(this);
        this.videoDAL.OpenWrite();
        this._albumNameArray = this.videoDAL.GetAlbumNames(Common.FolderId);
        if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectvideomsg_move, Toast.LENGTH_SHORT).show();
        } else if (this._albumNameArray.length > 0) {
            GetAlbumNameFromDB();
        } else {
            Toast.makeText(this, R.string.toast_OneAlbum, Toast.LENGTH_SHORT).show();
        }
    }


    public void Move(String str, String str2, String str3) throws IOException {
        String str4;
        VideoAlbum GetAlbum = GetAlbum(str3);
        for (int i = 0; i < this.videos.size(); i++) {
            if (((Video) this.videos.get(i)).GetFileCheck()) {
                if (((Video) this.videos.get(i)).getVideoName().contains("#")) {
                    str4 = ((Video) this.videos.get(i)).getVideoName();
                } else {
                    str4 = Utilities.ChangeFileExtention(((Video) this.videos.get(i)).getVideoName());
                }
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append("/");
                sb.append(str4);
                String sb2 = sb.toString();
                if (Utilities.MoveFileWithinDirectories(((Video) this.videos.get(i)).getFolderLockVideoLocation(), sb2)) {
                    String str5 = ((Video) this.videos.get(i)).getthumbnail_video_location();
                    String FileName = Utilities.FileName(((Video) this.videos.get(i)).getthumbnail_video_location());
                    if (!FileName.contains("#")) {
                        FileName = Utilities.ChangeFileExtention(FileName);
                    }
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(str2);
                    sb3.append("/VideoThumnails/");
                    sb3.append(FileName);
                    String sb4 = sb3.toString();
                    if (Utilities.MoveFileWithinDirectories(str5, sb4)) {
                        UpdateVideoLocationInDatabase((Video) this.videos.get(i), sb2, GetAlbum.getId(), sb4);
                        Common.FolderId = GetAlbum.getId();
                    }
                }
            }
        }
    }

    public void UpdateVideoLocationInDatabase(Video video, String str, int i, String str2) {
        VideoDAL videoDAL2;
        video.setFolderLockVideoLocation(str);
        video.setthumbnail_video_location(str2);
        video.setAlbumId(i);
        try {
            this.videoDAL.OpenWrite();
            this.videoDAL.UpdateVideoLocationById(video);
            videoDAL2 = this.videoDAL;
            if (videoDAL2 == null) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            videoDAL2 = this.videoDAL;
            if (videoDAL2 == null) {
                return;
            }
        } catch (Throwable th) {
            VideoDAL videoDAL3 = this.videoDAL;
            if (videoDAL3 != null) {
                videoDAL3.close();
            }
            throw th;
        }
        videoDAL2.close();
    }


    public VideoAlbum GetAlbum(String str) {
        VideoAlbumDAL videoAlbumDAL2;
        this.videoAlbumDAL = new VideoAlbumDAL(this);
        try {
            this.videoAlbumDAL.OpenRead();
            this.m_videoAlbum = this.videoAlbumDAL.GetAlbum(str);
            videoAlbumDAL2 = this.videoAlbumDAL;

            return m_videoAlbum;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            videoAlbumDAL2 = this.videoAlbumDAL;
        } catch (Throwable th) {
            VideoAlbumDAL videoAlbumDAL3 = this.videoAlbumDAL;
            if (videoAlbumDAL3 != null) {
                videoAlbumDAL3.close();
            }
            throw th;
        }
        return null;
    }

    private void GetAlbumNameFromDB() {
        VideoDAL videoDAL2;
        this.videoDAL = new VideoDAL(this);
        try {
            this.videoDAL.OpenWrite();
            this._albumNameArrayForMove = this.videoDAL.GetMoveAlbumNames(Common.FolderId);
            MoveVideoDialog();
            videoDAL2 = this.videoDAL;
            if (videoDAL2 == null) {
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            videoDAL2 = this.videoDAL;
            if (videoDAL2 == null) {
                return;
            }
        } catch (Throwable th) {
            VideoDAL videoDAL3 = this.videoDAL;
            if (videoDAL3 != null) {
                videoDAL3.close();
            }
            throw th;
        }
        videoDAL2.close();
    }


    public void MoveVideoDialog() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.move_customlistview);
        ListView listView = (ListView) dialog.findViewById(R.id.ListViewfolderslist);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        listView.setAdapter(new MoveAlbumAdapter(this, 17367043, this._albumNameArrayForMove, R.drawable.empty_folder_video_icon));
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long j) {
                if (Videos_Gallery_Actitvity.this._albumNameArrayForMove != null) {
                    Videos_Gallery_Actitvity.this.SelectedCount();
                    Videos_Gallery_Actitvity.this.showMoveProgress();
                    new Thread() {
                        public void run() {
                            try {
                                Common.isMove = true;
                                dialog.dismiss();
                                Videos_Gallery_Actitvity videos_Gallery_Actitvity = Videos_Gallery_Actitvity.this;
                                StringBuilder sb = new StringBuilder();
                                sb.append(StorageOptionsCommon.STORAGEPATH);
                                sb.append(StorageOptionsCommon.VIDEOS);
                                sb.append((String) Videos_Gallery_Actitvity.this._albumNameArrayForMove.get(i));
                                videos_Gallery_Actitvity.moveToFolderLocation = sb.toString();
                                Videos_Gallery_Actitvity.this.Move(Videos_Gallery_Actitvity.this.folderLocation, Videos_Gallery_Actitvity.this.moveToFolderLocation, (String) Videos_Gallery_Actitvity.this._albumNameArrayForMove.get(i));
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                Videos_Gallery_Actitvity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 2;
                                Videos_Gallery_Actitvity.this.handle.sendMessage(message2);
                            }
                        }
                    }.start();
                }
            }
        });
        dialog.show();
    }

    public void ShareVideos() {
        showCopyFilesProcessForShareProgress();
        new Thread() {
            public void run() {
                try {
                    SecurityLocksCommon.IsAppDeactive = false;
                    ArrayList arrayList = new ArrayList();
                    Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
                    intent.setType("image/*");
                    for (ResolveInfo resolveInfo : Videos_Gallery_Actitvity.this.getPackageManager().queryIntentActivities(intent, 0)) {
                        String str = resolveInfo.activityInfo.packageName;
                        if (!str.equals(AppPackageName) && !str.equals("com.dropbox.android") && !str.equals("com.facebook.katana")) {
                            Intent intent2 = new Intent("android.intent.action.SEND_MULTIPLE");
                            intent2.setType("image/*");
                            intent2.setPackage(str);
                            arrayList.add(intent2);
                            StringBuilder sb = new StringBuilder();
                            sb.append(StorageOptionsCommon.STORAGEPATH);
                            sb.append(StorageOptionsCommon.VIDEOS);
                            String sb2 = sb.toString();
                            ArrayList arrayList2 = new ArrayList();
                            ArrayList arrayList3 = new ArrayList();
                            String str2 = sb2;
                            for (int i = 0; i < Videos_Gallery_Actitvity.this.videos.size(); i++) {
                                if (((Video) Videos_Gallery_Actitvity.this.videos.get(i)).GetFileCheck()) {
                                    try {
                                        str2 = Utilities.CopyTemporaryFile(Videos_Gallery_Actitvity.this, ((Video) Videos_Gallery_Actitvity.this.videos.get(i)).getFolderLockVideoLocation(), str2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    arrayList2.add(str2);
                                    arrayList3.add(FileProvider.getUriForFile(Videos_Gallery_Actitvity.this, AppPackageName, new File(str2)));
                                }
                            }
                            intent2.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList3);
                        }
                    }
                    Intent createChooser = Intent.createChooser((Intent) arrayList.remove(0), "Share Via");
                    createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) arrayList.toArray(new Parcelable[0]));
                    Videos_Gallery_Actitvity.this.startActivity(createChooser);
                    Message message = new Message();
                    message.what = 4;
                    Videos_Gallery_Actitvity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 4;
                    Videos_Gallery_Actitvity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }

    public void DeleteTemporaryVideos() {
        File[] listFiles;
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.VIDEOS);
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


    public void dispatchTakeVideoIntent() {
        SecurityLocksSharedPreferences.GetObject(this).SetIsCameraOpenFromInApp(Boolean.valueOf(true));
        Common.isOpenCameraorGalleryFromApp = true;
        SecurityLocksCommon.IsAppDeactive = false;
        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
        if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
            startActivityForResult(intent, 2);
            return;
        }
        String format = new SimpleDateFormat("yyyymmddhhmmss").format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("Video_");
        sb.append(format);
        sb.append(".mp4");
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb3.append("/");
        sb3.append(sb2);
        String sb4 = sb3.toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", "VideoTitle");
        contentValues.put("mime_type", "video/mp4");
        contentValues.put("_data", sb4);
        intent.putExtra("output", getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, contentValues));
        startActivityForResult(intent, 2);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        FileOutputStream fileOutputStream;
        super.onActivityResult(i, i2, intent);
        SecurityLocksCommon.IsAppDeactive = true;
        switch (i) {
            case 1:
                if (i2 == -1) {
                    return;
                }
                break;
            case 2:
                break;
            default:
                return;
        }
        if (i2 == -1) {
            this.mVideoUri = Utilities.getLastPhotoOrVideo(this);
            String encodedPath = this.mVideoUri.getEncodedPath();
            new SimpleDateFormat("yyyymmddhhmmss").format(new Date());
            String ChangeFileExtention = Utilities.ChangeFileExtention(Utilities.FileName(encodedPath));
            File file = new File(encodedPath);
            Bitmap createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), 1);
            StringBuilder sb = new StringBuilder();
            sb.append(StorageOptionsCommon.STORAGEPATH);
            sb.append(StorageOptionsCommon.VIDEOS);
            sb.append(this.albumName);
            sb.append("/VideoThumnails/");
            File file2 = new File(sb.toString());
            if (VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(StorageOptionsCommon.STORAGEPATH_1);
                sb2.append(StorageOptionsCommon.VIDEOS);
                sb2.append(this.albumName);
                sb2.append("/VideoThumnails/");
                file2 = new File(sb2.toString());
            }
            file2.mkdirs();
            Utilities.FileName(encodedPath);
            StringBuilder sb3 = new StringBuilder();
            sb3.append(StorageOptionsCommon.STORAGEPATH);
            sb3.append(StorageOptionsCommon.VIDEOS);
            sb3.append(this.albumName);
            sb3.append("/VideoThumnails/thumbnil-");
            sb3.append(ChangeFileExtention.substring(0, ChangeFileExtention.lastIndexOf("#")));
            sb3.append("#jpg");
            String sb4 = sb3.toString();
            if (VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                StringBuilder sb5 = new StringBuilder();
                sb5.append(StorageOptionsCommon.STORAGEPATH_1);
                sb5.append(StorageOptionsCommon.VIDEOS);
                sb5.append(this.albumName);
                sb5.append("/VideoThumnails/thumbnil-");
                sb5.append(ChangeFileExtention.substring(0, ChangeFileExtention.lastIndexOf("#")));
                sb5.append("#jpg");
                sb4 = sb5.toString();
            }
            File file3 = new File(sb4);
            String str = null;
            try {
                fileOutputStream = new FileOutputStream(file3, false);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                fileOutputStream = null;
            }
            createVideoThumbnail.compress(CompressFormat.JPEG, 100, fileOutputStream);
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
                Utilities.NSEncryption(file3);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            StringBuilder sb6 = new StringBuilder();
            sb6.append(StorageOptionsCommon.STORAGEPATH);
            sb6.append(StorageOptionsCommon.VIDEOS);
            sb6.append(this.albumName);
            sb6.append("/");
            sb6.append(ChangeFileExtention);
            File file4 = new File(sb6.toString());
            if (VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                StringBuilder sb7 = new StringBuilder();
                sb7.append(StorageOptionsCommon.STORAGEPATH_1);
                sb7.append(StorageOptionsCommon.VIDEOS);
                sb7.append(this.albumName);
                sb7.append("/");
                sb7.append(ChangeFileExtention);
                file4 = new File(sb7.toString());
            }
            try {
                str = Utilities.NSHideFile(this, file, new File(file4.getParent()));
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            AddVideoToDatabase(Utilities.ChangeFileExtentionToOrignal(ChangeFileExtention), encodedPath, sb4, str);
            file.delete();
            try {
                Utilities.NSEncryption(new File(str));
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            LoadVideosFromDB(this._SortBy);
            Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_LONG).show();
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor managedQuery = managedQuery(uri, new String[]{"_data"}, null, null, null);
        int columnIndexOrThrow = managedQuery.getColumnIndexOrThrow("_data");
        managedQuery.moveToFirst();
        return managedQuery.getString(columnIndexOrThrow);
    }

    private void AddVideoToDatabase(String str, String str2, String str3, String str4) {
        Video video = new Video();
        video.setVideoName(str);
        video.setFolderLockVideoLocation(str4);
        video.setOriginalVideoLocation(str2);
        video.setthumbnail_video_location(str3);
        video.setAlbumId(Common.FolderId);
        VideoDAL videoDAL2 = new VideoDAL(this);
        try {
            videoDAL2.OpenWrite();
            videoDAL2.AddVideos(video);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            videoDAL2.close();
            throw th;
        }
        videoDAL2.close();
    }


    public void SelectOrUnSelectAll() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.videos.size(); i++) {
                ((Video) this.videos.get(i)).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_unselectallicon);
        } else {
            for (int i2 = 0; i2 < this.videos.size(); i2++) {
                ((Video) this.videos.get(i2)).SetFileCheck(true);
            }
            this.IsSelectAll = true;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_selectallicon);
        }
        AppGalleryVideoAdapter appGalleryVideoAdapter = new AppGalleryVideoAdapter(this, 1, this.videos, true, _ViewBy);
        this.galleryVideosAdapter = appGalleryVideoAdapter;
        this.imagegrid.setAdapter(this.galleryVideosAdapter);
        this.galleryVideosAdapter.notifyDataSetChanged();
    }

    private void SetcheckFlase() {
        for (int i = 0; i < this.videos.size(); i++) {
            ((Video) this.videos.get(i)).SetFileCheck(false);
        }
        AppGalleryVideoAdapter appGalleryVideoAdapter = new AppGalleryVideoAdapter(this, 1, this.videos, false, _ViewBy);
        this.galleryVideosAdapter = appGalleryVideoAdapter;
        this.imagegrid.setAdapter(this.galleryVideosAdapter);
        this.galleryVideosAdapter.notifyDataSetChanged();
        if (Common.VideoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.VideoThumbnailCurrentPosition);
            Common.VideoThumbnailCurrentPosition = 0;
        }
    }


    public void LoadVideosFromDB(int i) {
        this.videos = new ArrayList();
        VideoDAL videoDAL2 = new VideoDAL(this);
        videoDAL2.OpenRead();
        this.videoCount = videoDAL2.GetVideoCountByAlbumId(Common.FolderId);
        this.videos = videoDAL2.GetVideoByAlbumId(Common.FolderId, i);
        videoDAL2.close();
        AppGalleryVideoAdapter appGalleryVideoAdapter = new AppGalleryVideoAdapter(this, 1, this.videos, false, _ViewBy);
        this.galleryVideosAdapter = appGalleryVideoAdapter;
        this.imagegrid.setAdapter(this.galleryVideosAdapter);
        this.galleryVideosAdapter.notifyDataSetChanged();
        if (this.videos.size() < 1) {
            this.ll_photo_video_grid.setVisibility(View.INVISIBLE);
            this.ll_photo_video_empty.setVisibility(View.VISIBLE);
            this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_video_empty_icon);
            this.lbl_photo_video_empty.setText(R.string.lbl_No_Video);
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
                                Videos_Gallery_Actitvity.this._SortBy = SortBy.Name.ordinal();
                                Videos_Gallery_Actitvity videos_Gallery_Actitvity = Videos_Gallery_Actitvity.this;
                                videos_Gallery_Actitvity.LoadVideosFromDB(videos_Gallery_Actitvity._SortBy);
                                Videos_Gallery_Actitvity.this.AddSortInDB();
                                popupWindow.dismiss();
                                Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
                                break;
                            case 1:
                                Videos_Gallery_Actitvity.this._SortBy = SortBy.Time.ordinal();
                                Videos_Gallery_Actitvity videos_Gallery_Actitvity2 = Videos_Gallery_Actitvity.this;
                                videos_Gallery_Actitvity2.LoadVideosFromDB(videos_Gallery_Actitvity2._SortBy);
                                Videos_Gallery_Actitvity.this.AddSortInDB();
                                popupWindow.dismiss();
                                Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
                                break;
                            case 2:
                                Videos_Gallery_Actitvity.this._SortBy = SortBy.Size.ordinal();
                                Videos_Gallery_Actitvity videos_Gallery_Actitvity3 = Videos_Gallery_Actitvity.this;
                                videos_Gallery_Actitvity3.LoadVideosFromDB(videos_Gallery_Actitvity3._SortBy);
                                Videos_Gallery_Actitvity.this.AddSortInDB();
                                popupWindow.dismiss();
                                Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
                                break;
                        }
                    }
                } else {
                    switch (i2) {
                        case 0:
                            Videos_Gallery_Actitvity._ViewBy = ViewBy.List.ordinal();
                            Videos_Gallery_Actitvity.this.ViewBy();
                            popupWindow.dismiss();
                            Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
                            break;
                        case 1:
                            Videos_Gallery_Actitvity._ViewBy = ViewBy.Tiles.ordinal();
                            Videos_Gallery_Actitvity.this.ViewBy();
                            popupWindow.dismiss();
                            Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
                            break;
                        case 2:
                            Videos_Gallery_Actitvity._ViewBy = ViewBy.LargeTiles.ordinal();
                            Videos_Gallery_Actitvity.this.ViewBy();
                            popupWindow.dismiss();
                            Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
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
        VideoAlbumDAL videoAlbumDAL2 = new VideoAlbumDAL(this);
        videoAlbumDAL2.OpenWrite();
        videoAlbumDAL2.AddSortByInVideoAlbum(this._SortBy);
        videoAlbumDAL2.close();
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
        AppGalleryVideoAdapter appGalleryVideoAdapter = new AppGalleryVideoAdapter(this, 1, this.videos, false, _ViewBy);
        this.galleryVideosAdapter = appGalleryVideoAdapter;
        this.imagegrid.setAdapter(this.galleryVideosAdapter);
        this.galleryVideosAdapter.notifyDataSetChanged();
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


    public void onResume() {
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
        SetcheckFlase();
        Utilities.changeFileExtention(StorageOptionsCommon.VIDEOS);
        this.isEditMode = false;
        this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_AddPhotos_Bottom_Baar.setVisibility(View.INVISIBLE);
        this.ll_EditPhotos.setVisibility(View.INVISIBLE);
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
                this.isEditMode = false;
                this.IsSortingDropdown = false;
                this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
                this.ll_AddPhotos_Bottom_Baar.setVisibility(View.INVISIBLE);
                this.ll_EditPhotos.setVisibility(View.INVISIBLE);
                this.IsSelectAll = false;
                this.btnSelectAll.setVisibility(View.INVISIBLE);
                this._btnSortingDropdown.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            Common.FolderId = 0;
            Common.VideoFolderName = StorageOptionsCommon.VIDEOS_DEFAULT_ALBUM;
            startActivity(new Intent(this, VideosAlbumActivty.class));
            finish();
            DeleteTemporaryVideos();
        }
        return super.onKeyDown(i, keyEvent);
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
                for (int i = 0; i < this.videos.size(); i++) {
                    ((Video) this.videos.get(i)).SetFileCheck(false);
                }
                this.IsSelectAll = false;
                menuItem.setIcon(R.drawable.ic_unselectallicon);
                invalidateOptionsMenu();
            } else {
                for (int i2 = 0; i2 < this.videos.size(); i2++) {
                    ((Video) this.videos.get(i2)).SetFileCheck(true);
                }
                this.IsSelectAll = true;
                menuItem.setIcon(R.drawable.ic_selectallicon);
            }
            AppGalleryVideoAdapter appGalleryVideoAdapter = new AppGalleryVideoAdapter(this, 1, this.videos, true, _ViewBy);
            this.galleryVideosAdapter = appGalleryVideoAdapter;
            this.imagegrid.setAdapter(this.galleryVideosAdapter);
            this.galleryVideosAdapter.notifyDataSetChanged();
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
