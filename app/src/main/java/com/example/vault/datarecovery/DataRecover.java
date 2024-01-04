package com.example.vault.datarecovery;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import com.example.vault.common.Constants;
import com.example.vault.documents.util.DocumentDAL;
import com.example.vault.documents.model.DocumentFolder;
import com.example.vault.documents.util.DocumentFolderDAL;
import com.example.vault.documents.model.DocumentsEnt;
import com.example.vault.notes.model.NotesFileDB_Pojo;
import com.example.vault.notes.util.NotesFilesDAL;
import com.example.vault.notes.util.NotesFolderDAL;
import com.example.vault.notes.model.NotesFolderDB_Pojo;
import com.example.vault.notes.util.ReadNoteFromXML;
import com.example.vault.photo.model.Photo;
import com.example.vault.photo.model.PhotoAlbum;
import com.example.vault.photo.util.PhotoAlbumDAL;
import com.example.vault.photo.util.PhotoDAL;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;
import com.example.vault.video.model.Video;
import com.example.vault.video.model.VideoAlbum;
import com.example.vault.video.util.VideoAlbumDAL;
import com.example.vault.video.util.VideoDAL;
import com.example.vault.wallet.util.WalletCategoriesDAL;
import com.example.vault.wallet.model.WalletCategoriesFileDB_Pojo;
import com.example.vault.wallet.model.WalletCategoriesPojo;
import com.example.vault.wallet.util.WalletCommon;
import com.example.vault.wallet.util.WalletEntriesDAL;
import com.example.vault.wallet.model.WalletEntryFileDB_Pojo;

public class DataRecover {
    Context context;

    public void RecoverALLData(Context context2) {
        this.context = context2;
        if (!StorageOptionsCommon.IsDeviceHaveMoreThenOneStorage) {
            RecoverPhotos(StorageOptionsCommon.STORAGEPATH, true);
            RecoverVideos(StorageOptionsCommon.STORAGEPATH, true);
            RecoverDocuments(StorageOptionsCommon.STORAGEPATH, true);
            RecoverNotes(StorageOptionsCommon.STORAGEPATH, true);
            RecoverWalletEntries(StorageOptionsCommon.STORAGEPATH, true);
        } else if (!StorageOptionsCommon.STORAGEPATH.equals(StorageOptionsCommon.STORAGEPATH_1)) {
            RecoverPhotos(StorageOptionsCommon.STORAGEPATH, true);
            RecoverPhotos(StorageOptionsCommon.STORAGEPATH_1, false);
            RecoverVideos(StorageOptionsCommon.STORAGEPATH, true);
            RecoverVideos(StorageOptionsCommon.STORAGEPATH_1, false);
            RecoverDocuments(StorageOptionsCommon.STORAGEPATH, true);
            RecoverDocuments(StorageOptionsCommon.STORAGEPATH_1, false);
            RecoverNotes(StorageOptionsCommon.STORAGEPATH, true);
            RecoverNotes(StorageOptionsCommon.STORAGEPATH_1, false);
            RecoverWalletEntries(StorageOptionsCommon.STORAGEPATH, true);
            RecoverWalletEntries(StorageOptionsCommon.STORAGEPATH_1, false);
        } else if (!StorageOptionsCommon.STORAGEPATH.equals(StorageOptionsCommon.STORAGEPATH_2)) {
            RecoverPhotos(StorageOptionsCommon.STORAGEPATH, true);
            RecoverPhotos(StorageOptionsCommon.STORAGEPATH_2, false);
            RecoverVideos(StorageOptionsCommon.STORAGEPATH, true);
            RecoverVideos(StorageOptionsCommon.STORAGEPATH_2, false);
            RecoverDocuments(StorageOptionsCommon.STORAGEPATH, true);
            RecoverDocuments(StorageOptionsCommon.STORAGEPATH_2, false);
            RecoverNotes(StorageOptionsCommon.STORAGEPATH, true);
            RecoverNotes(StorageOptionsCommon.STORAGEPATH_2, false);
            RecoverWalletEntries(StorageOptionsCommon.STORAGEPATH, true);
            RecoverWalletEntries(StorageOptionsCommon.STORAGEPATH_2, false);
        }
    }

    private void RecoverPhotos(String str, boolean z) {

        String str2;
        boolean z2;
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(StorageOptionsCommon.PHOTOS);
        sb.append("/");
        File file = new File(sb.toString());
        if (file.exists()) {
            boolean z3 = false;
            for (File file2 : file.listFiles()) {
                if (file2.isDirectory()) {
                    for (File file3 : file2.listFiles()) {
                        if (file3.isFile()) {
                            String absolutePath = file3.getAbsolutePath();
                            if (!z) {
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append(StorageOptionsCommon.STORAGEPATH);
                                sb2.append(StorageOptionsCommon.PHOTOS);
                                sb2.append("/");
                                sb2.append(file2.getName());
                                try {
                                    absolutePath = Utilities.RecoveryHideFileSDCard(this.context, file3, new File(sb2.toString()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    z3 = true;
                                }
                            }
                            if (!z3) {
                                if (file3.getName().contains("#")) {
                                    str2 = Utilities.ChangeFileExtentionToOrignal(file3.getName());
                                } else {
                                    str2 = file3.getName();
                                }
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append(StorageOptionsCommon.STORAGEPATH);
                                sb3.append("Calculator Vault Unhide Data/");
                                sb3.append(str2);
                                String sb4 = sb3.toString();
                                PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
                                photoAlbumDAL.OpenRead();
                                int IfAlbumNameExistReturnId = photoAlbumDAL.IfAlbumNameExistReturnId(file2.getName());
                                if (IfAlbumNameExistReturnId == 0) {
                                    AddPhotoAlbumToDatabase(file2.getName());
                                    IfAlbumNameExistReturnId = photoAlbumDAL.GetLastAlbumId();
                                }
                                photoAlbumDAL.close();
                                PhotoDAL photoDAL = new PhotoDAL(this.context);
                                photoDAL.OpenRead();
                                if (absolutePath.contains("'")) {
                                    z2 = photoDAL.IsFileAlreadyExist(absolutePath.replaceAll("'", "''"));
                                } else {
                                    z2 = photoDAL.IsFileAlreadyExist(absolutePath);
                                }
                                photoDAL.close();
                                if (!z2) {
                                    StorageOptionsCommon.IsUserHasDataToRecover = true;
                                    AddPhotoToDatabase(file3.getName(), absolutePath, sb4, IfAlbumNameExistReturnId);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void RecoverVideos(String str, boolean z) {

        int i;
        String str2;
        boolean z2;
        String str3;
        int i2;
        boolean z3;
        String str4 = str;
        StringBuilder sb = new StringBuilder();
        sb.append(str4);
        sb.append(StorageOptionsCommon.VIDEOS);
        File file = new File(sb.toString());
        if (file.exists()) {
            boolean z4 = false;
            for (File file2 : file.listFiles()) {
                if (file2.isDirectory() && !file2.getName().equals("VideoThumnails")) {
                    File[] listFiles2 = file2.listFiles();
                    int length = listFiles2.length;
                    int i3 = 0;
                    while (i3 < length) {
                        File file3 = listFiles2[i3];
                        if (file3.isFile()) {
                            String absolutePath = file3.getAbsolutePath();
                            if (!z) {
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append(StorageOptionsCommon.STORAGEPATH);
                                sb2.append(StorageOptionsCommon.VIDEOS);
                                sb2.append(file2.getName());
                                sb2.append("/");
                                String sb3 = sb2.toString();
                                try {
                                    absolutePath = Utilities.RecoveryHideFileSDCard(this.context, file3, new File(sb3));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    z4 = true;
                                }
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append(sb3);
                                sb4.append("VideoThumnails/");
                                File file4 = new File(sb4.toString());
                                if (!file4.exists()) {
                                    file4.mkdirs();
                                }
                                String name = file3.getName();
                                StringBuilder sb5 = new StringBuilder();
                                sb5.append(str4);
                                sb5.append(StorageOptionsCommon.VIDEOS);
                                sb5.append(file2.getName());
                                sb5.append("/VideoThumnails/thumbnil-");
                                String str5 = absolutePath;
                                sb5.append(name.substring(0, name.lastIndexOf("#")));
                                sb5.append("#jpg");
                                String sb6 = sb5.toString();
                                StringBuilder sb7 = new StringBuilder();
                                sb7.append(sb3);
                                sb7.append("VideoThumnails/thumbnil-");
                                sb7.append(file3.getName().substring(0, file3.getName().lastIndexOf("#")));
                                sb7.append("#jpg");
                                try {
                                    Utilities.UnHideThumbnail(this.context, sb6, sb7.toString());
                                    str2 = str5;
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                    str2 = str5;
                                    z4 = true;
                                }
                            } else {
                                str2 = absolutePath;
                            }
                            if (!z4) {
                                if (file3.getName().contains("#")) {
                                    str3 = Utilities.ChangeFileExtentionToOrignal(file3.getName());
                                } else {
                                    str3 = file3.getName();
                                }
                                StringBuilder sb8 = new StringBuilder();
                                sb8.append(StorageOptionsCommon.STORAGEPATH);
                                sb8.append("Calculator Vault Unhide Data/");
                                sb8.append(str3);
                                String sb9 = sb8.toString();
                                VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
                                videoAlbumDAL.OpenRead();
                                int IfAlbumNameExistReturnId = videoAlbumDAL.IfAlbumNameExistReturnId(file2.getName());
                                if (IfAlbumNameExistReturnId == 0) {
                                    AddVideoAlbumToDatabase(file2.getName());
                                    if (!new File(file2.getAbsolutePath()).exists()) {
                                        file2.mkdirs();
                                    }
                                    i2 = videoAlbumDAL.GetLastAlbumId();
                                } else {
                                    i2 = IfAlbumNameExistReturnId;
                                }
                                videoAlbumDAL.close();
                                VideoDAL videoDAL = new VideoDAL(this.context);
                                videoDAL.OpenRead();
                                if (str2.contains("'")) {
                                    z2 = z4;
                                    z3 = videoDAL.IsFileAlreadyExist(str2.replaceAll("'", "''"));
                                } else {
                                    z2 = z4;
                                    z3 = videoDAL.IsFileAlreadyExist(str2);
                                }
                                videoDAL.close();
                                if (!z3) {
                                    StringBuilder sb10 = new StringBuilder();
                                    sb10.append(StorageOptionsCommon.STORAGEPATH);
                                    sb10.append(StorageOptionsCommon.VIDEOS);
                                    sb10.append(file2.getName());
                                    sb10.append("/VideoThumnails/thumbnil-");
                                    int i4 = i3;
                                    sb10.append(file3.getName().substring(0, file3.getName().lastIndexOf("#")));
                                    sb10.append("#jpg");
                                    String sb11 = sb10.toString();
                                    StorageOptionsCommon.IsUserHasDataToRecover = true;
                                    if (new File(sb11).exists()) {
                                        i = i4;
                                        AddVideoToDatabase(file3.getName(), str2, sb9, sb11, i2);
                                    } else {
                                        i = i4;
                                    }
                                } else {
                                    i = i3;
                                }
                            } else {
                                z2 = z4;
                                i = i3;
                            }
                            z4 = z2;
                        } else {
                            i = i3;
                        }
                        i3 = i + 1;
                    }
                }
            }
        }
    }

    private void RecoverDocuments(String str, boolean z) {
        File[] listFiles;
        File[] listFiles2;
        String str2;
        boolean z2;
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(StorageOptionsCommon.DOCUMENTS);
        sb.append("/");
        File file = new File(sb.toString());
        if (file.exists()) {
            boolean z3 = false;
            for (File file2 : file.listFiles()) {
                if (file2.isDirectory()) {
                    for (File file3 : file2.listFiles()) {
                        if (file3.isFile()) {
                            String absolutePath = file3.getAbsolutePath();
                            if (!z) {
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append(StorageOptionsCommon.STORAGEPATH);
                                sb2.append(StorageOptionsCommon.DOCUMENTS);
                                sb2.append("/");
                                sb2.append(file2.getName());
                                try {
                                    absolutePath = Utilities.RecoveryHideFileSDCard(this.context, file3, new File(sb2.toString()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    z3 = true;
                                }
                            }
                            if (!z3) {
                                if (file3.getName().contains("#")) {
                                    str2 = Utilities.ChangeFileExtentionToOrignal(file3.getName());
                                } else {
                                    str2 = file3.getName();
                                }
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append(StorageOptionsCommon.STORAGEPATH);
                                sb3.append(Common.UnhideKitkatAlbumName);
                                sb3.append(str2);
                                String sb4 = sb3.toString();
                                DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
                                documentFolderDAL.OpenRead();
                                int IfFolderNameExistReturnId = documentFolderDAL.IfFolderNameExistReturnId(file2.getName());
                                if (IfFolderNameExistReturnId == 0) {
                                    AddDocumentFolderToDatabase(file2.getName());
                                    IfFolderNameExistReturnId = documentFolderDAL.GetLastFolderId();
                                }
                                documentFolderDAL.close();
                                DocumentDAL documentDAL = new DocumentDAL(this.context);
                                documentDAL.OpenRead();
                                if (absolutePath.contains("'")) {
                                    z2 = documentDAL.IsFileAlreadyExist(absolutePath.replaceAll("'", "''"));
                                } else {
                                    z2 = documentDAL.IsFileAlreadyExist(absolutePath);
                                }
                                documentDAL.close();
                                if (!z2) {
                                    StorageOptionsCommon.IsUserHasDataToRecover = true;
                                    AddDocumentToDatabase(file3.getName(), absolutePath, sb4, IfFolderNameExistReturnId);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void RecoverNotes(String str, boolean z) {
        String str2;
        int i;
        File[] fileArr;
        int i2;
        File[] fileArr2;
        String str3;
        boolean z2;
        boolean z3;
        DataRecover dataRecover = this;
        NotesFilesDAL notesFilesDAL = new NotesFilesDAL(dataRecover.context);
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(dataRecover.context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(StorageOptionsCommon.NOTES);
        sb.append("/");
        File file = new File(sb.toString());
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            int length = listFiles.length;
            int i3 = 0;
            boolean z4 = false;
            int i4 = 0;
            while (i4 < length) {
                File file2 = listFiles[i4];
                if (file2.isDirectory()) {
                    String name = file2.getName();
                    File[] listFiles2 = file2.listFiles();
                    int length2 = listFiles2.length;
                    int i5 = 0;
                    while (i5 < length2) {
                        File file3 = listFiles2[i5];
                        if (file3.isFile()) {
                            String name2 = file3.getName();
                            boolean z5 = z4;
                            String substring = name2.substring(i3, name2.lastIndexOf("."));
                            String absolutePath = file3.getAbsolutePath();
                            StringBuilder sb2 = new StringBuilder();
                            fileArr2 = listFiles;
                            sb2.append(StorageOptionsCommon.STORAGEPATH);
                            sb2.append(StorageOptionsCommon.NOTES);
                            sb2.append(name);
                            File file4 = new File(sb2.toString());
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append(substring);
                            i2 = length;
                            sb3.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
                            File file5 = new File(file4, sb3.toString());
                            if (!z) {
                                try {
                                    if (!file4.exists()) {
                                        file4.mkdirs();
                                    }
                                    z2 = z5;
                                    str3 = Utilities.RecoveryEntryFile(dataRecover.context, file3, file5);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    str3 = absolutePath;
                                    z2 = true;
                                }
                            } else {
                                z2 = z5;
                                str3 = absolutePath;
                            }
                            if (!z2) {
                                StringBuilder sb4 = new StringBuilder();
                                constants.getClass();
                                sb4.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
                                constants.getClass();
                                sb4.append("NotesFileName");
                                sb4.append(" = '");
                                sb4.append(substring);
                                sb4.append("' AND ");
                                constants.getClass();
                                sb4.append("NotesFileIsDecoy");
                                sb4.append(" = ");
                                sb4.append(SecurityLocksCommon.IsFakeAccount);
                                String sb5 = sb4.toString();
                                StringBuilder sb6 = new StringBuilder();
                                constants.getClass();
                                sb6.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
                                constants.getClass();
                                sb6.append("NotesFolderName");
                                sb6.append(" = '");
                                sb6.append(name);
                                sb6.append("' AND ");
                                constants.getClass();
                                sb6.append("NotesFolderIsDecoy");
                                sb6.append(" = ");
                                sb6.append(SecurityLocksCommon.IsFakeAccount);
                                String sb7 = sb6.toString();
                                z3 = z2;
                                double length3 = (double) str3.length();
                                new HashMap();
                                NotesFileDB_Pojo notesFileDB_Pojo = new NotesFileDB_Pojo();
                                fileArr = listFiles2;
                                NotesFolderDB_Pojo notesFolderDB_Pojo = new NotesFolderDB_Pojo();
                                i = length2;
                                HashMap ReadNote = new ReadNoteFromXML().ReadNote(str3);
                                if (ReadNote != null) {
                                    if (!notesFolderDAL.IsFolderAlreadyExist(sb7)) {
                                        notesFolderDB_Pojo.setNotesFolderName(name);
                                        str2 = name;
                                        notesFolderDB_Pojo.setNotesFolderLocation(file2.getAbsolutePath());
                                        notesFolderDB_Pojo.setNotesFolderCreatedDate((String) ReadNote.get("note_datetime_c"));
                                        notesFolderDB_Pojo.setNotesFolderModifiedDate((String) ReadNote.get("note_datetime_m"));
                                        notesFolderDB_Pojo.setNotesFolderFilesSortBy(1);
                                        notesFolderDB_Pojo.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
                                        notesFolderDAL.addNotesFolderInfoInDatabase(notesFolderDB_Pojo);
                                    } else {
                                        str2 = name;
                                    }
                                    new NotesFolderDB_Pojo();
                                    notesFileDB_Pojo.setNotesFileFolderId(notesFolderDAL.getNotesFolderInfoFromDatabase(sb7).getNotesFolderId());
                                    notesFileDB_Pojo.setNotesFileName(substring);
                                    notesFileDB_Pojo.setNotesFileText((String) ReadNote.get("Text"));
                                    notesFileDB_Pojo.setNotesFileCreatedDate((String) ReadNote.get("note_datetime_c"));
                                    notesFileDB_Pojo.setNotesFileModifiedDate((String) ReadNote.get("note_datetime_m"));
                                    notesFileDB_Pojo.setNotesFileLocation(str3);
                                    notesFileDB_Pojo.setNotesFileFromCloud(0);
                                    notesFileDB_Pojo.setNotesFileSize(length3);
                                    notesFileDB_Pojo.setNotesFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                                    if (notesFilesDAL.IsFileAlreadyExist(sb5)) {
                                        constants.getClass();
                                        notesFilesDAL.updateNotesFileInfoInDatabase(notesFileDB_Pojo, "NotesFileId", String.valueOf(notesFileDB_Pojo.getNotesFileId()));
                                    } else {
                                        notesFilesDAL.addNotesFilesInfoInDatabase(notesFileDB_Pojo);
                                    }
                                } else {
                                    str2 = name;
                                }
                            } else {
                                z3 = z2;
                                str2 = name;
                                fileArr = listFiles2;
                                i = length2;
                            }
                            z4 = z3;
                        } else {
                            boolean z6 = z4;
                            fileArr2 = listFiles;
                            i2 = length;
                            str2 = name;
                            fileArr = listFiles2;
                            i = length2;
                        }
                        i5++;
                        listFiles = fileArr2;
                        length = i2;
                        listFiles2 = fileArr;
                        length2 = i;
                        name = str2;
                        dataRecover = this;
                        i3 = 0;
                    }
                    boolean z7 = z4;
                }
                i4++;
                listFiles = listFiles;
                length = length;
                dataRecover = this;
                i3 = 0;
            }
        }
    }

    private void RecoverWalletEntries(String str, boolean z) {
        File file;
        WalletCommon walletCommon;
        int i;
        File[] fileArr;
        int i2;
        File[] fileArr2;
        String str2;
        DataRecover dataRecover = this;
        WalletEntriesDAL walletEntriesDAL = new WalletEntriesDAL(dataRecover.context);
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(dataRecover.context);
        Constants constants = new Constants();
        WalletCommon walletCommon2 = new WalletCommon();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(StorageOptionsCommon.WALLET);
        sb.append("/");
        File file2 = new File(sb.toString());
        walletCommon2.createDefaultCategories(dataRecover.context);
        if (file2.exists()) {
            File[] listFiles = file2.listFiles();
            int length = listFiles.length;
            boolean z2 = false;
            int i3 = 0;
            while (i3 < length) {
                File file3 = listFiles[i3];
                if (file3.isDirectory()) {
                    String name = file3.getName();
                    File[] listFiles2 = file3.listFiles();
                    int length2 = listFiles2.length;
                    int i4 = 0;
                    while (i4 < length2) {
                        File file4 = listFiles2[i4];
                        if (file4.isFile()) {
                            String fileNameWithoutExtention = Utilities.getFileNameWithoutExtention(file4.getName());
                            String absolutePath = file4.getAbsolutePath();
                            boolean z3 = z2;
                            fileArr2 = listFiles;
                            StringBuilder sb2 = new StringBuilder();
                            i2 = length;
                            sb2.append(StorageOptionsCommon.STORAGEPATH);
                            sb2.append(StorageOptionsCommon.WALLET);
                            sb2.append(name);
                            File file5 = new File(sb2.toString());
                            StringBuilder sb3 = new StringBuilder();
                            fileArr = listFiles2;
                            sb3.append(StorageOptionsCommon.ENTRY);
                            sb3.append(fileNameWithoutExtention);
                            sb3.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
                            File file6 = new File(file5, sb3.toString());
                            if (!z) {
                                try {
                                    if (!file5.exists()) {
                                        file5.mkdirs();
                                    }
                                    str2 = Utilities.RecoveryEntryFile(dataRecover.context, file4, file6);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    str2 = absolutePath;
                                    z3 = true;
                                }
                            } else {
                                str2 = absolutePath;
                            }
                            if (!z3) {
                                StringBuilder sb4 = new StringBuilder();
                                constants.getClass();
                                sb4.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
                                constants.getClass();
                                sb4.append("WalletEntryFileIsDecoy");
                                sb4.append(" = ");
                                sb4.append(SecurityLocksCommon.IsFakeAccount);
                                sb4.append(" AND ");
                                constants.getClass();
                                sb4.append("WalletEntryFileName");
                                sb4.append(" = '");
                                sb4.append(fileNameWithoutExtention);
                                sb4.append("'");
                                String sb5 = sb4.toString();
                                StringBuilder sb6 = new StringBuilder();
                                constants.getClass();
                                sb6.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
                                constants.getClass();
                                sb6.append("WalletCategoriesFileIsDecoy");
                                sb6.append(" = ");
                                sb6.append(SecurityLocksCommon.IsFakeAccount);
                                sb6.append(" AND ");
                                constants.getClass();
                                sb6.append("WalletCategoriesFileName");
                                sb6.append(" = '");
                                sb6.append(name);
                                sb6.append("'");
                                String sb7 = sb6.toString();
                                WalletEntryFileDB_Pojo walletEntryFileDB_Pojo = new WalletEntryFileDB_Pojo();
                                WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo = new WalletCategoriesFileDB_Pojo();
                                i = length2;
                                WalletCategoriesPojo currentCategoryData = walletCommon2.getCurrentCategoryData(dataRecover.context, name);
                                String currentDate = walletCommon2.getCurrentDate();
                                if (!walletCategoriesDAL.IsWalletCategoryAlreadyExist(sb7)) {
                                    walletCategoriesFileDB_Pojo.setCategoryFileName(name);
                                    walletCommon = walletCommon2;
                                    walletCategoriesFileDB_Pojo.setCategoryFileLocation(file3.getAbsolutePath());
                                    walletCategoriesFileDB_Pojo.setCategoryFileCreatedDate(currentDate);
                                    walletCategoriesFileDB_Pojo.setCategoryFileModifiedDate(currentDate);
                                    walletCategoriesFileDB_Pojo.setCategoryFileSortBy(1);
                                    walletCategoriesFileDB_Pojo.setCategoryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                                    walletCategoriesDAL.addWalletCategoriesInfoInDatabase(walletCategoriesFileDB_Pojo);
                                    file = file3;
                                } else {
                                    walletCommon = walletCommon2;
                                    WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL.getCategoryInfoFromDatabase(sb7);
                                    categoryInfoFromDatabase.setCategoryFileModifiedDate(currentDate);
                                    categoryInfoFromDatabase.setCategoryFileLocation(file3.getAbsolutePath());
                                    categoryInfoFromDatabase.setCategoryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                                    constants.getClass();
                                    file = file3;
                                    walletCategoriesDAL.updateCategoryFromDatabase(categoryInfoFromDatabase, "WalletCategoriesFileId", String.valueOf(categoryInfoFromDatabase.getCategoryFileId()));
                                }
                                walletEntryFileDB_Pojo.setCategoryId(walletCategoriesDAL.getCategoryInfoFromDatabase(sb7).getCategoryFileId());
                                walletEntryFileDB_Pojo.setEntryFileName(fileNameWithoutExtention);
                                walletEntryFileDB_Pojo.setEntryFileCreatedDate(currentDate);
                                walletEntryFileDB_Pojo.setEntryFileModifiedDate(currentDate);
                                walletEntryFileDB_Pojo.setEntryFileLocation(str2);
                                walletEntryFileDB_Pojo.setCategoryFileIconIndex(currentCategoryData.getCategoryIconIndex());
                                walletEntryFileDB_Pojo.setEntryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                                if (walletEntriesDAL.IsWalletEntryAlreadyExist(sb5)) {
                                    constants.getClass();
                                    walletEntriesDAL.updateEntryInDatabase(walletEntryFileDB_Pojo, "WalletEntryFileId", String.valueOf(walletEntryFileDB_Pojo.getEntryFileId()));
                                } else {
                                    walletEntriesDAL.addWalletEntriesInfoInDatabase(walletEntryFileDB_Pojo);
                                }
                            } else {
                                walletCommon = walletCommon2;
                                file = file3;
                                i = length2;
                            }
                            z2 = z3;
                        } else {
                            boolean z4 = z2;
                            walletCommon = walletCommon2;
                            fileArr2 = listFiles;
                            i2 = length;
                            file = file3;
                            fileArr = listFiles2;
                            i = length2;
                        }
                        i4++;
                        listFiles = fileArr2;
                        length = i2;
                        listFiles2 = fileArr;
                        length2 = i;
                        walletCommon2 = walletCommon;
                        file3 = file;
                        dataRecover = this;
                    }
                    boolean z5 = z2;
                }
                i3++;
                listFiles = listFiles;
                length = length;
                walletCommon2 = walletCommon2;
                dataRecover = this;
            }
        }
    }

    private void AddPhotoAlbumToDatabase(String str) {
        PhotoAlbum photoAlbum = new PhotoAlbum();
        photoAlbum.setAlbumName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.PHOTOS);
        sb.append(str);
        photoAlbum.setAlbumLocation(sb.toString());
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        try {
            photoAlbumDAL.OpenWrite();
            photoAlbumDAL.AddPhotoAlbum(photoAlbum);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            photoAlbumDAL.close();
            throw th;
        }
        photoAlbumDAL.close();
    }

    private void AddVideoAlbumToDatabase(String str) {
        VideoAlbum videoAlbum = new VideoAlbum();
        videoAlbum.setAlbumName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append("/");
        sb.append(StorageOptionsCommon.VIDEOS);
        sb.append(str);
        videoAlbum.setAlbumLocation(sb.toString());
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        try {
            videoAlbumDAL.OpenWrite();
            videoAlbumDAL.AddVideoAlbum(videoAlbum);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            videoAlbumDAL.close();
            throw th;
        }
        videoAlbumDAL.close();
    }

    private void AddPhotoToDatabase(String str, String str2, String str3, int i) {
        if (str.contains("#")) {
            str = Utilities.ChangeFileExtentionToOrignal(str);
        }
        Log.d("Path", str2);
        Photo photo = new Photo();
        photo.setPhotoName(str);
        photo.setFolderLockPhotoLocation(str2);
        photo.setOriginalPhotoLocation(str3);
        photo.setAlbumId(i);
        PhotoDAL photoDAL = new PhotoDAL(this.context);
        try {
            photoDAL.OpenWrite();
            photoDAL.AddPhotos(photo);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            photoDAL.close();
            throw th;
        }
        photoDAL.close();
    }

    private void AddVideoToDatabase(String str, String str2, String str3, String str4, int i) {
        if (str.contains("#")) {
            str = Utilities.ChangeFileExtentionToOrignal(str);
        }
        Video video = new Video();
        video.setVideoName(str);
        video.setFolderLockVideoLocation(str2);
        video.setOriginalVideoLocation(str3);
        video.setthumbnail_video_location(str4);
        video.setAlbumId(i);
        VideoDAL videoDAL = new VideoDAL(this.context);
        try {
            videoDAL.OpenWrite();
            videoDAL.AddVideos(video);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            videoDAL.close();
            throw th;
        }
        videoDAL.close();
    }

    private void AddDocumentFolderToDatabase(String str) {
        DocumentFolder documentFolder = new DocumentFolder();
        documentFolder.setFolderName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.DOCUMENTS);
        sb.append(str);
        documentFolder.setFolderLocation(sb.toString());
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
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

    private void AddDocumentToDatabase(String str, String str2, String str3, int i) {
        if (str.contains("#")) {
            str = Utilities.ChangeFileExtentionToOrignal(str);
        }
        DocumentsEnt documentsEnt = new DocumentsEnt();
        documentsEnt.setDocumentName(str);
        documentsEnt.setFolderLockDocumentLocation(str2);
        documentsEnt.setOriginalDocumentLocation(str3);
        documentsEnt.setFolderId(i);
        DocumentDAL documentDAL = new DocumentDAL(this.context);
        try {
            documentDAL.OpenWrite();
            documentDAL.AddDocuments(documentsEnt, str2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            documentDAL.close();
            throw th;
        }
        documentDAL.close();
    }
}
