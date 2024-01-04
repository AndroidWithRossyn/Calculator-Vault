package com.example.vault.todolist;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

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
import com.example.vault.todolist.model.ToDoCheckableRowPojo;
import com.example.vault.todolist.model.ToDoDB_Pojo;
import com.example.vault.todolist.model.ToDoPojo;
import com.example.vault.todolist.model.ToDoTask;
import com.example.vault.todolist.util.ToDoDAL;
import com.example.vault.todolist.util.ToDoReadFromXML;
import com.example.vault.todolist.util.ToDoWriteInXML;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;

public class ViewToDoActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    Constants constants;
    boolean hasModified = false;
    LayoutInflater inflater;
    boolean isModified = false;
    LinearLayout ll_container;
    LinearLayout ll_main;
    List<ToDoCheckableRowPojo> rowList;
    private SensorManager sensorManager;
    TableLayout tl_TodoTasks;
    ToDoPojo toDoList;
    Toolbar toolbar;
    TextView tv_ToDoTitle;

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(128);
        setContentView((int) R.layout.activity_todo_view);
        SecurityLocksCommon.IsAppDeactive = true;
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tl_TodoTasks = (TableLayout) findViewById(R.id.tl_TodoTasks);
        this.tv_ToDoTitle = (TextView) findViewById(R.id.tv_ToDoTitle);
        this.ll_container = (LinearLayout) findViewById(R.id.ll_container);
        this.ll_main = (LinearLayout) findViewById(R.id.ll_main);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.toDoList = new ToDoPojo();
        this.rowList = new ArrayList();
        this.constants = new Constants();
        try {
            setSupportActionBar(this.toolbar);
            this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
//            getSupportActionBar().setTitle((CharSequence) "");
            // getSupportActionBar().setTitle("");

        } catch (Exception e) {
            e.printStackTrace();
        }
        getToDoListFromFile();
        fillToDoDataInTableLayout();
    }

    public void getToDoListFromFile() {
        try {
            ToDoReadFromXML toDoReadFromXML = new ToDoReadFromXML();
            StringBuilder sb = new StringBuilder();
            sb.append(StorageOptionsCommon.STORAGEPATH);
            sb.append(StorageOptionsCommon.TODOLIST);
            sb.append(Common.ToDoListName);
            sb.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
            this.toDoList = toDoReadFromXML.ReadToDoList(sb.toString());
//            getSupportActionBar().setTitle((CharSequence) this.toDoList.getTitle());
            // getSupportActionBar().setTitle("");

            this.ll_main.setBackgroundColor(Color.parseColor(this.toDoList.getTodoColor()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillToDoDataInTableLayout() {
        ToDoPojo toDoPojo = this.toDoList;
        if (toDoPojo != null) {
            ArrayList toDoList2 = toDoPojo.getToDoList();
            int i = 0;
            while (i < toDoList2.size()) {
                try {
                    ToDoCheckableRowPojo toDoCheckableRowPojo = new ToDoCheckableRowPojo();
                    TableRow tableRow = new TableRow(getApplicationContext());
                    LayoutParams layoutParams = new LayoutParams(-1, -1, 1.0f);
                    tableRow.setLayoutParams(layoutParams);
                    View inflate = this.inflater.inflate(R.layout.tablerow_todo_view, null);
                    final TextView textView = (TextView) inflate.findViewById(R.id.tv_text);
                    final CheckBox checkBox = (CheckBox) inflate.findViewById(R.id.cb_done);
                    int i2 = i + 1;
                    ((TextView) inflate.findViewById(R.id.tv_rowNo)).setText(String.valueOf(i2));
                    checkBox.setChecked(((ToDoTask) toDoList2.get(i)).isChecked());
                    Utilities.strikeTextview(textView, ((ToDoTask) toDoList2.get(i)).getToDo(), checkBox.isChecked());
                    tableRow.addView(inflate, layoutParams);
                    this.tl_TodoTasks.addView(tableRow);
                    toDoCheckableRowPojo.setCb_done(checkBox);
                    toDoCheckableRowPojo.setTv_text(textView);
                    this.rowList.add(toDoCheckableRowPojo);
                    checkBox.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            boolean isChecked = checkBox.isChecked();

                            Utilities.strikeTextview(textView, textView.getText().toString(), isChecked);
                            ViewToDoActivity.this.isModified = true;
                        }
                    });
                    textView.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            boolean isChecked = checkBox.isChecked();
                            checkBox.setChecked(!isChecked);

                            Utilities.strikeTextview(textView, textView.getText().toString(), !isChecked);
                            ViewToDoActivity.this.isModified = true;
                        }
                    });
                    i = i2;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public void SaveDataBeforeNavigation() {
        if (this.isModified) {
            ArrayList arrayList = new ArrayList();
            boolean z = true;
            for (int i = 0; i < this.rowList.size(); i++) {
                boolean isChecked = ((ToDoCheckableRowPojo) this.rowList.get(i)).getCb_done().isChecked();
                ToDoTask toDoTask = new ToDoTask();
                toDoTask.setChecked(isChecked);
                toDoTask.setToDo(((ToDoCheckableRowPojo) this.rowList.get(i)).getTv_text().getText().toString());
                arrayList.add(toDoTask);
                if (!isChecked) {
                    z = false;
                }
            }
            if (arrayList.size() > 0) {
                String currentDateTime2 = Utilities.getCurrentDateTime2();
                this.toDoList.deleteTodoList();
                this.toDoList.setToDoList(arrayList);
                this.toDoList.setDateModified(currentDateTime2);
                ToDoWriteInXML toDoWriteInXML = new ToDoWriteInXML();
                ToDoPojo toDoPojo = this.toDoList;
                if (toDoWriteInXML.saveToDoList(this, toDoPojo, toDoPojo.getTitle(), true)) {
                    ToDoDAL toDoDAL = new ToDoDAL(this);
                    ToDoDB_Pojo toDoDB_Pojo = new ToDoDB_Pojo();
                    toDoDB_Pojo.setToDoFileModifiedDate(currentDateTime2);
                    toDoDB_Pojo.setToDoFileTask1(((ToDoTask) arrayList.get(0)).getToDo());
                    toDoDB_Pojo.setToDoFileTask1IsChecked(((ToDoTask) arrayList.get(0)).isChecked());
                    if (arrayList.size() >= 2) {
                        toDoDB_Pojo.setToDoFileTask2(((ToDoTask) arrayList.get(1)).getToDo());
                        toDoDB_Pojo.setToDoFileTask2IsChecked(((ToDoTask) arrayList.get(1)).isChecked());
                    }
                    toDoDB_Pojo.setToDoFinished(z);
                    this.constants.getClass();
                    toDoDAL.updateToDoFileTasksInDatabase(toDoDB_Pojo, "ToDoId", String.valueOf(Common.ToDoListId));
                }
            }
        }
    }

    public void deleteToDo() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.confirmation_message_box);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_Ok);
        LinearLayout linearLayout2 = (LinearLayout) dialog.findViewById(R.id.ll_Cancel);
        ((TextView) dialog.findViewById(R.id.tvmessagedialogtitle)).setText(R.string.delete_todo);
        linearLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    dialog.dismiss();
                    StringBuilder sb = new StringBuilder();
                    sb.append(StorageOptionsCommon.STORAGEPATH);
                    sb.append(StorageOptionsCommon.TODOLIST);
                    sb.append(Common.ToDoListName);
                    sb.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
                    File file = new File(sb.toString());
                    if (file.exists() && file.delete()) {
                        new ToDoDAL(ViewToDoActivity.this).deleteToDoFileFromDatabase("ToDoId", String.valueOf(Common.ToDoListId));
                        SecurityLocksCommon.IsAppDeactive = false;
                        ViewToDoActivity.this.startActivity(new Intent(ViewToDoActivity.this, ToDoActivity.class));
                        ViewToDoActivity.this.finish();
                        ViewToDoActivity.this.overridePendingTransition(17432576, 17432577);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        linearLayout2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void onBackPressed() {
        SaveDataBeforeNavigation();
        Common.ToDoListEdit = false;
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, ToDoActivity.class));
        finish();
        overridePendingTransition(17432576, 17432577);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_del, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            SaveDataBeforeNavigation();
            Common.ToDoListEdit = false;
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, ToDoActivity.class));
            finish();
            overridePendingTransition(17432576, 17432577);
        } else if (itemId == R.id.action_menu_del) {
            deleteToDo();
        } else if (itemId == R.id.action_menu_edit) {
            SaveDataBeforeNavigation();
            SecurityLocksCommon.IsAppDeactive = false;
            Common.ToDoListEdit = true;
            startActivity(new Intent(this, AddToDoActivity.class));
            finish();
            overridePendingTransition(17432576, 17432577);
        }
        return super.onOptionsItemSelected(menuItem);
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
