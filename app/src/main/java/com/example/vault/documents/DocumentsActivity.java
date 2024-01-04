package com.example.vault.documents;


import static com.example.vault.utilities.Common.AppPackageName;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import androidx.core.content.FileProvider;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vault.documents.adapter.AppDocumentsAdapter;
import com.example.vault.documents.model.DocumentFolder;
import com.example.vault.documents.model.DocumentsEnt;
import com.example.vault.documents.util.DocumentDAL;
import com.example.vault.documents.util.DocumentFolderDAL;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.vault.R;
import com.example.vault.adapter.ExpandableListAdapter1;
import com.example.vault.BaseActivity;
import com.example.vault.common.Constants;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.photo.adapter.MoveAlbumAdapter;
import com.example.vault.privatebrowser.SecureBrowserActivity;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;

public class DocumentsActivity extends BaseActivity {
    public static int _ViewBy;
    public static ProgressDialog myProgressDialog;
    private boolean IsSelectAll = false;
    boolean IsSortingDropdown = false;
    int _SortBy = 1;
    ImageButton _btnSortingDropdown;
    private String[] _folderNameArray;

    public List<String> _folderNameArrayForMove = null;
    int albumId;

    public AppDocumentsAdapter appDocumentsAdapter;
    ImageButton btnSelectAll;
    private DocumentDAL documentDAL;
    List<DocumentsEnt> documentEntList;
    private DocumentFolder documentFolder;
    private DocumentFolderDAL documentFolderDAL;
    FloatingActionButton fabImpBrowser;
    FloatingActionButton fabImpGallery;
    FloatingActionButton fabImpPcMac;
    FloatingActionsMenu fabMenu;
    private int fileCount = 0;
    ImageView file_empty_icon;
    private ArrayList<String> files = new ArrayList<>();
    FrameLayout fl_bottom_baar;
    protected String folderLocation;
    String folderName;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 2) {
                DocumentsActivity.this.hideProgress();
                if (Common.isUnHide) {
                    Common.isUnHide = false;
                    Toast.makeText(DocumentsActivity.this, R.string.Unhide_error, Toast.LENGTH_SHORT).show();
                } else if (Common.isMove) {
                    Common.isMove = false;
                    Toast.makeText(DocumentsActivity.this, R.string.Move_error, Toast.LENGTH_SHORT).show();
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(DocumentsActivity.this, R.string.Delete_error, Toast.LENGTH_SHORT).show();
                }
            } else if (message.what == 4) {
                Toast.makeText(DocumentsActivity.this, R.string.toast_share, Toast.LENGTH_LONG).show();
            } else if (message.what == 3) {
                if (Common.isUnHide) {
                    Common.isUnHide = false;
                    Toast.makeText(DocumentsActivity.this, R.string.toast_unhide, Toast.LENGTH_LONG).show();
                    DocumentsActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent = new Intent(DocumentsActivity.this, DocumentsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        DocumentsActivity.this.startActivity(intent);
                        DocumentsActivity.this.finish();
                    }
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(DocumentsActivity.this, R.string.toast_delete, Toast.LENGTH_SHORT).show();
                    DocumentsActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent2 = new Intent(DocumentsActivity.this, DocumentsActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        DocumentsActivity.this.startActivity(intent2);
                        DocumentsActivity.this.finish();
                    }
                } else if (Common.isMove) {
                    Common.isMove = false;
                    Toast.makeText(DocumentsActivity.this, R.string.toast_move, Toast.LENGTH_SHORT).show();
                    DocumentsActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent3 = new Intent(DocumentsActivity.this, DocumentsActivity.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        DocumentsActivity.this.startActivity(intent3);
                        DocumentsActivity.this.finish();
                    }
                }
            }
            super.handleMessage(message);
        }
    };
    GridView imagegrid;
    boolean isEditMode = false;
    TextView lbl_album_name_topbaar;
    TextView lbl_file_empty;
    LinearLayout ll_AddPhotos_Bottom_Baar;
    LinearLayout ll_EditFiles;
//    LayoutParams ll_Hide_Params;
//    LayoutParams ll_Show_Params;
    private LinearLayout ll_anchor;
    LinearLayout ll_background;
    LinearLayout ll_delete_btn;
    LinearLayout ll_file_empty;
    LinearLayout ll_file_grid;
    LinearLayout ll_import_from_gallery_btn;
    LinearLayout ll_import_intenet_btn;
    LinearLayout ll_import_wifi_btn;
    LinearLayout ll_move_btn;
    LinearLayout ll_share_btn;
    LinearLayout ll_topbaar;
    LinearLayout ll_unhide_btn;

    public String moveToFolderLocation;
    int selectCount;
    private SensorManager sensorManager;
    private Toolbar toolbar;

    public enum SortBy {
        Time,
        Name,
        Size
    }

    public enum ViewBy {
        List,
        Detail
    }

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void showUnhideProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }


    public void showDeleteProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }


    public void showMoveProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    private void showIsImportingProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }


    public void hideProgress() {
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
    }

    private void showCopyFilesProcessForShareProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.documents_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DocumentsActivity.this.Back();
            }
        });
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
//        this.ll_Show_Params = new LayoutParams(-1, -2);
//        this.ll_Hide_Params = new LayoutParams(-2, 0);
        this.fl_bottom_baar = (FrameLayout) findViewById(R.id.fl_bottom_baar);
//        this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_AddPhotos_Bottom_Baar = (LinearLayout) findViewById(R.id.ll_AddPhotos_Bottom_Baar);
        this.ll_EditFiles = (LinearLayout) findViewById(R.id.ll_EditPhotos);
        this.imagegrid = (GridView) findViewById(R.id.customGalleryGrid);
        this.btnSelectAll = (ImageButton) findViewById(R.id.btnSelectAll);
        this.fabMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu);
        this.fabImpGallery = (FloatingActionButton) findViewById(R.id.btn_impGallery);
        this.fabImpBrowser = (FloatingActionButton) findViewById(R.id.btn_impBrowser);
        this.fabImpPcMac = (FloatingActionButton) findViewById(R.id.btn_impPcMac);
        this.fabImpGallery.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                DocumentsActivity.this.startActivity(new Intent(DocumentsActivity.this, DocumentsImportActivity.class));
                DocumentsActivity.this.finish();
            }
        });
        this.fabImpBrowser.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                DocumentsActivity documentsActivity = DocumentsActivity.this;
                Common.CurrentWebBrowserActivity = documentsActivity;
                DocumentsActivity.this.startActivity(new Intent(documentsActivity, SecureBrowserActivity.class));
                DocumentsActivity.this.finish();
            }
        });
        this.ll_import_from_gallery_btn = (LinearLayout) findViewById(R.id.ll_import_from_gallery_btn);
        this.ll_import_intenet_btn = (LinearLayout) findViewById(R.id.ll_import_intenet_btn);
        this.ll_delete_btn = (LinearLayout) findViewById(R.id.ll_delete_btn);
        this.ll_unhide_btn = (LinearLayout) findViewById(R.id.ll_unhide_btn);
        this.ll_move_btn = (LinearLayout) findViewById(R.id.ll_move_btn);
        this.ll_share_btn = (LinearLayout) findViewById(R.id.ll_share_btn);
        this._btnSortingDropdown = (ImageButton) findViewById(R.id.btnSort);
        this.ll_file_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.ll_file_grid = (LinearLayout) findViewById(R.id.ll_photo_video_grid);
        this.file_empty_icon = (ImageView) findViewById(R.id.photo_video_empty_icon);
        this.lbl_file_empty = (TextView) findViewById(R.id.lbl_photo_video_empty);
        this.ll_file_grid.setVisibility(View.VISIBLE);
        this.ll_file_empty.setVisibility(View.INVISIBLE);
        this.btnSelectAll.setVisibility(View.INVISIBLE);
        this.lbl_album_name_topbaar = (TextView) findViewById(R.id.lbl_album_name_topbaar);
        DocumentFolderDAL documentFolderDAL2 = new DocumentFolderDAL(this);
        documentFolderDAL2.OpenRead();
        DocumentFolder GetFolderById = documentFolderDAL2.GetFolderById(Integer.toString(Common.FolderId));
        this._SortBy = documentFolderDAL2.GetSortByFolderId(Common.FolderId);
        documentFolderDAL2.close();
        this.folderName = GetFolderById.getFolderName();
        Common.DocumentFolderName = this.folderName;
        this.folderLocation = GetFolderById.getFolderLocation();
        TextView title6 = findViewById(R.id.title6);
        title6.setText(this.folderName);
        // getSupportActionBar().setTitle("");

//        getSupportActionBar().setTitle((CharSequence) this.folderName);
        this.ll_background.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (DocumentsActivity.this.IsSortingDropdown) {
                    DocumentsActivity.this.IsSortingDropdown = false;
                }
                if (DocumentsActivity.this.IsSortingDropdown) {
                    DocumentsActivity.this.IsSortingDropdown = false;
                }
                return false;
            }
        });
        this.imagegrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (!DocumentsActivity.this.isEditMode) {
                    int id = ((DocumentsEnt) DocumentsActivity.this.documentEntList.get(i)).getId();
                    DocumentDAL documentDAL = new DocumentDAL(DocumentsActivity.this);
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
                    DocumentsActivity.this.CopyTempFile(file2.getAbsolutePath());
                }
            }
        });
        this.imagegrid.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                Common.PhotoThumbnailCurrentPosition = DocumentsActivity.this.imagegrid.getFirstVisiblePosition();
                DocumentsActivity documentsActivity = DocumentsActivity.this;
                documentsActivity.isEditMode = true;
//                documentsActivity.fl_bottom_baar.setLayoutParams(DocumentsActivity.this.ll_Show_Params);
                DocumentsActivity.this.ll_AddPhotos_Bottom_Baar.setVisibility(View.GONE);
                DocumentsActivity.this.ll_EditFiles.setVisibility(View.VISIBLE);
                DocumentsActivity.this._btnSortingDropdown.setVisibility(View.INVISIBLE);
                DocumentsActivity.this.btnSelectAll.setVisibility(View.VISIBLE);
                DocumentsActivity.this.invalidateOptionsMenu();
                ((DocumentsEnt) DocumentsActivity.this.documentEntList.get(i)).SetFileCheck(true);
                DocumentsActivity documentsActivity2 = DocumentsActivity.this;
                AppDocumentsAdapter appDocumentsAdapter = new AppDocumentsAdapter(documentsActivity2, 1, documentsActivity2.documentEntList, true, DocumentsActivity._ViewBy);
                documentsActivity2.appDocumentsAdapter = appDocumentsAdapter;
                DocumentsActivity.this.imagegrid.setAdapter(DocumentsActivity.this.appDocumentsAdapter);
                DocumentsActivity.this.appDocumentsAdapter.notifyDataSetChanged();
                if (Common.PhotoThumbnailCurrentPosition != 0) {
                    DocumentsActivity.this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
                }
                return true;
            }
        });
        this.btnSelectAll.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DocumentsActivity.this.SelectOrUnSelectAll();
            }
        });
        this.ll_import_from_gallery_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                DocumentsActivity.this.startActivity(new Intent(DocumentsActivity.this, DocumentsImportActivity.class));
                DocumentsActivity.this.finish();
            }
        });
        this.ll_import_intenet_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                DocumentsActivity documentsActivity = DocumentsActivity.this;
                Constants.CurrentWebBrowserActivity = documentsActivity;
                DocumentsActivity.this.startActivity(new Intent(documentsActivity, SecureBrowserActivity.class));
                DocumentsActivity.this.finish();
            }
        });
        this.ll_delete_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DocumentsActivity.this.DeleteFiles();
            }
        });
        this.ll_unhide_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DocumentsActivity.this.UnhideFiles();
            }
        });
        this.ll_move_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DocumentsActivity.this.MoveFiles();
            }
        });
        this.ll_share_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!DocumentsActivity.this.IsFileCheck()) {
                    Toast.makeText(DocumentsActivity.this, R.string.toast_unselectphotomsg_share, Toast.LENGTH_SHORT).show();
                } else {
                    DocumentsActivity.this.ShareDocuments();
                }
            }
        });
        if (Common.IsImporting) {
            try {
                showIsImportingProgress();
            } catch (Exception unused) {
            }
        } else if (Common.isUnHide) {
            try {
                showUnhideProgress();
            } catch (Exception unused2) {
            }
        } else if (Common.isDelete) {
            try {
                showDeleteProgress();
            } catch (Exception unused3) {
            }
        } else if (Common.isMove) {
            try {
                showMoveProgress();
            } catch (Exception unused4) {
            }
        } else {
            LoadFilesFromDB(this._SortBy);
        }
        if (Common.PhotoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
            Common.PhotoThumbnailCurrentPosition = 0;
        }
    }


    public void SelectOrUnSelectAll() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.documentEntList.size(); i++) {
                ((DocumentsEnt) this.documentEntList.get(i)).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_unselectallicon);
        } else {
            for (int i2 = 0; i2 < this.documentEntList.size(); i2++) {
                ((DocumentsEnt) this.documentEntList.get(i2)).SetFileCheck(true);
            }
            this.IsSelectAll = true;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_selectallicon);
        }
        AppDocumentsAdapter appDocumentsAdapter2 = new AppDocumentsAdapter(this, 1, this.documentEntList, true, _ViewBy);
        this.appDocumentsAdapter = appDocumentsAdapter2;
        this.imagegrid.setAdapter(this.appDocumentsAdapter);
        this.appDocumentsAdapter.notifyDataSetChanged();
    }

    private void SetcheckFlase() {
        for (int i = 0; i < this.documentEntList.size(); i++) {
            ((DocumentsEnt) this.documentEntList.get(i)).SetFileCheck(false);
        }
        AppDocumentsAdapter appDocumentsAdapter2 = new AppDocumentsAdapter(this, 1, this.documentEntList, false, _ViewBy);
        this.appDocumentsAdapter = appDocumentsAdapter2;
        this.imagegrid.setAdapter(this.appDocumentsAdapter);
        this.appDocumentsAdapter.notifyDataSetChanged();
        if (Common.PhotoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
            Common.PhotoThumbnailCurrentPosition = 0;
        }
    }

    public void btnBackonClick(View view) {
        Back();
    }

    public void Back() {
        if (this.isEditMode) {
            SetcheckFlase();
            this.isEditMode = false;
            this.IsSortingDropdown = false;
//            this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
            this.ll_AddPhotos_Bottom_Baar.setVisibility(View.GONE);
            this.ll_EditFiles.setVisibility(View.INVISIBLE);
            this.IsSelectAll = false;
            this.btnSelectAll.setVisibility(View.INVISIBLE);
            this._btnSortingDropdown.setVisibility(View.VISIBLE);
            invalidateOptionsMenu();
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        Common.FolderId = 0;
        Common.DocumentFolderName = StorageOptionsCommon.DOCUMENTS_DEFAULT_ALBUM;
        startActivity(new Intent(this, DocumentsFolderActivity.class));
        finish();
    }

    public void UnhideFiles() {
        if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectphotomsg_unhide, Toast.LENGTH_SHORT).show();
            return;
        }
        SelectedCount();
        if (Common.GetTotalFree() > Common.GetFileSize(this.files)) {
            final Dialog dialog = new Dialog(this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.confirmation_message_box);
            dialog.setCancelable(true);
            LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
            TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
            textView.setTypeface(Typeface.createFromAsset(getAssets(), "ebrima.ttf"));
            StringBuilder sb = new StringBuilder();
            sb.append("Are you sure you want to restore (");
            sb.append(this.selectCount);
            sb.append(") document(s)?");
            textView.setText(sb.toString());
            ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    DocumentsActivity.this.showUnhideProgress();
                    new Thread() {
                        public void run() {
                            try {
                                dialog.dismiss();
                                Common.isUnHide = true;
                                DocumentsActivity.this.Unhide();
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                DocumentsActivity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 3;
                                DocumentsActivity.this.handle.sendMessage(message2);
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
    }


    public void Unhide() throws IOException {
        for (int i = 0; i < this.documentEntList.size(); i++) {
            if (((DocumentsEnt) this.documentEntList.get(i)).GetFileCheck()) {
                if (Utilities.NSUnHideFile(this, ((DocumentsEnt) this.documentEntList.get(i)).getFolderLockDocumentLocation(), ((DocumentsEnt) this.documentEntList.get(i)).getOriginalDocumentLocation())) {
                    DeleteFromDatabase(((DocumentsEnt) this.documentEntList.get(i)).getId());
                } else {
                    Toast.makeText(this, R.string.Unhide_error, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void DeleteFiles() {
        if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectphotomsg_delete, Toast.LENGTH_SHORT).show();
            return;
        }
        SelectedCount();
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.confirmation_message_box);
        dialog.setCancelable(true);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
        textView.setTypeface(Typeface.createFromAsset(getAssets(), "ebrima.ttf"));
        StringBuilder sb = new StringBuilder();
        sb.append("Are you sure you want to delete (");
        sb.append(this.selectCount);
        sb.append(") document(s)?");
        textView.setText(sb.toString());
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DocumentsActivity.this.showDeleteProgress();
                new Thread() {
                    public void run() {
                        try {
                            Common.isDelete = true;
                            dialog.dismiss();
                            DocumentsActivity.this.Delete();
                            Common.IsWorkInProgress = true;
                            Message message = new Message();
                            message.what = 3;
                            DocumentsActivity.this.handle.sendMessage(message);
                            Common.IsWorkInProgress = false;
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 3;
                            DocumentsActivity.this.handle.sendMessage(message2);
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
        for (int i = 0; i < this.documentEntList.size(); i++) {
            if (((DocumentsEnt) this.documentEntList.get(i)).GetFileCheck()) {
                new File(((DocumentsEnt) this.documentEntList.get(i)).getFolderLockDocumentLocation()).delete();
                DeleteFromDatabase(((DocumentsEnt) this.documentEntList.get(i)).getId());
            }
        }
    }

    public void DeleteFromDatabase(int i) {
        DocumentDAL documentDAL2;
        this.documentDAL = new DocumentDAL(this);
        try {
            this.documentDAL.OpenWrite();
            this.documentDAL.DeleteDocumentById(i);
            documentDAL2 = this.documentDAL;
            if (documentDAL2 == null) {
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            documentDAL2 = this.documentDAL;
            if (documentDAL2 == null) {
                return;
            }
        } catch (Throwable th) {
            DocumentDAL documentDAL3 = this.documentDAL;
            if (documentDAL3 != null) {
                documentDAL3.close();
            }
            throw th;
        }
        documentDAL2.close();
    }


    public void SelectedCount() {
        this.files.clear();
        this.selectCount = 0;
        for (int i = 0; i < this.documentEntList.size(); i++) {
            if (((DocumentsEnt) this.documentEntList.get(i)).GetFileCheck()) {
                this.files.add(((DocumentsEnt) this.documentEntList.get(i)).getFolderLockDocumentLocation());
                this.selectCount++;
            }
        }
    }


    public boolean IsFileCheck() {
        for (int i = 0; i < this.documentEntList.size(); i++) {
            if (((DocumentsEnt) this.documentEntList.get(i)).GetFileCheck()) {
                return true;
            }
        }
        return false;
    }

    public void MoveFiles() {
        this.documentDAL = new DocumentDAL(this);
        this.documentDAL.OpenWrite();
        this._folderNameArray = this.documentDAL.GetFolderNames(Common.FolderId);
        if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectdocumentmsg_move, Toast.LENGTH_SHORT).show();
        } else if (this._folderNameArray.length > 0) {
            GetFolderNameFromDB();
        } else {
            Toast.makeText(this, R.string.toast_OneFolder, Toast.LENGTH_SHORT).show();
        }
    }


    public void Move(String str, String str2, String str3) {
        String str4;
        DocumentFolder GetAlbum = GetAlbum(str3);
        for (int i = 0; i < this.documentEntList.size(); i++) {
            if (((DocumentsEnt) this.documentEntList.get(i)).GetFileCheck()) {
                if (((DocumentsEnt) this.documentEntList.get(i)).getDocumentName().contains("#")) {
                    str4 = ((DocumentsEnt) this.documentEntList.get(i)).getDocumentName();
                } else {
                    str4 = Utilities.ChangeFileExtention(((DocumentsEnt) this.documentEntList.get(i)).getDocumentName());
                }
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append("/");
                sb.append(str4);
                String sb2 = sb.toString();
                try {
                    if (Utilities.MoveFileWithinDirectories(((DocumentsEnt) this.documentEntList.get(i)).getFolderLockDocumentLocation(), sb2)) {
                        UpdateFileLocationInDatabase((DocumentsEnt) this.documentEntList.get(i), sb2, GetAlbum.getId());
                        Common.FolderId = GetAlbum.getId();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void UpdateFileLocationInDatabase(DocumentsEnt documentsEnt, String str, int i) {
        DocumentDAL documentDAL2;
        documentsEnt.setFolderLockDocumentLocation(str);
        documentsEnt.setFolderId(i);
        try {
            DocumentDAL documentDAL3 = new DocumentDAL(this);
            documentDAL3.OpenWrite();
            documentDAL3.UpdateDocumentLocation(documentsEnt);
            documentDAL2 = this.documentDAL;
            if (documentDAL2 == null) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            documentDAL2 = this.documentDAL;
            if (documentDAL2 == null) {
                return;
            }
        } catch (Throwable th) {
            DocumentDAL documentDAL4 = this.documentDAL;
            if (documentDAL4 != null) {
                documentDAL4.close();
            }
            throw th;
        }
        documentDAL2.close();
    }



    public DocumentFolder GetAlbum(String str) {
        DocumentFolderDAL documentFolderDAL2;
        this.documentFolderDAL = new DocumentFolderDAL(this);
        try {
            this.documentFolderDAL.OpenRead();
            this.documentFolder = this.documentFolderDAL.GetFolder(str);
            documentFolderDAL2 = this.documentFolderDAL;

            return documentFolder;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            documentFolderDAL2 = this.documentFolderDAL;
        } catch (Throwable th) {
            DocumentFolderDAL documentFolderDAL3 = this.documentFolderDAL;
            if (documentFolderDAL3 != null) {
                documentFolderDAL3.close();
            }
            throw th;
        }
        return null;
    }

    private void GetFolderNameFromDB() {
        DocumentDAL documentDAL2;
        this.documentDAL = new DocumentDAL(this);
        try {
            this.documentDAL.OpenWrite();
            this._folderNameArrayForMove = this.documentDAL.GetMoveFolderNames(Common.FolderId);
            MovePhotoDialog();
            documentDAL2 = this.documentDAL;
            if (documentDAL2 == null) {
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            documentDAL2 = this.documentDAL;
            if (documentDAL2 == null) {
                return;
            }
        } catch (Throwable th) {
            DocumentDAL documentDAL3 = this.documentDAL;
            if (documentDAL3 != null) {
                documentDAL3.close();
            }
            throw th;
        }
        documentDAL2.close();
    }


    public void MovePhotoDialog() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.move_customlistview);
        ListView listView = (ListView) dialog.findViewById(R.id.ListViewfolderslist);
        listView.setAdapter(new MoveAlbumAdapter(this, 17367043, this._folderNameArrayForMove, R.drawable.ic_notesfolder_thumb_icon));
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long j) {
                if (DocumentsActivity.this._folderNameArrayForMove != null) {
                    DocumentsActivity.this.SelectedCount();
                    DocumentsActivity.this.showMoveProgress();
                    new Thread() {
                        public void run() {
                            try {
                                Common.isMove = true;
                                dialog.dismiss();
                                DocumentsActivity documentsActivity = DocumentsActivity.this;
                                StringBuilder sb = new StringBuilder();
                                sb.append(StorageOptionsCommon.STORAGEPATH);
                                sb.append(StorageOptionsCommon.DOCUMENTS);
                                sb.append((String) DocumentsActivity.this._folderNameArrayForMove.get(i));
                                documentsActivity.moveToFolderLocation = sb.toString();
                                DocumentsActivity.this.Move(DocumentsActivity.this.folderLocation, DocumentsActivity.this.moveToFolderLocation, (String) DocumentsActivity.this._folderNameArrayForMove.get(i));
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                DocumentsActivity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 3;
                                DocumentsActivity.this.handle.sendMessage(message2);
                            }
                        }
                    }.start();
                }
            }
        });
        dialog.show();
    }

    public void ShareDocuments() {
        showCopyFilesProcessForShareProgress();
        new Thread() {
            public void run() {
                try {
                    SecurityLocksCommon.IsAppDeactive = false;
                    ArrayList arrayList = new ArrayList();
                    Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
                    intent.setType("image/*");
                    for (ResolveInfo resolveInfo : DocumentsActivity.this.getPackageManager().queryIntentActivities(intent, 0)) {
                        String str = resolveInfo.activityInfo.packageName;
                        if (!str.equals(AppPackageName) && !str.equals("com.dropbox.android") && !str.equals("com.facebook.katana")) {
                            Intent intent2 = new Intent("android.intent.action.SEND_MULTIPLE");
                            intent2.setType("image/*");
                            intent2.setPackage(str);
                            arrayList.add(intent2);
                            StringBuilder sb = new StringBuilder();
                            sb.append(StorageOptionsCommon.STORAGEPATH);
                            sb.append(StorageOptionsCommon.DOCUMENTS);
                            String sb2 = sb.toString();
                            ArrayList arrayList2 = new ArrayList();
                            ArrayList arrayList3 = new ArrayList();
                            String str2 = sb2;
                            for (int i = 0; i < DocumentsActivity.this.documentEntList.size(); i++) {
                                if (((DocumentsEnt) DocumentsActivity.this.documentEntList.get(i)).GetFileCheck()) {
                                    try {
                                        str2 = Utilities.CopyTemporaryFile(DocumentsActivity.this, ((DocumentsEnt) DocumentsActivity.this.documentEntList.get(i)).getFolderLockDocumentLocation(), str2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    arrayList2.add(str2);
                                    arrayList3.add(FileProvider.getUriForFile(DocumentsActivity.this, AppPackageName, new File(str2)));
                                }
                            }
                            intent2.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList3);
                        }
                    }
                    Intent createChooser = Intent.createChooser((Intent) arrayList.remove(0), "Share Via");
                    createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) arrayList.toArray(new Parcelable[0]));
                    DocumentsActivity.this.startActivity(createChooser);
                    Message message = new Message();
                    message.what = 4;
                    DocumentsActivity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 4;
                    DocumentsActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }


    public void LoadFilesFromDB(int i) {
        this.documentEntList = new ArrayList();
        DocumentDAL documentDAL2 = new DocumentDAL(this);
        documentDAL2.OpenRead();
        this.fileCount = documentDAL2.GetDocumentCountByFolderId(Common.FolderId);
        this.documentEntList = documentDAL2.GetDocuments(Common.FolderId, i);
        documentDAL2.close();
        AppDocumentsAdapter appDocumentsAdapter2 = new AppDocumentsAdapter(this, 1, this.documentEntList, false, _ViewBy);
        this.appDocumentsAdapter = appDocumentsAdapter2;
        this.imagegrid.setAdapter(this.appDocumentsAdapter);
        this.appDocumentsAdapter.notifyDataSetChanged();
        if (this.documentEntList.size() < 1) {
            this.ll_file_grid.setVisibility(View.INVISIBLE);
            this.ll_file_empty.setVisibility(View.VISIBLE);
            this.file_empty_icon.setBackgroundResource(R.drawable.ic_documents_empty_icon);
            this.lbl_file_empty.setText(R.string.lbl_No_Documents);
            return;
        }
        this.ll_file_grid.setVisibility(View.VISIBLE);
        this.ll_file_empty.setVisibility(View.INVISIBLE);
    }

    public void btnSortonClick(View view) {
        this.IsSortingDropdown = false;
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
        arrayList2.add("Detail");
        hashMap.put(arrayList.get(0), arrayList2);
        arrayList.add("Sort by");
        arrayList3.add("Name");
        arrayList3.add("Date");
        arrayList3.add("Size");
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
                                DocumentsActivity.this._SortBy = SortBy.Name.ordinal();
                                DocumentsActivity documentsActivity = DocumentsActivity.this;
                                documentsActivity.LoadFilesFromDB(documentsActivity._SortBy);
                                DocumentsActivity.this.AddSortInDB();
                                popupWindow.dismiss();
                                DocumentsActivity.this.IsSortingDropdown = false;
                                break;
                            case 1:
                                DocumentsActivity.this._SortBy = SortBy.Time.ordinal();
                                DocumentsActivity documentsActivity2 = DocumentsActivity.this;
                                documentsActivity2.LoadFilesFromDB(documentsActivity2._SortBy);
                                DocumentsActivity.this.AddSortInDB();
                                popupWindow.dismiss();
                                DocumentsActivity.this.IsSortingDropdown = false;
                                break;
                            case 2:
                                DocumentsActivity.this._SortBy = SortBy.Size.ordinal();
                                DocumentsActivity documentsActivity3 = DocumentsActivity.this;
                                documentsActivity3.LoadFilesFromDB(documentsActivity3._SortBy);
                                DocumentsActivity.this.AddSortInDB();
                                popupWindow.dismiss();
                                DocumentsActivity.this.IsSortingDropdown = false;
                                break;
                        }
                    }
                } else {
                    switch (i2) {
                        case 0:
                            DocumentsActivity._ViewBy = ViewBy.List.ordinal();
                            DocumentsActivity.this.ViewBy();
                            popupWindow.dismiss();
                            DocumentsActivity.this.IsSortingDropdown = false;
                            break;
                        case 1:
                            DocumentsActivity._ViewBy = ViewBy.Detail.ordinal();
                            DocumentsActivity.this.ViewBy();
                            popupWindow.dismiss();
                            DocumentsActivity.this.IsSortingDropdown = false;
                            break;
                    }
                }
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


    public void AddSortInDB() {
        DocumentFolderDAL documentFolderDAL2 = new DocumentFolderDAL(this);
        documentFolderDAL2.OpenWrite();
        documentFolderDAL2.AddSortByInDocumentFolder(this._SortBy);
        documentFolderDAL2.close();
    }


    public void CopyTempFile(String str) {
        File file = new File(str);
        try {
            Utilities.NSDecryption(file);
            SecurityLocksCommon.IsAppDeactive = false;
            String guessContentTypeFromName = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(FileProvider.getUriForFile(this, AppPackageName, file), guessContentTypeFromName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
           // Log.e(HitTypes.EXCEPTION, e.toString());
        }
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

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    public void ViewBy() {
        AppDocumentsAdapter appDocumentsAdapter2 = new AppDocumentsAdapter(this, 1, this.documentEntList, false, _ViewBy);
        this.appDocumentsAdapter = appDocumentsAdapter2;
        this.imagegrid.setAdapter(this.appDocumentsAdapter);
        this.appDocumentsAdapter.notifyDataSetChanged();
    }


    public void onPause() {
        Common.IsWorkInProgress = true;
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
        this.handle.removeCallbacksAndMessages(null);
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


    public void onResume() {
        new Thread() {
            public void run() {
                try {
                    Utilities.changeFileExtention(StorageOptionsCommon.DOCUMENTS);
                } catch (Exception unused) {
                    Log.v("Login Activity", "error in changeVideosExtention method");
                }
            }
        }.start();
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
        SetcheckFlase();
        this.IsSortingDropdown = false;
        this.isEditMode = false;
//        this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_AddPhotos_Bottom_Baar.setVisibility(View.GONE);
        this.ll_EditFiles.setVisibility(View.INVISIBLE);
        this.IsSelectAll = false;
        this.btnSelectAll.setVisibility(View.INVISIBLE);
        this._btnSortingDropdown.setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
        super.onResume();
    }


    public void onStop() {
        super.onStop();
    }


    public void onDestroy() {
        super.onDestroy();
        new Thread() {
            public void run() {
                try {
                    Utilities.changeFileExtention(StorageOptionsCommon.DOCUMENTS);
                } catch (Exception unused) {
                    Log.v("Login Activity", "error in changeVideosExtention method");
                }
            }
        }.start();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            Common.isOpenCameraorGalleryFromApp = false;
            if (this.isEditMode) {
                SetcheckFlase();
                this.IsSortingDropdown = false;
                this.isEditMode = false;
//                this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
                this.ll_AddPhotos_Bottom_Baar.setVisibility(View.GONE);
                this.ll_EditFiles.setVisibility(View.INVISIBLE);
                this.IsSelectAll = false;
                this.btnSelectAll.setVisibility(View.INVISIBLE);
                this._btnSortingDropdown.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            Common.FolderId = 0;
            Common.DocumentFolderName = StorageOptionsCommon.DOCUMENTS_DEFAULT_ALBUM;
            startActivity(new Intent(this, DocumentsFolderActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.action_more) {
            this.IsSortingDropdown = false;
            showPopupWindow();
            return true;
        } else if (itemId != R.id.action_select) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            if (this.IsSelectAll) {
                for (int i = 0; i < this.documentEntList.size(); i++) {
                    ((DocumentsEnt) this.documentEntList.get(i)).SetFileCheck(false);
                }
                this.IsSelectAll = false;
                menuItem.setIcon(R.drawable.ic_unselectallicon);
                invalidateOptionsMenu();
            } else {
                for (int i2 = 0; i2 < this.documentEntList.size(); i2++) {
                    ((DocumentsEnt) this.documentEntList.get(i2)).SetFileCheck(true);
                }
                this.IsSelectAll = true;
                menuItem.setIcon(R.drawable.ic_selectallicon);
            }
            AppDocumentsAdapter appDocumentsAdapter2 = new AppDocumentsAdapter(this, 1, this.documentEntList, true, _ViewBy);
            this.appDocumentsAdapter = appDocumentsAdapter2;
            this.imagegrid.setAdapter(this.appDocumentsAdapter);
            this.appDocumentsAdapter.notifyDataSetChanged();
            return true;
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.isEditMode) {
            menu.findItem(R.id.action_more).setVisible(false);
            getMenuInflater().inflate(R.menu.menu_selection, menu);
        } else {
            menu.findItem(R.id.action_more).setVisible(true);
            if (this.IsSelectAll && this.isEditMode) {
                menu.findItem(R.id.action_select).setIcon(R.drawable.ic_unselectallicon);
            } else if (!this.IsSelectAll && this.isEditMode) {
                menu.findItem(R.id.action_select).setIcon(R.drawable.ic_selectallicon);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
