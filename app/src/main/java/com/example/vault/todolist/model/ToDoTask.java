package com.example.vault.todolist.model;

public class ToDoTask {
    private String ToDo;
    private boolean isChecked;

    public String getToDo() {
        return this.ToDo;
    }

    public void setToDo(String str) {
        this.ToDo = str;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
    }
}
