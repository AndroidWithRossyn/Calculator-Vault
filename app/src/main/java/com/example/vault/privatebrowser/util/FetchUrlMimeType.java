package com.example.vault.privatebrowser.util;

import android.app.DownloadManager.Request;
import android.content.Context;
import android.widget.Toast;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import com.example.vault.R;
import org.apache.http.cookie.SM;
import org.apache.http.entity.mime.MIME;

public class FetchUrlMimeType extends Thread {
    private final Context mContext;
    private final String mCookies;
    private final Request mRequest;
    private final String mUri;
    private final String mUserAgent;

    public FetchUrlMimeType(Context context, Request request, String str, String str2, String str3) {
        this.mContext = context.getApplicationContext();
        this.mRequest = request;
        this.mUri = str;
        this.mCookies = str2;
        this.mUserAgent = str3;
        Toast.makeText(this.mContext, R.string.download_pending, Toast.LENGTH_SHORT).show();
    }


    public void run() {
        String str;
        HttpURLConnection httpURLConnection;
        String str2 = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(this.mUri).openConnection();
            try {
                if (this.mCookies != null && this.mCookies.length() > 0) {
                    httpURLConnection.addRequestProperty(SM.COOKIE, this.mCookies);
                    httpURLConnection.setRequestProperty("User-Agent", this.mUserAgent);
                }
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() == 200) {
                    str = httpURLConnection.getHeaderField("Content-Type");
                    if (str != null) {
                        try {
                            int indexOf = str.indexOf(59);
                            if (indexOf != -1) {
                                str = str.substring(0, indexOf);
                            }
                        } catch (IllegalArgumentException unused) {

                        }
                    } else {
                        str = null;
                    }
                    String headerField = httpURLConnection.getHeaderField(MIME.CONTENT_DISPOSITION);
                    if (headerField != null) {
                        str2 = headerField;
                    }
                } else {
                    str = null;
                }
            } catch (IOException | IllegalArgumentException unused2) {
                str = null;
                if (httpURLConnection != null) {
                }
            }
        } catch (IOException | IllegalArgumentException unused3) {
            httpURLConnection = null;
            str = null;
            if (httpURLConnection != null) {
                try {
                    httpURLConnection.disconnect();
                } catch (Throwable th) {
                    th = th;
                }
            }
        } catch (Throwable th2) {
            Throwable th3 = th2;
            httpURLConnection = null;

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            throw th2;
        }
    }
}
