package com.example.vault.notes.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Xml;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import com.example.vault.Flaes;
import com.example.vault.R;
import com.example.vault.common.Constants;
import com.example.vault.notes.NotesFilesActivity;
import com.example.vault.notes.model.NotesFileDB_Pojo;
import com.example.vault.notes.model.NotesFolderDB_Pojo;
import com.example.vault.notes.util.NotesCommon;
import com.example.vault.notes.util.NotesFilesDAL;
import com.example.vault.notes.util.NotesFolderDAL;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionsCommon;
import org.xmlpull.v1.XmlSerializer;

public class SaveNoteInXML {
    Constants constants;
    File newFile;
    String noteName;
    NotesFilesDAL notesFilesDAL;
    File oldFile;

    public void SaveNote(Activity activity, HashMap<String, String> hashMap, String str, boolean z) {
        String str2;
        String str3;
        String str4;
        String str5;
        this.constants = new Constants();
        this.noteName = (String) hashMap.get("Title");
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.NOTES);
        sb.append(NotesCommon.CurrentNotesFolder);
        sb.append(File.separator);
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(this.noteName);
        sb3.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
        this.newFile = new File(sb2, sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(StorageOptionsCommon.STORAGEPATH);
        sb4.append(StorageOptionsCommon.NOTES);
        sb4.append(NotesCommon.CurrentNotesFolder);
        sb4.append(File.separator);
        String sb5 = sb4.toString();
        StringBuilder sb6 = new StringBuilder();
        sb6.append(str);
        sb6.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
        this.oldFile = new File(sb5, sb6.toString());
        double d = 0.0d;
        if (!z) {
            try {
                if (this.newFile.exists()) {
                    Toast.makeText(activity, R.string.toast_exists, Toast.LENGTH_SHORT).show();
                    if (z) {
                        this.constants.getClass();
                        StringBuilder sb7 = new StringBuilder();
                        this.constants.getClass();
                        sb7.append("NotesFileName");
                        sb7.append(" = '");
                        sb7.append(str);
                        sb7.append("' AND ");
                        this.constants.getClass();
                        sb7.append("NotesFileIsDecoy");
                        sb7.append(" = ");
                        sb7.append(SecurityLocksCommon.IsFakeAccount);
                        str5 = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ".concat(sb7.toString());
                    } else {
                        this.constants.getClass();
                        StringBuilder sb8 = new StringBuilder();
                        this.constants.getClass();
                        sb8.append("NotesFileName");
                        sb8.append(" = '");
                        sb8.append(this.noteName);
                        sb8.append("' AND ");
                        this.constants.getClass();
                        sb8.append("NotesFileIsDecoy");
                        sb8.append(" = ");
                        sb8.append(SecurityLocksCommon.IsFakeAccount);
                        str5 = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ".concat(sb8.toString());
                    }
                    if (this.newFile.exists()) {
                        d = (double) this.newFile.length();
                    }
                    this.notesFilesDAL = new NotesFilesDAL(activity);
                    new NotesFileDB_Pojo();
                    NotesFileDB_Pojo notesFileInfoFromDatabase = this.notesFilesDAL.getNotesFileInfoFromDatabase(str5);
                    notesFileInfoFromDatabase.setNotesFileFolderId(NotesCommon.CurrentNotesFolderId);
                    notesFileInfoFromDatabase.setNotesFileName(this.noteName);
                    notesFileInfoFromDatabase.setNotesFileText((String) hashMap.get("Text"));
                    notesFileInfoFromDatabase.setNotesFileCreatedDate((String) hashMap.get("note_datetime_c"));
                    notesFileInfoFromDatabase.setNotesFileModifiedDate((String) hashMap.get("note_datetime_m"));
                    notesFileInfoFromDatabase.setNotesfileColor((String) hashMap.get("NoteColor"));
                    notesFileInfoFromDatabase.setNotesFileLocation(this.newFile.getAbsolutePath());
                    notesFileInfoFromDatabase.setNotesFileFromCloud(0);
                    notesFileInfoFromDatabase.setNotesFileSize(d);
                    notesFileInfoFromDatabase.setNotesFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                    if (this.notesFilesDAL.IsFileAlreadyExist(str5)) {
                        NotesFilesDAL notesFilesDAL2 = this.notesFilesDAL;
                        this.constants.getClass();
                        notesFilesDAL2.updateNotesFileInfoInDatabase(notesFileInfoFromDatabase, "NotesFileId", String.valueOf(notesFileInfoFromDatabase.getNotesFileId()));
                    } else {
                        this.notesFilesDAL.addNotesFilesInfoInDatabase(notesFileInfoFromDatabase);
                    }
                    NotesFolderDAL notesFolderDAL = new NotesFolderDAL(activity);
                    new NotesFolderDB_Pojo();
                    StringBuilder sb9 = new StringBuilder();
                    this.constants.getClass();
                    sb9.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
                    this.constants.getClass();
                    sb9.append("NotesFolderId");
                    sb9.append(" = ");
                    sb9.append(NotesCommon.CurrentNotesFolderId);
                    sb9.append(" AND ");
                    this.constants.getClass();
                    sb9.append("NotesFolderIsDecoy");
                    sb9.append(" = ");
                    sb9.append(SecurityLocksCommon.IsFakeAccount);
                    NotesFolderDB_Pojo notesFolderInfoFromDatabase = notesFolderDAL.getNotesFolderInfoFromDatabase(sb9.toString());
                    notesFolderInfoFromDatabase.setNotesFolderModifiedDate((String) hashMap.get("note_datetime_m"));
                    notesFolderInfoFromDatabase.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
                    this.constants.getClass();
                    notesFolderDAL.updateNotesFolderFromDatabase(notesFolderInfoFromDatabase, "NotesFolderId", String.valueOf(notesFolderInfoFromDatabase.getNotesFolderId()));
                    SecurityLocksCommon.IsAppDeactive = false;
                    activity.startActivity(new Intent(activity, NotesFilesActivity.class));
                    activity.finish();
                    return;
                }
                this.newFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                if (z) {
                    this.constants.getClass();
                    StringBuilder sb10 = new StringBuilder();
                    this.constants.getClass();
                    sb10.append("NotesFileName");
                    sb10.append(" = '");
                    sb10.append(str);
                    sb10.append("' AND ");
                    this.constants.getClass();
                    sb10.append("NotesFileIsDecoy");
                    sb10.append(" = ");
                    sb10.append(SecurityLocksCommon.IsFakeAccount);
                    str3 = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ".concat(sb10.toString());
                } else {
                    this.constants.getClass();
                    StringBuilder sb11 = new StringBuilder();
                    this.constants.getClass();
                    sb11.append("NotesFileName");
                    sb11.append(" = '");
                    sb11.append(this.noteName);
                    sb11.append("' AND ");
                    this.constants.getClass();
                    sb11.append("NotesFileIsDecoy");
                    sb11.append(" = ");
                    sb11.append(SecurityLocksCommon.IsFakeAccount);
                    str3 = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ".concat(sb11.toString());
                }
                if (this.newFile.exists()) {
                    d = (double) this.newFile.length();
                }
                this.notesFilesDAL = new NotesFilesDAL(activity);
                new NotesFileDB_Pojo();
                NotesFileDB_Pojo notesFileInfoFromDatabase2 = this.notesFilesDAL.getNotesFileInfoFromDatabase(str3);
                notesFileInfoFromDatabase2.setNotesFileFolderId(NotesCommon.CurrentNotesFolderId);
                notesFileInfoFromDatabase2.setNotesFileName(this.noteName);
                notesFileInfoFromDatabase2.setNotesFileText((String) hashMap.get("Text"));
                notesFileInfoFromDatabase2.setNotesFileCreatedDate((String) hashMap.get("note_datetime_c"));
                notesFileInfoFromDatabase2.setNotesFileModifiedDate((String) hashMap.get("note_datetime_m"));
                notesFileInfoFromDatabase2.setNotesfileColor((String) hashMap.get("NoteColor"));
                notesFileInfoFromDatabase2.setNotesFileLocation(this.newFile.getAbsolutePath());
                notesFileInfoFromDatabase2.setNotesFileFromCloud(0);
                notesFileInfoFromDatabase2.setNotesFileSize(d);
                notesFileInfoFromDatabase2.setNotesFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                if (this.notesFilesDAL.IsFileAlreadyExist(str3)) {
                    NotesFilesDAL notesFilesDAL3 = this.notesFilesDAL;
                    this.constants.getClass();
                    notesFilesDAL3.updateNotesFileInfoInDatabase(notesFileInfoFromDatabase2, "NotesFileId", String.valueOf(notesFileInfoFromDatabase2.getNotesFileId()));
                } else {
                    this.notesFilesDAL.addNotesFilesInfoInDatabase(notesFileInfoFromDatabase2);
                }
                NotesFolderDAL notesFolderDAL2 = new NotesFolderDAL(activity);
                new NotesFolderDB_Pojo();
                StringBuilder sb12 = new StringBuilder();
                this.constants.getClass();
                sb12.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
                this.constants.getClass();
                sb12.append("NotesFolderId");
                sb12.append(" = ");
                sb12.append(NotesCommon.CurrentNotesFolderId);
                sb12.append(" AND ");
                this.constants.getClass();
                sb12.append("NotesFolderIsDecoy");
                sb12.append(" = ");
                sb12.append(SecurityLocksCommon.IsFakeAccount);
                NotesFolderDB_Pojo notesFolderInfoFromDatabase2 = notesFolderDAL2.getNotesFolderInfoFromDatabase(sb12.toString());
                notesFolderInfoFromDatabase2.setNotesFolderModifiedDate((String) hashMap.get("note_datetime_m"));
                notesFolderInfoFromDatabase2.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
                this.constants.getClass();
                notesFolderDAL2.updateNotesFolderFromDatabase(notesFolderInfoFromDatabase2, "NotesFolderId", String.valueOf(notesFolderInfoFromDatabase2.getNotesFolderId()));
                SecurityLocksCommon.IsAppDeactive = false;
                activity.startActivity(new Intent(activity, NotesFilesActivity.class));
                activity.finish();
                return;
            } catch (Throwable th) {
                if (z) {
                    this.constants.getClass();
                    StringBuilder sb13 = new StringBuilder();
                    this.constants.getClass();
                    sb13.append("NotesFileName");
                    sb13.append(" = '");
                    sb13.append(str);
                    sb13.append("' AND ");
                    this.constants.getClass();
                    sb13.append("NotesFileIsDecoy");
                    sb13.append(" = ");
                    sb13.append(SecurityLocksCommon.IsFakeAccount);
                    str4 = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ".concat(sb13.toString());
                } else {
                    this.constants.getClass();
                    StringBuilder sb14 = new StringBuilder();
                    this.constants.getClass();
                    sb14.append("NotesFileName");
                    sb14.append(" = '");
                    sb14.append(this.noteName);
                    sb14.append("' AND ");
                    this.constants.getClass();
                    sb14.append("NotesFileIsDecoy");
                    sb14.append(" = ");
                    sb14.append(SecurityLocksCommon.IsFakeAccount);
                    str4 = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ".concat(sb14.toString());
                }
                if (this.newFile.exists()) {
                    d = (double) this.newFile.length();
                }
                this.notesFilesDAL = new NotesFilesDAL(activity);
                new NotesFileDB_Pojo();
                NotesFileDB_Pojo notesFileInfoFromDatabase3 = this.notesFilesDAL.getNotesFileInfoFromDatabase(str4);
                notesFileInfoFromDatabase3.setNotesFileFolderId(NotesCommon.CurrentNotesFolderId);
                notesFileInfoFromDatabase3.setNotesFileName(this.noteName);
                notesFileInfoFromDatabase3.setNotesFileText((String) hashMap.get("Text"));
                notesFileInfoFromDatabase3.setNotesFileCreatedDate((String) hashMap.get("note_datetime_c"));
                notesFileInfoFromDatabase3.setNotesFileModifiedDate((String) hashMap.get("note_datetime_m"));
                notesFileInfoFromDatabase3.setNotesfileColor((String) hashMap.get("NoteColor"));
                notesFileInfoFromDatabase3.setNotesFileLocation(this.newFile.getAbsolutePath());
                notesFileInfoFromDatabase3.setNotesFileFromCloud(0);
                notesFileInfoFromDatabase3.setNotesFileSize(d);
                notesFileInfoFromDatabase3.setNotesFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                if (this.notesFilesDAL.IsFileAlreadyExist(str4)) {
                    NotesFilesDAL notesFilesDAL4 = this.notesFilesDAL;
                    this.constants.getClass();
                    notesFilesDAL4.updateNotesFileInfoInDatabase(notesFileInfoFromDatabase3, "NotesFileId", String.valueOf(notesFileInfoFromDatabase3.getNotesFileId()));
                } else {
                    this.notesFilesDAL.addNotesFilesInfoInDatabase(notesFileInfoFromDatabase3);
                }
                NotesFolderDAL notesFolderDAL3 = new NotesFolderDAL(activity);
                new NotesFolderDB_Pojo();
                StringBuilder sb15 = new StringBuilder();
                this.constants.getClass();
                sb15.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
                this.constants.getClass();
                sb15.append("NotesFolderId");
                sb15.append(" = ");
                sb15.append(NotesCommon.CurrentNotesFolderId);
                sb15.append(" AND ");
                this.constants.getClass();
                sb15.append("NotesFolderIsDecoy");
                sb15.append(" = ");
                sb15.append(SecurityLocksCommon.IsFakeAccount);
                NotesFolderDB_Pojo notesFolderInfoFromDatabase3 = notesFolderDAL3.getNotesFolderInfoFromDatabase(sb15.toString());
                notesFolderInfoFromDatabase3.setNotesFolderModifiedDate((String) hashMap.get("note_datetime_m"));
                notesFolderInfoFromDatabase3.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
                this.constants.getClass();
                notesFolderDAL3.updateNotesFolderFromDatabase(notesFolderInfoFromDatabase3, "NotesFolderId", String.valueOf(notesFolderInfoFromDatabase3.getNotesFolderId()));
                SecurityLocksCommon.IsAppDeactive = false;
                activity.startActivity(new Intent(activity, NotesFilesActivity.class));
                activity.finish();
                throw th;
            }
        } else if (!this.noteName.equals(str)) {
            if (this.oldFile.exists()) {
                this.oldFile.renameTo(this.newFile);
            }
        } else if (!this.newFile.exists()) {
            try {
                this.newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        XmlSerializer newSerializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();
        try {
            newSerializer.setOutput(stringWriter);
            newSerializer.startDocument(null, Boolean.valueOf(true));
            newSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            newSerializer.startTag(null, "dict");
            for (Entry entry : hashMap.entrySet()) {
                if (!((String) entry.getKey()).equals("note_datetime_c")) {
                    if (!((String) entry.getKey()).equals("note_datetime_m")) {
                        if (((String) entry.getKey()).equals("Text")) {
                            newSerializer.startTag(null, "key");
                            newSerializer.text((String) entry.getKey());
                            newSerializer.endTag(null, "key");
                            newSerializer.startTag(null, "string");
                            newSerializer.text(Flaes.getencodedstring((String) entry.getValue()));
                            newSerializer.endTag(null, "string");
                        } else {
                            newSerializer.startTag(null, "key");
                            newSerializer.text((String) entry.getKey());
                            newSerializer.endTag(null, "key");
                            newSerializer.startTag(null, "string");
                            newSerializer.text((String) entry.getValue());
                            newSerializer.endTag(null, "string");
                        }
                    }
                }
                newSerializer.startTag(null, "key");
                newSerializer.text((String) entry.getKey());
                newSerializer.endTag(null, "key");
                newSerializer.startTag(null, "date");
                newSerializer.text((String) entry.getValue());
                newSerializer.endTag(null, "date");
            }
            newSerializer.endTag(null, "dict");
            newSerializer.endDocument();
            newSerializer.flush();
            FileOutputStream fileOutputStream = new FileOutputStream(this.newFile);
            fileOutputStream.write(stringWriter.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (z) {
            this.constants.getClass();
            StringBuilder sb16 = new StringBuilder();
            this.constants.getClass();
            sb16.append("NotesFileName");
            sb16.append(" = '");
            sb16.append(str);
            sb16.append("' AND ");
            this.constants.getClass();
            sb16.append("NotesFileIsDecoy");
            sb16.append(" = ");
            sb16.append(SecurityLocksCommon.IsFakeAccount);
            str2 = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ".concat(sb16.toString());
        } else {
            this.constants.getClass();
            StringBuilder sb17 = new StringBuilder();
            this.constants.getClass();
            sb17.append("NotesFileName");
            sb17.append(" = '");
            sb17.append(this.noteName);
            sb17.append("' AND ");
            this.constants.getClass();
            sb17.append("NotesFileIsDecoy");
            sb17.append(" = ");
            sb17.append(SecurityLocksCommon.IsFakeAccount);
            str2 = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ".concat(sb17.toString());
        }
        if (this.newFile.exists()) {
            d = (double) this.newFile.length();
        }
        this.notesFilesDAL = new NotesFilesDAL(activity);
        new NotesFileDB_Pojo();
        NotesFileDB_Pojo notesFileInfoFromDatabase4 = this.notesFilesDAL.getNotesFileInfoFromDatabase(str2);
        notesFileInfoFromDatabase4.setNotesFileFolderId(NotesCommon.CurrentNotesFolderId);
        notesFileInfoFromDatabase4.setNotesFileName(this.noteName);
        notesFileInfoFromDatabase4.setNotesFileText((String) hashMap.get("Text"));
        notesFileInfoFromDatabase4.setNotesFileCreatedDate((String) hashMap.get("note_datetime_c"));
        notesFileInfoFromDatabase4.setNotesFileModifiedDate((String) hashMap.get("note_datetime_m"));
        notesFileInfoFromDatabase4.setNotesfileColor((String) hashMap.get("NoteColor"));
        notesFileInfoFromDatabase4.setNotesFileLocation(this.newFile.getAbsolutePath());
        notesFileInfoFromDatabase4.setNotesFileFromCloud(0);
        notesFileInfoFromDatabase4.setNotesFileSize(d);
        notesFileInfoFromDatabase4.setNotesFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
        if (this.notesFilesDAL.IsFileAlreadyExist(str2)) {
            NotesFilesDAL notesFilesDAL5 = this.notesFilesDAL;
            this.constants.getClass();
            notesFilesDAL5.updateNotesFileInfoInDatabase(notesFileInfoFromDatabase4, "NotesFileId", String.valueOf(notesFileInfoFromDatabase4.getNotesFileId()));
        } else {
            this.notesFilesDAL.addNotesFilesInfoInDatabase(notesFileInfoFromDatabase4);
        }
        NotesFolderDAL notesFolderDAL4 = new NotesFolderDAL(activity);
        new NotesFolderDB_Pojo();
        StringBuilder sb18 = new StringBuilder();
        this.constants.getClass();
        sb18.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
        this.constants.getClass();
        sb18.append("NotesFolderId");
        sb18.append(" = ");
        sb18.append(NotesCommon.CurrentNotesFolderId);
        sb18.append(" AND ");
        this.constants.getClass();
        sb18.append("NotesFolderIsDecoy");
        sb18.append(" = ");
        sb18.append(SecurityLocksCommon.IsFakeAccount);
        NotesFolderDB_Pojo notesFolderInfoFromDatabase4 = notesFolderDAL4.getNotesFolderInfoFromDatabase(sb18.toString());
        notesFolderInfoFromDatabase4.setNotesFolderModifiedDate((String) hashMap.get("note_datetime_m"));
        notesFolderInfoFromDatabase4.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
        this.constants.getClass();
        notesFolderDAL4.updateNotesFolderFromDatabase(notesFolderInfoFromDatabase4, "NotesFolderId", String.valueOf(notesFolderInfoFromDatabase4.getNotesFolderId()));
        SecurityLocksCommon.IsAppDeactive = false;
        activity.startActivity(new Intent(activity, NotesFilesActivity.class));
        activity.finish();
    }
}
