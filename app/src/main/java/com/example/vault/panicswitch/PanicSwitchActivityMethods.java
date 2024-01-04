package com.example.vault.panicswitch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.example.vault.panicswitch.PanicSwitchCommon.SwitchApp;
import com.example.vault.securitylocks.SecurityLocksCommon;

public class PanicSwitchActivityMethods {

    public static void SwitchApp(Context context) {
        Intent intent;
        SecurityLocksCommon.IsAppDeactive = true;
        if (PanicSwitchCommon.SwitchingApp.equals(SwitchApp.Browser.toString())) {
            intent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.google.com"));
        } else {
            intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
