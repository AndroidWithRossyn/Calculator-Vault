package com.example.vault.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;
import com.example.vault.R;
import com.example.vault.photo.model.PhotoAlbum;
import com.example.vault.video.model.VideoAlbum;

public class SelectAlbumInImportAdapter extends ArrayAdapter {
    boolean Isvideo = false;
    private Context con;
    LayoutInflater layoutInflater;
    RelativeLayout ll_thumimage;
    List<PhotoAlbum> photoAlbums;
    Resources res;
    List<VideoAlbum> videoAlbums;

    public SelectAlbumInImportAdapter(Context context, int i, List<PhotoAlbum> list) {
        super(context, i, list);
        this.con = context;
        this.photoAlbums = list;
        this.res = context.getResources();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public SelectAlbumInImportAdapter(Context context, int i, List<VideoAlbum> list, boolean z) {
        super(context, i, list);
        this.con = context;
        this.videoAlbums = list;
        this.Isvideo = z;
        this.res = context.getResources();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressLint({"ViewHolder"})
    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = this.layoutInflater.inflate(R.layout.move_customlistview_item, null);
        TextView textView = (TextView) inflate.findViewById(R.id.lblmoveitem);
        if (!this.Isvideo) {
            textView.setText(((PhotoAlbum) this.photoAlbums.get(i)).getAlbumName());
        } else {
            textView.setText(((VideoAlbum) this.videoAlbums.get(i)).getAlbumName());
        }
        return inflate;
    }
}
