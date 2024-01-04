package com.example.vault.todolist.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import com.example.vault.R;
import com.example.vault.common.Constants;
import com.example.vault.common.ValidationCommon;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.todolist.ToDoActivity;
import com.example.vault.todolist.model.ToDoPojo;
import com.example.vault.todolist.util.ToDoReadFromXML;
import com.example.vault.todolist.util.ToDoWriteInXML;
import com.example.vault.todolist.ViewToDoActivity;
import com.example.vault.todolist.model.ToDoDB_Pojo;
import com.example.vault.todolist.util.ToDoDAL;
import com.example.vault.utilities.Common;
import com.example.vault.utilities.Utilities;

public class ToDoListAdapter extends Adapter<ToDoListAdapter.ViewHolder> {
    int ToDofilesCount = 0;
    Constants constants;
    ToDoActivity fragment;
    LayoutInflater inflater;
    Activity mContext;
    ArrayList<ToDoDB_Pojo> toDoList;
    int viewBy = 0;

    public enum ViewBy {
        Detail,
        List,
        Tile
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        ImageView colorBorder;
        ImageView iv_toDoOptions;
        TextView tv_ToDoDate;
        TextView tv_ToDoName;
        TextView tv_ToDoTime;
        TextView tv_task1;
        TextView tv_task1c;
        TextView tv_task2;
        TextView tv_task2c;

        public ViewHolder(View view) {
            super(view);
            this.tv_ToDoName = (TextView) view.findViewById(R.id.tv_ToDoName);
            this.tv_ToDoDate = (TextView) view.findViewById(R.id.tv_ToDoDate);
            this.tv_ToDoTime = (TextView) view.findViewById(R.id.tv_ToDoTime);
            this.tv_task1 = (TextView) view.findViewById(R.id.tv_task1);
            this.tv_task2 = (TextView) view.findViewById(R.id.tv_task2);
            this.tv_task1c = (TextView) view.findViewById(R.id.tv_task1c);
            this.tv_task2c = (TextView) view.findViewById(R.id.tv_task2c);
            this.colorBorder = (ImageView) view.findViewById(R.id.colorBorder);
            this.iv_toDoOptions = (ImageView) view.findViewById(R.id.iv_toDoOptions);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            view.setSelected(true);
            int adapterPosition = getAdapterPosition();
            SecurityLocksCommon.IsAppDeactive = false;
            Common.ToDoListEdit = false;
            Common.ToDoListName = ((ToDoDB_Pojo) ToDoListAdapter.this.toDoList.get(adapterPosition)).getToDoFileName();
            Common.ToDoListId = ((ToDoDB_Pojo) ToDoListAdapter.this.toDoList.get(adapterPosition)).getToDoId();
            ToDoListAdapter.this.mContext.startActivity(new Intent(ToDoListAdapter.this.mContext, ViewToDoActivity.class));
            ToDoListAdapter.this.mContext.finish();
            ToDoListAdapter.this.mContext.overridePendingTransition(17432576, 17432577);
        }
    }

    public ToDoListAdapter(Activity activity, ArrayList<ToDoDB_Pojo> arrayList, ToDoActivity toDoActivity) {
        this.mContext = activity;
        this.fragment = toDoActivity;
        this.toDoList = arrayList;
        this.ToDofilesCount = arrayList.size();
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getItemCount() {
        return this.toDoList.size();
    }

    public void setViewBy(int i) {
        this.viewBy = i;
    }

    public void setAdapterData(ArrayList<ToDoDB_Pojo> arrayList) {
        this.toDoList = arrayList;
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        if (this.ToDofilesCount > 0) {
            String str = ((ToDoDB_Pojo) this.toDoList.get(i)).getToDoFileModifiedDate().split(" ")[0];
            String str2 = ((ToDoDB_Pojo) this.toDoList.get(i)).getToDoFileModifiedDate().split(" ")[1];
            boolean isToDoFileTask1IsChecked = ((ToDoDB_Pojo) this.toDoList.get(i)).isToDoFileTask1IsChecked();
            boolean isToDoFileTask2IsChecked = ((ToDoDB_Pojo) this.toDoList.get(i)).isToDoFileTask2IsChecked();
            boolean isToDoFinished = ((ToDoDB_Pojo) this.toDoList.get(i)).isToDoFinished();
            Utilities.strikeTextview(viewHolder.tv_ToDoName, ((ToDoDB_Pojo) this.toDoList.get(i)).getToDoFileName().toString(), isToDoFinished);
            if (isToDoFinished) {
                viewHolder.tv_ToDoName.append("  ✓");
            }
            TextView textView = viewHolder.tv_ToDoDate;
            StringBuilder sb = new StringBuilder();
            sb.append("Date: ");
            sb.append(str);
            textView.setText(sb.toString());
            TextView textView2 = viewHolder.tv_ToDoTime;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Time: ");
            sb2.append(str2);
            textView2.setText(sb2.toString());
            viewHolder.tv_ToDoDate.setSelected(true);
            viewHolder.tv_ToDoTime.setSelected(true);
            String trim = ((ToDoDB_Pojo) this.toDoList.get(i)).getToDoFileTask1().trim();
            String trim2 = ((ToDoDB_Pojo) this.toDoList.get(i)).getToDoFileTask2().trim();
            TextView textView3 = viewHolder.tv_task1;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("• ");
            sb3.append(trim);
            Utilities.strikeTextview(textView3, sb3.toString(), isToDoFileTask1IsChecked);
            if (!trim2.equals("")) {
                TextView textView4 = viewHolder.tv_task2;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("• ");
                sb4.append(trim2);
                Utilities.strikeTextview(textView4, sb4.toString(), isToDoFileTask2IsChecked);
                viewHolder.tv_task2.setVisibility(View.VISIBLE);
                viewHolder.tv_task2c.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tv_task2c.setVisibility(View.GONE);
            }
            int i2 = -16711936;
            viewHolder.tv_task1c.setTextColor(isToDoFileTask1IsChecked ? -16711936 : this.mContext.getResources().getColor(R.color.Color_Secondary_Font));
            TextView textView5 = viewHolder.tv_task2c;
            if (!isToDoFileTask2IsChecked) {
                i2 = this.mContext.getResources().getColor(R.color.Color_Secondary_Font);
            }
            textView5.setTextColor(i2);
            try {
                String substring = ((ToDoDB_Pojo) this.toDoList.get(i)).getToDoFileColor().substring(3);
                ImageView imageView = viewHolder.colorBorder;
                StringBuilder sb5 = new StringBuilder();
                sb5.append("#E6");
                sb5.append(substring);
                imageView.setBackgroundColor(Color.parseColor(sb5.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewHolder.iv_toDoOptions.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ToDoListAdapter.this.showPopup(view, i);
                }
            });
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (this.viewBy == ViewBy.List.ordinal()) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_todo_list_item, viewGroup, false);
        } else if (this.viewBy == ViewBy.Detail.ordinal()) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_todo_detail_item, viewGroup, false);
        } else if (this.viewBy == ViewBy.Tile.ordinal()) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_todo_grid_item, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_todo_detail_item, viewGroup, false);
        }
        return new ViewHolder(view);
    }


    public void showPopup(View view, final int i) {
        View inflate = this.inflater.inflate(R.layout.popup_delete_rename, null);
        final PopupWindow popupWindow = new PopupWindow(this.mContext);
        popupWindow.setContentView(inflate);
        popupWindow.setWidth(-2);
        popupWindow.setHeight(-2);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(view);
        TextView textView = (TextView) inflate.findViewById(R.id.tv_NotesFolderRename);
        TextView textView2 = (TextView) inflate.findViewById(R.id.tv_NotesFolderDelete);
        textView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                final String toDoFileName = ((ToDoDB_Pojo) ToDoListAdapter.this.toDoList.get(i)).getToDoFileName();
                final Dialog dialog = new Dialog(ToDoListAdapter.this.mContext, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.album_add_edit_popup);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                TextView textView = (TextView) dialog.findViewById(R.id.lbl_Create_Edit_Album);
                final EditText editText = (EditText) dialog.findViewById(R.id.txt_AlbumName);
                editText.setText(toDoFileName);
                LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_Cancel);
                LinearLayout linearLayout2 = (LinearLayout) dialog.findViewById(R.id.ll_Ok);
                textView.setText(R.string.rename_todo);
                linearLayout2.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        String trim = editText.getText().toString().trim();
                        if (trim.trim().equals("")) {
                            Toast.makeText(ToDoListAdapter.this.mContext, "Todo name can't be empty", Toast.LENGTH_SHORT).show();
                        } else if (ValidationCommon.isNoSpecialCharsInName(trim)) {
                            ToDoDAL toDoDAL = new ToDoDAL(ToDoListAdapter.this.mContext);
                            Constants constants = new Constants();
                            String currentDateTime2 = Utilities.getCurrentDateTime2();
                            StringBuilder sb = new StringBuilder();
                            sb.append(StorageOptionsCommon.STORAGEPATH);
                            sb.append(StorageOptionsCommon.TODOLIST);
                            sb.append(trim);
                            sb.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
                            File file = new File(sb.toString());
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append(StorageOptionsCommon.STORAGEPATH);
                            sb2.append(StorageOptionsCommon.TODOLIST);
                            sb2.append(toDoFileName);
                            sb2.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
                            File file2 = new File(sb2.toString());
                            StringBuilder sb3 = new StringBuilder();
                            constants.getClass();
                            sb3.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
                            constants.getClass();
                            sb3.append("ToDoName");
                            sb3.append(" = '");
                            sb3.append(trim);
                            sb3.append("'");
                            boolean IsFileAlreadyExist = toDoDAL.IsFileAlreadyExist(sb3.toString());
                            if (!file2.exists()) {
                                Toast.makeText(ToDoListAdapter.this.mContext, "Todo does not exists", Toast.LENGTH_SHORT).show();
                            } else if (!IsFileAlreadyExist) {
                                if (file.exists()) {
                                    file.delete();
                                }
                                ToDoReadFromXML toDoReadFromXML = new ToDoReadFromXML();
                                ToDoWriteInXML toDoWriteInXML = new ToDoWriteInXML();
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append(StorageOptionsCommon.STORAGEPATH);
                                sb4.append(StorageOptionsCommon.TODOLIST);
                                sb4.append(toDoFileName);
                                sb4.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
                                ToDoPojo ReadToDoList = toDoReadFromXML.ReadToDoList(sb4.toString());
                                ReadToDoList.setTitle(trim);
                                ReadToDoList.setDateModified(currentDateTime2);
                                if (toDoWriteInXML.saveToDoList(ToDoListAdapter.this.mContext, ReadToDoList, toDoFileName, true)) {
                                    ((ToDoDB_Pojo) ToDoListAdapter.this.toDoList.get(i)).setToDoFileName(trim);
                                    ((ToDoDB_Pojo) ToDoListAdapter.this.toDoList.get(i)).setToDoFileLocation(file.getAbsolutePath());
                                    ((ToDoDB_Pojo) ToDoListAdapter.this.toDoList.get(i)).setToDoFileModifiedDate(currentDateTime2);
                                    ToDoDB_Pojo toDoDB_Pojo = (ToDoDB_Pojo) ToDoListAdapter.this.toDoList.get(i);
                                    constants.getClass();
                                    toDoDAL.updateToDoFileInfoInDatabase(toDoDB_Pojo, "ToDoId", String.valueOf(((ToDoDB_Pojo) ToDoListAdapter.this.toDoList.get(i)).getToDoId()));
                                    ToDoListAdapter.this.notifyItemChanged(i);
                                    Toast.makeText(ToDoListAdapter.this.mContext, "Todo renamed successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ToDoListAdapter.this.mContext, "Todo can't be renamed", Toast.LENGTH_SHORT).show();
                                }
                                dialog.cancel();
                            } else {
                                Toast.makeText(ToDoListAdapter.this.mContext, "Todo already exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ToDoListAdapter.this.mContext, "Todo name can't have special characters", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
                linearLayout.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                popupWindow.dismiss();
            }
        });
        textView2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ToDoListAdapter.this.mContext);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.confirmation_message_box);
                LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_Ok);
                LinearLayout linearLayout2 = (LinearLayout) dialog.findViewById(R.id.ll_Cancel);
                ((TextView) dialog.findViewById(R.id.tvmessagedialogtitle)).setText(ToDoListAdapter.this.mContext.getResources().getString(R.string.delete_todo));
                linearLayout.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        try {
                            dialog.dismiss();
                            String toDoFileName = ((ToDoDB_Pojo) ToDoListAdapter.this.toDoList.get(i)).getToDoFileName();
                            StringBuilder sb = new StringBuilder();
                            sb.append(StorageOptionsCommon.STORAGEPATH);
                            sb.append(StorageOptionsCommon.TODOLIST);
                            sb.append(toDoFileName);
                            sb.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
                            File file = new File(sb.toString());
                            if (file.exists() && file.delete()) {
                                new ToDoDAL(ToDoListAdapter.this.mContext).deleteToDoFileFromDatabase("ToDoId", String.valueOf(((ToDoDB_Pojo) ToDoListAdapter.this.toDoList.get(i)).getToDoId()));
                                ToDoListAdapter.this.toDoList.remove(i);
                                ToDoListAdapter.this.notifyItemRemoved(i);
                                ToDoListAdapter.this.fragment.getData();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                linearLayout2.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                popupWindow.dismiss();
            }
        });
    }
}
