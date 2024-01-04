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
import android.widget.TextView;
import java.util.List;
import com.example.vault.R;
import com.example.vault.common.Constants;
import com.example.vault.wallet.model.WalletCategoriesFileDB_Pojo;
import com.example.vault.wallet.util.WalletEntriesDAL;

public class WalletCategoriesAdapter extends BaseAdapter {
    int categoriesCount;
    Constants constants;
    private Context context;
    int count;
    int[] entryCount;
    int focusedPosition = 0;
    LayoutInflater inflater;
    boolean isEdit = false;
    boolean isGridView = true;
    List<WalletCategoriesFileDB_Pojo> walletCategoriesFileInfoPojoList;

    public class ViewHolder {
        ImageView iv_walletCategoriesIcon;
        LinearLayout ll_selection;
        TextView tv_WalletCategoriesEntryCount;
        TextView tv_WalletCategoriesName;

        public ViewHolder() {
        }
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public WalletCategoriesAdapter(Context context2, List<WalletCategoriesFileDB_Pojo> list) {
        int i = 0;
        WalletEntriesDAL walletEntriesDAL = new WalletEntriesDAL(context2);
        this.context = context2;
        this.walletCategoriesFileInfoPojoList = list;
        this.categoriesCount = this.walletCategoriesFileInfoPojoList.size();
        this.constants = new Constants();
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.entryCount = new int[this.walletCategoriesFileInfoPojoList.size()];
        while (true) {
            int[] iArr = this.entryCount;
            if (i < iArr.length) {
                this.constants.getClass();
                StringBuilder sb = new StringBuilder();
                this.constants.getClass();
                sb.append("WalletCategoriesFileId");
                sb.append(" = ");
                sb.append(((WalletCategoriesFileDB_Pojo) list.get(i)).getCategoryFileId());
                iArr[i] = walletEntriesDAL.getEntriesCount("SELECT \t COUNT(*)\t\t\t\t\t   FROM TableWalletEntries WHERE ".concat(sb.toString()));
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
        return this.walletCategoriesFileInfoPojoList.size();
    }

    public Object getItem(int i) {
        return this.walletCategoriesFileInfoPojoList.get(i);
    }

    @Override
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
                viewHolder.tv_WalletCategoriesName = (TextView) view2.findViewById(R.id.tv_WalletTitle);
                viewHolder.tv_WalletCategoriesEntryCount = (TextView) view2.findViewById(R.id.tv_WalletCategoriesEntryCount);
                viewHolder.iv_walletCategoriesIcon = (ImageView) view2.findViewById(R.id.iv_walletCategoriesIcon);
                viewHolder.ll_selection = (LinearLayout) view2.findViewById(R.id.ll_selection);
                view2.setTag(viewHolder);
            } else {
                view2 = view;
                viewHolder = (ViewHolder) view.getTag();
            }
            TypedArray obtainTypedArray = this.context.getResources().obtainTypedArray(R.array.wallet_drawables_list);
            int resourceId = obtainTypedArray.getResourceId(((WalletCategoriesFileDB_Pojo) this.walletCategoriesFileInfoPojoList.get(i)).getCategoryFileIconIndex(), -1);
            viewHolder.tv_WalletCategoriesEntryCount.setText(String.valueOf(this.entryCount[i]));
            viewHolder.tv_WalletCategoriesName.setText(((WalletCategoriesFileDB_Pojo) this.walletCategoriesFileInfoPojoList.get(i)).getCategoryFileName());
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
