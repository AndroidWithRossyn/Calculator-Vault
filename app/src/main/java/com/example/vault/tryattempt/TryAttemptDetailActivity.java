package com.example.vault.tryattempt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import com.example.vault.R;
import com.example.vault.tryattempt.model.TryAttemptEntity;
import com.example.vault.panicswitch.AccelerometerListener;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.securitylocks.SecurityLocksCommon.LoginOptions;

public class TryAttemptDetailActivity extends Activity implements AccelerometerListener, SensorEventListener, SimpleGestureFilter.SimpleGestureListener {
    String DateTime = "";
    String TryImagePath = "";
    int Position = 0;
    String WrongPass = "";
    private SimpleGestureFilter detector;
    ArrayList<TryAttemptEntity> tryAttemptEntities;
    ImageView imgtryattempt;
    LinearLayout ll_HackAttemptDetailTopBaar;
    LinearLayout ll_background;
    private SensorManager sensorManager;
    TextView txtattempttime;
    TextView txtwrongpass;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onDoubleTap() {
    }


    @SuppressLint("MissingInflatedId")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.tryattempt_detail_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.detector = new SimpleGestureFilter(this, this);
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.imgtryattempt = (ImageView) findViewById(R.id._imgtryattempt);
        this.txtwrongpass = (TextView) findViewById(R.id.txtwrongpass);
        this.txtattempttime = (TextView) findViewById(R.id.txtattempttime);
        this.tryAttemptEntities = TryAttemptsSharedPreferences.GetObject(getApplicationContext()).GetTryAttemptObject();
        Intent intent = getIntent();
        this.TryImagePath = intent.getStringExtra("HackerImagePath");
        SetHackerImageToImageView(this.TryImagePath);
        this.WrongPass = intent.getStringExtra("WrongPass");
        this.DateTime = intent.getStringExtra("DateTime");
        this.Position = intent.getIntExtra("Position", 0);
        if (LoginOptions.Password.toString().equals(((TryAttemptEntity) this.tryAttemptEntities.get(this.Position)).GetLoginOption().toString())) {
            TextView textView = this.txtwrongpass;
            StringBuilder sb = new StringBuilder();
            sb.append("Wrong Password: ");
            sb.append(((TryAttemptEntity) this.tryAttemptEntities.get(this.Position)).GetWrongPassword().toString());
            textView.setText(sb.toString());
        } else if (LoginOptions.Pin.toString().equals(((TryAttemptEntity) this.tryAttemptEntities.get(this.Position)).GetLoginOption().toString())) {
            TextView textView2 = this.txtwrongpass;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Wrong PIN: ");
            sb2.append(((TryAttemptEntity) this.tryAttemptEntities.get(this.Position)).GetWrongPassword().toString());
            textView2.setText(sb2.toString());
        } else if (LoginOptions.Pattern.toString().equals(((TryAttemptEntity) this.tryAttemptEntities.get(this.Position)).GetLoginOption().toString())) {
            TextView textView3 = this.txtwrongpass;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Wrong Pattern: ");
            sb3.append(((TryAttemptEntity) this.tryAttemptEntities.get(this.Position)).GetWrongPassword().toString());
            textView3.setText(sb3.toString());
        }
        this.DateTime = this.DateTime.replace("GMT+05:00", "");
        if (this.DateTime.length() > 0) {
            this.txtattempttime.setText(this.DateTime);
        }
    }

    public void btnBackonClick(View view) {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, TryAttemptActivity.class));
        finish();
    }

    public void SetHackerImageToImageView(String str) {
        try {
            this.imgtryattempt.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(new File(str)), null, null));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        this.detector.onTouchEvent(motionEvent);
        return super.dispatchTouchEvent(motionEvent);
    }

    public void onSwipe(int i) {
        switch (i) {
            case 3:
                if (this.Position == 0) {
                    this.Position = this.tryAttemptEntities.size();
                }
                int i2 = this.Position;
                if (i2 > 0) {
                    this.Position = i2 - 1;
                    this.TryImagePath = ((TryAttemptEntity) this.tryAttemptEntities.get(this.Position)).GetImagePath();
                    this.WrongPass = ((TryAttemptEntity) this.tryAttemptEntities.get(this.Position)).GetWrongPassword();
                    this.DateTime = ((TryAttemptEntity) this.tryAttemptEntities.get(this.Position)).GetTryAttemptTime();
                    this.DateTime = this.DateTime.replace("GMT+05:00", "");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            TryAttemptDetailActivity tryAttemptDetailActivity = TryAttemptDetailActivity.this;
                            tryAttemptDetailActivity.SetHackerImageToImageView(tryAttemptDetailActivity.TryImagePath);
                            if (TryAttemptDetailActivity.this.WrongPass.length() > 0) {
                                if (LoginOptions.Password.toString().equals(((TryAttemptEntity) TryAttemptDetailActivity.this.tryAttemptEntities.get(TryAttemptDetailActivity.this.Position)).GetLoginOption().toString())) {
                                    TextView textView = TryAttemptDetailActivity.this.txtwrongpass;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Wrong Password: ");
                                    sb.append(((TryAttemptEntity) TryAttemptDetailActivity.this.tryAttemptEntities.get(TryAttemptDetailActivity.this.Position)).GetWrongPassword().toString());
                                    textView.setText(sb.toString());
                                } else if (LoginOptions.Pin.toString().equals(((TryAttemptEntity) TryAttemptDetailActivity.this.tryAttemptEntities.get(TryAttemptDetailActivity.this.Position)).GetLoginOption().toString())) {
                                    TextView textView2 = TryAttemptDetailActivity.this.txtwrongpass;
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append("Wrong PIN: ");
                                    sb2.append(((TryAttemptEntity) TryAttemptDetailActivity.this.tryAttemptEntities.get(TryAttemptDetailActivity.this.Position)).GetWrongPassword().toString());
                                    textView2.setText(sb2.toString());
                                } else if (LoginOptions.Pattern.toString().equals(((TryAttemptEntity) TryAttemptDetailActivity.this.tryAttemptEntities.get(TryAttemptDetailActivity.this.Position)).GetLoginOption().toString())) {
                                    TextView textView3 = TryAttemptDetailActivity.this.txtwrongpass;
                                    StringBuilder sb3 = new StringBuilder();
                                    sb3.append("Wrong Pattern: ");
                                    sb3.append(((TryAttemptEntity) TryAttemptDetailActivity.this.tryAttemptEntities.get(TryAttemptDetailActivity.this.Position)).GetWrongPassword().toString());
                                    textView3.setText(sb3.toString());
                                }
                            }
                            if (TryAttemptDetailActivity.this.DateTime.length() > 0) {
                                TryAttemptDetailActivity.this.txtattempttime.setText(TryAttemptDetailActivity.this.DateTime);
                            }
                        }
                    });
                    return;
                }
                return;
            case 4:
                if (this.Position == this.tryAttemptEntities.size()) {
                    this.Position = 0;
                }
                int i3 = this.Position;
                if (i3 >= 0 && i3 < this.tryAttemptEntities.size()) {
                    this.TryImagePath = ((TryAttemptEntity) this.tryAttemptEntities.get(this.Position)).GetImagePath();
                    this.WrongPass = ((TryAttemptEntity) this.tryAttemptEntities.get(this.Position)).GetWrongPassword();
                    this.DateTime = ((TryAttemptEntity) this.tryAttemptEntities.get(this.Position)).GetTryAttemptTime();
                    this.DateTime = this.DateTime.replace("GMT+05:00", "");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            TryAttemptDetailActivity tryAttemptDetailActivity = TryAttemptDetailActivity.this;
                            tryAttemptDetailActivity.SetHackerImageToImageView(tryAttemptDetailActivity.TryImagePath);
                            if (TryAttemptDetailActivity.this.WrongPass.length() > 0) {
                                TryAttemptDetailActivity.this.txtwrongpass.setText(TryAttemptDetailActivity.this.WrongPass);
                            }
                            if (TryAttemptDetailActivity.this.DateTime.length() > 0) {
                                TryAttemptDetailActivity.this.txtattempttime.setText(TryAttemptDetailActivity.this.DateTime);
                            }
                        }
                    });
                    this.Position++;
                    return;
                }
                return;
            default:
                return;
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
            System.exit(0);
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
            startActivity(new Intent(getApplicationContext(), TryAttemptActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
