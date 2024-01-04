package com.example.vault.calculator;

import java.util.ArrayList;
import java.util.Iterator;

public class Equation extends ArrayList<String> {
    public String getText() {
        String str = "";
        Iterator it = iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(str2);
            sb.append(" ");
            str = sb.toString();
        }
        return str;
    }

    public void setText(String str) {
        while (size() > 0) {
            removeLast();
        }
        if (str.length() > 0) {
            for (String add : str.split(" ")) {
                add(add);
            }
        }
    }

    public void attachToLast(char c) {
        if (size() == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(c);
            add(sb.toString());
            return;
        }
        int size = size() - 1;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(getLast());
        sb2.append(c);
        set(size, sb2.toString());
    }

    public void detachFromLast() {
        if (getLast().length() > 0) {
            set(size() - 1, getLast().substring(0, getLast().length() - 1));
        }
    }

    public void removeLast() {
        if (size() > 0) {
            remove(size() - 1);
        }
    }

    public String getLast() {
        return getRecent(0);
    }

    public char getLastChar() {
        String last = getLast();
        if (last.length() > 0) {
            return last.charAt(last.length() - 1);
        }
        return ' ';
    }

    public String getRecent(int i) {
        if (size() <= i) {
            return "";
        }
        return (String) get((size() - i) - 1);
    }

    public boolean isNumber(int i) {
        String recent = getRecent(i);
        if (recent != null && recent.length() > 0) {
            char charAt = recent.charAt(0);
            if (isRawNumber(i) || charAt == 960 || charAt == 'e' || charAt == ')' || charAt == '!') {
                return true;
            }
        }
        return false;
    }

    public boolean isOperator(int i) {
        String recent = getRecent(i);
        if (recent != null && recent.length() == 1) {
            char charAt = recent.charAt(0);
            if (charAt == '/' || charAt == '*' || charAt == '-' || charAt == '+' || charAt == '%') {
                return true;
            }
        }
        return false;
    }

    public boolean isRawNumber(int i) {
        String recent = getRecent(i);
        if (recent == null || recent.length() <= 0 || (!Character.isDigit(recent.charAt(0)) && (recent.charAt(0) != '-' || !isStartCharacter(i + 1) || (recent.length() != 1 && !Character.isDigit(recent.charAt(1)))))) {
            return false;
        }
        return true;
    }

    public boolean isStartCharacter(int i) {
        String recent = getRecent(i);
        if (recent != null && recent.length() > 0) {
            char charAt = recent.charAt(0);
            if (recent.length() > 1 && charAt == '-') {
                charAt = recent.charAt(1);
            }
            if (charAt == 8730 || charAt == 's' || charAt == 'c' || charAt == 't' || charAt == 'n' || charAt == 'l' || charAt == '(' || charAt == '/' || charAt == '*' || charAt == '-' || charAt == '+' || charAt == '%') {
                return true;
            }
        }
        return recent.equals("");
    }

    public int numOf(char c) {
        int i = 0;
        for (int i2 = 0; i2 < getText().length(); i2++) {
            if (getText().charAt(i2) == c) {
                i++;
            }
        }
        return i;
    }
}
