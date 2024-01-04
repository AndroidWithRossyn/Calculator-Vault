package com.example.vault.audio;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.vault.R;
import com.example.vault.adapter.ExpandableListAdapter1;
import com.example.vault.audio.adapter.AudioPlayListAdapter;
import com.example.vault.audio.model.AudioPlayListEnt;
import com.example.vault.audio.util.AudioPlayListDAL;
import com.example.vault.audio.util.AudioPlaylistGalleryMethods;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.banrossyn.imageloader.utils.LibCommonAppClass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AudioPlayListActivity extends BaseActivity {
    private static int RESULT_LOAD_CAMERA = 1;
    public static int albumPosition = 0;
    public static boolean isEditAudioAlbum = false;
    public static boolean isGridView = true;
    int AlbumId = 0;
    boolean IsMoreDropdown = false;
    public ProgressBar Progress;
    int _SortBy = 0;

    public AudioPlayListAdapter adapter;
    String albumName = "";
    AppSettingsSharedPreferences appSettingsSharedPreferences;

    public ArrayList<AudioPlayListEnt> audioAlbums;
    private AudioPlayListDAL audioPlayListDAL;
    private FloatingActionButton btn_Add_Album;

    public GridView gridView;

    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                AudioPlayListActivity.this.Progress.setVisibility(View.GONE);
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
    LinearLayout ll_rename_btn;
    private Uri outputFileUri;
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
        setContentView((int) R.layout.activity_audios_test);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Log.d("TAG", "Audio");
        this.gridView = (GridView) findViewById(R.id.AlbumsGalleryGrid);
        this.btn_Add_Album = (FloatingActionButton) findViewById(R.id.btn_Add_Album);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        setSupportActionBar(this.toolbar);
//        getSupportActionBar().setTitle((int) R.string.lblFeature9);
        // getSupportActionBar().setTitle("");

        this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AudioPlayListActivity.this.btnBackonClick();
            }
        });
        LibCommonAppClass.IsPhoneGalleryLoad = true;
        SecurityLocksCommon.IsAppDeactive = true;
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_EditAlbum = (LinearLayout) findViewById(R.id.ll_EditAlbum);
        this.gridView = (GridView) findViewById(R.id.AlbumsGalleryGrid);
        this.ll_rename_btn = (LinearLayout) findViewById(R.id.ll_rename_btn);
        this.ll_delete_btn = (LinearLayout) findViewById(R.id.ll_delete_btn);
//        this.ll_EditAlbum_Show_Params = new LayoutParams(-1, -2);
//        this.ll_EditAlbum_Hide_Params = new LayoutParams(-2, 0);
//        this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
        this.ib_more = (ImageButton) findViewById(R.id.ib_more);
        this.ib_more.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tvActivityTopBaar_Title)).setText(R.string.lblFeature9);
        this.appSettingsSharedPreferences = AppSettingsSharedPreferences.GetObject(this);
        this._SortBy = this.appSettingsSharedPreferences.GetAudioViewBy();
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
                if (AudioPlayListActivity.this.IsMoreDropdown) {
                    AudioPlayListActivity.this.IsMoreDropdown = false;
                }
                return false;
            }
        });
        this.Progress.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                audioPlayListActivity.GetAlbumsFromDatabase(audioPlayListActivity._SortBy);
                Message message = new Message();
                message.what = 1;
                AudioPlayListActivity.this.handle.sendMessage(message);
            }
        }, 300);
        this.btn_Add_Album.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!AudioPlayListActivity.isEditAudioAlbum) {
                    AudioPlayListActivity.this.AddAlbumPopup();
                }
            }
        });
        this.gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                AudioPlayListActivity.albumPosition = AudioPlayListActivity.this.gridView.getFirstVisiblePosition();
                if (AudioPlayListActivity.isEditAudioAlbum) {
                    AudioPlayListActivity.isEditAudioAlbum = false;
//                    AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
                    AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                    AudioPlayListAdapter audioPlayListAdapter = new AudioPlayListAdapter(audioPlayListActivity, 17367043, audioPlayListActivity.audioAlbums, i, AudioPlayListActivity.isEditAudioAlbum, AudioPlayListActivity.isGridView);
                    audioPlayListActivity.adapter = audioPlayListAdapter;
                    AudioPlayListActivity.this.gridView.setAdapter(AudioPlayListActivity.this.adapter);
                    AudioPlayListActivity.this.adapter.notifyDataSetChanged();
                    return;
                }
                SecurityLocksCommon.IsAppDeactive = false;
                Common.FolderId = ((AudioPlayListEnt) AudioPlayListActivity.this.audioAlbums.get(i)).getId();
                AudioPlayListActivity.this.startActivity(new Intent(AudioPlayListActivity.this, AudioActivity.class));
                AudioPlayListActivity.this.finish();
            }
        });
        this.gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                AudioPlayListActivity.albumPosition = AudioPlayListActivity.this.gridView.getFirstVisiblePosition();
                if (AudioPlayListActivity.isEditAudioAlbum) {
                    AudioPlayListActivity.isEditAudioAlbum = false;
//                    AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
                    AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                    AudioPlayListAdapter audioPlayListAdapter = new AudioPlayListAdapter(audioPlayListActivity, 17367043, audioPlayListActivity.audioAlbums, i, AudioPlayListActivity.isEditAudioAlbum, AudioPlayListActivity.isGridView);
                    audioPlayListActivity.adapter = audioPlayListAdapter;
                    AudioPlayListActivity.this.gridView.setAdapter(AudioPlayListActivity.this.adapter);
                    AudioPlayListActivity.this.adapter.notifyDataSetChanged();
                } else {
                    AudioPlayListActivity.isEditAudioAlbum = true;
//                    AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Show_Params);
                    AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                    audioPlayListActivity2.position = i;
                    audioPlayListActivity2.AlbumId = Common.FolderId;
                    AudioPlayListActivity audioPlayListActivity3 = AudioPlayListActivity.this;
                    audioPlayListActivity3.albumName = ((AudioPlayListEnt) audioPlayListActivity3.audioAlbums.get(AudioPlayListActivity.this.position)).getPlayListName();
                    AudioPlayListActivity audioPlayListActivity4 = AudioPlayListActivity.this;
                    AudioPlayListAdapter audioPlayListAdapter2 = new AudioPlayListAdapter(audioPlayListActivity4, 17367043, audioPlayListActivity4.audioAlbums, i, AudioPlayListActivity.isEditAudioAlbum, AudioPlayListActivity.isGridView);
                    audioPlayListActivity4.adapter = audioPlayListAdapter2;
                    AudioPlayListActivity.this.gridView.setAdapter(AudioPlayListActivity.this.adapter);
                    AudioPlayListActivity.this.adapter.notifyDataSetChanged();
                }
                if (AudioPlayListActivity.albumPosition != 0) {
                    AudioPlayListActivity.this.gridView.setSelection(AudioPlayListActivity.albumPosition);
                }
                return true;
            }
        });
        this.ll_rename_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (((AudioPlayListEnt) AudioPlayListActivity.this.audioAlbums.get(AudioPlayListActivity.this.position)).getId() != 1) {
                    AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                    audioPlayListActivity.EditAlbumPopup(((AudioPlayListEnt) audioPlayListActivity.audioAlbums.get(AudioPlayListActivity.this.position)).getId(), ((AudioPlayListEnt) AudioPlayListActivity.this.audioAlbums.get(AudioPlayListActivity.this.position)).getPlayListName(), ((AudioPlayListEnt) AudioPlayListActivity.this.audioAlbums.get(AudioPlayListActivity.this.position)).getPlayListLocation());
                    return;
                }
                Toast.makeText(AudioPlayListActivity.this, R.string.lbl_default_album_notrenamed, Toast.LENGTH_SHORT).show();
                AudioPlayListActivity.isEditAudioAlbum = false;
//                AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
                AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                AudioPlayListAdapter audioPlayListAdapter = new AudioPlayListAdapter(audioPlayListActivity2, 17367043, audioPlayListActivity2.audioAlbums, 0, AudioPlayListActivity.isEditAudioAlbum, AudioPlayListActivity.isGridView);
                audioPlayListActivity2.adapter = audioPlayListAdapter;
                AudioPlayListActivity.this.gridView.setAdapter(AudioPlayListActivity.this.adapter);
                AudioPlayListActivity.this.adapter.notifyDataSetChanged();
            }
        });
        this.ll_delete_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (((AudioPlayListEnt) AudioPlayListActivity.this.audioAlbums.get(AudioPlayListActivity.this.position)).getId() != 1) {
                    AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                    audioPlayListActivity.DeleteALertDialog(((AudioPlayListEnt) audioPlayListActivity.audioAlbums.get(AudioPlayListActivity.this.position)).getId(), ((AudioPlayListEnt) AudioPlayListActivity.this.audioAlbums.get(AudioPlayListActivity.this.position)).getPlayListName(), ((AudioPlayListEnt) AudioPlayListActivity.this.audioAlbums.get(AudioPlayListActivity.this.position)).getPlayListLocation());
                    return;
                }
                Toast.makeText(AudioPlayListActivity.this, R.string.lbl_default_album_notdeleted, Toast.LENGTH_SHORT).show();
                AudioPlayListActivity.isEditAudioAlbum = false;
//                AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
                AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                AudioPlayListAdapter audioPlayListAdapter = new AudioPlayListAdapter(audioPlayListActivity2, 17367043, audioPlayListActivity2.audioAlbums, 0, AudioPlayListActivity.isEditAudioAlbum, AudioPlayListActivity.isGridView);
                audioPlayListActivity2.adapter = audioPlayListAdapter;
                AudioPlayListActivity.this.gridView.setAdapter(AudioPlayListActivity.this.adapter);
                AudioPlayListActivity.this.adapter.notifyDataSetChanged();
            }
        });
        int i = albumPosition;
        if (i != 0) {
            this.gridView.setSelection(i);
            albumPosition = 0;
        }
    }

    public void btnOnCloudClick(View view) {
        SecurityLocksCommon.IsAppDeactive = false;
        CloudCommon.ModuleType = DropboxType.Audio.ordinal();
        Utilities.StartCloudActivity(this);
    }

    public void btnOnMoreClick(View view) {
        this.IsMoreDropdown = false;
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
                                AudioPlayListActivity.this._SortBy = SortBy.Name.ordinal();
                                AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                                audioPlayListActivity.GetAlbumsFromDatabase(audioPlayListActivity._SortBy);
                                AudioPlayListActivity.this.appSettingsSharedPreferences.SetAudioViewBy(AudioPlayListActivity.this._SortBy);
                                popupWindow.dismiss();
                                AudioPlayListActivity.this.IsMoreDropdown = false;
                                break;
                            case 1:
                                AudioPlayListActivity.this._SortBy = SortBy.Time.ordinal();
                                AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                                audioPlayListActivity2.GetAlbumsFromDatabase(audioPlayListActivity2._SortBy);
                                AudioPlayListActivity.this.appSettingsSharedPreferences.SetAudioViewBy(AudioPlayListActivity.this._SortBy);
                                popupWindow.dismiss();
                                AudioPlayListActivity.this.IsMoreDropdown = false;
                                break;
                        }
                    }
                } else {
                    switch (i2) {
                        case 0:
                            AudioPlayListActivity.isGridView = false;
                            AudioPlayListActivity.this.ViewBy();
                            popupWindow.dismiss();
                            AudioPlayListActivity.this.IsMoreDropdown = false;
                            break;
                        case 1:
                            AudioPlayListActivity.isGridView = true;
                            AudioPlayListActivity.this.ViewBy();
                            popupWindow.dismiss();
                            AudioPlayListActivity.this.IsMoreDropdown = false;
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
        AudioPlayListAdapter audioPlayListAdapter = new AudioPlayListAdapter(this, 17367043, this.audioAlbums, 0, isEditAudioAlbum, isGridView);
        this.adapter = audioPlayListAdapter;
        this.gridView.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
    }

    public void btnBackonClick() {
        if (isEditAudioAlbum) {
            SecurityLocksCommon.IsAppDeactive = false;
            isEditAudioAlbum = false;
//            this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
            AudioPlayListAdapter audioPlayListAdapter = new AudioPlayListAdapter(this, 17367043, this.audioAlbums, 0, isEditAudioAlbum, isGridView);
            this.adapter = audioPlayListAdapter;
            this.gridView.setAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, MainiFeaturesActivity.class));
        finish();
    }


    public void GetAlbumsFromDatabase(int i) {

        isEditAudioAlbum = false;
        this.audioPlayListDAL = new AudioPlayListDAL(this);
        try {
            this.audioPlayListDAL.OpenRead();
            this.audioAlbums = (ArrayList) this.audioPlayListDAL.GetPlayLists(i);
            AudioPlayListAdapter audioPlayListAdapter = new AudioPlayListAdapter(this, 17367043, this.audioAlbums, 0, isEditAudioAlbum, isGridView);
            this.adapter = audioPlayListAdapter;
            this.gridView.setAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();

        } catch (Exception e) {
            System.out.println(e.getMessage());

        } catch (Throwable th) {
            AudioPlayListDAL audioPlayListDAL3 = this.audioPlayListDAL;
            if (audioPlayListDAL3 != null) {
                audioPlayListDAL3.close();
            }
            throw th;
        }
    }


    public void AddAlbumPopup() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.album_add_edit_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        ((TextView) dialog.findViewById(R.id.lbl_Create_Edit_Album)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_Ok)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_Cancel)).setTypeface(createFromAsset);
        final EditText editText = (EditText) dialog.findViewById(R.id.txt_AlbumName);
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (editText.getEditableText().toString().length() <= 0 || editText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AudioPlayListActivity.this, R.string.lbl_Photos_Album_Create_Album_please_enter, Toast.LENGTH_SHORT).show();
                    return;
                }
                AudioPlayListActivity.this.albumName = editText.getEditableText().toString();
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append("/");
                sb.append(StorageOptionsCommon.AUDIOS);
                sb.append(AudioPlayListActivity.this.albumName);
                File file = new File(sb.toString());
                if (file.exists()) {
                    AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("\"");
                    sb2.append(AudioPlayListActivity.this.albumName);
                    sb2.append("\" already exist");
                    Toast.makeText(audioPlayListActivity, sb2.toString(), Toast.LENGTH_SHORT).show();
                } else if (file.mkdirs()) {
                    AudioPlaylistGalleryMethods audioPlaylistGalleryMethods = new AudioPlaylistGalleryMethods();
                    AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                    audioPlaylistGalleryMethods.AddPlaylistToDatabase(audioPlayListActivity2, audioPlayListActivity2.albumName);
                    Toast.makeText(AudioPlayListActivity.this, R.string.lbl_Photos_Album_Create_Album_Success, Toast.LENGTH_SHORT).show();
                    AudioPlayListActivity audioPlayListActivity3 = AudioPlayListActivity.this;
                    audioPlayListActivity3.GetAlbumsFromDatabase(audioPlayListActivity3._SortBy);
                    dialog.dismiss();
                } else {
                    Toast.makeText(AudioPlayListActivity.this, "ERROR! Some Error in creating album", Toast.LENGTH_SHORT).show();
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
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
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
                    Toast.makeText(AudioPlayListActivity.this, R.string.lbl_Photos_Album_Create_Album_please_enter, Toast.LENGTH_SHORT).show();
                    return;
                }
                AudioPlayListActivity.this.albumName = editText.getEditableText().toString();
                if (new File(str3).exists()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(StorageOptionsCommon.STORAGEPATH);
                    sb.append(StorageOptionsCommon.AUDIOS);
                    sb.append(AudioPlayListActivity.this.albumName);
                    File file = new File(sb.toString());
                    if (file.exists()) {
                        AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("\"");
                        sb2.append(AudioPlayListActivity.this.albumName);
                        sb2.append("\" already exist");
                        Toast.makeText(audioPlayListActivity, sb2.toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(StorageOptionsCommon.STORAGEPATH);
                    sb3.append(StorageOptionsCommon.AUDIOS);
                    sb3.append(str4);
                    File file2 = new File(sb3.toString());
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    if (file2.renameTo(file)) {
                        AudioPlaylistGalleryMethods audioPlaylistGalleryMethods = new AudioPlaylistGalleryMethods();
                        AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                        audioPlaylistGalleryMethods.UpdatePlaylistInDatabase(audioPlayListActivity2, i2, audioPlayListActivity2.albumName);
                        Toast.makeText(AudioPlayListActivity.this, R.string.lbl_Photos_Album_Create_Album_Success_renamed, Toast.LENGTH_SHORT).show();
                        AudioPlayListActivity audioPlayListActivity3 = AudioPlayListActivity.this;
                        audioPlayListActivity3.GetAlbumsFromDatabase(audioPlayListActivity3._SortBy);
                        dialog2.dismiss();
//                        AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
                    }
                }
            }
        };
        linearLayout.setOnClickListener(r0);
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
//                AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
                AudioPlayListActivity.isEditAudioAlbum = false;
                AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                AudioPlayListAdapter audioPlayListAdapter = new AudioPlayListAdapter(audioPlayListActivity, 17367043, audioPlayListActivity.audioAlbums, AudioPlayListActivity.this.position, AudioPlayListActivity.isEditAudioAlbum, AudioPlayListActivity.isGridView);
                audioPlayListActivity.adapter = audioPlayListAdapter;
                AudioPlayListActivity.this.gridView.setAdapter(AudioPlayListActivity.this.adapter);
                AudioPlayListActivity.this.adapter.notifyDataSetChanged();
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
                AudioPlayListActivity.this.DeleteAlbum(i2, str3, str4);
                dialog2.dismiss();
//                AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
            }
        };
        linearLayout.setOnClickListener(r0);
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AudioPlayListActivity.isEditAudioAlbum = false;
//                AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
                AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                AudioPlayListAdapter audioPlayListAdapter = new AudioPlayListAdapter(audioPlayListActivity, 17367043, audioPlayListActivity.audioAlbums, AudioPlayListActivity.this.position, AudioPlayListActivity.isEditAudioAlbum, AudioPlayListActivity.isGridView);
                audioPlayListActivity.adapter = audioPlayListAdapter;
                AudioPlayListActivity.this.gridView.setAdapter(AudioPlayListActivity.this.adapter);
                AudioPlayListActivity.this.adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void DeleteAlbum(int i, String str, String str2) {
        File file = new File(str2);
        DeletePlayListFromDatabase(i);
        try {
            Utilities.DeleteAlbum(file, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void DeletePlayListFromDatabase(int i) {
        AudioPlayListDAL audioPlayListDAL2 = new AudioPlayListDAL(this);
        try {
            audioPlayListDAL2.OpenWrite();
            audioPlayListDAL2.DeletePlayListById(i);
            Toast.makeText(this, R.string.lbl_delete_success, Toast.LENGTH_SHORT).show();
            GetAlbumsFromDatabase(this._SortBy);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            audioPlayListDAL2.close();
            throw th;
        }
        audioPlayListDAL2.close();
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
            if (isEditAudioAlbum) {
                SecurityLocksCommon.IsAppDeactive = false;
                isEditAudioAlbum = false;
//                this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
                AudioPlayListAdapter audioPlayListAdapter = new AudioPlayListAdapter(this, 17367043, this.audioAlbums, 0, isEditAudioAlbum, isGridView);
                this.adapter = audioPlayListAdapter;
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
            CloudCommon.ModuleType = DropboxType.Audio.ordinal();
            Utilities.StartCloudActivity(this);
            return true;
        }

//        else {
//            SecurityLocksCommon.IsAppDeactive = false;
//            InAppPurchaseActivity._cameFrom = CameFrom.AudioFolder.ordinal();
//            startActivity(new Intent(this, InAppPurchaseActivity.class));
//            finish();
//            return true;
//        }
        return false;
    }
}
