package com.example.vault.photo;


import static com.example.vault.utilities.Common.AppPackageName;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.vault.R;
import com.example.vault.adapter.ExpandableListAdapter1;
import com.example.vault.BaseActivity;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.photo.adapter.PhotosAlbumsAdapter;
import com.example.vault.photo.model.Photo;
import com.example.vault.photo.model.PhotoAlbum;
import com.example.vault.photo.util.AlbumsGalleryPhotosMethods;
import com.example.vault.photo.util.PhotoAlbumDAL;
import com.example.vault.photo.util.PhotoDAL;
import com.example.vault.securebackupcloud.CloudCommon;
import com.example.vault.securebackupcloud.CloudCommon.DropboxType;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.securitylocks.SecurityLocksSharedPreferences;
import com.example.vault.storageoption.AppSettingsSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.banrossyn.imageloader.utils.LibCommonAppClass;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class PhotosAlbumActivty extends BaseActivity {
    private static int RESULT_LOAD_CAMERA = 1;
    public static int albumPosition = 0;
    public static boolean isEditPhotoAlbum = false;
    public static boolean isGridView = false;
    int AlbumId = 0;
    boolean IsMoreDropdown = false;
    public ProgressBar Progress;
    int _SortBy = 0;
    public PhotosAlbumsAdapter adapter;
    String albumName = "";
    AppSettingsSharedPreferences appSettingsSharedPreferences;
    private FloatingActionButton btn_Add_Album;
    File cacheDir;

    public GridView gridView;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                PhotosAlbumActivty.this.Progress.setVisibility(View.GONE);
            }
            super.handleMessage(message);
        }
    };
    private ImageButton ib_more;
    LinearLayout ll_EditAlbum;
//    LayoutParams ll_EditAlbum_Hide_Params;
//    LayoutParams ll_EditAlbum_Show_Params;
    private LinearLayout ll_anchor;
    LinearLayout ll_background;
    LinearLayout ll_delete_btn;
    LinearLayout ll_import_from_camera_btn;
    LinearLayout ll_import_from_gallery_btn;
    LinearLayout ll_rename_btn;
    private Uri outputFileUri;
    private PhotoAlbumDAL photoAlbumDAL;

    public ArrayList<PhotoAlbum> photoAlbums;
    int position;
    private SensorManager sensorManager;
    private Toolbar toolbar;


    public enum SortBy {
        Name, Time
    }

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_photos_videos_albums);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


//        ImageView bgmainImg = findViewById(R.id.bgmainImg);
//        bgmainImg.setImageResource(R.drawable.bg_ic_photos);

        Log.d("TAG", "PhotoAlumActivity");
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(this.toolbar);
        TextView title2 = findViewById(R.id.title2);
        title2.setText("Photos");
//        getSupportActionBar().setTitle((int) R.string.lblFeature1);
        // getSupportActionBar().setTitle("");

        this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PhotosAlbumActivty.this.Back();
            }
        });
        this.gridView = (GridView) findViewById(R.id.AlbumsGalleryGrid);
        this.btn_Add_Album = (FloatingActionButton) findViewById(R.id.btn_Add_Album);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        LibCommonAppClass.IsPhoneGalleryLoad = true;
        SecurityLocksCommon.IsAppDeactive = true;
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_EditAlbum = (LinearLayout) findViewById(R.id.ll_EditAlbum);
        this.gridView = (GridView) findViewById(R.id.AlbumsGalleryGrid);
        this.ll_rename_btn = (LinearLayout) findViewById(R.id.ll_rename_btn);
        this.ll_delete_btn = (LinearLayout) findViewById(R.id.ll_delete_btn);
        this.ll_import_from_gallery_btn = (LinearLayout) findViewById(R.id.ll_import_from_gallery_btn);
        this.ll_import_from_camera_btn = (LinearLayout) findViewById(R.id.ll_import_from_camera_btn);
//        this.ll_EditAlbum_Show_Params = new LayoutParams(-1, -2);
//        this.ll_EditAlbum_Hide_Params = new LayoutParams(-2, 0);
//        this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
        this.appSettingsSharedPreferences = AppSettingsSharedPreferences.GetObject(this);
        this._SortBy = this.appSettingsSharedPreferences.GetPhotosAlbumsSortBy();
        if (isGridView) {
            this.gridView.setNumColumns(1);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
        } else {
            this.gridView.setNumColumns(1);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
        }
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(3);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(5);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        }
        this.ll_background.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (PhotosAlbumActivty.this.IsMoreDropdown) {
                    PhotosAlbumActivty.this.IsMoreDropdown = false;
                }
                return false;
            }
        });
        this.Progress.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                photosAlbumActivty.GetAlbumsFromDatabase(photosAlbumActivty._SortBy);
                Message message = new Message();
                message.what = 1;
                PhotosAlbumActivty.this.handle.sendMessage(message);
            }
        }, 300);
        this.btn_Add_Album.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!PhotosAlbumActivty.isEditPhotoAlbum) {
                    PhotosAlbumActivty.this.AddAlbumPopup();
                }
            }
        });
        this.gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                PhotosAlbumActivty.albumPosition = PhotosAlbumActivty.this.gridView.getFirstVisiblePosition();
                if (PhotosAlbumActivty.isEditPhotoAlbum) {
                    PhotosAlbumActivty.isEditPhotoAlbum = false;
//                    PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                    PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                    PhotosAlbumsAdapter photosAlbumsAdapter = new PhotosAlbumsAdapter(photosAlbumActivty, 17367043, photosAlbumActivty.photoAlbums, i, PhotosAlbumActivty.isEditPhotoAlbum, PhotosAlbumActivty.isGridView);
                    photosAlbumActivty.adapter = photosAlbumsAdapter;
                    PhotosAlbumActivty.this.gridView.setAdapter(PhotosAlbumActivty.this.adapter);
                    PhotosAlbumActivty.this.adapter.notifyDataSetChanged();
                    return;
                }
                SecurityLocksCommon.IsAppDeactive = false;
                Common.FolderId = ((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(i)).getId();
                PhotosAlbumActivty.this.startActivity(new Intent(PhotosAlbumActivty.this, Photos_Gallery_Actitvity.class));
                PhotosAlbumActivty.this.finish();
            }
        });
        this.gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                PhotosAlbumActivty.albumPosition = PhotosAlbumActivty.this.gridView.getFirstVisiblePosition();
                if (PhotosAlbumActivty.isEditPhotoAlbum) {
                    PhotosAlbumActivty.isEditPhotoAlbum = false;
//                    PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                    PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                    PhotosAlbumsAdapter photosAlbumsAdapter = new PhotosAlbumsAdapter(photosAlbumActivty, 17367043, photosAlbumActivty.photoAlbums, i, PhotosAlbumActivty.isEditPhotoAlbum, PhotosAlbumActivty.isGridView);
                    photosAlbumActivty.adapter = photosAlbumsAdapter;
                    PhotosAlbumActivty.this.gridView.setAdapter(PhotosAlbumActivty.this.adapter);
                    PhotosAlbumActivty.this.adapter.notifyDataSetChanged();
                } else {
                    PhotosAlbumActivty.isEditPhotoAlbum = true;
//                    PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Show_Params);
                    PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                    photosAlbumActivty2.position = i;
                    photosAlbumActivty2.AlbumId = Common.FolderId;
                    PhotosAlbumActivty photosAlbumActivty3 = PhotosAlbumActivty.this;
                    photosAlbumActivty3.albumName = ((PhotoAlbum) photosAlbumActivty3.photoAlbums.get(PhotosAlbumActivty.this.position)).getAlbumName();
                    PhotosAlbumActivty photosAlbumActivty4 = PhotosAlbumActivty.this;
                    PhotosAlbumsAdapter photosAlbumsAdapter2 = new PhotosAlbumsAdapter(photosAlbumActivty4, 17367043, photosAlbumActivty4.photoAlbums, i, PhotosAlbumActivty.isEditPhotoAlbum, PhotosAlbumActivty.isGridView);
                    photosAlbumActivty4.adapter = photosAlbumsAdapter2;
                    PhotosAlbumActivty.this.gridView.setAdapter(PhotosAlbumActivty.this.adapter);
                    PhotosAlbumActivty.this.adapter.notifyDataSetChanged();
                }
                if (PhotosAlbumActivty.albumPosition != 0) {
                    PhotosAlbumActivty.this.gridView.setSelection(PhotosAlbumActivty.albumPosition);
                }
                return true;
            }
        });
        this.ll_rename_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getId() != 1) {
                    PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                    photosAlbumActivty.EditAlbumPopup(((PhotoAlbum) photosAlbumActivty.photoAlbums.get(PhotosAlbumActivty.this.position)).getId(), ((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getAlbumName(), ((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getAlbumLocation());
                    return;
                }
                Toast.makeText(PhotosAlbumActivty.this, R.string.lbl_default_album_notrenamed, Toast.LENGTH_SHORT).show();
                PhotosAlbumActivty.isEditPhotoAlbum = false;
//                PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                PhotosAlbumsAdapter photosAlbumsAdapter = new PhotosAlbumsAdapter(photosAlbumActivty2, 17367043, photosAlbumActivty2.photoAlbums, 0, PhotosAlbumActivty.isEditPhotoAlbum, PhotosAlbumActivty.isGridView);
                photosAlbumActivty2.adapter = photosAlbumsAdapter;
                PhotosAlbumActivty.this.gridView.setAdapter(PhotosAlbumActivty.this.adapter);
                PhotosAlbumActivty.this.adapter.notifyDataSetChanged();
            }
        });
        this.ll_delete_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getId() != 1) {
                    PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                    photosAlbumActivty.DeleteALertDialog(((PhotoAlbum) photosAlbumActivty.photoAlbums.get(PhotosAlbumActivty.this.position)).getId(), ((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getAlbumName(), ((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getAlbumLocation());
                    return;
                }
                Toast.makeText(PhotosAlbumActivty.this, R.string.lbl_default_album_notdeleted, Toast.LENGTH_SHORT).show();
                PhotosAlbumActivty.isEditPhotoAlbum = false;
//                PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                PhotosAlbumsAdapter photosAlbumsAdapter = new PhotosAlbumsAdapter(photosAlbumActivty2, 17367043, photosAlbumActivty2.photoAlbums, 0, PhotosAlbumActivty.isEditPhotoAlbum, PhotosAlbumActivty.isGridView);
                photosAlbumActivty2.adapter = photosAlbumsAdapter;
                PhotosAlbumActivty.this.gridView.setAdapter(PhotosAlbumActivty.this.adapter);
                PhotosAlbumActivty.this.adapter.notifyDataSetChanged();
            }
        });
        this.ll_import_from_gallery_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = true;
                Common.FolderId = ((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getId();
                PhotosAlbumActivty.this.AlbumId = Common.FolderId;
                PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                photosAlbumActivty.albumName = ((PhotoAlbum) photosAlbumActivty.photoAlbums.get(PhotosAlbumActivty.this.position)).getAlbumName();
                Intent intent = new Intent(PhotosAlbumActivty.this, ImportAlbumsGalleryPhotoActivity.class);
                intent.putExtra("ALBUM_ID", PhotosAlbumActivty.this.AlbumId);
                intent.putExtra("FOLDER_NAME", PhotosAlbumActivty.this.AlbumId);
                PhotosAlbumActivty.this.startActivity(intent);
                PhotosAlbumActivty.this.finish();
            }
        });
        this.ll_import_from_camera_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Common.FolderId = ((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getId();
                PhotosAlbumActivty.this.AlbumId = Common.FolderId;
                PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                photosAlbumActivty.albumName = ((PhotoAlbum) photosAlbumActivty.photoAlbums.get(PhotosAlbumActivty.this.position)).getAlbumName();
                PhotosAlbumActivty.this.openCameraIntent();
            }
        });
        int i = albumPosition;
        if (i != 0) {
            this.gridView.setSelection(i);
            albumPosition = 0;
        }
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
        arrayList2.add("Tile");
        hashMap.put(arrayList.get(0), arrayList2);
        arrayList.add("Sort by");
        arrayList3.add("Name");
        arrayList3.add("Date");
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
                                PhotosAlbumActivty.this._SortBy = SortBy.Name.ordinal();
                                PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                                photosAlbumActivty.GetAlbumsFromDatabase(photosAlbumActivty._SortBy);
                                PhotosAlbumActivty.this.appSettingsSharedPreferences.SetPhotosAlbumsSortBy(PhotosAlbumActivty.this._SortBy);
                                popupWindow.dismiss();
                                PhotosAlbumActivty.this.IsMoreDropdown = false;
                                break;
                            case 1:
                                PhotosAlbumActivty.this._SortBy = SortBy.Time.ordinal();
                                PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                                photosAlbumActivty2.GetAlbumsFromDatabase(photosAlbumActivty2._SortBy);
                                PhotosAlbumActivty.this.appSettingsSharedPreferences.SetPhotosAlbumsSortBy(PhotosAlbumActivty.this._SortBy);
                                popupWindow.dismiss();
                                PhotosAlbumActivty.this.IsMoreDropdown = false;
                                break;
                        }
                    }
                } else {
                    switch (i2) {
                        case 0:
                            PhotosAlbumActivty.isGridView = false;
                            PhotosAlbumActivty.this.ViewBy();
                            popupWindow.dismiss();
                            PhotosAlbumActivty.this.IsMoreDropdown = false;
                            break;
                        case 1:
                            PhotosAlbumActivty.isGridView = true;
                            PhotosAlbumActivty.this.ViewBy();
                            popupWindow.dismiss();
                            PhotosAlbumActivty.this.IsMoreDropdown = false;
                            break;
                    }
                }
                return false;
            }
        });
        if (!this.IsMoreDropdown) {
            LinearLayout linearLayout = this.ll_anchor;
            popupWindow.showAsDropDown(linearLayout, linearLayout.getWidth(), 0);
            this.IsMoreDropdown = true;
            return;
        }
        popupWindow.dismiss();
        this.IsMoreDropdown = false;
    }

    public void ViewBy() {
        if (isGridView) {
            this.gridView.setNumColumns(1);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
        } else {
            this.gridView.setNumColumns(1);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
        }
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(3);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(5);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        }
        PhotosAlbumsAdapter photosAlbumsAdapter = new PhotosAlbumsAdapter(this, 17367043, this.photoAlbums, 0, isEditPhotoAlbum, isGridView);
        this.adapter = photosAlbumsAdapter;
        this.gridView.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
    }


    public void GetAlbumsFromDatabase(int i) {
        PhotoAlbumDAL photoAlbumDAL2;
        isEditPhotoAlbum = false;
        this.photoAlbumDAL = new PhotoAlbumDAL(this);
        try {
            this.photoAlbumDAL.OpenRead();
            this.photoAlbums = (ArrayList) this.photoAlbumDAL.GetAlbums(i);
            Iterator it = this.photoAlbums.iterator();
            while (it.hasNext()) {
                PhotoAlbum photoAlbum = (PhotoAlbum) it.next();
                photoAlbum.setAlbumCoverLocation(GetCoverPhotoLocation(photoAlbum.getId()));
            }
            PhotosAlbumsAdapter photosAlbumsAdapter = new PhotosAlbumsAdapter(this, 17367043, this.photoAlbums, 0, isEditPhotoAlbum, isGridView);
            this.adapter = photosAlbumsAdapter;
            this.gridView.setAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();
            photoAlbumDAL2 = this.photoAlbumDAL;
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
    }


    public void AddAlbumPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.album_add_edit_popup);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        ((TextView) dialog.findViewById(R.id.lbl_Create_Edit_Album)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_Ok)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_Cancel)).setTypeface(createFromAsset);
        final EditText editText = (EditText) dialog.findViewById(R.id.txt_AlbumName);
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (editText.getEditableText().toString().length() <= 0 || editText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(PhotosAlbumActivty.this, R.string.lbl_Photos_Album_Create_Album_please_enter, Toast.LENGTH_SHORT).show();
                    return;
                }
                PhotosAlbumActivty.this.albumName = editText.getEditableText().toString();
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append("/");
                sb.append(StorageOptionsCommon.PHOTOS);
                sb.append(PhotosAlbumActivty.this.albumName);
                File file = new File(sb.toString());
                if (file.exists()) {
                    PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("\"");
                    sb2.append(PhotosAlbumActivty.this.albumName);
                    sb2.append("\" already exist");
                    Toast.makeText(photosAlbumActivty, sb2.toString(), Toast.LENGTH_SHORT).show();
                } else if (file.mkdirs()) {
                    AlbumsGalleryPhotosMethods albumsGalleryPhotosMethods = new AlbumsGalleryPhotosMethods();
                    PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                    albumsGalleryPhotosMethods.AddAlbumToDatabase(photosAlbumActivty2, photosAlbumActivty2.albumName);
                    Toast.makeText(PhotosAlbumActivty.this, R.string.lbl_Photos_Album_Create_Album_Success, Toast.LENGTH_SHORT).show();
                    PhotosAlbumActivty photosAlbumActivty3 = PhotosAlbumActivty.this;
                    photosAlbumActivty3.GetAlbumsFromDatabase(photosAlbumActivty3._SortBy);
                    dialog.dismiss();
                } else {
                    Toast.makeText(PhotosAlbumActivty.this, "ERROR! Some Error in creating album", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void EditAlbumPopup(int i, String str, String str2) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.album_add_edit_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        TextView textView = (TextView) dialog.findViewById(R.id.lbl_Create_Edit_Album);
        textView.setTypeface(createFromAsset);
        textView.setText(R.string.lbl_Photos_Album_Rename_Album);
        ((TextView) dialog.findViewById(R.id.lbl_Ok)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_Cancel)).setTypeface(createFromAsset);
        final EditText editText = (EditText) dialog.findViewById(R.id.txt_AlbumName);
        if (str.length() > 0) {
            editText.setText(str);
        }
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_Ok);
        final String str3 = str2;
        final String str4 = str;
        final int i2 = i;
        final Dialog dialog2 = dialog;
        OnClickListener r0 = new OnClickListener() {
            public void onClick(View view) {
                if (editText.getEditableText().toString().length() <= 0 || editText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(PhotosAlbumActivty.this, R.string.lbl_Photos_Album_Create_Album_please_enter, Toast.LENGTH_SHORT).show();
                    return;
                }
                PhotosAlbumActivty.this.albumName = editText.getEditableText().toString();
                if (new File(str3).exists()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(StorageOptionsCommon.STORAGEPATH);
                    sb.append(StorageOptionsCommon.PHOTOS);
                    sb.append(PhotosAlbumActivty.this.albumName);
                    File file = new File(sb.toString());
                    if (file.exists()) {
                        PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("\"");
                        sb2.append(PhotosAlbumActivty.this.albumName);
                        sb2.append("\" already exist");
                        Toast.makeText(photosAlbumActivty, sb2.toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(StorageOptionsCommon.STORAGEPATH);
                    sb3.append(StorageOptionsCommon.PHOTOS);
                    sb3.append(str4);
                    File file2 = new File(sb3.toString());
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    if (file2.renameTo(file)) {
                        AlbumsGalleryPhotosMethods albumsGalleryPhotosMethods = new AlbumsGalleryPhotosMethods();
                        PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                        albumsGalleryPhotosMethods.UpdateAlbumInDatabase(photosAlbumActivty2, i2, photosAlbumActivty2.albumName);
                        Toast.makeText(PhotosAlbumActivty.this, R.string.lbl_Photos_Album_Create_Album_Success_renamed, Toast.LENGTH_SHORT).show();
                        PhotosAlbumActivty photosAlbumActivty3 = PhotosAlbumActivty.this;
                        photosAlbumActivty3.GetAlbumsFromDatabase(photosAlbumActivty3._SortBy);
                        dialog2.dismiss();
//                        PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                    }
                }
            }
        };
        linearLayout.setOnClickListener(r0);
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
//                PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                PhotosAlbumActivty.isEditPhotoAlbum = false;
                PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                PhotosAlbumsAdapter photosAlbumsAdapter = new PhotosAlbumsAdapter(photosAlbumActivty, 17367043, photosAlbumActivty.photoAlbums, PhotosAlbumActivty.this.position, PhotosAlbumActivty.isEditPhotoAlbum, PhotosAlbumActivty.isGridView);
                photosAlbumActivty.adapter = photosAlbumsAdapter;
                PhotosAlbumActivty.this.gridView.setAdapter(PhotosAlbumActivty.this.adapter);
                PhotosAlbumActivty.this.adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void DeleteALertDialog(int i, String str, String str2) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.confirmation_message_box);
        dialog.setCancelable(true);
        TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
        textView.setTypeface(Typeface.createFromAsset(getAssets(), "ebrima.ttf"));
        if (str.length() > 9) {
            StringBuilder sb = new StringBuilder();
            sb.append("Are you sure you want to delete ");
            sb.append(str.substring(0, 8));
            sb.append(".. including its data?");
            textView.setText(sb.toString());
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Are you sure you want to delete ");
            sb2.append(str);
            sb2.append(" including its data?");
            textView.setText(sb2.toString());
        }
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_Ok);
        final int i2 = i;
        final String str3 = str;
        final String str4 = str2;
        final Dialog dialog2 = dialog;
        OnClickListener r0 = new OnClickListener() {
            public void onClick(View view) {
                PhotosAlbumActivty.this.DeleteAlbum(i2, str3, str4);
                dialog2.dismiss();
//                PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
            }
        };
        linearLayout.setOnClickListener(r0);
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PhotosAlbumActivty.isEditPhotoAlbum = false;
//                PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                PhotosAlbumsAdapter photosAlbumsAdapter = new PhotosAlbumsAdapter(photosAlbumActivty, 17367043, photosAlbumActivty.photoAlbums, PhotosAlbumActivty.this.position, PhotosAlbumActivty.isEditPhotoAlbum, PhotosAlbumActivty.isGridView);
                photosAlbumActivty.adapter = photosAlbumsAdapter;
                PhotosAlbumActivty.this.gridView.setAdapter(PhotosAlbumActivty.this.adapter);
                PhotosAlbumActivty.this.adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void DeleteAlbum(int i, String str, String str2) {
        File file = new File(str2);
        DeleteAlbumFromDatabase(i);
        try {
            Utilities.DeleteAlbum(file, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void DeleteAlbumFromDatabase(int i) {
        PhotoAlbumDAL photoAlbumDAL2 = new PhotoAlbumDAL(this);
        try {
            photoAlbumDAL2.OpenWrite();
            photoAlbumDAL2.DeleteAlbumById(i);
            Toast.makeText(this, R.string.lbl_Photos_Album_delete_success, Toast.LENGTH_SHORT).show();
            GetAlbumsFromDatabase(this._SortBy);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            photoAlbumDAL2.close();
            throw th;
        }
        photoAlbumDAL2.close();
    }

    @SuppressLint({"SimpleDateFormat"})
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
                e.printStackTrace();
                str = str2;
            }
            if (!str.equals("")) {
                new AlbumsGalleryPhotosMethods().AddPhotoToDatabase(this, Common.FolderId, this.cacheDir.getName(), str, this.cacheDir.getAbsolutePath());
                Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show();
                GetAlbumsFromDatabase(this._SortBy);
            }
        }
    }


    public Bitmap GetCoverPhoto(int i) {
        String str;
        new Photo();
        PhotoDAL photoDAL = new PhotoDAL(this);
        try {
            photoDAL.OpenRead();
            str = photoDAL.GetCoverPhoto(i).getFolderLockPhotoLocation();
            photoDAL.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            photoDAL.close();
            str = null;
        } catch (Throwable th) {
            photoDAL.close();
            throw th;
        }
        if (str == null) {
            return null;
        }
        File file = new File(str);
        if (file.exists()) {
            return Utilities.DecodeFile(file);
        }
        return null;
    }


    public String GetCoverPhotoLocation(int i) {
        String str;
        new Photo();
        PhotoDAL photoDAL = new PhotoDAL(this);
        try {
            photoDAL.OpenRead();
            str = photoDAL.GetCoverPhoto(i).getFolderLockPhotoLocation();
            photoDAL.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            photoDAL.close();
            str = null;
        } catch (Throwable th) {
            photoDAL.close();
            throw th;
        }
        if (str != null) {
            return str;
        }
        return null;
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
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(5);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (isGridView) {
                this.gridView.setNumColumns(3);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        } else if (configuration.orientation != 1) {
        } else {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(3);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        }
    }

    public void onPause() {
//        this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
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

    public void onStop() {
        super.onStop();
    }

    public void onResume() {
//        this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            if (isEditPhotoAlbum) {
                SecurityLocksCommon.IsAppDeactive = false;
                isEditPhotoAlbum = false;
//                this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
                PhotosAlbumsAdapter photosAlbumsAdapter = new PhotosAlbumsAdapter(this, 17367043, this.photoAlbums, 0, isEditPhotoAlbum, isGridView);
                this.adapter = photosAlbumsAdapter;
                this.gridView.setAdapter(this.adapter);
                this.adapter.notifyDataSetChanged();
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, MainiFeaturesActivity.class));
            finish();
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
        File createTempFile = File.createTempFile(sb.toString(), ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        createTempFile.getAbsolutePath();
        return createTempFile;
    }

    public void Back() {
        if (isEditPhotoAlbum) {

            SecurityLocksCommon.IsAppDeactive = false;
            isEditPhotoAlbum = false;
//            this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
            PhotosAlbumsAdapter photosAlbumsAdapter = new PhotosAlbumsAdapter(this, 17367043, this.photoAlbums, 0, isEditPhotoAlbum, isGridView);
            this.adapter = photosAlbumsAdapter;
            this.gridView.setAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();
            return;

        }
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, MainiFeaturesActivity.class));
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more_cloud, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId != R.id.action_cloud) {
            if (itemId != R.id.action_more) {
                return super.onOptionsItemSelected(menuItem);
            }
            this.IsMoreDropdown = false;
            showPopupWindow();
            return true;
        } else if (Common.isPurchased) {
            SecurityLocksCommon.IsAppDeactive = false;
            CloudCommon.ModuleType = DropboxType.Photos.ordinal();
            Utilities.StartCloudActivity(this);
            return true;
        }

//        else {
//            SecurityLocksCommon.IsAppDeactive = false;
//            InAppPurchaseActivity._cameFrom = CameFrom.PhotoAlbum.ordinal();
//            startActivity(new Intent(this, InAppPurchaseActivity.class));
//            finish();
//            return true;
//        }
        return false;
    }
}
