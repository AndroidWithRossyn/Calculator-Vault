package com.example.vault.audio;

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
import android.hardware.SensorManager;
import android.net.Uri;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.vault.Flaes;
import com.example.vault.R;
import com.example.vault.adapter.ExpandableListAdapter1;
import com.example.vault.audio.adapter.AudioFileAdapter;
import com.example.vault.audio.model.AudioEnt;
import com.example.vault.audio.model.AudioPlayListEnt;
import com.example.vault.audio.util.AudioDAL;
import com.example.vault.audio.util.AudioPlayListDAL;
import com.example.vault.BaseActivity;
import com.example.vault.common.Constants;
import com.example.vault.photo.adapter.MoveAlbumAdapter;
import com.example.vault.privatebrowser.SecureBrowserActivity;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.AppSettingsSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AudioActivity extends BaseActivity implements OnClickListener {
    public static int _ViewBy;
    public static ProgressDialog myProgressDialog;
    private String GA_Audio = "Audio Play List";
    private boolean IsSelectAll = false;
    boolean IsSortingDropdown = false;
    int _SortBy = 1;
    private String[] _folderNameArray;
    public List<String> _folderNameArrayForMove = null;
    int albumId;
    AppSettingsSharedPreferences appSettingsSharedPreferences;
    private AudioDAL audioDAL;
    List<AudioEnt> audioEntList;
    public AudioFileAdapter audioFileAdapter;
    private AudioPlayListEnt audioFolder;
    private AudioPlayListDAL audioFolderDAL;
    final Context context = this;
    FloatingActionButton fabImpBrowser;
    FloatingActionButton fabImpGallery;
    FloatingActionButton fabImpPcMac;
    FloatingActionsMenu fabMenu;
    private int fileCount = 0;
    ImageView file_empty_icon;
    private ArrayList<String> files = new ArrayList<>();
    FrameLayout fl_bottom_baar;
    protected String folderLocation;
    String folderName;
    @SuppressLint("HandlerLeak")
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 2) {
                AudioActivity.this.hideProgress();
                if (Common.isUnHide) {
                    Common.isUnHide = false;
                    Toast.makeText(AudioActivity.this, R.string.Unhide_error, Toast.LENGTH_SHORT).show();
                } else if (Common.isMove) {
                    Common.isMove = false;
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                }
            } else if (message.what == 4) {
                Toast.makeText(AudioActivity.this, R.string.toast_share, Toast.LENGTH_LONG).show();
            } else if (message.what == 3) {
                if (Common.isUnHide) {
                    Common.isUnHide = false;
                    Toast.makeText(AudioActivity.this, R.string.toast_unhide, Toast.LENGTH_LONG).show();
                    AudioActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent = new Intent(AudioActivity.this, AudioActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        AudioActivity.this.startActivity(intent);
                        AudioActivity.this.finish();
                    }
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(AudioActivity.this, R.string.toast_delete, Toast.LENGTH_SHORT).show();
                    AudioActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent2 = new Intent(AudioActivity.this, AudioActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        AudioActivity.this.startActivity(intent2);
                        AudioActivity.this.finish();
                    }
                } else if (Common.isMove) {
                    Common.isMove = false;
                    Toast.makeText(AudioActivity.this, R.string.toast_move, Toast.LENGTH_SHORT).show();
                    AudioActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent3 = new Intent(AudioActivity.this, AudioActivity.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        AudioActivity.this.startActivity(intent3);
                        AudioActivity.this.finish();
                    }
                }
            }
            super.handleMessage(message);
        }
    };
    GridView imagegrid;
    boolean isAudioShared = false;
    boolean isEditMode = false;
    TextView lbl_file_empty;
    LinearLayout ll_EditAlbum;
    //    LayoutParams ll_EditAlbum_Hide_Params;
//    LayoutParams ll_EditAlbum_Show_Params;
//    LayoutParams ll_Hide_Params;
//    LayoutParams ll_Show_Params;
    LinearLayout ll_anchor;
    LinearLayout ll_background;
    LinearLayout ll_delete_btn;
    LinearLayout ll_file_empty;
    LinearLayout ll_file_grid;
    LinearLayout ll_move_btn;
    LinearLayout ll_share_btn;
    LinearLayout ll_unhide_btn;


    public String moveToFolderLocation;
    int selectCount;
    String selectedCount;
    private SensorManager sensorManager;
    private Toolbar toolbar;

    public enum SortBy {
        Time, Name, Size
    }

    public enum ViewBy {
        List, Detail
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_delete_btn) {
            DeleteFiles();
        } else if (id == R.id.ll_move_btn) {
            MoveFiles();
        } else if (id != R.id.ll_share_btn) {
            if (id == R.id.ll_unhide_btn) {
                UnhideFiles();
            }
        } else if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectaudiomsg_share, Toast.LENGTH_SHORT).show();
        } else {
            ShareAudio();
        }
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


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_audio);


        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);

//        this.ll_Show_Params = new LayoutParams(-1, -2);
//        this.ll_Hide_Params = new LayoutParams(-2, 0);

        this.fl_bottom_baar = (FrameLayout) findViewById(R.id.fl_bottom_baar);
//        this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_EditAlbum = (LinearLayout) findViewById(R.id.ll_EditAlbum);
        this.ll_unhide_btn = (LinearLayout) findViewById(R.id.ll_unhide_btn);
        this.ll_delete_btn = (LinearLayout) findViewById(R.id.ll_delete_btn);
        this.ll_move_btn = (LinearLayout) findViewById(R.id.ll_move_btn);
        this.ll_share_btn = (LinearLayout) findViewById(R.id.ll_share_btn);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.imagegrid = (GridView) findViewById(R.id.customGalleryGrid);
        this.fabMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu);
        this.fabImpGallery = (FloatingActionButton) findViewById(R.id.btn_impGallery);
        this.fabImpBrowser = (FloatingActionButton) findViewById(R.id.btn_impBrowser);
        this.fabImpPcMac = (FloatingActionButton) findViewById(R.id.btn_impPcMac);
        this.ll_file_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.ll_file_grid = (LinearLayout) findViewById(R.id.ll_photo_video_grid);
        this.file_empty_icon = (ImageView) findViewById(R.id.photo_video_empty_icon);
        this.lbl_file_empty = (TextView) findViewById(R.id.lbl_photo_video_empty);
        this.ll_file_grid.setVisibility(View.VISIBLE);
        this.ll_file_empty.setVisibility(View.INVISIBLE);

        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this);
        audioPlayListDAL.OpenRead();
        AudioPlayListEnt GetPlayListById = audioPlayListDAL.GetPlayListById(Common.FolderId);
        this._SortBy = audioPlayListDAL.GetSortByPlaylistId(Common.FolderId);
        audioPlayListDAL.close();
        this.folderName = GetPlayListById.getPlayListName();
        TextView title10 = findViewById(R.id.title10);
        title10.setText(this.folderName);
//        getSupportActionBar().setTitle((CharSequence) this.folderName);
        // getSupportActionBar().setTitle("");
        this.folderLocation = GetPlayListById.getPlayListLocation();
        this.appSettingsSharedPreferences = AppSettingsSharedPreferences.GetObject(this);
        _ViewBy = this.appSettingsSharedPreferences.GetAudioViewBy();
        this.ll_delete_btn.setOnClickListener(this);
        this.ll_unhide_btn.setOnClickListener(this);
        this.ll_move_btn.setOnClickListener(this);
        this.ll_share_btn.setOnClickListener(this);
        this.ll_background.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (AudioActivity.this.IsSortingDropdown) {
                    AudioActivity.this.IsSortingDropdown = false;
                }
                if (AudioActivity.this.IsSortingDropdown) {
                    AudioActivity.this.IsSortingDropdown = false;
                }
                return false;
            }
        });
        this.fabImpGallery.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                AudioActivity.this.startActivity(new Intent(AudioActivity.this, AudiosImportActivity.class));
                AudioActivity.this.finish();
            }
        });
        this.fabImpBrowser.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                AudioActivity audioActivity = AudioActivity.this;
                Common.CurrentWebBrowserActivity = audioActivity;
                AudioActivity.this.startActivity(new Intent(audioActivity, SecureBrowserActivity.class));
                AudioActivity.this.finish();
            }
        });
        this.fabImpPcMac.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (Common.IsAirplaneModeOn(AudioActivity.this.context) || !Common.IsWiFiModeOn(AudioActivity.this.context) || !Common.IsWiFiConnect(AudioActivity.this.context)) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    AudioActivity audioActivity = AudioActivity.this;
                    Common.CurrentWebServerActivity = audioActivity;
                    audioActivity.finish();
                    return;
                }
                SecurityLocksCommon.IsAppDeactive = false;
                AudioActivity audioActivity2 = AudioActivity.this;
                Common.CurrentWebServerActivity = audioActivity2;
                audioActivity2.finish();
            }
        });
        this.imagegrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (!AudioActivity.this.isEditMode) {
                    int id = ((AudioEnt) AudioActivity.this.audioEntList.get(i)).getId();
                    AudioDAL audioDAL = new AudioDAL(AudioActivity.this);
                    audioDAL.OpenRead();
                    String folderLockAudioLocation = audioDAL.GetAudio(Integer.toString(id)).getFolderLockAudioLocation();
                    audioDAL.close();
                    String FileName = Utilities.FileName(folderLockAudioLocation);
                    if (FileName.contains("#")) {
                        FileName = Utilities.ChangeFileExtentionToOrignal(FileName);
                    }
                    File file = new File(folderLockAudioLocation);
                    StringBuilder sb = new StringBuilder();
                    sb.append(file.getParent());
                    sb.append("/");
                    sb.append(FileName);
                    File file2 = new File(sb.toString());
                    file.renameTo(file2);
                    AudioActivity.this.CopyTempFile(file2.getAbsolutePath());
                }
            }
        });
        this.imagegrid.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                Common.PhotoThumbnailCurrentPosition = AudioActivity.this.imagegrid.getFirstVisiblePosition();
                AudioActivity audioActivity = AudioActivity.this;
                audioActivity.isEditMode = true;
                ((AudioEnt) audioActivity.audioEntList.get(i)).SetFileCheck(true);
//                AudioActivity.this.fl_bottom_baar.setLayoutParams(AudioActivity.this.ll_Show_Params);
                AudioActivity.this.ll_EditAlbum.setVisibility(View.VISIBLE);
                AudioActivity.this.invalidateOptionsMenu();
                AudioActivity audioActivity2 = AudioActivity.this;
                AudioFileAdapter audioFileAdapter = new AudioFileAdapter(audioActivity2, audioActivity2, 1, audioActivity2.audioEntList, Boolean.valueOf(true), AudioActivity._ViewBy);
                audioActivity2.audioFileAdapter = audioFileAdapter;
                AudioActivity.this.imagegrid.setAdapter(AudioActivity.this.audioFileAdapter);
                AudioActivity.this.audioFileAdapter.notifyDataSetChanged();
                if (Common.PhotoThumbnailCurrentPosition != 0) {
                    AudioActivity.this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
                }
                return true;
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
            LoadFilesFromDB(this._SortBy);
        }
        if (Common.PhotoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
            Common.PhotoThumbnailCurrentPosition = 0;
        }
        this.imagegrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (new File(((AudioEnt) AudioActivity.this.audioEntList.get(i)).getFolderLockAudioLocation()).exists()) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    Common.CurrentTrackId = ((AudioEnt) AudioActivity.this.audioEntList.get(i)).getId();
                    Intent intent = new Intent(AudioActivity.this, AudioPlayerActivity.class);
                    Common.CurrentTrackNextIndex = i;
                    AudioActivity.this.startActivity(intent);
                    AudioActivity.this.finish();
                    return;
                }
                SecurityLocksCommon.IsAppDeactive = false;
                AudioActivity audioActivity = AudioActivity.this;
                audioActivity.startActivity(audioActivity.getIntent());
                AudioActivity.this.finish();
            }
        });
    }

    private void SetcheckFlase() {
        for (int i = 0; i < this.audioEntList.size(); i++) {
            ((AudioEnt) this.audioEntList.get(i)).SetFileCheck(false);
        }
        AudioFileAdapter audioFileAdapter2 = new AudioFileAdapter(this, this, 1, this.audioEntList, Boolean.valueOf(false), _ViewBy);
        this.audioFileAdapter = audioFileAdapter2;
        this.imagegrid.setAdapter(this.audioFileAdapter);
        this.audioFileAdapter.notifyDataSetChanged();
        if (Common.PhotoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
            Common.PhotoThumbnailCurrentPosition = 0;
        }
    }

    public void Back() {
        Common.SelectedCount = 0;
        if (this.isEditMode) {
            SetcheckFlase();
            this.isEditMode = false;
            this.IsSortingDropdown = false;
            this.IsSelectAll = false;
            Common.IsSelectAll = false;
//            this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
            this.ll_EditAlbum.setVisibility(View.INVISIBLE);
            invalidateOptionsMenu();
        } else if (this.fabMenu.isExpanded()) {
            this.fabMenu.collapse();
            this.IsSortingDropdown = false;
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            Common.FolderId = 0;
            startActivity(new Intent(this, AudioPlayListActivity.class));
            finish();
        }
    }

    public void UnhideFiles() {
        if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectphotomsg_unhide, Toast.LENGTH_SHORT).show();
            return;
        }
        SelectedCount();
        if (Common.GetTotalFree() > Common.GetFileSize(this.files)) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.confirmation_message_box);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCancelable(true);
            LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
            TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
            textView.setTypeface(Typeface.createFromAsset(getAssets(), "ebrima.ttf"));
            StringBuilder sb = new StringBuilder();
            sb.append("Are you sure you want to restore (");
            sb.append(this.selectCount);
            sb.append(") audio(s)?");
            textView.setText(sb.toString());
            ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AudioActivity.this.showUnhideProgress();
                    new Thread() {
                        public void run() {
                            try {
                                dialog.dismiss();
                                Common.isUnHide = true;
                                AudioActivity.this.Unhide();
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                AudioActivity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 3;
                                AudioActivity.this.handle.sendMessage(message2);
                            }
                        }
                    }.start();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }


    public void Unhide() throws IOException {
        AudioDAL audioDAL2 = new AudioDAL(this);
        audioDAL2.OpenWrite();
        for (AudioEnt audioEnt : this.audioEntList) {
            if (audioEnt.GetFileCheck()) {
                File file = new File(audioEnt.getFolderLockAudioLocation());
                StringBuilder sb = new StringBuilder();
                sb.append(Environment.getExternalStorageDirectory().getPath());
                sb.append(Common.UnhideKitkatAlbumName);
                sb.append(audioEnt.getAudioName());
                File file2 = new File(sb.toString());
                File file3 = new File(file2.getParent());
                if (!file3.exists() && !file3.mkdirs() && file2.exists()) {
                    file2 = Utilities.GetDesFileNameForUnHide(file2.getAbsolutePath(), file2.getName(), file2);
                }
                if (file.exists()) {
                    file2.createNewFile();
                    Flaes.decryptUsingCipherStream_AES128(file, file2);
                    if (file.exists() && file2.exists()) {
                        file.delete();
                        audioDAL2.DeleteAudio(audioEnt);
                    }
                }
            }
        }
        audioDAL2.close();
    }

    public void DeleteFiles() {
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
        sb.append(") audio(s)?");
        textView.setText(sb.toString());
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AudioActivity.this.showDeleteProgress();
                new Thread() {
                    public void run() {
                        try {
                            Common.isDelete = true;
                            dialog.dismiss();
                            AudioActivity.this.Delete();
                            Common.IsWorkInProgress = true;
                            Message message = new Message();
                            message.what = 3;
                            AudioActivity.this.handle.sendMessage(message);
                            Common.IsWorkInProgress = false;
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 3;
                            AudioActivity.this.handle.sendMessage(message2);
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
        for (int i = 0; i < this.audioEntList.size(); i++) {
            if (((AudioEnt) this.audioEntList.get(i)).GetFileCheck()) {
                new File(((AudioEnt) this.audioEntList.get(i)).getFolderLockAudioLocation()).delete();
                DeleteFromDatabase(((AudioEnt) this.audioEntList.get(i)).getId());
            }
        }
    }

    public void DeleteFromDatabase(int i) {
        AudioDAL audioDAL2;
        this.audioDAL = new AudioDAL(this);
        try {
            this.audioDAL.OpenWrite();
            this.audioDAL.DeleteAudioById(i);
            audioDAL2 = this.audioDAL;
            if (audioDAL2 == null) {
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            audioDAL2 = this.audioDAL;
            if (audioDAL2 == null) {
                return;
            }
        } catch (Throwable th) {
            AudioDAL audioDAL3 = this.audioDAL;
            if (audioDAL3 != null) {
                audioDAL3.close();
            }
            throw th;
        }
        audioDAL2.close();
    }


    public void SelectedCount() {
        this.files.clear();
        this.selectCount = 0;
        for (int i = 0; i < this.audioEntList.size(); i++) {
            if (((AudioEnt) this.audioEntList.get(i)).GetFileCheck()) {
                this.files.add(((AudioEnt) this.audioEntList.get(i)).getFolderLockAudioLocation());
                this.selectCount++;
            }
        }
    }

    private boolean IsFileCheck() {
        for (int i = 0; i < this.audioEntList.size(); i++) {
            if (((AudioEnt) this.audioEntList.get(i)).GetFileCheck()) {
                return true;
            }
        }
        return false;
    }

    public void MoveFiles() {
        this.audioDAL = new AudioDAL(this);
        this.audioDAL.OpenWrite();
        this._folderNameArray = this.audioDAL.GetPlayListNames(Common.FolderId);
        if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectdocumentmsg_move, Toast.LENGTH_SHORT).show();
        } else if (this._folderNameArray.length > 0) {
            GetFolderNameFromDB();
        } else {
            Toast.makeText(this, R.string.toast_OneFolder, Toast.LENGTH_SHORT).show();
        }
    }


    public void Move(String str, String str2, String str3) {
        String str4;
        AudioPlayListEnt GetAlbum = GetAlbum(str3);
        for (int i = 0; i < this.audioEntList.size(); i++) {
            if (((AudioEnt) this.audioEntList.get(i)).GetFileCheck()) {
                if (((AudioEnt) this.audioEntList.get(i)).getAudioName().contains("#")) {
                    str4 = ((AudioEnt) this.audioEntList.get(i)).getAudioName();
                } else {
                    str4 = Utilities.ChangeFileExtention(((AudioEnt) this.audioEntList.get(i)).getAudioName());
                }
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append("/");
                sb.append(str4);
                String sb2 = sb.toString();
                try {
                    if (Utilities.MoveFileWithinDirectories(((AudioEnt) this.audioEntList.get(i)).getFolderLockAudioLocation(), sb2)) {
                        UpdateFileLocationInDatabase((AudioEnt) this.audioEntList.get(i), sb2, GetAlbum.getId());
                        Common.FolderId = GetAlbum.getId();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void UpdateFileLocationInDatabase(AudioEnt audioEnt, String str, int i) {
        AudioDAL audioDAL2;
        audioEnt.setFolderLockAudioLocation(str);
        audioEnt.setPlayListId(i);
        try {
            AudioDAL audioDAL3 = new AudioDAL(this);
            audioDAL3.OpenWrite();
            audioDAL3.UpdateAudiosLocation(audioEnt);
            audioDAL2 = this.audioDAL;
            if (audioDAL2 == null) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            audioDAL2 = this.audioDAL;
            if (audioDAL2 == null) {
                return;
            }
        } catch (Throwable th) {
            AudioDAL audioDAL4 = this.audioDAL;
            if (audioDAL4 != null) {
                audioDAL4.close();
            }
            throw th;
        }
        audioDAL2.close();
    }


    public AudioPlayListEnt GetAlbum(String str) {

        this.audioFolderDAL = new AudioPlayListDAL(this);
        try {
            this.audioFolderDAL.OpenRead();
            this.audioFolder = this.audioFolderDAL.GetPlayList(str);


            return audioFolder;
        } catch (Exception e) {
            System.out.println(e.getMessage());

        } catch (Throwable th) {
            AudioPlayListDAL audioPlayListDAL2 = this.audioFolderDAL;
            if (audioPlayListDAL2 != null) {
                audioPlayListDAL2.close();
            }
            throw th;
        }
        return null;
    }

    private void GetFolderNameFromDB() {
        AudioDAL audioDAL2;
        this.audioDAL = new AudioDAL(this);
        try {
            this.audioDAL.OpenWrite();
            this._folderNameArrayForMove = this.audioDAL.GetMovePlayListNames(Common.FolderId);
            MoveDocumentDialog();
            audioDAL2 = this.audioDAL;
            if (audioDAL2 == null) {
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            audioDAL2 = this.audioDAL;
            if (audioDAL2 == null) {
                return;
            }
        } catch (Throwable th) {
            AudioDAL audioDAL3 = this.audioDAL;
            if (audioDAL3 != null) {
                audioDAL3.close();
            }
            throw th;
        }
        audioDAL2.close();
    }


    public void MoveDocumentDialog() {
        final com.rey.material.app.Dialog dialog = new com.rey.material.app.Dialog(this);
        dialog.setContentView((int) R.layout.move_customlistview);
        dialog.setTitle((int) R.string.lbl_Moveto);
        ListView listView = (ListView) dialog.findViewById(R.id.ListViewfolderslist);
        listView.setAdapter(new MoveAlbumAdapter(this, 17367043, this._folderNameArrayForMove, R.drawable.audio_list_icon));
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long j) {
                if (AudioActivity.this._folderNameArrayForMove != null) {
                    AudioActivity.this.SelectedCount();
                    AudioActivity.this.showMoveProgress();
                    new Thread() {
                        public void run() {
                            try {
                                Common.isMove = true;
                                dialog.dismiss();
                                AudioActivity audioActivity = AudioActivity.this;
                                StringBuilder sb = new StringBuilder();
                                sb.append(StorageOptionsCommon.STORAGEPATH);
                                sb.append(StorageOptionsCommon.AUDIOS);
                                sb.append((String) AudioActivity.this._folderNameArrayForMove.get(i));
                                audioActivity.moveToFolderLocation = sb.toString();
                                AudioActivity.this.Move(AudioActivity.this.folderLocation, AudioActivity.this.moveToFolderLocation, (String) AudioActivity.this._folderNameArrayForMove.get(i));
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                AudioActivity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 3;
                                AudioActivity.this.handle.sendMessage(message2);
                            }
                        }
                    }.start();
                }
            }
        });
        dialog.show();
    }

    public void ShareAudio() {
        showCopyFilesProcessForShareProgress();
        new Thread() {
            public void run() {
                try {
                    SecurityLocksCommon.IsAppDeactive = false;
                    ArrayList arrayList = new ArrayList();
                    Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
                    intent.setType("image/*");
                    for (ResolveInfo resolveInfo : AudioActivity.this.getPackageManager().queryIntentActivities(intent, 0)) {
                        String str = resolveInfo.activityInfo.packageName;
                        if (!str.equals(AppPackageName) && !str.equals("com.dropbox.android") && !str.equals("com.facebook.katana")) {
                            Intent intent2 = new Intent("android.intent.action.SEND_MULTIPLE");
                            intent2.setType("image/*");
                            intent2.setPackage(str);
                            arrayList.add(intent2);
                            StringBuilder sb = new StringBuilder();
                            sb.append(StorageOptionsCommon.STORAGEPATH);
                            sb.append(StorageOptionsCommon.AUDIOS);
                            String sb2 = sb.toString();
                            ArrayList arrayList2 = new ArrayList();
                            ArrayList arrayList3 = new ArrayList();
                            String str2 = sb2;
                            for (int i = 0; i < AudioActivity.this.audioEntList.size(); i++) {
                                if (((AudioEnt) AudioActivity.this.audioEntList.get(i)).GetFileCheck()) {
                                    try {
                                        str2 = Utilities.CopyTemporaryFile(AudioActivity.this, ((AudioEnt) AudioActivity.this.audioEntList.get(i)).getFolderLockAudioLocation(), str2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    arrayList2.add(str2);
                                    arrayList3.add(FileProvider.getUriForFile(AudioActivity.this, AppPackageName, new File(str2)));
                                }
                            }
                            intent2.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList3);
                        }
                    }
                    Intent createChooser = Intent.createChooser((Intent) arrayList.remove(0), "Share Via");
                    createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) arrayList.toArray(new Parcelable[0]));
                    createChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    createChooser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    AudioActivity.this.startActivity(createChooser);
                    AudioActivity.this.isAudioShared = true;
                    Message message = new Message();
                    message.what = 4;
                    AudioActivity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 4;
                    AudioActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }


    public void LoadFilesFromDB(int i) {
        this.audioEntList = new ArrayList();
        AudioDAL audioDAL2 = new AudioDAL(this);
        audioDAL2.OpenRead();
        this.fileCount = audioDAL2.GetAudiosCountByFolderId(Common.FolderId);
        this.audioEntList = audioDAL2.GetAudiosByAlbumId(Common.FolderId, i);

        Log.e("loadfiledb", "" + audioEntList);
        Common.sortType = i;
        audioDAL2.close();
        AudioFileAdapter audioFileAdapter2 = new AudioFileAdapter(this, this, 1, this.audioEntList, Boolean.valueOf(false), _ViewBy);
        this.audioFileAdapter = audioFileAdapter2;
        this.imagegrid.setAdapter(this.audioFileAdapter);
        this.audioFileAdapter.notifyDataSetChanged();
        if (this.audioEntList.size() < 1) {
            this.ll_file_grid.setVisibility(View.INVISIBLE);
            this.ll_file_empty.setVisibility(View.VISIBLE);
            this.file_empty_icon.setBackgroundResource(R.drawable.ic_audio_empty_icon);
            this.lbl_file_empty.setText(R.string.lbl_No_audio);
            return;
        }
        this.ll_file_grid.setVisibility(View.VISIBLE);
        this.ll_file_empty.setVisibility(View.INVISIBLE);
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
        arrayList2.add("Detail");
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
                                AudioActivity.this._SortBy = SortBy.Name.ordinal();
                                AudioActivity audioActivity = AudioActivity.this;
                                audioActivity.LoadFilesFromDB(audioActivity._SortBy);
                                AudioActivity.this.AddSortInDB();
                                popupWindow.dismiss();
                                AudioActivity.this.IsSortingDropdown = false;
                                break;
                            case 1:
                                AudioActivity.this._SortBy = SortBy.Time.ordinal();
                                AudioActivity audioActivity2 = AudioActivity.this;
                                audioActivity2.LoadFilesFromDB(audioActivity2._SortBy);
                                AudioActivity.this.AddSortInDB();
                                popupWindow.dismiss();
                                AudioActivity.this.IsSortingDropdown = false;
                                break;
                            case 2:
                                AudioActivity.this._SortBy = SortBy.Size.ordinal();
                                AudioActivity audioActivity3 = AudioActivity.this;
                                audioActivity3.LoadFilesFromDB(audioActivity3._SortBy);
                                AudioActivity.this.AddSortInDB();
                                popupWindow.dismiss();
                                AudioActivity.this.IsSortingDropdown = false;
                                break;
                        }
                    }
                } else {
                    switch (i2) {
                        case 0:
                            AudioActivity._ViewBy = ViewBy.List.ordinal();
                            AudioActivity.this.ViewBy();
                            popupWindow.dismiss();
                            AudioActivity audioActivity4 = AudioActivity.this;
                            audioActivity4.IsSortingDropdown = false;
                            audioActivity4.appSettingsSharedPreferences.SetAudioViewBy(AudioActivity._ViewBy);
                            break;
                        case 1:
                            AudioActivity._ViewBy = ViewBy.Detail.ordinal();
                            AudioActivity.this.ViewBy();
                            popupWindow.dismiss();
                            AudioActivity audioActivity5 = AudioActivity.this;
                            audioActivity5.IsSortingDropdown = false;
                            audioActivity5.appSettingsSharedPreferences.SetAudioViewBy(AudioActivity._ViewBy);
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
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this);
        audioPlayListDAL.OpenWrite();
        audioPlayListDAL.AddSortByInAudioPlaylist(this._SortBy);
        audioPlayListDAL.close();
    }


    public void CopyTempFile(String str) {
        File file = new File(str);
        try {
            String guessContentTypeFromName = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
            Intent intent = new Intent("android.intent.action.VIEW");
            StringBuilder sb = new StringBuilder();
            sb.append(Constants.FILE);
            sb.append(file.getAbsolutePath());
            intent.setDataAndType(Uri.parse(sb.toString()), guessContentTypeFromName);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            Back();
            return true;
        } else if (itemId == R.id.action_delete) { /*2131296286*/
            DeleteFiles();
            return true;
        } else if (itemId == R.id.action_more) { /*2131296298*/
            this.IsSortingDropdown = false;
            showPopupWindow();
            return true;
        } else if (itemId == R.id.action_move) { /*2131296299*/
            MoveFiles();
            return true;
        } else if (itemId == R.id.action_select) { /*2131296302*/
            if (this.IsSelectAll) {
                for (int i = 0; i < this.audioEntList.size(); i++) {
                    ((AudioEnt) this.audioEntList.get(i)).SetFileCheck(false);
                }
                this.IsSelectAll = false;
                menuItem.setIcon(R.drawable.ic_unselectallicon);
                Common.IsSelectAll = false;
                SelectedItemsCount(0);
                Common.SelectedCount = 0;
                invalidateOptionsMenu();
            } else {
                for (int i2 = 0; i2 < this.audioEntList.size(); i2++) {
                    ((AudioEnt) this.audioEntList.get(i2)).SetFileCheck(true);
                }
                Common.SelectedCount = this.audioEntList.size();
                this.IsSelectAll = true;
                menuItem.setIcon(R.drawable.ic_selectallicon);
                Common.IsSelectAll = true;
            }
            AudioFileAdapter audioFileAdapter2 = new AudioFileAdapter(this, this, 1, this.audioEntList, Boolean.valueOf(true), _ViewBy);
            this.audioFileAdapter = audioFileAdapter2;
            this.imagegrid.setAdapter(this.audioFileAdapter);
            this.audioFileAdapter.notifyDataSetChanged();
            return true;
        } else if (itemId == R.id.action_share) { /*2131296304*/
            if (!IsFileCheck()) {
                Toast.makeText(this, R.string.toast_unselectaudiomsg_share, Toast.LENGTH_SHORT).show();
            } else {
                ShareAudio();
            }
            return true;
        } else if (itemId == R.id.action_unlock) { /*2131296306*/
            UnhideFiles();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
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

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Common.SelectedCount = 0;
    }

    public void ViewBy() {
        AudioFileAdapter audioFileAdapter2 = new AudioFileAdapter(this, this, 1, this.audioEntList, Boolean.valueOf(false), _ViewBy);
        this.audioFileAdapter = audioFileAdapter2;
        this.imagegrid.setAdapter(this.audioFileAdapter);
        this.audioFileAdapter.notifyDataSetChanged();
    }


    public void onPause() {
        super.onPause();
        Common.IsWorkInProgress = true;
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
        this.handle.removeCallbacksAndMessages(null);
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
        }
    }


    public void onResume() {
        super.onResume();
        SetcheckFlase();
        this.IsSortingDropdown = false;
        this.isEditMode = false;
        this.IsSelectAll = false;
//        this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_EditAlbum.setVisibility(View.INVISIBLE);
        invalidateOptionsMenu();
    }


    public void onStop() {
        super.onStop();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        Common.SelectedCount = 0;
        if (i == 4) {
            if (this.isEditMode) {
                SetcheckFlase();
                this.IsSortingDropdown = false;
                this.isEditMode = false;
                this.IsSelectAll = false;
                Common.IsSelectAll = false;
//                this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
                this.ll_EditAlbum.setVisibility(View.INVISIBLE);
                invalidateOptionsMenu();
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            Common.FolderId = 0;
            startActivity(new Intent(this, AudioPlayListActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void SelectedItemsCount(int i) {
        this.selectedCount = Integer.toString(i);
    }
}
