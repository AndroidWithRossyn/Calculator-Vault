package com.example.vault.wallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;


import com.example.vault.wallet.adapter.WalletEntriesAdapter;
import com.example.vault.wallet.model.WalletCategoriesFileDB_Pojo;
import com.example.vault.wallet.model.WalletEntryFileDB_Pojo;
import com.example.vault.wallet.util.CommonSharedPreferences;
import com.example.vault.wallet.util.WalletCategoriesDAL;
import com.example.vault.wallet.util.WalletCommon;
import com.example.vault.wallet.util.WalletEntriesDAL;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.vault.R;
import com.example.vault.adapter.ExpandableListAdapter1;
import com.example.vault.common.Constants;
import com.example.vault.panicswitch.AccelerometerListener;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securebackupcloud.CloudCommon;
import com.example.vault.securebackupcloud.CloudCommon.DropboxType;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;

public class WalletEntriesActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    boolean IsSortingDropdown = false;
    WalletCategoriesFileDB_Pojo categoriesFileDB_Pojo;
    Constants constants;
    List<WalletEntryFileDB_Pojo> entryFileDB_Pojo;
    WalletEntriesAdapter gvAdapter;
    GridView gv_wallet;
    boolean isEdittable = false;
    public boolean isGridview = false;
    LinearLayout ll_noWallet;
    FloatingActionButton mFab;
    private SensorManager sensorManager;
    int sortBy;
    private Toolbar toolbar;
    WalletCategoriesDAL walletCategoriesDAL;
    WalletCommon walletCommon;
    WalletEntriesDAL walletEntriesDAL;
    CommonSharedPreferences walletSharedPreferences;

    public enum SortBy {
        Name,
        Time
    }

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }



@SuppressLint("RestrictedApi")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_wallet);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.gv_wallet = (GridView) findViewById(R.id.gv_wallet);
        this.mFab = (FloatingActionButton) findViewById(R.id.fabbutton);
        this.ll_noWallet = (LinearLayout) findViewById(R.id.ll_noWallet);
        this.walletSharedPreferences = CommonSharedPreferences.GetObject(this);
        this.entryFileDB_Pojo = new ArrayList();
        this.walletEntriesDAL = new WalletEntriesDAL(this);
        this.walletCategoriesDAL = new WalletCategoriesDAL(this);
        this.categoriesFileDB_Pojo = new WalletCategoriesFileDB_Pojo();
        this.walletCommon = new WalletCommon();
        this.constants = new Constants();
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.isGridview = this.walletSharedPreferences.get_ViewByWalletEntry() != 0;
        setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
    TextView title3 = findViewById(R.id.title3);
    title3.setText(WalletCommon.walletCurrentCategoryName);
//        getSupportActionBar().setTitle((CharSequence) );
    // getSupportActionBar().setTitle("");

    Common.applyKitKatTranslucency(this);
        SecurityLocksCommon.IsAppDeactive = true;
        this.mFab.setVisibility(View.VISIBLE);
        getCurrentCategory();
        this.sortBy = this.categoriesFileDB_Pojo.getCategoryFileSortBy();
        setGridview();
        this.gv_wallet.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                SecurityLocksCommon.IsAppDeactive = false;
                WalletCommon.walletCurrentEntryName = ((WalletEntryFileDB_Pojo) entryFileDB_Pojo.get(i)).getEntryFileName();
                startActivity(new Intent(WalletEntriesActivity.this, WalletEntryActivity.class));
                finish();
            }
        });
        this.mFab.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                WalletCommon.walletCurrentEntryName = "";
                startActivity(new Intent(WalletEntriesActivity.this, WalletEntryActivity.class));
                finish();
            }
        });
    }

    public void getCurrentCategory() {
        WalletCategoriesDAL walletCategoriesDAL2 = this.walletCategoriesDAL;
        StringBuilder sb = new StringBuilder();
        this.constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
        this.constants.getClass();
        sb.append("WalletCategoriesFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" AND ");
        this.constants.getClass();
        sb.append("WalletCategoriesFileId");
        sb.append(" = ");
        sb.append(WalletCommon.WalletCurrentCategoryId);
        this.categoriesFileDB_Pojo = walletCategoriesDAL2.getCategoryInfoFromDatabase(sb.toString());
    }

    public void updateCurrentCategorySortBy() {
        this.categoriesFileDB_Pojo.setCategoryFileSortBy(this.sortBy);
        WalletCategoriesDAL walletCategoriesDAL2 = this.walletCategoriesDAL;
        WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo = this.categoriesFileDB_Pojo;
        this.constants.getClass();
        walletCategoriesDAL2.updateCategoryFromDatabase(walletCategoriesFileDB_Pojo, "WalletCategoriesFileId", String.valueOf(this.categoriesFileDB_Pojo.getCategoryFileId()));
    }

    public void setGridview() {
        if (this.sortBy == SortBy.Name.ordinal()) {
            WalletEntriesDAL walletEntriesDAL2 = this.walletEntriesDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
            this.constants.getClass();
            sb.append("WalletEntryFileIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            sb.append(" AND ");
            this.constants.getClass();
            sb.append("WalletCategoriesFileId");
            sb.append(" = ");
            sb.append(WalletCommon.WalletCurrentCategoryId);
            sb.append(" ORDER BY ");
            this.constants.getClass();
            sb.append("WalletEntryFileName");
            sb.append(" COLLATE NOCASE ASC");
            this.entryFileDB_Pojo = walletEntriesDAL2.getAllEntriesInfoFromDatabase(sb.toString());
        } else {
            WalletEntriesDAL walletEntriesDAL3 = this.walletEntriesDAL;
            StringBuilder sb2 = new StringBuilder();
            this.constants.getClass();
            sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
            this.constants.getClass();
            sb2.append("WalletEntryFileIsDecoy");
            sb2.append(" = ");
            sb2.append(SecurityLocksCommon.IsFakeAccount);
            sb2.append(" AND ");
            this.constants.getClass();
            sb2.append("WalletCategoriesFileId");
            sb2.append(" = ");
            sb2.append(WalletCommon.WalletCurrentCategoryId);
            sb2.append(" ORDER BY ");
            this.constants.getClass();
            sb2.append("WalletEntryFileModifiedDate");
            sb2.append(" DESC");
            this.entryFileDB_Pojo = walletEntriesDAL3.getAllEntriesInfoFromDatabase(sb2.toString());
        }
        if (this.entryFileDB_Pojo.size() > 0) {
            this.gvAdapter = new WalletEntriesAdapter(this, this.entryFileDB_Pojo);
            this.gvAdapter.setFocusedPosition(0);
            this.gvAdapter.setIsEdit(false);
            this.gvAdapter.setIsGridview(this.isGridview);
            this.gv_wallet.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), this.isGridview));
            this.gv_wallet.setAdapter(this.gvAdapter);
            this.gvAdapter.notifyDataSetChanged();
            this.ll_noWallet.setVisibility(View.GONE);
            return;
        }
        this.ll_noWallet.setVisibility(View.VISIBLE);
    }

    public void bindSearchResult(List<WalletEntryFileDB_Pojo> list) {
        this.gvAdapter = new WalletEntriesAdapter(this, list);
        this.gv_wallet.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), this.isGridview));
        this.gvAdapter.setIsGridview(this.isGridview);
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
                    WalletEntriesActivity walletEntriesActivity = WalletEntriesActivity.this;
                    walletEntriesActivity.isGridview = true;
                    walletEntriesActivity.walletSharedPreferences.set_viewByWalletEntry(isGridview ? 1 : 0);
                }
                if (str.equals(getResources().getString(R.string.list))) {
                    WalletEntriesActivity walletEntriesActivity2 = WalletEntriesActivity.this;
                    walletEntriesActivity2.isGridview = false;
                    walletEntriesActivity2.walletSharedPreferences.set_viewByWalletEntry(isGridview ? 1 : 0);
                }
                if (str.equals(getResources().getString(R.string.name))) {
                    WalletEntriesActivity walletEntriesActivity3 = WalletEntriesActivity.this;
                    walletEntriesActivity3.sortBy = 0;
                    walletEntriesActivity3.updateCurrentCategorySortBy();
                }
                if (str.equals(getResources().getString(R.string.time))) {
                    WalletEntriesActivity walletEntriesActivity4 = WalletEntriesActivity.this;
                    walletEntriesActivity4.sortBy = 1;
                    walletEntriesActivity4.updateCurrentCategorySortBy();
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
                    for (WalletEntryFileDB_Pojo walletEntryFileDB_Pojo : entryFileDB_Pojo) {
                        if (walletEntryFileDB_Pojo.getEntryFileName().toLowerCase().contains(str)) {
                            arrayList.add(walletEntryFileDB_Pojo);
                        }
                    }
                } else {
                    arrayList = entryFileDB_Pojo;
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
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, WalletCategoriesActivity.class));
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
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, WalletCategoriesActivity.class));
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
