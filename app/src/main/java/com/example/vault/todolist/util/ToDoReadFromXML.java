package com.example.vault.todolist.util;

import com.example.vault.todolist.model.ToDoTask;
import com.example.vault.todolist.model.ToDoPojo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ToDoReadFromXML {
    String CurrentTag = "";
    Boolean inDataTag = Boolean.valueOf(false);
    ToDoPojo toDoPojo;
    ToDoTask toDoTask;
    ArrayList<ToDoTask> toDoTasksList;

    public enum Tags {
        ToDoList,
        ToDoTitle,
        ToDoColor,
        ToDoDateCreated,
        ToDoDateModified,
        ToDoTasks,
        ToDoTask,
        isCompleted
    }

    public ToDoPojo ReadToDoList(String str) {
        try {
            File file = new File(str);
            XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
            newPullParser.setInput(new InputStreamReader(new ByteArrayInputStream(IOUtils.toString((InputStream) new FileInputStream(file), HTTP.UTF_8).getBytes())));
            int eventType = newPullParser.getEventType();

            while (eventType != 1) {
                switch (eventType) {
                    case 2:
                        this.CurrentTag = newPullParser.getName();
                        if (!this.CurrentTag.equals(Tags.ToDoList.toString())) {
                            if (!this.CurrentTag.equals(Tags.ToDoTasks.toString())) {
                                if (!this.CurrentTag.equals(Tags.ToDoTask.toString())) {
                                    break;
                                } else {
                                    this.toDoTask = new ToDoTask();
                                    this.toDoTask.setChecked(Boolean.parseBoolean(newPullParser.getAttributeValue(null, Tags.isCompleted.toString())));
                                    break;
                                }
                            } else {
                                this.toDoTasksList = new ArrayList<>();
                                break;
                            }
                        } else {
                            this.inDataTag = Boolean.valueOf(true);
                            this.toDoPojo = new ToDoPojo();
                            break;
                        }
                    case 3:
                        if (newPullParser.getName() == Tags.ToDoList.toString()) {
                            this.inDataTag = Boolean.valueOf(false);
                        }
                        this.CurrentTag = "";
                        break;
                    case 4:
                        if (this.inDataTag.booleanValue() && this.toDoPojo != null) {
                            String str2 = this.CurrentTag;


                            if (str2.equals("ToDoTask")) {
                                this.toDoTask.setToDo(newPullParser.getText());
                                this.toDoTasksList.add(this.toDoTask);
                                this.toDoTask = null;
                                break;
                            } else if (str2.equals("ToDoTitle")) {
                                this.toDoPojo.setTitle(newPullParser.getText());
                                break;
                            }else if (str2.equals("ToDoDateModified")) {
                                this.toDoPojo.setDateModified(newPullParser.getText());
                                break;
                            }else if (str2.equals("ToDoDateCreated")) {
                                this.toDoPojo.setDateCreated(newPullParser.getText());
                                break;
                            }else if (str2.equals("ToDoColor")) {
                                this.toDoPojo.setTodoColor(newPullParser.getText());
                                break;
                            }
                        }


                }
                eventType = newPullParser.next();
            }

        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        this.toDoPojo.setToDoList(this.toDoTasksList);
        // return this.toDoPojo;


        return toDoPojo;
    }
}
