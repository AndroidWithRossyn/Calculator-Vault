package com.example.vault.photo.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap.Config;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vault.photo.model.ImportAlbumEnt;
import com.banrossyn.imageloader.core.DisplayImageOptions;
import com.banrossyn.imageloader.core.DisplayImageOptions.Builder;
import com.banrossyn.imageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import com.example.vault.R;
import com.example.vault.utilities.Common;

public class AlbumsImportAdapter extends ArrayAdapter {
    boolean IsAlbumSelect = false;
    boolean IsVideo = false;
    Context con;
    ArrayList<ImportAlbumEnt> importAlbumEnts;
    LayoutInflater layoutInflater;
    DisplayImageOptions options;
    Resources res;

    class ViewHolder {
        CheckBox checkbox;
        ImageView imageAlbum;
        TextView lbl_import_album_photo_count_item;
        ImageView playimageAlbum;
        TextView textAlbumName;

        ViewHolder() {
        }
    }

    public AlbumsImportAdapter(Context context, int i, ArrayList<ImportAlbumEnt> arrayList, boolean z, boolean z2) {
        super(context, i, arrayList);
        this.res = context.getResources();
        this.importAlbumEnts = arrayList;
        this.con = context;
        this.IsAlbumSelect = z;
        this.IsVideo = z2;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.options = new Builder().showImageOnLoading((int) R.drawable.empty_folder_album_icon).showImageForEmptyUri((int) R.drawable.empty_folder_album_icon).showImageOnFail((int) R.drawable.empty_folder_album_icon).cacheInMemory(false).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Config.RGB_565).build();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        View view2;
        if (view == null) {
            viewHolder = new ViewHolder();
            view2 = this.layoutInflater.inflate(R.layout.import_album_list_item_activity, null);
            viewHolder.checkbox = (CheckBox) view2.findViewById(R.id.cb_import_album_item);
            viewHolder.textAlbumName = (TextView) view2.findViewById(R.id.lbl_import_album_item);
            viewHolder.imageAlbum = (ImageView) view2.findViewById(R.id.thumbnil_import_album_titem);
            viewHolder.playimageAlbum = (ImageView) view2.findViewById(R.id.playimageAlbum);
            viewHolder.imageAlbum.setBackgroundColor(0);
            viewHolder.checkbox.setId(i);
            viewHolder.imageAlbum.setId(i);
            if (!this.IsAlbumSelect) {
                for (int i2 = 0; i2 < this.importAlbumEnts.size(); i2++) {
                    ((ImportAlbumEnt) this.importAlbumEnts.get(i2)).SetAlbumFileCheck(false);
                }
            }
            viewHolder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    ((ImportAlbumEnt) AlbumsImportAdapter.this.importAlbumEnts.get(((Integer) compoundButton.getTag()).intValue())).SetAlbumFileCheck(compoundButton.isChecked());
                }
            });
            view2.setTag(viewHolder);
            view2.setTag(R.id.thumbImage, viewHolder.imageAlbum);
            view2.setTag(R.id.cb_import_album_item, viewHolder.checkbox);
        } else {
            view2 = view;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.imageAlbum.setTag(Integer.valueOf(i));
        viewHolder.checkbox.setTag(Integer.valueOf(i));
        viewHolder.textAlbumName.setText(new File(((ImportAlbumEnt) this.importAlbumEnts.get(i)).GetAlbumName()).getName());
        viewHolder.textAlbumName.setSelected(true);
        viewHolder.textAlbumName.setEllipsize(TruncateAt.MARQUEE);
        viewHolder.textAlbumName.setSingleLine(true);
        if (this.IsVideo) {
            ImageLoader imageLoader = Common.imageLoader;
            StringBuilder sb = new StringBuilder();
            sb.append("file:///");
            sb.append(((ImportAlbumEnt) this.importAlbumEnts.get(i)).GetPath().toString());
            imageLoader.displayImage(sb.toString(), viewHolder.imageAlbum, this.options);
            viewHolder.playimageAlbum.setVisibility(View.VISIBLE);
            viewHolder.playimageAlbum.setImageResource(R.drawable.play_video_btn);
        } else {
            try {
                viewHolder.playimageAlbum.setVisibility(View.INVISIBLE);
              //  viewHolder.imageAlbum.setScaleType(ScaleType.FIT_XY);
                ImageLoader imageLoader2 = Common.imageLoader;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("file:///");
                sb2.append(((ImportAlbumEnt) this.importAlbumEnts.get(i)).GetPath().toString());
                imageLoader2.displayImage(sb2.toString(), viewHolder.imageAlbum, this.options);
            } catch (Exception unused) {
            }
        }
        return view2;
    }
}
