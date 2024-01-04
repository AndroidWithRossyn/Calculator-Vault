package com.example.vault.utilities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.banrossyn.imageloader.cache.disc.naming.Md5FileNameGenerator;
import com.banrossyn.imageloader.core.ImageLoader;
import com.banrossyn.imageloader.core.ImageLoaderConfiguration.Builder;
import com.banrossyn.imageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import com.example.vault.notes.util.SystemBarTintManager;
import com.example.vault.notes.util.UIElementsHelper;
import com.example.vault.storageoption.StorageOptionsCommon;

import org.apache.http.HttpStatus;

public class Common {
    public static String AppPackageName = "com.example.vault";
    public static String AudioFolderName = StorageOptionsCommon.AUDIOS_DEFAULT_ALBUM;
    public static String[] ColorsArray = {"#049372", "#049372", "#049372", "#049372", "#049372", "#049372", "#049372", "#049372", "#049372", "#049372", "#049372", "#049372"};
    public static Activity CurrentActivity = null;
    public static int CurrentTrackId = 0;
    public static int CurrentTrackIndex = 0;
    public static int CurrentTrackNextIndex = 0;
    public static Activity CurrentWebBrowserActivity = null;
    public static Activity CurrentWebServerActivity = null;
    public static String DocumentFolderName = StorageOptionsCommon.DOCUMENTS_DEFAULT_ALBUM;
    public static int EncryptBytesSize = 121;
    public static int FolderId = 0;
    public static int GalleryThumbnailCurrentPosition = 0;
    public static int TryAttemptCount = 0;
    public static final int TryAttemptedTotal = 3;
    public static int InterstitialAdCount = 1;
    public static boolean IsCameFromAppGallery = false;
    public static boolean IsCameFromFeatureActivity = false;
    public static boolean IsCameFromGalleryFeature = false;
    public static boolean IsCameFromPhotoAlbum = false;
    public static boolean IsChangeVideoExtentionInProcess = false;
    public static boolean IsImporting = false;
    public static boolean IsOpenFile = false;
    public static boolean IsSelectAll = false;
    public static boolean IsStart = false;
    public static boolean IsWebBrowserActive = false;
    public static boolean IsWorkInProgress = false;
    public static String LastWebBrowserUrl = "http://www.Example.net";
    public static int MaxFileSizeInMB = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    public static int MemoriesThumbnailCurrentPosition = 0;
    public static String PhotoFolderName = StorageOptionsCommon.PHOTOS_DEFAULT_ALBUM;
    public static int PhotoThumbnailCurrentPosition = 0;
    public static int PlayListId = 0;
    public static int SelectedCount = 0;
    public static boolean ToDoListEdit = false;
    public static int ToDoListId = 0;
    public static String ToDoListName = null;
    public static String UnhideKitkatAlbumName = "/Unlocked Files/";
    public static String VideoFolderName = StorageOptionsCommon.VIDEOS_DEFAULT_ALBUM;
    public static int VideoThumbnailCurrentPosition = 0;
    public static boolean WhatsNew = false;
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    public static boolean isDelete = false;
    public static boolean isFolderImport = false;
    public static boolean isMove = false;
    public static boolean isOpenCameraorGalleryFromApp = false;
    public static boolean isPurchased = true;
    public static boolean isShared = false;
    public static boolean isSignOutSuccessfully = false;
    public static boolean isUnHide = false;

    public static int loginCount = 0;
    public static int loginCountForRateAndReview = 0;
    public static final MediaPlayer mediaplayer = new MediaPlayer();

    public static int sortType = 0;
    public static final MediaPlayer voiceplayer = new MediaPlayer();

    public enum ActivityType {
        Music,
        Document,
        Miscellaneous
    }

    public enum BrowserMenuType {
        Bookmark,
        History,
        Download
    }

    public enum DownloadStatus {
        Completed,
        InProgress,
        Failed
    }

    public enum DownloadType {
        Photo,
        Video,
        Music,
        Document,
        Miscellaneous
    }

    public static boolean IsAirplaneModeOn(Context context) {
        boolean z = false;
        try {
            if (System.getInt(context.getContentResolver(), "airplane_mode_on") == 1) {
                z = true;
            }
            return z;
        } catch (SettingNotFoundException unused) {
            return false;
        }
    }

    public static boolean IsWiFiModeOn(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
    }

    public static boolean IsWiFiConnect(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(1).isConnected();
    }

    public static void applyKitKatTranslucency(Activity activity) {
        if (VERSION.SDK_INT >= 19) {
            setTranslucentStatus(activity, true);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(activity);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setNavigationBarTintEnabled(true);
            systemBarTintManager.setTintDrawable(UIElementsHelper.getGeneralActionBarBackground(activity));
        }
    }

    private static void setTranslucentStatus(Activity activity, boolean z) {
        Window window = activity.getWindow();
        LayoutParams attributes = window.getAttributes();
        if (z) {
            attributes.flags |= 67108864;
        } else {
            attributes.flags &= -67108865;
        }
        window.setAttributes(attributes);
    }

    public static void initImageLoader(Context context) {
        ImageLoader.getInstance().init(new Builder(context).threadPriority(3).denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.FIFO).build());
    }

    public static float GetTotalMemory() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        return ((((float) statFs.getBlockCount()) * ((float) statFs.getBlockSize())) / 1.07374182E9f) * 1024.0f;
    }

    public static float GetTotalFree() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        return ((((float) statFs.getAvailableBlocks()) * ((float) statFs.getBlockSize())) / 1.07374182E9f) * 1024.0f;
    }

    public static long GetTotalFreeSpaceSDCard() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize = (((long) statFs.getBlockSize()) * ((long) statFs.getAvailableBlocks())) / 1048576;
        StringBuilder sb = new StringBuilder();
        sb.append("Available MB : ");
        sb.append(blockSize);
        Log.e("", sb.toString());
        return blockSize;
    }

    public static float GetTotalUsed() {
        return (GetTotalMemory() - GetTotalFree()) * 1024.0f;
    }

    public static float GetFileSize(ArrayList<String> arrayList) {
        Iterator it = arrayList.iterator();
        float f = 0.0f;
        while (it.hasNext()) {
            f += ((float) (new File((String) it.next()).length() / 1024)) / 1024.0f;
        }
        return f;
    }

    public static boolean isTablet7Inch(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) == 3;
    }

    public static boolean isTablet10Inch(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) == 4;
    }

    public static boolean isTablet(Context context) {
        boolean z = (context.getResources().getConfiguration().screenLayout & 15) == 4;
        boolean z2 = (context.getResources().getConfiguration().screenLayout & 15) == 3;
        if (z || z2) {
            return true;
        }
        return false;
    }

    public static int getProgressPercentage(long j, long j2) {
        Double.valueOf(0.0d);
        double d = (double) ((long) ((int) (j / 1000)));
        double d2 = (double) ((long) ((int) (j2 / 1000)));
        Double.isNaN(d);
        Double.isNaN(d2);
        return Double.valueOf((d / d2) * 100.0d).intValue();
    }

    public static String milliSecondsToTimer(long j) {
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

    public static int progressToTimer(int i, int i2) {
        int i3 = i2 / 1000;
        double d = (double) i;
        Double.isNaN(d);
        double d2 = d / 100.0d;
        double d3 = (double) i3;
        Double.isNaN(d3);
        return ((int) (d2 * d3)) * 1000;
    }
}
