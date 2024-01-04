package com.example.vault.video.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import com.example.vault.R;
import com.example.vault.utilities.Utilities;
import com.example.vault.video.model.ImportAlbumEnt;

public class AlbumsImportAdapter extends ArrayAdapter {
    boolean IsAlbumSelect = false;
    boolean IsVideo = false;
    Context con;
    ArrayList<ImportAlbumEnt> importAlbumEnts;
    LayoutInflater layoutInflater;
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
            viewHolder.imageAlbum.setImageBitmap(Thumbnails.getThumbnail(((Activity) getContext()).getContentResolver(), (long) ((ImportAlbumEnt) this.importAlbumEnts.get(i)).GetId(), 3, null));
            viewHolder.playimageAlbum.setVisibility(View.VISIBLE);
            viewHolder.playimageAlbum.setImageResource(R.drawable.play_video_btn);
        } else {
            try {
                viewHolder.playimageAlbum.setVisibility(View.INVISIBLE);
                viewHolder.imageAlbum.setScaleType(ScaleType.FIT_XY);
                viewHolder.imageAlbum.setImageBitmap(Utilities.DecodeFile(new File(((ImportAlbumEnt) this.importAlbumEnts.get(i)).GetPath())));
            } catch (Exception unused) {
            }
        }
        return view2;
    }
}
