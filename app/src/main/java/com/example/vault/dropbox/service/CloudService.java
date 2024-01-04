package com.example.vault.dropbox.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import com.example.vault.R;
import com.example.vault.securebackupcloud.BackupCloudEnt;
import com.example.vault.securebackupcloud.CloudCommon;
import com.example.vault.securebackupcloud.CloudCommon.CloudFolderStatus;
import com.example.vault.securebackupcloud.DropboxCloud;

public class CloudService extends IntentService {
    public static ArrayList<BackupCloudEnt> AddBackupCloudEntList = new ArrayList<>();
    public static ArrayList<BackupCloudEnt> CloudBackupCloudEntList = new ArrayList<>();
    public static boolean IsRemovingIndex = false;
    public static ArrayList<Integer> RemoveUpdateBackupIndexs = new ArrayList<>();
    public static ArrayList<BackupCloudEnt> UpdateBackupCloudEntList = new ArrayList<>();
    Context context;
    public volatile boolean run = true;

    public CloudService() {
        super("CloudService");
    }

    public CloudService(String str) {
        super(str);
    }


    public void onHandleIntent(Intent intent) {
        Log.i(NotificationCompat.CATEGORY_SERVICE, "service running");
        while (this.run) {
            synchronized (this) {
                try {
                    CloudCommon.IsCloudServiceStarted = true;
                    if (AddBackupCloudEntList.size() > 0) {
                        BackupCloudEnt backupCloudEnt = (BackupCloudEnt) AddBackupCloudEntList.remove(0);
                        backupCloudEnt.SetIsInProgress(true);
                        CloudBackupCloudEntList.add(backupCloudEnt);
                        UpdateBackupCloudEntList.add(backupCloudEnt);
                        DropboxCloud dropboxCloud = new DropboxCloud(this, backupCloudEnt.GetDropboxType());
                        if (backupCloudEnt.GetDownloadCount() > 0) {
                            dropboxCloud.DownloadFile(backupCloudEnt);
                        }
                        if (backupCloudEnt.GetUploadCount() > 0) {
                            dropboxCloud.UploadFile(backupCloudEnt);
                        }
                        if (backupCloudEnt.GetDownloadCount() == 0 && backupCloudEnt.GetUploadCount() == 0) {
                            if (backupCloudEnt.GetStatus() == CloudFolderStatus.OnlyCloud.ordinal()) {
                                dropboxCloud.CreateLocalFolder(backupCloudEnt);
                            }
                            if (backupCloudEnt.GetStatus() == CloudFolderStatus.OnlyPhone.ordinal()) {
                                dropboxCloud.CreateFolder(backupCloudEnt);
                            }
                            for (int i = 0; i < UpdateBackupCloudEntList.size(); i++) {
                                if (((BackupCloudEnt) UpdateBackupCloudEntList.get(i)).GetFolderName().equals(backupCloudEnt.GetFolderName())) {
                                    ((BackupCloudEnt) UpdateBackupCloudEntList.get(i)).SetIsInProgress(false);
                                    ((BackupCloudEnt) UpdateBackupCloudEntList.get(i)).SetUploadCount(0);
                                    ((BackupCloudEnt) UpdateBackupCloudEntList.get(i)).SetDownloadCount(0);
                                    ((BackupCloudEnt) UpdateBackupCloudEntList.get(i)).SetStatus(CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal());
                                }
                            }
                            CloudBackupCloudEntList.remove(backupCloudEnt);
                        }
                    }
                    if (CloudBackupCloudEntList.size() > 0) {
                        Iterator it = CloudBackupCloudEntList.iterator();
                        while (it.hasNext()) {
                            BackupCloudEnt backupCloudEnt2 = (BackupCloudEnt) it.next();
                            boolean contains = backupCloudEnt2.GetDownloadCount() > 0 ? backupCloudEnt2.GetDownloadPath().values().contains(Boolean.valueOf(false)) : false;
                            boolean contains2 = backupCloudEnt2.GetUploadCount() > 0 ? backupCloudEnt2.GetUploadPath().values().contains(Boolean.valueOf(false)) : false;
                            if (!contains && !contains2) {
                                for (int i2 = 0; i2 < UpdateBackupCloudEntList.size(); i2++) {
                                    if (((BackupCloudEnt) UpdateBackupCloudEntList.get(i2)).GetFolderName().equals(backupCloudEnt2.GetFolderName())) {
                                        ((BackupCloudEnt) UpdateBackupCloudEntList.get(i2)).SetIsInProgress(false);
                                        ((BackupCloudEnt) UpdateBackupCloudEntList.get(i2)).SetUploadCount(0);
                                        ((BackupCloudEnt) UpdateBackupCloudEntList.get(i2)).SetDownloadCount(0);
                                        ((BackupCloudEnt) UpdateBackupCloudEntList.get(i2)).SetImageStatus(R.drawable.synced_status);
                                        ((BackupCloudEnt) UpdateBackupCloudEntList.get(i2)).SetSyncVisibility(4);
                                        ((BackupCloudEnt) UpdateBackupCloudEntList.get(i2)).SetStatus(CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal());
                                    }
                                }
                                CloudBackupCloudEntList.remove(backupCloudEnt2);
                            }
                        }
                    }
                    if (RemoveUpdateBackupIndexs.size() > 0) {
                        IsRemovingIndex = true;
                        Iterator it2 = RemoveUpdateBackupIndexs.iterator();
                        while (it2.hasNext()) {
                            UpdateBackupCloudEntList.remove(((Integer) it2.next()).intValue());
                        }
                        RemoveUpdateBackupIndexs.clear();
                        IsRemovingIndex = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (AddBackupCloudEntList.size() <= 0 && CloudBackupCloudEntList.size() <= 0 && RemoveUpdateBackupIndexs.size() <= 0) {
                this.run = false;
                CloudCommon.IsCloudServiceStarted = false;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }
}
