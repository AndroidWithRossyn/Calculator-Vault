package com.example.vault.video;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.provider.MediaStore.Video.Media;
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

import com.example.vault.R;
import com.example.vault.adapter.ExpandableListAdapter1;
import com.example.vault.BaseActivity;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securebackupcloud.CloudCommon;
import com.example.vault.securebackupcloud.CloudCommon.DropboxType;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.securitylocks.SecurityLocksSharedPreferences;
import com.example.vault.storageoption.AppSettingsSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;
import com.example.vault.video.adapter.VideosAlbumsAdapter;
import com.example.vault.video.model.Video;
import com.example.vault.video.model.VideoAlbum;
import com.example.vault.video.util.AlbumsGalleryVideosMethods;
import com.example.vault.video.util.VideoAlbumDAL;
import com.example.vault.video.util.VideoDAL;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.banrossyn.imageloader.utils.LibCommonAppClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class VideosAlbumActivty extends BaseActivity {
    private static final int ACTION_TAKE_VIDEO = 2;
    private static final int RESULT_LOAD_IMAGE = 1;
    public static int albumPosition = 0;
    public static boolean isEditMode = false;
    public static boolean isGridView = true;
    int AlbumId = 0;
    boolean IsMoreDropdown = false;
    int Position = 0;
    public ProgressBar Progress;
    int _SortBy = 0;

    public VideosAlbumsAdapter adapter;
    String albumName;
    AppSettingsSharedPreferences appSettingsSharedPreferences;
    private FloatingActionButton btn_Add_Album;
    GridView gridView;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                VideosAlbumActivty.this.Progress.setVisibility(View.GONE);
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
    private Uri mVideoUri;
    private SensorManager sensorManager;
    private Toolbar toolbar;


    public ArrayList<VideoAlbum> videoAlbum;
    private VideoAlbumDAL videoAlbumDAL;

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
//        bgmainImg.setImageResource(R.drawable.bg_ic_video);


        this.gridView = (GridView) findViewById(R.id.AlbumsGalleryGrid);
        this.btn_Add_Album = (FloatingActionButton) findViewById(R.id.btn_Add_Album);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);

        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        setSupportActionBar(this.toolbar);
        TextView title2 = findViewById(R.id.title2);
        title2.setText("Videos");
//        getSupportActionBar().setTitle((int) R.string.lblFeature2);
        // getSupportActionBar().setTitle("");

        this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                VideosAlbumActivty.this.btnBackonClick();
            }
        });
        LibCommonAppClass.IsPhoneGalleryLoad = true;
        SecurityLocksCommon.IsAppDeactive = true;
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        getWindow().addFlags(128);
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_EditAlbum = (LinearLayout) findViewById(R.id.ll_EditAlbum);
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
                this.gridView.setNumColumns(1);
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
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        }
        this.ll_background.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (VideosAlbumActivty.this.IsMoreDropdown) {
                    VideosAlbumActivty.this.IsMoreDropdown = false;
                }
                return false;
            }
        });
        this.Progress.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                videosAlbumActivty.GetAlbumsFromDatabase(videosAlbumActivty._SortBy);
                Message message = new Message();
                message.what = 1;
                VideosAlbumActivty.this.handle.sendMessage(message);
            }
        }, 300);
        this.btn_Add_Album.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!VideosAlbumActivty.isEditMode) {
                    VideosAlbumActivty.this.AddAlbumPopup();
                }
            }
        });
        this.gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                VideosAlbumActivty.albumPosition = VideosAlbumActivty.this.gridView.getFirstVisiblePosition();
                if (VideosAlbumActivty.isEditMode) {
                    VideosAlbumActivty.isEditMode = false;
//                    VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                    VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                    VideosAlbumsAdapter videosAlbumsAdapter = new VideosAlbumsAdapter(videosAlbumActivty, 17367043, videosAlbumActivty.videoAlbum, i, VideosAlbumActivty.isEditMode, VideosAlbumActivty.isGridView);
                    videosAlbumActivty.adapter = videosAlbumsAdapter;
                    VideosAlbumActivty.this.gridView.setAdapter(VideosAlbumActivty.this.adapter);
                    VideosAlbumActivty.this.adapter.notifyDataSetChanged();
                    return;
                }
                SecurityLocksCommon.IsAppDeactive = false;
                Common.FolderId = ((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(i)).getId();
                VideosAlbumActivty.this.startActivity(new Intent(VideosAlbumActivty.this, Videos_Gallery_Actitvity.class));
                VideosAlbumActivty.this.finish();
            }
        });
        this.gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                VideosAlbumActivty.albumPosition = VideosAlbumActivty.this.gridView.getFirstVisiblePosition();
                if (VideosAlbumActivty.isEditMode) {
                    VideosAlbumActivty.isEditMode = false;
//                    VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                    VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                    VideosAlbumsAdapter videosAlbumsAdapter = new VideosAlbumsAdapter(videosAlbumActivty, 17367043, videosAlbumActivty.videoAlbum, i, VideosAlbumActivty.isEditMode, VideosAlbumActivty.isGridView);
                    videosAlbumActivty.adapter = videosAlbumsAdapter;
                    VideosAlbumActivty.this.gridView.setAdapter(VideosAlbumActivty.this.adapter);
                    VideosAlbumActivty.this.adapter.notifyDataSetChanged();
                } else {
                    VideosAlbumActivty.isEditMode = true;
//                    VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Show_Params);
                    VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                    videosAlbumActivty2.Position = i;
                    videosAlbumActivty2.AlbumId = Common.FolderId;
                    VideosAlbumActivty videosAlbumActivty3 = VideosAlbumActivty.this;
                    videosAlbumActivty3.albumName = ((VideoAlbum) videosAlbumActivty3.videoAlbum.get(i)).getAlbumName();
                    VideosAlbumActivty videosAlbumActivty4 = VideosAlbumActivty.this;
                    VideosAlbumsAdapter videosAlbumsAdapter2 = new VideosAlbumsAdapter(videosAlbumActivty4, 17367043, videosAlbumActivty4.videoAlbum, i, VideosAlbumActivty.isEditMode, VideosAlbumActivty.isGridView);
                    videosAlbumActivty4.adapter = videosAlbumsAdapter2;
                    VideosAlbumActivty.this.gridView.setAdapter(VideosAlbumActivty.this.adapter);
                    VideosAlbumActivty.this.adapter.notifyDataSetChanged();
                }
                if (VideosAlbumActivty.albumPosition != 0) {
                    VideosAlbumActivty.this.gridView.setSelection(VideosAlbumActivty.albumPosition);
                }
                return true;
            }
        });
        this.ll_rename_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(VideosAlbumActivty.this.Position)).getId() != 1) {
                    VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                    videosAlbumActivty.EditAlbumPopup(((VideoAlbum) videosAlbumActivty.videoAlbum.get(VideosAlbumActivty.this.Position)).getId(), ((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(VideosAlbumActivty.this.Position)).getAlbumName(), ((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(VideosAlbumActivty.this.Position)).getAlbumLocation());
                    return;
                }
                Toast.makeText(VideosAlbumActivty.this, R.string.lbl_default_album_notrenamed, Toast.LENGTH_SHORT).show();
//                VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                VideosAlbumActivty.isEditMode = false;
                VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                VideosAlbumsAdapter videosAlbumsAdapter = new VideosAlbumsAdapter(videosAlbumActivty2, 17367043, videosAlbumActivty2.videoAlbum, 0, VideosAlbumActivty.isEditMode, VideosAlbumActivty.isGridView);
                videosAlbumActivty2.adapter = videosAlbumsAdapter;
                VideosAlbumActivty.this.gridView.setAdapter(VideosAlbumActivty.this.adapter);
                VideosAlbumActivty.this.adapter.notifyDataSetChanged();
            }
        });
        this.ll_delete_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(VideosAlbumActivty.this.Position)).getId() != 1) {
                    VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                    videosAlbumActivty.DeleteALertDialog(((VideoAlbum) videosAlbumActivty.videoAlbum.get(VideosAlbumActivty.this.Position)).getId(), ((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(VideosAlbumActivty.this.Position)).getAlbumName(), ((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(VideosAlbumActivty.this.Position)).getAlbumLocation());
                    return;
                }
                Toast.makeText(VideosAlbumActivty.this, R.string.lbl_default_album_notdeleted, Toast.LENGTH_SHORT).show();
//                VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                VideosAlbumActivty.isEditMode = false;
                VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                VideosAlbumsAdapter videosAlbumsAdapter = new VideosAlbumsAdapter(videosAlbumActivty2, 17367043, videosAlbumActivty2.videoAlbum, 0, VideosAlbumActivty.isEditMode, VideosAlbumActivty.isGridView);
                videosAlbumActivty2.adapter = videosAlbumsAdapter;
                VideosAlbumActivty.this.gridView.setAdapter(VideosAlbumActivty.this.adapter);
                VideosAlbumActivty.this.adapter.notifyDataSetChanged();
            }
        });
        this.ll_import_from_gallery_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = true;
                Common.FolderId = ((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(VideosAlbumActivty.this.Position)).getId();
                VideosAlbumActivty.this.AlbumId = Common.FolderId;
                VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                videosAlbumActivty.albumName = ((VideoAlbum) videosAlbumActivty.videoAlbum.get(VideosAlbumActivty.this.Position)).getAlbumName();
                VideosAlbumActivty.this.startActivity(new Intent(VideosAlbumActivty.this, ImportAlbumsGalleryVideoActivity.class));
                VideosAlbumActivty.this.finish();
            }
        });
        this.ll_import_from_camera_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                videosAlbumActivty.AlbumId = ((VideoAlbum) videosAlbumActivty.videoAlbum.get(VideosAlbumActivty.this.Position)).getId();
                VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                videosAlbumActivty2.albumName = ((VideoAlbum) videosAlbumActivty2.videoAlbum.get(VideosAlbumActivty.this.Position)).getAlbumName();
                VideosAlbumActivty.this.dispatchTakeVideoIntent();
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
                                VideosAlbumActivty.this._SortBy = SortBy.Name.ordinal();
                                VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                                videosAlbumActivty.GetAlbumsFromDatabase(videosAlbumActivty._SortBy);
                                VideosAlbumActivty.this.appSettingsSharedPreferences.SetPhotosAlbumsSortBy(VideosAlbumActivty.this._SortBy);
                                popupWindow.dismiss();
                                VideosAlbumActivty.this.IsMoreDropdown = false;
                                break;
                            case 1:
                                VideosAlbumActivty.this._SortBy = SortBy.Time.ordinal();
                                VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                                videosAlbumActivty2.GetAlbumsFromDatabase(videosAlbumActivty2._SortBy);
                                VideosAlbumActivty.this.appSettingsSharedPreferences.SetPhotosAlbumsSortBy(VideosAlbumActivty.this._SortBy);
                                popupWindow.dismiss();
                                VideosAlbumActivty.this.IsMoreDropdown = false;
                                break;
                        }
                    }
                } else {
                    switch (i2) {
                        case 0:
                            VideosAlbumActivty.isGridView = false;
                            VideosAlbumActivty.this.ViewBy();
                            popupWindow.dismiss();
                            VideosAlbumActivty.this.IsMoreDropdown = false;
                            break;
                        case 1:
                            VideosAlbumActivty.isGridView = true;
                            VideosAlbumActivty.this.ViewBy();
                            popupWindow.dismiss();
                            VideosAlbumActivty.this.IsMoreDropdown = false;
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
            this.gridView.setNumColumns(2);
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
        VideosAlbumsAdapter videosAlbumsAdapter = new VideosAlbumsAdapter(this, 17367043, this.videoAlbum, 0, isEditMode, isGridView);
        this.adapter = videosAlbumsAdapter;
        this.gridView.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
    }

    public void btnBackonClick() {
        if (isEditMode) {
            SecurityLocksCommon.IsAppDeactive = false;
            isEditMode = false;
//            this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
            VideosAlbumsAdapter videosAlbumsAdapter = new VideosAlbumsAdapter(this, 17367043, this.videoAlbum, 0, isEditMode, isGridView);
            this.adapter = videosAlbumsAdapter;
            this.gridView.setAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, MainiFeaturesActivity.class));
        finish();
    }


    public void GetAlbumsFromDatabase(int i) {
        VideoAlbumDAL videoAlbumDAL2;
        isEditMode = false;
        this.videoAlbumDAL = new VideoAlbumDAL(this);
        try {
            this.videoAlbumDAL.OpenRead();
            this.videoAlbum = (ArrayList) this.videoAlbumDAL.GetAlbums(i);
            Iterator it = this.videoAlbum.iterator();
            while (it.hasNext()) {
                VideoAlbum videoAlbum2 = (VideoAlbum) it.next();
                videoAlbum2.setAlbumCoverLocation(GetCoverPhotoLocation(videoAlbum2.getId()));
            }
            VideosAlbumsAdapter videosAlbumsAdapter = new VideosAlbumsAdapter(this, 17367043, this.videoAlbum, 0, isEditMode, isGridView);
            this.adapter = videosAlbumsAdapter;
            this.gridView.setAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();
            videoAlbumDAL2 = this.videoAlbumDAL;
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
    }


    public void AddAlbumPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.album_add_edit_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        ((TextView) dialog.findViewById(R.id.lbl_Create_Edit_Album)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_Ok)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_Cancel)).setTypeface(createFromAsset);
        final EditText editText = (EditText) dialog.findViewById(R.id.txt_AlbumName);
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (editText.getEditableText().toString().length() <= 0 || editText.getEditableText().toString().trim().isEmpty()) {
                    Toast.makeText(VideosAlbumActivty.this, R.string.lbl_Photos_Album_Create_Album_please_enter, Toast.LENGTH_SHORT).show();
                    return;
                }
                VideosAlbumActivty.this.albumName = editText.getEditableText().toString();
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append("/");
                sb.append(StorageOptionsCommon.VIDEOS);
                sb.append(VideosAlbumActivty.this.albumName);
                File file = new File(sb.toString());
                if (file.exists()) {
                    VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("\"");
                    sb2.append(VideosAlbumActivty.this.albumName);
                    sb2.append("\" already exist");
                    Toast.makeText(videosAlbumActivty, sb2.toString(), Toast.LENGTH_SHORT).show();
                } else if (file.mkdirs()) {
                    AlbumsGalleryVideosMethods albumsGalleryVideosMethods = new AlbumsGalleryVideosMethods();
                    VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                    albumsGalleryVideosMethods.AddAlbumToDatabase(videosAlbumActivty2, videosAlbumActivty2.albumName);
                    Toast.makeText(VideosAlbumActivty.this, R.string.lbl_Photos_Album_Create_Album_Success, Toast.LENGTH_SHORT).show();
                    VideosAlbumActivty videosAlbumActivty3 = VideosAlbumActivty.this;
                    videosAlbumActivty3.GetAlbumsFromDatabase(videosAlbumActivty3._SortBy);
                    dialog.dismiss();
                } else {
                    Toast.makeText(VideosAlbumActivty.this, "ERROR! Some Error in creating album", Toast.LENGTH_SHORT).show();
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
                if (editText.getEditableText().toString().length() <= 0 || editText.getEditableText().toString().trim().isEmpty()) {
                    Toast.makeText(VideosAlbumActivty.this, R.string.lbl_Photos_Album_Create_Album_please_enter, Toast.LENGTH_SHORT).show();
                    return;
                }
                VideosAlbumActivty.this.albumName = editText.getEditableText().toString();
                if (new File(str3).exists()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(StorageOptionsCommon.STORAGEPATH);
                    sb.append(StorageOptionsCommon.VIDEOS);
                    sb.append(VideosAlbumActivty.this.albumName);
                    File file = new File(sb.toString());
                    if (file.exists()) {
                        VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("\"");
                        sb2.append(VideosAlbumActivty.this.albumName);
                        sb2.append("\" already exist");
                        Toast.makeText(videosAlbumActivty, sb2.toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(StorageOptionsCommon.STORAGEPATH);
                    sb3.append(StorageOptionsCommon.VIDEOS);
                    sb3.append(str4);
                    File file2 = new File(sb3.toString());
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    if (file2.renameTo(file)) {
                        AlbumsGalleryVideosMethods albumsGalleryVideosMethods = new AlbumsGalleryVideosMethods();
                        VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                        albumsGalleryVideosMethods.UpdateAlbumInDatabase(videosAlbumActivty2, i2, videosAlbumActivty2.albumName);
                        Toast.makeText(VideosAlbumActivty.this, R.string.lbl_Photos_Album_Create_Album_Success_renamed, Toast.LENGTH_SHORT).show();
                        VideosAlbumActivty videosAlbumActivty3 = VideosAlbumActivty.this;
                        videosAlbumActivty3.GetAlbumsFromDatabase(videosAlbumActivty3._SortBy);
                        dialog2.dismiss();
//                        VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                    }
                }
            }
        };
        linearLayout.setOnClickListener(r0);
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
//                VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                VideosAlbumActivty.isEditMode = false;
                VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                videosAlbumActivty.GetAlbumsFromDatabase(videosAlbumActivty._SortBy);
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
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
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
        LinearLayout linearLayout2 = (LinearLayout) dialog.findViewById(R.id.ll_Ok);
        final int i2 = i;
        final String str3 = str;
        final String str4 = str2;
        final Dialog dialog2 = dialog;
        OnClickListener r0 = new OnClickListener() {
            public void onClick(View view) {
                VideosAlbumActivty.this.DeleteAlbum(i2, str3, str4);
                dialog2.dismiss();
//                VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
            }
        };
        linearLayout2.setOnClickListener(r0);
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
//                VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                VideosAlbumActivty.isEditMode = false;
                VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                videosAlbumActivty.GetAlbumsFromDatabase(videosAlbumActivty._SortBy);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void DeleteAlbum(int i, String str, String str2) {
        File file = new File(str2);
        DeleteAlbumFromDatabase(i);
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append(File.separator);
        sb.append("VideoThumnails");
        File file2 = new File(sb.toString());
        if (file2.exists()) {
            file2.delete();
        }
        try {
            Utilities.DeleteAlbum(file, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void DeleteAlbumFromDatabase(int i) {
        VideoAlbumDAL videoAlbumDAL2 = new VideoAlbumDAL(this);
        try {
            videoAlbumDAL2.OpenWrite();
            videoAlbumDAL2.DeleteAlbumById(i);
            Toast.makeText(this, R.string.lbl_Photos_Album_delete_success, Toast.LENGTH_SHORT).show();
            GetAlbumsFromDatabase(this._SortBy);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            videoAlbumDAL2.close();
            throw th;
        }
        videoAlbumDAL2.close();
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
            GetAlbumsFromDatabase(this._SortBy);
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
        video.setAlbumId(this.AlbumId);
        VideoDAL videoDAL = new VideoDAL(this);
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


    public String GetCoverPhotoLocation(int i) {
        new Video();
        VideoDAL videoDAL = new VideoDAL(this);
        try {
            videoDAL.OpenRead();
            String str = videoDAL.GetCoverVideo(i).getthumbnail_video_location();
            videoDAL.close();
            return str;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            videoDAL.close();
            return null;
        } catch (Throwable th) {
            videoDAL.close();
            throw th;
        }
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
        Common.imageLoader.clearMemoryCache();
        Common.imageLoader.clearDiskCache();
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
            if (isEditMode) {
                SecurityLocksCommon.IsAppDeactive = false;
                isEditMode = false;
//                this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
                VideosAlbumsAdapter videosAlbumsAdapter = new VideosAlbumsAdapter(this, 17367043, this.videoAlbum, 0, isEditMode, isGridView);
                this.adapter = videosAlbumsAdapter;
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
            CloudCommon.ModuleType = DropboxType.Videos.ordinal();
            Utilities.StartCloudActivity(this);
            return true;
        }

//        else {
//            SecurityLocksCommon.IsAppDeactive = false;
//            InAppPurchaseActivity._cameFrom = CameFrom.VideoAlbum.ordinal();
//            startActivity(new Intent(this, InAppPurchaseActivity.class));
//            finish();
//            return true;
//        }
        return false;
    }
}