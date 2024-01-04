package com.example.vault.photo.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap.Config;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.vault.photo.model.ImportEnt;
import com.banrossyn.imageloader.core.DisplayImageOptions;
import com.banrossyn.imageloader.core.DisplayImageOptions.Builder;
import com.banrossyn.imageloader.core.ImageLoader;

import java.util.ArrayList;

import com.example.vault.R;
import com.example.vault.utilities.Common;

public class GalleryPhotoAdapter extends ArrayAdapter {
    private final Context con;
    LayoutInflater layoutInflater;
    DisplayImageOptions options = new Builder().showImageOnLoading((int) R.drawable.ic_photo_empty_icon).showImageForEmptyUri((int) R.drawable.ic_photo_empty_icon).showImageOnFail((int) R.drawable.ic_photo_empty_icon).cacheInMemory(false).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Config.RGB_565).build();

    public ArrayList<ImportEnt> photoImportEntList;
    Resources res;

    class ViewHolder {
        int id;
        ImageView imageview;
        ImageView iv_tick;
        RelativeLayout ll_custom_gallery;
        LinearLayout ll_dark_on_click;
        ImageView playimageAlbum;

        ViewHolder() {
        }
    }

    public GalleryPhotoAdapter(Context context, int i, ArrayList<ImportEnt> arrayList) {
        super(context, i, arrayList);
        this.con = context;
        this.photoImportEntList = arrayList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        View view2;
        if (view == null) {
            viewHolder = new ViewHolder();
            view2 = this.layoutInflater.inflate(R.layout.custom_gallery_item, null);
            viewHolder.ll_custom_gallery = (RelativeLayout) view2.findViewById(R.id.ll_custom_gallery);
            viewHolder.imageview = (ImageView) view2.findViewById(R.id.thumbImage);
            viewHolder.playimageAlbum = (ImageView) view2.findViewById(R.id.playthumbImage);
            viewHolder.ll_dark_on_click = (LinearLayout) view2.findViewById(R.id.ll_dark_on_click);
            viewHolder.iv_tick = (ImageView) view2.findViewById(R.id.iv_tick);
            ImportEnt importEnt = (ImportEnt) this.photoImportEntList.get(i);
            viewHolder.imageview.setImageBitmap(importEnt.GetThumbnail());
            if (importEnt.GetThumbnailSelection().booleanValue()) {
                viewHolder.ll_custom_gallery.setBackgroundResource(R.drawable.photo_grid_item_click);
                viewHolder.ll_dark_on_click.setBackgroundResource(R.color.transparent_black_color);
                viewHolder.iv_tick.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ll_custom_gallery.setBackgroundResource(R.color.fulltransparent_color);
                viewHolder.ll_dark_on_click.setBackgroundResource(R.color.fulltransparent_color);
                viewHolder.iv_tick.setVisibility(View.INVISIBLE);
            }
            viewHolder.imageview.setBackgroundColor(0);
            viewHolder.ll_custom_gallery.setId(i);
            viewHolder.imageview.setId(i);
            viewHolder.iv_tick.setId(i);
            viewHolder.imageview.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    int intValue = ((Integer) view.getTag()).intValue();
                    if (((ImportEnt) GalleryPhotoAdapter.this.photoImportEntList.get(intValue)).GetThumbnailSelection().booleanValue()) {
                        viewHolder.ll_custom_gallery.setBackgroundResource(R.color.fulltransparent_color);
                        ((ImportEnt) GalleryPhotoAdapter.this.photoImportEntList.get(intValue)).SetThumbnailSelection(Boolean.valueOf(false));
                        viewHolder.ll_dark_on_click.setBackgroundResource(R.color.fulltransparent_color);
                        viewHolder.iv_tick.setVisibility(View.INVISIBLE);
                        return;
                    }
                    viewHolder.ll_custom_gallery.setBackgroundResource(R.drawable.photo_grid_item_click);
                    ((ImportEnt) GalleryPhotoAdapter.this.photoImportEntList.get(intValue)).SetThumbnailSelection(Boolean.valueOf(true));
                    viewHolder.ll_dark_on_click.setBackgroundResource(R.color.transparent_black_color);
                    viewHolder.iv_tick.setVisibility(View.VISIBLE);
                }
            });
            view2.setTag(viewHolder);
            view2.setTag(R.id.thumbImage, viewHolder.imageview);
            view2.setTag(R.id.iv_tick, viewHolder.iv_tick);
        } else {
            view2 = view;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.imageview.setTag(Integer.valueOf(i));
        viewHolder.iv_tick.setTag(Integer.valueOf(i));
        ImageLoader imageLoader = Common.imageLoader;
        StringBuilder sb = new StringBuilder();
        sb.append("file:///");
        sb.append(((ImportEnt) this.photoImportEntList.get(i)).GetPath().toString());
        imageLoader.displayImage(sb.toString(), viewHolder.imageview, this.options);
        if (((ImportEnt) this.photoImportEntList.get(i)).GetThumbnailSelection().booleanValue()) {
            viewHolder.ll_custom_gallery.setBackgroundResource(R.drawable.photo_grid_item_click);
            viewHolder.ll_dark_on_click.setBackgroundResource(R.color.transparent_black_color);
            viewHolder.iv_tick.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ll_custom_gallery.setBackgroundResource(R.color.fulltransparent_color);
            viewHolder.ll_dark_on_click.setBackgroundResource(R.color.fulltransparent_color);
            viewHolder.iv_tick.setVisibility(View.INVISIBLE);
        }
        viewHolder.id = i;
        return view2;
    }
}
