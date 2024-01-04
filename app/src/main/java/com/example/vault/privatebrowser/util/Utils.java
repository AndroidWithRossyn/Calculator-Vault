package com.example.vault.privatebrowser.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.example.vault.R;
import com.example.vault.common.Constants;
import com.example.vault.privatebrowser.util.DownloadHandler;

public final class Utils {
    private static Bitmap mWebIconDark;
    private static Bitmap mWebIconLight;

    private Utils() {
    }

    public static void downloadFile(Activity activity, String str, String str2, String str3, boolean z) {
        String guessFileName = URLUtil.guessFileName(str, null, null);
        DownloadHandler.onDownloadStart(activity, str, str2, str3, null, z);
        String str4 = Constants.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Downloading");
        sb.append(guessFileName);
        Log.i(str4, sb.toString());
    }

    public static Intent newEmailIntent(Context context, String str, String str2, String str3, String str4) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.EMAIL", new String[]{str});
        intent.putExtra("android.intent.extra.TEXT", str3);
        intent.putExtra("android.intent.extra.SUBJECT", str2);
        intent.putExtra("android.intent.extra.CC", str4);
        intent.setType("message/rfc822");
        return intent;
    }

    public static void createInformativeDialog(Context context, String str, String str2) {
        Builder builder = new Builder(context);
        builder.setTitle(str);
        builder.setMessage(str2).setCancelable(true).setPositiveButton(context.getResources().getString(R.string.action_ok), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

    public static void showToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static int convertDpToPixels(int i) {
        return (int) ((((float) i) * Resources.getSystem().getDisplayMetrics().density) + 0.5f);
    }

    public static String getDomainName(String str) {
        boolean startsWith = str.startsWith(Constants.HTTPS);
        int indexOf = str.indexOf(47, 8);
        if (indexOf != -1) {
            str = str.substring(0, indexOf);
        }
        String str2 = null;
        try {
            str2 = new URI(str).getHost();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (str2 == null || str2.isEmpty()) {
            return str;
        }
        if (startsWith) {
            StringBuilder sb = new StringBuilder();
            sb.append(Constants.HTTPS);
            sb.append(str2);
            return sb.toString();
        }
        if (str2.startsWith("www.")) {
            str2 = str2.substring(4);
        }
        return str2;
    }

    public static String getProtocol(String str) {
        return str.substring(0, str.indexOf(47) + 2);
    }

    public static String[] getArray(String str) {
        return str.split("\\|\\$\\|SEPARATOR\\|\\$\\|");
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

    public static Bitmap padFavicon(Bitmap bitmap) {
        int convertDpToPixels = convertDpToPixels(4);
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth() + convertDpToPixels, bitmap.getHeight() + convertDpToPixels, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawARGB(0, 0, 0, 0);
        float f = (float) (convertDpToPixels / 2);
        canvas.drawBitmap(bitmap, f, f, new Paint(2));
        return createBitmap;
    }

    @SuppressLint({"SimpleDateFormat"})
    public static File createImageFile() throws IOException {
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("JPEG_");
        sb.append(format);
        sb.append("_");
        return File.createTempFile(sb.toString(), ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
    }
}
