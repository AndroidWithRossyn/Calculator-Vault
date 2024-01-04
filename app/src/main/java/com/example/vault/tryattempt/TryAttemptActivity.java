package com.example.vault.tryattempt;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;
import com.example.vault.R;
import com.example.vault.BaseActivity;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.tryattempt.model.TryAttemptEntity;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securitylocks.SecurityLocksCommon;

public class TryAttemptActivity extends BaseActivity {
    public static ProgressDialog myProgressDialog;
    ListView TryAttemptListView;
    ArrayList<TryAttemptEntity> tryAttemptEntities;
    TryAttemptListAdapter tryAttemptListAdapter;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 2) {
                TryAttemptActivity.this.hideProgress();
            } else if (message.what == 3) {
                TryAttemptActivity.this.hideProgress();
                TryAttemptActivity.this.ChangeCheckboxVisibility(false);
                Toast.makeText(TryAttemptActivity.this, R.string.toast_more_try_attempts_deleted, Toast.LENGTH_SHORT).show();
                SecurityLocksCommon.IsAppDeactive = false;
                TryAttemptActivity.this.startActivity(new Intent(TryAttemptActivity.this, TryAttemptActivity.class));
                TryAttemptActivity.this.overridePendingTransition(17432576, 17432577);
                TryAttemptActivity.this.finish();
            }
            super.handleMessage(message);
        }
    };
    boolean isEditMode = false;
    ImageView iv_tryer_image;
    LinearLayout ll_Delete;
    LinearLayout ll_DeleteAndSelectAll;
    LayoutParams ll_DeleteAndSelectAll_Params;
    LayoutParams ll_DeleteAndSelectAll_Params_hidden;
    LinearLayout ll_TryAttemptTopBaar;
    LinearLayout ll_No_tryattempts;
    LinearLayout ll_SelectAll;
    LinearLayout ll_background;
    LinearLayout ll_tryattempts;
    boolean selectAll = false;
    private SensorManager sensorManager;
    private Toolbar toolbar;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void showDeleteProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }


    public void hideProgress() {
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.try_attempt_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Try Attempts");
        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                TryAttemptActivity.this.btnBackonClick();
            }
        });
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_DeleteAndSelectAll_Params = new LayoutParams(-1, -2);
        this.ll_DeleteAndSelectAll_Params_hidden = new LayoutParams(-2, 0);
        this.ll_DeleteAndSelectAll = (LinearLayout) findViewById(R.id.ll_DeleteAndSelectAll);
        this.ll_DeleteAndSelectAll.setVisibility(View.INVISIBLE);
        this.ll_DeleteAndSelectAll.setLayoutParams(this.ll_DeleteAndSelectAll_Params_hidden);
        this.ll_TryAttemptTopBaar = (LinearLayout) findViewById(R.id.ll_TryAttemptTopBaar);
        this.ll_Delete = (LinearLayout) findViewById(R.id.ll_Delete);
        this.ll_SelectAll = (LinearLayout) findViewById(R.id.ll_SelectAll);
        this.ll_No_tryattempts = (LinearLayout) findViewById(R.id.ll_No_tryattempts);
        this.ll_tryattempts = (LinearLayout) findViewById(R.id.ll_tryattempts);
        this.ll_No_tryattempts.setVisibility(View.VISIBLE);
        this.ll_tryattempts.setVisibility(View.INVISIBLE);
        this.iv_tryer_image = (ImageView) findViewById(R.id.iv_tryattempt_item);
        this.TryAttemptListView = (ListView) findViewById(R.id.TryAttemptListView);
        ((TextView) findViewById(R.id.TryAttemptTopBaar_Title)).setTypeface(createFromAsset);
        TextView textView = (TextView) findViewById(R.id.lblSelectAll);
        ((TextView) findViewById(R.id.lblDelete)).setTypeface(createFromAsset);
        textView.setTypeface(createFromAsset);
        this.TryAttemptListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (!TryAttemptActivity.this.isEditMode) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    Intent intent = new Intent(TryAttemptActivity.this, TryAttemptDetailActivity.class);
                    intent.putExtra("TryerImagePath", ((TryAttemptEntity) TryAttemptActivity.this.tryAttemptEntities.get(i)).GetImagePath());
                    intent.putExtra("WrongPass", ((TryAttemptEntity) TryAttemptActivity.this.tryAttemptEntities.get(i)).GetWrongPassword());
                    intent.putExtra("DateTime", ((TryAttemptEntity) TryAttemptActivity.this.tryAttemptEntities.get(i)).GetTryAttemptTime());
                    intent.putExtra("Position", i);
                    TryAttemptActivity.this.startActivity(intent);
                    TryAttemptActivity.this.finish();
                }
            }
        });
        this.TryAttemptListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                ((TryAttemptEntity) TryAttemptActivity.this.tryAttemptEntities.get(i)).SetIsCheck(Boolean.valueOf(true));
                TryAttemptActivity.this.ll_DeleteAndSelectAll.setVisibility(View.VISIBLE);
                TryAttemptActivity.this.ll_DeleteAndSelectAll.setLayoutParams(TryAttemptActivity.this.ll_DeleteAndSelectAll_Params);
                TryAttemptActivity tryAttemptActivity = TryAttemptActivity.this;
                TryAttemptListAdapter tryAttemptListAdapter = new TryAttemptListAdapter(tryAttemptActivity, 17367043, tryAttemptActivity.tryAttemptEntities, true, Boolean.valueOf(false));
                tryAttemptActivity.tryAttemptListAdapter = tryAttemptListAdapter;
                TryAttemptActivity.this.TryAttemptListView.setAdapter(TryAttemptActivity.this.tryAttemptListAdapter);
                TryAttemptActivity.this.tryAttemptListAdapter.notifyDataSetChanged();
                return true;
            }
        });
        this.ll_Delete.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (TryAttemptActivity.this.tryAttemptEntities == null) {
                    return;
                }
                if (TryAttemptActivity.this.IsFileCheck()) {
                    TryAttemptActivity.this.DeleteTryAttept();
                } else {
                    Toast.makeText(TryAttemptActivity.this, R.string.toast_unselectphotomsg_Tryattempts, Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.ll_SelectAll.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (TryAttemptActivity.this.selectAll) {
                    TryAttemptActivity.this.selectAll = false;
                } else {
                    TryAttemptActivity.this.selectAll = true;
                }
                Iterator it = TryAttemptActivity.this.tryAttemptEntities.iterator();
                while (it.hasNext()) {
                    ((TryAttemptEntity) it.next()).SetIsCheck(Boolean.valueOf(TryAttemptActivity.this.selectAll));
                }
                TryAttemptActivity tryAttemptActivity = TryAttemptActivity.this;
                TryAttemptListAdapter tryAttemptListAdapter = new TryAttemptListAdapter(tryAttemptActivity, 17367043, tryAttemptActivity.tryAttemptEntities, true, Boolean.valueOf(TryAttemptActivity.this.selectAll));
                tryAttemptActivity.tryAttemptListAdapter = tryAttemptListAdapter;
                TryAttemptActivity.this.TryAttemptListView.setAdapter(TryAttemptActivity.this.tryAttemptListAdapter);
                TryAttemptActivity.this.tryAttemptListAdapter.notifyDataSetChanged();
            }
        });
        BindTryAttempsList();
    }

    public void btnBackonClick() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, MainiFeaturesActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TryAttemptActivity.this.btnBackonClick();
    }

    public void DeleteTryAttept() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.confirmation_message_box);
        dialog.setCancelable(true);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
        textView.setTypeface(Typeface.createFromAsset(getAssets(), "ebrima.ttf"));
        textView.setText("Are you sure you want to delete selected Try Attempts?");
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                TryAttemptActivity.this.showDeleteProgress();
                new Thread() {
                    public void run() {
                        try {
                            dialog.dismiss();
                            TryAttemptActivity.this.Delete();
                            Message message = new Message();
                            message.what = 3;
                            TryAttemptActivity.this.handle.sendMessage(message);
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 2;
                            TryAttemptActivity.this.handle.sendMessage(message2);
                        }
                    }
                }.start();
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


    public void Delete() {
        Iterator it = new ArrayList(this.tryAttemptEntities).iterator();
        while (it.hasNext()) {
            TryAttemptEntity tryAttemptEntity = (TryAttemptEntity) it.next();
            if (tryAttemptEntity.GetIsCheck()) {
                this.tryAttemptEntities.remove(tryAttemptEntity);
            }
        }
        TryAttemptsSharedPreferences.GetObject(this).SetTryAttemptObject(this.tryAttemptEntities);
    }


    public boolean IsFileCheck() {
        Iterator it = new ArrayList(this.tryAttemptEntities).iterator();
        while (it.hasNext()) {
            if (((TryAttemptEntity) it.next()).GetIsCheck()) {
                return true;
            }
        }
        return false;
    }

    private void BindTryAttempsList() {
        this.tryAttemptEntities = TryAttemptsSharedPreferences.GetObject(getApplicationContext()).GetTryAttemptObject();
        ArrayList<TryAttemptEntity> arrayList = this.tryAttemptEntities;
        if (arrayList != null) {
            if (arrayList.size() > 0) {
                this.ll_No_tryattempts.setVisibility(View.INVISIBLE);
                this.ll_tryattempts.setVisibility(View.VISIBLE);
            }
            TryAttemptListAdapter tryAttemptListAdapter2 = new TryAttemptListAdapter(this, 17367043, this.tryAttemptEntities, false, Boolean.valueOf(false));
            this.tryAttemptListAdapter = tryAttemptListAdapter2;
            this.TryAttemptListView.setAdapter(this.tryAttemptListAdapter);
            this.tryAttemptListAdapter.notifyDataSetChanged();
            return;
        }
        this.ll_No_tryattempts.setVisibility(View.VISIBLE);
        this.ll_tryattempts.setVisibility(View.INVISIBLE);
    }


    public void ChangeCheckboxVisibility(boolean z) {
        if (z) {
            this.ll_DeleteAndSelectAll.setVisibility(View.VISIBLE);
            this.ll_DeleteAndSelectAll.setLayoutParams(this.ll_DeleteAndSelectAll_Params);
        } else {
            this.ll_DeleteAndSelectAll.setVisibility(View.INVISIBLE);
            this.ll_DeleteAndSelectAll.setLayoutParams(this.ll_DeleteAndSelectAll_Params_hidden);
        }
        Iterator it = this.tryAttemptEntities.iterator();
        while (it.hasNext()) {
            ((TryAttemptEntity) it.next()).SetIsCheck(Boolean.valueOf(z));
        }
        TryAttemptListAdapter tryAttemptListAdapter2 = new TryAttemptListAdapter(this, 17367043, this.tryAttemptEntities, z, Boolean.valueOf(false));
        this.tryAttemptListAdapter = tryAttemptListAdapter2;
        this.TryAttemptListView.setAdapter(this.tryAttemptListAdapter);
        this.tryAttemptListAdapter.notifyDataSetChanged();
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
            if (this.isEditMode) {
                this.isEditMode = false;
                ChangeCheckboxVisibility(false);
            }
//            SecurityLocksCommon.IsAppDeactive = false;
//            startActivity(new Intent(this, MoreActivity.class));
//            finish();
            TryAttemptActivity.this.btnBackonClick();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
