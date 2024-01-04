package com.example.vault;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import com.example.vault.privatebrowser.util.DownloadFileDAL;
import com.example.vault.privatebrowser.model.DownloadFileEnt;

public class CalculatorSharpDownloadReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        DownloadFileDAL downloadFileDAL = new DownloadFileDAL(context.getApplicationContext());
        downloadFileDAL.OpenRead();
        List GetDownloadFiles = downloadFileDAL.GetDownloadFiles();
        long longExtra = intent.getLongExtra("extra_download_id", -1);
        Iterator it = GetDownloadFiles.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            DownloadFileEnt downloadFileEnt = (DownloadFileEnt) it.next();
            if (downloadFileEnt.GetReferenceId().equals(String.valueOf(longExtra))) {
                try {
                    downloadFileDAL.UpdateDownloadFile(downloadFileEnt);
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        downloadFileDAL.close();
        abortBroadcast();
    }
}
