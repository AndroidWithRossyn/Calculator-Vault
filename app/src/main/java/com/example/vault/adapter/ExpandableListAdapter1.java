package com.example.vault.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import com.example.vault.R;

public class ExpandableListAdapter1 extends BaseExpandableListAdapter {
    private Context _context;
    private HashMap<String, List<String>> _listDataChild;
    private List<String> _listDataHeader;

    public long getChildId(int i, int i2) {
        return (long) i2;
    }

    public long getGroupId(int i) {
        return (long) i;
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    public ExpandableListAdapter1(Context context, List<String> list, HashMap<String, List<String>> hashMap) {
        this._context = context;
        this._listDataHeader = list;
        this._listDataChild = hashMap;
    }

    public Object getChild(int i, int i2) {
        return ((List) this._listDataChild.get(this._listDataHeader.get(i))).get(i2);
    }

    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        String str = (String) getChild(i, i2);
        if (view == null) {
            view = ((LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_window_list_item, null);
        }
        ((TextView) view.findViewById(R.id.lblListItem)).setText(str);
        return view;
    }

    public int getChildrenCount(int i) {
        return ((List) this._listDataChild.get(this._listDataHeader.get(i))).size();
    }

    public Object getGroup(int i) {
        return this._listDataHeader.get(i);
    }

    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
        String str = (String) getGroup(i);
        if (view == null) {
            view = ((LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_window_list_group, null);
        }
        TextView textView = (TextView) view.findViewById(R.id.lblListHeader);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(str);
        return view;
    }
}
