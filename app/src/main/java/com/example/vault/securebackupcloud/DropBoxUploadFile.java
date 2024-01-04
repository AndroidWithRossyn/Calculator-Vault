package com.example.vault.securebackupcloud;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import com.example.vault.dropbox.service.CloudService;
import com.example.vault.securebackupcloud.CloudCommon.DropboxType;
import com.example.vault.utilities.Utilities;

public class DropBoxUploadFile extends AsyncTask<Void, Long, Boolean> {
    private BackupCloudEnt backupCloudEnt;
    private int downloadType;
    private String dropboxUploadPath;
    private DbxClientV2 mApi;
    private Context mContext;
    private String mErrorMsg;
    private File uploadfile;


    public void onProgressUpdate(Long... lArr) {
    }

    public DropBoxUploadFile(Context context, DbxClientV2 dbxClientV2, String str, String str2, int i, BackupCloudEnt backupCloudEnt2) {
        this.mContext = context.getApplicationContext();
        this.mApi = dbxClientV2;
        this.dropboxUploadPath = str;
        this.uploadfile = new File(str2);
        this.downloadType = i;
        this.backupCloudEnt = backupCloudEnt2;
    }


    public Boolean doInBackground(Void... voidArr) {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(this.uploadfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileInputStream = null;
        }
        String str = "";
        try {
            if (!(this.downloadType == DropboxType.Notes.ordinal() || this.downloadType == DropboxType.ToDo.ordinal() || this.downloadType == DropboxType.Wallet.ordinal())) {
                Utilities.NSDecryption(this.uploadfile);
            }
            if (this.uploadfile.getName().contains("#")) {
                str = Utilities.ChangeFileExtentionToOrignal(this.uploadfile.getName());
            } else {
                str = this.uploadfile.getName();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.dropboxUploadPath);
        sb.append("/");
        sb.append(str);
        try {
            this.mApi.files().uploadBuilder(sb.toString()).uploadAndFinish(fileInputStream);
            return Boolean.valueOf(true);
        } catch (DbxException e3) {
            e3.printStackTrace();
            return Boolean.valueOf(false);
        } catch (IOException e4) {
            e4.printStackTrace();
            return Boolean.valueOf(false);
        }
    }


    public void onPostExecute(Boolean bool) {
        try {
            if (bool.booleanValue()) {
                int indexOf = CloudService.CloudBackupCloudEntList.indexOf(this.backupCloudEnt);
                Hashtable GetUploadPath = this.backupCloudEnt.GetUploadPath();
                if (GetUploadPath.containsKey(this.uploadfile.getAbsolutePath())) {
                    GetUploadPath.put(this.uploadfile.getAbsolutePath().toString(), Boolean.valueOf(true));
                    this.backupCloudEnt.SetUploadPath(GetUploadPath);
                    CloudService.CloudBackupCloudEntList.set(indexOf, this.backupCloudEnt);
                }
                if (this.downloadType != DropboxType.Notes.ordinal() && this.downloadType != DropboxType.Wallet.ordinal() && this.downloadType != DropboxType.ToDo.ordinal()) {
                    try {
                        Utilities.NSEncryption(this.uploadfile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                showToast(this.mErrorMsg);
                if (this.downloadType != DropboxType.Notes.ordinal() && this.downloadType != DropboxType.Wallet.ordinal() && this.downloadType != DropboxType.ToDo.ordinal()) {
                    try {
                        Utilities.NSEncryption(this.uploadfile);
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        } catch (Exception unused) {
        }
    }

    private void showToast(String str) {
        Toast.makeText(this.mContext, str, Toast.LENGTH_LONG).show();
    }
}
