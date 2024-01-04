package com.example.vault.calculator;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.vault.R;
import com.example.vault.BaseActivity;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.recoveryofsecuritylocks.RecoveryOfCredentialsActivity;
import com.example.vault.securitylocks.SecurityLocksActivity;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.securitylocks.SecurityLocksCommon.LoginOptions;
import com.example.vault.securitylocks.SecurityLocksSharedPreferences;
import com.example.vault.storageoption.SettingActivity;



public class CalculatorPinSetting extends BaseActivity {

    String LoginOption = "";
    Button btnDone;
    StringBuilder builder = new StringBuilder();
    public CalculatorPin calculator;
    StringBuilder compringString = new StringBuilder();
    private String confCalPIN = "";
    private String confirmCalPin = "";
    private String decoyConfirmPIN = "";
    private String decoyPIN = "";
    public TextView displayPrimary;
    private TextView displaySecondary;
    String from;
    public HorizontalScrollView hsv;
    boolean isPinNotMatch = false;
    boolean isSettingDecoy = false;
    private boolean isconfirmDisguiseMode = false;
    String isconfirmdialog = "";
    private String newCalPin = "";
    SecurityLocksSharedPreferences securityCredentialsSharedPreferences;
    private SensorManager sensorManager;
    public TextView tvPin;


    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onCreate(Bundle bundle) {
        char c;
        char c2;
        Bundle bundle2 = bundle;
        super.onCreate(bundle);
        SecurityLocksCommon.IsAppDeactive = false;

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (!defaultSharedPreferences.getBoolean("pref_dark", false)) {
            String string = defaultSharedPreferences.getString("pref_theme", "0");
            switch (string.hashCode()) {
                case 48:
                    if (string.equals("0")) {
                        c = 0;
                        break;
                    }
                case 49:
                    if (string.equals("1")) {
                        c = 1;
                        break;
                    }
                case 50:
                    if (string.equals("2")) {
                        c = 2;
                        break;
                    }
                case 51:
                    if (string.equals("3")) {
                        c = 3;
                        break;
                    }
                case 52:
                    if (string.equals("4")) {
                        c = 4;
                        break;
                    }
                case 53:
                    if (string.equals("5")) {
                        c = 5;
                        break;
                    }
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    setTheme(R.style.AppTheme_Light_Blue);
                    break;
                case 1:
                    setTheme(R.style.AppTheme_Light_Cyan);
                    break;
                case 2:
                    setTheme(R.style.AppTheme_Light_Gray);
                    break;
                case 3:
                    setTheme(R.style.AppTheme_Light_Green);
                    break;
                case 4:
                    setTheme(R.style.AppTheme_Light_Purple);
                    break;
                case 5:
                    setTheme(R.style.AppTheme_Light_Red);
                    break;
            }
        } else {
            String string2 = defaultSharedPreferences.getString("pref_theme", "0");
            switch (string2.hashCode()) {
                case 48:
                    if (string2.equals("0")) {
                        c2 = 0;
                        break;
                    }
                case 49:
                    if (string2.equals("1")) {
                        c2 = 1;
                        break;
                    }
                case 50:
                    if (string2.equals("2")) {
                        c2 = 2;
                        break;
                    }
                case 51:
                    if (string2.equals("3")) {
                        c2 = 3;
                        break;
                    }
                case 52:
                    if (string2.equals("4")) {
                        c2 = 4;
                        break;
                    }
                case 53:
                    if (string2.equals("5")) {
                        c2 = 5;
                        break;
                    }
                default:
                    c2 = 65535;
                    break;
            }
            switch (c2) {
                case 0:
                    setTheme(R.style.AppTheme_Dark_Blue);
                    break;
                case 1:
                    setTheme(R.style.AppTheme_Dark_Cyan);
                    break;
                case 2:
                    setTheme(R.style.AppTheme_Dark_Gray);
                    break;
                case 3:
                    setTheme(R.style.AppTheme_Dark_Green);
                    break;
                case 4:
                    setTheme(R.style.AppTheme_Dark_Purple);
                    break;
                case 5:
                    setTheme(R.style.AppTheme_Dark_Red);
                    break;
            }
        }
        setContentView((int) R.layout.activity_calculator_pin_setting);


        this.securityCredentialsSharedPreferences = SecurityLocksSharedPreferences.GetObject(this);
        this.LoginOption = this.securityCredentialsSharedPreferences.GetLoginType();
        this.tvPin = (TextView) findViewById(R.id.tv_pin);
        this.displayPrimary = (TextView) findViewById(R.id.display_primary);
        this.displaySecondary = (TextView) findViewById(R.id.display_secondary);
        this.hsv = (HorizontalScrollView) findViewById(R.id.display_hsv);
        Bundle extras = getIntent().getExtras();
        if (getIntent().getStringExtra("isconfirmdialog") != null) {
            this.isconfirmdialog = getIntent().getStringExtra("isconfirmdialog");
        }
        if (extras != null) {
            this.from = extras.getString("from");
            String str = this.from;
            if (str == null || !str.equals("SettingFragment")) {
                this.tvPin.setText(R.string.cal_pin_text);
            } else if (this.securityCredentialsSharedPreferences.isGetCalModeEnable()) {
                this.tvPin.setText(R.string.cal_pin_text_conf);
                this.isconfirmDisguiseMode = true;
            } else {
                this.tvPin.setText(R.string.cal_pin_text);
            }
        } else {
            this.tvPin.setText(R.string.cal_pin_text);
        }
        TextView[] textViewArr = {(TextView) findViewById(R.id.button_0), (TextView) findViewById(R.id.button_1), (TextView) findViewById(R.id.button_2), (TextView) findViewById(R.id.button_3), (TextView) findViewById(R.id.button_4), (TextView) findViewById(R.id.button_5), (TextView) findViewById(R.id.button_6), (TextView) findViewById(R.id.button_7), (TextView) findViewById(R.id.button_8), (TextView) findViewById(R.id.button_9)};
        for (int i = 0; i < textViewArr.length; i++) {
            final String str2 = (String) textViewArr[i].getText();
            textViewArr[i].setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    CalculatorPinSetting.this.calculator.digit(str2.charAt(0));
                    CalculatorPinSetting calculatorPinSetting = CalculatorPinSetting.this;
                    StringBuilder sb = calculatorPinSetting.builder;
                    sb.append(str2.charAt(0));
                    calculatorPinSetting.compringString = sb;
                    if (CalculatorPinSetting.this.isPinNotMatch) {
                        CalculatorPinSetting.this.tvPin.setText(R.string.cal_pin_text_confirm);
                    }
                }
            });
        }
        TextView[] textViewArr2 = {(TextView) findViewById(R.id.button_sin), (TextView) findViewById(R.id.button_cos), (TextView) findViewById(R.id.button_tan), (TextView) findViewById(R.id.button_ln), (TextView) findViewById(R.id.button_log), (TextView) findViewById(R.id.button_factorial), (TextView) findViewById(R.id.button_pi), (TextView) findViewById(R.id.button_e), (TextView) findViewById(R.id.button_exponent), (TextView) findViewById(R.id.button_start_parenthesis), (TextView) findViewById(R.id.button_end_parenthesis), (TextView) findViewById(R.id.button_square_root), (TextView) findViewById(R.id.button_add), (TextView) findViewById(R.id.button_subtract), (TextView) findViewById(R.id.button_multiply), (TextView) findViewById(R.id.button_divide), (TextView) findViewById(R.id.button_decimal), (TextView) findViewById(R.id.button_equals)};
        for (int i2 = 0; i2 < textViewArr2.length; i2++) {
            final String str3 = (String) textViewArr2[i2].getText();
            textViewArr2[i2].setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (str3.equals("sin")) {
                        CalculatorPinSetting.this.calculator.opNum('s');
                    }
                    if (str3.equals("cos")) {
                        CalculatorPinSetting.this.calculator.opNum('c');
                    }
                    if (str3.equals("tan")) {
                        CalculatorPinSetting.this.calculator.opNum('t');
                    }
                    if (str3.equals("ln")) {
                        CalculatorPinSetting.this.calculator.opNum('n');
                    }
                    if (str3.equals("log")) {
                        CalculatorPinSetting.this.calculator.opNum('l');
                    }
                    if (str3.equals("-/+")) {
                        Toast.makeText(CalculatorPinSetting.this, R.string.disable_operation_cal, Toast.LENGTH_SHORT).show();
                    }
                    if (str3.equals("π")) {
                        CalculatorPinSetting.this.calculator.num((char) 960);
                    }
                    if (str3.equals("e")) {
                        CalculatorPinSetting.this.calculator.num('e');
                    }
                    if (str3.equals("^")) {
                        CalculatorPinSetting.this.calculator.numOpNum('^');
                    }
                    if (str3.equals("%")) {
//                        CalculatorPinSetting.this.ResetorSetCalPin();
                        Toast.makeText(CalculatorPinSetting.this, R.string.disable_operation_cal, Toast.LENGTH_SHORT).show();
                        CalculatorPinSetting.this.builder.setLength(0);
                        CalculatorPinSetting.this.compringString.setLength(0);
                    }
                    if (str3.equals("(")) {
                        CalculatorPinSetting.this.calculator.parenthesisLeft();
                    }
                    if (str3.equals(")")) {
                        CalculatorPinSetting.this.calculator.parenthesisRight();
                    }
                    if (str3.equals("C")) {
                        if (!CalculatorPinSetting.this.displayPrimary.getText().toString().trim().equals("")) {
                            View findViewById = CalculatorPinSetting.this.findViewById(R.id.display_overlay);
                            if (VERSION.SDK_INT >= 21) {
                                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(findViewById, findViewById.getMeasuredWidth() / 2, findViewById.getMeasuredHeight(), 0.0f, (float) ((int) Math.hypot((double) findViewById.getWidth(), (double) findViewById.getHeight())));
                                createCircularReveal.setDuration(300);
                                createCircularReveal.addListener(new AnimatorListener() {
                                    public void onAnimationRepeat(Animator animator) {
                                    }

                                    public void onAnimationStart(Animator animator) {
                                    }

                                    public void onAnimationEnd(Animator animator) {
                                        CalculatorPinSetting.this.calculator.setText("");
                                    }

                                    public void onAnimationCancel(Animator animator) {
                                        CalculatorPinSetting.this.calculator.setText("");
                                    }
                                });
                                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(findViewById, "alpha", new float[]{0.0f});
                                ofFloat.setInterpolator(new DecelerateInterpolator());
                                ofFloat.setDuration(200);
                                AnimatorSet animatorSet = new AnimatorSet();
                                animatorSet.playSequentially(new Animator[]{createCircularReveal, ofFloat});
                                findViewById.setAlpha(1.0f);
                                animatorSet.start();
                            } else {
                                CalculatorPinSetting.this.calculator.setText("");
                            }
                        }
                    }
                    if (str3.equals("÷")) {
                        Toast.makeText(CalculatorPinSetting.this, R.string.disable_operation_cal, Toast.LENGTH_SHORT).show();
                    }
                    if (str3.equals("×")) {
                        Toast.makeText(CalculatorPinSetting.this, R.string.disable_operation_cal, Toast.LENGTH_SHORT).show();
                    }
                    if (str3.equals("−")) {
                        Toast.makeText(CalculatorPinSetting.this, R.string.disable_operation_cal, Toast.LENGTH_SHORT).show();
                    }
                    if (str3.equals("+")) {
                        Toast.makeText(CalculatorPinSetting.this, R.string.disable_operation_cal, Toast.LENGTH_SHORT).show();
                    }
                    if (str3.equals(".")) {
                        Toast.makeText(CalculatorPinSetting.this, R.string.disable_operation_cal, Toast.LENGTH_SHORT).show();
                    }
                    if (str3.equals("=") && !CalculatorPinSetting.this.getText().equals("")) {
                        CalculatorPinSetting.this.ResetorSetCalPin();

                    }
                }
            });
        }
        findViewById(R.id.button_delete).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                CalculatorPinSetting.this.calculator.delete();
                CalculatorPinSetting.this.builder.setLength(0);
                CalculatorPinSetting.this.compringString.setLength(0);
            }
        });
        findViewById(R.id.button_delete).setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View view) {
                if (!CalculatorPinSetting.this.displayPrimary.getText().toString().trim().equals("")) {
                    View findViewById = CalculatorPinSetting.this.findViewById(R.id.display_overlay);
                    if (VERSION.SDK_INT >= 21) {
                        Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(findViewById, findViewById.getMeasuredWidth() / 2, findViewById.getMeasuredHeight(), 0.0f, (float) ((int) Math.hypot((double) findViewById.getWidth(), (double) findViewById.getHeight())));
                        createCircularReveal.setDuration(300);
                        createCircularReveal.addListener(new AnimatorListener() {
                            public void onAnimationRepeat(Animator animator) {
                            }

                            public void onAnimationStart(Animator animator) {
                            }

                            public void onAnimationEnd(Animator animator) {
                                CalculatorPinSetting.this.calculator.setText("");
                            }

                            public void onAnimationCancel(Animator animator) {
                                CalculatorPinSetting.this.calculator.setText("");
                            }
                        });
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(findViewById, "alpha", new float[]{0.0f});
                        ofFloat.setInterpolator(new DecelerateInterpolator());
                        ofFloat.setDuration(200);
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playSequentially(new Animator[]{createCircularReveal, ofFloat});
                        findViewById.setAlpha(1.0f);
                        animatorSet.start();
                    } else {
                        CalculatorPinSetting.this.calculator.setText("");
                    }
                }
                return false;
            }
        });
        findViewById(R.id.settings).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                CalculatorPinSetting calculatorPinSetting = CalculatorPinSetting.this;
                calculatorPinSetting.startActivity(new Intent(calculatorPinSetting, CalculatorSetting.class));
            }
        });
        this.calculator = new CalculatorPin(this);
        if (bundle2 != null) {
            setText(bundle2.getString("text"));
        }
        if (defaultSharedPreferences.getInt("launch_count", 5) == 0) {
            RateDialog.show(this);
            Editor edit = defaultSharedPreferences.edit();
            edit.putInt("launch_count", -1);
            edit.apply();
        }
    }

//    public Bitmap blurBitmap(Bitmap bitmap) {
//
//        //Let's create an empty bitmap with the same size of the bitmap we want to blur
//        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//
//        //Instantiate a new Renderscript
//        RenderScript rs = RenderScript.create(getApplicationContext());
//
//        //Create an Intrinsic Blur Script using the Renderscript
//        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
//
//        //Create the in/out Allocations with the Renderscript and the in/out bitmaps
//        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
//        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
//
//        //Set the radius of the blur
//        blurScript.setRadius(25.f);
//
//        //Perform the Renderscript
//        blurScript.setInput(allIn);
//        blurScript.forEach(allOut);
//
//        //Copy the final bitmap created by the out Allocation to the outBitmap
//        allOut.copyTo(outBitmap);
//
//        //recycle the original bitmap
//        bitmap.recycle();
//
//        //After finishing everything, we destroy the Renderscript.
//        rs.destroy();
//
//        return outBitmap;
//    }

    public void onResume() {
        super.onResume();
        setText(getText());
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("text", getText());
    }

    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        setText(bundle.getString("text"));
    }

    public String getText() {
        return this.calculator.getText();
    }

    public void setText(String str) {
        this.calculator.setText(str);
    }

    public void displayPrimaryScrollLeft(String str) {
        this.displayPrimary.setText(formatToDisplayMode(str));
        this.hsv.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                CalculatorPinSetting.this.hsv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                CalculatorPinSetting.this.hsv.fullScroll(17);
            }
        });
    }

    public void displayPrimaryScrollRight(String str) {
        this.displayPrimary.setText(formatToDisplayMode(str));
        this.hsv.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                CalculatorPinSetting.this.hsv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                CalculatorPinSetting.this.hsv.fullScroll(66);
            }
        });
    }

    public void displaySecondary(String str) {
        this.displaySecondary.setText(formatToDisplayMode(str));
    }

    private String formatToDisplayMode(String str) {
        return str.replace("/", "÷").replace("*", "×").replace("-", "−").replace("n ", "ln(").replace("l ", "log(").replace("C ", "C(").replace("s ", "sin(").replace("c ", "cos(").replace("t ", "tan(").replace(" ", "").replace("∞", "Infinity").replace("NaN", "Undefined");
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
        super.onPause();
    }


    public void ResetorSetCalPin() {
        if (!getText().trim().isEmpty()) {
//            findViewById(R.id.confirm_pass).setVisibility(View.VISIBLE);
//            findViewById(R.id.ok_pass).setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    findViewById(R.id.confirm_pass).setVisibility(View.INVISIBLE);
//                }
//            });
            String str = this.from;
            Log.e("TAG", "ResetorSetCalPin: str" + str);
            if (str != null && str.equals("SettingFragment")) {
                this.confCalPIN = this.securityCredentialsSharedPreferences.GetSecurityCredential();
                if (getText().contentEquals(this.confCalPIN)) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    if (SecurityLocksCommon.isBackupPasswordPin) {
                        SecurityLocksCommon.isBackupPasswordPin = false;
                        startActivity(new Intent(this, RecoveryOfCredentialsActivity.class));
                        finish();
                        return;
                    }
                    Intent intent = new Intent(this, SecurityLocksActivity.class);
                    intent.putExtra("isconfirmdialog", "isconfirmdialog");
                    startActivity(intent);
                    finish();
                    return;
                }
                Toast.makeText(this, R.string.lbl_Pin_doesnt_match, Toast.LENGTH_SHORT).show();
            } else if (getText().length() <= 4) {
                Toast.makeText(this, R.string.lbl_Pin_Limit, Toast.LENGTH_SHORT).show();
            } else if (getText().length() > 9) {
                Toast.makeText(this, R.string.lbl_Pin_not_greater_than_8, Toast.LENGTH_SHORT).show();
            } else if (!this.isSettingDecoy) {
                if (this.newCalPin.equals("")) {
                    this.tvPin.setText(R.string.cal_pin_text_confirm);
                    this.newCalPin = getText();
                    setText("");
                } else if (this.confirmCalPin.equals("")) {
                    this.confirmCalPin = getText();
                    if (this.confirmCalPin.equals(this.newCalPin)) {
                        this.securityCredentialsSharedPreferences.SetLoginType(LoginOptions.Calculator.toString());
                        this.securityCredentialsSharedPreferences.SetSecurityCredential(this.confirmCalPin);
                        Toast.makeText(this, "Calculator PIN set successfully.....", Toast.LENGTH_SHORT).show();
                        this.securityCredentialsSharedPreferences.RemoveDecoySecurityCredential();
                        this.securityCredentialsSharedPreferences.isSetCalModeEnable(Boolean.valueOf(true));
                        if (SecurityLocksCommon.IsFirstLogin) {
                            SecurityLocksCommon.IsFirstLogin = false;
                            this.securityCredentialsSharedPreferences.SetIsFirstLogin(Boolean.valueOf(false));
                        }
                        if (this.isSettingDecoy) {
                            Intent intent2 = new Intent(this, MainiFeaturesActivity.class);
                            SecurityLocksCommon.IsAppDeactive = false;
                            startActivity(intent2);
                            finish();
                        } else if (!this.isconfirmdialog.equals("isconfirmdialog")) {
                            if (this.securityCredentialsSharedPreferences.GetShowFirstTimeEmailPopup() && LoginOptions.None.toString().equals(this.LoginOption)) {
                                FirstTimeEmailDialog();
                            } else if (this.securityCredentialsSharedPreferences.GetSecurityCredential().length() <= 0 || this.securityCredentialsSharedPreferences.GetEmail().length() <= 0) {
                                FirstTimeEmailDialog();
                            } else {
                                Log.d("Calculator Pin Setting", "Calculator dialog will appear");
                                SecurityLocksCommon.IsAppDeactive = false;
                                startActivity(new Intent(this, MainiFeaturesActivity.class));
                                finish();
                            }
                        } else if (this.securityCredentialsSharedPreferences.GetEmail().trim().isEmpty()) {
                            FirstTimeEmailDialog();
                        } else {
                            SecurityLocksCommon.IsAppDeactive = false;
                            Intent intent3 = new Intent(this, MainiFeaturesActivity.class);
                            if (SecurityLocksCommon.IsFirstLogin) {
                                SecurityLocksCommon.IsFirstLogin = false;
                                this.securityCredentialsSharedPreferences.SetIsFirstLogin(Boolean.valueOf(false));
                            }
                            startActivity(intent3);
                            overridePendingTransition(17432576, 17432577);
                            finish();
                        }
                    } else {
                        Toast.makeText(this, R.string.lbl_Password_doesnt_match, Toast.LENGTH_SHORT).show();
                        this.confirmCalPin = "";
                        this.tvPin.setText(R.string.lbl_Pin_doesnt_match);
                        setText("");
                        this.isPinNotMatch = true;
                    }
                }
            }
        } else {
            Toast.makeText(this, "Enter your PIN", Toast.LENGTH_SHORT).show();
        }
    }

    public void DecoySetPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.confirmation_message_box);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
        textView.setTypeface(Typeface.createFromAsset(getAssets(), "ebrima.ttf"));
        textView.setText(R.string.lbl_msg_want_to_set_decoy_pin_ornot);
        TextView textView2 = (TextView) dialog.findViewById(R.id.lbl_Cancel);
        ((TextView) dialog.findViewById(R.id.lbl_Ok)).setText("Yes");
        textView2.setText("No");
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                CalculatorPinSetting.this.tvPin.setText("Set Decoy PIN");
                CalculatorPinSetting calculatorPinSetting = CalculatorPinSetting.this;
                calculatorPinSetting.isSettingDecoy = true;
                calculatorPinSetting.setText("");
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Intent intent = new Intent(CalculatorPinSetting.this, MainiFeaturesActivity.class);
                if (SecurityLocksCommon.IsFirstLogin) {
                    SecurityLocksCommon.IsFirstLogin = false;
                    CalculatorPinSetting.this.securityCredentialsSharedPreferences.SetIsFirstLogin(Boolean.valueOf(false));
                }
                CalculatorPinSetting.this.startActivity(intent);
                CalculatorPinSetting.this.overridePendingTransition(17432576, 17432577);
                CalculatorPinSetting.this.finish();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void DecoyModeEnable() {
        String str = this.from;
        if (str != null && str.equals("isSettingDecoy") && !getText().trim().isEmpty() && getText().length() > 4 && getText().length() < 9) {
            this.confCalPIN = this.securityCredentialsSharedPreferences.GetSecurityCredential();
            if (this.decoyPIN.equals("")) {
                this.decoyPIN = getText();
                this.isSettingDecoy = true;
                if (this.decoyPIN.equals(this.confCalPIN)) {
                    Toast.makeText(this, "Cant set same as master pin", Toast.LENGTH_LONG).show();
                    setText("");
                    return;
                }
                setText("");
                this.isSettingDecoy = true;
                this.tvPin.setText("Please enter confirm decoy PIN");
            } else if (this.decoyConfirmPIN.equals("")) {
                this.decoyConfirmPIN = getText();
                setText("");
                if (this.decoyConfirmPIN.equals(this.decoyPIN)) {
                    this.securityCredentialsSharedPreferences.SetDecoySecurityCredential(this.decoyConfirmPIN);
                    Toast.makeText(this, "Decoy PIN set successfully ", Toast.LENGTH_SHORT).show();
                    SecurityLocksCommon.IsAppDeactive = false;
                    Intent intent = new Intent(this, MainiFeaturesActivity.class);
                    if (SecurityLocksCommon.IsFirstLogin) {
                        SecurityLocksCommon.IsFirstLogin = false;
                        this.securityCredentialsSharedPreferences.SetIsFirstLogin(Boolean.valueOf(false));
                    }
                    startActivity(intent);
                    overridePendingTransition(17432576, 17432577);
                    finish();
                    return;
                }
                Toast.makeText(this, "Decoy PIN doest match...", Toast.LENGTH_SHORT).show();
                this.tvPin.setText("Decoy PIN doest match...");
                this.decoyConfirmPIN = "";
            } else {
                Toast.makeText(this, "Please enter minimum four digits", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8 && sensorEvent.values[0] == 0.0f && PanicSwitchCommon.IsPalmOnFaceOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    public void onShake(float f) {
        if (PanicSwitchCommon.IsFlickOn || PanicSwitchCommon.IsShakeOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    public void onBackPressed() {
        SecurityLocksCommon.IsAppDeactive = false;
        if (SecurityLocksCommon.isBackupPasswordPin) {
            Intent intent = new Intent(this, SettingActivity.class);
            overridePendingTransition(17432576, 17432577);
            startActivity(intent);
            SecurityLocksCommon.isBackupPasswordPin = false;
            finish();

        } else if (from != null) {
            if (this.from.equals("SettingFragment")) {
                Intent intent2 = new Intent(this, SettingActivity.class);
                overridePendingTransition(17432576, 17432577);
                startActivity(intent2);
                finish();
            }
        } else {


            startActivity(new Intent(this, SecurityLocksActivity.class));
            overridePendingTransition(17432576, 17432577);
            finish();
        }
    }

    public void FirstTimeEmailDialog() {

        CalculatorPinSetting.this.securityCredentialsSharedPreferences.SetShowFirstTimeEmailPopup(Boolean.valueOf(false));

        new AlertDialog.Builder(CalculatorPinSetting.this)
                .setTitle("Default Password")
                .setMessage("Your deault password is :- " + "0000")

                .setPositiveButton("Yes Remember", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        dialog.dismiss();
                        if (!CalculatorPinSetting.this.isconfirmdialog.equals("isconfirmdialog")) {
                            Log.d("CalculaltorPinSetting", "dialog for calculator will appear");
                            SecurityLocksCommon.IsAppDeactive = false;
                            CalculatorPinSetting.this.startActivity(new Intent(CalculatorPinSetting.this, MainiFeaturesActivity.class));
                            CalculatorPinSetting.this.finish();
                        } else {
                            SecurityLocksCommon.IsAppDeactive = false;
                            CalculatorPinSetting.this.startActivity(new Intent(CalculatorPinSetting.this, MainiFeaturesActivity.class));
                            CalculatorPinSetting.this.finish();
                        }
                    }


                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

      /*  Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
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
                        CalculatorPinSetting.this.securityCredentialsSharedPreferences.SetEmail(obj);
                        CalculatorPinSetting.this.securityCredentialsSharedPreferences.SetShowFirstTimeEmailPopup(Boolean.valueOf(false));
                        SecurityLocksCommon.IsAppDeactive = false;
                        Toast.makeText(CalculatorPinSetting.this, R.string.toast_Email_Saved, Toast.LENGTH_LONG).show();

                        dialog.dismiss();

                        Common.loginCount = 2;
                        CalculatorPinSetting.this.securityCredentialsSharedPreferences.SetRateCount(Common.loginCount);
                        new AlertDialog.Builder(CalculatorPinSetting.this)
                                .setTitle("Default Password")
                                .setMessage("Your deault password is :- " + "0000")

                                .setPositiveButton("Yes Remember", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        dialog.dismiss();
                                        if (!CalculatorPinSetting.this.isconfirmdialog.equals("isconfirmdialog")) {
                                            Log.d("CalculaltorPinSetting", "dialog for calculator will appear");
                                            SecurityLocksCommon.IsAppDeactive = false;
                                            CalculatorPinSetting.this.startActivity(new Intent(CalculatorPinSetting.this, FeaturesActivity.class));
                                            CalculatorPinSetting.this.finish();
                                        } else {
                                            SecurityLocksCommon.IsAppDeactive = false;
                                            CalculatorPinSetting.this.startActivity(new Intent(CalculatorPinSetting.this, FeaturesActivity.class));
                                            CalculatorPinSetting.this.finish();
                                        }
                                    }


                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                        return;
                    }
                    Toast.makeText(CalculatorPinSetting.this, R.string.toast_Invalid_Email, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(CalculatorPinSetting.this, R.string.toast_Enter_Email, Toast.LENGTH_SHORT).show();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Skip)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                CalculatorPinSetting.this.securityCredentialsSharedPreferences.SetShowFirstTimeEmailPopup(Boolean.valueOf(false));
                SecurityLocksCommon.IsAppDeactive = false;
                Toast.makeText(CalculatorPinSetting.this, R.string.toast_Skip, Toast.LENGTH_LONG).show();

                dialog.dismiss();
                Common.loginCount = 1;
                CalculatorPinSetting.this.securityCredentialsSharedPreferences.SetRateCount(Common.loginCount);
                new AlertDialog.Builder(CalculatorPinSetting.this)
                        .setTitle("Default Password")
                        .setMessage("Your deault password is :- " + "0000")

                        .setPositiveButton("Yes Remember", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                dialog.dismiss();
                                if (!CalculatorPinSetting.this.isconfirmdialog.equals("isconfirmdialog")) {
                                    Log.d("Calculator Pin setting", "Calculator dialog will appear");
                                    SecurityLocksCommon.IsAppDeactive = false;
                                    CalculatorPinSetting.this.startActivity(new Intent(CalculatorPinSetting.this, FeaturesActivity.class));
                                    CalculatorPinSetting.this.finish();
                                } else {
                                    SecurityLocksCommon.IsAppDeactive = false;
                                    CalculatorPinSetting.this.startActivity(new Intent(CalculatorPinSetting.this, FeaturesActivity.class));
                                    CalculatorPinSetting.this.finish();
                                }
                            }


                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
        });
        dialog.show();*/
    }

}
