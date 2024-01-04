package com.example.vault.video.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap.Config;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vault.R;
import com.example.vault.utilities.Common;
import com.example.vault.video.model.VideoAlbum;
import com.banrossyn.imageloader.core.DisplayImageOptions;
import com.banrossyn.imageloader.core.DisplayImageOptions.Builder;
import com.banrossyn.imageloader.core.ImageLoader;

import java.util.ArrayList;

public class VideosAlbumsAdapter extends ArrayAdapter {
    boolean _isEdit = false;
    boolean _isGridView = false;
    Context con;
    int focusedPosition;
    LayoutInflater layoutInflater;
    ArrayList<VideoAlbum> list;
    DisplayImageOptions options;
    Resources res;

    public VideosAlbumsAdapter(Context context, int i, ArrayList<VideoAlbum> arrayList, int i2, boolean z, boolean z2) {
        super(context, i, arrayList);
        this.res = context.getResources();
        this.list = arrayList;
        this.con = context;
        this.focusedPosition = i2;
        this._isEdit = z;
        this._isGridView = z2;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.options = new Builder().cacheInMemory(false).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Config.RGB_565).build();
    }

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
        ImageView imageView3 = (ImageView) view2.findViewById(R.id.playthumbImage);
        LinearLayout linearLayout = (LinearLayout) view2.findViewById(R.id.ll_selection);
        TextView textView3 = (TextView) view2.findViewById(R.id.lbl_Date);
        TextView textView4 = (TextView) view2.findViewById(R.id.lbl_Time);
        VideoAlbum videoAlbum = (VideoAlbum) this.list.get(i);
        String str = videoAlbum.get_modifiedDateTime().split(",")[0];
        String str2 = videoAlbum.get_modifiedDateTime().split(", ")[1];
        textView3.setSelected(true);
        StringBuilder sb = new StringBuilder();
        sb.append("Date: ");
        sb.append(str);
        textView3.setText(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Time: ");
        sb2.append(str2);
        textView4.setText(sb2.toString());
        textView.setText(videoAlbum.getAlbumName());
        textView2.setText(Integer.toString(videoAlbum.getVideoCount()));
        String albumCoverLocation = videoAlbum.getAlbumCoverLocation();
        if (this.focusedPosition != i || !this._isEdit) {
            linearLayout.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
        } else {
            linearLayout.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
        }
        if (this._isGridView) {
            imageView.setBackgroundResource(R.drawable.videooo);
        } else {
            imageView.setBackgroundResource(R.drawable.videooo);
        }
        if (albumCoverLocation != null) {
//            imageView2.setScaleType(ScaleType.FIT_XY);
            try {
                ImageLoader imageLoader = Common.imageLoader;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("file:///");
                sb3.append((this.list.get(i)).getAlbumCoverLocation().toString());
                imageLoader.displayImage(sb3.toString(), imageView2, this.options);
            } catch (Exception unused) {
            }
            imageView.setVisibility(View.INVISIBLE);
//            imageView3.setBackgroundResource(R.drawable.videooo);
//            imageView.setVisibility(View.INVISIBLE);
//        } else if (this._isGridView) {
//            imageView.setBackgroundResource(R.drawable.videooo);
//        } else {
//            imageView.setBackgroundResource(R.drawable.videooo);
        }
        return view2;
    }
}