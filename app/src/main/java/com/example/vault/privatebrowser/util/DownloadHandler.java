package com.example.vault.privatebrowser.util;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Toast;
import java.io.File;
import com.example.vault.R;
import com.example.vault.privatebrowser.model.DownloadFileEnt;
import com.example.vault.utilities.Common.DownloadStatus;
import com.example.vault.utilities.Common.DownloadType;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class DownloadHandler {
    private static final String LOGTAG = "DLHandler";


    public static void onDownloadStart(Activity activity, String str, String str2, String str3, String str4, boolean z) {
        if (str3 != null) {
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.parse(str), str4);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        ResolveInfo resolveActivity = activity.getPackageManager().resolveActivity(intent, 65536);
        if (resolveActivity != null) {
            ComponentName componentName = activity.getComponentName();
            if (!componentName.getPackageName().equals(resolveActivity.activityInfo.packageName) || !componentName.getClassName().equals(resolveActivity.activityInfo.name)) {
                try {
                    activity.startActivity(intent);
                    return;
                } catch (ActivityNotFoundException unused) {
                }
            }
        }
        onDownloadStartNoStream(activity, str, str2, str3, str4, z);
    }

    private static String encodePath(String str) {
        boolean z;
        char[] charArray = str.toCharArray();
        int length = charArray.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                z = false;
                break;
            }
            char c = charArray[i];
            if (c == '[' || c == ']' || c == '|') {
                z = true;
            } else {
                i++;
            }
        }
        if (!z) {
            return str;
        }
        StringBuilder sb = new StringBuilder("");
        for (char c2 : charArray) {
            if (c2 == '[' || c2 == ']' || c2 == '|') {
                sb.append('%');
                sb.append(Integer.toHexString(c2));
            } else {
                sb.append(c2);
            }
        }
        return sb.toString();
    }

    private static void onDownloadStartNoStream(Activity activity, String str, String str2, String str3, String str4, boolean z) {
        int i;
        String str5;
        Activity activity2 = activity;
        String str6 = str;
        String str7 = str4;
        String str8 = str3;
        final String guessFileName = URLUtil.guessFileName(str, str3, str4);
        String externalStorageState = Environment.getExternalStorageState();
        if (!externalStorageState.equals("mounted")) {
            if (externalStorageState.equals("shared")) {
                str5 = activity.getString(R.string.download_sdcard_busy_dlg_msg);
                i = R.string.download_sdcard_busy_dlg_title;
            } else {
                str5 = activity.getString(R.string.download_no_sdcard_dlg_msg, new Object[]{});
                i = R.string.download_no_sdcard_dlg_title;
            }
            new Builder(activity).setTitle(i).setIcon(17301543).setMessage(str5).setPositiveButton(R.string.action_ok, null).show();
            return;
        }
        try {
            WebAddress webAddress = new WebAddress(str);
            webAddress.setPath(encodePath(webAddress.getPath()));
            String webAddress2 = webAddress.toString();
            try {
                Request request = new Request(Uri.parse(webAddress2));
                request.setMimeType(str4);
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, guessFileName);
                request.allowScanningByMediaScanner();
                request.setDescription(webAddress.getHost());
                String cookie = CookieManager.getInstance().getCookie(str);
                request.addRequestHeader("cookie", cookie);
                request.setNotificationVisibility(1);
                if (str7 != null) {
                    final DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                    final Request request2 = request;
                    final String str9 = str;
                    final Activity activity3 = activity;
                    Thread r1 = new Thread("Browser download") {
                        public void run() {
                            long enqueue = downloadManager.enqueue(request2);
                            String str = str9;
                            if (str != null && str.length() > 0) {
                                DownloadFileEnt downloadFileEnt = new DownloadFileEnt();
                                if (str.contains(".jpg") || str.contains(".png") || str.contains(".gif") || str.contains(".bmp")) {
                                    downloadFileEnt.SetDownloadType(DownloadType.Photo.ordinal());
                                } else if (str.contains(".mp4") || str.contains(".3gp") || str.contains(".avi") || str.contains(".flv") || str.contains(".mkv") || str.contains(".wmv")) {
                                    downloadFileEnt.SetDownloadType(DownloadType.Video.ordinal());
                                } else if (str.contains(".pdf") || str.contains(".doc") || str.contains(".docx") || str.contains(".ppt") || str.contains(".pptx") || str.contains(".xls") || str.contains(".xlsx") || str.contains(".csv") || str.contains(".dbk") || str.contains(".dot") || str.contains(".dotx") || str.contains(".gdoc") || str.contains(".pdax") || str.contains(".pda") || str.contains(".rtf") || str.contains(".rpt") || str.contains(".stw") || str.contains(".txt") || str.contains(".uof") || str.contains(".uoml") || str.contains(".wps") || str.contains(".wpt") || str.contains(".wrd") || str.contains(".xps") || str.contains(".epub") || str.contains(".xml")) {
                                    downloadFileEnt.SetDownloadType(DownloadType.Document.ordinal());
                                } else if (str.contains(".7z") || str.contains(".ace") || str.contains(".bik") || str.contains(".bin") || str.contains(".bkf") || str.contains(".bzip2") || str.contains(".cab") || str.contains(".daa") || str.contains(".gzip") || str.contains(".jar") || str.contains(".apk") || str.contains(".xap") || str.contains(".lzip") || str.contains(".rar") || str.contains(".tgz") || str.contains(".iso") || str.contains(".img") || str.contains(".mdx") || str.contains(".dmg") || str.contains(".acp") || str.contains(".amf") || str.contains(".4db") || str.contains(".4dr") || str.contains(".ave") || str.contains(".fm") || str.contains(".acl") || str.contains(".ans") || str.contains(".ots") || str.contains(".egt") || str.contains(".ftx") || str.contains(".lwp") || str.contains(".nb") || str.contains(".nbp") || str.contains(".odm") || str.contains(".odt") || str.contains(".ott") || str.contains(".via") || str.contains(".wps") || str.contains(".wrf") || str.contains(".wri") || str.contains(".org") || str.contains(".ahk") || str.contains(".as") || str.contains(".bat") || str.contains(".bas") || str.contains(".hta") || str.contains(".ijs") || str.contains(".js") || str.contains(".ncf") || str.contains(".nut") || str.contains(".sdl") || str.contains(".au") || str.contains(".raw") || str.contains(".pac") || str.contains(".m4a") || str.contains(".ab2") || str.contains(".via") || str.contains(".wps") || str.contains(".wrf") || str.contains(".wri") || str.contains(".ab3") || str.contains(".aws") || str.contains(".clf") || str.contains(".ods") || str.contains(".vc") || str.contains(".bak") || str.contains(".bdf") || str.contains(".tos") || str.contains(".exe") || str.contains(".msg") || str.contains(".dtp") || str.contains(".pub") || str.contains(".zip")) {
                                    downloadFileEnt.SetDownloadType(DownloadType.Miscellaneous.ordinal());
                                } else if (str.contains(".mp3") || str.contains(".wav")) {
                                    downloadFileEnt.SetDownloadType(DownloadType.Music.ordinal());
                                } else {
                                    downloadFileEnt.SetDownloadType(DownloadType.Miscellaneous.ordinal());
                                }
                                StringBuilder sb = new StringBuilder();
                                sb.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                                sb.append(File.separator);
                                sb.append(guessFileName);
                                downloadFileEnt.SetFileDownloadPath(sb.toString());
                                downloadFileEnt.SetFileName(guessFileName);
                                downloadFileEnt.SetReferenceId(String.valueOf(enqueue));
                                downloadFileEnt.SetStatus(DownloadStatus.InProgress.ordinal());
                                downloadFileEnt.SetDownloadFileUrl(str9);
                                DownloadFileDAL downloadFileDAL = new DownloadFileDAL(activity3);
                                downloadFileDAL.OpenWrite();
                                downloadFileDAL.AddDownloadFile(downloadFileEnt);
                                downloadFileDAL.close();
                            }
                        }
                    };
                    r1.start();
                    Toast.makeText(activity, R.string.download_pending, Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.isEmpty(webAddress2)) {
                    FetchUrlMimeType fetchUrlMimeType = new FetchUrlMimeType(activity, request, webAddress2, cookie, str2);
                    fetchUrlMimeType.start();
                }
            } catch (IllegalArgumentException unused) {
                Toast.makeText(activity, R.string.cannot_download, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            String str10 = LOGTAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Exception while trying to parse url '");
            sb.append(str);
            sb.append('\'');
            Log.e(str10, sb.toString(), e);
        }
    }
}
