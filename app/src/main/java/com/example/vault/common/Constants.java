package com.example.vault.common;

import android.app.Activity;

public class Constants {
    public static Activity CurrentWebBrowserActivity = null;
    public static Activity CurrentWebServerActivity = null;
    public static final String FILE = "file://";
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static boolean IsWebBrowserActive = false;

    public static final String TAG = "Calculator Vault";
    public static String currentUrl = "";
    public static int currentUrlIndex;
    public final String NotesFileColor = "NotesFileColor";
    public final String NotesFileCreatedDate = "NotesFileCreatedDate";
    public final String NotesFileFromCloud = "NotesFileFromCloud";
    public final String NotesFileId = "NotesFileId";
    public final String NotesFileIsDecoy = "NotesFileIsDecoy";
    public final String NotesFileLocation = "NotesFileLocation";
    public final String NotesFileModifiedDate = "NotesFileModifiedDate";
    public final String NotesFileName = "NotesFileName";
    public final String NotesFileSize = "NotesFileSize";
    public final String NotesFileTable = "TableNotesFile";
    public final String NotesFileText = "NotesFileText";
    public final String NotesFolderColor = "NotesFolderColor";
    public final String NotesFolderCreatedDate = "NotesFolderCreatedDate";
    public final String NotesFolderId = "NotesFolderId";
    public final String NotesFolderIsDecoy = "NotesFolderIsDecoy";
    public final String NotesFolderLocation = "NotesFolderLocation";
    public final String NotesFolderModifiedDate = "NotesFolderModifiedDate";
    public final String NotesFolderName = "NotesFolderName";
    public final String NotesFolderSortBy = "NotesFolderSortBy";
    public final String NotesFolderTable = "TableNotesFolder";
    public final String NotesFolderViewBy = "NotesFolderViewBy";
    public final String QueryNotesFile_S_ALL = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile ORDER BY ";
    public final String QueryNotesFile_S_ALL_W = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ";
    public final String QueryNotesFile_S_fileSize_W = "SELECT SUM (NotesFileSize) FROM  TableNotesFile WHERE ";
    public final String QueryNotesFiles_S_CountAll_W = "SELECT \t\tCOUNT(*) \t\t\t\t   FROM TableNotesFile WHERE ";
    public final String QueryNotesFolder_S_ALL_W = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ";
    public final String QueryToDo_S_ALL_W = "SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ";
    public final String QueryToDo_S_CountALL = "SELECT \t    COUNT(*)  \t\t\t\t\t\t   FROM  TableToDo";
    public final String QueryWalletCategoriesFile_S_ALL_W = "SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ";
    public final String QueryWalletCategoriesFile_S_CategoriesCount = "SELECT \t COUNT(*)\t\t\t\t\t   FROM TableWalletCategories";
    public final String QueryWalletEntries_S_ALL_W = "SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ";
    public final String QueryWalletEntries_S_EntriesCount_W = "SELECT \t COUNT(*)\t\t\t\t\t   FROM TableWalletEntries WHERE ";
    public final String QueryWalletEntries_S_EntryFileId_W = "SELECT \t\tWalletEntryFileId FROM  TableWalletEntries WHERE ";
    public final String TABLE_NOTES_FILE = "CREATE TABLE [TableNotesFile]( [NotesFileId] \t\t\tINTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT ,  [NotesFolderId] \t\t\tINTEGER   NOT NULL                           ,  [NotesFileName] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t     ,  [NotesFileLocation] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t\t ,  [NotesFileCreatedDate] \t\t\tTIMESTAMP NOT NULL \t\t\t\t\t\t\t ,  [NotesFileModifiedDate] \t\t\tTIMESTAMP \t  NULL\t\t\t\t\t\t\t ,  [NotesFileFromCloud] \t\t\tINTEGER\t  NOT NULL\t\t\t\t\t\t\t ,  [NotesFileSize] \t\t\tREAL \t      NULL\t\t\t\t\t\t     ,  [NotesFileText] \t\t\tTEXT \t      NULL\t\t\t\t\t\t     ,  [NotesFileIsDecoy] \t\t\tINTEGER\t\t  NULL\t\t\t\t\t\t\t ,  [NotesFileColor] \t\t\tTEXT \t      NULL\t\t\t\t\t\t     ,  FOREIGN KEY(NotesFolderId) \t\t\tREFERENCES TableNotesFolder(NotesFolderId))";
    public final String TABLE_NOTES_FOLDER = "CREATE TABLE [TableNotesFolder]( [NotesFolderId] \t\t\tINTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT ,  [NotesFolderName] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t     ,  [NotesFolderLocation] \t\t\tTEXT \t  NOT NULL \t\t\t\t\t\t\t ,  [NotesFolderCreatedDate] \t\t\tTIMESTAMP NOT NULL  \t\t\t\t\t\t ,  [NotesFolderModifiedDate] \t\t\tTIMESTAMP \t  NULL\t\t\t\t\t\t\t ,  [NotesFolderSortBy] \t\t\tINTEGER \t  NULL  \t\t\t\t\t\t ,  [NotesFolderIsDecoy] \t\t\tINTEGER\t\t  NULL\t\t\t\t\t\t\t ,  [NotesFolderViewBy] \t\t\tINTEGER \t  NULL  \t\t\t\t\t\t ,  [NotesFolderColor] \t\t\tTEXT \t\t  NULL  \t\t\t\t\t\t )";
    public final String TABLE_TODO = "CREATE TABLE [TableToDo]( [ToDoId] \t\t\tINTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT ,  [ToDoName] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t     ,  [ToDoFileLocation] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t\t ,  [ToDoTask1] \t\t\tTEXT\t  \t  NULL\t\t\t\t\t\t\t ,  [ToDoTask1IsChecked] \t\t\tBOOLEAN \t  NULL \t\t\t\t\t\t\t ,  [ToDoTask2] \t\t\tTEXT \t  \t  NULL \t\t\t\t\t\t\t ,  [ToDoTask2IsChecked] \t\t\tBOOLEAN\t\t  NULL\t\t\t\t\t\t\t ,  [ToDoCreatedDate] \t\t\tTIMESTAMP NOT NULL \t\t\t\t\t\t\t ,  [ToDoModifiedDate] \t\t\tTIMESTAMP \t  NULL\t\t\t\t\t\t\t ,  [ToDoColor] \t\t\tTEXT \t  NOT NULL \t\t\t\t\t\t\t ,  [ToDoIsDecoy] \t\t\tINTEGER\t\t  NULL\t\t\t\t\t\t\t ,  [ToDoFinished] \t\t\tBOOLEAN \t  NULL \t\t\t\t     \t \t)";
    public final String TABLE_WALLET_CATEGORIES = "CREATE TABLE [TableWalletCategories]( [WalletCategoriesFileId] \t\t\tINTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT ,  [WalletCategoriesFileName] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t     ,  [WalletCategoriesFileLocation] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t\t ,  [WalletCategoriesFileCreatedDate] \t\t\tTIMESTAMP NOT NULL  \t\t\t\t\t\t ,  [WalletCategoriesFileModifiedDate] \t\t\tTIMESTAMP \t  NULL\t\t\t\t\t\t\t ,  [WalletCategoriesFileIconIndex] \t\t\tINTEGER  NOT  NULL  \t\t\t\t\t\t ,  [WalletCategoriesFileSortBy] \t\t\tINTEGER \t  NULL  \t\t\t\t\t\t ,  [WalletCategoriesFileIsDecoy] \t\t\tINTEGER\t\t  NULL\t\t\t\t\t\t\t )";
    public final String TABLE_WALLET_ENTRIES = "CREATE TABLE [TableWalletEntries]( [WalletEntryFileId] \t\t\tINTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT ,  [WalletCategoriesFileId] \t\t\tINTEGER   NOT NULL\t\t\t\t\t\t\t ,  [WalletEntryFileName] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t     ,  [WalletEntryFileLocation] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t\t ,  [WalletEntryFileCreatedDate] \t\t\tTIMESTAMP NOT NULL  \t\t\t\t\t\t ,  [WalletEntryFileModifiedDate] \t\t\tTIMESTAMP \t  NULL\t\t\t\t\t\t\t ,  [WalletCategoriesFileIconIndex] \t\t\tINTEGER  NOT  NULL  \t\t\t\t\t\t ,  [WalletEntryFilesSortBy] \t\t\tINTEGER \t  NULL  \t\t\t\t\t\t ,  [WalletEntryFileIsDecoy] \t\t\tINTEGER \t  NULL  \t\t\t\t\t\t ,  FOREIGN KEY(WalletCategoriesFileId) \t\t\tREFERENCES TableWalletCategories(WalletCategoriesFileId))";
    public final String TableToDo = "TableToDo";

    public final String TableWalletCategories = "TableWalletCategories";

    public final String WalletCategoriesFileIsDecoy = "WalletCategoriesFileIsDecoy";

}
