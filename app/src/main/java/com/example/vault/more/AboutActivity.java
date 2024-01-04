package com.example.vault.more;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.vault.R;
import com.example.vault.BaseActivity;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securitylocks.SecurityLocksCommon;

import org.apache.http.protocol.HTTP;

public class AboutActivity extends BaseActivity  implements View.OnClickListener{
    ImageView imginsta;
    ImageView imgfacebook;
    ImageView imgtweet, ib_back;
    LinearLayout ll_support;
    LinearLayout ll_visit;
    private SensorManager sensorManager;


    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.about_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        getWindow().addFlags(128);

        this.ib_back = findViewById(R.id.ib_back);
        this.ll_visit = (LinearLayout) findViewById(R.id.ll_visit);
        this.ll_support = (LinearLayout) findViewById(R.id.ll_support);
        this.imgfacebook = (ImageView) findViewById(R.id.imgfacebook);
        this.imgtweet = (ImageView) findViewById(R.id.imgtweet);
        this.imginsta = (ImageView) findViewById(R.id.imginsta);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    }
    @Override
    public void onClick(View v) {
        if(v== ib_back){
            AboutActivity.this.btnBackonClick();
        } else if (v== ll_visit) {
            SecurityLocksCommon.IsAppDeactive = false;
            AboutActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.visitweb))));

        }else if (v== ll_support) {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType(HTTP.PLAIN_TEXT_TYPE);
            intent.putExtra("android.intent.extra.EMAIL", new String[]{getString(R.string.Support_value)});
            intent.putExtra("android.intent.extra.SUBJECT", "Calculator Vault Android");
            intent.putExtra("android.intent.extra.TEXT", "");
            try {
                SecurityLocksCommon.IsAppDeactive = false;
                AboutActivity.this.startActivity(Intent.createChooser(intent, "Support via email..."));
            } catch (ActivityNotFoundException unused) {
            }
        }else if (v== imgfacebook) {
            SecurityLocksCommon.IsAppDeactive = false;
            AboutActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.facebookurl))));

        }else if (v== imgtweet) {
            SecurityLocksCommon.IsAppDeactive = false;
            AboutActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.twitterurl))));

        }else if (v== imginsta) {
            SecurityLocksCommon.IsAppDeactive = false;
            AboutActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.gplusurl))));

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

    public void btnBackonClick() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(getApplicationContext(), MainiFeaturesActivity.class));
        finish();
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
        SecurityLocksCommon.IsAppDeactive = true;
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
//            SecurityLocksCommon.IsAppDeactive = false;
//            startActivity(new Intent(getApplicationContext(), MoreActivity.class));
//            finish();
            AboutActivity.this.btnBackonClick();

        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AboutActivity.this.btnBackonClick();

    }



}
