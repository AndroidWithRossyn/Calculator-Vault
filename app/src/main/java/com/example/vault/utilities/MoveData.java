package com.example.vault.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.example.vault.common.Constants;
import com.example.vault.documents.util.DocumentDAL;
import com.example.vault.documents.model.DocumentFolder;
import com.example.vault.documents.util.DocumentFolderDAL;
import com.example.vault.documents.model.DocumentsEnt;
import com.example.vault.notes.model.NotesFileDB_Pojo;
import com.example.vault.notes.util.NotesFilesDAL;
import com.example.vault.notes.util.NotesFolderDAL;
import com.example.vault.notes.model.NotesFolderDB_Pojo;
import com.example.vault.photo.model.Photo;
import com.example.vault.photo.model.PhotoAlbum;
import com.example.vault.photo.util.PhotoAlbumDAL;
import com.example.vault.photo.util.PhotoDAL;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.video.model.Video;
import com.example.vault.video.model.VideoAlbum;
import com.example.vault.video.util.VideoAlbumDAL;
import com.example.vault.video.util.VideoDAL;
import com.example.vault.wallet.util.WalletCategoriesDAL;
import com.example.vault.wallet.model.WalletCategoriesFileDB_Pojo;
import com.example.vault.wallet.util.WalletEntriesDAL;
import com.example.vault.wallet.model.WalletEntryFileDB_Pojo;

public class MoveData {
    static SharedPreferences DataTransferPrefs;
    static Editor DataTransferprefsEditor;
    private static MoveData instance;
    private ArrayList<String> allFiles = new ArrayList<>();
    Constants constants = new Constants();
    Context context;
    DocumentDAL documentDAL;
    DocumentFolderDAL documentFolderDAL;
    private List<DocumentsEnt> documentsEnts = new ArrayList();
    private List<NotesFileDB_Pojo> notesDbList = new ArrayList();
    NotesFilesDAL notesFilesDAL;
    NotesFolderDAL notesFolderDAL;
    PhotoAlbumDAL photoAlbumDAL;
    PhotoDAL photoDAL;
    private List<Photo> photos = new ArrayList();
    VideoAlbumDAL videoAlbumDAL;
    VideoDAL videoDAL;
    private List<Video> videos = new ArrayList();
    WalletCategoriesDAL walletCategoriesDAL;
    WalletEntriesDAL walletEntriesDAL;
    private List<WalletEntryFileDB_Pojo> walletEntriesList = new ArrayList();

    private MoveData(Context context2) {
        this.context = context2;
    }

    public static MoveData getInstance(Context context2) {
        if (instance == null) {
            instance = new MoveData(context2);
            DataTransferPrefs = context2.getSharedPreferences("DataTransferStatus", 0);
            DataTransferprefsEditor = DataTransferPrefs.edit();
        }
        return instance;
    }

    public void MoveDataToORFromCard() {
        DataMove();
    }

    public void MoveDataToORFromCardFromSetting() {
        DataMoveFromSetting();
    }

    public void GetDataFromDataBase() {
        this.photoDAL = new PhotoDAL(this.context);
        this.photoDAL.OpenRead();
        this.photos = this.photoDAL.GetPhotos();
        this.photoDAL.close();
        this.videoDAL = new VideoDAL(this.context);
        this.videoDAL.OpenRead();
        this.videos = this.videoDAL.GetVideos();
        this.videoDAL.close();
        this.documentDAL = new DocumentDAL(this.context);
        this.documentDAL.OpenRead();
        this.documentsEnts = this.documentDAL.GetAllDocuments();
        this.documentDAL.close();
        this.notesFilesDAL = new NotesFilesDAL(this.context);
        this.walletEntriesDAL = new WalletEntriesDAL(this.context);
        NotesFilesDAL notesFilesDAL2 = this.notesFilesDAL;
        StringBuilder sb = new StringBuilder();
        this.constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
        this.constants.getClass();
        sb.append("NotesFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        this.notesDbList = notesFilesDAL2.getAllNotesFileInfoFromDatabase(sb.toString());
        WalletEntriesDAL walletEntriesDAL2 = this.walletEntriesDAL;
        StringBuilder sb2 = new StringBuilder();
        this.constants.getClass();
        sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
        this.constants.getClass();
        sb2.append("WalletEntryFileIsDecoy");
        sb2.append(" = ");
        sb2.append(SecurityLocksCommon.IsFakeAccount);
        this.walletEntriesList = walletEntriesDAL2.getAllEntriesInfoFromDatabase(sb2.toString());
    }

    public ArrayList<String> GetAllFilesPath() {
        this.allFiles.clear();
        for (Photo folderLockPhotoLocation : this.photos) {
            this.allFiles.add(folderLockPhotoLocation.getFolderLockPhotoLocation());
        }
        for (Video folderLockVideoLocation : this.videos) {
            this.allFiles.add(folderLockVideoLocation.getFolderLockVideoLocation());
        }
        for (DocumentsEnt folderLockDocumentLocation : this.documentsEnts) {
            this.allFiles.add(folderLockDocumentLocation.getFolderLockDocumentLocation());
        }
        for (NotesFileDB_Pojo notesFileLocation : this.notesDbList) {
            this.allFiles.add(notesFileLocation.getNotesFileLocation());
        }
        for (WalletEntryFileDB_Pojo entryFileLocation : this.walletEntriesList) {
            this.allFiles.add(entryFileLocation.getEntryFileLocation());
        }
        return this.allFiles;
    }

    private void DataMove() {
        try {
            if (!DataTransferPrefs.getBoolean("isPhotoTransferComplete", false)) {
                PhotoMove();
            }
        } catch (Exception e) {
            Log.e("Photo Move Exception", e.getMessage(), e);
            e.printStackTrace();
        }
        try {
            if (!DataTransferPrefs.getBoolean("isVideoTransferComplete", false)) {
                VideoMove();
            }
        } catch (Exception e2) {
            Log.e("Video Move Exception", e2.getMessage(), e2);
            e2.printStackTrace();
        }
        try {
            if (!DataTransferPrefs.getBoolean("isDocumentTransferComplete", false)) {
                VideoMove();
            }
        } catch (Exception e3) {
            Log.e("Document Move Exception", e3.getMessage(), e3);
            e3.printStackTrace();
        }
        try {
            if (!DataTransferPrefs.getBoolean("isNotesTransferComplete", false)) {
                NotesMove();
            }
        } catch (Exception e4) {
            Log.e("Document Move Exception", e4.getMessage(), e4);
            e4.printStackTrace();
        }
        try {
            if (!DataTransferPrefs.getBoolean("isWalletTransferComplete", false)) {
                WalletMove();
            }
        } catch (Exception e5) {
            Log.e("Document Move Exception", e5.getMessage(), e5);
            e5.printStackTrace();
        }
    }

    private void DataMoveFromSetting() {
        PhotoMove();
        VideoMove();
        DocumentMove();
        NotesMove();
        WalletMove();
    }

    private void PhotoMove() {
        String str;
        boolean z = true;
        for (Photo photo : this.photos) {
            StorageOptionsCommon.IsApphasDataforTransfer = true;
            this.photoAlbumDAL = new PhotoAlbumDAL(this.context);
            this.photoAlbumDAL.OpenWrite();
            this.photoDAL.OpenWrite();
            PhotoAlbum GetAlbumById = this.photoAlbumDAL.GetAlbumById(Integer.toString(photo.getAlbumId()));
            File file = new File(photo.getFolderLockPhotoLocation());
            if (!photo.getFolderLockPhotoLocation().contains(StorageOptionsCommon.STORAGEPATH)) {
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append(StorageOptionsCommon.PHOTOS);
                sb.append(GetAlbumById.getAlbumName());
                String sb2 = sb.toString();
                try {
                    Utilities.RecoveryHideFileSDCard(this.context, file, new File(sb2));
                    if (!photo.getPhotoName().contains("#")) {
                        str = Utilities.ChangeFileExtention(photo.getPhotoName());
                    } else {
                        str = photo.getPhotoName();
                    }
                    if (sb2.length() > 0) {
                        this.photoAlbumDAL.UpdateAlbumLocation(photo.getAlbumId(), sb2);
                        PhotoDAL photoDAL2 = this.photoDAL;
                        int id = photo.getId();
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(sb2);
                        sb3.append("/");
                        sb3.append(str);
                        photoDAL2.UpdatePhotosLocation(id, sb3.toString());
                    }
                } catch (IOException e) {
                    Log.e("Photo Move Exception", e.getMessage(), e);
                    e.printStackTrace();
                    z = false;
                }
            }
        }
        PhotoAlbumDAL photoAlbumDAL2 = this.photoAlbumDAL;
        if (!(photoAlbumDAL2 == null || this.photoDAL == null)) {
            photoAlbumDAL2.close();
            this.photoDAL.close();
        }
        DataTransferprefsEditor.putBoolean("isPhotoTransferComplete", z);
        DataTransferprefsEditor.commit();
    }

    private void VideoMove() {
        String str;
        String str2;
        boolean z = true;
        for (Video video : this.videos) {
            StorageOptionsCommon.IsApphasDataforTransfer = true;
            this.videoAlbumDAL = new VideoAlbumDAL(this.context);
            this.videoAlbumDAL.OpenWrite();
            this.videoDAL.OpenWrite();
            VideoAlbum GetAlbumById = this.videoAlbumDAL.GetAlbumById(video.getAlbumId());
            File file = new File(video.getFolderLockVideoLocation());
            String str3 = video.getthumbnail_video_location();
            String FileName = Utilities.FileName(str3);
            if (!video.getFolderLockVideoLocation().contains(StorageOptionsCommon.STORAGEPATH)) {
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append(StorageOptionsCommon.VIDEOS);
                sb.append(GetAlbumById.getAlbumName());
                String sb2 = sb.toString();
                try {
                    Utilities.RecoveryHideFileSDCard(this.context, file, new File(sb2));
                    if (FileName.contains("#")) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(StorageOptionsCommon.STORAGEPATH);
                        sb3.append(StorageOptionsCommon.VIDEOS);
                        sb3.append(GetAlbumById.getAlbumName());
                        sb3.append("/VideoThumnails/");
                        sb3.append(FileName);
                        str = sb3.toString();
                    } else {
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append(StorageOptionsCommon.STORAGEPATH);
                        sb4.append(StorageOptionsCommon.VIDEOS);
                        sb4.append(GetAlbumById.getAlbumName());
                        sb4.append("/VideoThumnails/");
                        sb4.append(FileName.substring(0, FileName.lastIndexOf(".")));
                        sb4.append("#jpg");
                        str = sb4.toString();
                    }
                    try {
                        Utilities.UnHideThumbnail(this.context, str3, str);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!video.getVideoName().contains("#")) {
                        str2 = Utilities.ChangeFileExtention(video.getVideoName());
                    } else {
                        str2 = video.getVideoName();
                    }
                    if (sb2.length() > 0) {
                        this.videoAlbumDAL.UpdateAlbumLocation(video.getAlbumId(), sb2);
                        VideoDAL videoDAL2 = this.videoDAL;
                        int id = video.getId();
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append(sb2);
                        sb5.append("/");
                        sb5.append(str2);
                        videoDAL2.UpdateVideosLocation(id, sb5.toString(), str);
                    }
                } catch (IOException e2) {
                    Log.e("Video Move Exception", e2.getMessage(), e2);
                    e2.printStackTrace();
                    z = false;
                }
            }
        }
        VideoAlbumDAL videoAlbumDAL2 = this.videoAlbumDAL;
        if (videoAlbumDAL2 != null) {
            videoAlbumDAL2.close();
            VideoDAL videoDAL3 = this.videoDAL;
            if (videoDAL3 != null) {
                videoDAL3.close();
            }
        }
        DataTransferprefsEditor.putBoolean("isVideoTransferComplete", z);
        DataTransferprefsEditor.commit();
    }

    private void DocumentMove() {
        String str;
        boolean z = true;
        for (DocumentsEnt documentsEnt : this.documentsEnts) {
            StorageOptionsCommon.IsApphasDataforTransfer = true;
            this.documentFolderDAL = new DocumentFolderDAL(this.context);
            this.documentFolderDAL.OpenWrite();
            this.documentDAL.OpenWrite();
            DocumentFolder GetFolderById = this.documentFolderDAL.GetFolderById(Integer.toString(documentsEnt.getFolderId()));
            File file = new File(documentsEnt.getFolderLockDocumentLocation());
            if (!documentsEnt.getFolderLockDocumentLocation().contains(StorageOptionsCommon.STORAGEPATH)) {
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append(StorageOptionsCommon.DOCUMENTS);
                sb.append(GetFolderById.getFolderName());
                String sb2 = sb.toString();
                try {
                    Utilities.RecoveryHideFileSDCard(this.context, file, new File(sb2));
                    if (!documentsEnt.getDocumentName().contains("#")) {
                        str = Utilities.ChangeFileExtention(documentsEnt.getDocumentName());
                    } else {
                        str = documentsEnt.getDocumentName();
                    }
                    if (sb2.length() > 0) {
                        this.documentFolderDAL.UpdateFolderLocation(documentsEnt.getFolderId(), sb2);
                        DocumentDAL documentDAL2 = this.documentDAL;
                        int id = documentsEnt.getId();
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(sb2);
                        sb3.append("/");
                        sb3.append(str);
                        documentDAL2.UpdateDocumentsLocation(id, sb3.toString());
                    }
                } catch (IOException e) {
                    Log.e("Document Move Exception", e.getMessage(), e);
                    e.printStackTrace();
                    z = false;
                }
            }
        }
        DocumentFolderDAL documentFolderDAL2 = this.documentFolderDAL;
        if (!(documentFolderDAL2 == null || this.photoDAL == null)) {
            documentFolderDAL2.close();
            this.photoDAL.close();
        }
        DataTransferprefsEditor.putBoolean("isDocumentTransferComplete", z);
        DataTransferprefsEditor.commit();
    }

    public void NotesMove() {
        this.notesFolderDAL = new NotesFolderDAL(this.context);
        boolean z = true;
        for (NotesFileDB_Pojo notesFileDB_Pojo : this.notesDbList) {
            StorageOptionsCommon.IsApphasDataforTransfer = true;
            NotesFolderDAL notesFolderDAL2 = this.notesFolderDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
            this.constants.getClass();
            sb.append("NotesFolderId");
            sb.append(" = ");
            sb.append(notesFileDB_Pojo.getNotesFileFolderId());
            sb.append(" AND ");
            this.constants.getClass();
            sb.append("NotesFolderIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            NotesFolderDB_Pojo notesFolderInfoFromDatabase = notesFolderDAL2.getNotesFolderInfoFromDatabase(sb.toString());
            File file = new File(notesFileDB_Pojo.getNotesFileLocation());
            if (!notesFileDB_Pojo.getNotesFileLocation().contains(StorageOptionsCommon.STORAGEPATH)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(StorageOptionsCommon.STORAGEPATH);
                sb2.append(StorageOptionsCommon.NOTES);
                sb2.append(notesFolderInfoFromDatabase.getNotesFolderName());
                String sb3 = sb2.toString();
                StringBuilder sb4 = new StringBuilder();
                sb4.append(sb3);
                sb4.append(File.separator);
                sb4.append(notesFileDB_Pojo.getNotesFileName());
                sb4.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
                String sb5 = sb4.toString();
                File file2 = new File(sb3);
                File file3 = new File(sb5);
                try {
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    String RecoveryEntryFile = Utilities.RecoveryEntryFile(this.context, file, file3);
                    if (RecoveryEntryFile.length() > 0) {
                        notesFileDB_Pojo.setNotesFileLocation(RecoveryEntryFile);
                        notesFolderInfoFromDatabase.setNotesFolderLocation(sb3);
                        NotesFolderDAL notesFolderDAL3 = this.notesFolderDAL;
                        this.constants.getClass();
                        notesFolderDAL3.updateNotesFolderLocationInDatabase(notesFolderInfoFromDatabase, "NotesFolderId", String.valueOf(notesFolderInfoFromDatabase.getNotesFolderId()));
                        NotesFilesDAL notesFilesDAL2 = this.notesFilesDAL;
                        this.constants.getClass();
                        notesFilesDAL2.updateNotesFileLocationInDatabase(notesFileDB_Pojo, "NotesFileId", String.valueOf(notesFileDB_Pojo.getNotesFileId()));
                    }
                } catch (IOException e) {
                    Log.e("Note Move Exception", e.getMessage(), e);
                    e.printStackTrace();
                    z = false;
                }
            }
        }
        DataTransferprefsEditor.putBoolean("isNotesTransferComplete", z);
        DataTransferprefsEditor.commit();
    }

    public void WalletMove() {
        this.walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        boolean z = true;
        for (WalletEntryFileDB_Pojo walletEntryFileDB_Pojo : this.walletEntriesList) {
            StorageOptionsCommon.IsApphasDataforTransfer = true;
            WalletCategoriesDAL walletCategoriesDAL2 = this.walletCategoriesDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
            this.constants.getClass();
            sb.append("WalletCategoriesFileIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            sb.append(" AND ");
            this.constants.getClass();
            sb.append("WalletCategoriesFileId");
            sb.append(" = ");
            sb.append(walletEntryFileDB_Pojo.getCategoryId());
            WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL2.getCategoryInfoFromDatabase(sb.toString());
            File file = new File(walletEntryFileDB_Pojo.getEntryFileLocation());
            if (!walletEntryFileDB_Pojo.getEntryFileLocation().contains(StorageOptionsCommon.STORAGEPATH)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(StorageOptionsCommon.STORAGEPATH);
                sb2.append(StorageOptionsCommon.WALLET);
                sb2.append(categoryInfoFromDatabase.getCategoryFileName());
                String sb3 = sb2.toString();
                StringBuilder sb4 = new StringBuilder();
                sb4.append(sb3);
                sb4.append(File.separator);
                sb4.append(StorageOptionsCommon.ENTRY);
                sb4.append(walletEntryFileDB_Pojo.getEntryFileName());
                sb4.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
                String sb5 = sb4.toString();
                File file2 = new File(sb3);
                File file3 = new File(sb5);
                try {
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    String RecoveryEntryFile = Utilities.RecoveryEntryFile(this.context, file, file3);
                    if (RecoveryEntryFile.length() > 0) {
                        walletEntryFileDB_Pojo.setEntryFileLocation(RecoveryEntryFile);
                        WalletEntriesDAL walletEntriesDAL2 = this.walletEntriesDAL;
                        this.constants.getClass();
                        walletEntriesDAL2.updateEntryLocationInDatabase(walletEntryFileDB_Pojo, "WalletEntryFileId", String.valueOf(walletEntryFileDB_Pojo.getEntryFileId()));
                    }
                } catch (IOException e) {
                    Log.e("Note Move Exception", e.getMessage(), e);
                    e.printStackTrace();
                    z = false;
                }
            }
        }
        DataTransferprefsEditor.putBoolean("isNotesTransferComplete", z);
        DataTransferprefsEditor.commit();
    }
}
