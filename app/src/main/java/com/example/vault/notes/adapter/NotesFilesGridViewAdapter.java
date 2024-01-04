package com.example.vault.notes.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.example.vault.R;
import com.example.vault.notes.model.NotesFileDB_Pojo;

public class NotesFilesGridViewAdapter extends BaseAdapter {
    List<Integer> focusedPosition;
    LayoutInflater inflater;
    boolean isEdit = false;
    private Context mContext;
    List<NotesFileDB_Pojo> notesFileDB_PojoList;
    int notesfilesCount = 0;
    int viewBy = 0;

    public class Holder {
        ImageView colorBorder;
        ImageView iv_notesFile;
        LinearLayout ll_selection;
        RelativeLayout rl_noteFolder_count;
        TextView tv_FileName;
        TextView tv_NoteContent;
        TextView tv_NoteFileDate;
        TextView tv_NoteFileTime;
        TextView tv_noteFolder_size;

        public Holder() {
        }
    }

    public enum ViewBy {
        Detail,
        List,
        Tile
    }

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        return 0;
    }

    public NotesFilesGridViewAdapter(Context context) {
        this.mContext = context;
    }

    public NotesFilesGridViewAdapter(Context context, List<NotesFileDB_Pojo> list) {
        this.mContext = context;
        this.notesFileDB_PojoList = list;
        this.notesfilesCount = list.size();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.focusedPosition = new ArrayList();
    }

    public void setFocusedPosition(int i) {
        if (!this.focusedPosition.contains(Integer.valueOf(i))) {
            this.focusedPosition.add(Integer.valueOf(i));
        }
    }

    public void removeFocusedPosition(int i) {
        if (this.focusedPosition.contains(Integer.valueOf(i))) {
            this.focusedPosition.remove(this.focusedPosition.indexOf(Integer.valueOf(i)));
        }
    }

    public void removeAllFocusedPosition() {
        this.focusedPosition.clear();
    }

    public void setIsEdit(boolean z) {
        this.isEdit = z;
    }

    public void setViewBy(int i) {
        this.viewBy = i;
    }

    public int getCount() {
        return this.notesFileDB_PojoList.size();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (this.notesfilesCount > 0) {
            String str = ((NotesFileDB_Pojo) this.notesFileDB_PojoList.get(i)).getNotesFileModifiedDate().split(",")[0];
            String str2 = ((NotesFileDB_Pojo) this.notesFileDB_PojoList.get(i)).getNotesFileModifiedDate().split(", ")[1];
            Holder holder = new Holder();
            if (view == null) {
                if (this.viewBy == ViewBy.List.ordinal()) {
                    view = this.inflater.inflate(R.layout.list_item_notes_folder_listview, viewGroup, false);
                } else if (this.viewBy == ViewBy.Tile.ordinal()) {
                    view = this.inflater.inflate(R.layout.list_item_notes_folder_gridview, viewGroup, false);
                } else {
                    view = this.inflater.inflate(R.layout.list_item_notes_folder_detailview, viewGroup, false);
                }
                holder.tv_FileName = (TextView) view.findViewById(R.id.tv_FolderName);
                holder.tv_NoteFileTime = (TextView) view.findViewById(R.id.tv_NoteFolderTime);
                holder.tv_NoteFileDate = (TextView) view.findViewById(R.id.tv_NoteFolderDate);
                holder.tv_noteFolder_size = (TextView) view.findViewById(R.id.tv_noteFolder_size);
                holder.iv_notesFile = (ImageView) view.findViewById(R.id.iv_notesFolder);
                holder.ll_selection = (LinearLayout) view.findViewById(R.id.ll_selection);
                holder.rl_noteFolder_count = (RelativeLayout) view.findViewById(R.id.rl_noteFolder_count);
                holder.colorBorder = (ImageView) view.findViewById(R.id.colorBorder);
                holder.tv_NoteContent = (TextView) view.findViewById(R.id.tv_NoteContent);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            double notesFileSize = ((NotesFileDB_Pojo) this.notesFileDB_PojoList.get(i)).getNotesFileSize();
            holder.tv_FileName.setText(((NotesFileDB_Pojo) this.notesFileDB_PojoList.get(i)).getNotesFileName());
            TextView textView = holder.tv_NoteFileDate;
            StringBuilder sb = new StringBuilder();
            sb.append("Date: ");
            sb.append(str);
            textView.setText(sb.toString());
            TextView textView2 = holder.tv_NoteFileTime;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Time: ");
            sb2.append(str2);
            textView2.setText(sb2.toString());
            holder.tv_NoteFileDate.setSelected(true);
            holder.tv_NoteFileTime.setSelected(true);
            holder.rl_noteFolder_count.setVisibility(View.INVISIBLE);
            TextView textView3 = holder.tv_noteFolder_size;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Size: ");
            sb3.append(readableFileSize(notesFileSize));
            textView3.setText(sb3.toString());
            try {
                holder.tv_NoteContent.setText(((NotesFileDB_Pojo) this.notesFileDB_PojoList.get(i)).getNotesFileText().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (this.viewBy == ViewBy.Tile.ordinal()) {
                holder.iv_notesFile.setBackgroundResource(R.drawable.ic_notesfolder_thumb_icon);
            } else {
                holder.iv_notesFile.setBackgroundResource(R.drawable.ic_notesfolder_thumb_icon);
            }
            try {
                holder.colorBorder.setBackgroundColor(Color.parseColor(((NotesFileDB_Pojo) this.notesFileDB_PojoList.get(i)).getNotesfileColor()));
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            if (!this.focusedPosition.contains(Integer.valueOf(i)) || !this.isEdit) {
                holder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
            } else {
                holder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
            }
        } else {
            view.setVisibility(View.GONE);
        }
        if (!this.isEdit) {
            view.startAnimation(AnimationUtils.loadAnimation(this.mContext, R.anim.anim_fade_in));
        }
        return view;
    }

    public String readableFileSize(double d) {
        if (d <= 0.0d) {
            return "0";
        }
        String[] strArr = {"B", "kB", "MB", "GB", "TB"};
        int log10 = (int) (Math.log10(d) / Math.log10(1024.0d));
        StringBuilder sb = new StringBuilder();
        sb.append(new DecimalFormat("#,##0.#").format(d / Math.pow(1024.0d, (double) log10)));
        sb.append(" ");
        sb.append(strArr[log10]);
        return sb.toString();
    }
}
