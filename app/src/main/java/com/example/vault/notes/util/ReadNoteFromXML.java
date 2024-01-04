package com.example.vault.notes.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.example.vault.Flaes;

import org.apache.commons.io.IOUtils;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ReadNoteFromXML {
    String CurrentTag = "";

    HashMap<String, String> hashMap = null;
    Boolean inDataTag = Boolean.valueOf(false);
    String key = "";
    String value = "";


    public HashMap<String, String> ReadNote(String str) {
        try {
            File file = new File(str);
            XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
            newPullParser.setInput(new InputStreamReader(new ByteArrayInputStream(IOUtils.toString((InputStream) new FileInputStream(file), HTTP.UTF_8).getBytes())));
            int eventType = newPullParser.getEventType();
            while (eventType != 1) {
                char c = 1;

                switch (eventType) {
                    case 2:
                        this.CurrentTag = newPullParser.getName();
                        if (!this.CurrentTag.equals("dict")) {
                            break;
                        } else {
                            this.inDataTag = Boolean.valueOf(true);
                            this.hashMap = new HashMap<>();
                            break;
                        }
                    case 3:
                        if (this.CurrentTag.equals("string") || (this.CurrentTag.equals("date") && !this.value.equals("") && !this.key.equals(""))) {
                            this.hashMap.put(this.key, this.value);
                            this.value = "";
                            this.key = "";
                        }
                        if (newPullParser.getName() == "dict") {
                            this.inDataTag = Boolean.valueOf(false);
                        }
                        this.CurrentTag = "";
                        break;
                    case 4:
                        if (this.inDataTag.booleanValue() && this.hashMap != null) {
                            String str2 = this.CurrentTag;


                            if (str2.equals("string")) {
                                switch (c) {
                                    case 0:
                                        break;
                                    case 1:
                                        if (!this.key.equals("Text")) {
                                            this.value = newPullParser.getText();
                                            break;
                                        } else {
                                            try {
                                                this.value = Flaes.getdecodedstring(newPullParser.getText());
                                                break;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                break;
                                            }
                                        }

                                    case 2:
                                        break;
                                }
                            } else if (str2.equals("key")) {
                                c = 0;
                                switch (c) {
                                    case 0:
                                        this.key = newPullParser.getText();
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        break;
                                }
                            } else if (str2.equals("date")) {
                                c = 2;
                                switch (c) {
                                    case 0:
                                        this.key = newPullParser.getText();
                                        break;
                                    case 1:
                                        if (!this.key.equals("Text")) {
                                            this.value = newPullParser.getText();
                                            break;
                                        } else {
                                            try {
                                                this.value = Flaes.getdecodedstring(newPullParser.getText());
                                                break;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                break;
                                            }
                                        }
                                    case 2:
                                        this.value = newPullParser.getText();
                                        break;
                                }
                            }


                        }
                        break;
                }
                eventType = newPullParser.next();
            }
        } catch (IOException | XmlPullParserException e2) {
            e2.printStackTrace();
        }
        return hashMap;
    }
}
