package com.example.vault.wallet;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.vault.R;
import com.example.vault.adapter.ExpandableListAdapter1;
import com.example.vault.common.Constants;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.panicswitch.AccelerometerListener;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securebackupcloud.CloudCommon;
import com.example.vault.securebackupcloud.CloudCommon.DropboxType;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;
import com.example.vault.wallet.adapter.WalletCategoriesAdapter;
import com.example.vault.wallet.model.WalletCategoriesFileDB_Pojo;
import com.example.vault.wallet.util.CommonSharedPreferences;
import com.example.vault.wallet.util.WalletCategoriesDAL;
import com.example.vault.wallet.util.WalletCommon;

public class WalletCategoriesActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    boolean IsSortingDropdown = false;
    int categoryCount = 0;
    List<WalletCategoriesFileDB_Pojo> categoryFileDB_List;
    Constants constants;
    WalletCategoriesAdapter gvAdapter;
    GridView gv_wallet;
    boolean isEdittable = false;
    public boolean isGridview = true;
    private SensorManager sensorManager;
    int sortBy;
    private Toolbar toolbar;

    WalletCategoriesDAL walletCategoriesDAL;
    WalletCommon walletCommon;
    CommonSharedPreferences walletSharedPreferences;

    public enum SortBy {
        Name,
        Time
    }

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_wallet);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.gv_wallet = (GridView) findViewById(R.id.gv_wallet);

        this.walletSharedPreferences = CommonSharedPreferences.GetObject(this);
        this.categoryFileDB_List = new ArrayList();
        this.walletCategoriesDAL = new WalletCategoriesDAL(this);
        this.walletCommon = new WalletCommon();
        this.constants = new Constants();
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        boolean z = true;
        SecurityLocksCommon.IsAppDeactive = true;
        setSupportActionBar(this.toolbar);
    TextView title3 = findViewById(R.id.title3);
    title3.setText("Password");
//    getSupportActionBar().setTitle((CharSequence) getResources().getString(R.string.wallet));
    // getSupportActionBar().setTitle("");

    this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
        if (this.walletSharedPreferences.get_ViewByWalletCategory() == 0) {
            z = false;
        }
        this.isGridview = z;
        this.sortBy = this.walletSharedPreferences.get_sortByWalletCategory();
        this.walletCommon.createDefaultCategories(this);
        setGridview();
        this.gv_wallet.setSelection(WalletCommon.walletCategoryScrollIndex);
        this.gv_wallet.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                WalletCommon.WalletCurrentCategoryId = ((WalletCategoriesFileDB_Pojo) categoryFileDB_List.get(i)).getCategoryFileId();
                WalletCommon.walletCurrentCategoryName = ((WalletCategoriesFileDB_Pojo) categoryFileDB_List.get(i)).getCategoryFileName();
                WalletCommon.walletCategoryScrollIndex = i;
                SecurityLocksCommon.IsAppDeactive = false;
                startActivity(new Intent(WalletCategoriesActivity.this, WalletEntriesActivity.class));
                finish();
            }
        });
    }

    public void setGridview() {
        if (this.sortBy == SortBy.Name.ordinal()) {
            WalletCategoriesDAL walletCategoriesDAL2 = this.walletCategoriesDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
            this.constants.getClass();
            sb.append("WalletCategoriesFileIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            sb.append(" ORDER BY ");
            this.constants.getClass();
            sb.append("WalletCategoriesFileName");
            sb.append(" COLLATE NOCASE ASC");
            this.categoryFileDB_List = walletCategoriesDAL2.getAllCategoriesInfoFromDatabase(sb.toString());
        } else {
            WalletCategoriesDAL walletCategoriesDAL3 = this.walletCategoriesDAL;
            StringBuilder sb2 = new StringBuilder();
            this.constants.getClass();
            sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
            this.constants.getClass();
            sb2.append("WalletCategoriesFileIsDecoy");
            sb2.append(" = ");
            sb2.append(SecurityLocksCommon.IsFakeAccount);
            sb2.append(" ORDER BY ");
            this.constants.getClass();
            sb2.append("WalletCategoriesFileModifiedDate");
            sb2.append(" DESC");
            this.categoryFileDB_List = walletCategoriesDAL3.getAllCategoriesInfoFromDatabase(sb2.toString());
        }
        this.gvAdapter = new WalletCategoriesAdapter(this, this.categoryFileDB_List);
        this.gvAdapter.setFocusedPosition(0);
        this.gvAdapter.setIsEdit(false);
        this.gvAdapter.setIsGridview(this.isGridview);
        this.gv_wallet.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), this.isGridview));
        this.gv_wallet.setAdapter(this.gvAdapter);
        this.gvAdapter.notifyDataSetChanged();
    }

    public void showPopupWindow() {
        View inflate = ((LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_window_expandable, null);
        final PopupWindow popupWindow = new PopupWindow(inflate, -2, -2, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        final ArrayList arrayList = new ArrayList();
        final HashMap hashMap = new HashMap();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ExpandableListView expandableListView = (ExpandableListView) inflate.findViewById(R.id.expListview);
        arrayList.add(getResources().getString(R.string.view_by));
        arrayList2.add(getResources().getString(R.string.list));
        arrayList2.add(getResources().getString(R.string.tile));
        hashMap.put(arrayList.get(0), arrayList2);
        arrayList.add(getResources().getString(R.string.sort_by));
        arrayList3.add(getResources().getString(R.string.name));
        arrayList3.add(getResources().getString(R.string.time));
        hashMap.put(arrayList.get(1), arrayList3);
        expandableListView.setAdapter(new ExpandableListAdapter1(this, arrayList, hashMap));
        expandableListView.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
                String str = ((String) ((List) hashMap.get(arrayList.get(i))).get(i2)).toString();
                if (str.equals(getResources().getString(R.string.tile))) {
                    WalletCategoriesActivity walletCategoriesActivity = WalletCategoriesActivity.this;
                    walletCategoriesActivity.isGridview = true;
                    walletCategoriesActivity.walletSharedPreferences.set_ViewByWalletCategory(isGridview ? 1 : 0);
                }
                if (str.equals(getResources().getString(R.string.list))) {
                    WalletCategoriesActivity walletCategoriesActivity2 = WalletCategoriesActivity.this;
                    walletCategoriesActivity2.isGridview = false;
                    walletCategoriesActivity2.walletSharedPreferences.set_ViewByWalletCategory(isGridview ? 1 : 0);
                }
                if (str.equals(getResources().getString(R.string.name))) {
                    WalletCategoriesActivity walletCategoriesActivity3 = WalletCategoriesActivity.this;
                    walletCategoriesActivity3.sortBy = 0;
                    walletCategoriesActivity3.walletSharedPreferences.set_sortByWalletCategory(sortBy);
                }
                if (str.equals(getResources().getString(R.string.time))) {
                    WalletCategoriesActivity walletCategoriesActivity4 = WalletCategoriesActivity.this;
                    walletCategoriesActivity4.sortBy = 1;
                    walletCategoriesActivity4.walletSharedPreferences.set_sortByWalletCategory(sortBy);
                }
                setGridview();
                popupWindow.dismiss();
                return false;
            }
        });
        if (!this.IsSortingDropdown) {
            Toolbar toolbar2 = this.toolbar;
            popupWindow.showAsDropDown(toolbar2, toolbar2.getWidth(), 0);
            this.IsSortingDropdown = true;
            return;
        }
        popupWindow.dismiss();
        this.IsSortingDropdown = false;
    }

    public void bindSearchResult(List<WalletCategoriesFileDB_Pojo> list) {
        this.gvAdapter = new WalletCategoriesAdapter(this, list);
        this.gv_wallet.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), this.isGridview));
        this.gvAdapter.setIsGridview(this.isGridview);
        this.gv_wallet.setAdapter(this.gvAdapter);
        this.gvAdapter.notifyDataSetChanged();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.gv_wallet.setNumColumns(Utilities.getNoOfColumns(this, configuration.orientation, true));
        setGridview();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_cloud_view_sort, menu);
        ((SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search))).setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                return true;
            }

            public boolean onQueryTextChange(String str) {
                List arrayList = new ArrayList();
                if (str.length() > 0) {
                    for (WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo : categoryFileDB_List) {
                        if (walletCategoriesFileDB_Pojo.getCategoryFileName().toLowerCase().contains(str)) {
                            arrayList.add(walletCategoriesFileDB_Pojo);
                        }
                    }
                } else {
                    arrayList = categoryFileDB_List;
                }
                bindSearchResult(arrayList);
                return true;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            WalletCommon.walletCategoryScrollIndex = 0;
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, MainiFeaturesActivity.class));
            finish();
        } else if (itemId != R.id.action_cloud) {
            if (itemId == R.id.action_viewSort) {
                this.IsSortingDropdown = false;
                showPopupWindow();
            }
        } else if (Common.isPurchased) {
            SecurityLocksCommon.IsAppDeactive = false;
            CloudCommon.ModuleType = DropboxType.Wallet.ordinal();
            Utilities.StartCloudActivity(this);
        }
//        else {
//            SecurityLocksCommon.IsAppDeactive = false;
//            InAppPurchaseActivity._cameFrom = CameFrom.WalletCategory.ordinal();
//            startActivity(new Intent(this, InAppPurchaseActivity.class));
//            finish();
//        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onBackPressed() {
        WalletCommon.walletCategoryScrollIndex = 0;
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, MainiFeaturesActivity.class));
        finish();
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
        super.onResume();
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
    }


    public void onStop() {
        super.onStop();
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
}
