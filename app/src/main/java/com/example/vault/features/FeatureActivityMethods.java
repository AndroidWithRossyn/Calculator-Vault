package com.example.vault.features;

import android.content.Context;
import java.util.ArrayList;
import com.example.vault.R;

public class FeatureActivityMethods {
    public ArrayList<FeatureActivityEnt> GetFeatures(Context context) {
        ArrayList<FeatureActivityEnt> arrayList = new ArrayList<>();
        FeatureActivityEnt featureActivityEnt = new FeatureActivityEnt();
        featureActivityEnt.set_featureName(context.getResources().getString(R.string.lblFeature1));
        featureActivityEnt.set_feature_Icon(R.drawable.photos);
        arrayList.add(featureActivityEnt);
        FeatureActivityEnt featureActivityEnt2 = new FeatureActivityEnt();
        featureActivityEnt2.set_featureName(context.getResources().getString(R.string.lblFeature2));
        featureActivityEnt2.set_feature_Icon(R.drawable.videos);
        arrayList.add(featureActivityEnt2);
        FeatureActivityEnt featureActivityEnt3 = new FeatureActivityEnt();
        featureActivityEnt3.set_featureName(context.getResources().getString(R.string.lblFeature3));
        featureActivityEnt3.set_feature_Icon(R.drawable.gallery);
        arrayList.add(featureActivityEnt3);
        FeatureActivityEnt featureActivityEnt4 = new FeatureActivityEnt();
        featureActivityEnt4.set_featureName(context.getResources().getString(R.string.lblFeature9));
        featureActivityEnt4.set_feature_Icon(R.drawable.audio);
        arrayList.add(featureActivityEnt4);
        FeatureActivityEnt featureActivityEnt5 = new FeatureActivityEnt();
        featureActivityEnt5.set_featureName(context.getResources().getString(R.string.lblFeature4));
        featureActivityEnt5.set_feature_Icon(R.drawable.document);
        arrayList.add(featureActivityEnt5);
        FeatureActivityEnt featureActivityEnt6 = new FeatureActivityEnt();
        featureActivityEnt6.set_featureName(context.getResources().getString(R.string.lblFeature8));
        featureActivityEnt6.set_feature_Icon(R.drawable.passsword);
        arrayList.add(featureActivityEnt6);
        FeatureActivityEnt featureActivityEnt7 = new FeatureActivityEnt();
        featureActivityEnt7.set_featureName(context.getResources().getString(R.string.lblFeature6));
        featureActivityEnt7.set_feature_Icon(R.drawable.notes);
        arrayList.add(featureActivityEnt7);
        FeatureActivityEnt featureActivityEnt8 = new FeatureActivityEnt();
        featureActivityEnt8.set_featureName(context.getResources().getString(R.string.lblFeature10));
        featureActivityEnt8.set_feature_Icon(R.drawable.todolist);
        arrayList.add(featureActivityEnt8);
        return arrayList;
    }
}
