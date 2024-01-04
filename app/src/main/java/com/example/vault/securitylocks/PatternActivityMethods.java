package com.example.vault.securitylocks;

import android.content.Context;
import android.graphics.Point;
import android.preference.PreferenceManager;
import java.util.List;

public class PatternActivityMethods {
    public static void clearAndBail(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().putBoolean("exited_hard", true).commit();
        System.exit(-1);
    }

    public static String ConvertPatternToNo(List<Point> list) {
        StringBuilder sb = new StringBuilder();
        for (Point ConvertPattern : list) {
            sb.append(ConvertPattern(ConvertPattern));
        }
        return sb.toString();
    }

    public static String ConvertPattern(Point point) {
        if (point.x == 0) {
            if (point.y == 0) {
                return "1";
            }
            if (point.y == 1) {
                return "4";
            }
            if (point.y == 2) {
                return "7";
            }
        }
        if (point.x == 1) {
            if (point.y == 0) {
                return "2";
            }
            if (point.y == 1) {
                return "5";
            }
            if (point.y == 2) {
                return "8";
            }
        }
        if (point.x == 2) {
            if (point.y == 0) {
                return "3";
            }
            if (point.y == 1) {
                return "6";
            }
            if (point.y == 2) {
                return "9";
            }
        }
        return "";
    }
}
