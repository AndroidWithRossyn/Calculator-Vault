package com.example.vault.dropbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.widget.Toolbar;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

import com.example.vault.R;
import com.example.vault.audio.AudioPlayListActivity;
import com.example.vault.BaseActivity;
import com.example.vault.documents.DocumentsFolderActivity;
import com.example.vault.dropbox.adapter.DropboxAdapter;
import com.example.vault.dropbox.service.CloudService;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.notes.NotesFoldersActivity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.photo.PhotosAlbumActivty;
import com.example.vault.securebackupcloud.BackupCloudEnt;
import com.example.vault.securebackupcloud.CloudCommon;
import com.example.vault.securebackupcloud.CloudCommon.CloudFolderStatus;
import com.example.vault.securebackupcloud.CloudCommon.DropboxType;
import com.example.vault.securebackupcloud.DropboxCloud;
import com.example.vault.securebackupcloud.ISecureBackupCloud;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.SettingActivity;
import com.example.vault.todolist.ToDoActivity;
import com.example.vault.video.VideosAlbumActivty;
import com.example.vault.wallet.WalletCategoriesActivity;

public class DropBoxDownloadActivity extends BaseActivity {
    public static ProgressDialog myProgressDialog;
    ArrayList<BackupCloudEnt> backupCloudEntList;
    DropboxAdapter dropboxAdapter;
    ListView dropboxdownloadListView;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 3) {
                DropBoxDownloadActivity.this.hideProgress();
            } else if (message.what == 1) {
                DropBoxDownloadActivity.this.hideProgress();
                DropBoxDownloadActivity dropBoxDownloadActivity = DropBoxDownloadActivity.this;
                dropBoxDownloadActivity.dropboxAdapter = new DropboxAdapter(dropBoxDownloadActivity.getApplicationContext(), 17367043, DropBoxDownloadActivity.this.backupCloudEntList);
                DropBoxDownloadActivity.this.dropboxdownloadListView.setAdapter(DropBoxDownloadActivity.this.dropboxAdapter);
                DropBoxDownloadActivity.this.dropboxAdapter.notifyDataSetChanged();
                DropBoxDownloadActivity.this.UpDatedListView();
            } else if (message.what == 2) {
                DropBoxDownloadActivity.this.hideProgress();
            }
            super.handleMessage(message);
        }
    };
    ISecureBackupCloud iSecureBackupCloud;
    LinearLayout ll_background;
    LinearLayout ll_topbaar;
    final Handler myHandler = new Handler() {
        public void handleMessage(Message message) {
            DropBoxDownloadActivity.this.UpDataBind(0, 0);
            DropBoxDownloadActivity.this.UpDatedListView();
        }
    };
    private SensorManager sensorManager;
    private Toolbar toolbar;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.dropbox_download_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.dropboxdownloadListView = (ListView) findViewById(R.id.dropboxdownloadListView);
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DropBoxDownloadActivity.this.btnBackonClick();
            }
        });
        TextView title7 = findViewById(R.id.title7);
//        title7.setText(R.string.lblFeature1);
        if (DropboxType.Photos.ordinal() == CloudCommon.ModuleType) {
            title7.setText(R.string.lblFeature1);
//            getSupportActionBar().setTitle((int) R.string.lblFeature1);
            // getSupportActionBar().setTitle("");

        } else if (DropboxType.Videos.ordinal() == CloudCommon.ModuleType) {
            title7.setText(R.string.lblFeature2);
//            getSupportActionBar().setTitle((int) R.string.lblFeature2);
            // getSupportActionBar().setTitle("");

        } else if (DropboxType.Documents.ordinal() == CloudCommon.ModuleType) {
            title7.setText(R.string.lblFeature4);

//            getSupportActionBar().setTitle((int) R.string.lblFeature4);
            // getSupportActionBar().setTitle("");

        } else if (DropboxType.Notes.ordinal() == CloudCommon.ModuleType) {
            title7.setText(R.string.lblFeature6);
//            getSupportActionBar().setTitle((int) R.string.lblFeature6);
            // getSupportActionBar().setTitle("");

        } else if (DropboxType.Wallet.ordinal() == CloudCommon.ModuleType) {
            title7.setText(R.string.lblFeature7);

//            getSupportActionBar().setTitle((int) R.string.lblFeature7);
            // getSupportActionBar().setTitle("");

        } else if (DropboxType.ToDo.ordinal() == CloudCommon.ModuleType) {
            title7.setText(R.string.todoList);

//            getSupportActionBar().setTitle((int) R.string.todoList);
            // getSupportActionBar().setTitle("");

        } else if (DropboxType.Audio.ordinal() == CloudCommon.ModuleType) {
            title7.setText(R.string.dropbox_Audios);
            // getSupportActionBar().setTitle("");

//            getSupportActionBar().setTitle((int) R.string.dropbox_Audios);
        }
        showProgress();
        new Thread() {
            public void run() {
                try {
                    DropBoxDownloadActivity.this.DataBind(CloudCommon.ModuleType);
                    Message message = new Message();
                    message.what = 1;
                    DropBoxDownloadActivity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 2;
                    DropBoxDownloadActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
        this.dropboxdownloadListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                BackupCloudEnt backupCloudEnt = (BackupCloudEnt) DropBoxDownloadActivity.this.backupCloudEntList.get(i);
                if (!backupCloudEnt.GetIsInProgress() && backupCloudEnt.GetSyncVisibility() != 4 && backupCloudEnt.GetStatus() != CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal()) {
                    CloudService.AddBackupCloudEntList.add(backupCloudEnt);
                    ((BackupCloudEnt) DropBoxDownloadActivity.this.backupCloudEntList.get(i)).SetIsInProgress(true);
                    int firstVisiblePosition = DropBoxDownloadActivity.this.dropboxdownloadListView.getFirstVisiblePosition();
                    int i2 = 0;
                    View childAt = DropBoxDownloadActivity.this.dropboxdownloadListView.getChildAt(0);
                    if (childAt != null) {
                        i2 = childAt.getTop();
                    }
                    DropBoxDownloadActivity.this.UpDataBind(firstVisiblePosition, i2);
                    if (!CloudCommon.IsCloudServiceStarted) {
                        DropBoxDownloadActivity.this.startService(new Intent(DropBoxDownloadActivity.this, CloudService.class));
                    }
                }
            }
        });
    }

    private void showProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... cloud data loading...", true);
    }


    public void hideProgress() {
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void btnBackonClick() {
        Intent intent;
        SecurityLocksCommon.IsAppDeactive = false;
        if (CloudCommon.IsCameFromSettings) {
            intent = new Intent(getApplicationContext(), SettingActivity.class);
            CloudCommon.IsCameFromSettings = false;
        } else if (CloudCommon.IsCameFromCloudMenu) {
            intent = new Intent(getApplicationContext(), MainiFeaturesActivity.class);
            CloudCommon.IsCameFromCloudMenu = false;
        } else {
            intent = DropboxType.Photos.ordinal() == CloudCommon.ModuleType ? new Intent(getApplicationContext(), PhotosAlbumActivty.class) : DropboxType.Videos.ordinal() == CloudCommon.ModuleType ? new Intent(getApplicationContext(), VideosAlbumActivty.class) : DropboxType.Documents.ordinal() == CloudCommon.ModuleType ? new Intent(getApplicationContext(), DocumentsFolderActivity.class) : DropboxType.Notes.ordinal() == CloudCommon.ModuleType ? new Intent(getApplicationContext(), NotesFoldersActivity.class) : DropboxType.Wallet.ordinal() == CloudCommon.ModuleType ? new Intent(getApplicationContext(), WalletCategoriesActivity.class) : DropboxType.ToDo.ordinal() == CloudCommon.ModuleType ? new Intent(getApplicationContext(), ToDoActivity.class) : DropboxType.Audio.ordinal() == CloudCommon.ModuleType ? new Intent(getApplicationContext(), AudioPlayListActivity.class) : null;
        }
        startActivity(intent);
        finish();
    }

    public void SyncAll(View view) {
        int i;
        Iterator it = this.backupCloudEntList.iterator();
        boolean z = false;
        while (it.hasNext()) {
            BackupCloudEnt backupCloudEnt = (BackupCloudEnt) it.next();
            if (!(backupCloudEnt.GetIsInProgress() || backupCloudEnt.GetSyncVisibility() == 4 || backupCloudEnt.GetStatus() == CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal())) {
                CloudService.AddBackupCloudEntList.add(backupCloudEnt);
                backupCloudEnt.SetIsInProgress(true);
                z = true;
            }
        }
        int firstVisiblePosition = this.dropboxdownloadListView.getFirstVisiblePosition();
        View childAt = this.dropboxdownloadListView.getChildAt(0);
        if (childAt == null) {
            i = 0;
        } else {
            i = childAt.getTop();
        }
        if (z) {
            UpDataBind(firstVisiblePosition, i);
            Toast.makeText(getApplicationContext(), "Sync All", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already sync", Toast.LENGTH_SHORT).show();
        }
        if (!CloudCommon.IsCloudServiceStarted) {
            startService(new Intent(this, CloudService.class));
        }
    }


    public void DataBind(int i) {
        if (DropboxType.Photos.ordinal() == i) {
            this.iSecureBackupCloud = new DropboxCloud(this, DropboxType.Photos.ordinal());
            this.backupCloudEntList = this.iSecureBackupCloud.GetFolders(CloudCommon.PhotoFolder);
        } else if (DropboxType.Videos.ordinal() == i) {
            this.iSecureBackupCloud = new DropboxCloud(this, DropboxType.Videos.ordinal());
            this.backupCloudEntList = this.iSecureBackupCloud.GetFolders(CloudCommon.VideoFolder);
        } else if (DropboxType.Documents.ordinal() == i) {
            this.iSecureBackupCloud = new DropboxCloud(this, DropboxType.Documents.ordinal());
            this.backupCloudEntList = this.iSecureBackupCloud.GetFolders(CloudCommon.DocumentFolder);
        } else if (DropboxType.Notes.ordinal() == i) {
            this.iSecureBackupCloud = new DropboxCloud(this, DropboxType.Notes.ordinal());
            this.backupCloudEntList = this.iSecureBackupCloud.GetFolders(CloudCommon.NotesFolder);
        } else if (DropboxType.Wallet.ordinal() == i) {
            this.iSecureBackupCloud = new DropboxCloud(this, DropboxType.Wallet.ordinal());
            this.backupCloudEntList = this.iSecureBackupCloud.GetFolders(CloudCommon.WalletFolder);
        } else if (DropboxType.ToDo.ordinal() == i) {
            this.iSecureBackupCloud = new DropboxCloud(this, DropboxType.ToDo.ordinal());
            this.backupCloudEntList = this.iSecureBackupCloud.GetFolders(CloudCommon.ToDoListFolder);
        } else if (DropboxType.Audio.ordinal() == i) {
            this.iSecureBackupCloud = new DropboxCloud(this, DropboxType.Audio.ordinal());
            this.backupCloudEntList = this.iSecureBackupCloud.GetFolders(CloudCommon.AudioFolder);
        }
        Iterator it = this.backupCloudEntList.iterator();
        while (it.hasNext()) {
            BackupCloudEnt backupCloudEnt = (BackupCloudEnt) it.next();
            if (backupCloudEnt.GetStatus() == CloudFolderStatus.OnlyPhone.ordinal()) {
                backupCloudEnt.SetImageStatus(R.drawable.up_status);
            } else if (backupCloudEnt.GetStatus() == CloudFolderStatus.OnlyCloud.ordinal()) {
                backupCloudEnt.SetImageStatus(R.drawable.down_status);
            } else if (backupCloudEnt.GetStatus() == CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal()) {
                backupCloudEnt.SetImageStatus(R.drawable.synced_status);
                backupCloudEnt.SetSyncVisibility(4);
            } else if (backupCloudEnt.GetStatus() == CloudFolderStatus.CloudAndPhoneNotSync.ordinal()) {
                backupCloudEnt.SetImageStatus(R.drawable.up_down_status);
            }
            Iterator it2 = CloudService.UpdateBackupCloudEntList.iterator();
            while (it2.hasNext()) {
                if (backupCloudEnt.GetFolderName().equals(((BackupCloudEnt) it2.next()).GetFolderName())) {
                    backupCloudEnt.SetIsInProgress(true);
                }
            }
        }
    }


    public void UpDataBind(int i, int i2) {
        this.dropboxAdapter = new DropboxAdapter(getApplicationContext(), 17367043, this.backupCloudEntList);
        this.dropboxdownloadListView.setAdapter(this.dropboxAdapter);
        this.dropboxAdapter.notifyDataSetChanged();
        this.dropboxdownloadListView.setSelectionFromTop(i, i2);
    }


    public void UpDatedListView() {
        new Thread(new Runnable() {
            public void run() {
                boolean z = true;
                int i = 0;
                while (z) {
                    try {
                        Iterator it = DropBoxDownloadActivity.this.backupCloudEntList.iterator();
                        while (it.hasNext()) {
                            BackupCloudEnt backupCloudEnt = (BackupCloudEnt) it.next();
                            int i2 = -1;
                            Iterator it2 = CloudService.UpdateBackupCloudEntList.iterator();
                            while (true) {
                                if (!it2.hasNext()) {
                                    break;
                                }
                                BackupCloudEnt backupCloudEnt2 = (BackupCloudEnt) it2.next();
                                i2++;
                                if (backupCloudEnt.GetFolderName().equals(backupCloudEnt2.GetFolderName()) && !backupCloudEnt2.GetIsInProgress()) {
                                    backupCloudEnt.SetDownloadCount(0);
                                    backupCloudEnt.SetUploadCount(0);
                                    backupCloudEnt.SetImageStatus(R.drawable.synced_status);
                                    backupCloudEnt.SetSyncVisibility(4);
                                    backupCloudEnt.SetStatus(CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal());
                                    i = i2;
                                    z = false;
                                    continue;

                                }
                            }
                            if (!z) {
                                break;
                            }
                        }
                        Thread.sleep(2000);
                    } catch (Exception unused) {
                        return;
                    }
                }
                if (CloudService.UpdateBackupCloudEntList.size() > 0 && !CloudService.IsRemovingIndex && !CloudService.RemoveUpdateBackupIndexs.contains(Integer.valueOf(i))) {
                    CloudService.RemoveUpdateBackupIndexs.add(Integer.valueOf(i));
                }
                DropBoxDownloadActivity.this.myHandler.sendMessage(DropBoxDownloadActivity.this.myHandler.obtainMessage());
            }
        }).start();
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


    public void onResume() {
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
        super.onResume();
    }


    public void onPause() {
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
        }
        super.onPause();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            SecurityLocksCommon.IsAppDeactive = false;
            Intent intent = null;
            if (CloudCommon.IsCameFromSettings) {
                intent = new Intent(this, SettingActivity.class);
                CloudCommon.IsCameFromSettings = false;
            } else if (CloudCommon.IsCameFromCloudMenu) {
                intent = new Intent(this, MainiFeaturesActivity.class);
                CloudCommon.IsCameFromCloudMenu = false;
            } else if (DropboxType.Photos.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), PhotosAlbumActivty.class);
            } else if (DropboxType.Videos.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), VideosAlbumActivty.class);
            } else if (DropboxType.Documents.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), DocumentsFolderActivity.class);
            } else if (DropboxType.Notes.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), NotesFoldersActivity.class);
            } else if (DropboxType.Wallet.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), WalletCategoriesActivity.class);
            } else if (DropboxType.ToDo.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), ToDoActivity.class);
            } else if (DropboxType.Audio.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), AudioPlayListActivity.class);
            }
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
