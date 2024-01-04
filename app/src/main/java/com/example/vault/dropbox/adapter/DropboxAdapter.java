package com.example.vault.dropbox.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import com.example.vault.R;
import com.example.vault.securebackupcloud.BackupCloudEnt;
import com.example.vault.securebackupcloud.CloudCommon;
import com.example.vault.securebackupcloud.CloudCommon.DropboxType;

public class DropboxAdapter extends ArrayAdapter {
    ArrayList<BackupCloudEnt> backupCloudEntlist;
    private Context con;
    LayoutInflater layoutInflater;
    Resources res;

    public class ViewHolder {
        public ImageView imagestatusitem;
        public ImageView imagesyncitem;
        public ImageView ivfile;
        public TextView lblDownloadSubject;
        public TextView lblFolderName;
        public TextView lblUploadSubject;

        public ViewHolder() {
        }
    }

    public DropboxAdapter(Context context, int i, ArrayList<BackupCloudEnt> arrayList) {
        super(context, i, arrayList);
        this.con = context;
        this.backupCloudEntlist = arrayList;
        this.res = context.getResources();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        View view2;
        String str;
        String str2;
        int i2 = i;
        if (view == null) {
            view2 = this.layoutInflater.inflate(R.layout.dropbox_download_items, null);
            viewHolder = new ViewHolder();
            viewHolder.lblFolderName = (TextView) view2.findViewById(R.id.lblFolderName);
            viewHolder.imagestatusitem = (ImageView) view2.findViewById(R.id.imagestatusitem);
            viewHolder.imagesyncitem = (ImageView) view2.findViewById(R.id.imagesyncitem);
            viewHolder.lblUploadSubject = (TextView) view2.findViewById(R.id.lblUploadSubject);
            viewHolder.lblDownloadSubject = (TextView) view2.findViewById(R.id.lblDownloadSubject);
            viewHolder.ivfile = (ImageView) view2.findViewById(R.id.ivfile);
            BackupCloudEnt backupCloudEnt = (BackupCloudEnt) this.backupCloudEntlist.get(i2);
            TextView textView = viewHolder.lblFolderName;
            if (backupCloudEnt.GetFolderName().length() > 16) {
                StringBuilder sb = new StringBuilder();
                sb.append(backupCloudEnt.GetFolderName().substring(0, 15));
                sb.append("...");
                str2 = sb.toString();
            } else {
                str2 = backupCloudEnt.GetFolderName();
            }
            textView.setText(str2);
            TextView textView2 = viewHolder.lblUploadSubject;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Subject Upload = ");
            sb2.append(Integer.toString(backupCloudEnt.GetUploadCount()));
            textView2.setText(sb2.toString());
            TextView textView3 = viewHolder.lblDownloadSubject;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Subject Download = ");
            sb3.append(Integer.toString(backupCloudEnt.GetDownloadCount()));
            textView3.setText(sb3.toString());
            if (DropboxType.Photos.ordinal() == CloudCommon.ModuleType) {
                viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_photo_icon);
            } else if (DropboxType.Videos.ordinal() == CloudCommon.ModuleType) {
                viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_video_icon);
            } else if (DropboxType.Audio.ordinal() == CloudCommon.ModuleType) {
                viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_audio_icon);
            } else if (DropboxType.Documents.ordinal() == CloudCommon.ModuleType) {
                viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_documents_icon);
            } else if (DropboxType.Notes.ordinal() == CloudCommon.ModuleType) {
                viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_notes_icon);
            } else if (DropboxType.Wallet.ordinal() == CloudCommon.ModuleType) {
                viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_password_icon);
            } else if (DropboxType.ToDo.ordinal() == CloudCommon.ModuleType) {
                viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_todos_icon);
                viewHolder.lblUploadSubject.setVisibility(View.GONE);
                viewHolder.lblDownloadSubject.setVisibility(View.GONE);
            }
            viewHolder.imagestatusitem.setBackgroundResource(backupCloudEnt.GetImageStatus());
            viewHolder.imagesyncitem.setVisibility(backupCloudEnt.GetSyncVisibility());
            if (backupCloudEnt.GetIsInProgress()) {
                viewHolder.imagesyncitem.startAnimation(AnimationUtils.loadAnimation(this.con, R.anim.speaker_plate));
            }
            view2.setTag(viewHolder);
            view2.setTag(R.id.lblFolderName, viewHolder.lblFolderName);
            view2.setTag(R.id.lblUploadSubject, viewHolder.lblUploadSubject);
            view2.setTag(R.id.lblDownloadSubject, viewHolder.lblDownloadSubject);
            view2.setTag(R.id.imagestatusitem, viewHolder.imagestatusitem);
            view2.setTag(R.id.imagesyncitem, viewHolder.imagesyncitem);
            view2.setTag(R.id.ivfile, viewHolder.ivfile);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            view2 = view;
        }
        viewHolder.imagestatusitem.setTag(Integer.valueOf(i));
        viewHolder.imagesyncitem.setTag(Integer.valueOf(i));
        viewHolder.lblFolderName.setTag(Integer.valueOf(i));
        viewHolder.lblUploadSubject.setTag(Integer.valueOf(i));
        viewHolder.lblDownloadSubject.setTag(Integer.valueOf(i));
        viewHolder.ivfile.setTag(Integer.valueOf(i));
        viewHolder.imagestatusitem.setBackgroundResource(((BackupCloudEnt) this.backupCloudEntlist.get(i2)).GetImageStatus());
        viewHolder.imagesyncitem.setVisibility(((BackupCloudEnt) this.backupCloudEntlist.get(i2)).GetSyncVisibility());
        TextView textView4 = viewHolder.lblFolderName;
        if (((BackupCloudEnt) this.backupCloudEntlist.get(i2)).GetFolderName().length() > 16) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(((BackupCloudEnt) this.backupCloudEntlist.get(i2)).GetFolderName().substring(0, 15));
            sb4.append("...");
            str = sb4.toString();
        } else {
            str = ((BackupCloudEnt) this.backupCloudEntlist.get(i2)).GetFolderName();
        }
        textView4.setText(str);
        TextView textView5 = viewHolder.lblUploadSubject;
        StringBuilder sb5 = new StringBuilder();
        sb5.append("Subject Upload = ");
        sb5.append(Integer.toString(((BackupCloudEnt) this.backupCloudEntlist.get(i2)).GetUploadCount()));
        textView5.setText(sb5.toString());
        TextView textView6 = viewHolder.lblDownloadSubject;
        StringBuilder sb6 = new StringBuilder();
        sb6.append("Subject Download = ");
        sb6.append(Integer.toString(((BackupCloudEnt) this.backupCloudEntlist.get(i2)).GetDownloadCount()));
        textView6.setText(sb6.toString());
        if (DropboxType.Photos.ordinal() == CloudCommon.ModuleType) {
            viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_photo_icon);
        } else if (DropboxType.Videos.ordinal() == CloudCommon.ModuleType) {
            viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_video_icon);
        } else if (DropboxType.Audio.ordinal() == CloudCommon.ModuleType) {
            viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_audio_icon);
        } else if (DropboxType.Documents.ordinal() == CloudCommon.ModuleType) {
            viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_documents_icon);
        } else if (DropboxType.Notes.ordinal() == CloudCommon.ModuleType) {
            viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_notes_icon);
        } else if (DropboxType.Wallet.ordinal() == CloudCommon.ModuleType) {
            viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_password_icon);
        } else if (DropboxType.ToDo.ordinal() == CloudCommon.ModuleType) {
            viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_todos_icon);
        }
        return view2;
    }
}
