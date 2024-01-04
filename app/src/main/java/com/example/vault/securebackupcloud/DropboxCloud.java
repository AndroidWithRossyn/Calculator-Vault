package com.example.vault.securebackupcloud;

import android.content.Context;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import com.example.vault.audio.util.AudioDAL;
import com.example.vault.audio.model.AudioEnt;
import com.example.vault.audio.util.AudioPlayListDAL;
import com.example.vault.audio.model.AudioPlayListEnt;
import com.example.vault.common.Constants;
import com.example.vault.documents.util.DocumentDAL;
import com.example.vault.documents.model.DocumentFolder;
import com.example.vault.documents.util.DocumentFolderDAL;
import com.example.vault.documents.model.DocumentsEnt;
import com.example.vault.notes.util.NotesCommon;
import com.example.vault.notes.model.NotesFileDB_Pojo;
import com.example.vault.notes.util.NotesFilesDAL;
import com.example.vault.notes.util.NotesFolderDAL;
import com.example.vault.notes.model.NotesFolderDB_Pojo;
import com.example.vault.photo.model.Photo;
import com.example.vault.photo.model.PhotoAlbum;
import com.example.vault.photo.util.PhotoAlbumDAL;
import com.example.vault.photo.util.PhotoDAL;
import com.example.vault.securebackupcloud.CloudCommon.CloudFolderStatus;
import com.example.vault.securebackupcloud.CloudCommon.DropboxType;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.todolist.util.ToDoDAL;
import com.example.vault.todolist.model.ToDoDB_Pojo;
import com.example.vault.utilities.Utilities;
import com.example.vault.video.model.Video;
import com.example.vault.video.model.VideoAlbum;
import com.example.vault.video.util.VideoAlbumDAL;
import com.example.vault.video.util.VideoDAL;
import com.example.vault.wallet.util.WalletCategoriesDAL;
import com.example.vault.wallet.model.WalletCategoriesFileDB_Pojo;
import com.example.vault.wallet.util.WalletEntriesDAL;
import com.example.vault.wallet.model.WalletEntryFileDB_Pojo;

public class DropboxCloud implements ISecureBackupCloud {
    private int DownloadType;
    Context context;
    public DropboxCloudApi dropboxCloudApi;
    ListFolderResult result = null;

    public void GetFiles(String str) {
    }

    public DropboxCloud(Context context2, int i) {
        this.context = context2;
        this.DownloadType = i;
        this.dropboxCloudApi = new DropboxCloudApi(context2);
        CreateFolderStructure();
    }

    public void UploadFile(BackupCloudEnt backupCloudEnt) {
        String str = "";
        if (DropboxType.Photos.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb = new StringBuilder();
            sb.append(CloudCommon.PhotoFolder);
            sb.append("/");
            sb.append(backupCloudEnt.GetFolderName());
            str = sb.toString();
            StringBuilder sb2 = new StringBuilder();
            sb2.append(CloudCommon.PhotoFolder);
            sb2.append("/");
            sb2.append(backupCloudEnt.GetFolderName());
            CreateFolder(sb2.toString());
        } else if (DropboxType.Videos.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(CloudCommon.VideoFolder);
            sb3.append("/");
            sb3.append(backupCloudEnt.GetFolderName());
            str = sb3.toString();
            StringBuilder sb4 = new StringBuilder();
            sb4.append(CloudCommon.VideoFolder);
            sb4.append("/");
            sb4.append(backupCloudEnt.GetFolderName());
            CreateFolder(sb4.toString());
        } else if (DropboxType.Documents.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append(CloudCommon.DocumentFolder);
            sb5.append("/");
            sb5.append(backupCloudEnt.GetFolderName());
            str = sb5.toString();
            StringBuilder sb6 = new StringBuilder();
            sb6.append(CloudCommon.DocumentFolder);
            sb6.append("/");
            sb6.append(backupCloudEnt.GetFolderName());
            CreateFolder(sb6.toString());
        } else if (DropboxType.Notes.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb7 = new StringBuilder();
            sb7.append(CloudCommon.NotesFolder);
            sb7.append("/");
            sb7.append(backupCloudEnt.GetFolderName());
            str = sb7.toString();
            StringBuilder sb8 = new StringBuilder();
            sb8.append(CloudCommon.NotesFolder);
            sb8.append("/");
            sb8.append(backupCloudEnt.GetFolderName());
            CreateFolder(sb8.toString());
        } else if (DropboxType.Wallet.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb9 = new StringBuilder();
            sb9.append(CloudCommon.WalletFolder);
            sb9.append("/");
            sb9.append(backupCloudEnt.GetFolderName());
            str = sb9.toString();
            StringBuilder sb10 = new StringBuilder();
            sb10.append(CloudCommon.WalletFolder);
            sb10.append("/");
            sb10.append(backupCloudEnt.GetFolderName());
            CreateFolder(sb10.toString());
        } else if (DropboxType.ToDo.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb11 = new StringBuilder();
            sb11.append(CloudCommon.ToDoListFolder);
            sb11.append("/");
            sb11.append(backupCloudEnt.GetFolderName());
            str = sb11.toString();
            StringBuilder sb12 = new StringBuilder();
            sb12.append(CloudCommon.ToDoListFolder);
            sb12.append("/");
            sb12.append(backupCloudEnt.GetFolderName());
            CreateFolder(sb12.toString());
        } else if (DropboxType.Audio.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb13 = new StringBuilder();
            sb13.append(CloudCommon.AudioFolder);
            sb13.append("/");
            sb13.append(backupCloudEnt.GetFolderName());
            str = sb13.toString();
            StringBuilder sb14 = new StringBuilder();
            sb14.append(CloudCommon.AudioFolder);
            sb14.append("/");
            sb14.append(backupCloudEnt.GetFolderName());
            CreateFolder(sb14.toString());
        }
        if (backupCloudEnt.GetUploadCount() > 0) {
            Enumeration keys = backupCloudEnt.GetUploadPath().keys();
            while (keys.hasMoreElements()) {
                String str2 = str;
                DropBoxUploadFile dropBoxUploadFile = new DropBoxUploadFile(this.context, this.dropboxCloudApi.client, str2, (String) keys.nextElement(), this.DownloadType, backupCloudEnt);
                dropBoxUploadFile.execute(new Void[0]);
            }
        }
    }

    public void DownloadFile(BackupCloudEnt backupCloudEnt) {
        String str;
        String str2 = "";
        if (DropboxType.Photos.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb = new StringBuilder();
            sb.append(StorageOptionsCommon.STORAGEPATH);
            sb.append(StorageOptionsCommon.PHOTOS);
            str2 = sb.toString();
        } else if (DropboxType.Videos.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(StorageOptionsCommon.STORAGEPATH);
            sb2.append(StorageOptionsCommon.VIDEOS);
            str2 = sb2.toString();
        } else if (DropboxType.Documents.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(StorageOptionsCommon.STORAGEPATH);
            sb3.append(StorageOptionsCommon.DOCUMENTS);
            str2 = sb3.toString();
        } else if (DropboxType.Notes.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(StorageOptionsCommon.STORAGEPATH);
            sb4.append(StorageOptionsCommon.NOTES);
            str2 = sb4.toString();
        } else if (DropboxType.Wallet.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append(StorageOptionsCommon.STORAGEPATH);
            sb5.append(StorageOptionsCommon.WALLET);
            str2 = sb5.toString();
        } else if (DropboxType.ToDo.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb6 = new StringBuilder();
            sb6.append(StorageOptionsCommon.STORAGEPATH);
            sb6.append(StorageOptionsCommon.TODOLIST);
            str2 = sb6.toString();
        } else if (DropboxType.Audio.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb7 = new StringBuilder();
            sb7.append(StorageOptionsCommon.STORAGEPATH);
            sb7.append(StorageOptionsCommon.AUDIOS);
            str2 = sb7.toString();
        }
        if (backupCloudEnt.GetDownloadCount() > 0) {
            Enumeration keys = backupCloudEnt.GetDownloadPath().keys();
            while (keys.hasMoreElements()) {
                String str3 = (String) keys.nextElement();
                if (DropboxType.ToDo.ordinal() == backupCloudEnt.GetDropboxType()) {
                    str = str2;
                } else {
                    StringBuilder sb8 = new StringBuilder();
                    sb8.append(str2);
                    sb8.append(new File(str3).getParentFile().getName());
                    str = sb8.toString();
                }
                DropBoxDownloadFile dropBoxDownloadFile = new DropBoxDownloadFile(this.context, this.dropboxCloudApi.client, str3, str, this.DownloadType, backupCloudEnt);
                dropBoxDownloadFile.execute(new Void[0]);
            }
        }
    }

    public void CreateLocalFolder(BackupCloudEnt backupCloudEnt) {
        String str = "";
        if (DropboxType.Photos.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb = new StringBuilder();
            sb.append(StorageOptionsCommon.STORAGEPATH);
            sb.append(StorageOptionsCommon.PHOTOS);
            str = sb.toString();
        } else if (DropboxType.Videos.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(StorageOptionsCommon.STORAGEPATH);
            sb2.append(StorageOptionsCommon.VIDEOS);
            str = sb2.toString();
        } else if (DropboxType.Documents.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(StorageOptionsCommon.STORAGEPATH);
            sb3.append(StorageOptionsCommon.DOCUMENTS);
            str = sb3.toString();
        } else if (DropboxType.Notes.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(StorageOptionsCommon.STORAGEPATH);
            sb4.append(StorageOptionsCommon.NOTES);
            str = sb4.toString();
        } else if (DropboxType.Wallet.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append(StorageOptionsCommon.STORAGEPATH);
            sb5.append(StorageOptionsCommon.WALLET);
            str = sb5.toString();
        } else if (DropboxType.Audio.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb6 = new StringBuilder();
            sb6.append(StorageOptionsCommon.STORAGEPATH);
            sb6.append(StorageOptionsCommon.AUDIOS);
            str = sb6.toString();
        }
        if (DropboxType.ToDo.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb7 = new StringBuilder();
            sb7.append(StorageOptionsCommon.STORAGEPATH);
            sb7.append(StorageOptionsCommon.TODOLIST);
            str = sb7.toString();
        }
        StringBuilder sb8 = new StringBuilder();
        sb8.append(str);
        sb8.append(backupCloudEnt.GetFolderName());
        AddFolderInLocal(backupCloudEnt.GetDropboxType(), sb8.toString());
    }

    public ArrayList<BackupCloudEnt> GetFolders(String str) {
        ListFolderResult listFolderResult;
        ArrayList<BackupCloudEnt> arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList();
        try {
            listFolderResult = this.dropboxCloudApi.client.files().listFolder(str);
        } catch (DbxException e) {
            e.printStackTrace();
            listFolderResult = null;
        }
        if (listFolderResult == null) {
            return arrayList;
        }
        for (Metadata metadata : listFolderResult.getEntries()) {
            BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
            if (metadata instanceof FolderMetadata) {
                FolderMetadata folderMetadata = (FolderMetadata) metadata;
                backupCloudEnt.SetMetadata(folderMetadata);
                backupCloudEnt.SetFolderName(folderMetadata.getName());
                backupCloudEnt.SetMimeType(backupCloudEnt.GetMimeType());
                backupCloudEnt.SetPath(metadata.getPathDisplay());
                backupCloudEnt.SetRoot(backupCloudEnt.GetRoot());
                backupCloudEnt.SetSize(backupCloudEnt.GetSize());
                backupCloudEnt.SetFileNames(GetCloudFolderFiles(backupCloudEnt.GetPath()));
                backupCloudEnt.SetUploadPath(GetUploadPaths(backupCloudEnt.GetFileNames(), metadata.getName()));
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDownloadPath(GetDownloadPaths(backupCloudEnt.GetFileNames(), metadata.getName()));
                backupCloudEnt.SetDownloadCount(backupCloudEnt.GetDownloadPath().size());
                backupCloudEnt.SetStatus(GetStatus(metadata.getName(), backupCloudEnt.GetDownloadCount(), backupCloudEnt.GetUploadCount()));
                backupCloudEnt.SetDropboxType(this.DownloadType);
                backupCloudEnt.SetDownloadCompleteStatus(false);
                backupCloudEnt.SetUploadCompleteStatus(false);
                arrayList.add(backupCloudEnt);
                arrayList2.add(metadata.getName());
            }
        }
        return GetPhoneFolders(arrayList2, arrayList);
    }

    public void CreateFolder(String str) {
        try {
            this.dropboxCloudApi.client.files().createFolder(str);
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    public void CreateFolder(BackupCloudEnt backupCloudEnt) {
        if (DropboxType.Photos.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb = new StringBuilder();
            sb.append(CloudCommon.PhotoFolder);
            sb.append(backupCloudEnt.GetFolderName());
            sb.append("/");
            CreateFolder(sb.toString());
        } else if (DropboxType.Videos.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(CloudCommon.VideoFolder);
            sb2.append(backupCloudEnt.GetFolderName());
            sb2.append("/");
            CreateFolder(sb2.toString());
        } else if (DropboxType.Documents.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(CloudCommon.DocumentFolder);
            sb3.append(backupCloudEnt.GetFolderName());
            sb3.append("/");
            CreateFolder(sb3.toString());
        } else if (DropboxType.Notes.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(CloudCommon.NotesFolder);
            sb4.append(backupCloudEnt.GetFolderName());
            sb4.append("/");
            CreateFolder(sb4.toString());
        } else if (DropboxType.Wallet.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append(CloudCommon.WalletFolder);
            sb5.append(backupCloudEnt.GetFolderName());
            sb5.append("/");
            CreateFolder(sb5.toString());
        } else if (DropboxType.ToDo.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb6 = new StringBuilder();
            sb6.append(CloudCommon.ToDoListFolder);
            sb6.append(backupCloudEnt.GetFolderName());
            sb6.append("/");
            CreateFolder(sb6.toString());
        } else if (DropboxType.Audio.ordinal() == backupCloudEnt.GetDropboxType()) {
            StringBuilder sb7 = new StringBuilder();
            sb7.append(CloudCommon.AudioFolder);
            sb7.append(backupCloudEnt.GetFolderName());
            sb7.append("/");
            CreateFolder(sb7.toString());
        }
    }

    public void CreateFolderStructure() {
        try {
            this.dropboxCloudApi.client.files().createFolder(CloudCommon.PhotoFolder);
        } catch (DbxException e) {
            e.printStackTrace();
        }
        try {
            this.dropboxCloudApi.client.files().createFolder(CloudCommon.VideoFolder);
        } catch (DbxException e2) {
            e2.printStackTrace();
        }
        try {
            this.dropboxCloudApi.client.files().createFolder(CloudCommon.DocumentFolder);
        } catch (DbxException e3) {
            e3.printStackTrace();
        }
        try {
            this.dropboxCloudApi.client.files().createFolder(CloudCommon.NotesFolder);
        } catch (DbxException e4) {
            e4.printStackTrace();
        }
        try {
            this.dropboxCloudApi.client.files().createFolder(CloudCommon.WalletFolder);
        } catch (DbxException e5) {
            e5.printStackTrace();
        }
        try {
            this.dropboxCloudApi.client.files().createFolder(CloudCommon.ToDoListFolder);
        } catch (DbxException e6) {
            e6.printStackTrace();
        }
        try {
            this.dropboxCloudApi.client.files().createFolder(CloudCommon.AudioFolder);
        } catch (DbxException e7) {
            e7.printStackTrace();
        }
    }

    private ArrayList<String> GetCloudFolderFiles(String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            this.result = this.dropboxCloudApi.client.files().listFolder(str);
        } catch (DbxException e) {
            e.printStackTrace();
        }
        for (Metadata pathDisplay : this.result.getEntries()) {
            arrayList.add(pathDisplay.getPathDisplay());
        }
        return arrayList;
    }

    private Hashtable<String, Boolean> GetDownloadPaths(ArrayList<String> arrayList, String str) {
        if (DropboxType.Photos.ordinal() == this.DownloadType) {
            return GetDownloadPhotosPath(arrayList, str);
        }
        if (DropboxType.Videos.ordinal() == this.DownloadType) {
            return GetDownloadVideosPath(arrayList, str);
        }
        if (DropboxType.Documents.ordinal() == this.DownloadType) {
            return GetDownloadDocumentsPath(arrayList, str);
        }
        if (DropboxType.Notes.ordinal() == this.DownloadType) {
            return GetDownloadNotesPath(arrayList, str);
        }
        if (DropboxType.Wallet.ordinal() == this.DownloadType) {
            return GetDownloadWalletPath(arrayList, str);
        }
        if (DropboxType.Audio.ordinal() == this.DownloadType) {
            return GetDownloadMusicPath(arrayList, str);
        }
        return null;
    }

    private Hashtable<String, Boolean> GetDownloadPathForToDo(String str, String str2) {
        if (DropboxType.ToDo.ordinal() == this.DownloadType) {
            return GetDownloadToDoPath(str, str2);
        }
        return null;
    }

    private Hashtable<String, Boolean> GetUploadPathForToDo(String str, String str2) {
        if (DropboxType.ToDo.ordinal() == this.DownloadType) {
            return GetUploadToDoPath(str, str2);
        }
        return null;
    }

    private Hashtable<String, Boolean> GetUploadPaths(ArrayList<String> arrayList, String str) {
        if (DropboxType.Photos.ordinal() == this.DownloadType) {
            return GetUploadPhotosPath(arrayList, str);
        }
        if (DropboxType.Videos.ordinal() == this.DownloadType) {
            return GetUploadVideosPath(arrayList, str);
        }
        if (DropboxType.Documents.ordinal() == this.DownloadType) {
            return GetUploadDocumentsPath(arrayList, str);
        }
        if (DropboxType.Notes.ordinal() == this.DownloadType) {
            return GetUploadNotesPath(arrayList, str);
        }
        if (DropboxType.Wallet.ordinal() == this.DownloadType) {
            return GetUploadWalletPath(arrayList, str);
        }
        if (DropboxType.Audio.ordinal() == this.DownloadType) {
            return GetUploadMusicPath(arrayList, str);
        }
        return null;
    }

    private int GetStatus(String str, int i, int i2) {
        if (DropboxType.Photos.ordinal() == this.DownloadType) {
            return GetPhotoStatus(str, i, i2);
        }
        if (DropboxType.Videos.ordinal() == this.DownloadType) {
            return GetVideoStatus(str, i, i2);
        }
        if (DropboxType.Documents.ordinal() == this.DownloadType) {
            return GetDocumentStatus(str, i, i2);
        }
        if (DropboxType.Notes.ordinal() == this.DownloadType) {
            return GetNoteStatus(str, i, i2);
        }
        if (DropboxType.Wallet.ordinal() == this.DownloadType) {
            return GetWalletStatus(str, i, i2);
        }
        if (DropboxType.ToDo.ordinal() == this.DownloadType) {
            return GetToDoStatus(str, i, i2);
        }
        if (DropboxType.Audio.ordinal() == this.DownloadType) {
            return GetMusicStatus(str, i, i2);
        }
        return 0;
    }

    private int GetToDoStatus(String str, int i, int i2) {
        ToDoDAL toDoDAL = new ToDoDAL(this.context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
        constants.getClass();
        sb.append("ToDoName");
        sb.append(" = '");
        sb.append(str);
        sb.append("' AND ");
        constants.getClass();
        sb.append("ToDoIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        if (toDoDAL.getToDoInfoFromDatabase(sb.toString()).getToDoFileName() == null) {
            return CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i != 0) {
            return CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i2 != 0) {
            return CloudFolderStatus.OnlyPhone.ordinal();
        }
        return CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal();
    }

    private ArrayList<BackupCloudEnt> GetPhoneFolders(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        if (DropboxType.Photos.ordinal() == this.DownloadType) {
            return GetPhotoPhoneFolders(arrayList, arrayList2);
        }
        if (DropboxType.Videos.ordinal() == this.DownloadType) {
            return GetVideoPhoneFolders(arrayList, arrayList2);
        }
        if (DropboxType.Documents.ordinal() == this.DownloadType) {
            return GetDocumentPhoneFolders(arrayList, arrayList2);
        }
        if (DropboxType.Notes.ordinal() == this.DownloadType) {
            return GetNotesPhoneFolders(arrayList, arrayList2);
        }
        if (DropboxType.Wallet.ordinal() == this.DownloadType) {
            return GetWalletPhoneFolders(arrayList, arrayList2);
        }
        if (DropboxType.ToDo.ordinal() == this.DownloadType) {
            return GetToDoPhoneFiles(arrayList, arrayList2);
        }
        if (DropboxType.Audio.ordinal() == this.DownloadType) {
            return GetMusicPhoneFolders(arrayList, arrayList2);
        }
        return null;
    }

    private Hashtable<String, Boolean> GetPhoneFolderFiles(String str) {
        if (DropboxType.Photos.ordinal() == this.DownloadType) {
            return GetPhotoPhoneFolderFiles(str);
        }
        if (DropboxType.Videos.ordinal() == this.DownloadType) {
            return GetVideoPhoneFolderFiles(str);
        }
        if (DropboxType.Documents.ordinal() == this.DownloadType) {
            return GetDocumentPhoneFolderFiles(str);
        }
        if (DropboxType.Notes.ordinal() == this.DownloadType) {
            return GetNotesPhoneFolderFiles(str);
        }
        if (DropboxType.Wallet.ordinal() == this.DownloadType) {
            return GetWalletPhoneFolderFiles(str);
        }
        if (DropboxType.Audio.ordinal() == this.DownloadType) {
            return GetMusicPhoneFolderFiles(str);
        }
        return null;
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

    private Hashtable<String, Boolean> GetDownloadToDoPath(String str, String str2) {
        new SimpleDateFormat("EEE, dd MMM yyyy HH:edit_share_btn:ss");
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        ToDoDAL toDoDAL = new ToDoDAL(this.context);
        Constants constants = new Constants();
        String str3 = new File(str2).getName().split(StorageOptionsCommon.NOTES_FILE_EXTENSION)[0];
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
        constants.getClass();
        sb.append("ToDoName");
        sb.append(" = '");
        sb.append(str3);
        sb.append("' AND ");
        constants.getClass();
        sb.append("ToDoIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        try {
            if (!new File(str2).getName().contentEquals(new File(toDoDAL.getToDoInfoFromDatabase(sb.toString()).getToDoFileLocation()).getName())) {
                hashtable.put(str2, Boolean.valueOf(false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetDownloadTodo123tPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        WalletEntriesDAL walletEntriesDAL = new WalletEntriesDAL(this.context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
        constants.getClass();
        sb.append("WalletCategoriesFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" AND ");
        constants.getClass();
        sb.append("WalletCategoriesFileName");
        sb.append(" = '");
        sb.append(str);
        sb.append("'");
        WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL.getCategoryInfoFromDatabase(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        constants.getClass();
        sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
        constants.getClass();
        sb2.append("WalletEntryFileIsDecoy");
        sb2.append(" = ");
        sb2.append(SecurityLocksCommon.IsFakeAccount);
        sb2.append(" AND ");
        constants.getClass();
        sb2.append("WalletCategoriesFileId");
        sb2.append(" = ");
        sb2.append(categoryInfoFromDatabase.getCategoryFileId());
        List allEntriesInfoFromDatabase = walletEntriesDAL.getAllEntriesInfoFromDatabase(sb2.toString());
        if (arrayList.size() > 0) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                String str2 = (String) it.next();
                Iterator it2 = allEntriesInfoFromDatabase.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        z = false;
                        break;
                    }
                    if (new File(str2).getName().contentEquals(new File(((WalletEntryFileDB_Pojo) it2.next()).getEntryFileLocation()).getName())) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(str2, Boolean.valueOf(false));
                }
            }
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetUploadToDoPath(String str, String str2) {
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        ToDoDAL toDoDAL = new ToDoDAL(this.context);
        Constants constants = new Constants();
        String str3 = new File(str2).getName().split(StorageOptionsCommon.NOTES_FILE_EXTENSION)[0];
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
        constants.getClass();
        sb.append("ToDoName");
        sb.append(" = '");
        sb.append(str3);
        sb.append("' AND ");
        constants.getClass();
        sb.append("ToDoIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        ToDoDB_Pojo toDoInfoFromDatabase = toDoDAL.getToDoInfoFromDatabase(sb.toString());
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:edit_share_btn:ss", Locale.getDefault());
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("EEE, dd MMM yyyy hh:edit_share_btn:ss +0000", Locale.getDefault());
            Date parse = simpleDateFormat.parse(toDoInfoFromDatabase.getToDoFileModifiedDate());
            Date parse2 = simpleDateFormat2.parse(str);
            Date parse3 = simpleDateFormat2.parse(Utilities.convertDateToGMT(parse));
            if (!toDoInfoFromDatabase.getToDoFileName().equals("") && new File(toDoInfoFromDatabase.getToDoFileLocation()).getName().contentEquals(new File(str2).getName()) && parse3.after(parse2)) {
                hashtable.put(toDoInfoFromDatabase.getToDoFileLocation(), Boolean.valueOf(false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashtable;
    }

    private ArrayList<BackupCloudEnt> GetToDoPhoneFiles(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        ToDoDAL toDoDAL = new ToDoDAL(this.context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
        constants.getClass();
        sb.append("ToDoIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        for (ToDoDB_Pojo toDoDB_Pojo : toDoDAL.getAllToDoInfoFromDatabase(sb.toString())) {
            if (!arrayList.contains(toDoDB_Pojo.getToDoFileName().concat(StorageOptionsCommon.NOTES_FILE_EXTENSION))) {
                Hashtable hashtable = new Hashtable();
                BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
                backupCloudEnt.SetFolderName(toDoDB_Pojo.getToDoFileName());
                backupCloudEnt.SetPath(CloudCommon.ToDoListFolder);
                backupCloudEnt.SetDropboxType(this.DownloadType);
                hashtable.put(toDoDB_Pojo.getToDoFileLocation(), Boolean.valueOf(false));
                backupCloudEnt.SetUploadPath(hashtable);
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDownloadCount(0);
                backupCloudEnt.SetStatus(CloudFolderStatus.OnlyPhone.ordinal());
                arrayList2.add(backupCloudEnt);
            }
        }
        return arrayList2;
    }

    private Hashtable<String, Boolean> GetDownloadVideosPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        videoAlbumDAL.OpenRead();
        VideoAlbum GetAlbum = videoAlbumDAL.GetAlbum(str);
        VideoDAL videoDAL = new VideoDAL(this.context);
        videoDAL.OpenRead();
        List GetVideosById = videoDAL.GetVideosById(GetAlbum.getId());
        if (arrayList.size() > 0) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                String str2 = (String) it.next();
                Iterator it2 = GetVideosById.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        z = false;
                        break;
                    }
                    if (new File(str2).getName().contentEquals(((Video) it2.next()).getVideoName())) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(str2, Boolean.valueOf(false));
                }
            }
        }
        videoAlbumDAL.close();
        videoDAL.close();
        return hashtable;
    }

    private Hashtable<String, Boolean> GetUploadVideosPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        videoAlbumDAL.OpenRead();
        VideoAlbum GetAlbum = videoAlbumDAL.GetAlbum(str);
        VideoDAL videoDAL = new VideoDAL(this.context);
        videoDAL.OpenRead();
        List<Video> GetVideosById = videoDAL.GetVideosById(GetAlbum.getId());
        if (GetVideosById.size() > 0) {
            for (Video video : GetVideosById) {
                Iterator it = arrayList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = false;
                        break;
                    }
                    if (video.getVideoName().contentEquals(new File((String) it.next()).getName())) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(video.getFolderLockVideoLocation(), Boolean.valueOf(false));
                }
            }
        }
        videoAlbumDAL.close();
        videoDAL.close();
        return hashtable;
    }

    private int GetVideoStatus(String str, int i, int i2) {
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        videoAlbumDAL.OpenRead();
        VideoAlbum GetAlbum = videoAlbumDAL.GetAlbum(str);
        videoAlbumDAL.close();
        if (GetAlbum.getAlbumName() == null) {
            return CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i == 0 && i2 == 0) {
            return CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal();
        }
        return CloudFolderStatus.CloudAndPhoneNotSync.ordinal();
    }

    private ArrayList<BackupCloudEnt> GetVideoPhoneFolders(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        videoAlbumDAL.OpenRead();
        for (VideoAlbum videoAlbum : videoAlbumDAL.GetAlbums(0)) {
            if (!arrayList.contains(videoAlbum.getAlbumName())) {
                BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
                backupCloudEnt.SetFolderName(videoAlbum.getAlbumName());
                backupCloudEnt.SetPath(videoAlbum.getAlbumLocation());
                backupCloudEnt.SetUploadPath(GetPhoneFolderFiles(videoAlbum.getAlbumName()));
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDropboxType(this.DownloadType);
                backupCloudEnt.SetDownloadCount(0);
                backupCloudEnt.SetStatus(CloudFolderStatus.OnlyPhone.ordinal());
                arrayList2.add(backupCloudEnt);
            }
        }
        return arrayList2;
    }

    private Hashtable<String, Boolean> GetVideoPhoneFolderFiles(String str) {
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        videoAlbumDAL.OpenRead();
        VideoAlbum GetAlbum = videoAlbumDAL.GetAlbum(str);
        VideoDAL videoDAL = new VideoDAL(this.context);
        videoDAL.OpenRead();
        List<Video> GetVideoByAlbumId = videoDAL.GetVideoByAlbumId(GetAlbum.getId(), 4);
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        for (Video folderLockVideoLocation : GetVideoByAlbumId) {
            hashtable.put(folderLockVideoLocation.getFolderLockVideoLocation(), Boolean.valueOf(false));
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetDownloadPhotosPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenRead();
        PhotoAlbum GetAlbum = photoAlbumDAL.GetAlbum(str);
        PhotoDAL photoDAL = new PhotoDAL(this.context);
        photoDAL.OpenRead();
        List GetPhotoByAlbumId = photoDAL.GetPhotoByAlbumId(GetAlbum.getId(), 4);
        if (arrayList.size() > 0) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                String str2 = (String) it.next();
                Iterator it2 = GetPhotoByAlbumId.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        z = false;
                        break;
                    }
                    if (new File(str2).getName().contentEquals(((Photo) it2.next()).getPhotoName())) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(str2, Boolean.valueOf(false));
                }
            }
        }
        photoAlbumDAL.close();
        photoDAL.close();
        return hashtable;
    }

    private Hashtable<String, Boolean> GetUploadPhotosPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenRead();
        PhotoAlbum GetAlbum = photoAlbumDAL.GetAlbum(str);
        PhotoDAL photoDAL = new PhotoDAL(this.context);
        photoDAL.OpenRead();
        List<Photo> GetPhotoByAlbumId = photoDAL.GetPhotoByAlbumId(GetAlbum.getId(), 4);
        if (GetPhotoByAlbumId.size() > 0) {
            for (Photo photo : GetPhotoByAlbumId) {
                Iterator it = arrayList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = false;
                        break;
                    }
                    if (photo.getPhotoName().contentEquals(new File((String) it.next()).getName())) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(photo.getFolderLockPhotoLocation(), Boolean.valueOf(false));
                }
            }
        }
        photoAlbumDAL.close();
        photoDAL.close();
        return hashtable;
    }

    private int GetPhotoStatus(String str, int i, int i2) {
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenRead();
        PhotoAlbum GetAlbum = photoAlbumDAL.GetAlbum(str);
        photoAlbumDAL.close();
        if (GetAlbum.getAlbumName() == null) {
            return CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i == 0 && i2 == 0) {
            return CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal();
        }
        return CloudFolderStatus.CloudAndPhoneNotSync.ordinal();
    }

    private ArrayList<BackupCloudEnt> GetPhotoPhoneFolders(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenRead();
        for (PhotoAlbum photoAlbum : photoAlbumDAL.GetAlbums(0)) {
            if (!arrayList.contains(photoAlbum.getAlbumName())) {
                BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
                backupCloudEnt.SetFolderName(photoAlbum.getAlbumName());
                backupCloudEnt.SetPath(photoAlbum.getAlbumLocation());
                backupCloudEnt.SetDropboxType(this.DownloadType);
                backupCloudEnt.SetUploadPath(GetPhoneFolderFiles(photoAlbum.getAlbumName()));
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDownloadCount(0);
                backupCloudEnt.SetStatus(CloudFolderStatus.OnlyPhone.ordinal());
                arrayList2.add(backupCloudEnt);
            }
        }
        return arrayList2;
    }

    private Hashtable<String, Boolean> GetPhotoPhoneFolderFiles(String str) {
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenRead();
        PhotoAlbum GetAlbum = photoAlbumDAL.GetAlbum(str);
        PhotoDAL photoDAL = new PhotoDAL(this.context);
        photoDAL.OpenRead();
        List<Photo> GetPhotoByAlbumId = photoDAL.GetPhotoByAlbumId(GetAlbum.getId(), 4);
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        for (Photo folderLockPhotoLocation : GetPhotoByAlbumId) {
            hashtable.put(folderLockPhotoLocation.getFolderLockPhotoLocation(), Boolean.valueOf(false));
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetDownloadMusicPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenRead();
        AudioPlayListEnt GetPlayList = audioPlayListDAL.GetPlayList(str);
        AudioDAL audioDAL = new AudioDAL(this.context);
        audioDAL.OpenRead();
        List GetAudios = audioDAL.GetAudios(GetPlayList.getId());
        if (arrayList.size() > 0) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                String str2 = (String) it.next();
                Iterator it2 = GetAudios.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        z = false;
                        break;
                    }
                    if (new File(str2).getName().contentEquals(((AudioEnt) it2.next()).getAudioName())) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(str2, Boolean.valueOf(false));
                }
            }
        }
        audioPlayListDAL.close();
        audioDAL.close();
        return hashtable;
    }

    private Hashtable<String, Boolean> GetUploadMusicPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenRead();
        AudioPlayListEnt GetPlayList = audioPlayListDAL.GetPlayList(str);
        AudioDAL audioDAL = new AudioDAL(this.context);
        audioDAL.OpenRead();
        List<AudioEnt> GetAudios = audioDAL.GetAudios(GetPlayList.getId());
        if (GetAudios.size() > 0) {
            for (AudioEnt audioEnt : GetAudios) {
                Iterator it = arrayList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = false;
                        break;
                    }
                    if (audioEnt.getAudioName().contentEquals(new File((String) it.next()).getName())) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(audioEnt.getFolderLockAudioLocation(), Boolean.valueOf(false));
                }
            }
        }
        audioPlayListDAL.close();
        audioDAL.close();
        return hashtable;
    }

    private int GetMusicStatus(String str, int i, int i2) {
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenRead();
        AudioPlayListEnt GetPlayList = audioPlayListDAL.GetPlayList(str);
        audioPlayListDAL.close();
        if (GetPlayList.getPlayListName() == null) {
            return CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i == 0 && i2 == 0) {
            return CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal();
        }
        return CloudFolderStatus.CloudAndPhoneNotSync.ordinal();
    }

    private ArrayList<BackupCloudEnt> GetMusicPhoneFolders(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenRead();
        for (AudioPlayListEnt audioPlayListEnt : audioPlayListDAL.GetPlayLists()) {
            if (!arrayList.contains(audioPlayListEnt.getPlayListName())) {
                BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
                backupCloudEnt.SetFolderName(audioPlayListEnt.getPlayListName());
                backupCloudEnt.SetPath(audioPlayListEnt.getPlayListLocation());
                backupCloudEnt.SetDropboxType(this.DownloadType);
                backupCloudEnt.SetUploadPath(GetPhoneFolderFiles(audioPlayListEnt.getPlayListName()));
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDownloadCount(0);
                backupCloudEnt.SetStatus(CloudFolderStatus.OnlyPhone.ordinal());
                arrayList2.add(backupCloudEnt);
            }
        }
        return arrayList2;
    }

    private Hashtable<String, Boolean> GetMusicPhoneFolderFiles(String str) {
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenRead();
        AudioPlayListEnt GetPlayList = audioPlayListDAL.GetPlayList(str);
        AudioDAL audioDAL = new AudioDAL(this.context);
        audioDAL.OpenRead();
        List<AudioEnt> GetAudios = audioDAL.GetAudios(GetPlayList.getId());
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        for (AudioEnt folderLockAudioLocation : GetAudios) {
            hashtable.put(folderLockAudioLocation.getFolderLockAudioLocation(), Boolean.valueOf(false));
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetDownloadNotesPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(this.context);
        NotesFilesDAL notesFilesDAL = new NotesFilesDAL(this.context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
        constants.getClass();
        sb.append("NotesFolderName");
        sb.append(" = '");
        sb.append(str);
        sb.append("' AND ");
        constants.getClass();
        sb.append("NotesFolderIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        NotesFolderDB_Pojo notesFolderInfoFromDatabase = notesFolderDAL.getNotesFolderInfoFromDatabase(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        constants.getClass();
        sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
        constants.getClass();
        sb2.append("NotesFolderId");
        sb2.append(" = ");
        sb2.append(notesFolderInfoFromDatabase.getNotesFolderId());
        sb2.append(" AND ");
        constants.getClass();
        sb2.append("NotesFileIsDecoy");
        sb2.append(" = ");
        sb2.append(SecurityLocksCommon.IsFakeAccount);
        List allNotesFileInfoFromDatabase = notesFilesDAL.getAllNotesFileInfoFromDatabase(sb2.toString());
        if (arrayList.size() > 0) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                String str2 = (String) it.next();
                Iterator it2 = allNotesFileInfoFromDatabase.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        z = false;
                        break;
                    }
                    if (new File(str2).getName().contentEquals(new File(((NotesFileDB_Pojo) it2.next()).getNotesFileLocation()).getName())) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(str2, Boolean.valueOf(false));
                }
            }
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetUploadNotesPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(this.context);
        NotesFilesDAL notesFilesDAL = new NotesFilesDAL(this.context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
        constants.getClass();
        sb.append("NotesFolderName");
        sb.append(" = '");
        sb.append(str);
        sb.append("' AND ");
        constants.getClass();
        sb.append("NotesFolderIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        NotesFolderDB_Pojo notesFolderInfoFromDatabase = notesFolderDAL.getNotesFolderInfoFromDatabase(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        constants.getClass();
        sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
        constants.getClass();
        sb2.append("NotesFolderId");
        sb2.append(" = ");
        sb2.append(notesFolderInfoFromDatabase.getNotesFolderId());
        sb2.append(" AND ");
        constants.getClass();
        sb2.append("NotesFileIsDecoy");
        sb2.append(" = ");
        sb2.append(SecurityLocksCommon.IsFakeAccount);
        List<NotesFileDB_Pojo> allNotesFileInfoFromDatabase = notesFilesDAL.getAllNotesFileInfoFromDatabase(sb2.toString());
        if (allNotesFileInfoFromDatabase.size() > 0) {
            for (NotesFileDB_Pojo notesFileDB_Pojo : allNotesFileInfoFromDatabase) {
                Iterator it = arrayList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = false;
                        break;
                    }
                    if (new File(notesFileDB_Pojo.getNotesFileLocation()).getName().contentEquals(new File((String) it.next()).getName())) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(notesFileDB_Pojo.getNotesFileLocation(), Boolean.valueOf(false));
                }
            }
        }
        return hashtable;
    }

    private int GetNoteStatus(String str, int i, int i2) {
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(this.context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
        constants.getClass();
        sb.append("NotesFolderName");
        sb.append(" = '");
        sb.append(str);
        sb.append("' AND ");
        constants.getClass();
        sb.append("NotesFolderIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        if (notesFolderDAL.getNotesFolderInfoFromDatabase(sb.toString()).getNotesFolderName() == null) {
            return CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i == 0 && i2 == 0) {
            return CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal();
        }
        return CloudFolderStatus.CloudAndPhoneNotSync.ordinal();
    }

    private ArrayList<BackupCloudEnt> GetNotesPhoneFolders(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(this.context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
        constants.getClass();
        sb.append("NotesFolderIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        for (NotesFolderDB_Pojo notesFolderDB_Pojo : notesFolderDAL.getAllNotesFolderInfoFromDatabase(sb.toString())) {
            if (!arrayList.contains(notesFolderDB_Pojo.getNotesFolderName())) {
                BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
                backupCloudEnt.SetFolderName(notesFolderDB_Pojo.getNotesFolderName());
                backupCloudEnt.SetPath(notesFolderDB_Pojo.getNotesFolderLocation());
                backupCloudEnt.SetDropboxType(this.DownloadType);
                backupCloudEnt.SetUploadPath(GetPhoneFolderFiles(notesFolderDB_Pojo.getNotesFolderName()));
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDownloadCount(0);
                backupCloudEnt.SetStatus(CloudFolderStatus.OnlyPhone.ordinal());
                arrayList2.add(backupCloudEnt);
            }
        }
        return arrayList2;
    }

    private Hashtable<String, Boolean> GetNotesPhoneFolderFiles(String str) {
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(this.context);
        NotesFilesDAL notesFilesDAL = new NotesFilesDAL(this.context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
        constants.getClass();
        sb.append("NotesFolderName");
        sb.append(" = '");
        sb.append(str);
        sb.append("' AND ");
        constants.getClass();
        sb.append("NotesFolderIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        NotesFolderDB_Pojo notesFolderInfoFromDatabase = notesFolderDAL.getNotesFolderInfoFromDatabase(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        constants.getClass();
        sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
        constants.getClass();
        sb2.append("NotesFolderId");
        sb2.append(" = ");
        sb2.append(notesFolderInfoFromDatabase.getNotesFolderId());
        sb2.append(" AND ");
        constants.getClass();
        sb2.append("NotesFileIsDecoy");
        sb2.append(" = ");
        sb2.append(SecurityLocksCommon.IsFakeAccount);
        for (NotesFileDB_Pojo notesFileLocation : notesFilesDAL.getAllNotesFileInfoFromDatabase(sb2.toString())) {
            hashtable.put(notesFileLocation.getNotesFileLocation(), Boolean.valueOf(false));
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetDownloadWalletPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        WalletEntriesDAL walletEntriesDAL = new WalletEntriesDAL(this.context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
        constants.getClass();
        sb.append("WalletCategoriesFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" AND ");
        constants.getClass();
        sb.append("WalletCategoriesFileName");
        sb.append(" = '");
        sb.append(str);
        sb.append("'");
        WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL.getCategoryInfoFromDatabase(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        constants.getClass();
        sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
        constants.getClass();
        sb2.append("WalletEntryFileIsDecoy");
        sb2.append(" = ");
        sb2.append(SecurityLocksCommon.IsFakeAccount);
        sb2.append(" AND ");
        constants.getClass();
        sb2.append("WalletCategoriesFileId");
        sb2.append(" = ");
        sb2.append(categoryInfoFromDatabase.getCategoryFileId());
        List allEntriesInfoFromDatabase = walletEntriesDAL.getAllEntriesInfoFromDatabase(sb2.toString());
        if (arrayList.size() > 0) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                String str2 = (String) it.next();
                Iterator it2 = allEntriesInfoFromDatabase.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        z = false;
                        break;
                    }
                    if (new File(str2).getName().contentEquals(new File(((WalletEntryFileDB_Pojo) it2.next()).getEntryFileLocation()).getName())) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(str2, Boolean.valueOf(false));
                }
            }
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetUploadWalletPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        WalletEntriesDAL walletEntriesDAL = new WalletEntriesDAL(this.context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
        constants.getClass();
        sb.append("WalletCategoriesFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" AND ");
        constants.getClass();
        sb.append("WalletCategoriesFileName");
        sb.append(" = '");
        sb.append(str);
        sb.append("'");
        WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL.getCategoryInfoFromDatabase(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        constants.getClass();
        sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
        constants.getClass();
        sb2.append("WalletEntryFileIsDecoy");
        sb2.append(" = ");
        sb2.append(SecurityLocksCommon.IsFakeAccount);
        sb2.append(" AND ");
        constants.getClass();
        sb2.append("WalletCategoriesFileId");
        sb2.append(" = ");
        sb2.append(categoryInfoFromDatabase.getCategoryFileId());
        List<WalletEntryFileDB_Pojo> allEntriesInfoFromDatabase = walletEntriesDAL.getAllEntriesInfoFromDatabase(sb2.toString());
        if (allEntriesInfoFromDatabase.size() > 0) {
            for (WalletEntryFileDB_Pojo walletEntryFileDB_Pojo : allEntriesInfoFromDatabase) {
                Iterator it = arrayList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = false;
                        break;
                    }
                    if (new File(walletEntryFileDB_Pojo.getEntryFileLocation()).getName().contentEquals(new File((String) it.next()).getName())) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(walletEntryFileDB_Pojo.getEntryFileLocation(), Boolean.valueOf(false));
                }
            }
        }
        return hashtable;
    }

    private int GetWalletStatus(String str, int i, int i2) {
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
        constants.getClass();
        sb.append("WalletCategoriesFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" AND ");
        constants.getClass();
        sb.append("WalletCategoriesFileName");
        sb.append(" = '");
        sb.append(str);
        sb.append("'");
        if (walletCategoriesDAL.getCategoryInfoFromDatabase(sb.toString()).getCategoryFileName() == null) {
            return CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i == 0 && i2 == 0) {
            return CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal();
        }
        return CloudFolderStatus.CloudAndPhoneNotSync.ordinal();
    }

    private ArrayList<BackupCloudEnt> GetWalletPhoneFolders(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
        constants.getClass();
        sb.append("WalletCategoriesFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        for (WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo : walletCategoriesDAL.getAllCategoriesInfoFromDatabase(sb.toString())) {
            if (!arrayList.contains(walletCategoriesFileDB_Pojo.getCategoryFileName())) {
                BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
                backupCloudEnt.SetFolderName(walletCategoriesFileDB_Pojo.getCategoryFileName());
                backupCloudEnt.SetPath(walletCategoriesFileDB_Pojo.getCategoryFileLocation());
                backupCloudEnt.SetDropboxType(this.DownloadType);
                backupCloudEnt.SetUploadPath(GetPhoneFolderFiles(walletCategoriesFileDB_Pojo.getCategoryFileName()));
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDownloadCount(0);
                backupCloudEnt.SetStatus(CloudFolderStatus.OnlyPhone.ordinal());
                arrayList2.add(backupCloudEnt);
            }
        }
        return arrayList2;
    }

    private Hashtable<String, Boolean> GetWalletPhoneFolderFiles(String str) {
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        WalletEntriesDAL walletEntriesDAL = new WalletEntriesDAL(this.context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
        constants.getClass();
        sb.append("WalletCategoriesFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" AND ");
        constants.getClass();
        sb.append("WalletCategoriesFileName");
        sb.append(" = '");
        sb.append(str);
        sb.append("'");
        WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL.getCategoryInfoFromDatabase(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        constants.getClass();
        sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
        constants.getClass();
        sb2.append("WalletEntryFileIsDecoy");
        sb2.append(" = ");
        sb2.append(SecurityLocksCommon.IsFakeAccount);
        sb2.append(" AND ");
        constants.getClass();
        sb2.append("WalletCategoriesFileId");
        sb2.append(" = ");
        sb2.append(categoryInfoFromDatabase.getCategoryFileId());
        for (WalletEntryFileDB_Pojo entryFileLocation : walletEntriesDAL.getAllEntriesInfoFromDatabase(sb2.toString())) {
            hashtable.put(entryFileLocation.getEntryFileLocation(), Boolean.valueOf(false));
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetDownloadDocumentsPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        documentFolderDAL.OpenRead();
        DocumentFolder GetFolder = documentFolderDAL.GetFolder(str);
        DocumentDAL documentDAL = new DocumentDAL(this.context);
        documentDAL.OpenRead();
        List GetDocuments = documentDAL.GetDocuments(GetFolder.getId(), 0);
        if (arrayList.size() > 0) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                String str2 = (String) it.next();
                Iterator it2 = GetDocuments.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        z = false;
                        break;
                    }
                    if (new File(str2).getName().contentEquals(((DocumentsEnt) it2.next()).getDocumentName())) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(str2, Boolean.valueOf(false));
                }
            }
        }
        documentFolderDAL.close();
        documentDAL.close();
        return hashtable;
    }

    private Hashtable<String, Boolean> GetUploadDocumentsPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        documentFolderDAL.OpenRead();
        DocumentFolder GetFolder = documentFolderDAL.GetFolder(str);
        DocumentDAL documentDAL = new DocumentDAL(this.context);
        documentDAL.OpenRead();
        List<DocumentsEnt> GetDocuments = documentDAL.GetDocuments(GetFolder.getId(), 0);
        if (GetDocuments.size() > 0) {
            for (DocumentsEnt documentsEnt : GetDocuments) {
                Iterator it = arrayList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = false;
                        break;
                    }
                    if (documentsEnt.getDocumentName().contentEquals(new File((String) it.next()).getName())) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(documentsEnt.getFolderLockDocumentLocation(), Boolean.valueOf(false));
                }
            }
        }
        documentFolderDAL.close();
        documentDAL.close();
        return hashtable;
    }

    private int GetDocumentStatus(String str, int i, int i2) {
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        documentFolderDAL.OpenRead();
        DocumentFolder GetFolder = documentFolderDAL.GetFolder(str);
        documentFolderDAL.close();
        if (GetFolder.getFolderName() == null) {
            return CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i == 0 && i2 == 0) {
            return CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal();
        }
        return CloudFolderStatus.CloudAndPhoneNotSync.ordinal();
    }

    private ArrayList<BackupCloudEnt> GetDocumentPhoneFolders(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        documentFolderDAL.OpenRead();
        for (DocumentFolder documentFolder : documentFolderDAL.GetFolders(0)) {
            if (!arrayList.contains(documentFolder.getFolderName())) {
                BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
                backupCloudEnt.SetFolderName(documentFolder.getFolderName());
                backupCloudEnt.SetPath(documentFolder.getFolderLocation());
                backupCloudEnt.SetDropboxType(this.DownloadType);
                backupCloudEnt.SetUploadPath(GetPhoneFolderFiles(documentFolder.getFolderName()));
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDownloadCount(0);
                backupCloudEnt.SetStatus(CloudFolderStatus.OnlyPhone.ordinal());
                arrayList2.add(backupCloudEnt);
            }
        }
        return arrayList2;
    }

    private Hashtable<String, Boolean> GetDocumentPhoneFolderFiles(String str) {
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        documentFolderDAL.OpenRead();
        DocumentFolder GetFolder = documentFolderDAL.GetFolder(str);
        DocumentDAL documentDAL = new DocumentDAL(this.context);
        documentDAL.OpenRead();
        List<DocumentsEnt> GetDocuments = documentDAL.GetDocuments(GetFolder.getId(), 0);
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        for (DocumentsEnt folderLockDocumentLocation : GetDocuments) {
            hashtable.put(folderLockDocumentLocation.getFolderLockDocumentLocation(), Boolean.valueOf(false));
        }
        return hashtable;
    }

    private void AddFolderInLocal(int i, String str) {
        if (DropboxType.Photos.ordinal() == i) {
            AddPhotoToDatabase(str);
        } else if (DropboxType.Videos.ordinal() == i) {
            AddVideoToDatabase(str);
        } else if (DropboxType.Documents.ordinal() == i) {
            AddDocumentToDatabase(str);
        } else if (DropboxType.Notes.ordinal() == i) {
            AddNoteToDatabase(str);
        } else if (DropboxType.Wallet.ordinal() == i) {
            AddWalletToDatabase(str);
        } else if (DropboxType.Audio.ordinal() == i) {
            AddMusicToDatabase(str);
        }
    }

    private void AddPhotoToDatabase(String str) {
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenWrite();
        PhotoAlbum photoAlbum = new PhotoAlbum();
        photoAlbum.setAlbumName(new File(str).getName());
        photoAlbum.setAlbumLocation(new File(str).getAbsolutePath());
        photoAlbumDAL.AddPhotoAlbum(photoAlbum);
        photoAlbumDAL.close();
    }

    private void AddVideoToDatabase(String str) {
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        videoAlbumDAL.OpenWrite();
        VideoAlbum videoAlbum = new VideoAlbum();
        videoAlbum.setAlbumName(new File(str).getName());
        videoAlbum.setAlbumLocation(new File(str).getParent());
        videoAlbumDAL.AddVideoAlbum(videoAlbum);
        videoAlbumDAL.close();
    }

    private void AddDocumentToDatabase(String str) {
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        documentFolderDAL.OpenWrite();
        DocumentFolder documentFolder = new DocumentFolder();
        documentFolder.setFolderName(new File(str).getName());
        documentFolder.setFolderLocation(new File(str).getParent());
        documentFolderDAL.AddDocumentFolder(documentFolder);
        documentFolderDAL.close();
    }

    private void AddNoteToDatabase(String str) {
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(this.context);
        NotesFolderDB_Pojo notesFolderDB_Pojo = new NotesFolderDB_Pojo();
        String currentDate = new NotesCommon().getCurrentDate();
        notesFolderDB_Pojo.setNotesFolderName(new File(str).getName());
        notesFolderDB_Pojo.setNotesFolderLocation(new File(str).getParent());
        notesFolderDB_Pojo.setNotesFolderFilesSortBy(0);
        notesFolderDB_Pojo.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
        notesFolderDB_Pojo.setNotesFolderCreatedDate(currentDate);
        notesFolderDB_Pojo.setNotesFolderModifiedDate(currentDate);
        notesFolderDAL.addNotesFolderInfoInDatabase(notesFolderDB_Pojo);
    }

    private void AddWalletToDatabase(String str) {
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo = new WalletCategoriesFileDB_Pojo();
        String currentDate = new NotesCommon().getCurrentDate();
        walletCategoriesFileDB_Pojo.setCategoryFileName(new File(str).getName());
        walletCategoriesFileDB_Pojo.setCategoryFileLocation(new File(str).getParent());
        walletCategoriesFileDB_Pojo.setCategoryFileSortBy(0);
        walletCategoriesFileDB_Pojo.setCategoryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
        walletCategoriesFileDB_Pojo.setCategoryFileCreatedDate(currentDate);
        walletCategoriesFileDB_Pojo.setCategoryFileModifiedDate(currentDate);
        walletCategoriesDAL.addWalletCategoriesInfoInDatabase(walletCategoriesFileDB_Pojo);
    }

    private void AddMusicToDatabase(String str) {
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenWrite();
        AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
        audioPlayListEnt.setPlayListName(new File(str).getName());
        audioPlayListEnt.setPlayListLocation(new File(str).getParent());
        audioPlayListDAL.AddAudioPlayList(audioPlayListEnt);
        audioPlayListDAL.close();
    }
}
