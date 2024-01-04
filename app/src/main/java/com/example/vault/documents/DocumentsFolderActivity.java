package com.example.vault.documents;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.vault.documents.adapter.AppDocumentsAdapter;
import com.example.vault.documents.adapter.DocumentsFolderAdapter;
import com.example.vault.documents.model.DocumentFolder;
import com.example.vault.documents.model.DocumentsEnt;
import com.example.vault.documents.util.DocumentDAL;
import com.example.vault.documents.util.DocumentFolderDAL;
import com.example.vault.documents.util.DocumentsFolderGalleryMethods;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.banrossyn.imageloader.utils.LibCommonAppClass;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.example.vault.R;
import com.example.vault.adapter.ExpandableListAdapter1;
import com.example.vault.common.Constants;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.notes.util.SystemBarTintManager;
import com.example.vault.notes.util.UIElementsHelper;
import com.example.vault.panicswitch.AccelerometerListener;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securebackupcloud.CloudCommon;
import com.example.vault.securebackupcloud.CloudCommon.DropboxType;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.AppSettingsSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;

public class DocumentsFolderActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    public static int albumPosition = 0;
    public static boolean isEdit = false;
    public static boolean isGridView = true;
    int AlbumId = 0;
    private com.example.vault.documents.util.DocumentFolderDAL DocumentFolderDAL;
    boolean IsMoreDropdown = false;
    public ProgressBar Progress;
    int _SortBy = 0;

    public DocumentsFolderAdapter adapter;
    AppSettingsSharedPreferences appSettingsSharedPreferences;
    private FloatingActionButton btn_Add_Album;

    public AppDocumentsAdapter docadapter;

    public ArrayList<DocumentFolder> documentFolders;

    public ArrayList<DocumentsEnt> documentList;
    String folderName = "";

    public GridView gridView;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                DocumentsFolderActivity.this.Progress.setVisibility(View.GONE);
            }
            super.handleMessage(message);
        }
    };
    private ImageButton ib_more;

    public boolean isSearch = false;
    LinearLayout ll_EditAlbum;
    //    LayoutParams ll_EditAlbum_Hide_Params;
//    LayoutParams ll_EditAlbum_Show_Params;
    LinearLayout ll_background;
    LinearLayout ll_delete_btn;
    LinearLayout ll_import_from_camera_btn;
    LinearLayout ll_import_from_gallery_btn;
    LinearLayout ll_rename_btn;
    int position;
    private SensorManager sensorManager;
    private Toolbar toolbar;


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
        setContentView((int) R.layout.activity_docuement_folders);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        this.toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(this.toolbar);
//        getSupportActionBar().setTitle((CharSequence) "Documents");
        // getSupportActionBar().setTitle("");

        this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
        this.gridView = (GridView) findViewById(R.id.AlbumsGalleryGrid);
        this.btn_Add_Album = (FloatingActionButton) findViewById(R.id.btn_Add_Album);
        LibCommonAppClass.IsPhoneGalleryLoad = true;
        SecurityLocksCommon.IsAppDeactive = true;
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        getWindow().addFlags(128);
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_EditAlbum = (LinearLayout) findViewById(R.id.ll_EditAlbum);
        this.gridView = (GridView) findViewById(R.id.AlbumsGalleryGrid);
        this.ll_rename_btn = (LinearLayout) findViewById(R.id.ll_rename_btn);
        this.ll_delete_btn = (LinearLayout) findViewById(R.id.ll_delete_btn);
        this.ll_import_from_gallery_btn = (LinearLayout) findViewById(R.id.ll_move_btn);
        this.ll_import_from_camera_btn = (LinearLayout) findViewById(R.id.ll_share_btn);
        this.ib_more = (ImageButton) findViewById(R.id.ib_more);
        this.ib_more.setVisibility(View.VISIBLE);
        this.appSettingsSharedPreferences = AppSettingsSharedPreferences.GetObject(this);
        this._SortBy = this.appSettingsSharedPreferences.GetDocumentFoldersSortBy();
        if (isGridView) {
            this.gridView.setNumColumns(1);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
        } else {
            this.gridView.setNumColumns(1);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
        }
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(3);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(5);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        }
        this.ll_background.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (DocumentsFolderActivity.this.IsMoreDropdown) {
                    DocumentsFolderActivity.this.IsMoreDropdown = false;
                }
                return false;
            }
        });
        this.Progress.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                documentsFolderActivity.GetFodlersFromDatabase(documentsFolderActivity._SortBy);
                Message message = new Message();
                message.what = 1;
                DocumentsFolderActivity.this.handle.sendMessage(message);
            }
        }, 300);
        this.btn_Add_Album.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!DocumentsFolderActivity.isEdit) {
                    DocumentsFolderActivity.this.AddAlbumPopup();
                }
            }
        });
        this.gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                DocumentsFolderActivity.albumPosition = DocumentsFolderActivity.this.gridView.getFirstVisiblePosition();
                if (DocumentsFolderActivity.isEdit) {
                    DocumentsFolderActivity.isEdit = false;
//                    DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
                    DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                    DocumentsFolderAdapter documentsFolderAdapter = new DocumentsFolderAdapter(documentsFolderActivity, 17367043, documentsFolderActivity.documentFolders, i, DocumentsFolderActivity.isEdit, DocumentsFolderActivity.isGridView);
                    documentsFolderActivity.adapter = documentsFolderAdapter;
                    DocumentsFolderActivity.this.gridView.setAdapter(DocumentsFolderActivity.this.adapter);
                    DocumentsFolderActivity.this.adapter.notifyDataSetChanged();
                } else if (DocumentsFolderActivity.this.isSearch) {
                    DocumentsFolderActivity.this.isSearch = false;
                    int id = ((DocumentsEnt) DocumentsFolderActivity.this.documentList.get(i)).getId();
                    DocumentDAL documentDAL = new DocumentDAL(DocumentsFolderActivity.this);
                    documentDAL.OpenRead();
                    String folderLockDocumentLocation = documentDAL.GetDocumentById(Integer.toString(id)).getFolderLockDocumentLocation();
                    documentDAL.close();
                    String FileName = Utilities.FileName(folderLockDocumentLocation);
                    if (FileName.contains("#")) {
                        FileName = Utilities.ChangeFileExtentionToOrignal(FileName);
                    }
                    File file = new File(folderLockDocumentLocation);
                    StringBuilder sb = new StringBuilder();
                    sb.append(file.getParent());
                    sb.append("/");
                    sb.append(FileName);
                    File file2 = new File(sb.toString());
                    file.renameTo(file2);
                    DocumentsFolderActivity.this.CopyTempFile(file2.getAbsolutePath());
                } else {
                    SecurityLocksCommon.IsAppDeactive = false;
                    Common.FolderId = ((DocumentFolder) DocumentsFolderActivity.this.documentFolders.get(i)).getId();
                    DocumentsFolderActivity.this.startActivity(new Intent(DocumentsFolderActivity.this, DocumentsActivity.class));
                    DocumentsFolderActivity.this.finish();
                }
            }
        });
        this.gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                DocumentsFolderActivity.albumPosition = DocumentsFolderActivity.this.gridView.getFirstVisiblePosition();
                if (DocumentsFolderActivity.isEdit) {
                    DocumentsFolderActivity.isEdit = false;
//                    DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
                    DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                    DocumentsFolderAdapter documentsFolderAdapter = new DocumentsFolderAdapter(documentsFolderActivity, 17367043, documentsFolderActivity.documentFolders, i, DocumentsFolderActivity.isEdit, DocumentsFolderActivity.isGridView);
                    documentsFolderActivity.adapter = documentsFolderAdapter;
                    DocumentsFolderActivity.this.gridView.setAdapter(DocumentsFolderActivity.this.adapter);
                    DocumentsFolderActivity.this.adapter.notifyDataSetChanged();
                } else {
                    DocumentsFolderActivity.isEdit = true;
//                    DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Show_Params);
                    DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                    documentsFolderActivity2.position = i;
                    documentsFolderActivity2.AlbumId = Common.FolderId;
                    DocumentsFolderActivity documentsFolderActivity3 = DocumentsFolderActivity.this;
                    documentsFolderActivity3.folderName = ((DocumentFolder) documentsFolderActivity3.documentFolders.get(DocumentsFolderActivity.this.position)).getFolderName();
                    DocumentsFolderActivity documentsFolderActivity4 = DocumentsFolderActivity.this;
                    DocumentsFolderAdapter documentsFolderAdapter2 = new DocumentsFolderAdapter(documentsFolderActivity4, 17367043, documentsFolderActivity4.documentFolders, i, DocumentsFolderActivity.isEdit, DocumentsFolderActivity.isGridView);
                    documentsFolderActivity4.adapter = documentsFolderAdapter2;
                    DocumentsFolderActivity.this.gridView.setAdapter(DocumentsFolderActivity.this.adapter);
                    DocumentsFolderActivity.this.adapter.notifyDataSetChanged();
                }
                if (DocumentsFolderActivity.albumPosition != 0) {
                    DocumentsFolderActivity.this.gridView.setSelection(DocumentsFolderActivity.albumPosition);
                }
                return true;
            }
        });
        this.ll_rename_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (((DocumentFolder) DocumentsFolderActivity.this.documentFolders.get(DocumentsFolderActivity.this.position)).getId() != 1) {
                    DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                    documentsFolderActivity.EditAlbumPopup(((DocumentFolder) documentsFolderActivity.documentFolders.get(DocumentsFolderActivity.this.position)).getId(), ((DocumentFolder) DocumentsFolderActivity.this.documentFolders.get(DocumentsFolderActivity.this.position)).getFolderName(), ((DocumentFolder) DocumentsFolderActivity.this.documentFolders.get(DocumentsFolderActivity.this.position)).getFolderLocation());
                    return;
                }
                Toast.makeText(DocumentsFolderActivity.this, R.string.lbl_default_folder_notrenamed, Toast.LENGTH_SHORT).show();
                DocumentsFolderActivity.isEdit = false;
//                DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
                DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                DocumentsFolderAdapter documentsFolderAdapter = new DocumentsFolderAdapter(documentsFolderActivity2, 17367043, documentsFolderActivity2.documentFolders, 0, DocumentsFolderActivity.isEdit, DocumentsFolderActivity.isGridView);
                documentsFolderActivity2.adapter = documentsFolderAdapter;
                DocumentsFolderActivity.this.gridView.setAdapter(DocumentsFolderActivity.this.adapter);
                DocumentsFolderActivity.this.adapter.notifyDataSetChanged();
            }
        });
        this.ll_delete_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (((DocumentFolder) DocumentsFolderActivity.this.documentFolders.get(DocumentsFolderActivity.this.position)).getId() != 1) {
                    DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                    documentsFolderActivity.DeleteALertDialog(((DocumentFolder) documentsFolderActivity.documentFolders.get(DocumentsFolderActivity.this.position)).getId(), ((DocumentFolder) DocumentsFolderActivity.this.documentFolders.get(DocumentsFolderActivity.this.position)).getFolderName(), ((DocumentFolder) DocumentsFolderActivity.this.documentFolders.get(DocumentsFolderActivity.this.position)).getFolderLocation());
                    return;
                }
                Toast.makeText(DocumentsFolderActivity.this, R.string.lbl_default_folder_notdeleted, Toast.LENGTH_SHORT).show();
                DocumentsFolderActivity.isEdit = false;
//                DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
                DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                DocumentsFolderAdapter documentsFolderAdapter = new DocumentsFolderAdapter(documentsFolderActivity2, 17367043, documentsFolderActivity2.documentFolders, 0, DocumentsFolderActivity.isEdit, DocumentsFolderActivity.isGridView);
                documentsFolderActivity2.adapter = documentsFolderAdapter;
                DocumentsFolderActivity.this.gridView.setAdapter(DocumentsFolderActivity.this.adapter);
                DocumentsFolderActivity.this.adapter.notifyDataSetChanged();
            }
        });
        int i = albumPosition;
        if (i != 0) {
            this.gridView.setSelection(i);
            albumPosition = 0;
        }
        documentBind();
    }

    public void btnOnCloudClick() {
        if (Common.isPurchased) {
            SecurityLocksCommon.IsAppDeactive = false;
            CloudCommon.ModuleType = DropboxType.Documents.ordinal();
            Utilities.StartCloudActivity(this);
            return;
        }
//        SecurityLocksCommon.IsAppDeactive = false;
//        InAppPurchaseActivity._cameFrom = CameFrom.DocuementFolder.ordinal();
//        startActivity(new Intent(this, InAppPurchaseActivity.class));
//        finish();
    }

    private void documentBind() {
        DocumentDAL documentDAL = new DocumentDAL(this);
        documentDAL.OpenRead();
        this.documentList = (ArrayList) documentDAL.GetAllDocuments();
        AppDocumentsAdapter appDocumentsAdapter = new AppDocumentsAdapter(this, 17367043, this.documentList, false, 1);
        this.docadapter = appDocumentsAdapter;
        this.gridView.setAdapter(this.adapter);
        documentDAL.close();
    }

    public void btnOnMoreClick() {
        this.IsMoreDropdown = false;
        showPopupWindow();
    }

    public void showPopupWindow() {
        View inflate = ((LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_window_expandable, null);
        final PopupWindow popupWindow = new PopupWindow(inflate, -2, -2, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        ArrayList arrayList = new ArrayList();
        HashMap hashMap = new HashMap();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ExpandableListView expandableListView = (ExpandableListView) inflate.findViewById(R.id.expListview);
        arrayList.add("View by");
        arrayList2.add("List");
        arrayList2.add("Tile");
        hashMap.put(arrayList.get(0), arrayList2);
        arrayList.add("Sort by");
        arrayList3.add("Name");
        arrayList3.add("Date");
        hashMap.put(arrayList.get(1), arrayList3);
        expandableListView.setAdapter(new ExpandableListAdapter1(this, arrayList, hashMap));
        expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {
            public void onGroupExpand(int i) {
                Log.v("", "yes");
            }
        });
        expandableListView.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
                if (i != 0) {
                    if (i == 1) {
                        switch (i2) {
                            case 0:
                                DocumentsFolderActivity.this._SortBy = SortBy.Name.ordinal();
                                DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                                documentsFolderActivity.GetFodlersFromDatabase(documentsFolderActivity._SortBy);
                                DocumentsFolderActivity.this.appSettingsSharedPreferences.SetDocumentFoldersSortBy(DocumentsFolderActivity.this._SortBy);
                                popupWindow.dismiss();
                                DocumentsFolderActivity.this.IsMoreDropdown = false;
                                break;
                            case 1:
                                DocumentsFolderActivity.this._SortBy = SortBy.Time.ordinal();
                                DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                                documentsFolderActivity2.GetFodlersFromDatabase(documentsFolderActivity2._SortBy);
                                DocumentsFolderActivity.this.appSettingsSharedPreferences.SetDocumentFoldersSortBy(DocumentsFolderActivity.this._SortBy);
                                popupWindow.dismiss();
                                DocumentsFolderActivity.this.IsMoreDropdown = false;
                                break;
                        }
                    }
                } else {
                    switch (i2) {
                        case 0:
                            DocumentsFolderActivity.isGridView = false;
                            DocumentsFolderActivity.this.ViewBy();
                            popupWindow.dismiss();
                            DocumentsFolderActivity.this.IsMoreDropdown = false;
                            break;
                        case 1:
                            DocumentsFolderActivity.isGridView = true;
                            DocumentsFolderActivity.this.ViewBy();
                            popupWindow.dismiss();
                            DocumentsFolderActivity.this.IsMoreDropdown = false;
                            break;
                    }
                }
                return false;
            }
        });
        if (!this.IsMoreDropdown) {
            Toolbar toolbar2 = this.toolbar;
            popupWindow.showAsDropDown(toolbar2, toolbar2.getWidth(), 0);
            this.IsMoreDropdown = true;
            return;
        }
        popupWindow.dismiss();
        this.IsMoreDropdown = false;
    }

    public void ViewBy() {
        if (isGridView) {
            this.gridView.setNumColumns(2);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
        } else {
            this.gridView.setNumColumns(1);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
        }
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(3);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(5);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        }
        DocumentsFolderAdapter documentsFolderAdapter = new DocumentsFolderAdapter(this, 17367043, this.documentFolders, 0, isEdit, isGridView);
        this.adapter = documentsFolderAdapter;
        this.gridView.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
    }

    public void btnBackonClick(View view) {
        if (isEdit) {
            SecurityLocksCommon.IsAppDeactive = false;
            isEdit = false;
//            this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
            DocumentsFolderAdapter documentsFolderAdapter = new DocumentsFolderAdapter(this, 17367043, this.documentFolders, 0, isEdit, isGridView);
            this.adapter = documentsFolderAdapter;
            this.gridView.setAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();
        } else if (this.isSearch) {
            this.isSearch = false;
            GetFodlersFromDatabase(this._SortBy);
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, MainiFeaturesActivity.class));
            finish();
        }
    }


    public void GetFodlersFromDatabase(int i) {
        DocumentFolderDAL documentFolderDAL;
        isEdit = false;
        this.DocumentFolderDAL = new DocumentFolderDAL(this);
        try {
            this.DocumentFolderDAL.OpenRead();
            this.documentFolders = (ArrayList) this.DocumentFolderDAL.GetFolders(i);
            DocumentsFolderAdapter documentsFolderAdapter = new DocumentsFolderAdapter(this, 17367043, this.documentFolders, 0, isEdit, isGridView);
            this.adapter = documentsFolderAdapter;
            this.gridView.setAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();
            documentFolderDAL = this.DocumentFolderDAL;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            documentFolderDAL = this.DocumentFolderDAL;
        } catch (Throwable th) {
            DocumentFolderDAL documentFolderDAL2 = this.DocumentFolderDAL;
            if (documentFolderDAL2 != null) {
                documentFolderDAL2.close();
            }
            throw th;
        }
    }


    public void AddAlbumPopup() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.album_add_edit_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        TextView textView = (TextView) dialog.findViewById(R.id.lbl_Create_Edit_Album);
        textView.setTypeface(createFromAsset);
        textView.setText(R.string.lbl_Document_folder_Create_Album);
        ((TextView) dialog.findViewById(R.id.lbl_Ok)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_Cancel)).setTypeface(createFromAsset);
        final EditText editText = (EditText) dialog.findViewById(R.id.txt_AlbumName);
        editText.setHint(R.string.lbl_Document_folder_Create_Folder_enter);
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (editText.getEditableText().toString().length() <= 0 || editText.getEditableText().toString().trim().isEmpty()) {
                    Toast.makeText(DocumentsFolderActivity.this, R.string.lbl_Document_folder_Create_Folder_please_enter, Toast.LENGTH_SHORT).show();
                    return;
                }
                DocumentsFolderActivity.this.folderName = editText.getEditableText().toString();
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append("/");
                sb.append(StorageOptionsCommon.DOCUMENTS);
                sb.append(DocumentsFolderActivity.this.folderName);
                File file = new File(sb.toString());
                if (file.exists()) {
                    DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("\"");
                    sb2.append(DocumentsFolderActivity.this.folderName);
                    sb2.append("\" already exist");
                    Toast.makeText(documentsFolderActivity, sb2.toString(), Toast.LENGTH_SHORT).show();
                } else if (file.mkdirs()) {
                    DocumentsFolderGalleryMethods documentsFolderGalleryMethods = new DocumentsFolderGalleryMethods();
                    DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                    documentsFolderGalleryMethods.AddFolderToDatabase(documentsFolderActivity2, documentsFolderActivity2.folderName);
                    Toast.makeText(DocumentsFolderActivity.this, R.string.lbl_Document_folder_Create_Album_Success, Toast.LENGTH_SHORT).show();
                    DocumentsFolderActivity documentsFolderActivity3 = DocumentsFolderActivity.this;
                    documentsFolderActivity3.GetFodlersFromDatabase(documentsFolderActivity3._SortBy);
                    dialog.dismiss();
                } else {
                    Toast.makeText(DocumentsFolderActivity.this, "ERROR! Some Error in creating folder", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void EditAlbumPopup(int i, String str, String str2) {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.album_add_edit_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        TextView textView = (TextView) dialog.findViewById(R.id.lbl_Create_Edit_Album);
        textView.setTypeface(createFromAsset);
        textView.setText(R.string.lbl_Document_folder_Rename_Album);
        ((TextView) dialog.findViewById(R.id.lbl_Ok)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_Cancel)).setTypeface(createFromAsset);
        final EditText editText = (EditText) dialog.findViewById(R.id.txt_AlbumName);
        editText.setHint(R.string.lbl_Document_folder_Create_Folder_enter);
        if (str.length() > 0) {
            editText.setText(str);
        }
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_Ok);
        final String str3 = str2;
        final String str4 = str;
        final int i2 = i;
        final Dialog dialog2 = dialog;
        OnClickListener r0 = new OnClickListener() {
            public void onClick(View view) {
                if (editText.getEditableText().toString().length() <= 0 || editText.getEditableText().toString().trim().isEmpty()) {
                    Toast.makeText(DocumentsFolderActivity.this, R.string.lbl_Document_folder_Create_Folder_please_enter, Toast.LENGTH_SHORT).show();
                    return;
                }
                DocumentsFolderActivity.this.folderName = editText.getEditableText().toString();
                if (new File(str3).exists()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(StorageOptionsCommon.STORAGEPATH);
                    sb.append(StorageOptionsCommon.DOCUMENTS);
                    sb.append(DocumentsFolderActivity.this.folderName);
                    File file = new File(sb.toString());
                    if (file.exists()) {
                        DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("\"");
                        sb2.append(DocumentsFolderActivity.this.folderName);
                        sb2.append("\" already exist");
                        Toast.makeText(documentsFolderActivity, sb2.toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(StorageOptionsCommon.STORAGEPATH);
                    sb3.append(StorageOptionsCommon.DOCUMENTS);
                    sb3.append(str4);
                    File file2 = new File(sb3.toString());
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    if (file2.renameTo(file)) {
                        DocumentsFolderGalleryMethods documentsFolderGalleryMethods = new DocumentsFolderGalleryMethods();
                        DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                        documentsFolderGalleryMethods.UpdateAlbumInDatabase(documentsFolderActivity2, i2, documentsFolderActivity2.folderName);
                        Toast.makeText(DocumentsFolderActivity.this, R.string.lbl_Photos_Album_Create_Album_Success_renamed, Toast.LENGTH_SHORT).show();
                        DocumentsFolderActivity documentsFolderActivity3 = DocumentsFolderActivity.this;
                        documentsFolderActivity3.GetFodlersFromDatabase(documentsFolderActivity3._SortBy);
                        dialog2.dismiss();
//                        DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
                    }
                }
            }
        };
        linearLayout.setOnClickListener(r0);
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
//                DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
                DocumentsFolderActivity.isEdit = false;
                DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                DocumentsFolderAdapter documentsFolderAdapter = new DocumentsFolderAdapter(documentsFolderActivity, 17367043, documentsFolderActivity.documentFolders, DocumentsFolderActivity.this.position, DocumentsFolderActivity.isEdit, DocumentsFolderActivity.isGridView);
                documentsFolderActivity.adapter = documentsFolderAdapter;
                DocumentsFolderActivity.this.gridView.setAdapter(DocumentsFolderActivity.this.adapter);
                DocumentsFolderActivity.this.adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void DeleteALertDialog(int i, String str, String str2) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.confirmation_message_box);
        dialog.setCancelable(true);
        TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
        textView.setTypeface(Typeface.createFromAsset(getAssets(), "ebrima.ttf"));
        if (str.length() > 9) {
            StringBuilder sb = new StringBuilder();
            sb.append("Are you sure you want to delete ");
            sb.append(str.substring(0, 8));
            sb.append("... including its data?");
            textView.setText(sb.toString());
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Are you sure you want to delete ");
            sb2.append(str);
            sb2.append(" including its data?");
            textView.setText(sb2.toString());
        }
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_Ok);
        final int i2 = i;
        final String str3 = str;
        final String str4 = str2;
        final Dialog dialog2 = dialog;
        OnClickListener r0 = new OnClickListener() {
            public void onClick(View view) {
                DocumentsFolderActivity.this.DeleteAlbum(i2, str3, str4);
                dialog2.dismiss();
//                DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
            }
        };
        linearLayout.setOnClickListener(r0);
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DocumentsFolderActivity.isEdit = false;
//                DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
                DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                DocumentsFolderAdapter documentsFolderAdapter = new DocumentsFolderAdapter(documentsFolderActivity, 17367043, documentsFolderActivity.documentFolders, DocumentsFolderActivity.this.position, DocumentsFolderActivity.isEdit, DocumentsFolderActivity.isGridView);
                documentsFolderActivity.adapter = documentsFolderAdapter;
                DocumentsFolderActivity.this.gridView.setAdapter(DocumentsFolderActivity.this.adapter);
                DocumentsFolderActivity.this.adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void DeleteAlbum(int i, String str, String str2) {
        File file = new File(str2);
        DeleteAlbumFromDatabase(i);
        try {
            Utilities.DeleteAlbum(file, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void DeleteAlbumFromDatabase(int i) {
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this);
        try {
            documentFolderDAL.OpenWrite();
            documentFolderDAL.DeleteFolderById(i);
            Toast.makeText(this, R.string.lbl_Photos_Album_delete_success, Toast.LENGTH_SHORT).show();
            GetFodlersFromDatabase(this._SortBy);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            documentFolderDAL.close();
            throw th;
        }
        documentFolderDAL.close();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (configuration.orientation == 2) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(5);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (isGridView) {
                this.gridView.setNumColumns(3);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        } else if (configuration.orientation != 1) {
        } else {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(3);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.document_folder_menu, menu);
        menu.findItem(R.id.action_search).setIcon(R.drawable.top_srch_icon);
        menu.findItem(R.id.action_view).setIcon(R.drawable.ic_more_top_bar_icon);
        menu.findItem(R.id.action_cloud).setIcon(R.drawable.ic_topcloudicon);
        ((SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search))).setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                return true;
            }

            public boolean onQueryTextChange(String str) {
                if (str.length() > 0) {
                    DocumentsFolderActivity.this.isSearch = true;
                    ArrayList arrayList = new ArrayList();
                    Iterator it = DocumentsFolderActivity.this.documentList.iterator();
                    while (it.hasNext()) {
                        DocumentsEnt documentsEnt = (DocumentsEnt) it.next();
                        if (documentsEnt.getDocumentName().toLowerCase().contains(str)) {
                            arrayList.add(documentsEnt);
                        }
                    }
                    DocumentsFolderActivity.this.gridView.setNumColumns(1);
                    DocumentsFolderActivity.this.gridView.setVerticalSpacing(Utilities.convertDptoPix(DocumentsFolderActivity.this.getApplicationContext(), 4));
                    DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                    AppDocumentsAdapter appDocumentsAdapter = new AppDocumentsAdapter(documentsFolderActivity, 17367043, arrayList, false, 1);
                    documentsFolderActivity.docadapter = appDocumentsAdapter;
                    DocumentsFolderActivity.this.gridView.setAdapter(DocumentsFolderActivity.this.docadapter);
                } else {
                    DocumentsFolderActivity.this.isSearch = false;
                    DocumentsFolderActivity.this.ViewBy();
                    DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                    documentsFolderActivity2.GetFodlersFromDatabase(documentsFolderActivity2._SortBy);
                }
                return true;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(getApplicationContext(), MainiFeaturesActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.action_cloud) {
            btnOnCloudClick();
            return true;
        } else if (itemId != R.id.action_view) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            btnOnMoreClick();
            return true;
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    private void applyKitKatTranslucency() {
        if (VERSION.SDK_INT >= 19) {
            setTranslucentStatus(true);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setNavigationBarTintEnabled(true);
            systemBarTintManager.setTintDrawable(UIElementsHelper.getGeneralActionBarBackground(this));
            this.toolbar.setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(this));
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean z) {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (z) {
            attributes.flags |= 67108864;
        } else {
            attributes.flags &= -67108865;
        }
        window.setAttributes(attributes);
    }


    public void CopyTempFile(String str) {
        File file = new File(str);
        try {
            Utilities.NSDecryption(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String guessContentTypeFromName = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
        Intent intent = new Intent("android.intent.action.VIEW");
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.FILE);
        sb.append(file.getAbsolutePath());
        intent.setDataAndType(Uri.parse(sb.toString()), guessContentTypeFromName);
        startActivity(intent);
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
//        this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
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

    public void onStop() {
        super.onStop();
    }

    public void onResume() {
//        this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            if (isEdit) {
                SecurityLocksCommon.IsAppDeactive = false;
                isEdit = false;
//                this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
                DocumentsFolderAdapter documentsFolderAdapter = new DocumentsFolderAdapter(this, 17367043, this.documentFolders, 0, isEdit, isGridView);
                this.adapter = documentsFolderAdapter;
                this.gridView.setAdapter(this.adapter);
                this.adapter.notifyDataSetChanged();
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, MainiFeaturesActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
