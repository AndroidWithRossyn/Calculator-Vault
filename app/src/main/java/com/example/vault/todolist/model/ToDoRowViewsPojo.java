package com.example.vault.todolist.model;

import android.widget.EditText;
import android.widget.ImageView;

public class ToDoRowViewsPojo {
    private EditText et_text;
    private boolean isChecked = false;
    private ImageView iv_rowDel;
    private ImageView iv_rowDown;
    private ImageView iv_rowUp;

    public ImageView getIv_rowUp() {
        return this.iv_rowUp;
    }

    public void setIv_rowUp(ImageView imageView) {
        this.iv_rowUp = imageView;
    }

    public ImageView getIv_rowDown() {
        return this.iv_rowDown;
    }

    public void setIv_rowDown(ImageView imageView) {
        this.iv_rowDown = imageView;
    }

    public ImageView getIv_rowDel() {
        return this.iv_rowDel;
    }

    public void setIv_rowDel(ImageView imageView) {
        this.iv_rowDel = imageView;
    }

    public EditText getEt_text() {
        return this.et_text;
    }

    public void setEt_text(EditText editText) {
        this.et_text = editText;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
    }
}
