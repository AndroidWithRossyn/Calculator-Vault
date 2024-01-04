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

import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vault.todolist.model.ToDoDB_Pojo;
import com.example.vault.todolist.model.ToDoPojo;
import com.example.vault.todolist.model.ToDoRowViewsPojo;
import com.example.vault.todolist.model.ToDoTask;
import com.example.vault.todolist.util.ToDoDAL;
import com.example.vault.todolist.util.ToDoReadFromXML;
import com.example.vault.todolist.util.ToDoWriteInXML;
import com.flask.colorpicker.ColorPickerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.vault.R;
import com.example.vault.common.Constants;
import com.example.vault.common.ValidationCommon;
import com.example.vault.panicswitch.AccelerometerListener;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;

public class AddToDoActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    Constants constants;
    ToDoDAL dal;
    EditText et_ToDoTitle;
    boolean hasModified = false;
    LayoutInflater inflater;
    LinearLayout ll_addTask;
    LinearLayout ll_anchor;
    LinearLayout ll_main;
    ToDoAddOnClickListeners onClickListener;
    List<ToDoRowViewsPojo> rowViewsList;
    private SensorManager sensorManager;
    String tempTitle = "";
    TableLayout tl_TodoTasks;
    ToDoPojo toDo;
    String toDoColor = "#33aac0ff";
    String toDoTitle = "";
    Toolbar toolbar;

    private class ToDoAddOnClickListeners implements OnClickListener {
        private ToDoAddOnClickListeners() {
        }

        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.et_text) {
                return;
            }
            if (id != R.id.ll_addTask) {
                if (id == R.id.iv_RowDel) { /*2131296535*/
                    if (AddToDoActivity.this.rowViewsList.size() != 1) {
                        TableRow tableRow = (TableRow) ((LinearLayout) ((LinearLayout) view.getParent()).getParent()).getParent();
                        try {
                            AddToDoActivity.this.rowViewsList.remove(AddToDoActivity.this.tl_TodoTasks.indexOfChild(tableRow));
                            AddToDoActivity.this.tl_TodoTasks.removeView(tableRow);
                            AddToDoActivity.this.tl_TodoTasks.requestLayout();
                            AddToDoActivity.this.hasModified = true;

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    } else {

                    }
                } else if (id == R.id.iv_RowDown) { /*2131296536*/
                    view.startAnimation(AnimationUtils.loadAnimation(AddToDoActivity.this.getApplicationContext(), R.anim.anim_pulse));
                    AddToDoActivity addToDoActivity = AddToDoActivity.this;
                    addToDoActivity.tl_TodoTasks = addToDoActivity.swapDownTableRow(view);
                    AddToDoActivity.this.hasModified = true;

                } else if (id == R.id.iv_RowUp) { /*2131296537*/
                    view.startAnimation(AnimationUtils.loadAnimation(AddToDoActivity.this.getApplicationContext(), R.anim.anim_pulse));
                    AddToDoActivity addToDoActivity2 = AddToDoActivity.this;
                    addToDoActivity2.tl_TodoTasks = addToDoActivity2.swapUpTableRow(view);
                    AddToDoActivity.this.hasModified = true;

                }

            } else {
                AddToDoActivity.this.addNewRow("", false);
                AddToDoActivity.this.hasModified = true;
            }
        }
    }

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_todo_add);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tl_TodoTasks = (TableLayout) findViewById(R.id.tl_TodoTasks);
        this.ll_addTask = (LinearLayout) findViewById(R.id.ll_addTask);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        this.ll_main = (LinearLayout) findViewById(R.id.ll_main);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.et_ToDoTitle = (EditText) findViewById(R.id.et_ToDoTitle);
        this.dal = new ToDoDAL(this);
        this.rowViewsList = new ArrayList();
        this.onClickListener = new ToDoAddOnClickListeners();
        this.toDo = new ToDoPojo();
        this.constants = new Constants();
        SecurityLocksCommon.IsAppDeactive = true;
        try {
            setSupportActionBar(this.toolbar);
            this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
//            getSupportActionBar().setTitle((CharSequence) "");
            // getSupportActionBar().setTitle("");

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.ll_addTask.setOnClickListener(this.onClickListener);
        try {
            if (Common.ToDoListEdit) {
                fillDataInToDoList();
                return;
            }
            addNewRow("", false);
            ToDoDAL toDoDAL = this.dal;
            this.constants.getClass();
            int GetToDoDbFileIntegerEntity = toDoDAL.GetToDoDbFileIntegerEntity("SELECT \t    COUNT(*)  \t\t\t\t\t\t   FROM  TableToDo") + 1;
            StringBuilder sb = new StringBuilder();
            sb.append("To do ");
            sb.append(GetToDoDbFileIntegerEntity);
            sb.append("");
            this.tempTitle = sb.toString();
            this.et_ToDoTitle.setHint(this.tempTitle);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void fillDataInToDoList() {
        ToDoReadFromXML toDoReadFromXML = new ToDoReadFromXML();
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.TODOLIST);
        sb.append(Common.ToDoListName);
        sb.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
        this.toDo = toDoReadFromXML.ReadToDoList(sb.toString());
        try {
            if (this.toDo != null) {
                this.et_ToDoTitle.setText(this.toDo.getTitle());
                this.toDoColor = this.toDo.getTodoColor();
                Iterator it = this.toDo.getToDoList().iterator();
                while (it.hasNext()) {
                    ToDoTask toDoTask = (ToDoTask) it.next();
                    addNewRow(toDoTask.getToDo(), toDoTask.isChecked());
                }
                this.ll_main.setBackgroundColor(Color.parseColor(this.toDoColor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNewRow(String str, boolean z) {
        TableRow tableRow = new TableRow(getApplicationContext());
        ToDoRowViewsPojo toDoRowViewsPojo = new ToDoRowViewsPojo();
        LayoutParams layoutParams = new LayoutParams(-1, -1, 1.0f);
        tableRow.setLayoutParams(layoutParams);
        View inflate = this.inflater.inflate(R.layout.tablerow_todo_add, null);
        tableRow.addView(inflate, layoutParams);
        this.tl_TodoTasks.addView(tableRow);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.iv_RowUp);
        ImageView imageView2 = (ImageView) inflate.findViewById(R.id.iv_RowDown);
        ImageView imageView3 = (ImageView) inflate.findViewById(R.id.iv_RowDel);
        InputFilter[] inputFilterArr = {new LengthFilter(30)};
        EditText editText = (EditText) inflate.findViewById(R.id.et_text);
        editText.requestFocus();
        editText.setInputType(65536);
        editText.setImeOptions(5);
        editText.setFilters(inputFilterArr);
        imageView.setOnClickListener(this.onClickListener);
        imageView2.setOnClickListener(this.onClickListener);
        imageView3.setOnClickListener(this.onClickListener);
        editText.setOnClickListener(this.onClickListener);
        toDoRowViewsPojo.setEt_text(editText);
        toDoRowViewsPojo.setIv_rowUp(imageView);
        toDoRowViewsPojo.setIv_rowDown(imageView2);
        toDoRowViewsPojo.setIv_rowDel(imageView3);
        if (Common.ToDoListEdit) {
            editText.setText(str);
            toDoRowViewsPojo.setChecked(z);
        }
        this.rowViewsList.add(toDoRowViewsPojo);
    }

    public TableLayout swapDownTableRow(View view) {
        TableLayout tableLayout = this.tl_TodoTasks;
        TableRow tableRow = (TableRow) ((LinearLayout) ((LinearLayout) view.getParent()).getParent()).getParent();
        int indexOfChild = tableLayout.indexOfChild(tableRow);
        int i = indexOfChild + 1;
        TableRow tableRow2 = (TableRow) tableLayout.getChildAt(i);
        if (indexOfChild != this.tl_TodoTasks.getChildCount() - 1) {
            try {
                tableLayout.removeView(tableRow);
                tableLayout.addView(tableRow, i);
                tableRow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_row_up));
                tableLayout.removeView(tableRow2);
                tableLayout.addView(tableRow2, indexOfChild);
                tableRow2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_row_down));
                return tableLayout;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ToDoRowViewsPojo toDoRowViewsPojo = (ToDoRowViewsPojo) this.rowViewsList.get(indexOfChild);
                this.rowViewsList.remove(indexOfChild);
                this.rowViewsList.add(i, toDoRowViewsPojo);
            }
        }
        return this.tl_TodoTasks;
    }

    public TableLayout swapUpTableRow(View view) {
        TableLayout tableLayout = this.tl_TodoTasks;
        TableRow tableRow = (TableRow) ((LinearLayout) ((LinearLayout) view.getParent()).getParent()).getParent();
        int indexOfChild = tableLayout.indexOfChild(tableRow);
        int i = indexOfChild - 1;
        TableRow tableRow2 = (TableRow) tableLayout.getChildAt(i);
        if (indexOfChild > 0) {
            try {
                tableLayout.removeView(tableRow);
                tableLayout.addView(tableRow, i);
                tableRow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_row_down));
                tableLayout.removeView(tableRow2);
                tableLayout.addView(tableRow2, indexOfChild);
                tableRow2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_row_up));
                return tableLayout;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ToDoRowViewsPojo toDoRowViewsPojo = (ToDoRowViewsPojo) this.rowViewsList.get(indexOfChild);
                this.rowViewsList.remove(indexOfChild);
                this.rowViewsList.add(i, toDoRowViewsPojo);
            }
        }
        return this.tl_TodoTasks;
    }

    public boolean SaveToDoList() {
        boolean z;
        String currentDateTime2 = Utilities.getCurrentDateTime2();
        String title = this.toDo.getTitle();
        this.toDo.setTitle(this.toDoTitle);
        this.toDo.setTodoColor(this.toDoColor);
        this.toDo.setDateModified(currentDateTime2);
        ArrayList arrayList = new ArrayList();
        for (ToDoRowViewsPojo toDoRowViewsPojo : this.rowViewsList) {
            ToDoTask toDoTask = new ToDoTask();
            toDoTask.setToDo(toDoRowViewsPojo.getEt_text().getText().toString());
            toDoTask.setChecked(toDoRowViewsPojo.isChecked());
            arrayList.add(toDoTask);
            if (toDoRowViewsPojo.getEt_text().getText().toString().trim().equals("")) {
                Toast.makeText(this, "Can't save empty field", Toast.LENGTH_SHORT).show();
                Utilities.hideKeyboard(this.ll_addTask, this);
                return false;
            }
        }
        this.toDo.setToDoList(arrayList);
        ToDoWriteInXML toDoWriteInXML = new ToDoWriteInXML();
        if (Common.ToDoListEdit) {
            z = toDoWriteInXML.saveToDoList(this, this.toDo, title, Common.ToDoListEdit);
        } else {
            this.toDo.setDateCreated(currentDateTime2);
            z = toDoWriteInXML.saveToDoList(this, this.toDo, this.toDoTitle, Common.ToDoListEdit);
        }
        if (z) {
            ToDoDB_Pojo toDoDB_Pojo = new ToDoDB_Pojo();
            toDoDB_Pojo.setToDoFileName(this.toDoTitle);
            StringBuilder sb = new StringBuilder();
            sb.append(StorageOptionsCommon.STORAGEPATH);
            sb.append(StorageOptionsCommon.TODOLIST);
            sb.append(this.toDoTitle);
            sb.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
            toDoDB_Pojo.setToDoFileLocation(sb.toString());
            toDoDB_Pojo.setToDoFileColor(this.toDoColor);
            toDoDB_Pojo.setToDoFileCreatedDate(currentDateTime2);
            toDoDB_Pojo.setToDoFileModifiedDate(currentDateTime2);
            toDoDB_Pojo.setToDoFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
            toDoDB_Pojo.setToDoFileTask1(((ToDoTask) arrayList.get(0)).getToDo());
            toDoDB_Pojo.setToDoFileTask1IsChecked(((ToDoTask) arrayList.get(0)).isChecked());
            if (arrayList.size() >= 2) {
                toDoDB_Pojo.setToDoFileTask2(((ToDoTask) arrayList.get(1)).getToDo());
                toDoDB_Pojo.setToDoFileTask2IsChecked(((ToDoTask) arrayList.get(1)).isChecked());
            }
            if (Common.ToDoListEdit) {
                ToDoDAL toDoDAL = this.dal;
                this.constants.getClass();
                toDoDAL.updateToDoFileInfoInDatabase(toDoDB_Pojo, "ToDoId", String.valueOf(Common.ToDoListId));
            } else {
                this.dal.addToDoInfoInDatabase(toDoDB_Pojo);
            }
            ToDoDAL toDoDAL2 = this.dal;
            StringBuilder sb2 = new StringBuilder();
            this.constants.getClass();
            sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
            this.constants.getClass();
            sb2.append("ToDoName");
            sb2.append("='");
            sb2.append(this.toDoTitle);
            sb2.append("'");
            Common.ToDoListId = toDoDAL2.GetToDoDbFileIntegerEntity(sb2.toString());
            if (Common.ToDoListEdit) {
                Common.ToDoListEdit = false;
                SecurityLocksCommon.IsAppDeactive = false;
                startActivity(new Intent(this, ViewToDoActivity.class));
                finish();
                overridePendingTransition(17432576, 17432577);
            } else {
                SecurityLocksCommon.IsAppDeactive = false;
                startActivity(new Intent(this, ToDoActivity.class));
                finish();
                overridePendingTransition(17432576, 17432577);
            }
        } else {
            Toast.makeText(this, "Failed to Save ToDo", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void setToDoColor() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.dialog_note_color_picker);
        final ColorPickerView colorPickerView = (ColorPickerView) dialog.findViewById(R.id.color_picker_view);
        colorPickerView.setAlpha(0.3f);
        colorPickerView.setDensity(4);
        TextView textView = (TextView) dialog.findViewById(R.id.tv_yes);
        ((TextView) dialog.findViewById(R.id.tv_no)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        textView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    int selectedColor = colorPickerView.getSelectedColor();
                    Log.i("color", String.valueOf(selectedColor));
                    StringBuilder sb = new StringBuilder();
                    sb.append("#33");
                    sb.append(Integer.toHexString(selectedColor).substring(2));
                    String sb2 = sb.toString();
                    Log.i("scolor", sb2);
                    AddToDoActivity.this.ll_main.setBackgroundColor(Color.parseColor(sb2));
                    AddToDoActivity.this.toDoColor = sb2;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDiscardDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.confirmation_message_box);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_Ok);
        LinearLayout linearLayout2 = (LinearLayout) dialog.findViewById(R.id.ll_Cancel);
        ((TextView) dialog.findViewById(R.id.tvmessagedialogtitle)).setText(R.string.discard_changes);
        linearLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (Common.ToDoListEdit) {
                    Common.ToDoListEdit = false;
                    SecurityLocksCommon.IsAppDeactive = false;
                    AddToDoActivity.this.startActivity(new Intent(AddToDoActivity.this, ViewToDoActivity.class));
                    AddToDoActivity.this.finish();
                    AddToDoActivity.this.overridePendingTransition(17432576, 17432577);
                } else {
                    SecurityLocksCommon.IsAppDeactive = false;
                    AddToDoActivity.this.startActivity(new Intent(AddToDoActivity.this, ToDoActivity.class));
                    AddToDoActivity.this.finish();
                    AddToDoActivity.this.overridePendingTransition(17432576, 17432577);
                }
                dialog.cancel();
            }
        });
        linearLayout2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_color, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId != 16908332) {
            if (itemId == R.id.action_menu_add) { /*2131296289*/
                this.toDoTitle = this.et_ToDoTitle.getText().toString().trim();
                if (this.toDoTitle.trim().equals("")) {
                    this.toDoTitle = this.tempTitle;
                }
                if (!ValidationCommon.isNoSpecialCharsInNameExceptBrackets(this.toDoTitle)) {
                    Toast.makeText(this, "Todo name is incorrect", Toast.LENGTH_SHORT).show();
                } else {
                    Common.ToDoListName = this.toDoTitle;
                    SaveToDoList();
                }
            } else if (itemId == R.id.action_menu_color) { /*2131296290*/
                setToDoColor();
            }
        } else if (this.hasModified) {
            showDiscardDialog();
        } else if (Common.ToDoListEdit) {
            Common.ToDoListEdit = false;
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, ViewToDoActivity.class));
            finish();
            overridePendingTransition(17432576, 17432577);
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, ToDoActivity.class));
            finish();
            overridePendingTransition(17432576, 17432577);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onBackPressed() {
        if (this.hasModified) {
            showDiscardDialog();
        } else if (Common.ToDoListEdit) {
            Common.ToDoListEdit = false;
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, ViewToDoActivity.class));
            finish();
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, ToDoActivity.class));
            finish();
            overridePendingTransition(17432576, 17432577);
        }
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
