package com.example.vault.datarecovery;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.vault.R;
import com.example.vault.BaseActivity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.SettingActivity;
import com.example.vault.storageoption.StorageOptionsCommon;

public class DataRecoveryActivity extends BaseActivity {
    public static ProgressDialog myProgressDialog;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 3) {
                if (StorageOptionsCommon.IsAllDataRecoveryInProg) {
                    DataRecoveryActivity.this.hideProgress();
                    StorageOptionsCommon.IsAllDataRecoveryInProg = false;
                    if (StorageOptionsCommon.IsUserHasDataToRecover) {
                        StorageOptionsCommon.IsUserHasDataToRecover = false;
                        Toast.makeText(DataRecoveryActivity.this, R.string.toast_dataRecovery_Success, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DataRecoveryActivity.this, R.string.toast_dataRecovery_have_no_data, Toast.LENGTH_LONG).show();
                    }
                }
            } else if (message.what == 2) {
                DataRecoveryActivity.this.hideProgress();
            }
            super.handleMessage(message);
        }
    };
    LinearLayout ll_DataRecover_Recover;
    LinearLayout ll_background;
    LinearLayout ll_storage_option_topbaar_bg;
    private SensorManager sensorManager;
    private Toolbar toolbar;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void showDataRecoveryProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Your data is being recovered\nWarning: Please be patient and do not close this app otherwise you may lose your data.", true);
    }


    public void hideProgress() {
        try {
            if (myProgressDialog != null && myProgressDialog.isShowing()) {
                myProgressDialog.dismiss();
            }
        } catch (Exception unused) {
        } catch (Throwable th) {
            myProgressDialog = null;
            throw th;
        }
        myProgressDialog = null;
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.data_recovery_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        StorageOptionsCommon.IsUserHasDataToRecover = false;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);

        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DataRecoveryActivity.this.btnBackonClick();
            }
        });
        this.ll_DataRecover_Recover = (LinearLayout) findViewById(R.id.ll_DataRecover_Recover);
        this.ll_DataRecover_Recover.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DataRecoveryActivity.this.showDataRecoveryProgress();
                StorageOptionsCommon.IsAllDataRecoveryInProg = true;
                new Thread() {
                    public void run() {
                        try {
                            new DataRecover().RecoverALLData(DataRecoveryActivity.this);
                            Message message = new Message();
                            message.what = 3;
                            DataRecoveryActivity.this.handle.sendMessage(message);
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 2;
                            DataRecoveryActivity.this.handle.sendMessage(message2);
                        }
                    }
                }.start();
            }
        });
    }

    public void btnBackonClick() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, SettingActivity.class));
        finish();
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
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive && !StorageOptionsCommon.IsAllDataRecoveryInProg) {
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
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, SettingActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
