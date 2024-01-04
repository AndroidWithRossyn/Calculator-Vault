package com.example.vault.dropbox.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import com.example.vault.R;
import com.example.vault.dropbox.model.CloudMenuEnt;

public class CloudMenuAdapter extends ArrayAdapter {
    boolean IsStealthModeOn = false;
    private final ArrayList<CloudMenuEnt> cloudEntList;
    private final Context con;
    Dialog dialog;
    LayoutInflater layoutInflater;
    SharedPreferences myPrefs;
    final Editor prefsEditor;
    Resources res;

    public CloudMenuAdapter(Context context, int i, ArrayList<CloudMenuEnt> arrayList) {
        super(context, i, arrayList);
        this.myPrefs = context.getSharedPreferences("Login", 0);
        this.prefsEditor = this.myPrefs.edit();
        this.con = context;
        this.cloudEntList = arrayList;
        this.res = context.getResources();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressLint({"NewApi"})
    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = this.layoutInflater.inflate(R.layout.cloud_menu_activity_item, null);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.imgclouditem);
        CloudMenuEnt cloudMenuEnt = (CloudMenuEnt) this.cloudEntList.get(i);
        ((TextView) inflate.findViewById(R.id.lblcloudheadingitem)).setText(cloudMenuEnt.GetCloudHeading());
        imageView.setImageResource(cloudMenuEnt.GetDrawable());
        return inflate;
    }
}
