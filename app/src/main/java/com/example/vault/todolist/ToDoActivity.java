package com.example.vault.todolist;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;


import com.example.vault.todolist.adapter.ToDoListAdapter;
import com.example.vault.todolist.model.ToDoDB_Pojo;
import com.example.vault.todolist.util.ToDoDAL;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

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
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.securitylocks.SecurityLocksSharedPreferences;
import com.example.vault.utilities.Utilities;
import com.example.vault.wallet.util.CommonSharedPreferences;

public class ToDoActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {

    boolean IsSortingDropdown = false;
    ToDoListAdapter adapter;
    CommonSharedPreferences commonSharedPreferences;
    Constants constants;
    FloatingActionButton fab_AddToDoTask;
    GridLayoutManager glm;
    LinearLayout ll_NotesFolderEdit;
    LinearLayout ll_anchor;
    LinearLayout ll_emptyToDo;
    RecyclerView recList;
    SecurityLocksSharedPreferences securityLocksSharedPreferences;
    private SensorManager sensorManager;
    int sortBy;
    public ArrayList<ToDoDB_Pojo> toDoList;

    ToDoDAL todoDAL;
    private Toolbar toolbar;

    int viewBy = 0;

    public enum SortBy {
        Name,
        CreatedTime,
        ModifiedTime
    }


    public class MyOnClickListeners implements OnClickListener {
        public MyOnClickListeners() {
        }

        public void onClick(View view) {
            SecurityLocksCommon.IsAppDeactive = false;
            ToDoActivity.this.startActivity(new Intent(ToDoActivity.this, AddToDoActivity.class));
            ToDoActivity.this.finish();
            ToDoActivity.this.overridePendingTransition(17432576, 17432577);
        }
    }


    public enum ViewBy {
        Detail,
        List,
        Tile
    }

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }



    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_to_do);
        getWindow().addFlags(128);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
;


        this.fab_AddToDoTask = (FloatingActionButton) findViewById(R.id.fab_AddToDoTask);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        this.recList = (RecyclerView) findViewById(R.id.toDoCardList);
        this.ll_emptyToDo = (LinearLayout) findViewById(R.id.ll_emptyToDo);
        this.constants = new Constants();

        this.todoDAL = new ToDoDAL(this);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.securityLocksSharedPreferences = SecurityLocksSharedPreferences.GetObject(this);
        this.commonSharedPreferences = CommonSharedPreferences.GetObject(this);
        SecurityLocksCommon.IsAppDeactive = true;
        try {
            this.toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(this.toolbar);
//            getSupportActionBar().setTitle((CharSequence) "To do Lists");
            // getSupportActionBar().setTitle("");

            this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.sortBy = this.commonSharedPreferences.get_sortByToDoFile();
        this.viewBy = this.commonSharedPreferences.get_viewByToDoFile();
        FloatingActionButton floatingActionButton = this.fab_AddToDoTask;

        floatingActionButton.setOnClickListener(new MyOnClickListeners());
        this.recList.setHasFixedSize(false);
        setRecyclerView();
    }

    public void setRecyclerView() {
        getData();
        if (this.viewBy == ViewBy.List.ordinal()) {
            this.glm = new GridLayoutManager(this, Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), false));
        } else if (this.viewBy == ViewBy.Tile.ordinal()) {
            this.glm = new GridLayoutManager(this, Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), true));
        } else {
            this.glm = new GridLayoutManager(this, Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), false));
        }
        this.recList.setLayoutManager(this.glm);
        if (this.toDoList.size() > 0) {
            this.adapter = new ToDoListAdapter(this, this.toDoList, this);
            this.adapter.setViewBy(this.viewBy);
            this.recList.setAdapter(this.adapter);
            this.ll_emptyToDo.setVisibility(View.GONE);
            return;
        }
        this.ll_emptyToDo.setVisibility(View.VISIBLE);
    }

    public void getData() {
        if (this.sortBy == SortBy.CreatedTime.ordinal()) {
            ToDoDAL toDoDAL = this.todoDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
            this.constants.getClass();
            sb.append("ToDoIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            sb.append(" ORDER BY ");
            this.constants.getClass();
            sb.append("ToDoCreatedDate");
            sb.append(" DESC");
            this.toDoList = toDoDAL.getAllToDoInfoFromDatabase(sb.toString());
        } else if (this.sortBy == SortBy.ModifiedTime.ordinal()) {
            ToDoDAL toDoDAL2 = this.todoDAL;
            StringBuilder sb2 = new StringBuilder();
            this.constants.getClass();
            sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
            this.constants.getClass();
            sb2.append("ToDoIsDecoy");
            sb2.append(" = ");
            sb2.append(SecurityLocksCommon.IsFakeAccount);
            sb2.append(" ORDER BY ");
            this.constants.getClass();
            sb2.append("ToDoModifiedDate");
            sb2.append(" DESC");
            this.toDoList = toDoDAL2.getAllToDoInfoFromDatabase(sb2.toString());
        } else if (this.sortBy == SortBy.Name.ordinal()) {
            ToDoDAL toDoDAL3 = this.todoDAL;
            StringBuilder sb3 = new StringBuilder();
            this.constants.getClass();
            sb3.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
            this.constants.getClass();
            sb3.append("ToDoIsDecoy");
            sb3.append(" = ");
            sb3.append(SecurityLocksCommon.IsFakeAccount);
            sb3.append(" ORDER BY ");
            this.constants.getClass();
            sb3.append("ToDoName");
            sb3.append(" COLLATE NOCASE ASC");
            this.toDoList = toDoDAL3.getAllToDoInfoFromDatabase(sb3.toString());
        } else {
            ToDoDAL toDoDAL4 = this.todoDAL;
            StringBuilder sb4 = new StringBuilder();
            this.constants.getClass();
            sb4.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
            this.constants.getClass();
            sb4.append("ToDoIsDecoy");
            sb4.append(" = ");
            sb4.append(SecurityLocksCommon.IsFakeAccount);
            sb4.append(" ORDER BY ");
            this.constants.getClass();
            sb4.append("ToDoName");
            sb4.append(" COLLATE NOCASE ASC");
            this.toDoList = toDoDAL4.getAllToDoInfoFromDatabase(sb4.toString());
        }
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
        arrayList2.add(getResources().getString(R.string.detail));
        arrayList2.add(getResources().getString(R.string.tile));
        hashMap.put(arrayList.get(0), arrayList2);
        arrayList.add(getResources().getString(R.string.sort_by));
        arrayList3.add(getResources().getString(R.string.name));
        arrayList3.add(getResources().getString(R.string.Created_time));
        arrayList3.add(getResources().getString(R.string.modified_time));
        hashMap.put(arrayList.get(1), arrayList3);
        expandableListView.setAdapter(new ExpandableListAdapter1(this, arrayList, hashMap));
        expandableListView.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
                String str = ((String) ((List) hashMap.get(arrayList.get(i))).get(i2)).toString();
                if (str.equals("Detail")) {
                    ToDoActivity toDoActivity = ToDoActivity.this;
                    toDoActivity.viewBy = 0;
                    toDoActivity.commonSharedPreferences.set_viewByToDoFile(ToDoActivity.this.viewBy);
                }
                if (str.equals("List")) {
                    ToDoActivity toDoActivity2 = ToDoActivity.this;
                    toDoActivity2.viewBy = 1;
                    toDoActivity2.commonSharedPreferences.set_viewByToDoFile(ToDoActivity.this.viewBy);
                }
                if (str.equals("Tile")) {
                    ToDoActivity toDoActivity3 = ToDoActivity.this;
                    toDoActivity3.viewBy = 2;
                    toDoActivity3.commonSharedPreferences.set_viewByToDoFile(ToDoActivity.this.viewBy);
                }
                if (str.equals("Created Time")) {
                    ToDoActivity toDoActivity4 = ToDoActivity.this;
                    toDoActivity4.sortBy = 1;
                    toDoActivity4.commonSharedPreferences.set_sortByToDoFile(ToDoActivity.this.sortBy);
                } else if (str.equals("Modified Time")) {
                    ToDoActivity toDoActivity5 = ToDoActivity.this;
                    toDoActivity5.sortBy = 2;
                    toDoActivity5.commonSharedPreferences.set_sortByToDoFile(ToDoActivity.this.sortBy);
                } else {
                    ToDoActivity toDoActivity6 = ToDoActivity.this;
                    toDoActivity6.sortBy = 0;
                    toDoActivity6.commonSharedPreferences.set_sortByToDoFile(ToDoActivity.this.sortBy);
                }
                ToDoActivity.this.setRecyclerView();
                popupWindow.dismiss();
                return false;
            }
        });
        if (!this.IsSortingDropdown) {
            LinearLayout linearLayout = this.ll_anchor;
            popupWindow.showAsDropDown(linearLayout, linearLayout.getWidth(), 0);
            this.IsSortingDropdown = true;
            return;
        }
        popupWindow.dismiss();
        this.IsSortingDropdown = false;
    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search_view_sort, menu);
//        ((SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search))).setOnQueryTextListener(new OnQueryTextListener() {
//            public boolean onQueryTextSubmit(String str) {
//                return true;
//            }
//
//            public boolean onQueryTextChange(String str) {
//                ArrayList<ToDoDB_Pojo> arrayList = new ArrayList<>();
//                if (str.length() > 0) {
//                    Iterator it = ToDoActivity.this.toDoList.iterator();
//                    while (it.hasNext()) {
//                        ToDoDB_Pojo toDoDB_Pojo = (ToDoDB_Pojo) it.next();
//                        if (toDoDB_Pojo.getToDoFileName().toLowerCase(Locale.getDefault()).contains(str)) {
//                            arrayList.add(toDoDB_Pojo);
//                        }
//                    }
//                } else {
//                    arrayList = ToDoActivity.this.toDoList;
//                }
//                try {
//                    ToDoActivity.this.adapter.setAdapterData(arrayList);
//                }catch (Exception e){
//                    Toast.makeText(ToDoActivity.this, "Create To Do List first..", Toast.LENGTH_SHORT).show();
//                }
//
//                ToDoActivity.this.adapter.notifyDataSetChanged();
//                return true;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, MainiFeaturesActivity.class));
            finish();
            overridePendingTransition(17432576, 17432577);
        } else if (itemId == R.id.action_viewSort) {
            this.IsSortingDropdown = false;
            showPopupWindow();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onBackPressed() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, MainiFeaturesActivity.class));
        finish();
        overridePendingTransition(17432576, 17432577);
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
