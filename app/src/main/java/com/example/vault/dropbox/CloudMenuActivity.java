package com.example.vault.dropbox;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import java.util.ArrayList;
import com.example.vault.R;
import com.example.vault.BaseActivity;
import com.example.vault.dropbox.adapter.CloudMenuAdapter;
import com.example.vault.dropbox.model.CloudMenuEnt;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securebackupcloud.CloudCommon;
import com.example.vault.securebackupcloud.CloudCommon.DropboxType;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.utilities.Utilities;

public class CloudMenuActivity extends BaseActivity {
    private CloudMenuAdapter adapter;
    private ArrayList<CloudMenuEnt> cloudEntList;
    private ListView cloudListView;
    LinearLayout ll_background;
    LinearLayout ll_topbaar;
    private SensorManager sensorManager;
    private Toolbar toolbar;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.cloud_menu_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Cloud");
        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                CloudMenuActivity.this.btnBackonClick();
            }
        });
        this.cloudListView = (ListView) findViewById(R.id.cloudListView);
        this.cloudListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                switch (i) {
                    case 0:
                        SecurityLocksCommon.IsAppDeactive = false;
                        CloudCommon.IsCameFromSettings = false;
                        CloudCommon.IsCameFromCloudMenu = true;
                        CloudCommon.ModuleType = DropboxType.Photos.ordinal();
                        Utilities.StartCloudActivity(CloudMenuActivity.this);
                        return;
                    case 1:
                        SecurityLocksCommon.IsAppDeactive = false;
                        CloudCommon.IsCameFromSettings = false;
                        CloudCommon.IsCameFromCloudMenu = true;
                        CloudCommon.ModuleType = DropboxType.Videos.ordinal();
                        Utilities.StartCloudActivity(CloudMenuActivity.this);
                        return;
                    case 2:
                        SecurityLocksCommon.IsAppDeactive = false;
                        CloudCommon.IsCameFromSettings = false;
                        CloudCommon.IsCameFromCloudMenu = true;
                        CloudCommon.ModuleType = DropboxType.Documents.ordinal();
                        Utilities.StartCloudActivity(CloudMenuActivity.this);
                        return;
                    case 3:
                        SecurityLocksCommon.IsAppDeactive = false;
                        CloudCommon.IsCameFromSettings = false;
                        CloudCommon.IsCameFromCloudMenu = true;
                        CloudCommon.ModuleType = DropboxType.Notes.ordinal();
                        Utilities.StartCloudActivity(CloudMenuActivity.this);
                        return;
                    case 4:
                        SecurityLocksCommon.IsAppDeactive = false;
                        CloudCommon.IsCameFromSettings = false;
                        CloudCommon.IsCameFromCloudMenu = true;
                        CloudCommon.ModuleType = DropboxType.Wallet.ordinal();
                        Utilities.StartCloudActivity(CloudMenuActivity.this);
                        return;
                    case 5:
                        SecurityLocksCommon.IsAppDeactive = false;
                        CloudCommon.IsCameFromSettings = false;
                        CloudCommon.IsCameFromCloudMenu = true;
                        CloudCommon.ModuleType = DropboxType.ToDo.ordinal();
                        Utilities.StartCloudActivity(CloudMenuActivity.this);
                        return;
                    case 6:
                        SecurityLocksCommon.IsAppDeactive = false;
                        CloudCommon.IsCameFromSettings = false;
                        CloudCommon.IsCameFromCloudMenu = true;
                        CloudCommon.ModuleType = DropboxType.Audio.ordinal();
                        Utilities.StartCloudActivity(CloudMenuActivity.this);
                        return;
                    default:
                        return;
                }
            }
        });
        BindCloudMenu();
    }

    public void btnBackonClick() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, MainiFeaturesActivity.class));
        finish();
    }

    private void BindCloudMenu() {
        this.cloudEntList = GetCloudDetail();
        this.adapter = new CloudMenuAdapter(this, 17367043, this.cloudEntList);
        this.cloudListView.setAdapter(this.adapter);
    }

    private ArrayList<CloudMenuEnt> GetCloudDetail() {
        ArrayList<CloudMenuEnt> arrayList = new ArrayList<>();
        if (SecurityLocksCommon.IsFakeAccount != 1) {
            CloudMenuEnt cloudMenuEnt = new CloudMenuEnt();
            cloudMenuEnt.SetCloudHeading(R.string.lblFeature1);
            cloudMenuEnt.SetDrawable(R.drawable.ic_menu_cloud_photo_icon);
            arrayList.add(cloudMenuEnt);
            CloudMenuEnt cloudMenuEnt2 = new CloudMenuEnt();
            cloudMenuEnt2.SetCloudHeading(R.string.lblFeature2);
            cloudMenuEnt2.SetDrawable(R.drawable.ic_menu_cloud_video_icon);
            arrayList.add(cloudMenuEnt2);
            CloudMenuEnt cloudMenuEnt3 = new CloudMenuEnt();
            cloudMenuEnt3.SetCloudHeading(R.string.lblFeature9);
            cloudMenuEnt3.SetDrawable(R.drawable.ic_menu_cloud_audio_icon);
            arrayList.add(cloudMenuEnt3);
            CloudMenuEnt cloudMenuEnt4 = new CloudMenuEnt();
            cloudMenuEnt4.SetCloudHeading(R.string.lblFeature4);
            cloudMenuEnt4.SetDrawable(R.drawable.ic_menu_cloud_documents_icon);
            arrayList.add(cloudMenuEnt4);
            CloudMenuEnt cloudMenuEnt5 = new CloudMenuEnt();
            cloudMenuEnt5.SetCloudHeading(R.string.lblFeature6);
            cloudMenuEnt5.SetDrawable(R.drawable.ic_menu_cloud_notes_icon);
            arrayList.add(cloudMenuEnt5);
            CloudMenuEnt cloudMenuEnt6 = new CloudMenuEnt();
            cloudMenuEnt6.SetCloudHeading(R.string.lblFeature7);
            cloudMenuEnt6.SetDrawable(R.drawable.ic_menu_cloud_password_icon);
            arrayList.add(cloudMenuEnt6);
            CloudMenuEnt cloudMenuEnt7 = new CloudMenuEnt();
            cloudMenuEnt7.SetCloudHeading(R.string.todoList);
            cloudMenuEnt7.SetDrawable(R.drawable.ic_menu_cloud_todos_icon);
            arrayList.add(cloudMenuEnt7);
        }
        return arrayList;
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
