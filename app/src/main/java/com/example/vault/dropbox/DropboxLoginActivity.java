package com.example.vault.dropbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.LinearLayout;
import com.dropbox.core.android.Auth;
import com.example.vault.R;
import com.example.vault.audio.AudioPlayListActivity;
import com.example.vault.BaseActivity;
import com.example.vault.documents.DocumentsFolderActivity;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.notes.NotesFoldersActivity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.photo.PhotosAlbumActivty;
import com.example.vault.securebackupcloud.CloudCommon;
import com.example.vault.securebackupcloud.CloudCommon.DropboxType;
import com.example.vault.securebackupcloud.DropboxCloudApi;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.SettingActivity;
import com.example.vault.todolist.ToDoActivity;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;
import com.example.vault.video.VideosAlbumActivty;
import com.example.vault.wallet.WalletCategoriesActivity;

public class DropboxLoginActivity extends BaseActivity {
    private static final String ACCOUNT_PREFS_NAME = "DropboxPerf";
    static SharedPreferences CloudPrefs;
    static Editor CloudprefsEditor;
    private String APP_KEY = "u041g5ffkpo64o5";
    Handler SignOuthandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                Common.isSignOutSuccessfully = true;
            }
            super.handleMessage(message);
        }
    };
    private DropboxCloudApi dropboxCloudApi;
    LinearLayout ll_background;
    LinearLayout ll_btnDropboxSignIn;
    LinearLayout ll_btnDropboxSignOut;
    LinearLayout ll_topbaar;
    private boolean mLoggedIn;
    private SensorManager sensorManager;
    private Toolbar toolbar;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.dropboxsignin_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        this.dropboxCloudApi = new DropboxCloudApi(this);
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.ll_btnDropboxSignIn = (LinearLayout) findViewById(R.id.ll_btnDropboxSignIn);
        this.ll_btnDropboxSignOut = (LinearLayout) findViewById(R.id.ll_btnDropboxSignOut);
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Cloud");
        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DropboxLoginActivity.this.btnBackonClick();
            }
        });
        CloudPrefs = getSharedPreferences("Cloud", 0);
        CloudCommon.CloudType = CloudPrefs.getInt("CloudType", 0);
        boolean z = CloudPrefs.getBoolean("isAppRegisterd", false);
        String string = getSharedPreferences(ACCOUNT_PREFS_NAME, 0).getString("access-token", null);
        if (z && string != null) {
            this.ll_btnDropboxSignIn.setVisibility(View.INVISIBLE);
            this.ll_btnDropboxSignOut.setVisibility(View.VISIBLE);
        }
        CloudprefsEditor = CloudPrefs.edit();
        CloudprefsEditor.putInt("CloudType", 0);
        CloudprefsEditor.commit();
        CloudCommon.CloudType = 0;
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

    public void DropboxSignIn(View view) {
        SecurityLocksCommon.IsAppDeactive = false;
        Common.isSignOutSuccessfully = false;
        Auth.startOAuth2Authentication(this, this.APP_KEY);
    }

    public void DropboxSignOut(View view) {
        DropboxSignOut();
    }

    public void DropboxSignOut() {
        if (CloudPrefs.getBoolean("isAppRegisterd", false)) {
            CloudprefsEditor = CloudPrefs.edit();
            CloudprefsEditor.putBoolean("isAppRegisterd", false);
            CloudprefsEditor.commit();
        }
        SharedPreferences sharedPreferences = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        if (sharedPreferences.getString("access-token", null) != null) {
            sharedPreferences.edit().remove("access-token").commit();
        }
        if (Auth.getOAuth2Token() != null) {
            this.dropboxCloudApi.client = null;
            Message message = new Message();
            message.what = 1;
            this.SignOuthandler.sendMessage(message);
        }
        setLoggedIn(false);
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(getApplicationContext(), DropboxLoginActivity.class));
        finish();
    }

    private void setLoggedIn(boolean z) {
        this.mLoggedIn = z;
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

    public void StartCloudDownloadActiviy() {
        SecurityLocksCommon.IsAppDeactive = false;
        if (DropboxType.Photos.ordinal() == CloudCommon.ModuleType) {
            CloudCommon.ModuleType = DropboxType.Photos.ordinal();
            Utilities.StartCloudActivity(this);
        } else if (DropboxType.Videos.ordinal() == CloudCommon.ModuleType) {
            CloudCommon.ModuleType = DropboxType.Videos.ordinal();
            Utilities.StartCloudActivity(this);
        } else if (DropboxType.Documents.ordinal() == CloudCommon.ModuleType) {
            CloudCommon.ModuleType = DropboxType.Documents.ordinal();
            Utilities.StartCloudActivity(this);
        } else if (DropboxType.Notes.ordinal() == CloudCommon.ModuleType) {
            CloudCommon.ModuleType = DropboxType.Notes.ordinal();
            Utilities.StartCloudActivity(this);
        } else if (DropboxType.Wallet.ordinal() == CloudCommon.ModuleType) {
            CloudCommon.ModuleType = DropboxType.Wallet.ordinal();
            Utilities.StartCloudActivity(this);
        } else if (DropboxType.ToDo.ordinal() == CloudCommon.ModuleType) {
            CloudCommon.ModuleType = DropboxType.ToDo.ordinal();
            Utilities.StartCloudActivity(this);
        } else if (DropboxType.Audio.ordinal() == CloudCommon.ModuleType) {
            CloudCommon.ModuleType = DropboxType.Audio.ordinal();
            Utilities.StartCloudActivity(this);
        }
    }


    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        if (sharedPreferences.getString("access-token", null) == null) {
            String oAuth2Token = Auth.getOAuth2Token();
            if (oAuth2Token != null && !Common.isSignOutSuccessfully) {
                sharedPreferences.edit().putString("access-token", oAuth2Token).apply();
                if (!CloudPrefs.getBoolean("isAppRegisterd", false)) {
                    CloudprefsEditor = CloudPrefs.edit();
                    CloudprefsEditor.putBoolean("isAppRegisterd", true);
                    CloudprefsEditor.commit();
                }
                if (!CloudCommon.IsCameFromSettings) {
                    StartCloudDownloadActiviy();
                    return;
                }
                this.ll_btnDropboxSignIn.setVisibility(View.INVISIBLE);
                this.ll_btnDropboxSignOut.setVisibility(View.VISIBLE);
            }
        }
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
                intent = new Intent(getApplicationContext(), SettingActivity.class);
                CloudCommon.IsCameFromSettings = false;
            } else if (CloudCommon.IsCameFromCloudMenu) {
                intent = new Intent(getApplicationContext(), MainiFeaturesActivity.class);
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
