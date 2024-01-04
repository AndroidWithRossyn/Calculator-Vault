package com.example.vault.notes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;
import com.example.vault.R;
import com.example.vault.common.Constants;
import com.example.vault.notes.util.NotesFilesDAL;
import com.example.vault.notes.model.NotesFolderDB_Pojo;

public class NotesFolderGridViewAdapter extends BaseAdapter {
    Constants constants;
    int focusedPosition = 0;
    double[] folderSize;
    int foldersCount;
    LayoutInflater inflater;
    boolean isEdit = false;
    boolean isGridView = true;
    private Context mContext;
    int[] notesCount;
    NotesFilesDAL notesFilesDAL;
    List<NotesFolderDB_Pojo> notesFolderPojoList;

    public class Holder {
        ImageView colorBorder;
        ImageView iv_notesFolder;
        LinearLayout ll_selection;
        TextView tv_FolderName;
        TextView tv_NoteFolderDate;
        TextView tv_NoteFolderTime;
        TextView tv_NotesCount;
        TextView tv_noteFolder_size;

        public Holder() {
        }
    }

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        return 0;
    }

    public NotesFolderGridViewAdapter(Context context) {
        this.mContext = context;
    }

    public NotesFolderGridViewAdapter(Context context, List<NotesFolderDB_Pojo> list) {
        int i = 0;
        this.mContext = context;
        this.notesFolderPojoList = list;
        this.foldersCount = list.size();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.notesCount = new int[list.size()];
        this.folderSize = new double[list.size()];
        this.constants = new Constants();
        this.notesFilesDAL = new NotesFilesDAL(context);
        while (true) {
            int[] iArr = this.notesCount;
            if (i < iArr.length) {
                NotesFilesDAL notesFilesDAL2 = this.notesFilesDAL;
                this.constants.getClass();
                StringBuilder sb = new StringBuilder();
                this.constants.getClass();
                sb.append("NotesFolderId");
                sb.append(" = ");
                sb.append(((NotesFolderDB_Pojo) list.get(i)).getNotesFolderId());
                iArr[i] = notesFilesDAL2.getNotesFilesCount("SELECT \t\tCOUNT(*) \t\t\t\t   FROM TableNotesFile WHERE ".concat(sb.toString()));
                double[] dArr = this.folderSize;
                NotesFilesDAL notesFilesDAL3 = this.notesFilesDAL;
                this.constants.getClass();
                StringBuilder sb2 = new StringBuilder();
                this.constants.getClass();
                sb2.append("NotesFolderId");
                sb2.append(" = ");
                sb2.append(((NotesFolderDB_Pojo) list.get(i)).getNotesFolderId());
                dArr[i] = notesFilesDAL3.GetNotesFileRealEntity("SELECT SUM (NotesFileSize) FROM  TableNotesFile WHERE ".concat(sb2.toString()));
                i++;
            } else {
                return;
            }
        }
    }

    public void setFocusedPosition(int i) {
        this.focusedPosition = i;
    }

    public void setIsEdit(boolean z) {
        this.isEdit = z;
    }

    public void setIsGridview(boolean z) {
        this.isGridView = z;
    }

    public int getCount() {
        return this.notesFolderPojoList.size();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (this.foldersCount > 0) {
            String str = ((NotesFolderDB_Pojo) this.notesFolderPojoList.get(i)).getNotesFolderModifiedDate().split(",")[0];
            String str2 = ((NotesFolderDB_Pojo) this.notesFolderPojoList.get(i)).getNotesFolderModifiedDate().split(", ")[1];
            Holder holder = new Holder();
            if (view == null) {
                if (this.isGridView) {
                    view = this.inflater.inflate(R.layout.list_item_notes_folder_gridview, viewGroup, false);
                } else {
                    view = this.inflater.inflate(R.layout.list_item_notes_folder_listview, viewGroup, false);
                }
                holder.tv_FolderName = (TextView) view.findViewById(R.id.tv_FolderName);
                holder.tv_NoteFolderDate = (TextView) view.findViewById(R.id.tv_NoteFolderDate);
                holder.tv_NoteFolderTime = (TextView) view.findViewById(R.id.tv_NoteFolderTime);
                holder.tv_noteFolder_size = (TextView) view.findViewById(R.id.tv_noteFolder_size);
                holder.tv_NotesCount = (TextView) view.findViewById(R.id.tv_NotesCount);
                holder.iv_notesFolder = (ImageView) view.findViewById(R.id.iv_notesFolder);
                holder.ll_selection = (LinearLayout) view.findViewById(R.id.ll_selection);
                holder.colorBorder = (ImageView) view.findViewById(R.id.colorBorder);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            holder.tv_FolderName.setText(((NotesFolderDB_Pojo) this.notesFolderPojoList.get(i)).getNotesFolderName().toString());
            TextView textView = holder.tv_NoteFolderDate;
            StringBuilder sb = new StringBuilder();
            sb.append("Date: ");
            sb.append(str);
            textView.setText(sb.toString());
            TextView textView2 = holder.tv_NoteFolderTime;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Time: ");
            sb2.append(str2);
            textView2.setText(sb2.toString());
            holder.tv_NoteFolderDate.setSelected(true);
            holder.tv_NoteFolderTime.setSelected(true);
            holder.tv_NotesCount.setText(String.valueOf(this.notesCount[i]));
            if (this.isGridView) {
                if (this.notesCount[i] > 0) {
                    holder.iv_notesFolder.setBackgroundResource(R.drawable.ic_notesfolder_thumb_icon);
                } else {
                    holder.iv_notesFolder.setBackgroundResource(R.drawable.imgnotessss);
                }
                holder.tv_noteFolder_size.setVisibility(View.GONE);
            } else {
                if (this.notesCount[i] > 0) {
                    holder.iv_notesFolder.setBackgroundResource(R.drawable.ic_notesfolder_thumb_icon);
                } else {
                    holder.iv_notesFolder.setBackgroundResource(R.drawable.imgnotessss);
                }
                TextView textView3 = holder.tv_noteFolder_size;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Size: ");
                sb3.append(readableFileSize(this.folderSize[i]));
                textView3.setText(sb3.toString());
            }
            try {
                if (((NotesFolderDB_Pojo) this.notesFolderPojoList.get(i)).getNotesFolderName().equals("My Notes")) {
                    holder.colorBorder.setBackgroundColor(this.mContext.getResources().getColor(R.color.ColorLightBlue));
                } else {
                    holder.colorBorder.setBackgroundColor(Integer.parseInt(((NotesFolderDB_Pojo) this.notesFolderPojoList.get(i)).getNotesFolderColor()));
                }
            } catch (Exception unused) {
            }
            if (this.focusedPosition != i || !this.isEdit) {
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
            return "0 B";
        }
        String[] strArr = {"B", "KB", "MB", "GB", "TB"};
        int log10 = (int) (Math.log10(d) / Math.log10(1024.0d));
        StringBuilder sb = new StringBuilder();
        sb.append(new DecimalFormat("#,##0.#").format(d / Math.pow(1024.0d, (double) log10)));
        sb.append(" ");
        sb.append(strArr[log10]);
        return sb.toString();
    }
}
