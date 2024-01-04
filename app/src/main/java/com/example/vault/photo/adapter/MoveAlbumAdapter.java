package com.example.vault.photo.adapter;

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

public class MoveAlbumAdapter extends ArrayAdapter {
    List<String> Albumlist;
    Context con;
    LayoutInflater layoutInflater;
    int path;
    Resources res;

    public MoveAlbumAdapter(Context context, int i, List<String> list, int i2) {
        super(context, i, list);
        this.res = context.getResources();
        this.Albumlist = list;
        this.con = context;
        this.path = i2;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = this.layoutInflater.inflate(R.layout.move_customlistview_item, null);
        String str = (String) this.Albumlist.get(i);
        TextView textView = (TextView) inflate.findViewById(R.id.lblmoveitem);
        ((ImageView) inflate.findViewById(R.id.imgurlitem)).setImageResource(this.path);
        textView.setText(str);
        return inflate;
    }
}
