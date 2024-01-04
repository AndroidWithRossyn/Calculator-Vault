package com.example.vault.utilities;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;

public class FileUtils {
    private static final String DIRECTORY = Environment.getExternalStorageDirectory().getPath();
    private static final String DIRECTORY2 = "/mnt/extSdCard";
    private static final String DIRECTORY3 = "/storage/sdcard1";
    ArrayList<String> filterRemove = new ArrayList<>();

    public ArrayList<File> FindFiles(String[] strArr) {
        ArrayList<File> arrayList = new ArrayList<>();
        FilenameFilter[] filenameFilterArr = new FilenameFilter[strArr.length];
        int i = 0;
        for (final String strs : strArr) {
            filenameFilterArr[i] = new FilenameFilter() {
                public boolean accept(File file, String str) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(".");
                    sb.append(strs);
                    return str.endsWith(sb.toString());
                }
            };
            i++;
        }
        for (File add : listFilesAsArray(new File(DIRECTORY), filenameFilterArr, -1)) {
            arrayList.add(add);
        }
        File file = new File(DIRECTORY2);
        if (file.exists() && file.isDirectory()) {
            for (File add2 : listFilesAsArray(new File(DIRECTORY2), filenameFilterArr, -1)) {
                arrayList.add(add2);
            }
        }
        File file2 = new File(DIRECTORY3);
        if (file2.exists() && file2.isDirectory()) {
            for (File add3 : listFilesAsArray(new File(DIRECTORY3), filenameFilterArr, -1)) {
                arrayList.add(add3);
            }
        }
        return arrayList;
    }

    public static void deleteDirectory(File file) throws IOException {
        if (file.exists()) {
            if (!isSymlink(file)) {
                org.apache.commons.io.FileUtils.cleanDirectory(file);
            }
            if (!file.delete()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Unable to delete directory ");
                sb.append(file);
                sb.append(".");
                throw new IOException(sb.toString());
            }
        }
    }

    private void saveArray(String str, List<String> list) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(str)));
            objectOutputStream.writeObject(list);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private List<String> loadArray(String str) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new GZIPInputStream(new FileInputStream(str)));
            List<String> list = (List) objectInputStream.readObject();
            objectInputStream.close();
            return list;
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }

    private File[] listFilesAsArray(File file, FilenameFilter[] filenameFilterArr, int i) {
        Collection listFiles = listFiles(file, filenameFilterArr, i);
        return (File[]) listFiles.toArray(new File[listFiles.size()]);
    }

    private Collection<File> listFiles(File file, FilenameFilter[] filenameFilterArr, int i) {
        Vector vector = new Vector();
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            int i2 = i;
            for (File file2 : listFiles) {
                for (FilenameFilter filenameFilter : filenameFilterArr) {
                    if (filenameFilterArr == null || filenameFilter.accept(file, file2.getName())) {
                        vector.add(file2);
                        StringBuilder sb = new StringBuilder();
                        sb.append("Added: ");
                        sb.append(file2.getName());
                        Log.v("FileUtils", sb.toString());
                    }
                }
                if (i2 <= -1 || (i2 > 0 && file2.isDirectory())) {
                    int i3 = i2 - 1;
                    vector.addAll(listFiles(file2, filenameFilterArr, i3));
                    i2 = i3 + 1;
                }
            }
        }
        return vector;
    }

    public ArrayList<File> FindFilesForMiscellaneous(String[] strArr) {
        File[] listFilesAsArrayMiscellaneous;
        File[] listFilesAsArrayMiscellaneous2;
        ArrayList<File> arrayList = new ArrayList<>();
        SetRemoveFilesExt();
        FilenameFilter[] filenameFilterArr = new FilenameFilter[strArr.length];
        int i = 0;
        for (String str : strArr) {
            filenameFilterArr[i] = new FilenameFilter() {
                public boolean accept(File file, String str) {
                    return true;
                }
            };
            i++;
        }
        for (File file : listFilesAsArrayMiscellaneous(new File(DIRECTORY), filenameFilterArr, -1)) {
            if (!file.getParent().substring(0, 1).contains(".") && file.getName().contains(".") && !file.getName().substring(0, 1).contains(".")) {
                arrayList.add(file);
            }
        }
        File file2 = new File(DIRECTORY2);
        if (file2.exists() && file2.isDirectory()) {
            for (File file3 : listFilesAsArrayMiscellaneous(new File(DIRECTORY2), filenameFilterArr, -1)) {
                if (!file3.getParent().substring(0, 1).contains(".") && file3.getName().contains(".") && !file3.getName().substring(0, 1).contains(".")) {
                    arrayList.add(file3);
                }
            }
        }
        return arrayList;
    }

    private Collection<File> listFilesMiscellaneous(File file, FilenameFilter[] filenameFilterArr, int i) {
        Vector vector = new Vector();
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            int i2 = i;
            for (File file2 : listFiles) {
                for (FilenameFilter filenameFilter : filenameFilterArr) {
                    if (filenameFilterArr == null || filenameFilter.accept(file, file2.getName())) {
                        String filename = filename(file2.getName().toLowerCase());
                        if (file2.isFile() && !file2.isDirectory() && !this.filterRemove.contains(filename)) {
                            vector.add(file2);
                        }
                    }
                }
                if (i2 <= -1 || (i2 > 0 && file2.isDirectory())) {
                    int i3 = i2 - 1;
                    vector.addAll(listFilesMiscellaneous(file2, filenameFilterArr, i3));
                    i2 = i3 + 1;
                }
            }
        }
        return vector;
    }

    private String filename(String str) {
        return str.substring(str.lastIndexOf(".") + 1, str.length());
    }

    private File[] listFilesAsArrayMiscellaneous(File file, FilenameFilter[] filenameFilterArr, int i) {
        Collection listFilesMiscellaneous = listFilesMiscellaneous(file, filenameFilterArr, i);
        return (File[]) listFilesMiscellaneous.toArray(new File[listFilesMiscellaneous.size()]);
    }

    private void SetRemoveFilesExt() {
        this.filterRemove.add("jpg");
        this.filterRemove.add("jpeg");
        this.filterRemove.add("png");
        this.filterRemove.add("gif");
        this.filterRemove.add("bmp");
        this.filterRemove.add("webp");
        this.filterRemove.add("mp3");
        this.filterRemove.add("wav");
        this.filterRemove.add("m4a");
        this.filterRemove.add("3gp");
        this.filterRemove.add("webm");
        this.filterRemove.add("mp4");
        this.filterRemove.add("avi");
        this.filterRemove.add("ts");
        this.filterRemove.add("mkv");
        this.filterRemove.add("flv");
        this.filterRemove.add("pdf");
        this.filterRemove.add("doc");
        this.filterRemove.add("docx");
        this.filterRemove.add("ppt");
        this.filterRemove.add("pptx");
        this.filterRemove.add("xls");
        this.filterRemove.add("xlsx");
        this.filterRemove.add("csv");
        this.filterRemove.add("dbk");
        this.filterRemove.add("dot");
        this.filterRemove.add("dotx");
        this.filterRemove.add("gdoc");
        this.filterRemove.add("pdax");
        this.filterRemove.add("pda");
        this.filterRemove.add("rtf");
        this.filterRemove.add("rpt");
        this.filterRemove.add("stw");
        this.filterRemove.add("txt");
        this.filterRemove.add("uof");
        this.filterRemove.add("uoml");
        this.filterRemove.add("wps");
        this.filterRemove.add("wpt");
        this.filterRemove.add("wrd");
        this.filterRemove.add("xps");
        this.filterRemove.add("epub");
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

    public static boolean isSymlink(File file) throws IOException {
        if (file != null) {
            if (file.getParent() != null) {
                file = new File(file.getParentFile().getCanonicalFile(), file.getName());
            }
            return !file.getCanonicalFile().equals(file.getAbsoluteFile());
        }
        throw new NullPointerException("File must not be null");
    }
}
