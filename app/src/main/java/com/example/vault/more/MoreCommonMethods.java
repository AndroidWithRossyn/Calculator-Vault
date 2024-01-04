package com.example.vault.more;

import static com.example.vault.utilities.Common.AppPackageName;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.example.vault.R;
import com.example.vault.securitylocks.SecurityLocksCommon;
import org.apache.http.protocol.HTTP;

public class MoreCommonMethods {
    public static void TellaFriendDialog(final Context context) {
        final Dialog dialog = new Dialog(context, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.tell_a_friend_dialog);
        dialog.setCancelable(true);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.ivfacebook);
        ImageView imageView2 = (ImageView) dialog.findViewById(R.id.ivtwitter);
        ImageView imageView3 = (ImageView) dialog.findViewById(R.id.ivinsta);
        ((ImageView) dialog.findViewById(R.id.ivemail)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType(HTTP.PLAIN_TEXT_TYPE);
                intent.putExtra("android.intent.extra.EMAIL", new String[]{""});
                intent.putExtra("android.intent.extra.SUBJECT", "Download this amazing app, ");
                StringBuilder sb = new StringBuilder();
                sb.append("Hey, there's a securer and easier way to hide secret photos, videos, documents and every other sort of data on your Android Phone. Download this amazing app Calculator Vault, and see for yourself. Download Calculator Vault by clicking here:  Play Store Link \n https://play.google.com/store/apps/details?id=");
                sb.append(AppPackageName);
                intent.putExtra("android.intent.extra.TEXT", sb.toString());
                try {
                    SecurityLocksCommon.IsAppDeactive = false;
                    context.startActivity(Intent.createChooser(intent, "Tell A Friend via email..."));
                } catch (ActivityNotFoundException unused) {
                }
            }
        });
        imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                SecurityLocksCommon.IsAppDeactive = false;
                StringBuilder sb = new StringBuilder();
                sb.append("http://www.facebook.com/sharer/sharer.php?u=https://play.google.com/store/apps/details?id=");
                sb.append(AppPackageName);
                context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(sb.toString())));
            }
        });
        imageView2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                SecurityLocksCommon.IsAppDeactive = false;
                StringBuilder sb = new StringBuilder();
                sb.append("https://twitter.com/intent/tweet?text=Protect photos, videos, audio files, documents and other sort of data on your phone with Calculator Vault! https://play.google.com/store/apps/details?id=");
                sb.append(AppPackageName);
                context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(sb.toString())));
            }
        });
        imageView3.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                SecurityLocksCommon.IsAppDeactive = false;
                StringBuilder sb = new StringBuilder();
                sb.append("https://www.instagram.com/?hl=en=https://play.google.com/store/apps/details?id=");
                sb.append(AppPackageName);
                context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(sb.toString())));
            }
        });
        ((Button) dialog.findViewById(R.id.btnDialogCancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
