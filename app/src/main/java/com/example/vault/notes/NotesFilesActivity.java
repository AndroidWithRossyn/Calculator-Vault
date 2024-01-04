package com.example.vault.notes;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;


import com.example.vault.notes.adapter.MoveNoteAdapter;
import com.example.vault.notes.adapter.NotesFilesGridViewAdapter;
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

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
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
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;
import com.example.vault.wallet.util.CommonSharedPreferences;

import org.apache.commons.io.FileUtils;

public class NotesFilesActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    boolean IsSortingDropdown = false;
    CommonSharedPreferences commonSharedPreference;
    Constants constants;
    NotesFolderDB_Pojo currentFolderDBInfo;
    Dialog dialog;
    List<Integer> focusedPosition;
    GridView gv_NotesFiles;
    boolean isEdittable = false;
    AppCompatImageView iv_NotesFileDelete;
    AppCompatImageView iv_NotesFileMove;
    LinearLayout ll_NotesFileEdit;
    LinearLayout ll_noNotes;
    FloatingActionButton mFab;
    NotesCommon notesCommon;
    List<NotesFileDB_Pojo> notesFileDB_PojoList;
    NotesFilesDAL notesFilesDAL;
    NotesFilesGridViewAdapter notesFilesGridViewAdapter;

    NotesFolderDAL notesFolderDAL;
    List<NotesFolderDB_Pojo> notesFolderDB_PojoList;
    private SensorManager sensorManager;
    int sortBy;
    Toolbar toolbar;

    int viewBy = 0;


    public class NotesFilesOnClickListener implements OnClickListener {
        public NotesFilesOnClickListener() {
        }

        public void onClick(View view) {
            if (NotesFilesActivity.this.isEdittable && NotesFilesActivity.this.focusedPosition.size() > 0) {
                int id = view.getId();
                if (id == R.id.iv_NotesFileDelete) { /*2131296529*/
                    NotesFilesActivity.this.noteDeleteDialog();
                    NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(View.GONE);
                    NotesFilesActivity.this.isEdittable = false;
                    NotesFilesActivity.this.setGridview();
                } else if (id == R.id.iv_NotesFileMove) { /*2131296530*/
                    NotesFilesActivity.this.NoteMoveDialog();
                    NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(View.GONE);
                    NotesFilesActivity.this.isEdittable = false;
                    NotesFilesActivity.this.setGridview();

                }

            }
        }
    }

    public class NotesFilesOnItemClickListener implements OnItemClickListener {
        public NotesFilesOnItemClickListener() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (NotesFilesActivity.this.isEdittable) {
                if (NotesFilesActivity.this.focusedPosition.contains(Integer.valueOf(i))) {
                    NotesFilesActivity.this.focusedPosition.remove(NotesFilesActivity.this.focusedPosition.indexOf(Integer.valueOf(i)));
                    NotesFilesActivity.this.notesFilesGridViewAdapter.removeFocusedPosition(i);
                    if (NotesFilesActivity.this.focusedPosition.size() == 0) {
                        NotesFilesActivity.this.isEdittable = false;
                        NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(View.GONE);
                        NotesFilesActivity.this.notesFilesGridViewAdapter.removeAllFocusedPosition();
                        NotesFilesActivity.this.focusedPosition.clear();
                    }
                } else {
                    NotesFilesActivity.this.focusedPosition.add(Integer.valueOf(i));
                    NotesFilesActivity.this.notesFilesGridViewAdapter.setFocusedPosition(i);
                }
                NotesFilesActivity.this.notesFilesGridViewAdapter.setIsEdit(NotesFilesActivity.this.isEdittable);
                if (NotesFilesActivity.this.viewBy == 2) {
                    NotesFilesActivity.this.gv_NotesFiles.setNumColumns(2);
                } else {
                    NotesFilesActivity.this.gv_NotesFiles.setNumColumns(1);
                }
                NotesFilesActivity.this.gv_NotesFiles.setAdapter(NotesFilesActivity.this.notesFilesGridViewAdapter);
                NotesFilesActivity.this.notesFilesGridViewAdapter.notifyDataSetChanged();
                return;
            }
            NotesCommon.isEdittingNote = true;
            SecurityLocksCommon.IsAppDeactive = false;
            NotesCommon.CurrentNotesFile = ((NotesFileDB_Pojo) NotesFilesActivity.this.notesFileDB_PojoList.get(i)).getNotesFileName();
            NotesFilesActivity.this.startActivity(new Intent(NotesFilesActivity.this, MyNoteActivity.class));
            NotesFilesActivity.this.finish();
        }
    }

    public class NotesFilesOnItemLongClickListener implements OnItemLongClickListener {
        public NotesFilesOnItemLongClickListener() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (NotesFilesActivity.this.focusedPosition.contains(Integer.valueOf(i))) {
                NotesFilesActivity.this.focusedPosition.remove(NotesFilesActivity.this.focusedPosition.indexOf(Integer.valueOf(i)));
                NotesFilesActivity.this.notesFilesGridViewAdapter.removeFocusedPosition(i);
            } else {
                NotesFilesActivity.this.focusedPosition.add(Integer.valueOf(i));
                NotesFilesActivity.this.notesFilesGridViewAdapter.setFocusedPosition(i);
            }
            if (NotesFilesActivity.this.focusedPosition.size() > 0) {
                NotesFilesActivity.this.isEdittable = true;
                NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(View.VISIBLE);
            } else {
                NotesFilesActivity.this.isEdittable = false;
                NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(View.GONE);
            }
            NotesFilesActivity.this.notesFilesGridViewAdapter.setIsEdit(NotesFilesActivity.this.isEdittable);
            if (NotesFilesActivity.this.viewBy == 2) {
                NotesFilesActivity.this.gv_NotesFiles.setNumColumns(2);
            } else {
                NotesFilesActivity.this.gv_NotesFiles.setNumColumns(1);
            }
            NotesFilesActivity.this.gv_NotesFiles.setAdapter(NotesFilesActivity.this.notesFilesGridViewAdapter);
            NotesFilesActivity.this.notesFilesGridViewAdapter.notifyDataSetChanged();
            return true;
        }
    }


    public enum SortBy {
        Name, Time, Size
    }

    public enum ViewBy {
        Detail, List, Tile
    }

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_notes_files);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.gv_NotesFiles = (GridView) findViewById(R.id.gv_NotesFiles);
        this.mFab = (FloatingActionButton) findViewById(R.id.fabbutton_notesFiles);
        this.ll_NotesFileEdit = (LinearLayout) findViewById(R.id.ll_NotesFileEdit);
        this.ll_noNotes = (LinearLayout) findViewById(R.id.ll_noNotes);
        this.iv_NotesFileMove = (AppCompatImageView) findViewById(R.id.iv_NotesFileMove);
        this.iv_NotesFileDelete = (AppCompatImageView) findViewById(R.id.iv_NotesFileDelete);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.notesFileDB_PojoList = new ArrayList();
        this.notesCommon = new NotesCommon();
        this.notesFilesDAL = new NotesFilesDAL(this);
        this.notesFolderDAL = new NotesFolderDAL(this);

        this.notesFolderDB_PojoList = new ArrayList();
        this.focusedPosition = new ArrayList();
        this.constants = new Constants();
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
        sb.append("NotesFolderModifiedDate");
        sb.append(" DESC");
        this.notesFolderDB_PojoList = notesFolderDAL2.getAllNotesFolderInfoFromDatabase(sb.toString());
        this.currentFolderDBInfo = new NotesFolderDB_Pojo();
        SecurityLocksCommon.IsAppDeactive = true;
        getCurrentFolder();
        this.commonSharedPreference = CommonSharedPreferences.GetObject(this);
        this.sortBy = this.currentFolderDBInfo.getNotesFolderFilesSortBy();
        this.viewBy = this.commonSharedPreference.get_viewByNotesFile();
        TextView title = findViewById(R.id.title1);
        title.setText(NotesCommon.CurrentNotesFolder);
        setSupportActionBar(this.toolbar);
//        getSupportActionBar().setTitle((CharSequence) NotesCommon.CurrentNotesFolder);
        // getSupportActionBar().setTitle("");

        this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
        setGridview();
        GridView gridView = this.gv_NotesFiles;

        gridView.setOnItemClickListener(new NotesFilesOnItemClickListener());
        GridView gridView2 = this.gv_NotesFiles;

        gridView2.setOnItemLongClickListener(new NotesFilesOnItemLongClickListener());
        AppCompatImageView appCompatImageView = this.iv_NotesFileMove;

        appCompatImageView.setOnClickListener(new NotesFilesOnClickListener());
        AppCompatImageView appCompatImageView2 = this.iv_NotesFileDelete;

        appCompatImageView2.setOnClickListener(new NotesFilesOnClickListener());
    }

    public void getCurrentFolder() {
        NotesFolderDAL notesFolderDAL2 = this.notesFolderDAL;
        StringBuilder sb = new StringBuilder();
        this.constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
        this.constants.getClass();
        sb.append("NotesFolderId");
        sb.append(" = ");
        sb.append(NotesCommon.CurrentNotesFolderId);
        sb.append(" AND ");
        this.constants.getClass();
        sb.append("NotesFolderIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        this.currentFolderDBInfo = notesFolderDAL2.getNotesFolderInfoFromDatabase(sb.toString());
    }

    public void updateCurrentFolderSortBy() {
        this.currentFolderDBInfo.setNotesFolderFilesSortBy(this.sortBy);
        NotesFolderDAL notesFolderDAL2 = this.notesFolderDAL;
        NotesFolderDB_Pojo notesFolderDB_Pojo = this.currentFolderDBInfo;
        this.constants.getClass();
        notesFolderDAL2.updateNotesFolderFromDatabase(notesFolderDB_Pojo, "NotesFolderId", String.valueOf(this.currentFolderDBInfo.getNotesFolderId()));
    }

    public void updateCurrentFolderViewBy() {
        this.commonSharedPreference.set_viewByNotesFiles(this.viewBy);
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
                    for (NotesFileDB_Pojo notesFileDB_Pojo : NotesFilesActivity.this.notesFileDB_PojoList) {
                        if (notesFileDB_Pojo.getNotesFileName().toLowerCase().contains(str)) {
                            arrayList.add(notesFileDB_Pojo);
                        }
                    }
                } else {
                    arrayList = NotesFilesActivity.this.notesFileDB_PojoList;
                }
                NotesFilesActivity.this.bindSearchResult(arrayList);
                return true;
            }
        });
        return true;
    }

    public void bindSearchResult(List<NotesFileDB_Pojo> list) {
        this.notesFilesGridViewAdapter = new NotesFilesGridViewAdapter(this, list);
        if (this.viewBy == ViewBy.List.ordinal()) {
            this.gv_NotesFiles.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), false));
        } else if (this.viewBy == ViewBy.Tile.ordinal()) {
            this.gv_NotesFiles.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), true));
        } else {
            this.gv_NotesFiles.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), false));
        }
        this.notesFilesGridViewAdapter.setViewBy(this.viewBy);
        this.gv_NotesFiles.setAdapter(this.notesFilesGridViewAdapter);
        this.notesFilesGridViewAdapter.notifyDataSetChanged();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, NotesFoldersActivity.class));
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

    public void setGridview() {
        if (this.sortBy == SortBy.Name.ordinal()) {
            NotesFilesDAL notesFilesDAL2 = this.notesFilesDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            StringBuilder sb2 = new StringBuilder();
            this.constants.getClass();
            sb2.append("NotesFolderId");
            sb2.append(" = ");
            sb2.append(NotesCommon.CurrentNotesFolderId);
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ".concat(sb2.toString()));
            sb.append(" AND ");
            this.constants.getClass();
            sb.append("NotesFileIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            sb.append(" ORDER BY ");
            this.constants.getClass();
            sb.append("NotesFileName");
            sb.append(" COLLATE NOCASE ASC");
            this.notesFileDB_PojoList = notesFilesDAL2.getAllNotesFileInfoFromDatabase(sb.toString());
        } else if (this.sortBy == SortBy.Size.ordinal()) {
            NotesFilesDAL notesFilesDAL3 = this.notesFilesDAL;
            StringBuilder sb3 = new StringBuilder();
            this.constants.getClass();
            StringBuilder sb4 = new StringBuilder();
            this.constants.getClass();
            sb4.append("NotesFolderId");
            sb4.append(" = ");
            sb4.append(NotesCommon.CurrentNotesFolderId);
            sb3.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ".concat(sb4.toString()));
            sb3.append(" AND ");
            this.constants.getClass();
            sb3.append("NotesFileIsDecoy");
            sb3.append(" = ");
            sb3.append(SecurityLocksCommon.IsFakeAccount);
            sb3.append(" ORDER BY ");
            this.constants.getClass();
            sb3.append("NotesFileSize");
            sb3.append(" DESC");
            this.notesFileDB_PojoList = notesFilesDAL3.getAllNotesFileInfoFromDatabase(sb3.toString());
        } else {
            NotesFilesDAL notesFilesDAL4 = this.notesFilesDAL;
            StringBuilder sb5 = new StringBuilder();
            this.constants.getClass();
            StringBuilder sb6 = new StringBuilder();
            this.constants.getClass();
            sb6.append("NotesFolderId");
            sb6.append(" = ");
            sb6.append(NotesCommon.CurrentNotesFolderId);
            sb5.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ".concat(sb6.toString()));
            sb5.append(" AND ");
            this.constants.getClass();
            sb5.append("NotesFileIsDecoy");
            sb5.append(" = ");
            sb5.append(SecurityLocksCommon.IsFakeAccount);
            sb5.append(" ORDER BY ");
            this.constants.getClass();
            sb5.append("NotesFileModifiedDate");
            sb5.append(" DESC");
            this.notesFileDB_PojoList = notesFilesDAL4.getAllNotesFileInfoFromDatabase(sb5.toString());
        }
        if (this.viewBy == ViewBy.List.ordinal()) {
            this.gv_NotesFiles.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), false));
        } else if (this.viewBy == ViewBy.Tile.ordinal()) {
            this.gv_NotesFiles.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), true));
        } else {
            this.gv_NotesFiles.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), false));
        }
        if (this.notesFileDB_PojoList.size() > 0) {
            this.notesFilesGridViewAdapter = new NotesFilesGridViewAdapter(this, this.notesFileDB_PojoList);
            this.notesFilesGridViewAdapter.setIsEdit(false);
            this.notesFilesGridViewAdapter.setViewBy(this.viewBy);
            this.gv_NotesFiles.setAdapter(this.notesFilesGridViewAdapter);
            this.notesFilesGridViewAdapter.notifyDataSetChanged();
            this.ll_noNotes.setVisibility(View.GONE);
            return;
        }
        this.ll_noNotes.setVisibility(View.VISIBLE);
    }

    public void fabClicked(View view) {
        NotesCommon.isEdittingNote = false;
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, MyNoteActivity.class));
        finish();
    }

    public void onBackPressed() {
        if (this.isEdittable) {
            this.notesFilesGridViewAdapter.removeAllFocusedPosition();
            this.focusedPosition.clear();
            this.isEdittable = false;
            this.ll_NotesFileEdit.setVisibility(View.GONE);
            setGridview();
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, NotesFoldersActivity.class));
        finish();
    }

    public boolean deleteNote(int i, String str) {
        File file = new File(str);
        try {
            NotesFilesDAL notesFilesDAL2 = this.notesFilesDAL;
            this.constants.getClass();
            notesFilesDAL2.deleteNotesFileFromDatabase("NotesFileId", String.valueOf(i));
            if (file.exists()) {
                return file.delete();
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public void moveNote(int i, String str) {
        for (int i2 = 0; i2 < this.focusedPosition.size(); i2++) {
            int intValue = ((Integer) this.focusedPosition.get(i2)).intValue();
            new NotesFileDB_Pojo();
            NotesFileDB_Pojo notesFileDB_Pojo = (NotesFileDB_Pojo) this.notesFileDB_PojoList.get(intValue);
            File file = new File(notesFileDB_Pojo.getNotesFileLocation());
            StringBuilder sb = new StringBuilder();
            sb.append(StorageOptionsCommon.STORAGEPATH);
            sb.append(StorageOptionsCommon.NOTES);
            File file2 = new File(sb.toString(), str);
            StringBuilder sb2 = new StringBuilder();
            sb2.append(file2.getAbsolutePath());
            sb2.append(File.separator);
            sb2.append(notesFileDB_Pojo.getNotesFileName());
            sb2.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
            String sb3 = sb2.toString();
            if (file.exists()) {
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                try {
                    FileUtils.moveToDirectory(file, file2, true);
                    notesFileDB_Pojo.setNotesFileFolderId(i);
                    notesFileDB_Pojo.setNotesFileName(notesFileDB_Pojo.getNotesFileName());
                    notesFileDB_Pojo.setNotesFileModifiedDate(this.notesCommon.getCurrentDate());
                    notesFileDB_Pojo.setNotesFileLocation(sb3);
                    notesFileDB_Pojo.setNotesFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                    NotesFilesDAL notesFilesDAL2 = this.notesFilesDAL;
                    this.constants.getClass();
                    notesFilesDAL2.updateNotesFileInfoInDatabase(notesFileDB_Pojo, "NotesFileId", String.valueOf(((NotesFileDB_Pojo) this.notesFileDB_PojoList.get(intValue)).getNotesFileId()));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, R.string.toast_exists, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.toast_not_exists, Toast.LENGTH_SHORT).show();
            }
        }
        setGridview();
        Toast.makeText(this, R.string.toast_move, Toast.LENGTH_SHORT).show();
    }

    public void NoteMoveDialog() {
        final ArrayList arrayList = new ArrayList();
        for (NotesFolderDB_Pojo notesFolderDB_Pojo : this.notesFolderDB_PojoList) {
            if (!notesFolderDB_Pojo.getNotesFolderName().equals(NotesCommon.CurrentNotesFolder)) {
                arrayList.add(notesFolderDB_Pojo);
            }
        }
        if (arrayList.size() > 0) {
            final Dialog dialog2 = new Dialog(this, R.style.FullHeightDialog);
            dialog2.setContentView(R.layout.move_customlistview);
            ListView listView = (ListView) dialog2.findViewById(R.id.ListViewfolderslist);
            listView.setAdapter(new MoveNoteAdapter(this, arrayList));
            listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    NotesFilesActivity.this.moveNote(((NotesFolderDB_Pojo) arrayList.get(i)).getNotesFolderId(), ((NotesFolderDB_Pojo) arrayList.get(i)).getNotesFolderName());
                    dialog2.dismiss();
                }
            });
            dialog2.show();
            return;
        }
        Toast.makeText(this, R.string.no_other_folder_exists, Toast.LENGTH_SHORT).show();
        this.notesFilesGridViewAdapter.removeAllFocusedPosition();
        this.focusedPosition.clear();
    }

    public void noteDeleteDialog() {
        final Dialog dialog2 = new Dialog(this);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog2.setContentView(R.layout.confirmation_message_box);
        dialog2.setCancelable(false);
        LinearLayout linearLayout = (LinearLayout) dialog2.findViewById(R.id.ll_Ok);
        LinearLayout linearLayout2 = (LinearLayout) dialog2.findViewById(R.id.ll_Cancel);
        ((TextView) dialog2.findViewById(R.id.tvmessagedialogtitle)).setText(getResources().getString(R.string.delete_note));
        linearLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                for (int i = 0; i < NotesFilesActivity.this.focusedPosition.size(); i++) {
                    int intValue = ((Integer) NotesFilesActivity.this.focusedPosition.get(i)).intValue();
                    NotesFilesActivity notesFilesActivity = NotesFilesActivity.this;
                    if (!notesFilesActivity.deleteNote(((NotesFileDB_Pojo) notesFilesActivity.notesFileDB_PojoList.get(intValue)).getNotesFileId(), ((NotesFileDB_Pojo) NotesFilesActivity.this.notesFileDB_PojoList.get(intValue)).getNotesFileLocation())) {
                        NotesFilesActivity notesFilesActivity2 = NotesFilesActivity.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append(((NotesFileDB_Pojo) NotesFilesActivity.this.notesFileDB_PojoList.get(intValue)).getNotesFileName());
                        sb.append(" could not be deleted");
                        Toast.makeText(notesFilesActivity2, sb.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                NotesFilesActivity.this.setGridview();
                dialog2.dismiss();
                NotesFilesActivity.this.notesFilesGridViewAdapter.removeAllFocusedPosition();
                NotesFilesActivity.this.focusedPosition.clear();
            }
        });
        linearLayout2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog2.dismiss();
                NotesFilesActivity.this.notesFilesGridViewAdapter.removeAllFocusedPosition();
                NotesFilesActivity.this.focusedPosition.clear();
            }
        });
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
        arrayList2.add(getResources().getString(R.string.detail));
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
                if (str.equals("Detail")) {
                    NotesFilesActivity notesFilesActivity = NotesFilesActivity.this;
                    notesFilesActivity.viewBy = 0;
                    notesFilesActivity.updateCurrentFolderViewBy();
                }
                if (str.equals("List")) {
                    NotesFilesActivity notesFilesActivity2 = NotesFilesActivity.this;
                    notesFilesActivity2.viewBy = 1;
                    notesFilesActivity2.updateCurrentFolderViewBy();
                }
                if (str.equals("Tile")) {
                    NotesFilesActivity notesFilesActivity3 = NotesFilesActivity.this;
                    notesFilesActivity3.viewBy = 2;
                    notesFilesActivity3.updateCurrentFolderViewBy();
                }
                if (str.equals("Created Time")) {
                    NotesFilesActivity notesFilesActivity4 = NotesFilesActivity.this;
                    notesFilesActivity4.sortBy = 1;
                    notesFilesActivity4.updateCurrentFolderSortBy();
                } else if (str.equals("Modified Time")) {
                    NotesFilesActivity notesFilesActivity5 = NotesFilesActivity.this;
                    notesFilesActivity5.sortBy = 2;
                    notesFilesActivity5.updateCurrentFolderSortBy();
                } else {
                    NotesFilesActivity notesFilesActivity6 = NotesFilesActivity.this;
                    notesFilesActivity6.sortBy = 0;
                    notesFilesActivity6.updateCurrentFolderSortBy();
                }
                NotesFilesActivity.this.setGridview();
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
        this.gv_NotesFiles.setNumColumns(Utilities.getNoOfColumns(this, configuration.orientation, true));
        setGridview();
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
