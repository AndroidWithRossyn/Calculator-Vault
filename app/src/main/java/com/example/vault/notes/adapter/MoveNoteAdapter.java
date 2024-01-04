package com.example.vault.notes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import com.example.vault.R;
import com.example.vault.notes.model.NotesFolderDB_Pojo;

public class MoveNoteAdapter extends BaseAdapter {
    Context con;
    LayoutInflater layoutInflater;
    List<NotesFolderDB_Pojo> noteFolderlist;

    public class ViewHolder {
        ImageView imgurlitem;
        TextView lblmoveitem;

        public ViewHolder() {
        }
    }

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        return 0;
    }

    public MoveNoteAdapter(Context context, List<NotesFolderDB_Pojo> list) {
        this.noteFolderlist = list;
        this.con = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();
        if (view == null) {
            view = this.layoutInflater.inflate(R.layout.move_customlistview_item, viewGroup, false);
            viewHolder.lblmoveitem = (TextView) view.findViewById(R.id.lblmoveitem);
            viewHolder.imgurlitem = (ImageView) view.findViewById(R.id.imgurlitem);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.lblmoveitem.setText(((NotesFolderDB_Pojo) this.noteFolderlist.get(i)).getNotesFolderName());
        viewHolder.imgurlitem.setVisibility(View.GONE);
        return view;
    }

    public int getCount() {
        return this.noteFolderlist.size();
    }
}
