package com.example.vault.privatebrowser.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import com.example.vault.R;
import com.example.vault.utilities.Common.BrowserMenuType;

public class UrlAdapter extends ArrayAdapter {
    Context con;
    LayoutInflater layoutInflater;
    private int listIcon = 0;
    Resources res;
    List<String> urlList;

    public UrlAdapter(Context context, int i, List<String> list, int i2) {
        super(context, i, list);
        this.res = context.getResources();
        this.urlList = list;
        this.con = context;
        this.listIcon = i2;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = this.layoutInflater.inflate(R.layout.custom_url_item, null);
        TextView textView = (TextView) inflate.findViewById(R.id.lblurlitem);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.imgurlitem);
        if (this.listIcon == BrowserMenuType.Bookmark.ordinal()) {
            imageView.setImageResource(R.drawable.download_bokmrk_histry_list_icon);
        } else if (this.listIcon == BrowserMenuType.Download.ordinal()) {
            imageView.setImageResource(R.drawable.download_dwnlod_histry_list_icon);
        }
        textView.setText((String) this.urlList.get(i));
        return inflate;
    }
}
