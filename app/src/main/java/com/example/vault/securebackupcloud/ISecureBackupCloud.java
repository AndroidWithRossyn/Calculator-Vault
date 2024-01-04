package com.example.vault.securebackupcloud;

import java.util.ArrayList;

public interface ISecureBackupCloud {
    void CreateFolder(String str);

    void CreateFolder(BackupCloudEnt backupCloudEnt);

    void CreateFolderStructure();

    void CreateLocalFolder(BackupCloudEnt backupCloudEnt);

    void DownloadFile(BackupCloudEnt backupCloudEnt);

    void GetFiles(String str);

    ArrayList<BackupCloudEnt> GetFolders(String str);

    void UploadFile(BackupCloudEnt backupCloudEnt);
}
