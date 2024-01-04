package com.example.vault.securitylocks;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Toast;
import com.example.vault.R;
import com.example.vault.BaseActivity;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.recoveryofsecuritylocks.RecoveryOfCredentialsMethods;
import com.example.vault.securitylocks.SecurityLocksCommon.LoginOptions;
import com.example.vault.storageoption.SettingActivity;
import com.example.vault.utilities.Common;

public class SetPasswordActivity extends BaseActivity {
    String LoginOption = "";
    public String _confirmPassword = "";
    public String _newPassword = "";
    CheckBox cb_show_password_pin;
    boolean isSettingDecoy = false;
    boolean isShowPassword = false;
    TextView lblCancel;
    TextView lblContinueOrDone;
    TextView lblnewpass;
    public TextView lbltop;
    LinearLayout ll_Cancel;
    LinearLayout ll_ContinueOrDone;
    LinearLayout ll_SetPasswordTopBaar;
    LinearLayout ll_background;
    SecurityLocksSharedPreferences securityCredentialsSharedPreferences;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    EditText txtnewpass;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.set_password_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.txtnewpass = (EditText) findViewById(R.id.txtnewpass);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.lblnewpass = (TextView) findViewById(R.id.lblnewpass);
        this.lbltop = (TextView) findViewById(R.id.lbltop);
        this.lbltop.setTypeface(createFromAsset);
        this.lblContinueOrDone = (TextView) findViewById(R.id.lblContinueOrDone);
        this.lblCancel = (TextView) findViewById(R.id.lblCancel);
        this.ll_SetPasswordTopBaar = (LinearLayout) findViewById(R.id.ll_SetPasswordTopBaar);
        this.ll_Cancel = (LinearLayout) findViewById(R.id.ll_Cancel);
        this.ll_ContinueOrDone = (LinearLayout) findViewById(R.id.ll_ContinueOrDone);
        this.ll_SetPasswordTopBaar.setBackgroundColor(getResources().getColor(R.color.ColorAppTheme));
        this.lblContinueOrDone.setTypeface(createFromAsset);
        this.lblContinueOrDone.setTextColor(getResources().getColor(R.color.black_color));
        this.lblCancel.setTypeface(createFromAsset);
        this.lblCancel.setTextColor(getResources().getColor(R.color.black_color));
        this.cb_show_password_pin = (CheckBox) findViewById(R.id.cb_show_password_pin);
        Intent intent = getIntent();
        this.isSettingDecoy = intent.getBooleanExtra("isSettingDecoy", false);
        this.LoginOption = intent.getStringExtra("LoginOption");
        this.securityCredentialsSharedPreferences = SecurityLocksSharedPreferences.GetObject(this);
        if (this.isSettingDecoy) {
            this.lblnewpass.setText("");
            this.txtnewpass.setText("");
            this.lblContinueOrDone.setText("");
            this._newPassword = "";
            this._confirmPassword = "";
            if (LoginOptions.Pin.toString().equals(this.LoginOption)) {
                this.txtnewpass.setInputType(2);
                this.txtnewpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                if (this.isSettingDecoy) {
                    this.lbltop.setText(R.string.lbl_set_decoy_pin);
                    this.lblnewpass.setText(R.string.lbl_enter_decoy_PIN);
                } else {
                    this.lbltop.setText(R.string.lblsetting_SecurityCredentials_SetyourPin);
                    this.lblnewpass.setText(R.string.lblsetting_SecurityCredentials_Newpin);
                }
            } else {
                this.txtnewpass.setInputType(1);
                this.txtnewpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                if (this.isSettingDecoy) {
                    this.lbltop.setText(R.string.lbl_set_decoy_password);
                    this.lblnewpass.setText(R.string.lbl_enter_decoy_password);
                } else {
                    this.lbltop.setText(R.string.lblsetting_SecurityCredentials_SetyourPassword);
                    this.lblnewpass.setText(R.string.lblsetting_SecurityCredentials_Newpassword);
                }
            }
        }
        this.ll_Cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Intent intent = new Intent(SetPasswordActivity.this, MainiFeaturesActivity.class);
                if (!SecurityLocksCommon.IsFirstLogin) {
                    intent = new Intent(SetPasswordActivity.this, SettingActivity.class);
                } else if (SetPasswordActivity.this.isSettingDecoy) {
                    SecurityLocksCommon.IsFirstLogin = false;
                    SetPasswordActivity setPasswordActivity = SetPasswordActivity.this;
                    setPasswordActivity.securityCredentialsSharedPreferences = SecurityLocksSharedPreferences.GetObject(setPasswordActivity);
                    SetPasswordActivity.this.securityCredentialsSharedPreferences.SetIsFirstLogin(Boolean.valueOf(false));
                    intent = new Intent(SetPasswordActivity.this, MainiFeaturesActivity.class);
                }
                SetPasswordActivity.this.startActivity(intent);
                SetPasswordActivity.this.overridePendingTransition(17432576, 17432577);
                SetPasswordActivity.this.finish();
            }
        });
        this.ll_ContinueOrDone.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (LoginOptions.Password.toString().equals(SetPasswordActivity.this.LoginOption)) {
                    SetPasswordActivity.this.SavePassword();
                } else if (LoginOptions.Pin.toString().equals(SetPasswordActivity.this.LoginOption)) {
                    SetPasswordActivity.this.SavePin();
                }
            }
        });
        this.txtnewpass.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (i3 > 0 && i3 < 4) {
                    if (LoginOptions.Pin.toString().equals(SetPasswordActivity.this.LoginOption)) {
                        SetPasswordActivity.this.lblnewpass.setText(R.string.lbl_Pin_Limit);
                    }
                    if (LoginOptions.Password.toString().equals(SetPasswordActivity.this.LoginOption)) {
                        SetPasswordActivity.this.lblnewpass.setText(R.string.lbl_Password_Limit);
                    }
                    SetPasswordActivity.this.lblContinueOrDone.setText("");
                }
                if (i3 < 1) {
                    if (LoginOptions.Pin.toString().equals(SetPasswordActivity.this.LoginOption) && SetPasswordActivity.this._newPassword.equals("")) {
                        if (SetPasswordActivity.this.isSettingDecoy) {
                            SetPasswordActivity.this.lblnewpass.setText(R.string.lbl_enter_decoy_password);
                        } else {
                            SetPasswordActivity.this.lblnewpass.setText(R.string.lblsetting_SecurityCredentials_Newpin);
                        }
                    }
                    if (LoginOptions.Password.toString().equals(SetPasswordActivity.this.LoginOption) && SetPasswordActivity.this._newPassword.equals("")) {
                        if (SetPasswordActivity.this.isSettingDecoy) {
                            SetPasswordActivity.this.lblnewpass.setText(R.string.lbl_enter_decoy_password);
                        } else {
                            SetPasswordActivity.this.lblnewpass.setText(R.string.lblsetting_SecurityCredentials_Newpassword);
                        }
                    }
                    SetPasswordActivity.this.lblContinueOrDone.setText("");
                }
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!SetPasswordActivity.this.isShowPassword) {
                    return;
                }
                if (LoginOptions.Pin.toString().equals(SetPasswordActivity.this.LoginOption)) {
                    if (SetPasswordActivity.this.txtnewpass.getText().toString().length() > 0) {
                        int selectionStart = SetPasswordActivity.this.txtnewpass.getSelectionStart();
                        int selectionEnd = SetPasswordActivity.this.txtnewpass.getSelectionEnd();
                        SetPasswordActivity.this.txtnewpass.setInputType(2);
                        SetPasswordActivity.this.txtnewpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        SetPasswordActivity.this.txtnewpass.setSelection(selectionStart, selectionEnd);
                    }
                } else if (SetPasswordActivity.this.txtnewpass.getText().toString().length() > 0) {
                    int selectionStart2 = SetPasswordActivity.this.txtnewpass.getSelectionStart();
                    int selectionEnd2 = SetPasswordActivity.this.txtnewpass.getSelectionEnd();
                    SetPasswordActivity.this.txtnewpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    SetPasswordActivity.this.txtnewpass.setSelection(selectionStart2, selectionEnd2);
                }
            }

            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 4 && editable.length() <= 16) {
                    if (LoginOptions.Pin.toString().equals(SetPasswordActivity.this.LoginOption)) {
                        if (!SetPasswordActivity.this._newPassword.equals("")) {
                            if (SetPasswordActivity.this.isSettingDecoy) {
                                SetPasswordActivity.this.lblnewpass.setText(R.string.lbl_confirm_decoy_pin);
                            } else {
                                SetPasswordActivity.this.lblnewpass.setText(R.string.lblsetting_SecurityCredentials_Confirmpin);
                            }
                        } else if (SetPasswordActivity.this.isSettingDecoy) {
                            SetPasswordActivity.this.lblnewpass.setText(R.string.lbl_enter_decoy_PIN);
                        } else {
                            SetPasswordActivity.this.lblnewpass.setText(R.string.lblsetting_SecurityCredentials_Newpin);
                        }
                    }
                    if (LoginOptions.Password.toString().equals(SetPasswordActivity.this.LoginOption)) {
                        if (!SetPasswordActivity.this._newPassword.equals("")) {
                            if (SetPasswordActivity.this.isSettingDecoy) {
                                SetPasswordActivity.this.lblnewpass.setText(R.string.lbl_confirm_decoy_password);
                            } else {
                                SetPasswordActivity.this.lblnewpass.setText(R.string.lblsetting_SecurityCredentials_Confirmpassword);
                            }
                        } else if (SetPasswordActivity.this.isSettingDecoy) {
                            SetPasswordActivity.this.lblnewpass.setText(R.string.lbl_enter_decoy_password);
                        } else {
                            SetPasswordActivity.this.lblnewpass.setText(R.string.lblsetting_SecurityCredentials_Newpassword);
                        }
                    }
                    if (editable.length() >= 4 && editable.length() <= 16) {
                        if (SetPasswordActivity.this._newPassword.equals("")) {
                            SetPasswordActivity.this.lblContinueOrDone.setText(R.string.lblsetting_SecurityCredentials_Setpattern_Continue);
                        } else {
                            SetPasswordActivity.this.lblContinueOrDone.setText(R.string.lblsetting_SecurityCredentials_Setpattern_Done);
                        }
                        if (SetPasswordActivity.this.isSettingDecoy && SetPasswordActivity.this.txtnewpass.getText().toString().equals(SetPasswordActivity.this.securityCredentialsSharedPreferences.GetSecurityCredential())) {
                            if (LoginOptions.Password.toString().equals(SetPasswordActivity.this.LoginOption)) {
                                SetPasswordActivity.this.lblnewpass.setText(R.string.toast_securitycredentias_set_decoy_fail_password);
                            } else if (LoginOptions.Pin.toString().equals(SetPasswordActivity.this.LoginOption)) {
                                SetPasswordActivity.this.lblnewpass.setText(R.string.toast_securitycredentias_set_decoy_fail_pin);
                            }
                        }
                    }
                }
                if (editable.length() > 16) {
                    if (LoginOptions.Pin.toString().equals(SetPasswordActivity.this.LoginOption)) {
                        SetPasswordActivity.this.lblnewpass.setText(R.string.lbl_pin_lenth_less_limit);
                    }
                    if (LoginOptions.Password.toString().equals(SetPasswordActivity.this.LoginOption)) {
                        SetPasswordActivity.this.lblnewpass.setText(R.string.lbl_password_lenth_less_limit);
                    }
                    SetPasswordActivity.this.lblContinueOrDone.setText("");
                }
            }
        });
        this.cb_show_password_pin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                    SetPasswordActivity.this.isShowPassword = true;
                    if (LoginOptions.Pin.toString().equals(SetPasswordActivity.this.LoginOption)) {
                        if (SetPasswordActivity.this.txtnewpass.getText().toString().length() > 0) {
                            int selectionStart = SetPasswordActivity.this.txtnewpass.getSelectionStart();
                            int selectionEnd = SetPasswordActivity.this.txtnewpass.getSelectionEnd();
                            SetPasswordActivity.this.txtnewpass.setInputType(2);
                            SetPasswordActivity.this.txtnewpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            SetPasswordActivity.this.txtnewpass.setSelection(selectionStart, selectionEnd);
                        }
                    } else if (SetPasswordActivity.this.txtnewpass.getText().toString().length() > 0) {
                        int selectionStart2 = SetPasswordActivity.this.txtnewpass.getSelectionStart();
                        int selectionEnd2 = SetPasswordActivity.this.txtnewpass.getSelectionEnd();
                        SetPasswordActivity.this.txtnewpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        SetPasswordActivity.this.txtnewpass.setSelection(selectionStart2, selectionEnd2);
                    }
                } else {
                    SetPasswordActivity.this.isShowPassword = false;
                    if (LoginOptions.Pin.toString().equals(SetPasswordActivity.this.LoginOption)) {
                        if (SetPasswordActivity.this.txtnewpass.getText().toString().length() > 0) {
                            int selectionStart3 = SetPasswordActivity.this.txtnewpass.getSelectionStart();
                            int selectionEnd3 = SetPasswordActivity.this.txtnewpass.getSelectionEnd();
                            SetPasswordActivity.this.txtnewpass.setInputType(2);
                            SetPasswordActivity.this.txtnewpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            SetPasswordActivity.this.txtnewpass.setSelection(selectionStart3, selectionEnd3);
                        }
                    } else if (SetPasswordActivity.this.txtnewpass.getText().toString().length() > 0) {
                        int selectionStart4 = SetPasswordActivity.this.txtnewpass.getSelectionStart();
                        int selectionEnd4 = SetPasswordActivity.this.txtnewpass.getSelectionEnd();
                        SetPasswordActivity.this.txtnewpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        SetPasswordActivity.this.txtnewpass.setSelection(selectionStart4, selectionEnd4);
                    }
                }
            }
        });
        if (LoginOptions.Pin.toString().equals(this.LoginOption)) {
            this.cb_show_password_pin.setText(R.string.lbl_show_pin);
            this.txtnewpass.setInputType(2);
            this.txtnewpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.lbltop.setText(R.string.lblsetting_SecurityCredentials_SetyourPin);
            this.lblnewpass.setText(R.string.lblsetting_SecurityCredentials_Newpin);
            return;
        }
        this.cb_show_password_pin.setText(R.string.lbl_show_password);
    }

    public void SavePassword() {
        this.securityCredentialsSharedPreferences = SecurityLocksSharedPreferences.GetObject(this);
        if (this.isSettingDecoy && this.txtnewpass.getText().toString().endsWith(this.securityCredentialsSharedPreferences.GetSecurityCredential())) {
            this.lblnewpass.setText(R.string.toast_securitycredentias_set_decoy_fail_password);
            this.lblContinueOrDone.setText("");
            this.lblnewpass.setText("");
            this.txtnewpass.setText("");
            this._newPassword = "";
            this._confirmPassword = "";
        } else if (this.txtnewpass.getText().length() <= 0) {
        } else {
            if (this.txtnewpass.getText().length() < 4) {
                Toast.makeText(this, R.string.lbl_Password_Limit, Toast.LENGTH_SHORT).show();
            } else if (this._newPassword.equals("")) {
                this._newPassword = this.txtnewpass.getText().toString();
                this.txtnewpass.setText("");
                if (this.isSettingDecoy) {
                    this.lblnewpass.setText(R.string.lbl_confirm_decoy_password);
                } else {
                    this.lblnewpass.setText(R.string.lblsetting_SecurityCredentials_Confirmpassword);
                }
                this.lblContinueOrDone.setText("");
            } else if (this._confirmPassword.equals("")) {
                this._confirmPassword = this.txtnewpass.getText().toString();
                if (this._confirmPassword.equals(this._newPassword)) {
                    this.securityCredentialsSharedPreferences.SetLoginType(LoginOptions.Password.toString());
                    if (this.isSettingDecoy) {
                        this.securityCredentialsSharedPreferences.SetDecoySecurityCredential(this.txtnewpass.getText().toString());
                        Toast.makeText(this, R.string.toast_securitycredentias_set_sucess_password_decoy, Toast.LENGTH_SHORT).show();
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent = new Intent(this, MainiFeaturesActivity.class);
                        if (SecurityLocksCommon.IsFirstLogin) {
                            SecurityLocksCommon.IsFirstLogin = false;
                            this.securityCredentialsSharedPreferences.SetIsFirstLogin(Boolean.valueOf(false));
                        } else {
                            intent = new Intent(this, SettingActivity.class);
                        }
                        startActivity(intent);
                        overridePendingTransition(17432576, 17432577);
                        finish();
                        return;
                    }
                    this.securityCredentialsSharedPreferences.SetSecurityCredential(this.txtnewpass.getText().toString());
                    Toast.makeText(this, R.string.toast_securitycredentias_set_sucess_password, Toast.LENGTH_SHORT).show();
                    if (this.securityCredentialsSharedPreferences.GetSecurityCredential().length() <= 0 || this.securityCredentialsSharedPreferences.GetEmail().length() <= 0) {
                        FirstTimeEmailDialog();
                    } else {
                        DecoySetPopup(false);
                    }
                } else {
                    Toast.makeText(this, R.string.lbl_Password_doesnt_match, Toast.LENGTH_SHORT).show();
                    this.txtnewpass.selectAll();
                    this._confirmPassword = "";
                    this.lblnewpass.setText(R.string.lbl_Password_doesnt_match);
                }
            }
        }
    }

    public void SavePin() {
        this.securityCredentialsSharedPreferences = SecurityLocksSharedPreferences.GetObject(this);
        if (this.isSettingDecoy && this.txtnewpass.getText().toString().endsWith(this.securityCredentialsSharedPreferences.GetSecurityCredential())) {
            this.lblnewpass.setText(R.string.toast_securitycredentias_set_decoy_fail_pin);
            this.lblContinueOrDone.setText("");
            this.lblnewpass.setText("");
            this.txtnewpass.setText("");
            this._newPassword = "";
            this._confirmPassword = "";
        } else if (this.txtnewpass.getText().length() <= 0) {
        } else {
            if (this.txtnewpass.getText().length() < 4) {
                Toast.makeText(this, R.string.lbl_Pin_Limit, Toast.LENGTH_SHORT).show();
            } else if (this._newPassword.equals("")) {
                this._newPassword = this.txtnewpass.getText().toString();
                this.txtnewpass.setText("");
                if (this.isSettingDecoy) {
                    this.lblnewpass.setText(R.string.lbl_confirm_decoy_pin);
                } else {
                    this.lblnewpass.setText(R.string.lblsetting_SecurityCredentials_Confirmpin);
                }
                this.lblContinueOrDone.setText("");
            } else if (this._confirmPassword.equals("")) {
                this._confirmPassword = this.txtnewpass.getText().toString();
                if (this._confirmPassword.equals(this._newPassword)) {
                    this.securityCredentialsSharedPreferences.SetLoginType(LoginOptions.Pin.toString());
                    if (this.isSettingDecoy) {
                        this.securityCredentialsSharedPreferences.SetDecoySecurityCredential(this.txtnewpass.getText().toString());
                        Toast.makeText(this, R.string.toast_securitycredentias_set_sucess_pin_decoy, Toast.LENGTH_SHORT).show();
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent = new Intent(this, MainiFeaturesActivity.class);
                        if (SecurityLocksCommon.IsFirstLogin) {
                            SecurityLocksCommon.IsFirstLogin = false;
                            this.securityCredentialsSharedPreferences.SetIsFirstLogin(Boolean.valueOf(false));
                        } else {
                            intent = new Intent(this, SettingActivity.class);
                        }
                        startActivity(intent);
                        overridePendingTransition(17432576, 17432577);
                        finish();
                        return;
                    }
                    this.securityCredentialsSharedPreferences.SetSecurityCredential(this.txtnewpass.getText().toString());
                    Toast.makeText(this, R.string.toast_securitycredentias_set_sucess_pin, Toast.LENGTH_SHORT).show();
                    if (this.securityCredentialsSharedPreferences.GetSecurityCredential().length() <= 0 || this.securityCredentialsSharedPreferences.GetEmail().length() <= 0) {
                        FirstTimeEmailDialog();
                    } else {
                        DecoySetPopup(true);
                    }
                } else {
                    Toast.makeText(this, R.string.lbl_Pin_doesnt_match, Toast.LENGTH_SHORT).show();
                    this.txtnewpass.selectAll();
                    this._confirmPassword = "";
                    this.lblnewpass.setText(R.string.lbl_Pin_doesnt_match);
                }
            }
        }
    }

    public void DecoySetPopup(final boolean z) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.confirmation_message_box);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
        textView.setTypeface(Typeface.createFromAsset(getAssets(), "ebrima.ttf"));
        if (z) {
            textView.setText(R.string.lbl_msg_want_to_set_decoy_pin_ornot);
        } else {
            textView.setText(R.string.lbl_msg_want_to_set_decoy_pas_ornot);
        }
        TextView textView2 = (TextView) dialog.findViewById(R.id.lbl_Cancel);
        ((TextView) dialog.findViewById(R.id.lbl_Ok)).setText("Yes");
        textView2.setText("No");
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SetPasswordActivity setPasswordActivity = SetPasswordActivity.this;
                setPasswordActivity.isSettingDecoy = true;
                setPasswordActivity.lblnewpass.setText("");
                SetPasswordActivity.this.txtnewpass.setText("");
                SetPasswordActivity.this.lblContinueOrDone.setText("");
                SetPasswordActivity setPasswordActivity2 = SetPasswordActivity.this;
                setPasswordActivity2._newPassword = "";
                setPasswordActivity2._confirmPassword = "";
                if (!z) {
                    setPasswordActivity2.LoginOption = "Password";
                } else {
                    setPasswordActivity2.LoginOption = "Pin";
                }
                if (LoginOptions.Pin.toString().equals(SetPasswordActivity.this.LoginOption)) {
                    SetPasswordActivity.this.txtnewpass.setInputType(2);
                    SetPasswordActivity.this.txtnewpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    if (SetPasswordActivity.this.isSettingDecoy) {
                        SetPasswordActivity.this.lbltop.setText(R.string.lbl_set_decoy_pin);
                        SetPasswordActivity.this.lblnewpass.setText(R.string.lbl_enter_decoy_PIN);
                    } else {
                        SetPasswordActivity.this.lbltop.setText(R.string.lblsetting_SecurityCredentials_SetyourPin);
                        SetPasswordActivity.this.lblnewpass.setText(R.string.lblsetting_SecurityCredentials_Newpin);
                    }
                } else {
                    SetPasswordActivity.this.txtnewpass.setInputType(1);
                    SetPasswordActivity.this.txtnewpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    if (SetPasswordActivity.this.isSettingDecoy) {
                        SetPasswordActivity.this.lbltop.setText(R.string.lbl_set_decoy_password);
                        SetPasswordActivity.this.lblnewpass.setText(R.string.lbl_enter_decoy_password);
                    } else {
                        SetPasswordActivity.this.lbltop.setText(R.string.lblsetting_SecurityCredentials_SetyourPassword);
                        SetPasswordActivity.this.lblnewpass.setText(R.string.lblsetting_SecurityCredentials_Newpassword);
                    }
                }
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Intent intent = new Intent(SetPasswordActivity.this, MainiFeaturesActivity.class);
                if (SecurityLocksCommon.IsFirstLogin) {
                    SecurityLocksCommon.IsFirstLogin = false;
                    SetPasswordActivity.this.securityCredentialsSharedPreferences.SetIsFirstLogin(Boolean.valueOf(false));
                } else {
                    intent = new Intent(SetPasswordActivity.this, SettingActivity.class);
                }
                SetPasswordActivity.this.startActivity(intent);
                SetPasswordActivity.this.overridePendingTransition(17432576, 17432577);
                SetPasswordActivity.this.finish();
                dialog.dismiss();
            }
        });
        dialog.show();
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
        if (!SecurityLocksCommon.IsFirstLogin) {
            if (AccelerometerManager.isSupported(this)) {
                AccelerometerManager.startListening(this);
            }
            SensorManager sensorManager2 = this.sensorManager;
            sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
        }
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        Intent intent;
        if (i == 4) {
            SecurityLocksCommon.IsAppDeactive = false;
            if (SecurityLocksCommon.IsFirstLogin) {
                intent = new Intent(this, SecurityLocksActivity.class);
            } else {
                intent = new Intent(this, SettingActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(17432576, 17432577);
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void FirstTimeEmailDialog() {
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.backup_email_popup);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        final EditText editText = (EditText) dialog.findViewById(R.id.txtemailid);
        TextView textView = (TextView) dialog.findViewById(R.id.lbl_email_warning_title);
        TextView textView2 = (TextView) dialog.findViewById(R.id.lbl_email_warning_desc);
        TextView textView3 = (TextView) dialog.findViewById(R.id.lblSave);
        TextView textView4 = (TextView) dialog.findViewById(R.id.lblSkip);
        ((TextView) dialog.findViewById(R.id.lblEmail_popup_title)).setTypeface(createFromAsset);
        editText.setTypeface(createFromAsset);
        textView.setTypeface(createFromAsset);
        textView2.setTypeface(createFromAsset);
        textView3.setTypeface(createFromAsset);
        textView4.setTypeface(createFromAsset);
        ((LinearLayout) dialog.findViewById(R.id.ll_Save)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (editText.getText().length() > 0) {
                    String obj = editText.getText().toString();
                    if (new RecoveryOfCredentialsMethods().isEmailValid(obj)) {
                        SetPasswordActivity.this.securityCredentialsSharedPreferences.SetEmail(obj);
                        SetPasswordActivity.this.securityCredentialsSharedPreferences.SetShowFirstTimeEmailPopup(Boolean.valueOf(false));
                        SecurityLocksCommon.IsAppDeactive = false;
                        Toast.makeText(SetPasswordActivity.this, R.string.toast_Email_Saved, Toast.LENGTH_LONG).show();
                        SetPasswordActivity.this.DecoySetPopup(false);
                        dialog.dismiss();
                        Common.loginCount = 2;
                        SetPasswordActivity.this.securityCredentialsSharedPreferences.SetRateCount(Common.loginCount);
                        return;
                    }
                    Toast.makeText(SetPasswordActivity.this, R.string.toast_Invalid_Email, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(SetPasswordActivity.this, R.string.toast_Enter_Email, Toast.LENGTH_SHORT).show();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Skip)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SetPasswordActivity.this.securityCredentialsSharedPreferences.SetShowFirstTimeEmailPopup(Boolean.valueOf(false));
                SecurityLocksCommon.IsAppDeactive = false;
                Toast.makeText(SetPasswordActivity.this, R.string.toast_Skip, Toast.LENGTH_LONG).show();
                SetPasswordActivity.this.DecoySetPopup(false);
                dialog.dismiss();
                Common.loginCount = 1;
                SetPasswordActivity.this.securityCredentialsSharedPreferences.SetRateCount(Common.loginCount);
            }
        });
        dialog.show();
    }
}
