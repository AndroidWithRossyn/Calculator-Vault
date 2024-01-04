package com.example.vault.documents;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.widget.AppCompatImageView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.vault.R;
import com.example.vault.common.Constants;
import com.example.vault.documents.adapter.DocumentSystemFileAdapter;
import com.example.vault.documents.adapter.FoldersImportAdapter;
import com.example.vault.documents.model.DocumentFolder;
import com.example.vault.documents.model.DocumentsEnt;
import com.example.vault.documents.model.ImportAlbumEnt;
import com.example.vault.documents.util.DocumentDAL;
import com.example.vault.documents.util.DocumentFolderDAL;
import com.example.vault.panicswitch.AccelerometerListener;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.FileUtils;
import com.example.vault.utilities.Utilities;

public class DocumentsImportActivity extends Activity implements AccelerometerListener, SensorEventListener {

    public boolean IsExceptionInImportPhotos = false;
    private boolean IsSelectAll = false;
    public ProgressBar Progress;

    public FoldersImportAdapter adapter;
    ListView album_import_ListView;
    AppCompatImageView btnSelectAll;
    Context context = this;
    private int count;

    public ArrayList<DocumentsEnt> fileImportEntList = new ArrayList<>();

    public ArrayList<DocumentsEnt> fileImportEntListShow = new ArrayList<>();
    List<List<DocumentsEnt>> fileImportEntListShowList = new ArrayList();

    public DocumentSystemFileAdapter filesAdapter;
    int folderId;
    String folderName;
    String folderPath;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            Intent intent;
            if (message.what == 1) {
                DocumentsImportActivity.this.hideProgress();
                if (DocumentsImportActivity.this.fileImportEntListShow.size() > 0) {
                    DocumentsImportActivity documentsImportActivity = DocumentsImportActivity.this;
                    documentsImportActivity.filesAdapter = new DocumentSystemFileAdapter(documentsImportActivity, 1, documentsImportActivity.fileImportEntListShow);
                    DocumentsImportActivity.this.imagegrid.setAdapter(DocumentsImportActivity.this.filesAdapter);
                } else {
                    DocumentsImportActivity.this.btnSelectAll.setEnabled(false);
                    DocumentsImportActivity.this.ll_Import_bottom_baar.setEnabled(false);
                }
            } else if (message.what == 3) {
                if (Common.IsImporting) {
                    if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        DocumentsImportActivity documentsImportActivity2 = DocumentsImportActivity.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append(Constants.FILE);
                        sb.append(Environment.getExternalStorageDirectory());
                        documentsImportActivity2.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb.toString())));
                    } else {
                        DocumentsImportActivity.this.RefershGalleryforKitkat();
                    }
                    Common.IsImporting = false;
                    if (DocumentsImportActivity.this.IsExceptionInImportPhotos) {
                        DocumentsImportActivity.this.IsExceptionInImportPhotos = false;
                    } else {
                        DocumentsImportActivity documentsImportActivity3 = DocumentsImportActivity.this;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(DocumentsImportActivity.this.selectCount);
                        sb2.append(" document(s) imported successfully");
                        Toast.makeText(documentsImportActivity3, sb2.toString(), Toast.LENGTH_SHORT).show();
                    }
                    DocumentsImportActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        if (DocumentsImportActivity.this.isAlbumClick) {
                            intent = new Intent(DocumentsImportActivity.this, DocumentsActivity.class);
                        } else {
                            intent = new Intent(DocumentsImportActivity.this, DocumentsFolderActivity.class);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        DocumentsImportActivity.this.startActivity(intent);
                        DocumentsImportActivity.this.finish();
                    }
                }
            } else if (message.what == 2) {
                DocumentsImportActivity.this.hideProgress();
            }
            super.handleMessage(message);
        }
    };
    GridView imagegrid;
    List<ImportAlbumEnt> importAlbumEnts = new ArrayList();
    boolean isAlbumClick = false;
    TextView lbl_import_photo_album_topbaar;
    TextView lbl_photo_video_empty;
    LinearLayout ll_Import_bottom_baar;
    LinearLayout ll_background;
    LinearLayout ll_photo_video_empty;
    LinearLayout ll_topbaar;
    ProgressDialog myProgressDialog = null;
    ImageView photo_video_empty_icon;
    int selectCount;
    private ArrayList<String> selectPath = new ArrayList<>();
    private SensorManager sensorManager;
    ArrayList<String> spinnerValues = new ArrayList<>();

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public void RefershGalleryforKitkat() {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.FILE);
        sb.append(Environment.getExternalStorageDirectory());
        Uri contentUri = Uri.fromFile(new File(sb.toString()));
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private void ShowImportProgress() {
        this.myProgressDialog = ProgressDialog.show(this, null, "Your data is being encrypted... this may take a few moments... ", true);
    }


    public void hideProgress() {
        ProgressDialog progressDialog = this.myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            this.myProgressDialog.dismiss();
        }
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.import_album_list_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        Common.IsWorkInProgress = false;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.album_import_ListView = (ListView) findViewById(R.id.album_import_ListView);
        this.imagegrid = (GridView) findViewById(R.id.customGalleryGrid);
        this.lbl_import_photo_album_topbaar = (TextView) findViewById(R.id.lbl_import_photo_album_topbaar);
        this.lbl_import_photo_album_topbaar.setText(R.string.lbl_import_photo_album_select_folder_topbaar);
        this.btnSelectAll = (AppCompatImageView) findViewById(R.id.btnSelectAll);
        this.ll_Import_bottom_baar = (LinearLayout) findViewById(R.id.ll_Import_bottom_baar);
        this.ll_photo_video_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.photo_video_empty_icon = (ImageView) findViewById(R.id.photo_video_empty_icon);
        this.lbl_photo_video_empty = (TextView) findViewById(R.id.lbl_photo_video_empty);
        this.folderId = Common.FolderId;
        this.folderName = null;
        if (this.folderName == null) {
            this.folderId = Common.FolderId;
            DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
            documentFolderDAL.OpenRead();
            DocumentFolder GetFolderById = documentFolderDAL.GetFolderById(Integer.toString(Common.FolderId));
            documentFolderDAL.close();
            this.folderName = GetFolderById.getFolderName();


            Log.e("foldername", "" + folderName);
        }
        DocumentFileBind();
        GetFolders();
        this.Progress.setVisibility(View.GONE);
        Iterator it = this.fileImportEntList.iterator();
        while (it.hasNext()) {
            DocumentsEnt documentsEnt = (DocumentsEnt) it.next();
            if (((String) this.spinnerValues.get(0)).contains(new File(documentsEnt.getOriginalDocumentLocation()).getParent())) {
                this.fileImportEntListShow.add(documentsEnt);
            }
        }
        this.ll_Import_bottom_baar.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DocumentsImportActivity.this.OnImportClick();
            }
        });
        this.album_import_ListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                DocumentsImportActivity documentsImportActivity = DocumentsImportActivity.this;
                documentsImportActivity.isAlbumClick = true;
                documentsImportActivity.lbl_import_photo_album_topbaar.setText(R.string.lbl_import_doc_album_select_doc_topbaar);
                DocumentsImportActivity.this.album_import_ListView.setVisibility(View.INVISIBLE);
                DocumentsImportActivity.this.imagegrid.setVisibility(View.VISIBLE);
                DocumentsImportActivity.this.btnSelectAll.setVisibility(View.VISIBLE);
                DocumentsImportActivity documentsImportActivity2 = DocumentsImportActivity.this;
                documentsImportActivity2.adapter = new FoldersImportAdapter(documentsImportActivity2.context, 17367043, DocumentsImportActivity.this.importAlbumEnts, false);
                DocumentsImportActivity.this.album_import_ListView.setAdapter(DocumentsImportActivity.this.adapter);
                DocumentsImportActivity.this.fileImportEntListShow.clear();
                Iterator it = DocumentsImportActivity.this.fileImportEntList.iterator();
                while (it.hasNext()) {
                    DocumentsEnt documentsEnt = (DocumentsEnt) it.next();
                    if (((String) DocumentsImportActivity.this.spinnerValues.get(i)).equals(new File(documentsEnt.getOriginalDocumentLocation()).getParent())) {
                        documentsEnt.GetFileCheck();
                        DocumentsImportActivity.this.fileImportEntListShow.add(documentsEnt);
                    }
                }
                DocumentsImportActivity documentsImportActivity3 = DocumentsImportActivity.this;
                documentsImportActivity3.filesAdapter = new DocumentSystemFileAdapter(documentsImportActivity3.context, 1, DocumentsImportActivity.this.fileImportEntListShow);
                DocumentsImportActivity.this.imagegrid.setAdapter(DocumentsImportActivity.this.filesAdapter);
                DocumentsImportActivity.this.filesAdapter.notifyDataSetChanged();
                if (DocumentsImportActivity.this.fileImportEntListShow.size() <= 0) {
                    DocumentsImportActivity.this.album_import_ListView.setVisibility(View.INVISIBLE);
                    DocumentsImportActivity.this.imagegrid.setVisibility(View.INVISIBLE);
                    DocumentsImportActivity.this.btnSelectAll.setVisibility(View.INVISIBLE);
                    DocumentsImportActivity.this.ll_photo_video_empty.setVisibility(View.VISIBLE);
                    DocumentsImportActivity.this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_video_empty_icon);
                    DocumentsImportActivity.this.lbl_photo_video_empty.setText(R.string.no_docs);
                }
            }
        });
        this.filesAdapter = new DocumentSystemFileAdapter(this.context, 1, this.fileImportEntListShow);
        this.imagegrid.setAdapter(this.filesAdapter);
    }

    private void DocumentFileBind() {
        Iterator it = new FileUtils().FindFiles(new String[]{"doc", "pdf", "txt", "xlsx", "docx", "ppt", "pptx", "xls", "csv", "dbk", "dot", "dotx", "gdoc", "pdax", "pda", "rtf", "rpt", "uoml", "uof", "stw", "xps", "wrd", "wpt", "wps", "epub"}).iterator();
        while (it.hasNext()) {
            File file = (File) it.next();
            DocumentsEnt documentsEnt = new DocumentsEnt();
            documentsEnt.SetFile(file);
            documentsEnt.setDocumentName(file.getName());
            documentsEnt.setOriginalDocumentLocation(file.getAbsolutePath());
            documentsEnt.SetFileCheck(false);
            this.fileImportEntList.add(documentsEnt);
            ImportAlbumEnt importAlbumEnt = new ImportAlbumEnt();
            if (this.spinnerValues.size() <= 0 || !this.spinnerValues.contains(file.getParent())) {
                importAlbumEnt.SetAlbumName(file.getParent());
                this.importAlbumEnts.add(importAlbumEnt);
                this.spinnerValues.add(file.getParent());
            }
        }
        if (this.fileImportEntList.size() <= 0) {
            this.btnSelectAll.setEnabled(false);
            this.ll_Import_bottom_baar.setEnabled(false);
        }
    }

    public void GetFolders() {
        this.adapter = new FoldersImportAdapter(this.context, 17367043, this.importAlbumEnts, false);
        this.album_import_ListView.setAdapter(this.adapter);
        if (this.importAlbumEnts.size() <= 0) {
            this.album_import_ListView.setVisibility(View.INVISIBLE);
            this.imagegrid.setVisibility(View.INVISIBLE);
            this.btnSelectAll.setVisibility(View.INVISIBLE);
            this.ll_photo_video_empty.setVisibility(View.VISIBLE);
            this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_video_empty_icon);
            this.lbl_photo_video_empty.setText(R.string.no_docs);
        }
    }


    public int albumCheckCount() {
        int i = 0;
        for (int i2 = 0; i2 < this.importAlbumEnts.size(); i2++) {
            if (((ImportAlbumEnt) this.importAlbumEnts.get(i2)).GetAlbumFileCheck()) {
                i++;
            }
        }
        return i;
    }

    public void OnImportClick() {
        final StorageOptionSharedPreferences GetObject = StorageOptionSharedPreferences.GetObject(this);
        if (!IsFileCheck()) {
            Toast.makeText(this, R.string.toast_unselectdocmsg_import, Toast.LENGTH_SHORT).show();
        } else if (Common.GetFileSize(this.selectPath) < Common.GetTotalFree()) {
            int albumCheckCount = albumCheckCount();
            if (albumCheckCount >= 2) {
                final Dialog dialog = new Dialog(this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.confirmation_message_box);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
                textView.setTypeface(Typeface.createFromAsset(getAssets(), "ebrima.ttf"));
                StringBuilder sb = new StringBuilder();
                sb.append("Are you sure you want to import ");
                sb.append(albumCheckCount);
                sb.append(" folders? Importing may take time according to the size of your data.");
                textView.setText(sb.toString());
                ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (VERSION.SDK_INT < 21 || VERSION.SDK_INT >= 23) {
                            DocumentsImportActivity.this.Import();
                        } else if (GetObject.GetSDCardUri().length() > 0) {
                            DocumentsImportActivity.this.Import();
                        } else if (!GetObject.GetISDAlertshow()) {
                            final Dialog dialog = new Dialog(DocumentsImportActivity.this, R.style.FullHeightDialog);
                            dialog.setContentView(R.layout.sdcard_permission_alert_msgbox);
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            ((TextView) dialog.findViewById(R.id.tvmessagedialogtitle)).setText(R.string.lblSdCardAlertMsgDocs);
                            ((CheckBox) dialog.findViewById(R.id.cbalertdialog)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
                                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                                    GetObject.SetISDAlertshow(Boolean.valueOf(z));
                                }
                            });
                            ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    DocumentsImportActivity.this.Import();
                                }
                            });
                            dialog.show();
                        } else {
                            DocumentsImportActivity.this.Import();
                        }
                    }
                });
                ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        for (int i = 0; i < DocumentsImportActivity.this.importAlbumEnts.size(); i++) {
                            ((ImportAlbumEnt) DocumentsImportActivity.this.importAlbumEnts.get(i)).SetAlbumFileCheck(false);
                        }
                        DocumentsImportActivity documentsImportActivity = DocumentsImportActivity.this;
                        documentsImportActivity.adapter = new FoldersImportAdapter(documentsImportActivity.context, 17367043, DocumentsImportActivity.this.importAlbumEnts, false);
                        DocumentsImportActivity.this.album_import_ListView.setAdapter(DocumentsImportActivity.this.adapter);
                        DocumentsImportActivity.this.adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            } else if (VERSION.SDK_INT < 21 || VERSION.SDK_INT >= 23) {
                Import();
            } else if (GetObject.GetSDCardUri().length() > 0) {
                Import();
            } else if (!GetObject.GetISDAlertshow()) {
                final Dialog dialog2 = new Dialog(this, R.style.FullHeightDialog);
                dialog2.setContentView(R.layout.sdcard_permission_alert_msgbox);
                dialog2.setCancelable(false);
                dialog2.setCanceledOnTouchOutside(false);
                ((TextView) dialog2.findViewById(R.id.tvmessagedialogtitle)).setText(R.string.lblSdCardAlertMsgDocs);
                ((CheckBox) dialog2.findViewById(R.id.cbalertdialog)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        GetObject.SetISDAlertshow(Boolean.valueOf(z));
                    }
                });
                ((LinearLayout) dialog2.findViewById(R.id.ll_Ok)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        dialog2.dismiss();
                        DocumentsImportActivity.this.Import();
                    }
                });
                ((LinearLayout) dialog2.findViewById(R.id.ll_Cancel)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        dialog2.dismiss();
                    }
                });
                dialog2.show();
            } else {
                Import();
            }
        }
    }


    public void Import() {
        SelectedCount();
        ShowImportProgress();
        Common.IsWorkInProgress = true;
        new Thread() {
            public void run() {
                try {
                    DocumentsImportActivity.this.ImportDocuments();
                    Message message = new Message();
                    message.what = 3;
                    DocumentsImportActivity.this.handle.sendMessage(message);
                    Common.IsWorkInProgress = false;
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 3;
                    DocumentsImportActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }

    public void ImportDocuments() {
        if (this.isAlbumClick) {
            ImportOnlyDocumentsSDCard();
        } else {
            importFolder();
        }
    }


    public void importFolder() {
        if (this.importAlbumEnts.size() > 0) {
            int i = 0;
            for (int i2 = 0; i2 < this.importAlbumEnts.size(); i2++) {
                if (((ImportAlbumEnt) this.importAlbumEnts.get(i2)).GetAlbumFileCheck()) {
                    File file = new File(((ImportAlbumEnt) this.importAlbumEnts.get(i2)).GetAlbumName());
                    StringBuilder sb = new StringBuilder();
                    sb.append(StorageOptionsCommon.STORAGEPATH);
                    sb.append(StorageOptionsCommon.DOCUMENTS);
                    sb.append(file.getName());
                    File file2 = new File(sb.toString());
                    this.folderName = file.getName();
                    if (file2.exists()) {
                        File file3 = file2;
                        int i3 = 1;
                        while (i3 < 100) {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append(file.getName());
                            sb2.append("(");
                            sb2.append(i3);
                            sb2.append(")");
                            this.folderName = sb2.toString();
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append(StorageOptionsCommon.STORAGEPATH);
                            sb3.append(StorageOptionsCommon.DOCUMENTS);
                            sb3.append(this.folderName);
                            file3 = new File(sb3.toString());
                            if (!file3.exists()) {
                                i3 = 100;
                            }
                            i3++;
                        }
                        file2 = file3;
                    }
                    AddFolderToDatabase(this.folderName);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this);
                    documentFolderDAL.OpenRead();
                    this.folderId = documentFolderDAL.GetLastFolderId();
                    Common.FolderId = this.folderId;
                    documentFolderDAL.close();
                    ImportAlbumsDocumentsSDCard(i);
                    i++;
                }
            }
        }
    }


    public void ImportAlbumsDocumentsSDCard(int i) {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        List list = (List) this.fileImportEntListShowList.get(i);
        for (int i2 = 0; i2 < list.size(); i2++) {
            if (((DocumentsEnt) list.get(i2)).GetFileCheck()) {
                File file = new File(((DocumentsEnt) list.get(i2)).getOriginalDocumentLocation());
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append(StorageOptionsCommon.DOCUMENTS);
                sb.append(this.folderName);
                sb.append("/");
                try {
                    String NSHideFile = Utilities.NSHideFile(this, file, new File(sb.toString()));
                    Utilities.NSEncryption(new File(NSHideFile));
                    if (NSHideFile.length() > 0) {
                        AddDocumentToDatabase(FileName(((DocumentsEnt) list.get(i2)).getOriginalDocumentLocation()), ((DocumentsEnt) list.get(i2)).getOriginalDocumentLocation(), NSHideFile);
                    }
                    if (VERSION.SDK_INT >= 21 && VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                        Utilities.DeleteSDcardImageLollipop(this, file.getAbsolutePath());
                    }
                } catch (IOException e) {
                    this.IsExceptionInImportPhotos = true;
                    e.printStackTrace();
                }
            }
        }
    }


    public void ImportOnlyDocumentsSDCard() {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        int size = this.fileImportEntListShow.size();
        for (int i = 0; i < size; i++) {
            if (((DocumentsEnt) this.fileImportEntListShow.get(i)).GetFileCheck()) {
                File file = new File(((DocumentsEnt) this.fileImportEntListShow.get(i)).getOriginalDocumentLocation());
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append(StorageOptionsCommon.DOCUMENTS);
                sb.append(this.folderName);
                sb.append("/");
                try {
                    String NSHideFile = Utilities.NSHideFile(this, file, new File(sb.toString()));
                    Utilities.NSEncryption(new File(NSHideFile));
                    if (NSHideFile.length() > 0) {
                        AddDocumentToDatabase(FileName(((DocumentsEnt) this.fileImportEntListShow.get(i)).getOriginalDocumentLocation()), ((DocumentsEnt) this.fileImportEntListShow.get(i)).getOriginalDocumentLocation(), NSHideFile);
                    }
                    if (VERSION.SDK_INT >= 21 && VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                        Utilities.DeleteSDcardImageLollipop(this, file.getAbsolutePath());
                    }
                } catch (IOException e) {
                    this.IsExceptionInImportPhotos = true;
                    e.printStackTrace();
                }
            }
        }
    }


    public void AddDocumentToDatabase(String str, String str2, String str3) {
        DocumentsEnt documentsEnt = new DocumentsEnt();
        documentsEnt.setDocumentName(str);
        documentsEnt.setFolderLockDocumentLocation(str3);
        documentsEnt.setOriginalDocumentLocation(str2);
        documentsEnt.setFolderId(this.folderId);
        DocumentDAL documentDAL = new DocumentDAL(this.context);
        try {
            documentDAL.OpenWrite();
            documentDAL.AddDocuments(documentsEnt, str3);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            documentDAL.close();
            throw th;
        }
        documentDAL.close();
    }

    public void AddFolderToDatabase(String str) {
        DocumentFolder documentFolder = new DocumentFolder();
        documentFolder.setFolderName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.DOCUMENTS);
        sb.append(str);
        documentFolder.setFolderLocation(sb.toString());
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this);
        try {
            documentFolderDAL.OpenWrite();
            documentFolderDAL.AddDocumentFolder(documentFolder);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            documentFolderDAL.close();
            throw th;
        }
        documentFolderDAL.close();
    }

    private boolean IsFileCheck() {
        for (int i = 0; i < this.importAlbumEnts.size(); i++) {
            if (((ImportAlbumEnt) this.importAlbumEnts.get(i)).GetAlbumFileCheck()) {
                this.fileImportEntListShow = new ArrayList<>();
                Iterator it = this.fileImportEntList.iterator();
                while (it.hasNext()) {
                    DocumentsEnt documentsEnt = (DocumentsEnt) it.next();
                    if (((String) this.spinnerValues.get(i)).equals(new File(documentsEnt.getOriginalDocumentLocation()).getParent())) {
                        this.fileImportEntListShow.add(documentsEnt);
                    }
                    for (int i2 = 0; i2 < this.fileImportEntListShow.size(); i2++) {
                        ((DocumentsEnt) this.fileImportEntListShow.get(i2)).SetFileCheck(true);
                    }
                }
                this.fileImportEntListShowList.add(this.fileImportEntListShow);
            }
        }
        this.selectPath.clear();
        for (int i3 = 0; i3 < this.fileImportEntListShow.size(); i3++) {
            if (((DocumentsEnt) this.fileImportEntListShow.get(i3)).GetFileCheck()) {
                this.selectPath.add(((DocumentsEnt) this.fileImportEntListShow.get(i3)).getOriginalDocumentLocation());
                return true;
            }
        }
        return false;
    }

    private void SelectedCount() {
        this.selectCount = 0;
        for (int i = 0; i < this.fileImportEntListShow.size(); i++) {
            if (((DocumentsEnt) this.fileImportEntListShow.get(i)).GetFileCheck()) {
                this.selectCount++;
            }
        }
    }

    public void btnSelectAllonClick(View view) {
        SelectAllPhotos();
        this.filesAdapter = new DocumentSystemFileAdapter(this.context, 1, this.fileImportEntListShow);
        this.imagegrid.setAdapter(this.filesAdapter);
        this.filesAdapter.notifyDataSetChanged();
    }

    public void btnBackonClick(View view) {
        Back();
    }

    private void SelectAllPhotos() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.fileImportEntListShow.size(); i++) {
                ((DocumentsEnt) this.fileImportEntListShow.get(i)).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setImageResource(R.drawable.ic_unselectallicon);
            return;
        }
        for (int i2 = 0; i2 < this.fileImportEntListShow.size(); i2++) {
            ((DocumentsEnt) this.fileImportEntListShow.get(i2)).SetFileCheck(true);
        }
        this.IsSelectAll = true;
        this.btnSelectAll.setImageResource(R.drawable.ic_selectallicon);
    }

    public String FileName(String str) {
        String str2 = " /";
        for (int length = str.length() - 1; length > 0; length--) {
            if (str.charAt(length) == str2.charAt(1)) {
                return str.substring(length + 1, str.length());
            }
        }
        return "";
    }

    public void Back() {
        if (this.isAlbumClick) {
            this.isAlbumClick = false;
            this.lbl_import_photo_album_topbaar.setText("Import Folders");
            this.album_import_ListView.setVisibility(View.VISIBLE);
            this.imagegrid.setVisibility(View.INVISIBLE);
            this.btnSelectAll.setVisibility(View.INVISIBLE);
            for (int i = 0; i < this.fileImportEntListShow.size(); i++) {
                ((DocumentsEnt) this.fileImportEntListShow.get(i)).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, DocumentsActivity.class));
        finish();
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


    public void onPause() {
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive && !Common.IsWorkInProgress) {
            finish();
            System.exit(0);
        }
        super.onPause();
    }


    public void onResume() {
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            if (this.isAlbumClick) {
                this.isAlbumClick = false;
                this.lbl_import_photo_album_topbaar.setText("Import Albums");
                this.album_import_ListView.setVisibility(View.VISIBLE);
                this.imagegrid.setVisibility(View.INVISIBLE);
                this.btnSelectAll.setVisibility(View.INVISIBLE);
                for (int i2 = 0; i2 < this.fileImportEntListShow.size(); i2++) {
                    ((DocumentsEnt) this.fileImportEntListShow.get(i2)).SetFileCheck(false);
                }
                this.IsSelectAll = false;
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, DocumentsActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
