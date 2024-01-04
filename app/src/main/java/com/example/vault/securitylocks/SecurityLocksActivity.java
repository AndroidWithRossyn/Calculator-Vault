package com.example.vault.securitylocks;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import com.example.vault.R;
import com.example.vault.BaseActivity;
import com.example.vault.calculator.CalculatorPinSetting;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.storageoption.SettingActivity;
import com.example.vault.utilities.Utilities;

public class SecurityLocksActivity extends BaseActivity {
    TextView SecurityCredentialsToBaar_Title;
    private SecurityLocksListAdapter adapter;
    public LinearLayout btnCancel;
    public LinearLayout btnOk;
    public CheckBox checkboxCal;
    boolean isSettingDecoy = false;
    public boolean ischeckbox = false;
    String isconfirmdialog = "";
    TextView lblloginoptionitem;
    LinearLayout ll_SecurityCredentials_TopBaar;
    LinearLayout ll_background;
    LinearLayout rootViewGroup;
    private ArrayList<SecurityLocksEnt> securityLocksEntEntList;
    private ListView securityLocksListView;
    private SensorManager sensorManager;
    private Toolbar toolbar;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.security_lock_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (getIntent().getStringExtra("isconfirmdialog") != null) {
            this.isconfirmdialog = getIntent().getStringExtra("isconfirmdialog");
        }
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "   Select Security Locks");
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.securityLocksListView = (ListView) findViewById(R.id.SecurityCredentialsListView);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.ll_SecurityCredentials_TopBaar = (LinearLayout) findViewById(R.id.ll_SecurityCredentials_TopBaar);
        this.ll_SecurityCredentials_TopBaar.setBackgroundColor(getResources().getColor(R.color.ColorAppTheme));
        this.SecurityCredentialsToBaar_Title = (TextView) findViewById(R.id.SecurityCredentialsToBaar_Title);
        this.SecurityCredentialsToBaar_Title.setTextColor(getResources().getColor(R.color.ColorWhite));
        this.rootViewGroup = (LinearLayout) findViewById(R.id.rootViewGroup);
        this.isSettingDecoy = SecurityLocksCommon.isSettingDecoy;
        if (Utilities.getScreenOrientation(this) == 1) {
            this.rootViewGroup.setVisibility(View.INVISIBLE);
        } else if (Utilities.getScreenOrientation(this) == 2) {
            this.rootViewGroup.setVisibility(View.GONE);
        }
        this.securityLocksListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                switch (i) {
                    case 0:
                        SecurityLocksCommon.IsAppDeactive = false;
                        SecurityLocksActivity.this.agreeDisguiseModeDialog();
                        return;
                    case 1:
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent = new Intent(SecurityLocksActivity.this, SetPasswordActivity.class);
                        intent.putExtra("LoginOption", "Pin");
                        intent.putExtra("isSettingDecoy", SecurityLocksActivity.this.isSettingDecoy);
                        SecurityLocksActivity.this.startActivity(intent);
                        SecurityLocksActivity.this.finish();
                        return;
                    case 2:
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent2 = new Intent(SecurityLocksActivity.this, SetPatternActivity.class);
                        intent2.putExtra("isSettingDecoy", SecurityLocksActivity.this.isSettingDecoy);
                        SecurityLocksActivity.this.startActivity(intent2);
                        SecurityLocksActivity.this.finish();
                        return;
                    case 3:
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent3 = new Intent(SecurityLocksActivity.this, SetPasswordActivity.class);
                        intent3.putExtra("LoginOption", "Password");
                        intent3.putExtra("isSettingDecoy", SecurityLocksActivity.this.isSettingDecoy);
                        SecurityLocksActivity.this.startActivity(intent3);
                        SecurityLocksActivity.this.finish();
                        return;
                    default:
                        return;
                }
            }
        });
        BindSecurityLocks();
    }

    private void BindSecurityLocks() {
        this.securityLocksEntEntList = new SecurityLocksActivityMethods().GetSecurityCredentialsDetail(this);
        this.adapter = new SecurityLocksListAdapter(this, 17367043, this.securityLocksEntEntList);
        this.securityLocksListView.setAdapter(this.adapter);
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (configuration.orientation == 2) {
            this.rootViewGroup.setVisibility(View.GONE);
        } else if (configuration.orientation == 1) {
            this.rootViewGroup.setVisibility(View.INVISIBLE);
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


    public void onResume() {
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4 && !SecurityLocksCommon.IsFirstLogin) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, SettingActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }


    public void agreeDisguiseModeDialog() {
        SecurityLocksCommon.IsAppDeactive = false;
        Intent intent = new Intent(this, CalculatorPinSetting.class);
        intent.putExtra("from", "Cal");
        intent.putExtra("isconfirmdialog", this.isconfirmdialog);
        intent.putExtra("from", "isSettingDecoy");
        startActivity(intent);
        finish();
    }
}
