package com.example.vault.storageoption;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.vault.R;
import com.example.vault.BaseActivity;
import com.example.vault.calculator.CalculatorPinSetting;
import com.example.vault.datarecovery.DataRecoveryActivity;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivity;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securitylocks.ConfirmPasswordPinActivity;
import com.example.vault.securitylocks.ConfirmPatternActivity;
import com.example.vault.securitylocks.SecurityLocksActivity;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.securitylocks.SecurityLocksCommon.LoginOptions;
import com.example.vault.securitylocks.SecurityLocksSharedPreferences;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.MoveData;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;
import pub.devrel.easypermissions.PermissionRequest.Builder;

public class SettingActivity extends BaseActivity implements PermissionCallbacks {
    static SharedPreferences DataTransferPrefs;
    static Editor DataTransferprefsEditor;
    public static ProgressDialog myProgressDialog;
    boolean IsStealthModeOn = false;
    String LoginOption = "";
    String[] PERMISSIONS_ = VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? new String[]{"android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO", "android.permission.READ_MEDIA_AUDIO"} : new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    CheckBox cb_stealth_mode;
    Dialog dialog;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 3) {
                if (StorageOptionsCommon.IsAllDataMoveingInProg) {
                    StorageOptionsCommon.IsAllDataMoveingInProg = false;
                    hideProgress();
                    if (StorageOptionsCommon.IsApphasDataforTransfer) {
                        StorageOptionsCommon.IsApphasDataforTransfer = false;
                        Toast.makeText(SettingActivity.this, "Data Moved successfully.", Toast.LENGTH_LONG).show();
                    }
                }
            } else if (message.what == 2 && StorageOptionsCommon.IsAllDataMoveingInProg) {
                StorageOptionsCommon.IsAllDataMoveingInProg = false;
                hideProgress();
                if (StorageOptionsCommon.IsApphasDataforTransfer) {
                    StorageOptionsCommon.IsApphasDataforTransfer = false;
                    Toast.makeText(SettingActivity.this, "Data transferred successfully.", Toast.LENGTH_LONG).show();
                }
            }
            super.handleMessage(message);
        }
    };
    boolean isSDCard = false;
    boolean isStealthModeAlreadyCheck = false;
    private LinearLayout ll_anchor;
    LinearLayout ll_background;

    LinearLayout ll_data_recovery;
    LinearLayout ll_decoy_security_lock;
    LinearLayout ll_lollipop_sd_permission;
    LinearLayout ll_panic_switch;
    LinearLayout ll_recovery_credentials;
    LinearLayout ll_security_credentials;
    LinearLayout ll_stealth_mode;
    LinearLayout ll_storage_options;
    LinearLayout ll_tick;
    LinearLayout fingerprint;
    LinearLayout selectappicon;
    int number = 0;
    List<UriPermission> permissions;
    SecurityLocksSharedPreferences securityCredentialsSharedPreferences;
    private SensorManager sensorManager;
    StorageOptionSharedPreferences storageOptionSharedPreferences;
    private Toolbar toolbar;
    Switch fingerSwitch;

    BottomSheetLayout bottomSheetLayout;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    public SecurityLocksSharedPreferences securityLocksSharedPreferences;


    private static final String disguise = "com.example.vault.icon.icon_disguise";


    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onPermissionsGranted(int i, @NonNull List<String> list) {
    }


    public void showMoveDataToOrFromSDCardProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Data transferring \nWarning: Please be patient and do not close this app otherwise you may lose your data.", true);
    }


    public void hideProgress() {
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
    }


    private void showMenuSheet(final MenuSheetView.MenuType menuType) {
        MenuSheetView menuSheetView =
                new MenuSheetView(SettingActivity.this, menuType, "Choose Icon...", new MenuSheetView.OnMenuItemClickListener() {


                    @Override
                    public boolean onMenuItemClick(MenuItem item) {


                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }
                        ChangeAppIcon(item.getOrder());
                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.menuicon);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }


    private void ChangeAppIcon(int pos) {


        for (int i = 0; i <= 11; i++) {


            if (i == pos) {

                getPackageManager().setComponentEnabledSetting(
                        new ComponentName(this, "com.example.vault.icon.icon_disguise" + i),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            } else {

                getPackageManager().setComponentEnabledSetting(
                        new ComponentName(this, "com.example.vault.icon.icon_disguise" + i),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            }


        }


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_settings);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);
        bottomSheetLayout.setPeekOnDismiss(true);

        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.securityLocksSharedPreferences = SecurityLocksSharedPreferences.GetObject(this);
        fingerprint = (LinearLayout) findViewById(R.id.fingerprint);
        fingerSwitch = (Switch) findViewById(R.id.fingerSwitch);

        selectappicon = (LinearLayout) findViewById(R.id.selectappicon);


        selectappicon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                showMenuSheet(MenuSheetView.MenuType.LIST);


//                powerMenu = new PowerMenu.Builder(SettingActivity.this)
//
//                        .addItem(new PowerMenuItem("Calculator", R.drawable.appicon, true)) // add an item.
//                        .addItem(new PowerMenuItem("Calculator", R.mipmap.ic_vault_cal_aosp))
//                        .addItem(new PowerMenuItem("Calculator", R.mipmap.ic_vault_cal_google))
//                        .addItem(new PowerMenuItem("Calculator", R.mipmap.ic_vault_cal_samsung))
//                        .addItem(new PowerMenuItem("Calculator", R.mipmap.ic_vault_cal_huawei01))
//                        .addItem(new PowerMenuItem("Calculator", R.mipmap.ic_vault_cal_oppo01))
//                        .addItem(new PowerMenuItem("Calculator", R.mipmap.ic_vault_cal_vivo01))
//                        .addItem(new PowerMenuItem("Calculator", R.mipmap.ic_vault_cal_xiaomi01))
//                        .addItem(new PowerMenuItem("Calculator", R.mipmap.ic_vault_cal_huawei02))
//                        .addItem(new PowerMenuItem("Calculator", R.mipmap.ic_vault_cal_oppo02))
//                        .addItem(new PowerMenuItem("Calculator", R.mipmap.ic_vault_cal_vivo02))
//                        .addItem(new PowerMenuItem("Calculator", R.mipmap.ic_vault_cal_xiaomi02))
//
//
//                        .setAnimation(MenuAnimation.ELASTIC_CENTER) // Animation start point (TOP | LEFT).
//                        .setMenuRadius(10f) // sets the corner radius.
//                        .setMenuShadow(10f) // sets the shadow.
//                        .setTextColor(ContextCompat.getColor(SettingActivity.this, R.color.colorPrimary))
//                        .setTextGravity(Gravity.CENTER)
//                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
//                        .setSelectedTextColor(Color.WHITE)
//                        .setMenuColor(Color.WHITE)
//                        .setDividerHeight(10)
//                        .setSelectedMenuColor(ContextCompat.getColor(SettingActivity.this, R.color.colorPrimary))
//                        .setOnMenuItemClickListener(onMenuItemClickListener)
//                        .build();
//
//                View layout = findViewById(R.id.ll_background);
//                powerMenu.showAtCenter(layout);


            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {


            keyguardManager =
                    (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager =
                    (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);


            if (!fingerprintManager.isHardwareDetected()) {

                fingerprint.setVisibility(View.GONE);

            }


        }

        boolean isfingerprint = securityLocksSharedPreferences.GetFingerprint();

        if (isfingerprint) {

            fingerSwitch.setChecked(true);
        } else {
            fingerSwitch.setChecked(false);

        }


        fingerSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    printtoast("Please enable the fingerprint permission");
                    fingerSwitch.setChecked(false);
                    securityLocksSharedPreferences.SetFingerPrint(true);
                }

                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    fingerSwitch.setChecked(false);
                    securityLocksSharedPreferences.SetFingerPrint(true);
                    printtoast("No fingerprint configured. Please register at least one fingerprint in your device's Settings");

                }

                if (!keyguardManager.isKeyguardSecure()) {
                    fingerSwitch.setChecked(false);
                    securityLocksSharedPreferences.SetFingerPrint(true);
                    printtoast("Please enable lockscreen security in your device's Settings");
                }


            }
        });


        fingerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    fingerSwitch.setChecked(true);
                    securityLocksSharedPreferences.SetFingerPrint(true);

                } else {
                    fingerSwitch.setChecked(false);
                    securityLocksSharedPreferences.SetFingerPrint(false);
                }
            }
        });

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        setSupportActionBar(this.toolbar);
//        getSupportActionBar().setTitle((CharSequence) "Settings");
        // getSupportActionBar().setTitle("");

        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                btnBackonClick();
            }
        });
        this.securityCredentialsSharedPreferences = SecurityLocksSharedPreferences.GetObject(this);
        this.LoginOption = this.securityCredentialsSharedPreferences.GetLoginType();
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.ll_lollipop_sd_permission = (LinearLayout) findViewById(R.id.ll_lollipop_sd_permission);
        this.ll_security_credentials = (LinearLayout) findViewById(R.id.ll_security_credentials);

        this.ll_data_recovery = (LinearLayout) findViewById(R.id.ll_data_recovery);
        this.ll_recovery_credentials = (LinearLayout) findViewById(R.id.ll_recovery_credentials);
        this.ll_decoy_security_lock = (LinearLayout) findViewById(R.id.ll_decoy_security_lock);
        this.ll_storage_options = (LinearLayout) findViewById(R.id.ll_storage_options);
        this.ll_panic_switch = (LinearLayout) findViewById(R.id.ll_panic_switch);
        this.ll_stealth_mode = (LinearLayout) findViewById(R.id.ll_stealth_mode);
        this.cb_stealth_mode = (CheckBox) findViewById(R.id.cb_stealth_mode);
        this.ll_tick = (LinearLayout) findViewById(R.id.ll_tick);
        if (SecurityLocksCommon.IsFakeAccount == 1) {

            this.ll_data_recovery.setVisibility(View.GONE);
            this.ll_recovery_credentials.setVisibility(View.GONE);
            this.ll_decoy_security_lock.setVisibility(View.GONE);
            this.ll_storage_options.setVisibility(View.GONE);
            this.ll_panic_switch.setVisibility(View.GONE);
            this.ll_stealth_mode.setVisibility(View.GONE);
        }
        if (VERSION.SDK_INT >= 21 && VERSION.SDK_INT < 23) {
            this.ll_lollipop_sd_permission.setVisibility(View.VISIBLE);
            if (StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                this.ll_tick.setVisibility(View.VISIBLE);
            } else {
                this.ll_tick.setVisibility(View.INVISIBLE);
            }
        }
        this.ll_lollipop_sd_permission.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                final Dialog dialog = new Dialog(SettingActivity.this, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.sdcard_permission_select_hint_msgbox);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        startActivityForResult(new Intent("android.intent.action.OPEN_DOCUMENT_TREE"), 42);
                        dialog.dismiss();
                    }
                });
                ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        this.ll_security_credentials.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                if (LoginOptions.Pattern.toString().equals(LoginOption)) {
                    SecurityLocksCommon.IsConfirmPatternActivity = true;
                    startActivity(new Intent(SettingActivity.this, ConfirmPatternActivity.class));
                    finish();
                } else if (LoginOptions.Pin.toString().equals(LoginOption) || LoginOptions.Password.toString().equals(LoginOption)) {
                    startActivity(new Intent(SettingActivity.this, ConfirmPasswordPinActivity.class));
                    finish();
                } else if (LoginOptions.Calculator.toString().equals(LoginOption)) {
                    Intent intent = new Intent(SettingActivity.this, CalculatorPinSetting.class);
                    intent.putExtra("from", "SettingFragment");
                    startActivity(intent);
                    finish();
                } else {
                    startActivity(new Intent(SettingActivity.this, SecurityLocksActivity.class));
                    finish();
                }
            }
        });
//        this.ll_cloud_backup.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) {
//                if (Common.isPurchased) {
//                    SecurityLocksCommon.IsAppDeactive = false;
//                    CloudCommon.IsCameFromSettings = true;
//                    startActivity(new Intent(SettingActivity.this, DropboxLoginActivity.class));
//                    finish();
//                    return;
//                }
////                SecurityLocksCommon.IsAppDeactive = false;
////                InAppPurchaseActivity._cameFrom = CameFrom.Feature.ordinal();
////                startActivity(new Intent(SettingActivity.this, InAppPurchaseActivity.class));
////                finish();
//            }
//        });
        this.ll_data_recovery.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SettingActivity settingActivity = SettingActivity.this;
                settingActivity.number = 1;
                SecurityLocksCommon.IsAppDeactive = false;
                settingActivity.requestPermission(settingActivity.PERMISSIONS_);
            }
        });
        this.ll_recovery_credentials.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                if (LoginOptions.Pattern.toString().equals(LoginOption)) {
                    SecurityLocksCommon.isBackupPattern = true;
                    startActivity(new Intent(SettingActivity.this, ConfirmPatternActivity.class));
                    finish();
                } else if (LoginOptions.Pin.toString().equals(LoginOption) || LoginOptions.Password.toString().equals(LoginOption)) {
                    SecurityLocksCommon.isBackupPasswordPin = true;
                    startActivity(new Intent(SettingActivity.this, ConfirmPasswordPinActivity.class));
                    finish();
                } else if (LoginOptions.Calculator.toString().equals(LoginOption)) {
                    SecurityLocksCommon.isBackupPasswordPin = true;
                    Intent intent = new Intent(SettingActivity.this, CalculatorPinSetting.class);
                    intent.putExtra("from", "SettingFragment");
                    startActivity(intent);
                    finish();
                }
            }
        });
        this.ll_decoy_security_lock.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (LoginOptions.Pattern.toString().equals(LoginOption)) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    SecurityLocksCommon.isSettingDecoy = true;
                    startActivity(new Intent(SettingActivity.this, ConfirmPatternActivity.class));
                    finish();
                } else if (LoginOptions.Pin.toString().equals(LoginOption) || LoginOptions.Password.toString().equals(LoginOption)) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    SecurityLocksCommon.isSettingDecoy = true;
                    startActivity(new Intent(SettingActivity.this, ConfirmPasswordPinActivity.class));
                    finish();
                } else {
                    Toast.makeText(SettingActivity.this, R.string.decoy_mode_toast_disguise, Toast.LENGTH_LONG).show();
                }
            }
        });
        this.ll_storage_options.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SetStorageOption();
            }
        });
        this.ll_panic_switch.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                startActivity(new Intent(SettingActivity.this, PanicSwitchActivity.class));
                finish();
            }
        });
        this.ll_stealth_mode.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                if (securityCredentialsSharedPreferences.isGetCalModeEnable()) {
                    showDisguiseModeSelected();
                }
            }
        });
    }

    private void printtoast(String toaststring) {

        Toast.makeText(this, toaststring, Toast.LENGTH_SHORT).show();

    }

    @SuppressLint({"NewApi"})
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == 42 && VERSION.SDK_INT >= 21 && VERSION.SDK_INT < 23) {
            Uri data = intent.getData();
            String uri = data.toString();
            getContentResolver().takePersistableUriPermission(data, 3);
            this.permissions = getContentResolver().getPersistedUriPermissions();
            String uri2 = ((UriPermission) this.permissions.get(0)).getUri().toString();
            if (uri2.contains("primary") || !uri2.contains("-")) {
                Toast.makeText(this, R.string.lblwrong_sd_card_permssion, Toast.LENGTH_SHORT).show();
                this.ll_tick.setVisibility(View.INVISIBLE);
            } else if (uri2.substring(uri2.length() - 3).contains("%3A")) {
                StorageOptionSharedPreferences.GetObject(this).SetSDCardUri(uri);
                this.ll_tick.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, R.string.lblwrong_sd_card_permssion, Toast.LENGTH_SHORT).show();
                this.ll_tick.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void btnBackonClick() {
        SecurityLocksCommon.IsAppDeactive = false;
        if (bottomSheetLayout.isSheetShowing()) {
            bottomSheetLayout.dismissSheet();
        } else {


            startActivity(new Intent(this, MainiFeaturesActivity.class));
            finish();
        }
    }


    public void SetStorageOption() {
        this.storageOptionSharedPreferences = StorageOptionSharedPreferences.GetObject(this);
        if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
            this.isSDCard = false;
            StorageOptionsCommon.IsStorageSDCard = this.storageOptionSharedPreferences.GetIsStorageSDCard();
            this.isSDCard = StorageOptionsCommon.IsStorageSDCard;
            final Dialog dialog2 = new Dialog(this, R.style.FullHeightDialog);
            dialog2.setContentView(R.layout.set_storage_option_popup);
            dialog2.setCancelable(true);
            LinearLayout linearLayout = (LinearLayout) dialog2.findViewById(R.id.ll_background);
            LinearLayout linearLayout2 = (LinearLayout) dialog2.findViewById(R.id.ll_PhoneMemory);
            LinearLayout linearLayout3 = (LinearLayout) dialog2.findViewById(R.id.ll_SDCard);
            final CheckBox checkBox = (CheckBox) dialog2.findViewById(R.id.cbPhoneMemory);
            final CheckBox checkBox2 = (CheckBox) dialog2.findViewById(R.id.cbSDCard);
            TextView textView = (TextView) dialog2.findViewById(R.id.lblPhoneMemory);
            TextView textView2 = (TextView) dialog2.findViewById(R.id.lblSDCard);
            checkBox.setEnabled(false);
            checkBox2.setEnabled(false);
            if (!StorageOptionsCommon.IsDeviceHaveMoreThenOneStorage) {
                textView.setText(StorageOptionsCommon.STORAGEPATH_1);
                linearLayout3.setVisibility(View.INVISIBLE);
                linearLayout3.setEnabled(false);
            } else {
                textView.setText(StorageOptionsCommon.STORAGEPATH_1);
                textView2.setText(StorageOptionsCommon.STORAGEPATH_2);
            }
            if (StorageOptionsCommon.IsStorageSDCard) {
                checkBox2.setChecked(true);
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(true);
                checkBox2.setChecked(false);
            }
            linearLayout2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkBox.setChecked(true);
                    checkBox2.setChecked(false);
                }
            });
            linearLayout3.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (StorageOptionsCommon.IsDeviceHaveMoreThenOneStorage) {
                        checkBox2.setChecked(true);
                        checkBox.setChecked(false);
                    }
                }
            });
            ((LinearLayout) dialog2.findViewById(R.id.ll_Save)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog2.dismiss();
                    if (isSDCard && checkBox2.isChecked()) {
                        SettingActivity settingActivity = SettingActivity.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append("You are already using");
                        sb.append(StorageOptionsCommon.STORAGEPATH_2);
                        Toast.makeText(settingActivity, sb.toString(), Toast.LENGTH_SHORT).show();
                        StorageOptionsCommon.STORAGEPATH = StorageOptionsCommon.STORAGEPATH_2;
                        storageOptionSharedPreferences.SetStoragePath(StorageOptionsCommon.STORAGEPATH);
                    } else if (isSDCard || !checkBox.isChecked()) {
                        final MoveData instance = MoveData.getInstance(SettingActivity.this);
                        if (StorageOptionsCommon.IsStorageSDCard) {
                            if (((float) Common.GetTotalFreeSpaceSDCard()) < Common.GetFileSize(instance.GetAllFilesPath())) {
                                Toast.makeText(SettingActivity.this, "You dont have enough space in SD Card", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else if (Common.GetTotalFree() < Common.GetFileSize(instance.GetAllFilesPath())) {
                            Toast.makeText(SettingActivity.this, "You dont have enough space in Phone Memory", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (checkBox2.isChecked()) {
                            StorageOptionsCommon.IsStorageSDCard = true;
                            storageOptionSharedPreferences.SetIsStorageSDCard(Boolean.valueOf(true));
                            StorageOptionsCommon.STORAGEPATH = StorageOptionsCommon.STORAGEPATH_2;
                            storageOptionSharedPreferences.SetStoragePath(StorageOptionsCommon.STORAGEPATH);
                        } else {
                            StorageOptionsCommon.IsStorageSDCard = false;
                            storageOptionSharedPreferences.SetIsStorageSDCard(Boolean.valueOf(false));
                            StorageOptionsCommon.STORAGEPATH = StorageOptionsCommon.STORAGEPATH_1;
                            storageOptionSharedPreferences.SetStoragePath(StorageOptionsCommon.STORAGEPATH);
                        }
                        showMoveDataToOrFromSDCardProgress();
                        StorageOptionsCommon.IsAllDataMoveingInProg = true;
                        new Thread() {
                            public void run() {
                                try {
                                    SettingActivity.DataTransferPrefs = getSharedPreferences("DataTransferStatus", 0);
                                    SettingActivity.DataTransferprefsEditor = SettingActivity.DataTransferPrefs.edit();
                                    SettingActivity.DataTransferprefsEditor.putBoolean("isPhotoTransferComplete", false);
                                    SettingActivity.DataTransferprefsEditor.commit();
                                    SettingActivity.DataTransferprefsEditor.putBoolean("isVideoTransferComplete", false);
                                    SettingActivity.DataTransferprefsEditor.commit();
                                    SettingActivity.DataTransferprefsEditor.putBoolean("isDocumentTransferComplete", false);
                                    SettingActivity.DataTransferprefsEditor.commit();
                                    instance.GetDataFromDataBase();
                                    instance.MoveDataToORFromCardFromSetting();
                                    Message message = new Message();
                                    message.what = 3;
                                    handle.sendMessage(message);
                                    Toast.makeText(SettingActivity.this, "Storage setting changed successfully.", Toast.LENGTH_LONG).show();
                                } catch (Exception unused) {
                                    Message message2 = new Message();
                                    message2.what = 2;
                                    handle.sendMessage(message2);
                                }
                            }
                        }.start();
                        dialog2.dismiss();
                    } else {
                        SettingActivity settingActivity2 = SettingActivity.this;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("You are already using");
                        sb2.append(StorageOptionsCommon.STORAGEPATH_1);
                        Toast.makeText(settingActivity2, sb2.toString(), Toast.LENGTH_SHORT).show();
                        StorageOptionsCommon.STORAGEPATH = StorageOptionsCommon.STORAGEPATH_1;
                        storageOptionSharedPreferences.SetStoragePath(StorageOptionsCommon.STORAGEPATH);
                    }
                }
            });
            dialog2.show();
        } else if (VERSION.SDK_INT == 19) {
            Toast.makeText(this, R.string.lbl_KitKat_StorageOption_Alert, Toast.LENGTH_LONG).show();
        } else if (VERSION.SDK_INT >= 21) {
            Toast.makeText(this, R.string.lbl_Lollipop_StorageOption_Alert, Toast.LENGTH_LONG).show();
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
        if (SecurityLocksCommon.IsAppDeactive && !StorageOptionsCommon.IsAllDataMoveingInProg) {
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

            if (bottomSheetLayout.isSheetShowing()) {
                bottomSheetLayout.dismissSheet();
            } else {


                startActivity(new Intent(this, MainiFeaturesActivity.class));
                finish();
            }

        }
        return super.onKeyDown(i, keyEvent);
    }


    @AfterPermissionGranted(123)
    public void requestPermission(String[] strArr) {
        SecurityLocksCommon.IsAppDeactive = false;
        if (!EasyPermissions.hasPermissions(this, strArr)) {
            EasyPermissions.requestPermissions(new Builder((Activity) this, 123, strArr).setRationale("For the best Calculator Vault experience, please Allow Permission").setPositiveButtonText("ok").setNegativeButtonText("").build());
        } else if (this.number == 1) {
            startActivity(new Intent(this, DataRecoveryActivity.class));
            finish();
        }
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (iArr.length <= 0 || iArr[0] != 0) {
            if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_MEDIA_IMAGES") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_MEDIA_VIDEO") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_MEDIA_AUDIO") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.PROCESS_OUTGOING_CALLS") : ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.PROCESS_OUTGOING_CALLS")) {
                EasyPermissions.hasPermissions(this, VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? this.number == 11 ? new String[]{"android.permission.PROCESS_OUTGOING_CALLS"} : new String[]{"android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO", "android.permission.READ_MEDIA_AUDIO"} : this.number == 11 ? new String[]{"android.permission.PROCESS_OUTGOING_CALLS"} : new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"});
                Toast.makeText(this, "Permission is denied", Toast.LENGTH_SHORT).show();
                return;
            }
            EasyPermissions.onRequestPermissionsResult(i, strArr, iArr, this);
        } else if (hasPermissions(this, strArr)) {
            if (this.number == 1) {
                startActivity(new Intent(this, DataRecoveryActivity.class));
                finish();
            }
            Toast.makeText(this, "Permission is granted ", Toast.LENGTH_SHORT).show();
        } else if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_MEDIA_IMAGES") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_MEDIA_VIDEO") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_MEDIA_AUDIO") : ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_EXTERNAL_STORAGE")) {
            if (EasyPermissions.hasPermissions(this, VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? new String[]{"android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO", "android.permission.READ_MEDIA_AUDIO"} : new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"})) {
                Toast.makeText(this, "Permission  again...", Toast.LENGTH_SHORT).show();
            } else {
                EasyPermissions.requestPermissions(new Builder((Activity) this, 123, VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? new String[]{"android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO", "android.permission.READ_MEDIA_AUDIO"} : new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}).setRationale("For the best Calculator Vault experience, please Allow Permission").setPositiveButtonText("ok").setNegativeButtonText("").build());
            }
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.onRequestPermissionsResult(i, strArr, iArr, this);
        }
    }

    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied((Activity) this, list)) {
            new AppSettingsDialog.Builder((Activity) this).build().show();
        }
    }

    public static boolean hasPermissions(Context context, String... strArr) {
        if (!(context == null || strArr == null)) {
            for (String checkSelfPermission : strArr) {
                if (ActivityCompat.checkSelfPermission(context, checkSelfPermission) != 0) {
                    return false;
                }
            }
        }
        return true;
    }


    public void showDisguiseModeSelected() {
        new AlertDialog.Builder(this).setTitle((CharSequence) "Calculator Disguise Mode").setCancelable(false).setMessage((CharSequence) "You have already selected Calculator Disguise Mode").setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }
}
