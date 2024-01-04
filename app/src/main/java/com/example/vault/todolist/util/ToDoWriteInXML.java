package com.example.vault.todolist.util;

import android.app.Activity;
import android.util.Xml;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import com.example.vault.R;
import com.example.vault.common.Constants;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.todolist.model.ToDoPojo;
import com.example.vault.todolist.model.ToDoTask;
import com.example.vault.todolist.util.ToDoDAL;

import org.xmlpull.v1.XmlSerializer;

public class ToDoWriteInXML {
    Constants constants;
    ToDoDAL dal;
    File newFile;
    File oldFile;
    String toDoName;

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

    public boolean saveToDoList(Activity activity, ToDoPojo toDoPojo, String str, boolean z) {
        this.constants = new Constants();
        this.dal = new ToDoDAL(activity);
        this.toDoName = toDoPojo.getTitle();
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.TODOLIST);
        File file = new File(sb.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        String absolutePath = file.getAbsolutePath();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.toDoName);
        sb2.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
        this.newFile = new File(absolutePath, sb2.toString());
        String absolutePath2 = file.getAbsolutePath();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
        this.oldFile = new File(absolutePath2, sb3.toString());
        if (!z) {
            try {
                if (this.newFile.exists()) {
                    ToDoDAL toDoDAL = this.dal;
                    StringBuilder sb4 = new StringBuilder();
                    this.constants.getClass();
                    sb4.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
                    this.constants.getClass();
                    sb4.append("ToDoName");
                    sb4.append("='");
                    sb4.append(this.toDoName);
                    sb4.append("'");
                    if (toDoDAL.IsFileAlreadyExist(sb4.toString())) {
                        Toast.makeText(activity, R.string.toast_exists, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            } catch (Exception e) {
                try {
                    e.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                    return false;
                }
            }
            try {
                this.newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!this.toDoName.equals(str)) {
            if (this.oldFile.exists()) {
                this.oldFile.renameTo(this.newFile);
            }
        } else if (!this.newFile.exists()) {
            try {
                this.newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        XmlSerializer newSerializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();
        try {
            newSerializer.setOutput(stringWriter);

            newSerializer.startDocument(null, Boolean.valueOf(true));
            newSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            newSerializer.startTag(null, Tags.ToDoList.toString());
            newSerializer.startTag(null, Tags.ToDoTitle.toString());
            newSerializer.text(toDoPojo.getTitle());
            newSerializer.endTag(null, Tags.ToDoTitle.toString());
            newSerializer.startTag(null, Tags.ToDoColor.toString());
            newSerializer.text(toDoPojo.getTodoColor());
            newSerializer.endTag(null, Tags.ToDoColor.toString());
            newSerializer.startTag(null, Tags.ToDoDateCreated.toString());
            newSerializer.text(toDoPojo.getDateCreated());
            newSerializer.endTag(null, Tags.ToDoDateCreated.toString());
            newSerializer.startTag(null, Tags.ToDoDateModified.toString());
            newSerializer.text(toDoPojo.getDateModified());
            newSerializer.endTag(null, Tags.ToDoDateModified.toString());
            newSerializer.startTag(null, Tags.ToDoTasks.toString());
            Iterator it = toDoPojo.getToDoList().iterator();
            while (it.hasNext()) {
                ToDoTask toDoTask = (ToDoTask) it.next();
                newSerializer.startTag(null, Tags.ToDoTask.toString()).attribute(null, Tags.isCompleted.toString(), String.valueOf(toDoTask.isChecked()));
                newSerializer.text(toDoTask.getToDo());
                newSerializer.endTag(null, Tags.ToDoTask.toString());
            }
            newSerializer.endTag(null, Tags.ToDoTasks.toString());
            newSerializer.endTag(null, Tags.ToDoList.toString());
            newSerializer.endDocument();
            newSerializer.flush();
            FileOutputStream fileOutputStream = new FileOutputStream(this.newFile);
            fileOutputStream.write(stringWriter.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
