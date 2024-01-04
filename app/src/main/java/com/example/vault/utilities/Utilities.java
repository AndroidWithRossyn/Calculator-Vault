package com.example.vault.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Video.Media;
import androidx.documentfile.provider.DocumentFile;
import android.text.Spannable;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.example.vault.R;
import com.example.vault.dropbox.service.CloudService;
import com.example.vault.dropbox.DropBoxDownloadActivity;
import com.example.vault.dropbox.DropboxLoginActivity;
import com.example.vault.securebackupcloud.CloudCommon;
import com.example.vault.securebackupcloud.CloudCommon.CloudType1;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionSharedPreferences;
import com.example.vault.storageoption.StorageOptionsCommon;

import org.apache.commons.io.IOUtils;

public class Utilities {
    public static void StartCloudActivity(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Cloud", 0);
        boolean z = sharedPreferences.getBoolean("isAppRegisterd", false);
        CloudCommon.CloudType = sharedPreferences.getInt("CloudType", 0);
        Intent intent = null;
        String string = context.getSharedPreferences("DropboxPerf", 0).getString("access-token", null);
        if (!z || string == null) {
            if (!CloudCommon.IsCloudServiceStarted) {
                context.startService(new Intent(context, CloudService.class));
            }
            SecurityLocksCommon.IsAppDeactive = false;
            if (CloudCommon.CloudType == CloudType1.DropBox.ordinal()) {
                intent = new Intent(context, DropboxLoginActivity.class);
            }
            context.startActivity(intent);
            ((Activity) context).finish();
            return;
        }
        if (!CloudCommon.IsCloudServiceStarted) {
            context.startService(new Intent(context, CloudService.class));
        }
        SecurityLocksCommon.IsAppDeactive = false;
        if (CloudCommon.CloudType == CloudType1.DropBox.ordinal()) {
            intent = new Intent(context, DropBoxDownloadActivity.class);
        }
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public static void DeleteFile(String str) throws IOException {
        new File(str).delete();
    }

    public static void hideKeyboard(View view, Context context) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean DeleteAlbum(File file, Context context) throws IOException {
        if (!file.exists() || !file.isDirectory()) {
            return false;
        }
        file.delete();
        return true;
    }

    public static String convertDateToGMT(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(simpleDateFormat.format(instance.getTime()));
        sb.append(" +0000");
        return sb.toString();
    }

    public static String getCurrentDateTime2() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
    }

    public static TextView strikeTextview(TextView textView, String str, boolean z) {
        StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
        if (z) {
            textView.setText(str, BufferType.SPANNABLE);
            ((Spannable) textView.getText()).setSpan(strikethroughSpan, 0, str.length(), 33);
        } else {
            textView.setText(str);
        }
        return textView;
    }

    public static void trimCache(Context context) {
        try {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                deleteDir(cacheDir);
            }
        } catch (Exception unused) {
        }
    }

    public static boolean deleteDir(File file) {
        boolean z = false;
        if (file != null && file.isDirectory()) {
            for (String file2 : file.list()) {
                if (!deleteDir(new File(file, file2))) {
                    return false;
                }
            }
        }
        if (file != null && file.delete()) {
            z = true;
        }
        return z;
    }

    public static String FileSize(String str) {
        FileChannel fileChannel;
        try {
            fileChannel = new FileInputStream(str).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileChannel = null;
        }
        long j = 0;
        try {
            j = fileChannel.size();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        long j2 = j / ((long) 1024);
        if (j2 > 1000) {
            long j3 = j / ((long) 1048576);
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(j3));
            sb.append("mb");
            return sb.toString();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(String.valueOf(j2));
        sb2.append("kb");
        return sb2.toString();
    }

    public static String getFileNameWithoutExtention(String str) {
        String name = new File(str).getName();
        String str2 = "_";
        for (int length = name.length() - 1; length > 0; length--) {
            if (name.charAt(length) == str2.charAt(0)) {
                int lastIndexOf = name.lastIndexOf(".");
                if (lastIndexOf > 0) {
                    name = name.substring(length + 1, lastIndexOf);
                }
                return name;
            }
        }
        return "";
    }

    public static int getNoOfColumns(Context context, int i, boolean z) {
        if (i == 1) {
            return Common.isTablet10Inch(context) ? z ? 4 : 1 : Common.isTablet7Inch(context) ? z ? 3 : 1 : z ? 2 : 1;
        }
        if (i != 2) {
            return 2;
        }
        if (!Common.isTablet10Inch(context)) {
            return Common.isTablet7Inch(context) ? z ? 4 : 1 : z ? 3 : 1;
        }
        if (z) {
            return 5;
        }
        return 1;
    }


    public static String NSHideFile(Context context, File file, File file2) throws IOException {
        FileChannel fileChannel;
        if (file.exists()) {
            if (!file2.exists()) {
                file2.mkdirs();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(file2.getAbsolutePath());
            sb.append("/");
            sb.append(ChangeFileExtention(file.getName()));
            File file3 = new File(sb.toString());
            if (file3.exists()) {
                file3 = GetFileName(ChangeFileExtention(file.getName()), file3, file2.getAbsolutePath());
            }
            file3.createNewFile();
            FileChannel fileChannel2 = null;
            try {
                fileChannel = new FileInputStream(file).getChannel();
                try {
                    fileChannel2 = new FileOutputStream(file3, true).getChannel();
                    if (file.getAbsolutePath().contains(StorageOptionsCommon.STORAGEPATH)) {
                        file.renameTo(file3);
                    } else {
                        long size = fileChannel.size();
                        long abs = Math.abs(size / ((long) 1048576));
                        if (abs > ((long) Common.MaxFileSizeInMB)) {
                            int CalculateChunkCounts = CalculateChunkCounts(abs);
                            double d = (double) size;
                            double d2 = (double) CalculateChunkCounts;
                            Double.isNaN(d);
                            Double.isNaN(d2);
                            int abs2 = Math.abs((int) Math.ceil(d / d2));
                            for (int i = 0; i < CalculateChunkCounts; i++) {
                                fileChannel.transferTo((long) (abs2 * i), (long) abs2, fileChannel2);
                            }
                        } else {
                            fileChannel.transferTo(0, size, fileChannel2);
                        }
                        if (fileChannel != null) {
                            fileChannel.close();
                        }
                        if (fileChannel2 != null) {
                            fileChannel2.close();
                        }
                        if (file.exists() && file3.exists()) {
                            file.delete();
                        }
                    }
                    return file3.getAbsolutePath();
                } catch (Exception unused) {

                    Log.e("TAG", "NSHideFile: "+unused.toString());
                    if (fileChannel != null) {
                    }
                    if (fileChannel2 != null) {
                    }
                    return "";
                }
            } catch (Exception unused2) {

                Log.e("TAG", "NSHideFile unused2: "+unused2.toString());
                fileChannel = null;
                if (fileChannel != null) {
                    fileChannel.close();
                }
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                return "";
            }
        }
        return "";
    }

    public static boolean NSUnHideFile(Context context, String str, String str2) throws IOException {
        if (VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
            str2 = FileName(str2);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Environment.getExternalStorageDirectory().getPath());
            stringBuilder.append(Common.UnhideKitkatAlbumName);
            stringBuilder.append(str2);
            str2 = stringBuilder.toString();
        }
        File file = new File(str);
        File file2 = new File(str2);
        if (file2.exists()) {
            file2 = GetDesFileNameForUnHide(file2.getAbsolutePath(), file2.getName(), file2);
        }
        File file3 = new File(file2.getParent());
        if (!(file3.exists() || file3.mkdirs())) {
            str = FileName(str2);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(Environment.getExternalStorageDirectory().getPath());
            stringBuilder2.append(Common.UnhideKitkatAlbumName);
            stringBuilder2.append(str);
            File file4 = new File(stringBuilder2.toString());
            file2 = file4.exists() ? GetDesFileNameForUnHide(file4.getAbsolutePath(), file4.getName(), file4) : file4;
            if (!(file3.exists() || file3.mkdirs())) {
                return false;
            }
        }
        if (file2.createNewFile() && file3.exists()) {
            FileChannel fileChannel = null;
            try {
                if (!(file2.getAbsolutePath().contains(StorageOptionsCommon.STORAGEPATH) ? file.renameTo(file2) : false)) {
                    try {
                        long size = fileChannel.size();
                        long abs = Math.abs(size / ((long) 1048576));
                        if (abs > ((long) Common.MaxFileSizeInMB)) {
                            int CalculateChunkCounts = CalculateChunkCounts(abs);
                            double d = (double) size;
                            double d2 = (double) CalculateChunkCounts;
                            Double.isNaN(d);
                            Double.isNaN(d2);
                            int abs2 = Math.abs((int) Math.ceil(d / d2));
                            for (int i = 0; i < CalculateChunkCounts; i++) {
                                FileChannel fileChannel2 = null;
                                fileChannel2.transferTo((long) ((abs2 * i) + 0), (long) abs2, null);
                            }
                        } else {
                            FileChannel fileChannel3 = null;
                            fileChannel3.transferTo(0, size, null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                    intent.setData(Uri.fromFile(new File(file2.getAbsolutePath())));
                    context.sendBroadcast(intent);
                }
                if (file.exists() && file2.exists()) {
                    NSDecryption(file2);
                    file.delete();
                } else {
                    NSDecryption(file2);
                }
                return true;
            } catch (Exception unused) {
            }
        }
        return false;
    }

    public static File GetDesFileNameForUnHide(String str, String str2, File file) {
        StringBuilder sb = new StringBuilder();
        sb.append(str.substring(0, str.lastIndexOf(47)));
        sb.append("/");
        String sb2 = sb.toString();
        String substring = str2.substring(0, str2.lastIndexOf(46));
        String str3 = "";
        int lastIndexOf = str2.lastIndexOf(46);
        if (lastIndexOf > Math.max(str2.lastIndexOf(47), str2.lastIndexOf(92))) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(".");
            sb3.append(str2.substring(lastIndexOf + 1));
            str3 = sb3.toString();
        }
        for (int i = 0; i < 100; i++) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(substring);
            sb4.append("(");
            sb4.append(i);
            sb4.append(")");
            sb4.append(str3);
            String sb5 = sb4.toString();
            StringBuilder sb6 = new StringBuilder();
            sb6.append(sb2);
            sb6.append("/");
            sb6.append(sb5);
            file = new File(sb6.toString());
            if (!file.exists()) {
                return file;
            }
        }
        return file;
    }

    public static boolean MoveFileWithinDirectories(String str, String str2) throws IOException {
        File file = new File(str);
        File file2 = new File(str2);
        File file3 = new File(file2.getParent());
        if (!file3.exists()) {
            file3.mkdirs();
        }
        if (file2.exists()) {
            if (file.renameTo(GetFileName(file.getName(), file2, file2.getParent()))) {
                return true;
            }
        } else if (file.renameTo(file2)) {
            return true;
        }
        return false;
    }


    public static String CopyTemporaryFile(Context context, String str, String str2) throws IOException {
        FileChannel fileChannel;
        File file = new File(str);
        String FileName = FileName(str);
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append(ChangeFileExtentionToOrignal(FileName));
        File file2 = new File(sb.toString());
        File file3 = new File(file2.getParent());
        if (!file3.exists() && !file3.mkdirs()) {
            String FileName2 = FileName(str2);
            StringBuilder sb2 = new StringBuilder();
            sb2.append(Environment.getExternalStorageDirectory().getPath());
            sb2.append("/");
            sb2.append(FileName2);
            file2 = new File(sb2.toString());
            if (!file3.exists()) {
                file3.mkdirs();
            }
        }
        file2.createNewFile();
        if (file3.exists()) {
            FileChannel fileChannel2 = null;
            try {
                fileChannel = new FileInputStream(file).getChannel();
                try {
                    fileChannel2 = new FileOutputStream(file2).getChannel();
                    long size = fileChannel.size();
                    long abs = Math.abs(size / ((long) 1048576));
                    if (abs > ((long) Common.MaxFileSizeInMB)) {
                        int CalculateChunkCounts = CalculateChunkCounts(abs);
                        double d = (double) size;
                        double d2 = (double) CalculateChunkCounts;
                        Double.isNaN(d);
                        Double.isNaN(d2);
                        int abs2 = Math.abs((int) Math.ceil(d / d2));
                        for (int i = 0; i < CalculateChunkCounts; i++) {
                            fileChannel.transferTo((long) ((abs2 * i) + 0), (long) abs2, fileChannel2);
                        }
                    } else {
                        fileChannel.transferTo(0, size, fileChannel2);
                    }
                    if (fileChannel != null) {
                        fileChannel.close();
                    }
                    if (fileChannel2 != null) {
                        fileChannel2.close();
                    }
                    NSDecryption(file2);
                    return file2.getAbsolutePath();
                } catch (Exception unused) {
                    if (fileChannel != null) {
                    }
                    if (fileChannel2 != null) {
                    }
                    return file2.getAbsolutePath();
                }
            } catch (Exception unused2) {
                fileChannel = null;
                if (fileChannel != null) {
                    fileChannel.close();
                }
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                return file2.getAbsolutePath();
            }
        }
        return file2.getAbsolutePath();
    }


    public static boolean UnHideThumbnail(Context context, String str, String str2) throws IOException {
        FileChannel fileChannel;
        File file = new File(str);
        File file2 = new File(str2);
        if (file2.exists()) {
            file2 = GetDesFileNameForUnHide(file2.getAbsolutePath(), file2.getName(), file2);
        }
        File file3 = new File(file2.getParent());
        if ((file3.exists() || file3.mkdirs() || file2.createNewFile()) && file3.exists()) {
            FileChannel fileChannel2 = null;
            try {
                fileChannel = new FileInputStream(file).getChannel();
                try {
                    FileChannel channel = new FileOutputStream(file2).getChannel();
                    fileChannel.transferTo(0, fileChannel.size(), channel);
                    if (VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                        intent.setData(Uri.fromFile(new File(file2.getAbsolutePath())));
                        context.sendBroadcast(intent);
                    }
                    if (fileChannel != null) {
                        fileChannel.close();
                    }
                    if (channel != null) {
                        channel.close();
                    }
                    if (file.exists() && file2.exists()) {
                        file.delete();
                    }
                    return true;
                } catch (Exception unused) {
                    if (fileChannel != null) {
                    }
                    if (fileChannel2 != null) {
                    }
                    return false;
                }
            } catch (Exception unused2) {
                fileChannel = fileChannel2;
                if (fileChannel != null) {
                    fileChannel.close();
                }
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                return false;
            }
        }
        return false;
    }


    public static String RecoveryHideFileSDCard(Context context, File file, File file2) throws IOException {
        File file3;
        FileChannel fileChannel;
        if (file.exists()) {
            if (!file2.exists()) {
                file2.mkdirs();
            }
            if (file.getName().contains("#")) {
                StringBuilder sb = new StringBuilder();
                sb.append(file2.getAbsolutePath());
                sb.append("/");
                sb.append(file.getName());
                file3 = new File(sb.toString());
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(file2.getAbsolutePath());
                sb2.append("/");
                sb2.append(ChangeFileExtention(file.getName()));
                file3 = new File(sb2.toString());
            }
            if (file3.exists()) {
                file3 = GetFileName(file.getName(), file3, file2.getAbsolutePath());
            }
            file3.createNewFile();
            FileChannel fileChannel2 = null;
            try {
                fileChannel = new FileInputStream(file).getChannel();
                try {
                    fileChannel2 = new FileOutputStream(file3).getChannel();
                    if (file.getAbsolutePath().contains(StorageOptionsCommon.STORAGEPATH)) {
                        file.renameTo(file3);
                    } else {
                        long size = fileChannel.size();
                        long abs = Math.abs(size / ((long) 1048576));
                        if (abs > ((long) Common.MaxFileSizeInMB)) {
                            int CalculateChunkCounts = CalculateChunkCounts(abs);
                            double d = (double) size;
                            double d2 = (double) CalculateChunkCounts;
                            Double.isNaN(d);
                            Double.isNaN(d2);
                            int abs2 = Math.abs((int) Math.ceil(d / d2));
                            for (int i = 0; i < CalculateChunkCounts; i++) {
                                fileChannel.transferTo((long) ((abs2 * i) + 0), (long) abs2, fileChannel2);
                            }
                        } else {
                            fileChannel.transferTo(0, size, fileChannel2);
                        }
                    }
                    if (VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                        intent.setData(Uri.fromFile(new File(file.getAbsolutePath())));
                        context.sendBroadcast(intent);
                    }
                    if (fileChannel != null) {
                        fileChannel.close();
                    }
                    if (fileChannel2 != null) {
                        fileChannel2.close();
                    }
                    if (file.exists() && file3.exists()) {
                        file.delete();
                    }
                    return file3.getAbsolutePath();
                } catch (Exception unused) {
                    if (fileChannel != null) {
                    }
                    if (fileChannel2 != null) {
                    }
                    return "";
                }
            } catch (Exception unused2) {
                fileChannel = null;
                if (fileChannel != null) {
                    fileChannel.close();
                }
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                return "";
            }
        }
        return "";
    }


    public static String RecoveryEntryFile(Context context, File file, File file2) throws IOException {
        FileChannel fileChannel;
        if (file.exists()) {
            if (!file2.exists()) {
                file2.createNewFile();
            }
            FileChannel fileChannel2 = null;
            try {
                fileChannel = new FileInputStream(file).getChannel();
                try {
                    fileChannel2 = new FileOutputStream(file2).getChannel();
                    if (file.getAbsolutePath().contains(StorageOptionsCommon.STORAGEPATH)) {
                        file.renameTo(file2);
                    } else {
                        long size = fileChannel.size();
                        long abs = Math.abs(size / ((long) 1048576));
                        if (abs > ((long) Common.MaxFileSizeInMB)) {
                            int CalculateChunkCounts = CalculateChunkCounts(abs);
                            double d = (double) size;
                            double d2 = (double) CalculateChunkCounts;
                            Double.isNaN(d);
                            Double.isNaN(d2);
                            int abs2 = Math.abs((int) Math.ceil(d / d2));
                            for (int i = 0; i < CalculateChunkCounts; i++) {
                                fileChannel.transferTo((long) ((abs2 * i) + 0), (long) abs2, fileChannel2);
                            }
                        } else {
                            fileChannel.transferTo(0, size, fileChannel2);
                        }
                    }
                    if (VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                        intent.setData(Uri.fromFile(new File(file.getAbsolutePath())));
                        context.sendBroadcast(intent);
                    }
                    if (fileChannel != null) {
                        fileChannel.close();
                    }
                    if (fileChannel2 != null) {
                        fileChannel2.close();
                    }
                    if (file.exists() && file2.exists()) {
                        file.delete();
                    }
                    return file2.getAbsolutePath();
                } catch (Exception unused) {
                    if (fileChannel != null) {
                        fileChannel.close();
                    }
                    if (fileChannel2 != null) {
                        fileChannel2.close();
                    }
                    return file2.getAbsolutePath();
                }
            } catch (Exception unused2) {
                fileChannel = null;
                if (fileChannel != null) {
                }
                if (fileChannel2 != null) {
                }
                return file2.getAbsolutePath();
            }
        }
        return file2.getAbsolutePath();
    }

    private static int CalculateChunkCounts(long j) {
        int i = 2;
        do {
            i++;
        } while (j / ((long) i) > ((long) Common.MaxFileSizeInMB));
        return i;
    }

    public static void NSEncryption(File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        byte[] bArr = new byte[Common.EncryptBytesSize];
        randomAccessFile.read(bArr, 0, Common.EncryptBytesSize);
        randomAccessFile.seek(0);
        randomAccessFile.write(ReverseBytes(bArr));
        randomAccessFile.close();
    }

    public static void NSDecryption(File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        byte[] bArr = new byte[Common.EncryptBytesSize];
        randomAccessFile.read(bArr, 0, Common.EncryptBytesSize);
        randomAccessFile.seek(0);
        randomAccessFile.write(ReverseBytes(bArr));
        randomAccessFile.close();
    }

    public static String NSVideoDecryptionDuringPlay(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(file.getParent());
        sb.append(File.separator);
        sb.append(ChangeFileExtentionToOrignal(FileName(file.getAbsolutePath())));
        File file2 = new File(sb.toString());
        file.renameTo(file2);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "rw");
        byte[] bArr = new byte[Common.EncryptBytesSize];
        randomAccessFile.read(bArr, 0, Common.EncryptBytesSize);
        randomAccessFile.seek(0);
        randomAccessFile.write(ReverseBytes(bArr));
        randomAccessFile.close();
        return file2.getAbsolutePath();
    }

    public static String NSVideoEncryptionAfterPlay(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(file.getParent());
        sb.append(File.separator);
        sb.append(ChangeFileExtention(FileName(file.getAbsolutePath())));
        File file2 = new File(sb.toString());
        file.renameTo(file2);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "rw");
        byte[] bArr = new byte[Common.EncryptBytesSize];
        randomAccessFile.read(bArr, 0, Common.EncryptBytesSize);
        randomAccessFile.seek(0);
        randomAccessFile.write(ReverseBytes(bArr));
        randomAccessFile.close();
        return file2.getAbsolutePath();
    }

    public static byte[] ReverseBytes(byte[] bArr) {
        if (bArr == null) {
            return bArr;
        }
        int length = bArr.length - 1;
        for (int i = 0; length > i; i++) {
            byte b = bArr[length];
            bArr[length] = bArr[i];
            bArr[i] = b;
            length--;
        }
        return bArr;
    }

    public static String ChangeFileExtentionToOrignal(String str) {
        String substring = str.substring(0, str.lastIndexOf(35));
        int lastIndexOf = str.lastIndexOf(35);
        StringBuilder sb = new StringBuilder();
        sb.append(".");
        sb.append(str.substring(lastIndexOf + 1));
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(substring);
        sb3.append(sb2);
        return sb3.toString();
    }

    public static String FileName(String str) {
        String str2 = " /";
        for (int length = str.length() - 1; length > 0; length--) {
            if (str.charAt(length) == str2.charAt(1)) {
                return str.substring(length + 1, str.length());
            }
        }
        return "";
    }

    private static File GetFileName(String str, File file, String str2) {
        int i = 0;
        if (str.contains("#")) {
            String substring = str.substring(0, str.lastIndexOf(35));
            String str3 = "";
            int lastIndexOf = str.lastIndexOf(35);
            if (lastIndexOf > Math.max(str.lastIndexOf(47), str.lastIndexOf(92))) {
                StringBuilder sb = new StringBuilder();
                sb.append("#");
                sb.append(str.substring(lastIndexOf + 1));
                str3 = sb.toString();
            }
            while (i < 100) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(substring);
                sb2.append("(");
                sb2.append(i);
                sb2.append(")");
                sb2.append(str3);
                String sb3 = sb2.toString();
                StringBuilder sb4 = new StringBuilder();
                sb4.append(str2);
                sb4.append("/");
                sb4.append(sb3);
                file = new File(sb4.toString());
                if (!file.exists()) {
                    return file;
                }
                i++;
            }
        } else {
            String substring2 = str.substring(0, str.lastIndexOf(46));
            String str4 = "";
            int lastIndexOf2 = str.lastIndexOf(46);
            if (lastIndexOf2 > Math.max(str.lastIndexOf(47), str.lastIndexOf(92))) {
                StringBuilder sb5 = new StringBuilder();
                sb5.append(".");
                sb5.append(str.substring(lastIndexOf2 + 1));
                str4 = sb5.toString();
            }
            while (i < 100) {
                StringBuilder sb6 = new StringBuilder();
                sb6.append(substring2);
                sb6.append("(");
                sb6.append(i);
                sb6.append(")");
                sb6.append(str4);
                String sb7 = sb6.toString();
                StringBuilder sb8 = new StringBuilder();
                sb8.append(str2);
                sb8.append("/");
                sb8.append(sb7);
                file = new File(sb8.toString());
                if (!file.exists()) {
                    return file;
                }
                i++;
            }
        }
        return file;
    }

    public static String ChangeFileExtention(String str) {
        String substring = str.substring(0, str.lastIndexOf(46));
        int lastIndexOf = str.lastIndexOf(46);
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(str.substring(lastIndexOf + 1));
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(substring);
        sb3.append(sb2);
        return sb3.toString();
    }

    public static void CheckDeviceStoragePaths(Context context) {
        new ArrayList();
        if (VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
            StorageOptionsCommon.IsStorageSDCard = context.getSharedPreferences("StorageOption", Context.MODE_MULTI_PROCESS).getBoolean("IsStorageSDCard", false);
            ArrayList externalMountss = getExternalMountss();
            if (externalMountss.size() > 0) {
                StorageOptionsCommon.IsDeviceHaveMoreThenOneStorage = true;
                for (int i = 0; i < externalMountss.size(); i++) {
                    String str = (String) externalMountss.get(i);
                    String[] split = str.split("/");
                    String str2 = split[2];
                    if (!str2.equals("sdcard") && !str2.equals("sdcard0") && new File(str).exists()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(str);
                        sb.append("/");
                        StorageOptionsCommon.STORAGEPATH_2 = sb.toString();
                    } else if (str2.equals("media_rw")) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("/");
                        sb2.append(split[1]);
                        sb2.append("/");
                        sb2.append(split[3]);
                        sb2.append("/");
                        StorageOptionsCommon.STORAGEPATH_2 = sb2.toString();
                    }
                }
            } else {
                StorageOptionsCommon.IsDeviceHaveMoreThenOneStorage = false;
            }
        } else {
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File file = new File(externalStoragePublicDirectory.getParent());
            if (new File("/storage/sdcard/").exists()) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(externalStoragePublicDirectory.getParent());
                sb3.append(File.separator);
                StorageOptionsCommon.STORAGEPATH = sb3.toString();
            }
            if (new File("/storage/sdcard0/").exists()) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(externalStoragePublicDirectory.getParent());
                sb4.append(File.separator);
                StorageOptionsCommon.STORAGEPATH = sb4.toString();
            } else if (file.exists()) {
                StringBuilder sb5 = new StringBuilder();
                sb5.append(externalStoragePublicDirectory.getParent());
                sb5.append(File.separator);
                StorageOptionsCommon.STORAGEPATH = sb5.toString();
            }
            StringBuilder sb6 = new StringBuilder();
            sb6.append(StorageOptionsCommon.STORAGEPATH);
            sb6.append(StorageOptionsCommon.PHOTOS);
            sb6.append(StorageOptionsCommon.PHOTOS_DEFAULT_ALBUM);
            File file2 = new File(sb6.toString());
            if (!file2.exists()) {
                file2.mkdirs();
                boolean exists = file2.exists();
            }
            StringBuilder sb7 = new StringBuilder();
            sb7.append(StorageOptionsCommon.STORAGEPATH);
            sb7.append(StorageOptionsCommon.STORAGE);
            File file3 = new File(sb7.toString(), "don't delete this folder.txt");
            String str3 = "Warning! \nDo Not Delete this folder; it contains Calculator Vault Encrypted data.";
            if (!file3.exists()) {
                try {
                    file3.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file3);
                    fileOutputStream.write(str3.getBytes());
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Editor edit = context.getSharedPreferences("StorageOption", Context.MODE_MULTI_PROCESS).edit();
            edit.putString("STORAGEPATH", StorageOptionsCommon.STORAGEPATH);
            edit.commit();
        }
        File externalStoragePublicDirectory2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File file4 = new File(externalStoragePublicDirectory2.getParent());
        File file5 = new File(StorageOptionsCommon.STORAGEPATH_2);
        if (file4.exists()) {
            StringBuilder sb8 = new StringBuilder();
            sb8.append(externalStoragePublicDirectory2.getParent());
            sb8.append(File.separator);
            StorageOptionsCommon.STORAGEPATH_1 = sb8.toString();
        }
        StringBuilder sb9 = new StringBuilder();
        sb9.append(StorageOptionsCommon.STORAGEPATH_1);
        sb9.append(StorageOptionsCommon.PHOTOS);
        sb9.append(StorageOptionsCommon.PHOTOS_DEFAULT_ALBUM);
        File file6 = new File(sb9.toString());
        if (!file6.exists()) {
            file6.mkdirs();
            boolean exists2 = file6.exists();
        }
        StringBuilder sb10 = new StringBuilder();
        sb10.append(StorageOptionsCommon.STORAGEPATH);
        sb10.append(StorageOptionsCommon.STORAGE);
        File file7 = new File(sb10.toString(), "don't delete this folder.txt");
        String str4 = "Warning! \nDo Not Delete this folder, it contains Calculator Vault data.";
        if (!file7.exists()) {
            try {
                file7.createNewFile();
                FileOutputStream fileOutputStream2 = new FileOutputStream(file7);
                fileOutputStream2.write(str4.getBytes());
                fileOutputStream2.flush();
                fileOutputStream2.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        if (!file5.exists()) {
            StorageOptionsCommon.IsDeviceHaveMoreThenOneStorage = false;
        }
    }

    public static Bitmap DecodeFile(File file) {
        try {
            Options options = new Options();
            int i = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            while ((options.outWidth / i) / 2 >= 70 && (options.outHeight / i) / 2 >= 70) {
                i *= 2;
            }
            Options options2 = new Options();
            options2.inSampleSize = i;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, options2);
        } catch (FileNotFoundException unused) {
            return null;
        }
    }

    public static ArrayList<String> getExternalMountss() {
        String[] split;
        String[] split2;
        ArrayList<String> arrayList = new ArrayList<>();
        String str = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        String str2 = "";
        try {
            Process start = new ProcessBuilder(new String[0]).command(new String[]{"mount"}).redirectErrorStream(true).start();
            start.waitFor();
            InputStream inputStream = start.getInputStream();
            byte[] bArr = new byte[1024];
            while (inputStream.read(bArr) != -1) {
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append(new String(bArr));
                str2 = sb.toString();
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String str3 : str2.split(IOUtils.LINE_SEPARATOR_UNIX)) {
            if (!str3.toLowerCase(Locale.US).contains("asec") && str3.matches(str)) {
                for (String str4 : str3.split(" ")) {
                    if (str4.startsWith("/") && !str4.toLowerCase(Locale.US).contains("vold")) {
                        arrayList.add(str4);
                    }
                }
            }
        }
        return arrayList;
    }

    public static int getScreenOrientation(Context context) {
        Activity activity = (Activity) context;
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        defaultDisplay.getOrientation();
        int i = activity.getResources().getConfiguration().orientation;
        if (i != 0) {
            return i;
        }
        if (defaultDisplay.getWidth() == defaultDisplay.getHeight()) {
            return 3;
        }
        return defaultDisplay.getWidth() < defaultDisplay.getHeight() ? 1 : 2;
    }

    public static boolean isNetworkOnline(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(0);
            boolean z = true;
            if (networkInfo == null || networkInfo.getState() != State.CONNECTED) {
                NetworkInfo networkInfo2 = connectivityManager.getNetworkInfo(1);
                if (networkInfo2 == null || networkInfo2.getState() != State.CONNECTED) {
                    z = false;
                }
            }
            return z;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getCurrentDateTime() {
        return new SimpleDateFormat("EEE dd MMM yyyy, HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static int convertDptoPix(Context context, int i) {
        return (int) TypedValue.applyDimension(1, (float) i, context.getResources().getDisplayMetrics());
    }

    public static Uri getLastPhotoOrVideo(Context context) {
        Cursor query = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "date_added"}, null, null, "date_added DESC");
        int columnIndexOrThrow = query.getColumnIndexOrThrow("_data");
        query.moveToFirst();
        String string = query.getString(columnIndexOrThrow);
        query.close();
        return Uri.fromFile(new File(string));
    }

    public static void changeFileExtention(String str) {
        File[] listFiles;
        File[] listFiles2;
        File file = null;
        try {
            if (str.equals(StorageOptionsCommon.VIDEOS)) {
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append(StorageOptionsCommon.VIDEOS);
                sb.append("/");
                file = new File(sb.toString());
            }
            if (str.equals(StorageOptionsCommon.DOCUMENTS)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(StorageOptionsCommon.STORAGEPATH);
                sb2.append(StorageOptionsCommon.DOCUMENTS);
                sb2.append("/");
                file = new File(sb2.toString());
            }
            if (file.exists()) {
                for (File file2 : file.listFiles()) {
                    if (file2.isDirectory()) {
                        for (File file3 : file2.listFiles()) {
                            if (file3.isFile()) {
                                String absolutePath = file3.getAbsolutePath();
                                if (!FileName(absolutePath).contains("#")) {
                                    NSVideoEncryptionAfterPlay(new File(absolutePath));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception unused) {
            Log.v("changeVideosExtention", "error in changeVideosExtention method");
        }
    }

    public static void DeleteSDcardImageLollipop(Context context, String str) {
        DeleteFile(context, str, DocumentFile.fromTreeUri(context, Uri.parse(StorageOptionSharedPreferences.GetObject(context).GetSDCardUri())));
    }

    public static void DeleteFile(Context context, String str, DocumentFile documentFile) {
        File file = new File(str);
        traverseDoc(context, documentFile, new ArrayList(Arrays.asList(file.getParent().split("/"))), file.getName());
    }

    @SuppressLint({"NewApi"})
    public static void traverseDoc(Context context, DocumentFile documentFile, ArrayList<String> arrayList, String str) {
        DocumentFile[] listFiles;
        for (DocumentFile documentFile2 : documentFile.listFiles()) {
            if (documentFile2.isDirectory()) {
                if (arrayList.contains(documentFile2.getName())) {
                    traverseDoc(context, documentFile2, arrayList, str);
                    return;
                }
            } else if (documentFile2.isFile() && documentFile2.getName().equals(str)) {
                try {
                    if (DocumentsContract.deleteDocument(context.getContentResolver(), documentFile2.getUri())) {
                        return;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint({"NewApi"})
    public static boolean needPermissionForBlocking(Context context) {
        boolean z = true;
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            if (((AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE)).checkOpNoThrow("android:get_usage_stats", applicationInfo.uid, applicationInfo.packageName) == 0) {
                z = false;
            }
            return z;
        } catch (NameNotFoundException unused) {
            return true;
        }
    }

    public static void hideMenuItems(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).getItemId() != R.id.action_buy) {
                menu.getItem(i).setVisible(true);
            } else if (Common.isPurchased) {
                menu.getItem(i).setVisible(false);
            } else {
                menu.getItem(i).setVisible(true);
            }
        }
    }
}
