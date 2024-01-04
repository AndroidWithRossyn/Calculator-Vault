package com.example.vault.audio.adapter;

import android.content.Context;
import android.content.res.Resources;
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
import java.io.File;
import java.util.List;
import com.example.vault.R;
import com.example.vault.audio.model.ImportAlbumEnt;

public class AudioFoldersImportAdapter extends ArrayAdapter {
    boolean IsAlbumSelect = false;
    Context con;
    List<ImportAlbumEnt> importAlbumEnts;
    LayoutInflater layoutInflater;
    Resources res;

    class ViewHolder {
        CheckBox checkbox;
        ImageView imageAlbum;
        TextView textAlbumName;

        ViewHolder() {
        }
    }

    public AudioFoldersImportAdapter(Context context, int i, List<ImportAlbumEnt> list, boolean z) {
        super(context, i, list);
        this.res = context.getResources();
        this.importAlbumEnts = list;
        this.con = context;
        this.IsAlbumSelect = z;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view2 = this.layoutInflater.inflate(R.layout.import_folder_list_item_activity, null);
            viewHolder.checkbox = (CheckBox) view2.findViewById(R.id.cb_import_album_item);
            viewHolder.textAlbumName = (TextView) view2.findViewById(R.id.lbl_import_album_item);
            viewHolder.imageAlbum = (ImageView) view2.findViewById(R.id.thumbnil_import_album_titem);
            viewHolder.imageAlbum.setBackgroundColor(0);
            viewHolder.imageAlbum.setBackgroundResource(R.drawable.audioooooo);
            viewHolder.checkbox.setId(i);
            viewHolder.imageAlbum.setId(i);
            if (!this.IsAlbumSelect) {
                for (int i2 = 0; i2 < this.importAlbumEnts.size(); i2++) {
                    ((ImportAlbumEnt) this.importAlbumEnts.get(i2)).SetAlbumFileCheck(false);
                }
            }
            viewHolder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    ((ImportAlbumEnt) AudioFoldersImportAdapter.this.importAlbumEnts.get(((Integer) compoundButton.getTag()).intValue())).SetAlbumFileCheck(compoundButton.isChecked());
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
        return view2;
    }
}
