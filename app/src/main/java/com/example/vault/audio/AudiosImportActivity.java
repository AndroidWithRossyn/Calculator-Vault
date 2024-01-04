package com.example.vault.audio;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import com.example.vault.Flaes;
import com.example.vault.R;
import com.example.vault.audio.adapter.AudioFoldersImportAdapter;
import com.example.vault.audio.adapter.AudioSystemFileAdapter;
import com.example.vault.audio.model.AudioEnt;
import com.example.vault.audio.model.AudioPlayListEnt;
import com.example.vault.audio.model.ImportAlbumEnt;
import com.example.vault.audio.util.AudioDAL;
import com.example.vault.audio.util.AudioPlayListDAL;
import com.example.vault.BaseActivity;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Common.ActivityType;
import com.example.vault.utilities.FileUtils;
import com.example.vault.utilities.Utilities;

public class AudiosImportActivity extends BaseActivity {
    private static String AUDIO_LOCALTION = "/audio/";
    private boolean IsExceptionInImportPhotos = false;
    private boolean IsSelectAll = false;
    public ProgressBar Progress;
    ListView album_import_ListView;
    List<List<AudioEnt>> audioEntListShowList = new ArrayList();

    public ArrayList<AudioEnt> audioImportEntList = new ArrayList<>();

    public ArrayList<AudioEnt> audioImportEntListShow = new ArrayList<>();
    AppCompatImageView btnSelectAll;
    Context context = this;
    ImageView document_empty_icon;

    public AudioSystemFileAdapter filesAdapter;
    int folderId;
    String folderName = "";
    String folderPath = "";

    public AudioFoldersImportAdapter folderadapter;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            Intent intent;
            if (message.what == 1) {
                AudiosImportActivity.this.hideProgress();
                AudiosImportActivity audiosImportActivity = AudiosImportActivity.this;
                audiosImportActivity.filesAdapter = new AudioSystemFileAdapter(audiosImportActivity, 1, audiosImportActivity.audioImportEntListShow);
                AudiosImportActivity.this.imagegrid.setAdapter(AudiosImportActivity.this.filesAdapter);
                AudiosImportActivity.this.filesAdapter.notifyDataSetChanged();
                if (AudiosImportActivity.this.audioImportEntListShow.size() <= 0) {
                    AudiosImportActivity.this.album_import_ListView.setVisibility(View.INVISIBLE);
                    AudiosImportActivity.this.imagegrid.setVisibility(View.INVISIBLE);
                    AudiosImportActivity.this.btnSelectAll.setVisibility(View.INVISIBLE);
                    AudiosImportActivity.this.ll_photo_video_empty.setVisibility(View.VISIBLE);
                }
            } else if (message.what == 3) {
                if (Common.IsImporting) {
                    Common.IsImporting = false;
                    AudiosImportActivity audiosImportActivity2 = AudiosImportActivity.this;
                    StringBuilder sb = new StringBuilder();
                    sb.append(AudiosImportActivity.this.selectCount);
                    sb.append(" File(s) imported successfully");
                    Toast.makeText(audiosImportActivity2, sb.toString(), Toast.LENGTH_SHORT).show();
                    AudiosImportActivity.this.hideProgress();
                    SecurityLocksCommon.IsAppDeactive = false;
                    if (Common.isFolderImport) {
                        Common.isFolderImport = false;
                        intent = new Intent(AudiosImportActivity.this, AudioActivity.class);
                    } else {
                        intent = new Intent(AudiosImportActivity.this, AudioActivity.class);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    AudiosImportActivity.this.startActivity(intent);
                    AudiosImportActivity.this.finish();
                }
            } else if (message.what == 2) {
                AudiosImportActivity.this.hideProgress();
            }
            super.handleMessage(message);
        }
    };
    GridView imagegrid;
    List<ImportAlbumEnt> importAlbumEnts = new ArrayList();
    boolean isAlbumClick = false;
    TextView lbl_import_photo_album_topbaar;
    TextView lbl_photo_video_empty;
    LinearLayout ll_Import_bottom_baar;
    LinearLayout ll_photo_video_empty;
    ProgressDialog myProgressDialog = null;
    int selectCount;
    private ArrayList<String> selectPath = new ArrayList<>();
    String selectedCount;
    private SensorManager sensorManager;
    ArrayList<String> spinnerValues = new ArrayList<>();
    private Toolbar toolbar;

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void ShowImportProgress() {
        this.myProgressDialog = ProgressDialog.show(this, null, "Your data is being copied... this may take a few moments... ", true);
    }


    public void hideProgress() {
        ProgressDialog progressDialog = this.myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            this.myProgressDialog.dismiss();
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        Back();
        invalidateOptionsMenu();
        return true;
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.import_album_list_activity);

        SecurityLocksCommon.IsAppDeactive = true;
        Common.IsWorkInProgress = false;

        this.btnSelectAll = (AppCompatImageView) findViewById(R.id.btnSelectAll);
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.lbl_import_photo_album_topbaar = (TextView) findViewById(R.id.lbl_import_photo_album_topbaar);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        this.lbl_import_photo_album_topbaar.setText("Select Audio");
        this.ll_Import_bottom_baar = (LinearLayout) findViewById(R.id.ll_Import_bottom_baar);
        StorageOptionsCommon.STORAGEPATH = StorageOptionSharedPreferences.GetObject(this).GetStoragePath();
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        this.album_import_ListView = (ListView) findViewById(R.id.album_import_ListView);
        this.imagegrid = (GridView) findViewById(R.id.customGalleryGrid);
        this.ll_photo_video_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.document_empty_icon = (ImageView) findViewById(R.id.photo_video_empty_icon);
        this.lbl_photo_video_empty = (TextView) findViewById(R.id.lbl_photo_video_empty);
        this.folderId = Common.FolderId;
        this.folderName = null;
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenWrite();
        AudioPlayListEnt GetPlayListById = audioPlayListDAL.GetPlayListById(Common.FolderId);
        this.folderId = GetPlayListById.getId();
        this.folderName = GetPlayListById.getPlayListName();
        this.folderPath = GetPlayListById.getPlayListLocation();
        this.btnSelectAll.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AudiosImportActivity.this.SelectAllAudios();
                AudiosImportActivity audiosImportActivity = AudiosImportActivity.this;
                audiosImportActivity.filesAdapter = new AudioSystemFileAdapter(audiosImportActivity, 1, audiosImportActivity.audioImportEntListShow);
                AudiosImportActivity.this.imagegrid.setAdapter(AudiosImportActivity.this.filesAdapter);
                AudiosImportActivity.this.filesAdapter.notifyDataSetChanged();
            }
        });
        this.ll_Import_bottom_baar.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AudiosImportActivity.this.OnImportClick();
            }
        });

        AudioFileBind();

        GetFolders();

        Iterator it = this.audioImportEntList.iterator();

        while (it.hasNext()) {
            AudioEnt audioEnt = (AudioEnt) it.next();
            if (((String) this.spinnerValues.get(0)).contains(new File(audioEnt.getOriginalAudioLocation()).getParent())) {
                this.audioImportEntListShow.add(audioEnt);

                Log.e("audioImportEntListShow",""+audioImportEntListShow);
            }
        }
        this.album_import_ListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                AudiosImportActivity audiosImportActivity = AudiosImportActivity.this;
                audiosImportActivity.isAlbumClick = true;
                audiosImportActivity.album_import_ListView.setVisibility(View.INVISIBLE);
                AudiosImportActivity.this.imagegrid.setVisibility(View.VISIBLE);
                AudiosImportActivity.this.btnSelectAll.setVisibility(View.VISIBLE);
                AudiosImportActivity audiosImportActivity2 = AudiosImportActivity.this;
                audiosImportActivity2.folderadapter = new AudioFoldersImportAdapter(audiosImportActivity2.context, 17367043, AudiosImportActivity.this.importAlbumEnts, false);
                AudiosImportActivity.this.album_import_ListView.setAdapter(AudiosImportActivity.this.folderadapter);
                AudiosImportActivity.this.audioImportEntListShow.clear();
                Iterator it = AudiosImportActivity.this.audioImportEntList.iterator();
                while (it.hasNext()) {
                    AudioEnt audioEnt = (AudioEnt) it.next();
                    if (((String) AudiosImportActivity.this.spinnerValues.get(i)).equals(new File(audioEnt.getOriginalAudioLocation()).getParent())) {
                        audioEnt.GetFileCheck();
                        AudiosImportActivity.this.audioImportEntListShow.add(audioEnt);
                    }
                }
                AudiosImportActivity audiosImportActivity3 = AudiosImportActivity.this;
                audiosImportActivity3.filesAdapter = new AudioSystemFileAdapter(audiosImportActivity3, 1, audiosImportActivity3.audioImportEntListShow);
                AudiosImportActivity.this.imagegrid.setAdapter(AudiosImportActivity.this.filesAdapter);
                AudiosImportActivity.this.filesAdapter.notifyDataSetChanged();
                if (AudiosImportActivity.this.audioImportEntListShow.size() <= 0) {
                    AudiosImportActivity.this.album_import_ListView.setVisibility(View.INVISIBLE);
                    AudiosImportActivity.this.imagegrid.setVisibility(View.INVISIBLE);
                    AudiosImportActivity.this.ll_photo_video_empty.setVisibility(View.VISIBLE);
                    AudiosImportActivity.this.document_empty_icon.setBackgroundResource(R.drawable.ic_audio_empty_icon);
                    AudiosImportActivity.this.lbl_photo_video_empty.setText(R.string.no_audio);
                }
                AudiosImportActivity.this.invalidateOptionsMenu();
            }
        });
        this.filesAdapter = new AudioSystemFileAdapter(this, 1, this.audioImportEntListShow);
        this.imagegrid.setAdapter(this.filesAdapter);


        this.Progress.setVisibility(View.GONE);
    }

    private void AudioFileBind() {
        Iterator it = new FileUtils().FindFiles(new String[]{"mp3", "wav", "m4a"}).iterator();
        while (it.hasNext()) {
            File file = (File) it.next();
            AudioEnt audioEnt = new AudioEnt();
            audioEnt.SetFile(file);
            audioEnt.setAudioName(file.getName());
            audioEnt.setOriginalAudioLocation(file.getAbsolutePath());
            audioEnt.setPlayListId(Common.PlayListId);
            audioEnt.SetFileCheck(false);
            audioEnt.SetFileImage(null);
            this.audioImportEntList.add(audioEnt);
            ImportAlbumEnt importAlbumEnt = new ImportAlbumEnt();

            Log.e("spinnerValues","spinnerValues");
            if (this.spinnerValues.size() <= 0 || !this.spinnerValues.contains(file.getParent())) {
                importAlbumEnt.SetAlbumName(file.getParent());
                importAlbumEnt.Set_Activity_type(ActivityType.Music.toString());
                this.importAlbumEnts.add(importAlbumEnt);

                Log.e("importAlbumEnt",""+importAlbumEnt);
                this.spinnerValues.add(file.getParent());
            }
        }
    }

    public void GetFolders() {
        this.folderadapter = new AudioFoldersImportAdapter(this.context, 17367043, this.importAlbumEnts, false);
        this.album_import_ListView.setAdapter(this.folderadapter);
        if (this.importAlbumEnts.size() <= 0) {
            this.album_import_ListView.setVisibility(View.INVISIBLE);
            this.imagegrid.setVisibility(View.INVISIBLE);
            this.ll_photo_video_empty.setVisibility(View.VISIBLE);
            this.document_empty_icon.setBackgroundResource(R.drawable.ic_audio_empty_icon);
            this.lbl_photo_video_empty.setText(R.string.no_audio);

            Log.e("GetFolders","GetFolders");
        }
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
            Toast.makeText(this, R.string.toast_unselectaudiomsg_import, Toast.LENGTH_SHORT).show();
        } else if (Common.GetFileSize(this.selectPath) < Common.GetTotalFree()) {
            int albumCheckCount = albumCheckCount();
            if (albumCheckCount >= 2) {
                final Dialog dialog = new Dialog(this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.confirmation_message_box);
                dialog.setCancelable(true);
                LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
                TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
                textView.setTypeface(Typeface.createFromAsset(getAssets(), "ebrima.ttf"));
                StringBuilder sb = new StringBuilder();
                sb.append("Are you sure you want to import ");
                sb.append(albumCheckCount);
                sb.append(" folders? Importing may take time according to the size of your data.");
                textView.setText(sb.toString());
                ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        for (int i = 0; i < AudiosImportActivity.this.importAlbumEnts.size(); i++) {
                            ((ImportAlbumEnt) AudiosImportActivity.this.importAlbumEnts.get(i)).SetAlbumFileCheck(false);
                        }
                        AudiosImportActivity audiosImportActivity = AudiosImportActivity.this;
                        audiosImportActivity.folderadapter = new AudioFoldersImportAdapter(audiosImportActivity.context, 17367043, AudiosImportActivity.this.importAlbumEnts, false);
                        AudiosImportActivity.this.album_import_ListView.setAdapter(AudiosImportActivity.this.folderadapter);
                        AudiosImportActivity.this.folderadapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (VERSION.SDK_INT < 21 || VERSION.SDK_INT >= 23) {
                            AudiosImportActivity.this.Import();
                        } else if (GetObject.GetSDCardUri().length() > 0) {
                            AudiosImportActivity.this.Import();
                        } else if (!GetObject.GetISDAlertshow()) {
                            AudiosImportActivity.this.LollipopSdCardPermissionDialog();
                        } else {
                            AudiosImportActivity.this.Import();
                        }
                    }
                });
                dialog.show();
            } else if (VERSION.SDK_INT < 21 || VERSION.SDK_INT >= 23) {
                Import();
            } else if (GetObject.GetSDCardUri().length() > 0) {
                Import();
            } else if (!GetObject.GetISDAlertshow()) {
                LollipopSdCardPermissionDialog();
            } else {
                Import();
            }
        }
    }


    public void LollipopSdCardPermissionDialog() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.sdcard_permission_alert_msgbox);
        dialog.setCancelable(true);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        ((CheckBox) dialog.findViewById(R.id.cbalertdialog)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                StorageOptionSharedPreferences.GetObject(AudiosImportActivity.this).SetISDAlertshow(Boolean.valueOf(z));
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                AudiosImportActivity.this.Import();
            }
        });
        dialog.show();
    }


    public void Import() {
        SelectedCount();
        ShowImportProgress();
        Common.IsWorkInProgress = true;
        new Thread() {
            public void run() {
                try {
                    AudiosImportActivity.this.ImportAudio();
                    Message message = new Message();
                    message.what = 3;
                    AudiosImportActivity.this.handle.sendMessage(message);
                    Common.IsWorkInProgress = false;
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 3;
                    AudiosImportActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }

    public void ImportAudio() {
        if (this.isAlbumClick) {
            ImportOnlyAudioSDCard();
        } else {
            importFolder();
        }
    }


    public void importFolder() {
        if (this.importAlbumEnts.size() > 0) {
            int i = 0;
            for (int i2 = 0; i2 < this.importAlbumEnts.size(); i2++) {
                if (((ImportAlbumEnt) this.importAlbumEnts.get(i2)).GetAlbumFileCheck()) {
                    File file = new File(((ImportAlbumEnt) this.importAlbumEnts.get(i2)).GetAlbumName());
                    StringBuilder sb = new StringBuilder();
                    sb.append(StorageOptionsCommon.STORAGEPATH);
                    sb.append(StorageOptionsCommon.AUDIOS);
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
                            sb3.append(StorageOptionsCommon.AUDIOS);
                            sb3.append(this.folderName);
                            file3 = new File(sb3.toString());
                            if (!file3.exists()) {
                                i3 = 100;
                            }
                            i3++;
                        }
                        file2 = file3;
                    }
                    AddFolderToDatabase(this.folderName);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this);
                    audioPlayListDAL.OpenRead();
                    this.folderId = audioPlayListDAL.GetLastPlayListId();
                    Common.FolderId = this.folderId;
                    audioPlayListDAL.close();
                    ImportAlbumsAudioSDCard(i);
                    i++;
                }
            }
        }
    }


    public void ImportAlbumsAudioSDCard(int i) {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        List list = (List) this.audioEntListShowList.get(i);
        for (int i2 = 0; i2 < list.size(); i2++) {
            if (((AudioEnt) list.get(i2)).GetFileCheck()) {
                File file = new File(((AudioEnt) list.get(i2)).getOriginalAudioLocation());
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append(StorageOptionsCommon.AUDIOS);
                sb.append(this.folderName);
                File file2 = new File(sb.toString());
                String str = "";
                try {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(file2.getAbsolutePath());
                    sb2.append("/");
                    sb2.append(Utilities.ChangeFileExtention(file.getName()));
                    File file3 = new File(sb2.toString());
                    if (file.exists()) {
                        File parentFile = file3.getParentFile();
                        if (!parentFile.exists()) {
                            parentFile.mkdirs();
                        }
                        file3.createNewFile();
                        Flaes.encryptUsingCipherStream_AES128(file, file3);
                        str = file3.getAbsolutePath();
                    }
                    if (file.exists() && file3.exists()) {
                        file.delete();
                    }
                    if (str.length() > 0) {
                        AddAudioToDatabase(FileName(((AudioEnt) list.get(i2)).getOriginalAudioLocation()), ((AudioEnt) list.get(i2)).getOriginalAudioLocation(), str);
                    }
                    if (VERSION.SDK_INT >= 21 && VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                        Utilities.DeleteSDcardImageLollipop(this, file.getAbsolutePath());
                    }
                } catch (IOException e) {
                    this.IsExceptionInImportPhotos = true;
                    e.printStackTrace();
                }
            }
        }
    }


    public void ImportOnlyAudioSDCard() {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        int size = this.audioImportEntListShow.size();
        for (int i = 0; i < size; i++) {
            if (((AudioEnt) this.audioImportEntListShow.get(i)).GetFileCheck()) {
                File file = new File(((AudioEnt) this.audioImportEntListShow.get(i)).getOriginalAudioLocation());
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append(StorageOptionsCommon.AUDIOS);
                sb.append(this.folderName);
                File file2 = new File(sb.toString());
                String str = "";
                try {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(file2.getAbsolutePath());
                    sb2.append("/");
                    sb2.append(Utilities.ChangeFileExtention(file.getName()));
                    File file3 = new File(sb2.toString());
                    if (file.exists()) {
                        File parentFile = file3.getParentFile();
                        if (!parentFile.exists()) {
                            parentFile.mkdirs();
                        }
                        file3.createNewFile();
                        Flaes.encryptUsingCipherStream_AES128(file, file3);
                        str = file3.getAbsolutePath();
                        if (file.exists() && file3.exists()) {
                            file.delete();
                        }
                    }
                    if (VERSION.SDK_INT >= 21 && VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                        Utilities.DeleteSDcardImageLollipop(this, file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    this.IsExceptionInImportPhotos = true;
                    e.printStackTrace();
                }
                if (str.length() > 0) {
                    String[] split = str.split("/");
                    AddAudioToDatabase(Utilities.ChangeFileExtentionToOrignal(split[split.length - 1]), ((AudioEnt) this.audioImportEntListShow.get(i)).getOriginalAudioLocation(), str);
                }
            }
        }
        Common.SelectedCount = 0;
        Common.IsSelectAll = false;
    }


    public void AddAudioToDatabase(String str, String str2, String str3) {
        AudioEnt audioEnt = new AudioEnt();
        audioEnt.setAudioName(str);
        audioEnt.setFolderLockAudioLocation(str3);
        audioEnt.setOriginalAudioLocation(str2);
        audioEnt.setPlayListId(this.folderId);
        AudioDAL audioDAL = new AudioDAL(this.context);
        try {
            audioDAL.OpenWrite();
            audioDAL.AddAudio(audioEnt, str3);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            audioDAL.close();
            throw th;
        }
        audioDAL.close();
    }

    public void AddFolderToDatabase(String str) {

        AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
        audioPlayListEnt.setPlayListName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.AUDIOS);
        sb.append(str);
        audioPlayListEnt.setPlayListLocation(sb.toString());
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this);
        try {
            audioPlayListDAL.OpenWrite();
            audioPlayListDAL.AddAudioPlayList(audioPlayListEnt);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            audioPlayListDAL.close();
            throw th;
        }
        audioPlayListDAL.close();
    }

    private byte[] getRawKey(byte[] bArr) throws Exception {
        KeyGenerator instance = KeyGenerator.getInstance("AES");
        SecureRandom instance2 = SecureRandom.getInstance("SHA1PRNG");
        instance2.setSeed(bArr);
        instance.init(128, instance2);
        return instance.generateKey().getEncoded();
    }

    private byte[] encrypt(byte[] bArr, byte[] bArr2) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "AES");
        Cipher instance = Cipher.getInstance("AES");
        instance.init(1, secretKeySpec);
        return instance.doFinal(bArr2);
    }

    private boolean IsFileCheck() {
        for (int i = 0; i < this.importAlbumEnts.size(); i++) {
            if (((ImportAlbumEnt) this.importAlbumEnts.get(i)).GetAlbumFileCheck()) {
                this.audioImportEntListShow = new ArrayList<>();
                Iterator it = this.audioImportEntList.iterator();
                while (it.hasNext()) {
                    AudioEnt audioEnt = (AudioEnt) it.next();
                    if (((String) this.spinnerValues.get(i)).equals(new File(audioEnt.getOriginalAudioLocation()).getParent())) {
                        this.audioImportEntListShow.add(audioEnt);
                    }
                    for (int i2 = 0; i2 < this.audioImportEntListShow.size(); i2++) {
                        ((AudioEnt) this.audioImportEntListShow.get(i2)).SetFileCheck(true);
                    }
                }
                this.audioEntListShowList.add(this.audioImportEntListShow);
            }
        }
        this.selectPath.clear();
        for (int i3 = 0; i3 < this.audioImportEntListShow.size(); i3++) {
            if (((AudioEnt) this.audioImportEntListShow.get(i3)).GetFileCheck()) {
                this.selectPath.add(((AudioEnt) this.audioImportEntListShow.get(i3)).getOriginalAudioLocation());
                return true;
            }
        }
        return false;
    }

    private void SelectedCount() {
        this.selectCount = 0;
        for (int i = 0; i < this.audioImportEntListShow.size(); i++) {
            if (((AudioEnt) this.audioImportEntListShow.get(i)).GetFileCheck()) {
                this.selectCount++;
            }
        }
    }

    public void btnBackonClick(View view) {
        Back();
    }


    public void SelectAllAudios() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.audioImportEntListShow.size(); i++) {
                ((AudioEnt) this.audioImportEntListShow.get(i)).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            Common.IsSelectAll = false;
            SelectedItemsCount(0);
            Common.SelectedCount = 0;
            this.btnSelectAll.setImageResource(R.drawable.ic_unselectallicon);
            return;
        }
        for (int i2 = 0; i2 < this.audioImportEntListShow.size(); i2++) {
            ((AudioEnt) this.audioImportEntListShow.get(i2)).SetFileCheck(true);
        }
        Common.SelectedCount = this.audioImportEntListShow.size();
        this.IsSelectAll = true;
        Common.IsSelectAll = true;
        this.btnSelectAll.setImageResource(R.drawable.ic_selectallicon);
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
        Common.SelectedCount = 0;
        if (this.isAlbumClick) {
            this.isAlbumClick = false;
            this.album_import_ListView.setVisibility(View.VISIBLE);
            this.imagegrid.setVisibility(View.INVISIBLE);
            this.btnSelectAll.setVisibility(View.INVISIBLE);
            for (int i = 0; i < this.audioImportEntListShow.size(); i++) {
                ((AudioEnt) this.audioImportEntListShow.get(i)).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            Common.IsSelectAll = false;
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, AudioActivity.class));
        finish();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Common.SelectedCount = 0;
    }


    public void onPause() {
        super.onPause();
        if (SecurityLocksCommon.IsAppDeactive && !Common.IsWorkInProgress) {
            finish();
            System.exit(0);
        }
    }


    public void onResume() {
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        Common.SelectedCount = 0;
        if (i == 4) {
            if (this.isAlbumClick) {
                this.isAlbumClick = false;
                this.album_import_ListView.setVisibility(View.VISIBLE);
                this.imagegrid.setVisibility(View.INVISIBLE);
                this.btnSelectAll.setVisibility(View.INVISIBLE);
                for (int i2 = 0; i2 < this.audioImportEntListShow.size(); i2++) {
                    ((AudioEnt) this.audioImportEntListShow.get(i2)).SetFileCheck(false);
                }
                this.IsSelectAll = false;
                Common.IsSelectAll = false;
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, AudioPlayListActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void SelectedItemsCount(int i) {
        this.selectedCount = Integer.toString(i);
        invalidateOptionsMenu();
    }
}
