package com.example.vault.common;

import android.widget.EditText;

import java.util.regex.Pattern;

public class ValidationCommon {
    public EditText usernameValidation(String str, EditText editText) {
        if (str.length() < 6 || str.length() > 20) {
            editText.setError("Limit is 6 to 20 characters");
        } else if (str.contains(" ")) {
            editText.setError("Space is not allowed");
        } else if (!isNoSpecialChars(str)) {
            editText.setError("Only _ and -  special characters are allowed");
        } else {
            editText.setError(null);
        }
        return editText;
    }

    public static boolean isNoSpecialChars(String str) {
        return Pattern.compile("^[a-zA-Z.0-9_-]+$").matcher(str).matches();
    }

    public static boolean isNoSpecialCharsInName(String str) {
        return Pattern.compile("^[a-zA-Z.0-9 -]+$").matcher(str).matches();
    }

    public static boolean isNoSpecialCharsInNameExceptBrackets(String str) {
        return Pattern.compile("^[a-zA-Z.0-9() -]+$").matcher(str).matches();
    }

    public boolean isValidUsername(String str) {
        return Pattern.compile("^[a-zA-Z.0-9_-]{6,20}$").matcher(str).matches();
    }

    public boolean isValidPassword(String str) {
        if (str == null || str.length() < 6) {
            return false;
        }
        return Pattern.compile("^[_A-Za-z0-9]+([_.A-Za-z0-9]+)*(-)*$").matcher(str).matches();
    }

    public boolean isValidEmail(String str) {
        return Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").matcher(str).matches();
    }
}
