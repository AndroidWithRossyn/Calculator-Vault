package com.example.vault.wallet.util;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.example.vault.Flaes;
import com.example.vault.wallet.model.WalletCategoriesFieldPojo;
import com.example.vault.wallet.model.WalletEntryPojo;

import org.apache.commons.io.IOUtils;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class EntryReadXml {
    public static WalletEntryPojo parseXML(Context context, String str) {

        ArrayList arrayList = new ArrayList();

        WalletCategoriesFieldPojo walletCategoriesFieldPojo = null;
        String str3 = "";
        Boolean bool = Boolean.valueOf(false);
        WalletEntryPojo walletEntryPojo = null;

        try {


            XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
            newPullParser.setInput(new InputStreamReader(new ByteArrayInputStream(Flaes.getdecodedstring(IOUtils.toString((InputStream) new FileInputStream(str), HTTP.UTF_8)).getBytes())));
            int eventType = newPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String name = newPullParser.getName();
                        if (name.equals("EntryInfo")) {
                            bool = Boolean.valueOf(true);
                            walletEntryPojo = new WalletEntryPojo();
                        }
                        if (name.equalsIgnoreCase("Field")) {
                            walletCategoriesFieldPojo = new WalletCategoriesFieldPojo();
                            str3 = name;
                            break;
                        } else {
                            str3 = name;
                            break;
                        }
                    case XmlPullParser.END_TAG:
                        if (newPullParser.getName() == "CategoryInfo") {
                            bool = Boolean.valueOf(false);
                        }
                        str3 = "";
                        break;
                    case XmlPullParser.TEXT:
                        try {
                            String text = newPullParser.getText();

                            if (str3.equals("IsSecured")) {
                                walletCategoriesFieldPojo.setSecured(Boolean.valueOf(text).booleanValue());
                                arrayList.add(walletCategoriesFieldPojo);
                                walletCategoriesFieldPojo = null;
                                // break;
                            } else if (str3.equals("Name")) {
                                walletCategoriesFieldPojo.setFieldName(text);
                                //break;
                            } else if (str3.equals("Value")) {
                                if (text.equals("none")) {
                                    walletCategoriesFieldPojo.setFieldValue("");
                                    //break;
                                } else {
                                    walletCategoriesFieldPojo.setFieldValue(text);
                                    // break;
                                }

                            } else if (str3.equals("CategoryName")) {
                                walletEntryPojo.setCategoryName(newPullParser.getText());
                                //break;
                            } else if (str3.equals("EntryName")) {
                                walletEntryPojo.setEntryName(newPullParser.getText());
                                //break;
                            }
                            break;

                        } catch (Exception e) {
                            e.printStackTrace();

                            Log.e("Entryreadxmleee11", "" + e.toString());
                            break;
                        }
                        //break;
                }


                eventType = newPullParser.next();
                //return walletEntryPojo;
            }
            walletEntryPojo.setFields(arrayList);
            // return walletEntryPojo;


        } catch (Exception e2) {

            walletEntryPojo = null;

            Log.e("Entryreadxml", "" + e2.toString());
        }
        return walletEntryPojo;
    }
}
