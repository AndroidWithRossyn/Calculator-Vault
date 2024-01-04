package com.example.vault.video.adapter;

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
import android.widget.TextView;

import com.example.vault.video.model.Video;
import com.banrossyn.imageloader.core.DisplayImageOptions;
import com.banrossyn.imageloader.core.DisplayImageOptions.Builder;
import com.banrossyn.imageloader.core.ImageLoader;
import java.util.List;
import com.example.vault.R;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;

public class AppGalleryVideoAdapter extends ArrayAdapter {

    public int _ViewBy = 0;
    private final Context con;
    private boolean isEdit;
    LayoutInflater layoutInflater;

    public List<Video> listItems;
    DisplayImageOptions options;
    Resources res;

    class ViewHolder {
        int id;
        ImageView imageview;
        ImageView iv_tick;
        TextView lbl_Date;
        TextView lbl_Size;
        TextView lbl_Time;
        RelativeLayout ll_custom_gallery;
        LinearLayout ll_dark_on_click;
        LinearLayout ll_selection;
        ImageView playthumbImage;
        RelativeLayout rl_thumimage;
        TextView textAlbumName;

        ViewHolder() {
        }
    }

    public AppGalleryVideoAdapter(Context context, int i, List<Video> list, boolean z, int i2) {
        super(context, i, list);
        this.con = context;
        this.listItems = list;
        this.isEdit = z;
        this._ViewBy = i2;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.options = new Builder().showImageOnLoading((int) R.drawable.ic_video_empty_icon).showImageForEmptyUri((int) R.drawable.ic_video_empty_icon).showImageOnFail((int) R.drawable.ic_video_empty_icon).cacheInMemory(false).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Config.RGB_565).build();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        View view2;
        int i2 = i;
        if (view == null) {
            Video video = (Video) this.listItems.get(i2);
            viewHolder = new ViewHolder();
            int i3 = this._ViewBy;
            if (i3 == 0) {
                view2 = this.layoutInflater.inflate(R.layout.custom_gallery_item, null);
                viewHolder.ll_custom_gallery = (RelativeLayout) view2.findViewById(R.id.ll_custom_gallery);
                viewHolder.ll_dark_on_click = (LinearLayout) view2.findViewById(R.id.ll_dark_on_click);
                viewHolder.iv_tick = (ImageView) view2.findViewById(R.id.iv_tick);
                viewHolder.imageview = (ImageView) view2.findViewById(R.id.thumbImage);
                viewHolder.playthumbImage = (ImageView) view2.findViewById(R.id.playthumbImage);
                viewHolder.iv_tick.setId(i2);
            } else if (i3 == 1) {
                view2 = this.layoutInflater.inflate(R.layout.custom_gallery_item_tiles, null);
                viewHolder.ll_custom_gallery = (RelativeLayout) view2.findViewById(R.id.ll_custom_gallery);
                viewHolder.ll_dark_on_click = (LinearLayout) view2.findViewById(R.id.ll_dark_on_click);
                viewHolder.iv_tick = (ImageView) view2.findViewById(R.id.iv_tick);
                viewHolder.imageview = (ImageView) view2.findViewById(R.id.thumbImage);
                viewHolder.playthumbImage = (ImageView) view2.findViewById(R.id.playthumbImage);
                viewHolder.iv_tick.setId(i2);
            } else if (i3 == 2) {
                view2 = this.layoutInflater.inflate(R.layout.custom_gallery_item_list, null);
                viewHolder.textAlbumName = (TextView) view2.findViewById(R.id.textAlbumName);
                viewHolder.rl_thumimage = (RelativeLayout) view2.findViewById(R.id.rl_thumimage);
                viewHolder.ll_selection = (LinearLayout) view2.findViewById(R.id.ll_selection);
                viewHolder.lbl_Date = (TextView) view2.findViewById(R.id.lbl_Date);
                viewHolder.lbl_Time = (TextView) view2.findViewById(R.id.lbl_Time);
                viewHolder.lbl_Size = (TextView) view2.findViewById(R.id.lbl_Size);
                viewHolder.imageview = (ImageView) view2.findViewById(R.id.thumbImage);
                viewHolder.playthumbImage = (ImageView) view2.findViewById(R.id.playthumbImage);
            } else {
                view2 = view;
            }
            viewHolder.imageview.setId(i2);
            viewHolder.playthumbImage.setBackgroundResource(R.drawable.play_video_btn);
            if (video.GetFileCheck()) {
                if (this._ViewBy == 2) {
                    viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
                } else {
                    viewHolder.ll_custom_gallery.setBackgroundResource(R.drawable.photo_grid_item_click);
                    viewHolder.ll_dark_on_click.setBackgroundResource(R.color.transparent_black_color);
                    viewHolder.iv_tick.setVisibility(View.VISIBLE);
                }
            } else if (this._ViewBy == 2) {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
            } else {
                viewHolder.ll_custom_gallery.setBackgroundResource(R.color.fulltransparent_color);
                viewHolder.ll_dark_on_click.setBackgroundResource(R.color.fulltransparent_color);
                viewHolder.iv_tick.setVisibility(View.INVISIBLE);
            }
            if (this.isEdit) {
                viewHolder.imageview.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (AppGalleryVideoAdapter.this._ViewBy != 2) {
                            int intValue = ((Integer) view.getTag()).intValue();
                            if (((Video) AppGalleryVideoAdapter.this.listItems.get(intValue)).GetFileCheck()) {
                                viewHolder.ll_custom_gallery.setBackgroundResource(R.color.fulltransparent_color);
                                viewHolder.ll_dark_on_click.setBackgroundResource(R.color.fulltransparent_color);
                                ((Video) AppGalleryVideoAdapter.this.listItems.get(intValue)).SetFileCheck(false);
                                viewHolder.iv_tick.setVisibility(View.INVISIBLE);
                                return;
                            }
                            viewHolder.ll_custom_gallery.setBackgroundResource(R.drawable.photo_grid_item_click);
                            viewHolder.ll_dark_on_click.setBackgroundResource(R.color.transparent_black_color);
                            ((Video) AppGalleryVideoAdapter.this.listItems.get(intValue)).SetFileCheck(true);
                            viewHolder.iv_tick.setVisibility(View.VISIBLE);
                        }
                    }
                });
                if (this._ViewBy == 2) {
                    viewHolder.rl_thumimage.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            int intValue = ((Integer) view.getTag()).intValue();
                            if (((Video) AppGalleryVideoAdapter.this.listItems.get(intValue)).GetFileCheck()) {
                                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
                                ((Video) AppGalleryVideoAdapter.this.listItems.get(intValue)).SetFileCheck(false);
                                return;
                            }
                            viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
                            ((Video) AppGalleryVideoAdapter.this.listItems.get(intValue)).SetFileCheck(true);
                        }
                    });
                }
            }
            view2.setTag(viewHolder);
            view2.setTag(R.id.thumbImage, viewHolder.imageview);
            if (this._ViewBy != 2) {
                view2.setTag(R.id.iv_tick, viewHolder.iv_tick);
            } else {
                view2.setTag(R.id.rl_thumimage, viewHolder.rl_thumimage);
            }
        } else {
            viewHolder = (ViewHolder) view.getTag();
            view2 = view;
        }
        viewHolder.imageview.setTag(Integer.valueOf(i));
        if (this._ViewBy != 2) {
            viewHolder.iv_tick.setTag(Integer.valueOf(i));
        } else {
            viewHolder.rl_thumimage.setTag(Integer.valueOf(i));
        }
        if (this._ViewBy == 2) {
            viewHolder.textAlbumName.setText(((Video) this.listItems.get(i2)).getVideoName());
            String str = ((Video) this.listItems.get(i2)).get_modifiedDateTime().split(",")[0];
            String str2 = ((Video) this.listItems.get(i2)).get_modifiedDateTime().split(", ")[1];
            viewHolder.lbl_Date.setText(str);
            viewHolder.lbl_Time.setText(str2);
            viewHolder.lbl_Size.setText(Utilities.FileSize(((Video) this.listItems.get(i2)).getFolderLockVideoLocation()));
        }
        try {
            ImageLoader imageLoader = Common.imageLoader;
            StringBuilder sb = new StringBuilder();
            sb.append("file:///");
            sb.append(((Video) this.listItems.get(i2)).getthumbnail_video_location().toString());
            imageLoader.displayImage(sb.toString(), viewHolder.imageview, this.options);
        } catch (Exception unused) {
        }
        if (((Video) this.listItems.get(i2)).GetFileCheck()) {
            if (this._ViewBy == 2) {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
            } else {
                viewHolder.ll_custom_gallery.setBackgroundResource(R.drawable.photo_grid_item_click);
                viewHolder.ll_dark_on_click.setBackgroundResource(R.color.transparent_black_color);
                viewHolder.iv_tick.setVisibility(View.VISIBLE);
            }
        } else if (this._ViewBy == 2) {
            viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
        } else {
            viewHolder.ll_custom_gallery.setBackgroundResource(R.color.fulltransparent_color);
            viewHolder.ll_dark_on_click.setBackgroundResource(R.color.fulltransparent_color);
            viewHolder.iv_tick.setVisibility(View.INVISIBLE);
        }
        viewHolder.id = i2;
        return view2;
    }
}
