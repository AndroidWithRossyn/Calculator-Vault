package com.example.vault.wallet.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;
import com.example.vault.R;
import com.example.vault.wallet.model.WalletEntryFileDB_Pojo;

public class WalletEntriesAdapter extends BaseAdapter {
    int categoriesCount;
    private Context context;
    int count;
    int[] entryCount;
    int focusedPosition = 0;
    LayoutInflater inflater;
    boolean isEdit = false;
    boolean isGridView = true;
    List<WalletEntryFileDB_Pojo> walletEntryDB_Pojo;

    public class ViewHolder {
        ImageView iv_walletCategoriesIcon;
        RelativeLayout ll_WalletCategoriesEntryCount;
        LinearLayout ll_selection;
        TextView tv_WalletTitle;

        public ViewHolder() {
        }
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public WalletEntriesAdapter(Context context2, List<WalletEntryFileDB_Pojo> list) {
        this.context = context2;
        this.walletEntryDB_Pojo = list;
        this.categoriesCount = list.size();
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.entryCount = new int[list.size()];
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
        return this.walletEntryDB_Pojo.size();
    }

    public Object getItem(int i) {
        return this.walletEntryDB_Pojo.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        ViewHolder viewHolder;
        if (this.categoriesCount > 0) {
            if (view == null) {
                viewHolder = new ViewHolder();
                if (this.isGridView) {
                    view2 = this.inflater.inflate(R.layout.wallet_categories_item_gridview, viewGroup, false);
                } else {
                    view2 = this.inflater.inflate(R.layout.wallet_categories_item_listview, viewGroup, false);
                }
                viewHolder.tv_WalletTitle = (TextView) view2.findViewById(R.id.tv_WalletTitle);
                viewHolder.ll_WalletCategoriesEntryCount = (RelativeLayout) view2.findViewById(R.id.ll_WalletCategoriesEntryCount);
                viewHolder.iv_walletCategoriesIcon = (ImageView) view2.findViewById(R.id.iv_walletCategoriesIcon);
                viewHolder.ll_selection = (LinearLayout) view2.findViewById(R.id.ll_selection);
                view2.setTag(viewHolder);
            } else {
                view2 = view;
                viewHolder = (ViewHolder) view.getTag();
            }
            TypedArray obtainTypedArray = this.context.getResources().obtainTypedArray(R.array.wallet_drawables_list);
            int resourceId = obtainTypedArray.getResourceId(((WalletEntryFileDB_Pojo) this.walletEntryDB_Pojo.get(i)).getCategoryFileIconIndex(), -1);
            viewHolder.tv_WalletTitle.setText(((WalletEntryFileDB_Pojo) this.walletEntryDB_Pojo.get(i)).getEntryFileName());
            viewHolder.ll_WalletCategoriesEntryCount.setVisibility(View.INVISIBLE);
            viewHolder.iv_walletCategoriesIcon.setBackgroundResource(resourceId);
            obtainTypedArray.recycle();
            if (this.focusedPosition != i || !this.isEdit) {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
            } else {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
            }
            view = view2;
        } else {
            view.setVisibility(View.GONE);
        }
        view.startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.anim_fade_in));
        return view;
    }
}
