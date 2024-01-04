package com.example.vault.tryattempt;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import com.example.vault.R;
import com.example.vault.tryattempt.model.TryAttemptEntity;
import com.example.vault.securitylocks.SecurityLocksCommon.LoginOptions;

public class TryAttemptListAdapter extends ArrayAdapter {
    boolean _isAllCheck = false;
    boolean _isEdit = false;
    private final Context con;

    public final ArrayList<TryAttemptEntity> tryAttemptEntities;
    LayoutInflater layoutInflater;
    Resources res;

    public class ViewHolder {
        public CheckBox cb_tryattempt_item;
        public ImageView iv_tryattempt_item;
        public TextView lbl_tryattempt_description_item;
        public TextView lbl_tryattempt_pass_item;

        public ViewHolder() {
        }
    }

    public TryAttemptListAdapter(Context context, int i, ArrayList<TryAttemptEntity> arrayList, boolean z, Boolean bool) {
        super(context, i, arrayList);
        this.con = context;
        this.tryAttemptEntities = arrayList;
        this.res = context.getResources();
        this._isEdit = z;
        this._isAllCheck = bool.booleanValue();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = this.layoutInflater.inflate(R.layout.try_attempt_activity_item, null);
            viewHolder = new ViewHolder();
            Typeface createFromAsset = Typeface.createFromAsset(this.con.getAssets(), "ebrima.ttf");
            viewHolder.lbl_tryattempt_pass_item = (TextView) view.findViewById(R.id.lbl_tryattempt_pass_item);
            viewHolder.lbl_tryattempt_description_item = (TextView) view.findViewById(R.id.lbl_tryattempt_description_item);
            viewHolder.lbl_tryattempt_pass_item.setTypeface(createFromAsset);
            viewHolder.iv_tryattempt_item = (ImageView) view.findViewById(R.id.iv_tryattempt_item);
            viewHolder.cb_tryattempt_item = (CheckBox) view.findViewById(R.id.cb_tryattempt_item);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_tryattemptitem);
            TryAttemptEntity tryAttemptEntity = (TryAttemptEntity) this.tryAttemptEntities.get(i);
            if (LoginOptions.Password.toString().equals(tryAttemptEntity.GetLoginOption())) {
                TextView textView = viewHolder.lbl_tryattempt_pass_item;
                StringBuilder sb = new StringBuilder();
                sb.append("Wrong Password: ");
                sb.append(tryAttemptEntity.GetWrongPassword());
                textView.setText(sb.toString());
            } else if (LoginOptions.Pin.toString().equals(tryAttemptEntity.GetLoginOption())) {
                TextView textView2 = viewHolder.lbl_tryattempt_pass_item;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Wrong PIN: ");
                sb2.append(tryAttemptEntity.GetWrongPassword());
                textView2.setText(sb2.toString());
            } else if (LoginOptions.Pattern.toString().equals(tryAttemptEntity.GetLoginOption())) {
                TextView textView3 = viewHolder.lbl_tryattempt_pass_item;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Wrong Pattern: ");
                sb3.append(tryAttemptEntity.GetWrongPassword());
                textView3.setText(sb3.toString());
            }
            viewHolder.lbl_tryattempt_description_item.setText(tryAttemptEntity.GetTryAttemptTime().replace("GMT+05:00", ""));
            viewHolder.cb_tryattempt_item.setChecked(tryAttemptEntity.GetIsCheck());
            if (tryAttemptEntity.GetImagePath().length() > 0) {
                viewHolder.iv_tryattempt_item.setImageBitmap(TryAttemptMethods.DecodeFile(new File(tryAttemptEntity.GetImagePath())));
            }
            if (this._isEdit) {
                viewHolder.cb_tryattempt_item.setVisibility(View.VISIBLE);
            } else {
                viewHolder.cb_tryattempt_item.setVisibility(View.INVISIBLE);
            }
            if (this._isAllCheck && tryAttemptEntity.GetIsCheck()) {
                viewHolder.cb_tryattempt_item.setChecked(true);
            }
            viewHolder.cb_tryattempt_item.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    ((TryAttemptEntity) TryAttemptListAdapter.this.tryAttemptEntities.get(((Integer) compoundButton.getTag()).intValue())).SetIsCheck(Boolean.valueOf(compoundButton.isChecked()));
                }
            });
            view.setTag(viewHolder);
            view.setTag(R.id.lbl_tryattempt_pass_item, viewHolder.lbl_tryattempt_pass_item);
            view.setTag(R.id.lbl_tryattempt_description_item, viewHolder.lbl_tryattempt_description_item);
            view.setTag(R.id.iv_tryattempt_item, viewHolder.iv_tryattempt_item);
            view.setTag(R.id.cb_tryattempt_item, viewHolder.cb_tryattempt_item);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.cb_tryattempt_item.setTag(Integer.valueOf(i));
        if (this._isEdit) {
            viewHolder.cb_tryattempt_item.setVisibility(View.VISIBLE);
        } else {
            viewHolder.cb_tryattempt_item.setVisibility(View.INVISIBLE);
        }
        if (LoginOptions.Password.toString().equals(((TryAttemptEntity) this.tryAttemptEntities.get(i)).GetLoginOption())) {
            TextView textView4 = viewHolder.lbl_tryattempt_pass_item;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Wrong Password: ");
            sb4.append(((TryAttemptEntity) this.tryAttemptEntities.get(i)).GetWrongPassword());
            textView4.setText(sb4.toString());
        } else if (LoginOptions.Pin.toString().equals(((TryAttemptEntity) this.tryAttemptEntities.get(i)).GetLoginOption())) {
            TextView textView5 = viewHolder.lbl_tryattempt_pass_item;
            StringBuilder sb5 = new StringBuilder();
            sb5.append("Wrong PIN: ");
            sb5.append(((TryAttemptEntity) this.tryAttemptEntities.get(i)).GetWrongPassword());
            textView5.setText(sb5.toString());
        } else if (LoginOptions.Pattern.toString().equals(((TryAttemptEntity) this.tryAttemptEntities.get(i)).GetLoginOption())) {
            TextView textView6 = viewHolder.lbl_tryattempt_pass_item;
            StringBuilder sb6 = new StringBuilder();
            sb6.append("Wrong Pattern: ");
            sb6.append(((TryAttemptEntity) this.tryAttemptEntities.get(i)).GetWrongPassword());
            textView6.setText(sb6.toString());
        }
        viewHolder.lbl_tryattempt_description_item.setText(((TryAttemptEntity) this.tryAttemptEntities.get(i)).GetTryAttemptTime().replace("GMT+05:00", ""));
        viewHolder.cb_tryattempt_item.setChecked(((TryAttemptEntity) this.tryAttemptEntities.get(i)).GetIsCheck());
        view.startAnimation(AnimationUtils.loadAnimation(this.con, 17432578));
        return view;
    }
}
