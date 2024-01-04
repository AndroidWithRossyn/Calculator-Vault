package com.example.vault;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.vault.Fingerprint.FingerprintHandler;
import com.example.vault.calculator.CalculatorPinSetting;
import com.example.vault.calculator.MyCalculatorActivity;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.tryattempt.CameraPreview;
import com.example.vault.tryattempt.model.TryAttemptEntity;
import com.example.vault.tryattempt.TryAttemptsSharedPreferences;
import com.example.vault.panicswitch.AccelerometerListener;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.panicswitch.PanicSwitchSharedPreferences;
import com.example.vault.securitylocks.ConfirmLockPatternViewLogin;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.securitylocks.SecurityLocksCommon.LoginOptions;
import com.example.vault.securitylocks.SecurityLocksSharedPreferences;
import com.example.vault.storageoption.AppSettingsSharedPreferences;
import com.example.vault.storageoption.StorageOptionSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    static int tryAttemptCount = 0;
    public static Camera mCamera = null;
    public static CameraPreview mCameraPreview = null;
    public static long startTime = 0;
    public static TextView txt_wrong_pttern = null;
    public static String wrongPassword = "";
    public String LoginOption = "";
    private ImageButton btnLogin;
    public Handler customHandler = new Handler();
    private RelativeLayout imgKey;
    private RelativeLayout imgKeyfail;
    String mypass;
    PanicSwitchSharedPreferences panicSwitchSharedPreferences;
    public SecurityLocksSharedPreferences securityLocksSharedPreferences;
    private SensorManager sensorManager;
    long timeInMilliseconds = 0;
    long timeSwapBuff = 0;
    public TextView tv_forgot;
    public EditText txtPassword;
    private TextView txtTimer;
    public TextView txt_wrong_password_pin;
    private TextView txtforgotpassword;
    private TextView txtforgotpattern;
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            Calendar instance = Calendar.getInstance();
            LoginActivity.this.timeInMilliseconds = LoginActivity.startTime - instance.getTimeInMillis();
            LoginActivity loginActivity = LoginActivity.this;
            loginActivity.updatedTime = loginActivity.timeSwapBuff + LoginActivity.this.timeInMilliseconds;
            int i = (int) (LoginActivity.this.updatedTime / 1000);
            int i2 = (i / 60) / 60;
            int i3 = i % 60;
            if (LoginActivity.startTime > instance.getTimeInMillis()) {
                LoginActivity.this.customHandler.postDelayed(this, 0);
                return;
            }
            Common.TryAttemptCount = 0;
            LoginActivity.tryAttemptCount = 0;
            LoginActivity.this.txtPassword.setEnabled(true);
        }
    };
    long updatedTime = 0;

    ArrayList<TryAttemptEntity> tryAttemptEntities = null;
    Context context;
    String tryAttemptPath = "";
    PictureCallback mPicture = new PictureCallback() {
        public void onPictureTaken(byte[] bArr, Camera camera) {
            StringBuilder sb = new StringBuilder();
            sb.append(SecurityLocksCommon.StoragePath);
            sb.append(SecurityLocksCommon.TryAttempts);
            File file = new File(sb.toString());
            if (!file.exists()) {
                file.mkdirs();
            }
            String uuid = UUID.randomUUID().toString();
            StringBuilder sb2 = new StringBuilder();
            sb2.append(SecurityLocksCommon.StoragePath);
            sb2.append(SecurityLocksCommon.TryAttempts);
            sb2.append(uuid);
            sb2.append("#jpg");
            File file2 = new File(sb2.toString());

            StringBuilder sb3 = new StringBuilder();
            sb3.append(SecurityLocksCommon.StoragePath);
            sb3.append(SecurityLocksCommon.TryAttempts);
            sb3.append(uuid);
            sb3.append("#jpg");
            tryAttemptPath = sb3.toString();
            if (!file2.exists()) {
                try {
                    file2.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                fileOutputStream.write(bArr);
                fileOutputStream.close();
                AddTryAttempToSharedPreference(context, LoginActivity.wrongPassword, tryAttemptPath);
            } catch (FileNotFoundException unused) {
                Toast.makeText(LoginActivity.this, "File not found exception", Toast.LENGTH_SHORT).show();
            } catch (IOException unused2) {
                Toast.makeText(LoginActivity.this, "IO Exception", Toast.LENGTH_SHORT).show();
            }
            camera.startPreview();
        }
    };


    public void AddTryAttempToSharedPreference(Context context2, String str, String str2) {
        SecurityLocksSharedPreferences GetObject = SecurityLocksSharedPreferences.GetObject(context2);
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date date = new Date(currentTimeMillis);
        System.out.println(simpleDateFormat.format(date));
        TryAttemptEntity tryAttemptEntity = new TryAttemptEntity();
        tryAttemptEntity.SetLoginOption(GetObject.GetLoginType());
        tryAttemptEntity.SetWrongPassword(str);
        tryAttemptEntity.SetImagePath(str2);
        tryAttemptEntity.SetTryAttemptTime(date.toString());
        tryAttemptEntity.SetIsCheck(Boolean.valueOf(false));
        this.tryAttemptEntities = new ArrayList<>();
        TryAttemptsSharedPreferences GetObject2 = TryAttemptsSharedPreferences.GetObject(context2);
        this.tryAttemptEntities = GetObject2.GetTryAttemptObject();
        ArrayList<TryAttemptEntity> arrayList = this.tryAttemptEntities;
        if (arrayList == null) {
            this.tryAttemptEntities = new ArrayList<>();
            this.tryAttemptEntities.add(tryAttemptEntity);
        } else {
            arrayList.add(tryAttemptEntity);
        }
        GetObject2.SetTryAttemptObject(this.tryAttemptEntities);
    }

    public void TryAttempt(Context context2) {
        this.context = context2;
        if (LoginActivity.mCamera != null) {
            new Thread() {
                public void run() {
                    try {
                        Boolean valueOf = Boolean.valueOf(true);
                        while (valueOf.booleanValue()) {
                            if (SecurityLocksCommon.IsPreviewStarted) {


                                Camera.Parameters param = mCamera.getParameters();
                                Camera.Size bestSize = null;
                                List<Camera.Size> sizeList = mCamera.getParameters().getSupportedPreviewSizes();
                                bestSize = sizeList.get(0);
                                for (int i = 1; i < sizeList.size(); i++) {
                                    if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
                                        bestSize = sizeList.get(i);
                                    }
                                }

                                List<Integer> supportedPreviewFormats = param.getSupportedPreviewFormats();
                                Iterator<Integer> supportedPreviewFormatsIterator = supportedPreviewFormats.iterator();
                                while (supportedPreviewFormatsIterator.hasNext()) {
                                    Integer previewFormat = supportedPreviewFormatsIterator.next();
                                    if (previewFormat == ImageFormat.YV12) {
                                        param.setPreviewFormat(previewFormat);
                                    }
                                }

                                param.setPreviewSize(bestSize.width, bestSize.height);

                                param.setPictureSize(bestSize.width, bestSize.height);

                                mCamera.setParameters(param);

                                LoginActivity.mCamera.takePicture(null, null, mPicture);
                                valueOf = Boolean.valueOf(false);
                            }
                        }
                    } catch (Exception e) {
                        Log.v("TakePicture Exception", e.toString());
                    }
                }
            }.start();
        }
    }

    public void initCamera(Context context2) {
        try {
            PackageManager packageManager = context2.getPackageManager();
            if (VERSION.SDK_INT >= 9 && packageManager.hasSystemFeature("android.hardware.camera")) {
                if (Camera.getNumberOfCameras() == 2) {
                    LoginActivity.mCamera = Camera.open(1);
                } else if (Camera.getNumberOfCameras() == 1) {
                    LoginActivity.mCamera = Camera.open(0);
                }
                if (LoginActivity.mCamera != null) {
                    LoginActivity.mCameraPreview = new CameraPreview(context2, LoginActivity.mCamera);
                    ((FrameLayout) LoginActivity.this.findViewById(R.id.camera_preview)).addView(LoginActivity.mCameraPreview);
                    SecurityLocksCommon.IsPreviewStarted = true;
                }
            }
        } catch (Exception unused) {
            SecurityLocksCommon.IsPreviewStarted = false;
        }
    }


    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {

        public void onProgressUpdate(Integer... numArr) {
        }

        private MyAsyncTask() {
        }


        public Double doInBackground(String... strArr) {
            postData(strArr[0], strArr[1], strArr[2]);
            return null;
        }


        public void onPostExecute(Double d) {
            if (LoginOptions.Password.toString().equals(LoginActivity.this.LoginOption)) {
                Toast.makeText(LoginActivity.this, R.string.toast_forgot_recovery_Success_Password_sent, Toast.LENGTH_LONG).show();
            } else if (LoginOptions.Pin.toString().equals(LoginActivity.this.LoginOption)) {
                Toast.makeText(LoginActivity.this, R.string.toast_forgot_recovery_Success_Pin_sent, Toast.LENGTH_LONG).show();
            } else if (LoginOptions.Pattern.toString().equals(LoginActivity.this.LoginOption)) {
                Toast.makeText(LoginActivity.this, R.string.toast_forgot_recovery_Success_Pattern_sent, Toast.LENGTH_LONG).show();
            }
        }

        public void postData(String str, String str2, String str3) {

            Log.e("AppType", "AppType");
            Log.e("Email", "" + str2);
            Log.e("Pass", "" + str);
            Log.e("PassType", "" + str3);

            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(SecurityLocksCommon.ServerAddress);
            try {
                ArrayList arrayList = new ArrayList(3);
                arrayList.add(new BasicNameValuePair("AppType", "Android"));
                arrayList.add(new BasicNameValuePair("Email", str2));
                arrayList.add(new BasicNameValuePair("Pass", str));
                arrayList.add(new BasicNameValuePair("PassType", str3));
                httpPost.setEntity(new UrlEncodedFormEntity(arrayList));
                inputStreamToString(defaultHttpClient.execute(httpPost).getEntity().getContent()).toString().toString().equalsIgnoreCase("Successfully");
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

        private StringBuilder inputStreamToString(InputStream inputStream) {
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                try {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    sb.append(readLine);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb;
        }
    }

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onStart() {
        super.onStart();

        initCamera(this);

    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_login);
        SecurityLocksCommon.IsAppDeactive = true;



        this.securityLocksSharedPreferences = SecurityLocksSharedPreferences.GetObject(this);
        this.LoginOption = this.securityLocksSharedPreferences.GetLoginType();
        Common.loginCountForRateAndReview = this.securityLocksSharedPreferences.GetRateCountForRateAndReview();
        Common.loginCountForRateAndReview++;
        this.securityLocksSharedPreferences.SetRateCountForRateAndReview(Common.loginCountForRateAndReview);
        this.mypass = this.securityLocksSharedPreferences.GetSecurityCredential();
        StorageOptionsCommon.STORAGEPATH = StorageOptionSharedPreferences.GetObject(this).GetStoragePath();
        SecurityLocksCommon.PatternPassword = this.securityLocksSharedPreferences.GetSecurityCredential();
        Utilities.CheckDeviceStoragePaths(this);
        Common.initImageLoader(this);
        new Thread() {
            public void run() {
                try {
                    Utilities.changeFileExtention(StorageOptionsCommon.VIDEOS);
                    Utilities.changeFileExtention(StorageOptionsCommon.DOCUMENTS);
                } catch (Exception unused) {
                    Log.v("Login ", "error in changeVideosExtention method");
                }
            }
        }.start();
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.panicSwitchSharedPreferences = PanicSwitchSharedPreferences.GetObject(this);
        PanicSwitchCommon.IsFlickOn = this.panicSwitchSharedPreferences.GetIsFlickOn();
        PanicSwitchCommon.IsShakeOn = this.panicSwitchSharedPreferences.GetIsShakeOn();
        PanicSwitchCommon.IsPalmOnFaceOn = this.panicSwitchSharedPreferences.GetIsPalmOnScreenOn();
        PanicSwitchCommon.SwitchingApp = this.panicSwitchSharedPreferences.GetSwitchApp();


        if (securityLocksSharedPreferences.GetFingerprint()) {

            fingerprintmethod();

        }

        if (this.securityLocksSharedPreferences.GetIsFirstLogin()) {
            SecurityLocksCommon.IsFirstLogin = true;
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, CalculatorPinSetting.class));
            finish();
        } else if (LoginOptions.Calculator.toString().equals(this.LoginOption)) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, MyCalculatorActivity.class));
            finish();
        } else if (LoginOptions.Pattern.toString().equals(this.LoginOption)) {
            setContentView((int) R.layout.pattern_login_activity);
            txt_wrong_pttern = (TextView) findViewById(R.id.txt_wrong_pttern);
            txt_wrong_pttern.setVisibility(View.INVISIBLE);
            ConfirmLockPatternViewLogin confirmLockPatternViewLogin = (ConfirmLockPatternViewLogin) findViewById(R.id.pattern_view);
            this.txtforgotpattern = (TextView) findViewById(R.id.lblforgotpattern);
            this.txtforgotpattern.setVisibility(View.VISIBLE);
            confirmLockPatternViewLogin.setPracticeMode(true);
            confirmLockPatternViewLogin.invalidate();
            this.txtforgotpattern.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (!Utilities.isNetworkOnline(LoginActivity.this)) {
                        LoginActivity.txt_wrong_pttern.setVisibility(View.VISIBLE);
                        LoginActivity.txt_wrong_pttern.setText(R.string.toast_connection_error);
                    } else if (LoginActivity.this.securityLocksSharedPreferences.GetSecurityCredential().length() <= 0 || LoginActivity.this.securityLocksSharedPreferences.GetEmail().length() <= 0) {
                        LoginActivity.txt_wrong_pttern.setVisibility(View.VISIBLE);
                        LoginActivity.txt_wrong_pttern.setText(R.string.toast_forgot_recovery_fail_Pattern);
                    } else {
                        new MyAsyncTask().execute(new String[]{LoginActivity.this.mypass, LoginActivity.this.securityLocksSharedPreferences.GetEmail(), LoginActivity.this.LoginOption});
                        Toast.makeText(LoginActivity.this, R.string.toast_forgot_recovery_Success_Pattern, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            setContentView((int) R.layout.activity_login);
            getWindow().setSoftInputMode(5);
            this.txtPassword = (EditText) findViewById(R.id.txtPassword);
            this.txtPassword.setTextColor(getResources().getColor(R.color.ColorWhite));
            this.txtforgotpassword = (TextView) findViewById(R.id.txtforgotpassword);
            this.txt_wrong_password_pin = (TextView) findViewById(R.id.txt_wrong_password_pin);
            this.txt_wrong_password_pin.setVisibility(View.INVISIBLE);
            this.tv_forgot = (TextView) findViewById(R.id.tv_forgot);
            this.tv_forgot.setVisibility(View.INVISIBLE);
            this.txtforgotpassword.setVisibility(View.VISIBLE);
            this.txtPassword.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    LoginActivity.this.txt_wrong_password_pin.setText("Enter Password");
                    if (LoginActivity.this.txtPassword.length() >= 4 && LoginActivity.this.securityLocksSharedPreferences.GetSecurityCredential().equals(LoginActivity.this.txtPassword.getText().toString())) {
                        LoginActivity.this.SignIn();
                    }
                }
            });
            this.txtPassword.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == 6) {
                        LoginActivity.this.SignIn();
                    }
                    return true;
                }
            });
            this.txtforgotpassword.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (!Utilities.isNetworkOnline(LoginActivity.this)) {
                        ((InputMethodManager) LoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                        LoginActivity.this.tv_forgot.setVisibility(View.VISIBLE);
                        LoginActivity.this.tv_forgot.setText(R.string.toast_connection_error);
                    } else if (LoginActivity.this.securityLocksSharedPreferences.GetSecurityCredential().length() <= 0 || LoginActivity.this.securityLocksSharedPreferences.GetEmail().length() <= 0) {
                        ((InputMethodManager) LoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                        if (LoginOptions.Pin.toString().equals(LoginActivity.this.LoginOption)) {
                            LoginActivity.this.tv_forgot.setVisibility(View.VISIBLE);
                            LoginActivity.this.tv_forgot.setText(R.string.toast_forgot_recovery_fail_Pin);
                            return;
                        }
                        LoginActivity.this.tv_forgot.setVisibility(View.VISIBLE);
                        LoginActivity.this.tv_forgot.setText(R.string.toast_forgot_recovery_fail_Password);
                    } else {
                        new MyAsyncTask().execute(new String[]{LoginActivity.this.mypass, LoginActivity.this.securityLocksSharedPreferences.GetEmail(), LoginActivity.this.LoginOption});
                        if (LoginOptions.Pin.toString().equals(LoginActivity.this.LoginOption)) {
                            Toast.makeText(LoginActivity.this, R.string.toast_forgot_recovery_Success_Pin, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.toast_forgot_recovery_Success_Password, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            if (this.securityLocksSharedPreferences.GetSecurityCredential().length() == 0) {
                if (LoginOptions.Pin.toString().equals(this.LoginOption)) {
                    this.txtPassword.setHint(R.string.lblsetting_SecurityCredentials_SetyourPin);
                } else {
                    this.txtPassword.setHint(R.string.lblsetting_SecurityCredentials_SetyourPassword);
                }
            }
            if (LoginOptions.Pin.toString().equals(this.LoginOption)) {
                this.txtforgotpassword.setText(R.string.lbl_Forgot_pin);
                this.txtPassword.setHint(R.string.lbl_Enter_pin);
                this.txtPassword.setInputType(2);
                this.txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
        tryAttemptCount = Common.TryAttemptCount;
        CheckTryAttemptCount(false);
        String string = getSharedPreferences("whatsnew", 0).getString("AppVersion", "");
        if (!this.securityLocksSharedPreferences.GetIconChanged()) {
            string.equals("");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fingerprintmethod() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {


            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);


            if (!fingerprintManager.isHardwareDetected()) {

                printtoast("Your device doesn't support fingerprint authentication");

            }


            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                printtoast("Please enable the fingerprint permission");

            }

            if (!fingerprintManager.hasEnrolledFingerprints()) {
                printtoast("No fingerprint configured. Please register at least one fingerprint in your device's Settings");

            }

            if (!keyguardManager.isKeyguardSecure()) {
                printtoast("Please enable lockscreen security in your device's Settings");
            } else {
                try {

                    generateKey();
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }
                if (initCipher()) {
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler helper = new FingerprintHandler(this);
                    helper.startAuth(fingerprintManager, cryptoObject);
                }
            }

        }

    }

    private void printtoast(String toaststring) {

        Toast.makeText(this, toaststring, Toast.LENGTH_SHORT).show();

    }


    private void generateKey() throws FingerprintException {
        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore");


            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setUserAuthenticationRequired(true).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build());

            keyGenerator.generateKey();

        } catch (KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | CertificateException | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }


    }


    public boolean initCipher() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }


    private class FingerprintException extends Exception {

        public FingerprintException(Exception e) {
            super(e);
        }
    }

    public void btnLoginonClick(View view) {
        SignIn();
    }


    public void SignIn() {
        if (this.txtPassword.getText().toString().length() > 0) {
            this.securityLocksSharedPreferences.GetEmail();
            if (this.securityLocksSharedPreferences.GetSecurityCredential().equals(this.txtPassword.getText().toString())) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.TryAttemptCount = 0;
                SecurityLocksCommon.IsFakeAccount = 0;
                if (!SecurityLocksCommon.IsAppDeactive || SecurityLocksCommon.CurrentActivity == null) {
                    Common.loginCount = this.securityLocksSharedPreferences.GetRateCount();
                    Common.loginCount++;
                    this.securityLocksSharedPreferences.SetRateCount(Common.loginCount);
                    SecurityLocksCommon.IsnewloginforAd = true;
                    SecurityLocksCommon.IsFakeAccount = 0;
                    Intent intent = new Intent(this, MainiFeaturesActivity.class);
                    overridePendingTransition(17432576, 17432577);
                    startActivity(intent);
                    finish();
                    return;
                }
                Common.loginCount = this.securityLocksSharedPreferences.GetRateCount();
                Common.loginCount++;
                this.securityLocksSharedPreferences.SetRateCount(Common.loginCount);
                SecurityLocksCommon.IsnewloginforAd = true;
                SecurityLocksCommon.IsFakeAccount = 0;
                Intent intent2 = new Intent(this, SecurityLocksCommon.CurrentActivity.getClass());
                overridePendingTransition(17432576, 17432577);
                startActivity(intent2);
                finish();
            } else if (this.securityLocksSharedPreferences.GetDecoySecurityCredential().equals(this.txtPassword.getText().toString())) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.TryAttemptCount = 0;
                Common.loginCount = this.securityLocksSharedPreferences.GetRateCount();
                Common.loginCount++;
                this.securityLocksSharedPreferences.SetRateCount(Common.loginCount);
                SecurityLocksCommon.IsFakeAccount = 1;
                Intent intent3 = new Intent(this, MainiFeaturesActivity.class);
                overridePendingTransition(17432576, 17432577);
                startActivity(intent3);
                finish();
            } else {
                tryAttemptCount++;
                Common.TryAttemptCount = tryAttemptCount;
                TryAttempt(this);
                wrongPassword = this.txtPassword.getText().toString();
                this.txtPassword.setText("");
                this.txt_wrong_password_pin.setVisibility(View.VISIBLE);
                if (LoginOptions.Pin.toString().equals(this.LoginOption)) {
                    this.txt_wrong_password_pin.setText(R.string.lblsetting_SecurityCredentials_Setpin_Tryagain);
                } else {
                    this.txt_wrong_password_pin.setText(R.string.lblsetting_SecurityCredentials_Setpasword_Tryagain);
                }
                CheckTryAttemptCount(true);
            }
        } else if (LoginOptions.Pin.toString().equals(this.LoginOption)) {
            this.txt_wrong_password_pin.setText(R.string.lblsetting_SecurityCredentials_Setpin_Tryagain);
        } else {
            this.txt_wrong_password_pin.setText(R.string.lblsetting_SecurityCredentials_Setpasword_Tryagain);
        }
    }

    private void CheckTryAttemptCount(boolean z) {
        if (z && tryAttemptCount == 3) {
            Common.IsStart = true;
        }
        if (tryAttemptCount == 3) {
            TimerStart();
        }
    }

    private void TimerStart() {
        if (Common.IsStart) {
            Calendar instance = Calendar.getInstance();
            instance.add(13, 30);
            instance.getTimeInMillis();
            startTime = instance.getTimeInMillis();
            Common.IsStart = false;
        }
        this.customHandler.postDelayed(this.updateTimerThread, 0);
    }

    private void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read != -1) {
                outputStream.write(bArr, 0, read);
            } else {
                return;
            }
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


    public void onStop() {
        super.onStop();
        if (!LoginOptions.Pattern.toString().equals(this.LoginOption)) {
            this.txtPassword.setText("");
        }
    }


    public void onPause() {
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        Camera camera = mCamera;
        if (camera != null) {
            camera.cancelAutoFocus();
            mCamera.stopPreview();
            mCamera.release();
            mCameraPreview = null;
            mCamera = null;
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            Log.i("app close", "appdeac true");
            finish();
            System.exit(0);
        }
        Log.i("app close", "appdeac false");
        super.onPause();
    }


    @SuppressLint({"NewApi"})
    public void onResume() {
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
        super.onResume();
    }

    @SuppressLint({"NewApi"})
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            if (VERSION.SDK_INT >= 16) {
                finishAffinity();
            } else {
                finish();
            }
        }
        return super.onKeyDown(i, keyEvent);
    }


    public void onDestroy() {
        super.onDestroy();
    }


    public void onBillingInitialized() {

        Common.isPurchased = true;
        AppSettingsSharedPreferences.GetObject(this).setIsPurchased(true);
    }
}
