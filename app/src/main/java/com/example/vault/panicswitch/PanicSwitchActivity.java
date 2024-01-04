package com.example.vault.panicswitch;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.example.vault.R;
import com.example.vault.BaseActivity;
import com.example.vault.panicswitch.PanicSwitchCommon.SwitchApp;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.SettingActivity;

public class PanicSwitchActivity extends BaseActivity {
    ToggleButton btnFlick;
    ToggleButton btnPalmOnScreen;
    ToggleButton btnShake;
    private SensorManager sensorManager;
    private Toolbar toolbar;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.panic_switch_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        SecurityLocksCommon.IsAppDeactive = true;
        this.btnFlick = (ToggleButton) findViewById(R.id.togglebtnFlick);
        this.btnShake = (ToggleButton) findViewById(R.id.togglebtnShake);
        this.btnPalmOnScreen = (ToggleButton) findViewById(R.id.togglebtnPalmOnScreen);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Panic Switch");
        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PanicSwitchActivity.this.btnBackonClick();
            }
        });
        final PanicSwitchSharedPreferences GetObject = PanicSwitchSharedPreferences.GetObject(this);
        PanicSwitchCommon.IsFlickOn = GetObject.GetIsFlickOn();
        PanicSwitchCommon.IsShakeOn = GetObject.GetIsShakeOn();
        PanicSwitchCommon.IsPalmOnFaceOn = GetObject.GetIsPalmOnScreenOn();
        PanicSwitchCommon.SwitchingApp = GetObject.GetSwitchApp();
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioChooseSwitchApp);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.HomeScreen) {
                    GetObject.SetSwitchApp(SwitchApp.HomeScreen.toString());
                    PanicSwitchCommon.SwitchingApp = SwitchApp.HomeScreen.toString();
                }
            }
        });
        if (PanicSwitchCommon.SwitchingApp.equals(SwitchApp.HomeScreen.toString())) {
            radioGroup.check(R.id.HomeScreen);
        } else {
            radioGroup.check(R.id.Browser);
        }
        if (PanicSwitchCommon.IsFlickOn) {
            this.btnFlick.setChecked(true);
        } else {
            this.btnFlick.setChecked(false);
        }
        if (PanicSwitchCommon.IsShakeOn) {
            this.btnShake.setChecked(true);
        } else {
            this.btnShake.setChecked(false);
        }
        if (PanicSwitchCommon.IsPalmOnFaceOn) {
            this.btnPalmOnScreen.setChecked(true);
        } else {
            this.btnPalmOnScreen.setChecked(false);
        }
        this.btnFlick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                    PanicSwitchActivity.this.btnFlick.setChecked(true);
                    GetObject.SetIsFlickOn(Boolean.valueOf(true));
                    Toast.makeText(PanicSwitchActivity.this, "Panic Switch Flick now activated", Toast.LENGTH_SHORT).show();
                    PanicSwitchCommon.IsFlickOn = true;
                    return;
                }
                PanicSwitchActivity.this.btnFlick.setChecked(false);
                GetObject.SetIsFlickOn(Boolean.valueOf(false));
                Toast.makeText(PanicSwitchActivity.this, "Panic Switch Flick now deactivated", Toast.LENGTH_SHORT).show();
                PanicSwitchCommon.IsFlickOn = false;
            }
        });
        this.btnShake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                    PanicSwitchActivity.this.btnShake.setChecked(true);
                    GetObject.SetIsShakeOn(Boolean.valueOf(true));
                    Toast.makeText(PanicSwitchActivity.this, "Panic Switch Flick now activated", Toast.LENGTH_SHORT).show();
                    PanicSwitchCommon.IsShakeOn = true;
                    return;
                }
                PanicSwitchActivity.this.btnShake.setChecked(false);
                GetObject.SetIsShakeOn(Boolean.valueOf(false));
                Toast.makeText(PanicSwitchActivity.this, "Panic Switch Flick now deactivated", Toast.LENGTH_SHORT).show();
                PanicSwitchCommon.IsShakeOn = false;
            }
        });
        this.btnPalmOnScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                    PanicSwitchActivity.this.btnPalmOnScreen.setChecked(true);
                    GetObject.SetIsPalmOnScreenOn(Boolean.valueOf(true));
                    Toast.makeText(PanicSwitchActivity.this, "Panic Switch Flick now activated", Toast.LENGTH_SHORT).show();
                    PanicSwitchCommon.IsPalmOnFaceOn = true;
                    return;
                }
                PanicSwitchActivity.this.btnPalmOnScreen.setChecked(false);
                GetObject.SetIsPalmOnScreenOn(Boolean.valueOf(false));
                Toast.makeText(PanicSwitchActivity.this, "Panic Switch Flick now deactivated", Toast.LENGTH_SHORT).show();
                PanicSwitchCommon.IsPalmOnFaceOn = false;
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
        if (SecurityLocksCommon.IsAppDeactive) {
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
            startActivity(new Intent(getApplicationContext(), SettingActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
