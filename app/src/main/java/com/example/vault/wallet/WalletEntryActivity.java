package com.example.vault.wallet;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.vault.R;
import com.example.vault.common.Constants;
import com.example.vault.panicswitch.AccelerometerListener;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.wallet.model.WalletCategoriesFieldPojo;
import com.example.vault.wallet.model.WalletCategoriesFileDB_Pojo;
import com.example.vault.wallet.model.WalletCategoriesPojo;
import com.example.vault.wallet.model.WalletEntryFileDB_Pojo;
import com.example.vault.wallet.model.WalletEntryItemsPojo;
import com.example.vault.wallet.model.WalletEntryPojo;
import com.example.vault.wallet.util.EntryReadXml;
import com.example.vault.wallet.util.WalletCategoriesDAL;
import com.example.vault.wallet.util.WalletCommon;
import com.example.vault.wallet.util.WalletEntriesDAL;
import com.example.vault.wallet.util.WalletEntryWriteXml;

public class WalletEntryActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    Constants constants;
    int entryAction = 0;
    List<WalletEntryItemsPojo> entryFieldItemsList;
    WalletCategoriesFieldPojo entryFieldsModel;
    String entryName = "";
    WalletEntryPojo entryPojo = null;
    String filePath = "";
//    ImageView iv_categoryIcon;
    LinearLayout ll_focus;
    private SensorManager sensorManager;
    TableLayout tableLayout;
    private Toolbar toolbar;

//    TextView tv_categoryName;
    WalletCategoriesDAL walletCategoriesDAL;
    WalletCategoriesPojo walletCategoriesPojo;
    WalletCommon walletCommon;
    WalletEntriesDAL walletEntriesDAL;

    public enum entryActions {
        New,
        View,
        Edit
    }

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_wallet_entry);


        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.tableLayout = (TableLayout) findViewById(R.id.tableLayoutNewEntryFields);
//        this.iv_categoryIcon = (ImageView) findViewById(R.id.iv_categoryIcon);
        this.ll_focus = (LinearLayout) findViewById(R.id.ll_focus);
//        this.tv_categoryName = (TextView) findViewById(R.id.tv_categoryName);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.walletCommon = new WalletCommon();
        this.constants = new Constants();
        this.walletEntriesDAL = new WalletEntriesDAL(this);
        this.walletCategoriesDAL = new WalletCategoriesDAL(this);
        setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
        TextView title4 = findViewById(R.id.title4);
        title4.setText( WalletCommon.walletCurrentCategoryName);
//        getSupportActionBar().setTitle((CharSequence));
        // getSupportActionBar().setTitle("");

        SecurityLocksCommon.IsAppDeactive = true;
        if (WalletCommon.walletCurrentEntryName.equals("")) {
            this.entryAction = entryActions.New.ordinal();
        } else {
            this.entryAction = entryActions.View.ordinal();
            getCurrentEntryFromXml();
        }
        this.walletCategoriesPojo = this.walletCommon.getCurrentCategoryData(this, WalletCommon.walletCurrentCategoryName);
        try {
            TypedArray obtainTypedArray = getResources().obtainTypedArray(R.array.wallet_drawables_list);
//            this.iv_categoryIcon.setImageResource(obtainTypedArray.getResourceId(this.walletCategoriesPojo.getCategoryIconIndex(), -1));
            obtainTypedArray.recycle();
        } catch (Exception unused) {
        }
//        this.tv_categoryName.setText(WalletCommon.walletCurrentCategoryName);
        updateTableLayout();
    }

    public void getCurrentEntryFromXml() {
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.WALLET);
        sb.append(WalletCommon.walletCurrentCategoryName);
        sb.append(File.separator);
        sb.append(StorageOptionsCommon.ENTRY);
        sb.append(WalletCommon.walletCurrentEntryName);
        sb.append(StorageOptionsCommon.WALLET_ENTRY_FILE_EXTENSION);
        this.filePath = sb.toString();
        this.entryPojo = EntryReadXml.parseXML(this, this.filePath);

        Log.e("entryPojo",""+entryPojo);
    }

    public void updateTableLayout() {
        if (this.entryAction == entryActions.New.ordinal()) {
            initLayout(this.walletCategoriesPojo.getCategoryFields());
        } else {
            WalletEntryPojo walletEntryPojo = this.entryPojo;
            if (walletEntryPojo != null) {
                initLayout(walletEntryPojo.getFields());
            }
        }
        this.ll_focus.requestFocus();
    }

    public void initLayout(List<WalletCategoriesFieldPojo> list) {
        this.entryFieldItemsList = new ArrayList();
        int i = 0;
        for (WalletCategoriesFieldPojo addNewRowInTableLayoutss : list) {
            addNewRowInTableLayout(addNewRowInTableLayoutss, i);
            i++;
        }
        this.tableLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom));
    }

    public void addNewRowInTableLayout(WalletCategoriesFieldPojo walletCategoriesFieldPojo, int i) {
        InputFilter[] inputFilterArr = {new LengthFilter(30)};
        LayoutParams layoutParams = new LayoutParams(-1, -1, 1.0f);
        TableRow tableRow = new TableRow(getApplicationContext());
        tableRow.setLayoutParams(layoutParams);
        View inflate = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.wallet_entry_list_item, null);
        tableRow.addView(inflate, layoutParams);
        WalletEntryItemsPojo walletEntryItemsPojo = new WalletEntryItemsPojo();
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams2.leftMargin = 15;
        layoutParams2.rightMargin = 15;
        TextView textView = (TextView) inflate.findViewById(R.id.tv_entryTitle);
        final EditText editText = (EditText) inflate.findViewById(R.id.et_entryValue);
        CheckBox checkBox = (CheckBox) inflate.findViewById(R.id.cb_entryIsSecured);
        textView.setTag(Integer.valueOf(i));
        editText.setTag(Integer.valueOf(i));
        textView.setText(walletCategoriesFieldPojo.getFieldName());
        editText.setInputType(65536);
        editText.setFilters(inputFilterArr);
        editText.setLayoutParams(layoutParams2);
        if (this.entryAction != entryActions.View.ordinal()) {
            editText.setBackgroundResource(R.drawable.white_et_curve_edge);
            editText.setText(walletCategoriesFieldPojo.getFieldValue());
            editText.setPadding(30, 30, 30, 30);
        } else if (walletCategoriesFieldPojo.getFieldValue().equals("")) {
            textView.setVisibility(View.GONE);
            editText.setVisibility(View.GONE);
        } else {
            editText.setFocusable(false);
            editText.setClickable(false);
            editText.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            editText.setText(walletCategoriesFieldPojo.getFieldValue());
            if (walletCategoriesFieldPojo.isSecured()) {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        if (compoundButton.isChecked()) {
                            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        } else {
                            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        }
                    }
                });
                checkBox.setChecked(walletCategoriesFieldPojo.isSecured());
            }
        }
        walletEntryItemsPojo.setTextView(textView);
        walletEntryItemsPojo.setEditText(editText);
        walletEntryItemsPojo.setSecured(walletCategoriesFieldPojo.isSecured());
        this.entryFieldItemsList.add(walletEntryItemsPojo);
        this.tableLayout.addView(tableRow);
    }

    public void clearData() {
        this.entryFieldItemsList = null;
        this.tableLayout.removeAllViews();
    }

    public void addEntryToDatabase(String str, boolean z) {
        String currentDate = this.walletCommon.getCurrentDate();
        try {
            WalletEntryFileDB_Pojo walletEntryFileDB_Pojo = new WalletEntryFileDB_Pojo();
            walletEntryFileDB_Pojo.setCategoryId(WalletCommon.WalletCurrentCategoryId);
            walletEntryFileDB_Pojo.setEntryFileName(str);
            StringBuilder sb = new StringBuilder();
            sb.append(StorageOptionsCommon.STORAGEPATH);
            sb.append(StorageOptionsCommon.WALLET);
            sb.append(WalletCommon.walletCurrentCategoryName);
            sb.append(File.separator);
            sb.append(StorageOptionsCommon.ENTRY);
            sb.append(str);
            sb.append(StorageOptionsCommon.WALLET_ENTRY_FILE_EXTENSION);
            walletEntryFileDB_Pojo.setEntryFileLocation(sb.toString());
            walletEntryFileDB_Pojo.setEntryFileCreatedDate(currentDate);
            walletEntryFileDB_Pojo.setEntryFileModifiedDate(currentDate);
            walletEntryFileDB_Pojo.setCategoryFileIconIndex(this.walletCategoriesPojo.getCategoryIconIndex());
            walletEntryFileDB_Pojo.setEntriesFileSortBy(0);
            walletEntryFileDB_Pojo.setEntryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
            if (z) {
                WalletEntriesDAL walletEntriesDAL2 = this.walletEntriesDAL;
                this.constants.getClass();
                walletEntriesDAL2.updateEntryInDatabase(walletEntryFileDB_Pojo, "WalletEntryFileId", String.valueOf(walletEntryFileDB_Pojo.getEntryFileId()));
            } else {
                this.walletEntriesDAL.addWalletEntriesInfoInDatabase(walletEntryFileDB_Pojo);
            }
            new WalletCategoriesFileDB_Pojo();
            WalletCategoriesDAL walletCategoriesDAL2 = this.walletCategoriesDAL;
            StringBuilder sb2 = new StringBuilder();
            this.constants.getClass();
            sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
            this.constants.getClass();
            sb2.append("WalletCategoriesFileIsDecoy");
            sb2.append(" = ");
            sb2.append(SecurityLocksCommon.IsFakeAccount);
            sb2.append(" AND ");
            this.constants.getClass();
            sb2.append("WalletCategoriesFileId");
            sb2.append(" = ");
            sb2.append(walletEntryFileDB_Pojo.getCategoryId());
            WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL2.getCategoryInfoFromDatabase(sb2.toString());
            categoryInfoFromDatabase.setCategoryFileModifiedDate(currentDate);
            WalletCategoriesDAL walletCategoriesDAL3 = this.walletCategoriesDAL;
            this.constants.getClass();
            walletCategoriesDAL3.updateCategoryFromDatabase(categoryInfoFromDatabase, "WalletCategoriesFileId", String.valueOf(walletEntryFileDB_Pojo.getCategoryId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean saveEntry() {
        boolean z;
        ArrayList arrayList = new ArrayList();
        this.entryName = this.walletCommon.capitalizeFirstLetter(((WalletEntryItemsPojo) this.entryFieldItemsList.get(0)).getEditText().getText().toString());
        if (this.entryName.length() <= 0) {
            Toast.makeText(this, R.string.entry_name_enter, Toast.LENGTH_SHORT).show();
            return false;
        } else if (this.walletCommon.isNoSpecialCharsInName(this.entryName)) {
            for (WalletEntryItemsPojo walletEntryItemsPojo : this.entryFieldItemsList) {
                this.entryFieldsModel = new WalletCategoriesFieldPojo();
                this.entryFieldsModel.setFieldName(this.walletCommon.capitalizeFirstLetter(walletEntryItemsPojo.getTextView().getText().toString()));
                this.entryFieldsModel.setFieldValue(walletEntryItemsPojo.getEditText().getText().toString());
                this.entryFieldsModel.setSecured(walletEntryItemsPojo.isSecured());
                arrayList.add(this.entryFieldsModel);
            }
            WalletEntryPojo walletEntryPojo = new WalletEntryPojo();
            walletEntryPojo.setCategoryName(WalletCommon.walletCurrentCategoryName);
            walletEntryPojo.setEntryName(this.entryName);
            walletEntryPojo.setFields(arrayList);
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
            sb.append("WalletEntryFileName");
            sb.append(" = '");
            sb.append(this.entryName);
            sb.append("'");
            boolean IsWalletEntryAlreadyExist = walletEntriesDAL2.IsWalletEntryAlreadyExist(sb.toString());
            if (this.entryAction == entryActions.New.ordinal()) {
                if (!IsWalletEntryAlreadyExist) {
                    z = WalletEntryWriteXml.write(walletEntryPojo, this, Boolean.valueOf(true));
                } else {
                    Toast.makeText(this, R.string.entry_exists, Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else if (WalletCommon.walletCurrentEntryName.equals(this.entryName) || !IsWalletEntryAlreadyExist) {
                z = WalletEntryWriteXml.write(walletEntryPojo, this, Boolean.valueOf(false));
            } else {
                Toast.makeText(this, R.string.entry_exists, Toast.LENGTH_SHORT).show();
                return false;
            }
            if (z) {
                Toast.makeText(this, R.string.entry_saved, Toast.LENGTH_SHORT).show();
                addEntryToDatabase(this.entryName, IsWalletEntryAlreadyExist);
                return true;
            }
            Toast.makeText(this, R.string.entry_not_saved, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(this, R.string.special_characters_not_allowed, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean deleteEntry(int i, String str) {
        File file = new File(str);
        try {
            WalletEntriesDAL walletEntriesDAL2 = this.walletEntriesDAL;
            this.constants.getClass();
            walletEntriesDAL2.deleteEntryFromDatabase("WalletEntryFileId", String.valueOf(i));
            if (file.exists()) {
                return file.delete();
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public void ShowDeleteDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.confirmation_message_box);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_Ok);
        LinearLayout linearLayout2 = (LinearLayout) dialog.findViewById(R.id.ll_Cancel);
        ((TextView) dialog.findViewById(R.id.tvmessagedialogtitle)).setText(getResources().getString(R.string.delete_entry));
        linearLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                entryName = walletCommon.capitalizeFirstLetter(((WalletEntryItemsPojo) entryFieldItemsList.get(0)).getEditText().getText().toString());

                StringBuilder sb = new StringBuilder();
                constants.getClass();
                sb.append("SELECT \t\tWalletEntryFileId FROM  TableWalletEntries WHERE ");
                constants.getClass();
                sb.append("WalletEntryFileName");
                sb.append(" = '");
                sb.append(entryName);
                sb.append("'");
                int GetWalletEntriesIntegerEntity = walletEntriesDAL.GetWalletEntriesIntegerEntity(sb.toString());
                WalletEntryActivity walletEntryActivity2 = WalletEntryActivity.this;
                if (walletEntryActivity2.deleteEntry(GetWalletEntriesIntegerEntity, walletEntryActivity2.filePath)) {
                    Toast.makeText(WalletEntryActivity.this, R.string.entry_deleted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WalletEntryActivity.this, R.string.entry_not_deleted, Toast.LENGTH_SHORT).show();
                }
                closeActivity();
                dialog.dismiss();
            }
        });
        linearLayout2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void closeActivity() {
        if (this.entryAction == entryActions.Edit.ordinal()) {
            this.entryAction = entryActions.View.ordinal();
            clearData();
            invalidateOptionsMenu();
            updateTableLayout();
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, WalletEntriesActivity.class));
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.entryAction == entryActions.New.ordinal() || this.entryAction == entryActions.Edit.ordinal()) {
            getMenuInflater().inflate(R.menu.menu_save, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_edit_del, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            closeActivity();
        } else if (itemId == R.id.action_menu_del) {
            ShowDeleteDialog();
        } else if (itemId == R.id.action_menu_edit) {
            this.entryAction = entryActions.Edit.ordinal();
            invalidateOptionsMenu();
            clearData();
            updateTableLayout();
        } else if (itemId == R.id.action_save) {
            if (saveEntry()) {
                WalletCommon.walletCurrentEntryName = this.entryName;
                this.entryAction = entryActions.View.ordinal();
                clearData();
                invalidateOptionsMenu();
                getCurrentEntryFromXml();
                updateTableLayout();
            }
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.ll_focus.getWindowToken(), 0);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }


    @Override
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


    @Override
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
