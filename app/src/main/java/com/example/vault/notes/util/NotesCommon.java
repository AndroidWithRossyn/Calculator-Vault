package com.example.vault.notes.util;

import android.app.Activity;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class NotesCommon {
    public static String CurrentNotesFile = null;
    public static String CurrentNotesFolder = null;
    public static int CurrentNotesFolderId = 0;
    public static boolean isEdittingNote = false;
    HashMap<String, String> hashMap;
    HashMap<String, String> hashMap2;
    SaveNoteInXML saveNoteInXML;

    public void createNote(Activity activity, String str, String str2, String str3, String str4, String str5, String str6, String str7, boolean z) {
        this.hashMap = new HashMap<>();
        this.hashMap.put("Title", str3);
        this.hashMap.put("Text", str2);
        this.hashMap.put("audioData", str5);
        this.hashMap.put("NoteColor", str);
        this.hashMap.put("note_datetime_c", str6);
        this.hashMap.put("note_datetime_m", str7);
        this.hashMap.put("note_id", "A5688328-2CE5-4B5A-8668-0D015F3C113A");
        this.saveNoteInXML = new SaveNoteInXML();
        this.saveNoteInXML.SaveNote(activity, this.hashMap, str4, z);
    }

    public String getEncodedAudio(String str) {
        String str2 = "";
        try {
            return Base64.encodeToString(FileUtils.readFileToByteArray(new File(str)), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return str2;
        }
    }

    public void getDecodedAudio(String str, String str2) {
        try {
            byte[] decode = Base64.decode(str, 0);
            FileOutputStream fileOutputStream = new FileOutputStream(str2);
            fileOutputStream.write(decode);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LinedEditText setEditTextFullPage(LinedEditText linedEditText) {
        int height = linedEditText.getHeight() / linedEditText.getLineHeight();
        if (linedEditText.getLineCount() > height) {
            height = linedEditText.getLineCount();
        }
        for (int i = 0; i < height; i++) {
            linedEditText.append(IOUtils.LINE_SEPARATOR_UNIX);
        }
        linedEditText.setSelection(0);
        return linedEditText;
    }

    public String getCurrentDate() {
        return new SimpleDateFormat("EEE d MMM yyyy, HH:mm:ss aaa").format(Calendar.getInstance().getTime());
    }

    public String convertDateToGMT() {
        Calendar instance = Calendar.getInstance();
        TimeZone timeZone = instance.getTimeZone();
        int rawOffset = timeZone.getRawOffset();
        if (timeZone.inDaylightTime(new Date())) {
            rawOffset += timeZone.getDSTSavings();
        }
        int i = (rawOffset / 1000) / 60;
        int i2 = i / 60;
        int i3 = i % 60;
        instance.add(11, -i2);
        instance.add(12, -i3);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss aaa");
        StringBuilder sb = new StringBuilder();
        sb.append(simpleDateFormat.format(instance.getTime()));
        sb.append(" +0000");
        return sb.toString();
    }

    public static String Gettime() {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static String GetDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
    }

    public int getProgressPercentage(long j, long j2) {
        Double.valueOf(0.0d);
        double d = (double) ((long) ((int) (j / 1000)));
        double d2 = (double) ((long) ((int) (j2 / 1000)));
        Double.isNaN(d);
        Double.isNaN(d2);
        return Double.valueOf((d / d2) * 100.0d).intValue();
    }

    public int progressToTimer(int i, int i2) {
        int i3 = i2 / 1000;
        double d = (double) i;
        Double.isNaN(d);
        double d2 = d / 100.0d;
        double d3 = (double) i3;
        Double.isNaN(d3);
        return ((int) (d2 * d3)) * 1000;
    }

    public String milliSecondsToTimer(long j) {
        String str;
        String str2 = "";
        int i = (int) (j / 3600000);
        long j2 = j % 3600000;
        int i2 = ((int) j2) / 60000;
        int i3 = (int) ((j2 % 60000) / 1000);
        if (i > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(i);
            sb.append(":");
            str2 = sb.toString();
        }
        if (i3 < 10) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("0");
            sb2.append(i3);
            str = sb2.toString();
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("");
            sb3.append(i3);
            str = sb3.toString();
        }
        StringBuilder sb4 = new StringBuilder();
        sb4.append(str2);
        sb4.append(i2);
        sb4.append(":");
        sb4.append(str);
        return sb4.toString();
    }

    public boolean hasNoData(String str) {
        return Pattern.compile("[\\n\\r ]+$").matcher(str).matches();
    }

    public boolean isNoSpecialCharsInName(String str) {
        return Pattern.compile("^[a-zA-Z.0-9 -]+$").matcher(str).matches();
    }
}
