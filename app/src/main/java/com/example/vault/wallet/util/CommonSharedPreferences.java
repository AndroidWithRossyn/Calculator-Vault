package com.example.vault.wallet.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CommonSharedPreferences {
    private static String _ViewByWalletCategory = "ViewByWalletCategory";
    private static String _fileName = "CommonSharedPreferences";
    private static String _sortByNotesFile = "SortByNotesFile";
    private static String _sortByNotesFolder = "SortByNotesFolder";
    private static String _sortByToDoFile = "SortByToDoFile";
    private static String _sortByWalletCategory = "SortByWalletCategory";
    private static String _sortByWalletEntry = "SortByWalletEntry";
    private static String _viewByNotesFiles = "ViewByNotesFiles";
    private static String _viewByNotesFolder = "ViewByNotesFolder";
    private static String _viewByToDoFile = "ViewByToDoFile";
    private static String _viewByWalletEntry = "ViewByWalletEntry";
    static Context context;
    static SharedPreferences myPrefs;
    private static CommonSharedPreferences walletSharedPreferences;

    private CommonSharedPreferences() {
    }

    public static CommonSharedPreferences GetObject(Context context2) {
        if (walletSharedPreferences == null) {
            walletSharedPreferences = new CommonSharedPreferences();
        }
        context = context2;
        myPrefs = context.getSharedPreferences(_fileName, 0);
        return walletSharedPreferences;
    }

    public int get_sortByWalletCategory() {
        return myPrefs.getInt(_sortByWalletCategory, 0);
    }

    public void set_sortByWalletCategory(int i) {
        Editor edit = myPrefs.edit();
        edit.putInt(_sortByWalletCategory, i);
        edit.commit();
    }

    public int get_ViewByWalletCategory() {
        return myPrefs.getInt(_ViewByWalletCategory, 0);
    }

    public void set_ViewByWalletCategory(int i) {
        Editor edit = myPrefs.edit();
        edit.putInt(_ViewByWalletCategory, i);
        edit.commit();
    }

    public int get_sortByWalletEntry() {
        return myPrefs.getInt(_sortByWalletEntry, 0);
    }

    public void set_sortByWalletEntry(int i) {
        Editor edit = myPrefs.edit();
        edit.putInt(_sortByWalletEntry, i);
        edit.commit();
    }

    public int get_ViewByWalletEntry() {
        return myPrefs.getInt(_viewByWalletEntry, 0);
    }

    public void set_viewByWalletEntry(int i) {
        Editor edit = myPrefs.edit();
        edit.putInt(_viewByWalletEntry, i);
        edit.commit();
    }

    public int get_sortByNotesFolder() {
        return myPrefs.getInt(_sortByNotesFolder, 1);
    }

    public void set_sortByNotesFolder(int i) {
        Editor edit = myPrefs.edit();
        edit.putInt(_sortByNotesFolder, i);
        edit.commit();
    }

    public int get_sortByNotesFile() {
        return myPrefs.getInt(_sortByNotesFile, 0);
    }

    public void set_sortByNotesFile(int i) {
        Editor edit = myPrefs.edit();
        edit.putInt(_sortByNotesFile, i);
        edit.commit();
    }

    public int get_sortByToDoFile() {
        return myPrefs.getInt(_sortByToDoFile, 2);
    }

    public void set_sortByToDoFile(int i) {
        Editor edit = myPrefs.edit();
        edit.putInt(_sortByToDoFile, i);
        edit.commit();
    }

    public int get_viewByToDoFile() {
        return myPrefs.getInt(_viewByToDoFile, 0);
    }

    public void set_viewByToDoFile(int i) {
        Editor edit = myPrefs.edit();
        edit.putInt(_viewByToDoFile, i);
        edit.commit();
    }

    public int get_viewByNotesFolder() {
        return myPrefs.getInt(_viewByNotesFolder, 2);
    }

    public void set_viewByNotesFolder(int i) {
        Editor edit = myPrefs.edit();
        edit.putInt(_viewByNotesFolder, i);
        edit.commit();
    }

    public int get_viewByNotesFile() {
        return myPrefs.getInt(_viewByNotesFiles, 0);
    }

    public void set_viewByNotesFiles(int i) {
        Editor edit = myPrefs.edit();
        edit.putInt(_viewByNotesFiles, i);
        edit.commit();
    }
}
