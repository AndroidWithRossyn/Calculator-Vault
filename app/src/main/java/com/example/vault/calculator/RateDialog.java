package com.example.vault.calculator;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog.Builder;

public class RateDialog {
    public static void show(final Context context) {
        new Builder(context).setTitle("Rate Calculator Vault").setMessage((CharSequence) "Would you like to rate Calculator Vault on the Google Play store?").setPositiveButton((CharSequence) "Sure", (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                try {

                    StringBuilder sb = new StringBuilder();
                    sb.append("market://details?id=");
                    sb.append(context.getPackageName());
                    context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(sb.toString())));
                } catch (ActivityNotFoundException unused) {
                    Context context2 = context;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("https://play.google.com/store/apps/details?id=");
                    sb2.append(context.getPackageName());
                    context2.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(sb2.toString())));
                }
            }
        }).setNegativeButton((CharSequence) "No thanks", (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create().show();
    }
}
