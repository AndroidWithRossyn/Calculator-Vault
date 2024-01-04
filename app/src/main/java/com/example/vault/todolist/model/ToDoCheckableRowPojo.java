package com.example.vault.todolist.model;

import android.widget.CheckBox;
import android.widget.TextView;

public class ToDoCheckableRowPojo {
    private CheckBox cb_done;
    private TextView tv_text;

    public TextView getTv_text() {
        return this.tv_text;
    }

    public void setTv_text(TextView textView) {
        this.tv_text = textView;
    }

    public CheckBox getCb_done() {
        return this.cb_done;
    }

    public void setCb_done(CheckBox checkBox) {
        this.cb_done = checkBox;
    }
}
