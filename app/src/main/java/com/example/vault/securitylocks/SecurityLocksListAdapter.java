package com.example.vault.securitylocks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import com.example.vault.R;

public class SecurityLocksListAdapter extends ArrayAdapter {
    private final ArrayList<SecurityLocksEnt> SecurityCredentialsEntList;
    private final Context con;
    LayoutInflater layoutInflater;
    Resources res;

    public SecurityLocksListAdapter(Context context, int i, ArrayList<SecurityLocksEnt> arrayList) {
        super(context, i, arrayList);
        this.con = context;
        this.SecurityCredentialsEntList = arrayList;
        this.res = context.getResources();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressLint({"ViewHolder"})
    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = this.layoutInflater.inflate(R.layout.security_locks_activity_item, null);
        TextView textView = (TextView) inflate.findViewById(R.id.lblloginoptionitem);
        textView.setTextColor(this.con.getResources().getColor(R.color.black_color));
        ImageView imageView = (ImageView) inflate.findViewById(R.id.icon);
        textView.setTypeface(Typeface.createFromAsset(this.con.getAssets(), "ebrima.ttf"));
        CheckBox checkBox = (CheckBox) inflate.findViewById(R.id.cbLoginOptionitem);
        RelativeLayout relativeLayout = (RelativeLayout) inflate.findViewById(R.id.ll_securitycredentials);
        checkBox.setClickable(false);
        SecurityLocksEnt securityLocksEnt = (SecurityLocksEnt) this.SecurityCredentialsEntList.get(i);
        checkBox.setClickable(false);
        if (securityLocksEnt.GetisCheck()) {
            relativeLayout.setBackgroundResource(R.drawable.list_login_option_click);
            checkBox.setChecked(true);
            textView.setTextColor(this.con.getResources().getColor(R.color.ColorAppTheme));
        } else {
            relativeLayout.setBackgroundResource(R.color.Coloractivity_bg);
        }
        textView.setText(securityLocksEnt.GetLoginOption());
        checkBox.setChecked(securityLocksEnt.GetisCheck());
        imageView.setBackgroundResource(securityLocksEnt.GetDrawable());
        return inflate;
    }
}
