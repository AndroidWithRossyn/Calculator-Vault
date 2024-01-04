package com.example.vault.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import com.example.vault.R;
import com.example.vault.features.FeatureActivityEnt;

public class GridAdapter extends ArrayAdapter {
    boolean IsStealthModeOn = false;
    private final Context con;
    Dialog dialog;
    private final ArrayList<FeatureActivityEnt> featureEntList;
    LayoutInflater layoutInflater;
    Resources res;

    public GridAdapter(Context context, int i, ArrayList<FeatureActivityEnt> arrayList) {
        super(context, i, arrayList);
        this.con = context;
        this.featureEntList = arrayList;
        this.res = context.getResources();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = this.layoutInflater.inflate(R.layout.grid_layout, null);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.img_grid);
        FeatureActivityEnt featureActivityEnt = (FeatureActivityEnt) this.featureEntList.get(i);
        ((TextView) inflate.findViewById(R.id.tv_text)).setText(featureActivityEnt.get_featureName());
        imageView.setBackgroundResource(featureActivityEnt.get_feature_Icon());
        return inflate;
    }
}
