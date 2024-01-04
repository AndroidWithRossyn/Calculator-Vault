package com.example.vault.features;

import static com.example.vault.utilities.Common.AppPackageName;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.vault.R;
import com.example.vault.adapter.GridAdapter;
import com.example.vault.audio.AudioPlayListActivity;
import com.example.vault.documents.DocumentsFolderActivity;
import com.example.vault.gallery.GalleryActivity;
import com.example.vault.tryattempt.TryAttemptActivity;
import com.example.vault.more.AboutActivity;
import com.example.vault.more.MoreCommonMethods;
import com.example.vault.notes.NotesFoldersActivity;
import com.example.vault.panicswitch.AccelerometerListener;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.photo.PhotosAlbumActivty;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.securitylocks.SecurityLocksSharedPreferences;
import com.example.vault.storageoption.SettingActivity;
import com.example.vault.storageoption.StorageOptionSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.todolist.ToDoActivity;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;
import com.example.vault.video.VideosAlbumActivty;
import com.example.vault.wallet.WalletCategoriesActivity;
import com.google.android.material.navigation.NavigationView;

import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog.Builder;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;


public class MainiFeaturesActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener, PermissionCallbacks, NavigationView.OnNavigationItemSelectedListener {

    String LoginOption = "";
    String[] PERMISSIONS = VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? new String[]{"android.permission.CAMERA", "android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO", "android.permission.READ_MEDIA_AUDIO"} : new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    Dialog dialog;
    private GridAdapter featureActivityListAdapter;
    private ArrayList<FeatureActivityEnt> featureEntList;
    private GridView gv_featureGrid;
    boolean isSDCard = false;
    boolean isSelectedStoraged = false;
    String[] perms = VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? new String[]{"android.permission.CAMERA", "android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO", "android.permission.READ_MEDIA_AUDIO"} : new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    int pos;
    SecurityLocksSharedPreferences securityLocksSharedPreferences;
    private SensorManager sensorManager;
    ImageView photo, todolist, notes, password, document, audio, gallary, video;
    StorageOptionSharedPreferences storageOptionSharedPreferences;

    NavigationView navigationView;
    private Toolbar toolbar;
    DrawerLayout drawerLayout;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onPermissionsGranted(int i, @NonNull List<String> list) {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.maindrawer);


        int currentNightMode = getResources ().getConfiguration ().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode){
            case Configuration.UI_MODE_NIGHT_NO:
                changeColor(R.color.black_color);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                changeColor(R.color.white);
                break;
        }


        dialog = new Dialog(MainiFeaturesActivity.this);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(0));
        }
        photo = findViewById(R.id.photo);
        todolist = findViewById(R.id.todolist);
        notes = findViewById(R.id.notes);
        password = findViewById(R.id.password);
        document = findViewById(R.id.document);
        audio = findViewById(R.id.audio);
        gallary = findViewById(R.id.gallary);
        video = findViewById(R.id.video);

        photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainiFeaturesActivity featuresActivity = MainiFeaturesActivity.this;
                featuresActivity.pos = 0;
                SecurityLocksCommon.IsAppDeactive = false;
                featuresActivity.requestPermission(featuresActivity.PERMISSIONS);
//                startActivity(new Intent(FeaturesActivity.this, PhotosAlbumActivty.class));
//                finish();
            }
        });
        todolist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainiFeaturesActivity featuresActivity2 = MainiFeaturesActivity.this;
                featuresActivity2.pos = 7;
                SecurityLocksCommon.IsAppDeactive = false;
                featuresActivity2.requestPermission(featuresActivity2.PERMISSIONS);
//                startActivity(new Intent(FeaturesActivity.this, ToDoActivity.class));
//                finish();
            }
        });
        notes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainiFeaturesActivity featuresActivity2 = MainiFeaturesActivity.this;
                featuresActivity2.pos = 6;
                SecurityLocksCommon.IsAppDeactive = false;
                featuresActivity2.requestPermission(featuresActivity2.PERMISSIONS);
//                startActivity(new Intent(FeaturesActivity.this, NotesFoldersActivity.class));
//                finish();
            }
        });
        password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainiFeaturesActivity featuresActivity2 = MainiFeaturesActivity.this;
                featuresActivity2.pos = 5;
                SecurityLocksCommon.IsAppDeactive = false;
                featuresActivity2.requestPermission(featuresActivity2.PERMISSIONS);
//                startActivity(new Intent(FeaturesActivity.this, WalletCategoriesActivity.class));
//                finish();
            }
        });
        document.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainiFeaturesActivity featuresActivity2 = MainiFeaturesActivity.this;
                featuresActivity2.pos = 4;
                SecurityLocksCommon.IsAppDeactive = false;
                featuresActivity2.requestPermission(featuresActivity2.PERMISSIONS);
//                startActivity(new Intent(FeaturesActivity.this, DocumentsFolderActivity.class));
//                finish();
            }
        });
        audio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainiFeaturesActivity featuresActivity2 = MainiFeaturesActivity.this;
                featuresActivity2.pos = 3;
                SecurityLocksCommon.IsAppDeactive = false;
                featuresActivity2.requestPermission(featuresActivity2.PERMISSIONS);
//                startActivity(new Intent(FeaturesActivity.this, AudioPlayListActivity.class));
//                finish();
            }
        });
        gallary.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainiFeaturesActivity featuresActivity2 = MainiFeaturesActivity.this;
                featuresActivity2.pos = 2;
                SecurityLocksCommon.IsAppDeactive = false;
                featuresActivity2.requestPermission(featuresActivity2.PERMISSIONS);
//                startActivity(new Intent(FeaturesActivity.this, GalleryActivity.class));
//                finish();
            }
        });
        video.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainiFeaturesActivity featuresActivity2 = MainiFeaturesActivity.this;
                featuresActivity2.pos = 1;
                SecurityLocksCommon.IsAppDeactive = false;
                featuresActivity2.requestPermission(featuresActivity2.PERMISSIONS);
//                startActivity(new Intent(FeaturesActivity.this, VideosAlbumActivty.class));
//                finish();
            }
        });


        dialog.setCancelable(false);
        dialog.setContentView(R.layout.permission_dialog);
        dialog.findViewById(R.id.btnCancelDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dialog.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityForResult(intent, 100);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 100);
                }
                dialog.dismiss();
            }
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        this.gv_featureGrid = (GridView) findViewById(R.id.gridview);
        navigationView = findViewById(R.id.nav_view);
        this.toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

//        setSupportActionBar(this.toolbar);
//        getSupportActionBar().setTitle((CharSequence) "   Calc Vault");
        // getSupportActionBar().setTitle("");

        final LinearLayout holder = findViewById(R.id.holder);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                float scaleFactor = 7f;
                float slideX = drawerView.getWidth() * slideOffset;

                holder.setTranslationX(slideX);
                holder.setScaleX(1 - (slideOffset / scaleFactor));
                holder.setScaleY(1 - (slideOffset / scaleFactor));

                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        drawerLayout.addDrawerListener(toggle);

        drawerLayout.setScrimColor(Color.TRANSPARENT);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.securityLocksSharedPreferences = SecurityLocksSharedPreferences.GetObject(this);
        Common.initImageLoader(this);
//        this.gv_featureGrid.setOnItemClickListener(new OnItemClickListener() {
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
//                switch (i) {
//                    case 0:
//                        FeaturesActivity featuresActivity = FeaturesActivity.this;
//                        featuresActivity.pos = i;
//                        SecurityLocksCommon.IsAppDeactive = false;
//                        featuresActivity.requestPermission(featuresActivity.PERMISSIONS);
//                        return;
//                    case 1:
//                        FeaturesActivity featuresActivity2 = FeaturesActivity.this;
//                        featuresActivity2.pos = i;
//                        SecurityLocksCommon.IsAppDeactive = false;
//                        featuresActivity2.requestPermission(featuresActivity2.PERMISSIONS);
//                        return;
//                    case 2:
//                        FeaturesActivity featuresActivity3 = FeaturesActivity.this;
//                        featuresActivity3.pos = i;
//                        SecurityLocksCommon.IsAppDeactive = false;
//                        featuresActivity3.requestPermission(featuresActivity3.PERMISSIONS);
//                        return;
//                    case 3:
//                        FeaturesActivity featuresActivity4 = FeaturesActivity.this;
//                        featuresActivity4.pos = i;
//                        SecurityLocksCommon.IsAppDeactive = false;
//                        featuresActivity4.requestPermission(featuresActivity4.PERMISSIONS);
//                        return;
//                    case 4:
//                        FeaturesActivity featuresActivity5 = FeaturesActivity.this;
//                        featuresActivity5.pos = i;
//                        SecurityLocksCommon.IsAppDeactive = false;
//                        featuresActivity5.requestPermission(featuresActivity5.PERMISSIONS);
//                        return;
//                    case 5:
//                        FeaturesActivity featuresActivity6 = FeaturesActivity.this;
//                        featuresActivity6.pos = i;
//                        SecurityLocksCommon.IsAppDeactive = false;
//                        featuresActivity6.requestPermission(featuresActivity6.PERMISSIONS);
//                        return;
//                    case 6:
//                        FeaturesActivity featuresActivity7 = FeaturesActivity.this;
//                        featuresActivity7.pos = i;
//                        SecurityLocksCommon.IsAppDeactive = false;
//                        featuresActivity7.requestPermission(featuresActivity7.PERMISSIONS);
//                        return;
//                    case 7:
//                        FeaturesActivity featuresActivity8 = FeaturesActivity.this;
//                        featuresActivity8.pos = i;
//                        SecurityLocksCommon.IsAppDeactive = false;
//                        featuresActivity8.requestPermission(featuresActivity8.PERMISSIONS);
//                        return;
//                    case 9:
//                        FeaturesActivity featuresActivity9 = FeaturesActivity.this;
//                        featuresActivity9.pos = i;
//                        SecurityLocksCommon.IsAppDeactive = false;
//                        featuresActivity9.requestPermission(featuresActivity9.PERMISSIONS);
//                        return;
//                    case 10:
//                        boolean isGetCalModeEnable = FeaturesActivity.this.securityLocksSharedPreferences.isGetCalModeEnable();
//                        return;
//                    case 11:
//                        SecurityLocksCommon.IsAppDeactive = false;
//                        return;
//                    default:
//                        return;
//                }
//            }
//        });
//        SetFeatureinGridView();
//        Rate();
    }

    public void changeColor(int resourseColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor( ContextCompat.getColor(getApplicationContext(), resourseColor));
        }
    }


//    public void SetFeatureinGridView() {
//        this.featureEntList = new FeatureActivityMethods().GetFeatures(this);
//        this.featureActivityListAdapter = new GridAdapter(this, 1, this.featureEntList);
//        this.gv_featureGrid.setAdapter(this.featureActivityListAdapter);
//        this.featureActivityListAdapter.notifyDataSetChanged();
//    }


    @AfterPermissionGranted(123)
    public void requestPermission(String[] strArr) {
        if (EasyPermissions.hasPermissions(this, strArr)) {

            int i = pos;
            IntergerActivty(i);
        } else {
            EasyPermissions.requestPermissions((Activity) this, "For the best Calc Vault experience, please Allow Permission", 123, strArr);
        }

    }


    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return (checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        }
    }

    //    public void requestStoragePermission() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                intent.addCategory("android.intent.category.DEFAULT");
//                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
//                startActivityForResult(intent, 100);
//            } catch (Exception e) {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivityForResult(intent, 100);
//            }
//        }else {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//        }
//
//    }
    public void showManagePermDialog(Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "$this$showManagePermDialog");
        if (dialog != null && !isFinishing()) {
            dialog.show();
        }
    }

    public void requestStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            showManagePermDialog(MainiFeaturesActivity.this);
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 100);

//          String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//            String rationale = "Please provide storage permission so that you can use our app";
//            Permissions.Options options = new Permissions.Options()
//                    .setRationaleDialogTitle("Info")
//                    .setSettingsDialogTitle("Warning");
//
//            Permissions.check(this/*context*/, permissions, rationale, options, new PermissionHandler() {
//                @Override
//                public void onGranted() {
//                    startActivity(new Intent(GoToAppActivity.this, MainActivity.class));
//                }
//
//                @Override
//                public void onDenied(Context context, ArrayList<String> deniedPermissions) {
//                    finish();
//                }
//            });
            // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }


    private void IntergerActivty(int i) {

        if (i == 0) {

            startActivity(new Intent(MainiFeaturesActivity.this, PhotosAlbumActivty.class));
            finish();


        } else if (i == 1) {

            startActivity(new Intent(this, VideosAlbumActivty.class));
            finish();


        } else if (i == 2) {

            startActivity(new Intent(MainiFeaturesActivity.this, GalleryActivity.class));
            finish();


        } else if (i == 3) {


            startActivity(new Intent(this, AudioPlayListActivity.class));
            finish();


        } else if (i == 4) {

            startActivity(new Intent(this, DocumentsFolderActivity.class));
            finish();


        } else if (i == 5) {


            startActivity(new Intent(this, WalletCategoriesActivity.class));
            finish();


        } else if (i == 6) {


            startActivity(new Intent(this, NotesFoldersActivity.class));
            finish();


        } else if (i == 7) {

            startActivity(new Intent(this, ToDoActivity.class));
            finish();


        } else if (i != 8) {
        }

    }


    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);

        if (iArr.length > 0 && iArr[0] == 0) {
            int i2 = this.pos;

            IntergerActivty(i2);
//            if (i2 == 0) {
//                startActivity(new Intent(this, PhotosAlbumActivty.class));
//                finish();
//            } else if (i2 == 1) {
//                startActivity(new Intent(this, VideosAlbumActivty.class));
//                finish();
//            } else if (i2 == 2) {
//                startActivity(new Intent(this, GalleryActivity.class));
//                finish();
//            } else if (i2 == 3) {
//                startActivity(new Intent(this, AudioPlayListActivity.class));
//                finish();
//            } else if (i2 == 4) {
//                startActivity(new Intent(this, DocumentsFolderActivity.class));
//                finish();
//            } else if (i2 == 5) {
//                startActivity(new Intent(this, WalletCategoriesActivity.class));
//                finish();
//            } else if (i2 == 6) {
//                startActivity(new Intent(this, NotesFoldersActivity.class));
//                finish();
//            } else if (i2 == 7) {
//                startActivity(new Intent(this, ToDoActivity.class));
//                finish();
//            } else if (i2 == 8) {
//                startActivity(new Intent(this, ToDoActivity.class));
//                finish();
//            }
            Toast.makeText(getApplicationContext(), "Permission is granted ", Toast.LENGTH_SHORT).show();
        } else if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.CAMERA") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_MEDIA_IMAGES") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_MEDIA_VIDEO") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_MEDIA_AUDIO") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.PROCESS_OUTGOING_CALLS") : ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.CAMERA") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.PROCESS_OUTGOING_CALLS")) {
            if (!EasyPermissions.hasPermissions(this, this.perms)) {
                EasyPermissions.requestPermissions((Activity) this, "For the best Calc Vault experience, please Allow Permission", 123, this.perms);
            }
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.onRequestPermissionsResult(i, strArr, iArr, this);
        }
    }

    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied((Activity) this, list)) {
            new Builder((Activity) this).build().show();
        }
    }


//    public void onActivityResult(int i, int i2, Intent intent) {
//        super.onActivityResult(i, i2, intent);
//        try {
//            if (!Common.isPurchased && !this.bp.handleActivityResult(i, i2, intent)) {
//                super.onActivityResult(i, i2, intent);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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


    public void onResume() {
        if (!isStoragePermissionGranted()) {
            requestStoragePermission();
        }
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            if (!this.isSelectedStoraged) {
                SecurityLocksCommon.IsAppDeactive = false;
                this.storageOptionSharedPreferences = StorageOptionSharedPreferences.GetObject(this);
                StorageOptionsCommon.STORAGEPATH = this.storageOptionSharedPreferences.GetStoragePath();
                if (StorageOptionsCommon.STORAGEPATH.length() <= 0) {
                    StorageOptionsCommon.STORAGEPATH = StorageOptionsCommon.STORAGEPATH_1;
                    this.storageOptionSharedPreferences.SetStoragePath(StorageOptionsCommon.STORAGEPATH);
                }
                this.isSelectedStoraged = true;
                return false;
            } else if (VERSION.SDK_INT >= 16) {
                finishAffinity();
            } else {
                finish();
            }
        }
        return super.onKeyDown(i, keyEvent);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

  /*  public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.action_settings) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, SettingActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.action_more) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, MoreActivity.class));
            finish();
            return true;
        }
//        else {
//            if (itemId == R.id.action_buy) {
//                SecurityLocksCommon.IsAppDeactive = false;
//                InAppPurchaseActivity._cameFrom = CameFrom.Feature.ordinal();
//                startActivity(new Intent(this, InAppPurchaseActivity.class));
//                finish();
//            }
//            return super.onOptionsItemSelected(menuItem);
//        }
        return false;
    }*/


    public boolean onPrepareOptionsMenu(Menu menu) {
        Utilities.hideMenuItems(menu);
        super.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }


//    public void onPurchaseHistoryRestored() {
//        try {
//            if (!Common.isPurchased) {
//                for (String equals : this.bp.listOwnedProducts()) {
//                    if (equals.equals(Common.sku_premium_pack)) {
//                        AppSettingsSharedPreferences.GetObject(this).setIsPurchased(true);
//                        Common.isPurchased = true;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public void onBillingInitialized() {
//        try {
//            if (!Common.isPurchased) {
//                this.bp.loadOwnedPurchasesFromGoogle();
//                if (this.bp.isPurchased(Common.sku_premium_pack)) {
//                    Common.isPurchased = true;
//                    AppSettingsSharedPreferences.GetObject(this).setIsPurchased(true);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void Rate() {
        if (!this.securityLocksSharedPreferences.GetIsAppRated() && Common.loginCountForRateAndReview == 3 && Utilities.isNetworkOnline(this)) {
            Common.loginCountForRateAndReview = 0;
            this.securityLocksSharedPreferences.SetRateCountForRateAndReview(0);
            final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
            dialog.setContentView(R.layout.ratedialog);
            dialog.setCancelable(false);
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            ((LinearLayout) dialog.findViewById(R.id.ll_ratePopup)).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    MainiFeaturesActivity.this.securityLocksSharedPreferences.SetIsAppRated(Boolean.valueOf(true));
                    MainiFeaturesActivity featuresActivity = MainiFeaturesActivity.this;
                    StringBuilder sb = new StringBuilder();
                    sb.append("market://details?id=");
                    sb.append(AppPackageName);
                    featuresActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(sb.toString())));
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.ll_rate_five_star).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    MainiFeaturesActivity.this.securityLocksSharedPreferences.SetIsAppRated(Boolean.valueOf(true));
                    MainiFeaturesActivity featuresActivity = MainiFeaturesActivity.this;
                    StringBuilder sb = new StringBuilder();
                    sb.append("market://details?id=");
                    sb.append(AppPackageName);
                    featuresActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(sb.toString())));
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.ll_dislike).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    MainiFeaturesActivity.this.securityLocksSharedPreferences.SetIsAppRated(Boolean.valueOf(true));
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType(HTTP.PLAIN_TEXT_TYPE);
                    intent.putExtra("android.intent.extra.EMAIL", new String[]{getString(R.string.Support_value)});
                    intent.putExtra("android.intent.extra.SUBJECT", "I dislike Calc Vault Pro because");
                    intent.putExtra("android.intent.extra.TEXT", "");
                    try {
                        SecurityLocksCommon.IsAppDeactive = false;
                        MainiFeaturesActivity.this.startActivity(Intent.createChooser(intent, "Support via email..."));
                    } catch (ActivityNotFoundException unused) {
                    }
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.ll_later).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_Setting) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, SettingActivity.class));
            finish();
        } else if (itemId == R.id.nav_Try) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(MainiFeaturesActivity.this, TryAttemptActivity.class));
            finish();
        } else if (itemId == R.id.nav_Tell || itemId == R.id.nav_Share) {
            MoreCommonMethods.TellaFriendDialog(MainiFeaturesActivity.this);
        } else if (itemId == R.id.nav_FeedBack) {
            SecurityLocksCommon.IsAppDeactive = false;

            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType(HTTP.PLAIN_TEXT_TYPE);
            intent.putExtra("android.intent.extra.EMAIL", new String[]{getString(R.string.Support_value)});
            intent.putExtra("android.intent.extra.SUBJECT", "Subject Type");
            intent.putExtra("android.intent.extra.TEXT", "Body Type");
            intent.setPackage("com.google.android.gm");
            try {
                SecurityLocksCommon.IsAppDeactive = false;
                MainiFeaturesActivity.this.startActivity(Intent.createChooser(intent, "Support via email..."));
            } catch (ActivityNotFoundException unused) {

            }
        } else if (itemId == R.id.nav_Privacy) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.privacypolicyurl))));
        } else if (itemId == R.id.nav_Rate) {
            SecurityLocksCommon.IsRateReview = true;
            SecurityLocksCommon.IsAppDeactive = false;

            StringBuilder sb = new StringBuilder();
            sb.append("market://details?id=");
            sb.append(AppPackageName);
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(sb.toString())));
        } else if (itemId == R.id.nav_About) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(MainiFeaturesActivity.this, AboutActivity.class));
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawers();
        return false;
    }


}
