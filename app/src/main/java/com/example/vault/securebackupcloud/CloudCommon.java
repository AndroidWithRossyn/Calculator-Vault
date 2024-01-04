package com.example.vault.securebackupcloud;

public class CloudCommon {
    public static String AudioFolder = "/NSVault/Audio";
    public static int CloudType = 0;
    public static String DocumentFolder = "/NSVault/Documents";
    public static boolean IsCameFromCloudMenu = false;
    public static boolean IsCameFromSettings = false;
    public static boolean IsCloudServiceStarted = false;
    public static int ModuleType = 0;
    public static String NotesFolder = "/NSVault/Notes";
    public static String PhotoFolder = "/NSVault/Photos";
    public static String ToDoListFolder = "/NSVault/ToDoLists";
    public static String VideoFolder = "/NSVault/Videos";
    public static String WalletFolder = "/NSVault/Wallet";

    public enum CloudFolderStatus {
        OnlyCloud,
        OnlyPhone,
        CloudAndPhoneCompleteSync,
        CloudAndPhoneNotSync
    }

    public enum CloudType1 {
        DropBox,
        GoogleDrive
    }

    public enum DropboxType {
        Photos,
        Videos,
        Documents,
        Notes,
        Audio,
        Wallet,
        ToDo
    }
}
