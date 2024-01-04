package com.example.vault.securitylocks;

import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.vault.R;
import com.example.vault.BaseActivity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.recoveryofsecuritylocks.RecoveryOfCredentialsActivity;
import com.example.vault.securitylocks.SecurityLocksCommon.LoginOptions;
import com.example.vault.storageoption.SettingActivity;

public class ConfirmPasswordPinActivity extends BaseActivity {
    String LoginOption = "";
    public String PasswordOrPin = "";
    CheckBox cb_show_password_pin;
    TextView lblCancel;
    TextView lblConfirmPinOrPassword;
    TextView lblOk;
    TextView lblconfirmPasswordPintop;
    LinearLayout ll_Cancel;
    LinearLayout ll_ConfirmPasswordPinTopBaar;
    LinearLayout ll_Ok;
    LinearLayout ll_background;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    EditText txtConfirmPinOrPassword;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.confirm_password_pin_activity);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        this.ll_ConfirmPasswordPinTopBaar = (LinearLayout) findViewById(R.id.ll_ConfirmPasswordPinTopBaar);
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((int) R.string.lbl_Enter_password);
        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ConfirmPasswordPinActivity.this.btnBackonClick();
            }
        });
        this.txtConfirmPinOrPassword = (EditText) findViewById(R.id.txtconfirm_password_pin);
        this.txtConfirmPinOrPassword.setTypeface(createFromAsset);
        this.lblConfirmPinOrPassword = (TextView) findViewById(R.id.lblconfirm_password_pin);
        this.lblConfirmPinOrPassword.setTypeface(createFromAsset);
        this.lblconfirmPasswordPintop = (TextView) findViewById(R.id.lblconfirmPasswordPintop);
        this.lblconfirmPasswordPintop.setTypeface(createFromAsset);
        this.ll_Cancel = (LinearLayout) findViewById(R.id.ll_Cancel);
        this.ll_Ok = (LinearLayout) findViewById(R.id.ll_Ok);
        this.lblCancel = (TextView) findViewById(R.id.lblCancel);
        this.lblCancel.setTypeface(createFromAsset);
        this.lblOk = (TextView) findViewById(R.id.lblOk);
        this.lblOk.setTypeface(createFromAsset);
        this.cb_show_password_pin = (CheckBox) findViewById(R.id.cb_show_password_pin);
        this.cb_show_password_pin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                    if (LoginOptions.Pin.toString().equals(ConfirmPasswordPinActivity.this.LoginOption)) {
                        if (ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.getText().toString().length() > 0) {
                            int selectionStart = ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.getSelectionStart();
                            int selectionEnd = ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.getSelectionEnd();
                            ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.setInputType(2);
                            ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.setSelection(selectionStart, selectionEnd);
                        }
                    } else if (ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.getText().toString().length() > 0) {
                        int selectionStart2 = ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.getSelectionStart();
                        int selectionEnd2 = ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.getSelectionEnd();
                        ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.setSelection(selectionStart2, selectionEnd2);
                    }
                } else if (LoginOptions.Pin.toString().equals(ConfirmPasswordPinActivity.this.LoginOption)) {
                    if (ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.getText().toString().length() > 0) {
                        int selectionStart3 = ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.getSelectionStart();
                        int selectionEnd3 = ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.getSelectionEnd();
                        ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.setInputType(2);
                        ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.setSelection(selectionStart3, selectionEnd3);
                    }
                } else if (ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.getText().toString().length() > 0) {
                    int selectionStart4 = ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.getSelectionStart();
                    int selectionEnd4 = ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.getSelectionEnd();
                    ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ConfirmPasswordPinActivity.this.txtConfirmPinOrPassword.setSelection(selectionStart4, selectionEnd4);
                }
            }
        });
        this.ll_Cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                ConfirmPasswordPinActivity.this.startActivity(new Intent(ConfirmPasswordPinActivity.this, SettingActivity.class));
                ConfirmPasswordPinActivity.this.finish();
            }
        });
        this.ll_Ok.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ConfirmPasswordPinActivity.this.Ok();
            }
        });
        SecurityLocksSharedPreferences GetObject = SecurityLocksSharedPreferences.GetObject(this);
        this.LoginOption = GetObject.GetLoginType();
        this.PasswordOrPin = GetObject.GetSecurityCredential();
        if (LoginOptions.Pin.toString().equals(this.LoginOption)) {
            this.cb_show_password_pin.setText(R.string.lbl_show_pin);
            this.txtConfirmPinOrPassword.setInputType(2);
            this.txtConfirmPinOrPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            getSupportActionBar().setTitle((int) R.string.lbl_Enter_pin);
            this.lblconfirmPasswordPintop.setText(R.string.lbl_Enter_pin);
            this.lblConfirmPinOrPassword.setText(R.string.lblsetting_SecurityCredentials_ConfirmYourPin);
            return;
        }
        this.cb_show_password_pin.setText(R.string.lbl_show_password);
    }

    public void btnBackonClick() {
        SecurityLocksCommon.IsAppDeactive = false;
        SecurityLocksCommon.isBackupPasswordPin = false;
        startActivity(new Intent(this, SettingActivity.class));
        finish();
    }

    public void Ok() {
        Intent intent;
        Intent intent2;
        if (LoginOptions.Password.toString().equals(this.LoginOption)) {
            if (this.txtConfirmPinOrPassword.getText().toString().contentEquals(this.PasswordOrPin)) {
                SecurityLocksCommon.IsAppDeactive = false;
                if (SecurityLocksCommon.isBackupPasswordPin) {
                    SecurityLocksCommon.isBackupPasswordPin = false;
                    intent2 = new Intent(this, RecoveryOfCredentialsActivity.class);
                } else if (SecurityLocksCommon.isSettingDecoy) {
                    SecurityLocksCommon.isSettingDecoy = false;
                    intent2 = new Intent(this, SetPasswordActivity.class);
                    intent2.putExtra("LoginOption", "Password");
                    intent2.putExtra("isSettingDecoy", true);
                } else {
                    intent2 = new Intent(this, SecurityLocksActivity.class);
                }
                startActivity(intent2);
                finish();
                return;
            }
            this.lblConfirmPinOrPassword.setText(R.string.lblsetting_SecurityCredentials_Setpasword_Tryagain);
            this.txtConfirmPinOrPassword.setText("");
        } else if (!LoginOptions.Pin.toString().equals(this.LoginOption)) {
        } else {
            if (this.txtConfirmPinOrPassword.getText().toString().contentEquals(this.PasswordOrPin)) {
                SecurityLocksCommon.IsAppDeactive = false;
                if (SecurityLocksCommon.isBackupPasswordPin) {
                    SecurityLocksCommon.isBackupPasswordPin = false;
                    intent = new Intent(this, RecoveryOfCredentialsActivity.class);
                } else if (SecurityLocksCommon.isSettingDecoy) {
                    SecurityLocksCommon.isSettingDecoy = false;
                    intent = new Intent(this, SetPasswordActivity.class);
                    intent.putExtra("LoginOption", "Pin");
                    intent.putExtra("isSettingDecoy", true);
                } else {
                    intent = new Intent(this, SecurityLocksActivity.class);
                }
                startActivity(intent);
                finish();
                return;
            }
            this.lblConfirmPinOrPassword.setText(R.string.lblsetting_SecurityCredentials_Setpin_Tryagain);
            this.txtConfirmPinOrPassword.setText("");
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
        super.onPause();
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
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

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            SecurityLocksCommon.IsAppDeactive = false;
            SecurityLocksCommon.isBackupPasswordPin = false;
            startActivity(new Intent(this, SettingActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
