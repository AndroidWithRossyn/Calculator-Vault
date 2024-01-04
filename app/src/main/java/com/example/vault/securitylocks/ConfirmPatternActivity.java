package com.example.vault.securitylocks;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.ArrayList;
import com.example.vault.R;
import com.example.vault.panicswitch.AccelerometerListener;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.storageoption.SettingActivity;

public class ConfirmPatternActivity extends Activity implements AccelerometerListener, SensorEventListener {
    public static final String BUNDLE_GRID_LENGTH = "grid_length";
    public static final String BUNDLE_HIGHLIGHT = "highlight";
    public static final String BUNDLE_PATTERN = "pattern";
    public static final String BUNDLE_PATTERN_MAX = "pattern_max";
    public static final String BUNDLE_PATTERN_MIN = "pattern_min";
    public static final int DIALOG_EXITED_HARD = 1;
    public static final int DIALOG_SEPARATION_WARNING = 0;
    public static TextView lblConfirmpattern;
    protected Button PrintPattern;
    LinearLayout ll_background;
    LinearLayout ll_confirm_pattern_topbaar_bg;
    protected Button mGenerateButton;
    protected int mGridLength;
    protected String mHighlightMode;
    protected int mPatternMax;
    protected int mPatternMin;
    protected ConfirmLockPatternView mPatternView;
    protected ToggleButton mPracticeToggle;
    protected Button mSecuritySettingsButton;
    protected boolean mTactileFeedback;
    private SensorManager sensorManager;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        SecurityLocksCommon.PatternPassword = SecurityLocksSharedPreferences.GetObject(this).GetSecurityCredential();
        setContentView(R.layout.confirm_pattern_activity);
        this.ll_confirm_pattern_topbaar_bg = (LinearLayout) findViewById(R.id.ll_confirm_pattern_topbaar_bg);
        this.mPatternView = (ConfirmLockPatternView) findViewById(R.id.pattern_view);
        lblConfirmpattern = (TextView) findViewById(R.id.txtdrawpattern);
        lblConfirmpattern.setTypeface(createFromAsset);
        lblConfirmpattern.setTextColor(getResources().getColor(R.color.ColorAppTheme));
        lblConfirmpattern.setText(R.string.lblsetting_SecurityCredentials_ConfirmYourPattern);
        this.mPatternView.setPracticeMode(true);
        this.mPatternView.invalidate();
        if (bundle != null) {
            this.mPatternView.setPattern(bundle.<Point>getParcelableArrayList("pattern"));
        }
    }

    public void btnBackonClick(View view) {
        SecurityLocksCommon.IsConfirmPatternActivity = false;
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
        SecurityLocksCommon.IsConfirmPatternActivity = false;
        SecurityLocksCommon.isBackupPasswordPin = false;
        SecurityLocksCommon.isBackupPattern = false;
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

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("grid_length", this.mGridLength);
        bundle.putInt("pattern_max", this.mPatternMax);
        bundle.putInt("pattern_min", this.mPatternMin);
        bundle.putString("highlight", this.mHighlightMode);
        bundle.putParcelableArrayList("pattern", new ArrayList(this.mPatternView.getPattern()));
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            SecurityLocksCommon.IsConfirmPatternActivity = false;
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, SettingActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
