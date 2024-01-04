package com.example.vault.video.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap.Config;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.vault.video.model.ImportEnt;
import com.banrossyn.imageloader.core.DisplayImageOptions;
import com.banrossyn.imageloader.core.DisplayImageOptions.Builder;
import java.util.ArrayList;
import com.example.vault.R;

public class GalleryVideoAdapter extends ArrayAdapter {
    private final Context con;

    public ArrayList<ImportEnt> importEntList;
    LayoutInflater layoutInflater;
    DisplayImageOptions options = new Builder().showImageOnLoading((int) R.drawable.ic_video_empty_icon).showImageForEmptyUri((int) R.drawable.ic_video_empty_icon).showImageOnFail((int) R.drawable.ic_video_empty_icon).cacheInMemory(false).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Config.RGB_565).build();
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

    public GalleryVideoAdapter(Context context, int i, ArrayList<ImportEnt> arrayList) {
        super(context, i, arrayList);
        this.con = context;
        this.importEntList = arrayList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        View view2;
        if (view == null) {
            viewHolder = new ViewHolder();
            view2 = this.layoutInflater.inflate(R.layout.custom_gallery_item, null);
            viewHolder.imageview = (ImageView) view2.findViewById(R.id.thumbImage);
            viewHolder.ll_custom_gallery = (RelativeLayout) view2.findViewById(R.id.ll_custom_gallery);
            viewHolder.ll_dark_on_click = (LinearLayout) view2.findViewById(R.id.ll_dark_on_click);
            viewHolder.playimageAlbum = (ImageView) view2.findViewById(R.id.playthumbImage);
            viewHolder.iv_tick = (ImageView) view2.findViewById(R.id.iv_tick);
            final ImportEnt importEnt = (ImportEnt) this.importEntList.get(i);
            new Thread() {
                public void run() {
                    try {
                        if (importEnt.GetThumbnail() == null) {
                            importEnt.SetThumbnail(Thumbnails.getThumbnail(((Activity) GalleryVideoAdapter.this.getContext()).getContentResolver(), (long) importEnt.GetId(), 3, null));
                        }
                        viewHolder.imageview.setImageBitmap(importEnt.GetThumbnail());
                    } catch (Exception unused) {
                    }
                }
            }.start();
            viewHolder.playimageAlbum.setBackgroundResource(R.drawable.play_video_btn);
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
                    if (((ImportEnt) GalleryVideoAdapter.this.importEntList.get(intValue)).GetThumbnailSelection().booleanValue()) {
                        viewHolder.ll_custom_gallery.setBackgroundResource(R.color.fulltransparent_color);
                        ((ImportEnt) GalleryVideoAdapter.this.importEntList.get(intValue)).SetThumbnailSelection(Boolean.valueOf(false));
                        viewHolder.ll_dark_on_click.setBackgroundResource(R.color.fulltransparent_color);
                        viewHolder.iv_tick.setVisibility(View.INVISIBLE);
                        return;
                    }
                    viewHolder.ll_custom_gallery.setBackgroundResource(R.drawable.photo_grid_item_click);
                    ((ImportEnt) GalleryVideoAdapter.this.importEntList.get(intValue)).SetThumbnailSelection(Boolean.valueOf(true));
                    viewHolder.ll_dark_on_click.setBackgroundResource(R.color.transparent_black_color);
                    viewHolder.iv_tick.setVisibility(View.VISIBLE);
                }
            });
            view2.setTag(viewHolder);
            view2.setTag(viewHolder);
            view2.setTag(R.id.thumbImage, viewHolder.imageview);
            view2.setTag(R.id.iv_tick, viewHolder.iv_tick);
        } else {
            view2 = view;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.imageview.setTag(Integer.valueOf(i));
        viewHolder.iv_tick.setTag(Integer.valueOf(i));
        if (((ImportEnt) this.importEntList.get(i)).GetThumbnail() == null) {
            ((ImportEnt) this.importEntList.get(i)).SetThumbnail(Thumbnails.getThumbnail(((Activity) getContext()).getContentResolver(), (long) ((ImportEnt) this.importEntList.get(i)).GetId(), 3, null));
            viewHolder.imageview.setImageBitmap(((ImportEnt) this.importEntList.get(i)).GetThumbnail());
        } else {
            viewHolder.imageview.setImageBitmap(((ImportEnt) this.importEntList.get(i)).GetThumbnail());
        }
        if (((ImportEnt) this.importEntList.get(i)).GetThumbnailSelection().booleanValue()) {
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
