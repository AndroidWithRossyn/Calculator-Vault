package com.example.vault.securebackupcloud;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxCloudApi {
    private static final String ACCOUNT_PREFS_NAME = "DropboxPerf";
    private static Context context;
    public DbxClientV2 client;

    public DropboxCloudApi(Context context2) {
        DbxRequestConfig dbxRequestConfig = new DbxRequestConfig("Vault", "en_US");
        String oAuth2Token = Auth.getOAuth2Token();
        String string = context2.getSharedPreferences(ACCOUNT_PREFS_NAME, 0).getString("access-token", null);
        if (oAuth2Token != null) {
            this.client = new DbxClientV2(dbxRequestConfig, oAuth2Token);
        } else if (string != null) {
            this.client = new DbxClientV2(dbxRequestConfig, string);
        }
    }

    public void ClearKeys() {
        Editor edit = context.getSharedPreferences(ACCOUNT_PREFS_NAME, 0).edit();
        edit.clear();
        edit.commit();
    }
}
