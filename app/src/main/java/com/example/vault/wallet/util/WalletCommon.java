package com.example.vault.wallet.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;
import com.example.vault.common.Constants;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.wallet.model.WalletCategoriesFileDB_Pojo;
import com.example.vault.wallet.model.WalletCategoriesPojo;
import com.example.vault.wallet.util.WalletCategoriesDAL;
import com.example.vault.wallet.util.WalletCategoryReadXml;

import org.apache.commons.io.IOUtils;
import org.apache.http.protocol.HTTP;

public class WalletCommon {
    public static int WalletCurrentCategoryId = 0;
    public static int walletCategoryScrollIndex = 0;
    public static String walletCurrentCategoryName = "";
    public static String walletCurrentEntryName = "";

    public WalletCategoriesPojo getCurrentCategoryData(Context context, String str) {
        AssetManager assets = context.getAssets();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("default-categories/Category_");
            sb.append(str);
            sb.append(".xml");
            Log.e("default-categories",""+ WalletCategoryReadXml.ReadCategoryFromXml(IOUtils.toString(assets.open(sb.toString()), HTTP.UTF_8)));
            return WalletCategoryReadXml.ReadCategoryFromXml(IOUtils.toString(assets.open(sb.toString()), HTTP.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("naitikex",""+e.toString());
            return null;
        }
    }

    public void createDefaultCategories(Context context) {
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(context);
        Constants constants = new Constants();
        StringBuilder sb = new StringBuilder();
        constants.getClass();
        sb.append("SELECT \t COUNT(*)\t\t\t\t\t   FROM TableWalletCategories");
        sb.append(" WHERE ");
        constants.getClass();
        sb.append("WalletCategoriesFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        int GetWalletCategoriesIntegerEntity = walletCategoriesDAL.GetWalletCategoriesIntegerEntity(sb.toString());
        constants.getClass();
        if (GetWalletCategoriesIntegerEntity < 14) {
            addCategoryFromAssetToDb(context);
        }
    }

    public void addCategoryFromAssetToDb(Context context) {
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(context);
        AssetManager assets = context.getAssets();
        Constants constants = new Constants();
        try {
            for (String FileNamed : assets.list("default-categories")) {
                String FileName2 = FileName(FileNamed);
                StringBuilder sb = new StringBuilder();
                constants.getClass();
                sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
                constants.getClass();
                sb.append("WalletCategoriesFileIsDecoy");
                sb.append(" = ");
                sb.append(SecurityLocksCommon.IsFakeAccount);
                sb.append(" AND ");
                constants.getClass();
                sb.append("WalletCategoriesFileName");
                sb.append(" = '");
                sb.append(FileName2);
                sb.append("'");
                if (!walletCategoriesDAL.IsWalletCategoryAlreadyExist(sb.toString())) {
                    WalletCategoriesPojo currentCategoryData = getCurrentCategoryData(context, FileName2);
                    WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo = new WalletCategoriesFileDB_Pojo();
                    walletCategoriesFileDB_Pojo.setCategoryFileName(currentCategoryData.getCategoryName());
                    walletCategoriesFileDB_Pojo.setCategoryFileCreatedDate(getCurrentDate());
                    walletCategoriesFileDB_Pojo.setCategoryFileModifiedDate(getCurrentDate());
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(StorageOptionsCommon.STORAGEPATH);
                    sb2.append(StorageOptionsCommon.WALLET);
                    sb2.append(FileName2);
                    walletCategoriesFileDB_Pojo.setCategoryFileLocation(sb2.toString());
                    walletCategoriesFileDB_Pojo.setCategoryFileIconIndex(currentCategoryData.getCategoryIconIndex());
                    walletCategoriesFileDB_Pojo.setCategoryFileSortBy(0);
                    walletCategoriesFileDB_Pojo.setCategoryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                    walletCategoriesDAL.OpenWrite();
                    walletCategoriesDAL.addWalletCategoriesInfoInDatabase(walletCategoriesFileDB_Pojo);
                    walletCategoriesDAL.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentDate() {
        return new SimpleDateFormat("EEE d MMM yyyy, HH:mm:ss aaa").format(Calendar.getInstance().getTime());
    }

    public static String FileName(String str) {
        String name = new File(str).getName();
        String str2 = "_";
        for (int length = name.length() - 1; length > 0; length--) {
            if (name.charAt(length) == str2.charAt(0)) {
                int lastIndexOf = name.lastIndexOf(".");
                if (lastIndexOf > 0) {
                    name = name.substring(length + 1, lastIndexOf);
                }
                return name;
            }
        }
        return "";
    }

    public String capitalizeFirstLetter(String str) {
        if (str.length() <= 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (String trim : str.split(" ")) {
            char[] charArray = trim.trim().toCharArray();
            charArray[0] = Character.toUpperCase(charArray[0]);
            stringBuffer.append(new String(charArray));
            stringBuffer.append(" ");
        }
        return stringBuffer.toString().trim();
    }

    public boolean isNoSpecialCharsInName(String str) {
        return Pattern.compile("^[a-zA-Z.0-9 -]+$").matcher(str).matches();
    }
}
