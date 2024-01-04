package com.example.vault.panicswitch;

public class PanicSwitchCommon {
    public static boolean IsFaceDownOn = false;
    public static boolean IsFlickOn = false;
    public static boolean IsPalmOnFaceOn = false;
    public static boolean IsShakeOn = false;
    public static String SwitchingApp = "";

    public enum SwitchApp {
        Browser,
        HomeScreen
    }
}
