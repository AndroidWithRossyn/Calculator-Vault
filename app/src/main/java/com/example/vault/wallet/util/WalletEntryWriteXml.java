package com.example.vault.wallet.util;

import android.content.Context;
import android.util.Xml;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import com.example.vault.Flaes;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.wallet.model.WalletCategoriesFieldPojo;
import com.example.vault.wallet.model.WalletEntryFileDB_Pojo;
import com.example.vault.wallet.model.WalletEntryPojo;
import com.example.vault.wallet.util.WalletCommon;

import org.xmlpull.v1.XmlSerializer;

public class WalletEntryWriteXml {
    WalletEntryFileDB_Pojo entryFileinfoModel;

    public static boolean write(WalletEntryPojo walletEntryPojo, Context context, Boolean bool) {
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.WALLET);
        sb.append(WalletCommon.walletCurrentCategoryName);
        File file = new File(sb.toString());
        String absolutePath = file.getAbsolutePath();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(StorageOptionsCommon.ENTRY);
        sb2.append(walletEntryPojo.getEntryName());
        sb2.append(StorageOptionsCommon.WALLET_ENTRY_FILE_EXTENSION);
        File file2 = new File(absolutePath, sb2.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            if (bool.booleanValue() && !file2.exists()) {
                file2.createNewFile();
            }
            XmlSerializer newSerializer = Xml.newSerializer();
            StringWriter stringWriter = new StringWriter();
            newSerializer.setOutput(stringWriter);
            newSerializer.startDocument(null, Boolean.valueOf(true));
            newSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            newSerializer.startTag(null, "EntryInfo");
            newSerializer.startTag(null, "CategoryName");
            newSerializer.text(walletEntryPojo.getCategoryName());
            newSerializer.endTag(null, "CategoryName");
            newSerializer.startTag(null, "EntryName");
            newSerializer.text(walletEntryPojo.getEntryName());
            newSerializer.endTag(null, "EntryName");
            for (WalletCategoriesFieldPojo walletCategoriesFieldPojo : walletEntryPojo.getFields()) {
                newSerializer.startTag(null, "Field");
                newSerializer.startTag(null, "Name");
                newSerializer.text(walletCategoriesFieldPojo.getFieldName());
                newSerializer.endTag(null, "Name");
                newSerializer.startTag(null, "Value");
                if (walletCategoriesFieldPojo.getFieldValue().equals("")) {
                    newSerializer.text("none");
                } else {
                    newSerializer.text(walletCategoriesFieldPojo.getFieldValue());
                }
                newSerializer.endTag(null, "Value");
                newSerializer.startTag(null, "IsSecured");
                newSerializer.text(String.valueOf(walletCategoriesFieldPojo.isSecured()));
                newSerializer.endTag(null, "IsSecured");
                newSerializer.endTag(null, "Field");
            }
            newSerializer.endTag(null, "EntryInfo");
            newSerializer.endDocument();
            newSerializer.flush();
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            fileOutputStream.write(Flaes.getencodedstring(stringWriter.toString()).getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
