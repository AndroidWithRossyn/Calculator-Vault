package com.example.vault.notes;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;


import com.example.vault.notes.adapter.NotesFolderGridViewAdapter;
import com.example.vault.notes.model.NotesFileDB_Pojo;
import com.example.vault.notes.model.NotesFolderDB_Pojo;
import com.example.vault.notes.util.NotesCommon;
import com.example.vault.notes.util.NotesFilesDAL;
import com.example.vault.notes.util.NotesFolderDAL;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import java.io.File;
import java.io.IOException;
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
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.FileUtils;
import com.example.vault.utilities.Utilities;
import com.example.vault.wallet.util.CommonSharedPreferences;

public class NotesFoldersActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    boolean IsSortingDropdown = false;
    CommonSharedPreferences commonSharedPreferences;
    Constants constants;
    Dialog dialog;
    int folderPosition = -1;
    GridView gv_NotesFolder;
    boolean isEdittable = false;
    public boolean isGridview = true;
    AppCompatImageView iv_NotesFolderDelete;
    AppCompatImageView iv_NotesFolderRename;
    LinearLayout ll_NotesFolderEdit;
    FloatingActionButton mFab;
    NotesCommon notesCommon;
    NotesFilesDAL notesFilesDAL;
    NotesFolderDAL notesFolderDAL;

    List<NotesFolderDB_Pojo> notesFolderPojoList;
    NotesFolderGridViewAdapter notesFolderadapter;
    private SensorManager sensorManager;
    int sortBy;
    private Toolbar toolbar;




        public class ClickListener implements OnClickListener {
            public ClickListener() {
            }

            public void onClick(View view) {
                if (NotesFoldersActivity.this.isEdittable && NotesFoldersActivity.this.folderPosition >= 0) {
                    int id = view.getId();
                    if (id == R.id.iv_NotesFolderDelete) { /*2131296531*/
                        if (((NotesFolderDB_Pojo) NotesFoldersActivity.this.notesFolderPojoList.get(NotesFoldersActivity.this.folderPosition)).getNotesFolderName().equals("My Notes")) {
                            Toast.makeText(NotesFoldersActivity.this, "Default folder can not be deleted", Toast.LENGTH_SHORT).show();
                        }
                        NotesFoldersActivity.this.show_Dialog(view, NotesFoldersActivity.this.folderPosition);
                        NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(View.GONE);
                        NotesFoldersActivity.this.isEdittable = false;
                        NotesFoldersActivity.this.setGridview();

                    } else if (id == R.id.iv_NotesFolderRename) { /*2131296532*/
                        if (((NotesFolderDB_Pojo) NotesFoldersActivity.this.notesFolderPojoList.get(NotesFoldersActivity.this.folderPosition)).getNotesFolderName().equals("My Notes")) {
                            Toast.makeText(NotesFoldersActivity.this, "Default folder can not be renamed", Toast.LENGTH_SHORT).show();

                        }
                        NotesFoldersActivity.this.show_Dialog(view, NotesFoldersActivity.this.folderPosition);
                        NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(View.GONE);
                        NotesFoldersActivity.this.isEdittable = false;
                        NotesFoldersActivity.this.setGridview();

                    }

                }
            }
        }

        public class ItemClickListener implements OnItemClickListener {
            public ItemClickListener() {
            }

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (NotesFoldersActivity.this.isEdittable) {
                    NotesFoldersActivity.this.isEdittable = false;
                    NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(View.GONE);
                    NotesFoldersActivity.this.notesFolderadapter.setFocusedPosition(i);
                    NotesFoldersActivity.this.notesFolderadapter.setIsEdit(NotesFoldersActivity.this.isEdittable);
                    if (!NotesFoldersActivity.this.isGridview) {
                        NotesFoldersActivity.this.gv_NotesFolder.setNumColumns(1);
                    } else {
                        NotesFoldersActivity.this.gv_NotesFolder.setNumColumns(1);
                    }
                    NotesFoldersActivity.this.gv_NotesFolder.setAdapter(NotesFoldersActivity.this.notesFolderadapter);
                    NotesFoldersActivity.this.notesFolderadapter.notifyDataSetChanged();
                    return;
                }
                SecurityLocksCommon.IsAppDeactive = false;
                NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(View.GONE);
                NotesCommon.CurrentNotesFolder = ((NotesFolderDB_Pojo) NotesFoldersActivity.this.notesFolderPojoList.get(i)).getNotesFolderName();
                NotesCommon.CurrentNotesFolderId = ((NotesFolderDB_Pojo) NotesFoldersActivity.this.notesFolderPojoList.get(i)).getNotesFolderId();
                NotesFoldersActivity.this.startActivity(new Intent(NotesFoldersActivity.this, NotesFilesActivity.class));
                NotesFoldersActivity.this.finish();
            }
        }

        public class ItemLongClickListeners implements OnItemLongClickListener {
            public ItemLongClickListeners() {
            }

            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (NotesFoldersActivity.this.isEdittable) {
                    NotesFoldersActivity.this.isEdittable = false;
                    NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(View.GONE);
                } else {
                    NotesFoldersActivity.this.isEdittable = true;
                    NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(View.VISIBLE);
                }
                NotesFoldersActivity.this.folderPosition = i;
                NotesFoldersActivity.this.notesFolderadapter.setFocusedPosition(i);
                NotesFoldersActivity.this.notesFolderadapter.setIsEdit(NotesFoldersActivity.this.isEdittable);
                if (!NotesFoldersActivity.this.isGridview) {
                    NotesFoldersActivity.this.gv_NotesFolder.setNumColumns(1);
                } else {
                    NotesFoldersActivity.this.gv_NotesFolder.setNumColumns(1);
                }
                NotesFoldersActivity.this.gv_NotesFolder.setAdapter(NotesFoldersActivity.this.notesFolderadapter);
                NotesFoldersActivity.this.notesFolderadapter.notifyDataSetChanged();
                return true;
            }
        }



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
        setContentView((int) R.layout.activity_notes_folders);


        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.gv_NotesFolder = (GridView) findViewById(R.id.gv_NotesFolder);
        this.ll_NotesFolderEdit = (LinearLayout) findViewById(R.id.ll_NotesFolderEdit);
        this.iv_NotesFolderRename = (AppCompatImageView) findViewById(R.id.iv_NotesFolderRename);
        this.iv_NotesFolderDelete = (AppCompatImageView) findViewById(R.id.iv_NotesFolderDelete);
        this.mFab = (FloatingActionButton) findViewById(R.id.fabbutton);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        this.notesCommon = new NotesCommon();
        this.notesFolderPojoList = new ArrayList();
        this.notesFolderDAL = new NotesFolderDAL(this);
        this.notesFilesDAL = new NotesFilesDAL(this);

        this.constants = new Constants();
        SecurityLocksCommon.IsAppDeactive = true;
        setSupportActionBar(this.toolbar);
//        getSupportActionBar().setTitle((CharSequence) "Notes");
    // getSupportActionBar().setTitle("");

    this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
        this.commonSharedPreferences = CommonSharedPreferences.GetObject(this);
        if (this.commonSharedPreferences.get_viewByNotesFolder() == 0) {
            this.isGridview = false;
        } else {
            this.isGridview = true;
        }
        this.sortBy = this.commonSharedPreferences.get_sortByNotesFolder();
        checkIsDefaultFolderCreated();
        setGridview();
        GridView gridView = this.gv_NotesFolder;

        gridView.setOnItemClickListener(new ItemClickListener());
        AppCompatImageView appCompatImageView = this.iv_NotesFolderRename;

        appCompatImageView.setOnClickListener(new ClickListener());
        AppCompatImageView appCompatImageView2 = this.iv_NotesFolderDelete;

        appCompatImageView2.setOnClickListener(new ClickListener());
        GridView gridView2 = this.gv_NotesFolder;

        gridView2.setOnItemLongClickListener(new ItemLongClickListeners());
        try {
            File file = new File(getFilesDir(), "Recordings");
            if (file.exists()) {
                FileUtils.deleteDirectory(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    for (NotesFolderDB_Pojo notesFolderDB_Pojo : NotesFoldersActivity.this.notesFolderPojoList) {
                        if (notesFolderDB_Pojo.getNotesFolderName().toLowerCase().contains(str)) {
                            arrayList.add(notesFolderDB_Pojo);
                        }
                    }
                } else {
                    arrayList = NotesFoldersActivity.this.notesFolderPojoList;
                }
                NotesFoldersActivity.this.bindSearchResult(arrayList);
                return true;
            }
        });
        return true;
    }

    public void bindSearchResult(List<NotesFolderDB_Pojo> list) {
        this.notesFolderadapter = new NotesFolderGridViewAdapter(this, list);
        this.gv_NotesFolder.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), this.isGridview));
        this.notesFolderadapter.setIsGridview(this.isGridview);
        this.gv_NotesFolder.setNumColumns(1);
        this.gv_NotesFolder.setAdapter(this.notesFolderadapter);
        this.notesFolderadapter.notifyDataSetChanged();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
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
            CloudCommon.ModuleType = DropboxType.Notes.ordinal();
            Utilities.StartCloudActivity(this);
        }

//        else {
//            SecurityLocksCommon.IsAppDeactive = false;
//            InAppPurchaseActivity._cameFrom = CameFrom.NotesFolder.ordinal();
//            startActivity(new Intent(this, InAppPurchaseActivity.class));
//            finish();
//        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void checkIsDefaultFolderCreated() {
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.NOTES);
        File file = new File(sb.toString(), getResources().getString(R.string.my_notes));
        if (!file.exists()) {
            file.mkdirs();
        }
        NotesFolderDAL notesFolderDAL2 = this.notesFolderDAL;
        StringBuilder sb2 = new StringBuilder();
        this.constants.getClass();
        sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
        this.constants.getClass();
        sb2.append("NotesFolderName");
        sb2.append(" = '");
        sb2.append(getResources().getString(R.string.my_notes));
        sb2.append("' AND ");
        this.constants.getClass();
        sb2.append("NotesFolderIsDecoy");
        sb2.append(" = ");
        sb2.append(SecurityLocksCommon.IsFakeAccount);
        if (!notesFolderDAL2.IsFolderAlreadyExist(sb2.toString())) {
            String currentDate = this.notesCommon.getCurrentDate();
            NotesFolderDB_Pojo notesFolderDB_Pojo = new NotesFolderDB_Pojo();
            notesFolderDB_Pojo.setNotesFolderName(getResources().getString(R.string.my_notes));
            notesFolderDB_Pojo.setNotesFolderLocation(file.getAbsolutePath());
            notesFolderDB_Pojo.setNotesFolderCreatedDate(currentDate);
            notesFolderDB_Pojo.setNotesFolderModifiedDate(currentDate);
            notesFolderDB_Pojo.setNotesFolderFilesSortBy(1);
            notesFolderDB_Pojo.setNotesFolderColor(String.valueOf(getResources().getColor(R.color.ColorLightBlue)));
            notesFolderDB_Pojo.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
            this.notesFolderDAL.addNotesFolderInfoInDatabase(notesFolderDB_Pojo);
        }
    }

    public void setGridview() {
        if (this.sortBy == SortBy.Name.ordinal()) {
            NotesFolderDAL notesFolderDAL2 = this.notesFolderDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
            this.constants.getClass();
            sb.append("NotesFolderIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            sb.append(" ORDER BY ");
            this.constants.getClass();
            sb.append("NotesFolderName");
            sb.append(" COLLATE NOCASE ASC");
            this.notesFolderPojoList = notesFolderDAL2.getAllNotesFolderInfoFromDatabase(sb.toString());
        } else {
            NotesFolderDAL notesFolderDAL3 = this.notesFolderDAL;
            StringBuilder sb2 = new StringBuilder();
            this.constants.getClass();
            sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
            this.constants.getClass();
            sb2.append("NotesFolderIsDecoy");
            sb2.append(" = ");
            sb2.append(SecurityLocksCommon.IsFakeAccount);
            sb2.append(" ORDER BY ");
            this.constants.getClass();
            sb2.append("NotesFolderModifiedDate");
            sb2.append(" DESC");
            this.notesFolderPojoList = notesFolderDAL3.getAllNotesFolderInfoFromDatabase(sb2.toString());
        }
        this.notesFolderadapter = new NotesFolderGridViewAdapter(this, this.notesFolderPojoList);
        this.notesFolderadapter.setFocusedPosition(0);
        this.notesFolderadapter.setIsEdit(false);
        this.notesFolderadapter.setIsGridview(this.isGridview);
        this.gv_NotesFolder.setNumColumns(1);
//        this.gv_NotesFolder.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), this.isGridview));
        this.gv_NotesFolder.setAdapter(this.notesFolderadapter);
        this.notesFolderadapter.notifyDataSetChanged();
    }

    public void fabClicked(View view) {
        addFolderDialog();
    }

    public void addFolderDialog() {
        this.dialog = new Dialog(this, R.style.FullHeightDialog);
        this.dialog.setContentView(R.layout.dialog_add_notes_folder);
        this.dialog.setCancelable(true);
        TextView textView = (TextView) this.dialog.findViewById(R.id.lbl_Create_Edit_Album);
        LinearLayout linearLayout = (LinearLayout) this.dialog.findViewById(R.id.ll_Ok);
        LinearLayout linearLayout2 = (LinearLayout) this.dialog.findViewById(R.id.ll_Cancel);
        final EditText editText = (EditText) this.dialog.findViewById(R.id.et_folderName);
        final ImageView imageView = (ImageView) this.dialog.findViewById(R.id.iv_selectedColor);
        final ColorPicker colorPicker = (ColorPicker) this.dialog.findViewById(R.id.folder_colorPicker);
        colorPicker.addSVBar((SVBar) this.dialog.findViewById(R.id.svbar));
        colorPicker.addOpacityBar((OpacityBar) this.dialog.findViewById(R.id.opacitybar));
        colorPicker.requestFocus();
        colorPicker.setColor(getResources().getColor(R.color.ColorLightBlue));
        colorPicker.setOldCenterColor(getResources().getColor(R.color.ColorLightBlue));
        imageView.setBackgroundColor(getResources().getColor(R.color.ColorLightBlue));
        colorPicker.setOnColorChangedListener(new OnColorChangedListener() {
            public void onColorChanged(int i) {
                imageView.setBackgroundColor(i);
            }
        });
        textView.setText(getResources().getString(R.string.add_folder));
        editText.setHint(R.string.add_folder_hint);
        linearLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String trim = editText.getText().toString().trim();
                if (trim.equals("") || trim.isEmpty()) {
                    Toast.makeText(NotesFoldersActivity.this, "Enter folder Name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                NotesFoldersActivity.this.createFolder(trim, String.valueOf(colorPicker.getColor()));
                Log.i("color", String.valueOf(colorPicker.getColor()));
            }
        });
        linearLayout2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                NotesFoldersActivity.this.dialog.dismiss();
            }
        });
        this.dialog.show();
    }

    public void createFolder(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.NOTES);
        File file = new File(sb.toString(), str);
        this.constants.getClass();
        StringBuilder sb2 = new StringBuilder();
        this.constants.getClass();
        sb2.append("NotesFolderName");
        sb2.append(" = '");
        sb2.append(str);
        sb2.append("' AND ");
        this.constants.getClass();
        sb2.append("NotesFolderIsDecoy");
        sb2.append(" = ");
        sb2.append(SecurityLocksCommon.IsFakeAccount);
        String concat = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ".concat(sb2.toString());
        String currentDate = this.notesCommon.getCurrentDate();
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
            NotesFolderDB_Pojo notesFolderDB_Pojo = new NotesFolderDB_Pojo();
            notesFolderDB_Pojo.setNotesFolderName(str);
            notesFolderDB_Pojo.setNotesFolderLocation(file.getAbsolutePath());
            notesFolderDB_Pojo.setNotesFolderCreatedDate(currentDate);
            notesFolderDB_Pojo.setNotesFolderModifiedDate(currentDate);
            notesFolderDB_Pojo.setNotesFolderFilesSortBy(1);
            notesFolderDB_Pojo.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
            notesFolderDB_Pojo.setNotesFolderColor(str2);
            if (!this.notesFolderDAL.IsFolderAlreadyExist(concat)) {
                this.notesFolderDAL.addNotesFolderInfoInDatabase(notesFolderDB_Pojo);
                Toast.makeText(this, getResources().getString(R.string.note_folder_created_successfully), Toast.LENGTH_SHORT).show();
                setGridview();
                this.dialog.dismiss();
                return;
            }
            Toast.makeText(this, getResources().getString(R.string.note_folder_exists), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renameFolder(NotesFolderDB_Pojo notesFolderDB_Pojo, String str, String str2) {
        File file = new File(notesFolderDB_Pojo.getNotesFolderLocation());
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.NOTES);
        File file2 = new File(sb.toString(), str);
        if (str.equals(notesFolderDB_Pojo.getNotesFolderName())) {
            notesFolderDB_Pojo.setNotesFolderName(str);
            notesFolderDB_Pojo.setNotesFolderLocation(file2.getAbsolutePath());
            notesFolderDB_Pojo.setNotesFolderModifiedDate(this.notesCommon.getCurrentDate());
            notesFolderDB_Pojo.setNotesFolderColor(str2);
            notesFolderDB_Pojo.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
            NotesFolderDAL notesFolderDAL2 = this.notesFolderDAL;
            this.constants.getClass();
            notesFolderDAL2.updateNotesFolderFromDatabase(notesFolderDB_Pojo, "NotesFolderId", String.valueOf(notesFolderDB_Pojo.getNotesFolderId()));
            updateNotesFolderPath(notesFolderDB_Pojo.getNotesFolderId(), str, file2.getAbsolutePath());
            setGridview();
            Toast.makeText(this, getResources().getString(R.string.note_folder_renamed_successfully), Toast.LENGTH_SHORT).show();
            return;
        }
        if (file.exists()) {
            NotesFolderDAL notesFolderDAL3 = this.notesFolderDAL;
            StringBuilder sb2 = new StringBuilder();
            this.constants.getClass();
            sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
            this.constants.getClass();
            sb2.append("NotesFolderId");
            sb2.append(" = ");
            sb2.append(notesFolderDB_Pojo.getNotesFolderId());
            sb2.append(" AND ");
            this.constants.getClass();
            sb2.append("NotesFolderIsDecoy");
            sb2.append(" = ");
            sb2.append(SecurityLocksCommon.IsFakeAccount);
            if (notesFolderDAL3.IsFolderAlreadyExist(sb2.toString())) {
                NotesFolderDAL notesFolderDAL4 = this.notesFolderDAL;
                StringBuilder sb3 = new StringBuilder();
                this.constants.getClass();
                sb3.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
                this.constants.getClass();
                sb3.append("NotesFolderName");
                sb3.append(" = '");
                sb3.append(str);
                sb3.append("' AND ");
                this.constants.getClass();
                sb3.append("NotesFolderIsDecoy");
                sb3.append(" = ");
                sb3.append(SecurityLocksCommon.IsFakeAccount);
                if (notesFolderDAL4.IsFolderAlreadyExist(sb3.toString())) {
                    Toast.makeText(this, getResources().getString(R.string.note_folder_exists), Toast.LENGTH_SHORT).show();
                } else if (file.renameTo(file2)) {
                    notesFolderDB_Pojo.setNotesFolderName(str);
                    notesFolderDB_Pojo.setNotesFolderLocation(file2.getAbsolutePath());
                    notesFolderDB_Pojo.setNotesFolderModifiedDate(this.notesCommon.getCurrentDate());
                    notesFolderDB_Pojo.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
                    notesFolderDB_Pojo.setNotesFolderColor(str2);
                    NotesFolderDAL notesFolderDAL5 = this.notesFolderDAL;
                    this.constants.getClass();
                    notesFolderDAL5.updateNotesFolderFromDatabase(notesFolderDB_Pojo, "NotesFolderId", String.valueOf(notesFolderDB_Pojo.getNotesFolderId()));
                    updateNotesFolderPath(notesFolderDB_Pojo.getNotesFolderId(), str, file2.getAbsolutePath());
                    setGridview();
                }
                Toast.makeText(this, getResources().getString(R.string.note_folder_renamed_successfully), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(this, getResources().getString(R.string.note_folder_exists), Toast.LENGTH_SHORT).show();
    }

    public boolean deleteFolder(int i, String str) {
        File[] listFiles;
        File file = new File(str);
        NotesFolderDAL notesFolderDAL2 = this.notesFolderDAL;
        StringBuilder sb = new StringBuilder();
        this.constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
        this.constants.getClass();
        sb.append("NotesFolderId");
        sb.append(" = ");
        sb.append(i);
        sb.append(" AND ");
        this.constants.getClass();
        sb.append("NotesFolderIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        if (notesFolderDAL2.IsFolderAlreadyExist(sb.toString())) {
            NotesFolderDAL notesFolderDAL3 = this.notesFolderDAL;
            this.constants.getClass();
            notesFolderDAL3.deleteNotesFolderFromDatabase("NotesFolderId", String.valueOf(i));
        }
        if (file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                if (file2.exists()) {
                    file2.delete();
                    NotesFilesDAL notesFilesDAL2 = this.notesFilesDAL;
                    this.constants.getClass();
                    notesFilesDAL2.deleteNotesFileFromDatabase("NotesFolderId", String.valueOf(i));
                }
            }
        }
        return file.delete();
    }

    public void updateNotesFolderPath(int i, String str, String str2) {
        new ArrayList();
        NotesFilesDAL notesFilesDAL2 = this.notesFilesDAL;
        StringBuilder sb = new StringBuilder();
        this.constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
        this.constants.getClass();
        sb.append("NotesFolderId");
        sb.append(" = ");
        sb.append(String.valueOf(i));
        sb.append(" AND ");
        this.constants.getClass();
        sb.append("NotesFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        for (NotesFileDB_Pojo notesFileDB_Pojo : notesFilesDAL2.getAllNotesFileInfoFromDatabase(sb.toString())) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str2);
            sb2.append(File.separator);
            sb2.append(notesFileDB_Pojo.getNotesFileName());
            sb2.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
            notesFileDB_Pojo.setNotesFileLocation(sb2.toString());
            NotesFilesDAL notesFilesDAL3 = this.notesFilesDAL;
            this.constants.getClass();
            notesFilesDAL3.updateNotesFileLocationInDatabase(notesFileDB_Pojo, "NotesFolderId", String.valueOf(i));
        }
    }

    public void show_Dialog(View view, final int i) {
        final Dialog dialog2 = new Dialog(this, R.style.FullHeightDialog);
        int id = view.getId();
        if (id == R.id.iv_NotesFolderDelete) { /*2131296531*/
            dialog2.setContentView(R.layout.confirmation_message_box);
            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView textView = (TextView) dialog2.findViewById(R.id.tvmessagedialogtitle);
            LinearLayout linearLayout = (LinearLayout) dialog2.findViewById(R.id.ll_Ok);
            LinearLayout linearLayout2 = (LinearLayout) dialog2.findViewById(R.id.ll_Cancel);
            StringBuilder sb = new StringBuilder();
            sb.append(getResources().getString(R.string.delete_toast));
            sb.append(" ");
            sb.append(((NotesFolderDB_Pojo) this.notesFolderPojoList.get(i)).getNotesFolderName());
            sb.append(" ?");
            textView.setText(sb.toString());
            linearLayout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    NotesFoldersActivity notesFoldersActivity = NotesFoldersActivity.this;
                    if (notesFoldersActivity.deleteFolder(((NotesFolderDB_Pojo) notesFoldersActivity.notesFolderPojoList.get(i)).getNotesFolderId(), ((NotesFolderDB_Pojo) NotesFoldersActivity.this.notesFolderPojoList.get(i)).getNotesFolderLocation())) {
                        NotesFoldersActivity notesFoldersActivity2 = NotesFoldersActivity.this;
                        Toast.makeText(notesFoldersActivity2, notesFoldersActivity2.getResources().getString(R.string.note_folder_deleted_successfully), Toast.LENGTH_SHORT).show();
                        NotesFoldersActivity.this.setGridview();
                    } else {
                        NotesFoldersActivity notesFoldersActivity3 = NotesFoldersActivity.this;
                        Toast.makeText(notesFoldersActivity3, notesFoldersActivity3.getResources().getString(R.string.note_folder_not_deleted), Toast.LENGTH_SHORT).show();
                    }
                    dialog2.dismiss();
                }
            });
            linearLayout2.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    dialog2.dismiss();
                }
            });
        } else if (id == R.id.iv_NotesFolderRename) { /*2131296532*/
            dialog2.setContentView(R.layout.dialog_add_notes_folder);
            TextView textView2 = (TextView) dialog2.findViewById(R.id.lbl_Create_Edit_Album);
            LinearLayout linearLayout3 = (LinearLayout) dialog2.findViewById(R.id.ll_Ok);
            LinearLayout linearLayout4 = (LinearLayout) dialog2.findViewById(R.id.ll_Cancel);
            final EditText editText = (EditText) dialog2.findViewById(R.id.et_folderName);
            final ImageView imageView = (ImageView) dialog2.findViewById(R.id.iv_selectedColor);
            final ColorPicker colorPicker = (ColorPicker) dialog2.findViewById(R.id.folder_colorPicker);
            colorPicker.addSVBar((SVBar) dialog2.findViewById(R.id.svbar));
            colorPicker.addOpacityBar((OpacityBar) dialog2.findViewById(R.id.opacitybar));
            colorPicker.requestFocus();
            editText.setText(((NotesFolderDB_Pojo) this.notesFolderPojoList.get(i)).getNotesFolderName());
            try {
                int parseInt = Integer.parseInt(((NotesFolderDB_Pojo) this.notesFolderPojoList.get(i)).getNotesFolderColor());
                colorPicker.setColor(parseInt);
                colorPicker.setOldCenterColor(parseInt);
                imageView.setBackgroundColor(parseInt);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            colorPicker.setOnColorChangedListener(new OnColorChangedListener() {
                public void onColorChanged(int i) {
                    imageView.setBackgroundColor(i);
                }
            });
            textView2.setText("Rename");
            editText.setHint(R.string.add_folder_hint);
            final int i2 = i;
            final Dialog dialog3 = dialog2;
            OnClickListener r0 = new OnClickListener() {
                public void onClick(View view) {
                    String trim = editText.getText().toString().trim();
                    if (trim.equals("") || trim.isEmpty()) {
                        Toast.makeText(NotesFoldersActivity.this, "Enter Folder name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    NotesFoldersActivity notesFoldersActivity = NotesFoldersActivity.this;
                    notesFoldersActivity.renameFolder((NotesFolderDB_Pojo) notesFoldersActivity.notesFolderPojoList.get(i2), trim, String.valueOf(colorPicker.getColor()));
                    dialog3.dismiss();
                }
            };
            linearLayout3.setOnClickListener(r0);
            linearLayout4.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    dialog2.dismiss();
                }
            });
        }
        dialog2.show();
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
                if (str.equals("Tile")) {
                    NotesFoldersActivity notesFoldersActivity = NotesFoldersActivity.this;
                    notesFoldersActivity.isGridview = true;
                    notesFoldersActivity.commonSharedPreferences.set_viewByNotesFolder(NotesFoldersActivity.this.isGridview ? 1 : 0);
                }
                if (str.equals("List")) {
                    NotesFoldersActivity notesFoldersActivity2 = NotesFoldersActivity.this;
                    notesFoldersActivity2.isGridview = false;
                    notesFoldersActivity2.commonSharedPreferences.set_viewByNotesFolder(NotesFoldersActivity.this.isGridview ? 1 : 0);
                }
                if (str.equals("Name")) {
                    NotesFoldersActivity notesFoldersActivity3 = NotesFoldersActivity.this;
                    notesFoldersActivity3.sortBy = 0;
                    notesFoldersActivity3.commonSharedPreferences.set_sortByNotesFolder(NotesFoldersActivity.this.sortBy);
                }
                if (str.equals("Time")) {
                    NotesFoldersActivity notesFoldersActivity4 = NotesFoldersActivity.this;
                    notesFoldersActivity4.sortBy = 1;
                    notesFoldersActivity4.commonSharedPreferences.set_sortByNotesFolder(NotesFoldersActivity.this.sortBy);
                }
                NotesFoldersActivity.this.setGridview();
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
        this.gv_NotesFolder.setNumColumns(Utilities.getNoOfColumns(this, configuration.orientation, true));
        setGridview();
    }

    public void onBackPressed() {
        if (this.isEdittable) {
            this.isEdittable = false;
            this.ll_NotesFolderEdit.setVisibility(View.GONE);
            setGridview();
            return;
        }
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
