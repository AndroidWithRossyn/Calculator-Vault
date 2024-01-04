package com.example.vault.audio.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vault.audio.model.AudioPlayListEnt;
import com.banrossyn.imageloader.core.DisplayImageOptions;

import java.util.ArrayList;

import com.example.vault.R;

public class AudioPlayListAdapter extends ArrayAdapter {
    boolean _isEdit = false;
    boolean _isGridView = false;
    Context con;
    int focusedPosition;
    LayoutInflater layoutInflater;
    ArrayList<AudioPlayListEnt> list;
    DisplayImageOptions options;
    Resources res;

    public AudioPlayListAdapter(Context context, int i, ArrayList<AudioPlayListEnt> arrayList, int i2, boolean z, boolean z2) {
        super(context, i, arrayList);
        this.res = context.getResources();
        this.list = arrayList;
        this.con = context;
        this.focusedPosition = i2;
        this._isEdit = z;
        this._isGridView = z2;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressLint({"ViewHolder"})
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        if (this._isGridView) {
            view2 = this.layoutInflater.inflate(R.layout.albums_gallery_item_grid, null);
        } else {
            view2 = this.layoutInflater.inflate(R.layout.albums_gallery_item_list, null);
        }
        TextView textView = (TextView) view2.findViewById(R.id.textAlbumName);
        TextView textView2 = (TextView) view2.findViewById(R.id.lbl_Count);
        ImageView imageView = (ImageView) view2.findViewById(R.id.thumbImage);
        ImageView imageView2 = (ImageView) view2.findViewById(R.id.iv_album_thumbnil);
        LinearLayout linearLayout = (LinearLayout) view2.findViewById(R.id.ll_selection);
        TextView textView3 = (TextView) view2.findViewById(R.id.lbl_Date);
        TextView textView4 = (TextView) view2.findViewById(R.id.lbl_Time);
        AudioPlayListEnt audioPlayListEnt = (AudioPlayListEnt) this.list.get(i);
        String str = audioPlayListEnt.get_modifiedDateTime().split(",")[0];
        String str2 = audioPlayListEnt.get_modifiedDateTime().split(", ")[1];
        textView3.setSelected(true);
        StringBuilder sb = new StringBuilder();
        sb.append("Date: ");
        sb.append(str);
        textView3.setText(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Time: ");
        sb2.append(str2);
        textView4.setText(sb2.toString());
        textView.setText(audioPlayListEnt.getPlayListName());
        textView.setSelected(true);
        if (this.focusedPosition != i || !this._isEdit) {
            linearLayout.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
        } else {
            linearLayout.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
        }
        textView2.setText(Integer.toString(audioPlayListEnt.get_fileCount()));
        if (this._isGridView) {
            if (((AudioPlayListEnt) this.list.get(i)).get_fileCount() > 0) {
                imageView.setBackgroundResource(R.drawable.ic_audiosfolder_thumb_icon);
            } else {
                imageView.setBackgroundResource(R.drawable.audioooooo);
            }
        } else if (audioPlayListEnt.get_fileCount() > 0) {
            imageView.setBackgroundResource(R.drawable.ic_audiosfolder_thumb_icon);
        } else {
            imageView.setBackgroundResource(R.drawable.audioooooo);
        }
        return view2;
    }
}
