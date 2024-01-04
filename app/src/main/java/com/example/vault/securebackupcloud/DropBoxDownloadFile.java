package com.example.vault.securebackupcloud;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Hashtable;
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
import com.example.vault.dropbox.service.CloudService;
import com.example.vault.notes.model.NotesFileDB_Pojo;
import com.example.vault.notes.util.NotesFilesDAL;
import com.example.vault.notes.util.NotesFolderDAL;
import com.example.vault.notes.model.NotesFolderDB_Pojo;
import com.example.vault.notes.util.ReadNoteFromXML;
import com.example.vault.photo.model.Photo;
import com.example.vault.photo.model.PhotoAlbum;
import com.example.vault.photo.util.PhotoAlbumDAL;
import com.example.vault.photo.util.PhotoDAL;
import com.example.vault.securebackupcloud.CloudCommon.DropboxType;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.todolist.util.ToDoDAL;
import com.example.vault.todolist.model.ToDoDB_Pojo;
import com.example.vault.todolist.model.ToDoPojo;
import com.example.vault.todolist.util.ToDoReadFromXML;
import com.example.vault.todolist.model.ToDoTask;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;
import com.example.vault.video.model.Video;
import com.example.vault.video.model.VideoAlbum;
import com.example.vault.video.util.VideoAlbumDAL;
import com.example.vault.video.util.VideoDAL;
import com.example.vault.wallet.util.WalletCategoriesDAL;
import com.example.vault.wallet.model.WalletCategoriesFileDB_Pojo;
import com.example.vault.wallet.util.WalletCommon;
import com.example.vault.wallet.util.WalletEntriesDAL;
import com.example.vault.wallet.model.WalletEntryFileDB_Pojo;

public class DropBoxDownloadFile extends AsyncTask<Void, Long, Boolean> {
    private BackupCloudEnt backupCloudEnt;
    private Context context;
    private String downloadFilePath;
    private int downloadType;
    private String localFilePath;
    private DbxClientV2 mApi;
    private String mErrorMsg;
    private FileOutputStream mFos;


    public void onProgressUpdate(Long... lArr) {
    }

    public DropBoxDownloadFile(Context context2, DbxClientV2 dbxClientV2, String str, String str2, int i, BackupCloudEnt backupCloudEnt2) {
        this.context = context2;
        this.mApi = dbxClientV2;
        this.localFilePath = str2;
        this.downloadFilePath = str;
        this.downloadType = i;
        this.backupCloudEnt = backupCloudEnt2;
    }


    public Boolean doInBackground(Void... voidArr) {
        try {
            File file = new File(this.localFilePath);
            try {
                if (!file.exists()) {
                    file.mkdirs();
                }
                String name = new File(this.downloadFilePath).getName();
                if (!(this.downloadType == DropboxType.Notes.ordinal() || this.downloadType == DropboxType.Wallet.ordinal())) {
                    name = Utilities.ChangeFileExtention(name);
                }
                StringBuilder sb = new StringBuilder();
                sb.append(this.localFilePath);
                sb.append("/");
                sb.append(name);
                this.localFilePath = sb.toString();
                new File(this.localFilePath).createNewFile();
                try {
                    this.mFos = new FileOutputStream(this.localFilePath);
                    this.mApi.files().download(this.downloadFilePath).download(this.mFos);
                } catch (DbxException e) {
                    e.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                return Boolean.valueOf(true);
            } catch (IOException e3) {
                e3.printStackTrace();
                return Boolean.valueOf(false);
            }
        } catch (Exception unused) {
        }
        return null;
    }


    public void onPostExecute(Boolean bool) {
        try {
            if (bool.booleanValue()) {
                if (this.downloadType == DropboxType.Photos.ordinal()) {
                    Utilities.NSEncryption(new File(this.localFilePath));
                    AddPhotoToDatabase(new File(this.downloadFilePath).getName(), this.localFilePath, new File(this.localFilePath).getParentFile().getName());
                } else if (this.downloadType == DropboxType.Videos.ordinal()) {
                    AddVideoToDatabase(new File(this.downloadFilePath).getName(), this.localFilePath, new File(this.localFilePath).getParentFile().getName());
                } else if (this.downloadType == DropboxType.Documents.ordinal()) {
                    Utilities.NSEncryption(new File(this.localFilePath));
                    AddDocumentToDatabase(new File(this.downloadFilePath).getName(), this.localFilePath, new File(this.localFilePath).getParentFile().getName());
                } else if (this.downloadType == DropboxType.Notes.ordinal()) {
                    AddNoteToDatabase(new File(this.downloadFilePath).getName(), this.localFilePath, new File(this.localFilePath).getParentFile().getName());
                } else if (this.downloadType == DropboxType.Wallet.ordinal()) {
                    AddWalletToDatabase(new File(this.downloadFilePath).getName(), this.localFilePath, new File(this.localFilePath).getParentFile().getName());
                } else if (this.downloadType == DropboxType.ToDo.ordinal()) {
                    Utilities.NSEncryption(new File(this.localFilePath));
                    AddToDoInDatabase(new File(this.downloadFilePath).getName(), this.localFilePath);
                } else if (this.downloadType == DropboxType.Audio.ordinal()) {
                    AddMusicToDatabase(new File(this.downloadFilePath).getName(), this.localFilePath, new File(this.localFilePath).getParentFile().getName());
                }
                int indexOf = CloudService.CloudBackupCloudEntList.indexOf(this.backupCloudEnt);
                Hashtable GetDownloadPath = this.backupCloudEnt.GetDownloadPath();
                if (GetDownloadPath.containsKey(this.downloadFilePath)) {
                    GetDownloadPath.put(this.downloadFilePath, Boolean.valueOf(true));
                    this.backupCloudEnt.SetDownloadPath(GetDownloadPath);
                    CloudService.CloudBackupCloudEntList.set(indexOf, this.backupCloudEnt);
                    return;
                }
                return;
            }
            showToast(this.mErrorMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showToast(String str) {
        Toast.makeText(this.context, str, Toast.LENGTH_LONG).show();
    }

    private void AddPhotoToDatabase(String str, String str2, String str3) {
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenWrite();
        PhotoAlbum GetAlbum = photoAlbumDAL.GetAlbum(str3);
        int id = GetAlbum.getId();
        if (GetAlbum.getId() == 0) {
            PhotoAlbum photoAlbum = new PhotoAlbum();
            photoAlbum.setAlbumName(str3);
            photoAlbum.setAlbumLocation(new File(str2).getParent());
            photoAlbumDAL.AddPhotoAlbum(photoAlbum);
            id = photoAlbumDAL.GetLastAlbumId();
        }
        Photo photo = new Photo();
        photo.setPhotoName(str);
        photo.setFolderLockPhotoLocation(str2);
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(Common.UnhideKitkatAlbumName);
        sb.append(str);
        photo.setOriginalPhotoLocation(sb.toString());
        photo.setAlbumId(id);
        PhotoDAL photoDAL = new PhotoDAL(this.context);
        try {
            photoDAL.OpenWrite();
            photoDAL.AddPhotos(photo);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            photoDAL.close();
            photoAlbumDAL.close();
            throw th;
        }
        photoDAL.close();
        photoAlbumDAL.close();
    }


    @SuppressLint({"NewApi"})
    private void AddVideoToDatabase(String str, String str2, String str3) {
        String str4;
        FileOutputStream fileOutputStream;
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.VIDEOS);
        sb.append(str3);
        sb.append("/VideoThumnails/");
        new File(sb.toString()).mkdir();
        if (str.contains("#")) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(StorageOptionsCommon.STORAGEPATH);
            sb2.append(StorageOptionsCommon.VIDEOS);
            sb2.append(str3);
            sb2.append("/VideoThumnails/thumbnil-");
            sb2.append(str.substring(0, str.lastIndexOf("#")));
            sb2.append("#jpg");
            str4 = sb2.toString();
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(StorageOptionsCommon.STORAGEPATH);
            sb3.append(StorageOptionsCommon.VIDEOS);
            sb3.append(str3);
            sb3.append("/VideoThumnails/thumbnil-");
            sb3.append(str.substring(0, str.lastIndexOf(".")));
            sb3.append("#jpg");
            str4 = sb3.toString();
        }
        File file = new File(str4);
        Bitmap bitmap = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileOutputStream = null;
        }
        File file2 = new File(str2);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file2.getAbsolutePath());
        bitmap = mediaMetadataRetriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST);
        bitmap.compress(CompressFormat.JPEG, 100, fileOutputStream);
        try {
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileOutputStream.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        try {
            Utilities.NSEncryption(file);
            Utilities.NSEncryption(file2);
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        videoAlbumDAL.OpenWrite();
        VideoAlbum GetAlbum = videoAlbumDAL.GetAlbum(str3);
        int id = GetAlbum.getId();
        if (GetAlbum.getId() == 0) {
            VideoAlbum videoAlbum = new VideoAlbum();
            videoAlbum.setAlbumName(str3);
            videoAlbum.setAlbumLocation(new File(str2).getParent());
            videoAlbumDAL.AddVideoAlbum(videoAlbum);
            id = videoAlbumDAL.GetLastAlbumId();
        }
        Video video = new Video();
        video.setVideoName(str);
        video.setFolderLockVideoLocation(str2);
        StringBuilder sb4 = new StringBuilder();
        sb4.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb4.append(Common.UnhideKitkatAlbumName);
        sb4.append(str);
        video.setOriginalVideoLocation(sb4.toString());
        video.setthumbnail_video_location(str4);
        video.setAlbumId(id);
        VideoDAL videoDAL = new VideoDAL(this.context);
        try {
            videoDAL.OpenWrite();
            videoDAL.AddVideos(video);
        } catch (Exception e4) {
            System.out.println(e4.getMessage());
        } catch (Throwable th) {
            videoDAL.close();
            throw th;
        }
        videoDAL.close();
    }

    private void AddDocumentToDatabase(String str, String str2, String str3) {
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        documentFolderDAL.OpenWrite();
        DocumentFolder GetFolder = documentFolderDAL.GetFolder(str3);
        int id = GetFolder.getId();
        if (GetFolder.getId() == 0) {
            DocumentFolder documentFolder = new DocumentFolder();
            documentFolder.setFolderName(str3);
            documentFolder.setFolderLocation(new File(str2).getParent());
            documentFolderDAL.AddDocumentFolder(documentFolder);
            id = documentFolderDAL.GetLastFolderId();
        }
        DocumentsEnt documentsEnt = new DocumentsEnt();
        documentsEnt.setDocumentName(str);
        documentsEnt.setFolderLockDocumentLocation(str2);
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(Common.UnhideKitkatAlbumName);
        sb.append(str);
        documentsEnt.setOriginalDocumentLocation(sb.toString());
        documentsEnt.setFolderId(id);
        DocumentDAL documentDAL = new DocumentDAL(this.context);
        try {
            documentDAL.OpenWrite();
            documentDAL.AddDocuments(documentsEnt, str2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            documentDAL.close();
            documentFolderDAL.close();
            throw th;
        }
        documentDAL.close();
        documentFolderDAL.close();
    }

    private void AddNoteToDatabase(String str, String str2, String str3) {
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(this.context);
        NotesFilesDAL notesFilesDAL = new NotesFilesDAL(this.context);
        Constants constants = new Constants();
        new HashMap();
        ReadNoteFromXML readNoteFromXML = new ReadNoteFromXML();
        NotesFolderDB_Pojo notesFolderDB_Pojo = new NotesFolderDB_Pojo();
        long length = new File(str2).length();
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.NOTES);
        sb.append(str3);
        File file = new File(sb.toString());
        String substring = str.substring(0, str.lastIndexOf("."));
        StringBuilder sb2 = new StringBuilder();
        constants.getClass();
        sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
        constants.getClass();
        sb2.append("NotesFolderName");
        sb2.append(" = '");
        sb2.append(str3);
        sb2.append("' AND ");
        constants.getClass();
        sb2.append("NotesFolderIsDecoy");
        sb2.append(" = ");
        sb2.append(SecurityLocksCommon.IsFakeAccount);
        String sb3 = sb2.toString();
        if (!file.exists()) {
            file.mkdirs();
        }
        HashMap ReadNote = readNoteFromXML.ReadNote(str2);
        if (ReadNote != null) {
            if (!notesFolderDAL.IsFolderAlreadyExist(sb3)) {
                notesFolderDB_Pojo.setNotesFolderName(str3);
                notesFolderDB_Pojo.setNotesFolderLocation(file.getAbsolutePath());
                notesFolderDB_Pojo.setNotesFolderCreatedDate((String) ReadNote.get("note_datetime_c"));
                notesFolderDB_Pojo.setNotesFolderModifiedDate((String) ReadNote.get("note_datetime_m"));
                notesFolderDB_Pojo.setNotesFolderFilesSortBy(1);
                notesFolderDB_Pojo.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
                notesFolderDAL.addNotesFolderInfoInDatabase(notesFolderDB_Pojo);
            }
            new NotesFolderDB_Pojo();
            NotesFolderDB_Pojo notesFolderInfoFromDatabase = notesFolderDAL.getNotesFolderInfoFromDatabase(sb3);
            NotesFileDB_Pojo notesFileDB_Pojo = new NotesFileDB_Pojo();
            notesFileDB_Pojo.setNotesFileFolderId(notesFolderInfoFromDatabase.getNotesFolderId());
            notesFileDB_Pojo.setNotesFileName(substring);
            notesFileDB_Pojo.setNotesFileCreatedDate((String) ReadNote.get("note_datetime_c"));
            notesFileDB_Pojo.setNotesFileModifiedDate((String) ReadNote.get("note_datetime_m"));
            notesFileDB_Pojo.setNotesFileLocation(str2);
            notesFileDB_Pojo.setNotesFileFromCloud(1);
            notesFileDB_Pojo.setNotesFileSize((double) length);
            notesFileDB_Pojo.setNotesFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
            notesFilesDAL.addNotesFilesInfoInDatabase(notesFileDB_Pojo);
        }
    }

    private void AddWalletToDatabase(String str, String str2, String str3) {
        WalletEntriesDAL walletEntriesDAL = new WalletEntriesDAL(this.context);
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        Constants constants = new Constants();
        WalletCommon walletCommon = new WalletCommon();
        WalletEntryFileDB_Pojo walletEntryFileDB_Pojo = new WalletEntryFileDB_Pojo();
        WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo = new WalletCategoriesFileDB_Pojo();
        String fileNameWithoutExtention = Utilities.getFileNameWithoutExtention(str);
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.WALLET);
        sb.append(str3);
        File file = new File(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        constants.getClass();
        sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
        constants.getClass();
        sb2.append("WalletCategoriesFileIsDecoy");
        sb2.append(" = ");
        sb2.append(SecurityLocksCommon.IsFakeAccount);
        sb2.append(" AND ");
        constants.getClass();
        sb2.append("WalletCategoriesFileName");
        sb2.append(" = '");
        sb2.append(str3);
        sb2.append("'");
        String sb3 = sb2.toString();
        if (!file.exists()) {
            file.mkdirs();
        }
        String currentDate = walletCommon.getCurrentDate();
        if (!walletCategoriesDAL.IsWalletCategoryAlreadyExist(sb3)) {
            walletCategoriesFileDB_Pojo.setCategoryFileName(str3);
            walletCategoriesFileDB_Pojo.setCategoryFileLocation(file.getAbsolutePath());
            walletCategoriesFileDB_Pojo.setCategoryFileCreatedDate(currentDate);
            walletCategoriesFileDB_Pojo.setCategoryFileModifiedDate(currentDate);
            walletCategoriesFileDB_Pojo.setCategoryFileSortBy(1);
            walletCategoriesFileDB_Pojo.setCategoryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
            walletCategoriesDAL.addWalletCategoriesInfoInDatabase(walletCategoriesFileDB_Pojo);
        }
        new WalletCategoriesFileDB_Pojo();
        WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL.getCategoryInfoFromDatabase(sb3);
        walletEntryFileDB_Pojo.setCategoryId(categoryInfoFromDatabase.getCategoryFileId());
        walletEntryFileDB_Pojo.setEntryFileName(fileNameWithoutExtention);
        walletEntryFileDB_Pojo.setEntryFileCreatedDate(currentDate);
        walletEntryFileDB_Pojo.setEntryFileModifiedDate(currentDate);
        walletEntryFileDB_Pojo.setEntryFileLocation(str2);
        walletEntryFileDB_Pojo.setCategoryFileIconIndex(categoryInfoFromDatabase.getCategoryFileIconIndex());
        walletEntryFileDB_Pojo.setEntryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
        walletEntriesDAL.addWalletEntriesInfoInDatabase(walletEntryFileDB_Pojo);
    }

    private void AddToDoInDatabase(String str, String str2) {
        new SimpleDateFormat("EEE, dd MMM yyyy hh:edit_share_btn:ss +0000", Locale.getDefault());
        String str3 = str.split(StorageOptionsCommon.NOTES_FILE_EXTENSION)[0];
        ToDoReadFromXML toDoReadFromXML = new ToDoReadFromXML();
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.TODOLIST);
        File file = new File(sb.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(StorageOptionsCommon.STORAGEPATH);
        sb2.append(StorageOptionsCommon.TODOLIST);
        sb2.append(str3);
        sb2.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
        String sb3 = sb2.toString();
        ToDoPojo ReadToDoList = toDoReadFromXML.ReadToDoList(sb3);
        ToDoDB_Pojo toDoDB_Pojo = new ToDoDB_Pojo();
        toDoDB_Pojo.setToDoFileName(str3);
        toDoDB_Pojo.setToDoFileLocation(sb3);
        toDoDB_Pojo.setToDoFileColor(ReadToDoList.getTodoColor());
        toDoDB_Pojo.setToDoFileCreatedDate(ReadToDoList.getDateCreated());
        toDoDB_Pojo.setToDoFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
        toDoDB_Pojo.setToDoFileTask1(((ToDoTask) ReadToDoList.getToDoList().get(0)).getToDo());
        toDoDB_Pojo.setToDoFileTask1IsChecked(((ToDoTask) ReadToDoList.getToDoList().get(0)).isChecked());
        if (ReadToDoList.getToDoList().size() >= 2) {
            toDoDB_Pojo.setToDoFileTask2(((ToDoTask) ReadToDoList.getToDoList().get(1)).getToDo());
            toDoDB_Pojo.setToDoFileTask2IsChecked(((ToDoTask) ReadToDoList.getToDoList().get(1)).isChecked());
        }
        new ToDoDAL(this.context).addToDoInfoInDatabase(toDoDB_Pojo);
    }

    private void AddMusicToDatabase(String str, String str2, String str3) {
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenWrite();
        AudioPlayListEnt GetPlayList = audioPlayListDAL.GetPlayList(str3);
        int id = GetPlayList.getId();
        if (GetPlayList.getId() == 0) {
            AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
            audioPlayListEnt.setPlayListName(str3);
            audioPlayListEnt.setPlayListLocation(new File(str2).getParent());
            audioPlayListDAL.AddAudioPlayList(audioPlayListEnt);
            id = audioPlayListDAL.GetLastPlayListId();
        }
        AudioEnt audioEnt = new AudioEnt();
        audioEnt.setAudioName(str);
        audioEnt.setFolderLockAudioLocation(str2);
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(Common.UnhideKitkatAlbumName);
        sb.append(str);
        audioEnt.setOriginalAudioLocation(sb.toString());
        audioEnt.setPlayListId(id);
        AudioDAL audioDAL = new AudioDAL(this.context);
        try {
            audioDAL.OpenWrite();
            audioDAL.AddAudio(audioEnt, str2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            audioDAL.close();
            audioPlayListDAL.close();
            throw th;
        }
        audioDAL.close();
        audioPlayListDAL.close();
    }
}
