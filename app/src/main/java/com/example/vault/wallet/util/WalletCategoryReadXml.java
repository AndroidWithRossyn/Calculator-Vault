package com.example.vault.wallet.util;


import android.util.Log;

import com.example.vault.wallet.model.WalletCategoriesFieldPojo;
import com.example.vault.wallet.model.WalletCategoriesPojo;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class WalletCategoryReadXml {

    public static WalletCategoriesPojo ReadCategoryFromXml(String str) {

        Log.e("wallpaper", "" + str);
        WalletCategoriesPojo walletCategoriesPojo = null;

        try {
            XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
            InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(str.getBytes()));
            ArrayList arrayList = new ArrayList();
            newPullParser.setInput(inputStreamReader);
            int eventType = newPullParser.getEventType();
            String str3 = "";
            WalletCategoriesFieldPojo walletCategoriesFieldPojo = null;
            Boolean bool = Boolean.valueOf(false);

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        str = newPullParser.getName();
                        if (str.equals("CategoryInfo")) {
                            bool = Boolean.valueOf(true);
                            walletCategoriesPojo = new WalletCategoriesPojo();
                            str3 = str;
                            break;
                        } else if (str.equals("Field")) {
                            walletCategoriesFieldPojo = new WalletCategoriesFieldPojo();
                            str3 = str;
                            break;
                        } else {
                            str3 = str;
                            break;
                        }

                    case XmlPullParser.TEXT:
                        try {
                            if (bool.booleanValue() && walletCategoriesPojo != null) {


                                if (str3.equals("IconIndex")) {
                                    walletCategoriesPojo.setCategoryIconIndex(Integer.parseInt(newPullParser.getText()));
                                    break;

                                } else if (str3.equals("CategoryName")) {

                                    Log.e("CategoryName", "" + newPullParser.getText());
                                    walletCategoriesPojo.setCategoryName(newPullParser.getText());
                                    break;

                                } else if (str3.equals("Field")) {

                                    Log.e("Field", "" + newPullParser.getText());
                                    walletCategoriesFieldPojo.setFieldName(newPullParser.getText());
                                    break;

                                } else if (str3.equals("IsSecured")) {
                                    if (walletCategoriesFieldPojo == null) {
                                        break;
                                    }
                                    walletCategoriesFieldPojo.setSecured(Boolean.parseBoolean(newPullParser.getText()));
                                    arrayList.add(walletCategoriesFieldPojo);
                                    walletCategoriesFieldPojo = null;
                                    break;

                                }

                            }

                        } catch (Exception e2) {
                            e2.printStackTrace();
                            break;
                        }

                    case XmlPullParser.END_TAG:
                        if (newPullParser.getName() == "CategoryInfo") {
                            bool = Boolean.valueOf(false);
                        }
                        str3 = "";
                        break;
                    default:
                        break;
                }
                eventType = newPullParser.next();
                // return walletCategoriesPojo;
            }
            walletCategoriesPojo.setCategoryFields(arrayList);
        } catch (Exception e3) {

            walletCategoriesPojo = null;
        }
        return walletCategoriesPojo;
    }
}