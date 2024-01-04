package com.example.vault.more;

import static com.example.vault.utilities.Common.AppPackageName;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.example.vault.R;
import com.example.vault.BaseActivity;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.tryattempt.TryAttemptActivity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securitylocks.SecurityLocksCommon;

public class MoreActivity extends BaseActivity {
    LinearLayout ll_More_TopBaar;
    LinearLayout ll_about;
    LinearLayout ll_dextopproduct;
    LinearLayout ll_hack_attempt;
    LinearLayout ll_license_agrement;
    LinearLayout ll_more_list;
    LinearLayout ll_moreproduct;
    LinearLayout ll_rate_and_review;
    LinearLayout ll_tell_a_friend;
    private SensorManager sensorManager;
    private Toolbar toolbar;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_more);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "More");
        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MoreActivity.this.btnBackonClick();
            }
        });
        this.ll_more_list = (LinearLayout) findViewById(R.id.ll_more_list);
        this.ll_More_TopBaar = (LinearLayout) findViewById(R.id.ll_More_TopBaar);
        this.ll_hack_attempt = (LinearLayout) findViewById(R.id.ll_hack_attempt);
        this.ll_rate_and_review = (LinearLayout) findViewById(R.id.ll_rate_and_review);
        this.ll_tell_a_friend = (LinearLayout) findViewById(R.id.ll_tell_friend_icon);
        this.ll_license_agrement = (LinearLayout) findViewById(R.id.ll_license_agrement);
        this.ll_about = (LinearLayout) findViewById(R.id.ll_about);
        this.ll_hack_attempt.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                MoreActivity.this.startActivity(new Intent(MoreActivity.this, TryAttemptActivity.class));
                MoreActivity.this.finish();
            }
        });
        this.ll_rate_and_review.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsRateReview = true;
                SecurityLocksCommon.IsAppDeactive = false;
                MoreActivity moreActivity = MoreActivity.this;
                StringBuilder sb = new StringBuilder();
                sb.append("market://details?id=");
                sb.append(AppPackageName);
                moreActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(sb.toString())));
            }
        });
        this.ll_tell_a_friend.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MoreCommonMethods.TellaFriendDialog(MoreActivity.this);
            }
        });
        this.ll_license_agrement.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                MoreActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.privacypolicyurl))));
            }
        });
        this.ll_about.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                MoreActivity.this.startActivity(new Intent(MoreActivity.this, AboutActivity.class));
                MoreActivity.this.finish();
            }
        });
    }

    public void btnBackonClick() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, MainiFeaturesActivity.class));
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
            startActivity(new Intent(this, MainiFeaturesActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
