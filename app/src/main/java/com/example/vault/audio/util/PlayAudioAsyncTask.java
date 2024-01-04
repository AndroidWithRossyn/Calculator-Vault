package com.example.vault.audio.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.example.vault.Flaes;
import com.example.vault.utilities.Common;

public class PlayAudioAsyncTask extends AsyncTask<Void, Void, Boolean> {
    Context context;
    File fileIn;
    FileInputStream fileInputStream;
    File fileOut;
    ProgressDialog progressDialog;
    long time = 0;

    public PlayAudioAsyncTask(Context context2, File file, File file2) {
        this.context = context2;
        this.fileIn = file;
        this.fileOut = file2;
    }


    public Boolean doInBackground(Void... voidArr) {
        long nanoTime = System.nanoTime();
        boolean decryptUsingCipherStream_AES128 = Flaes.decryptUsingCipherStream_AES128(this.fileIn, this.fileOut);
        this.time = (System.nanoTime() - nanoTime) / 1000000;
        if (decryptUsingCipherStream_AES128) {
            return Boolean.valueOf(true);
        }
        return Boolean.valueOf(false);
    }


    public void onPreExecute() {
        super.onPreExecute();
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setTitle("Please Wait");
        this.progressDialog.setMessage("Decrypting audio...");
        this.progressDialog.show();
    }


    public void onPostExecute(Boolean bool) {
        super.onPostExecute(bool);
        this.progressDialog.dismiss();
        try {
            this.fileInputStream = new FileInputStream(this.fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Common.mediaplayer.stop();
        Common.mediaplayer.reset();
        try {
            Common.mediaplayer.setDataSource(this.fileInputStream.getFD());
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalStateException e3) {
            e3.printStackTrace();
        } catch (IOException e4) {
            e4.printStackTrace();
        }
        try {
            Common.mediaplayer.prepare();
        } catch (IllegalStateException e5) {
            e5.printStackTrace();
        } catch (IOException e6) {
            e6.printStackTrace();
        }
        Common.mediaplayer.start();
    }
}
