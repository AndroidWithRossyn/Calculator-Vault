package com.example.vault.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.math3.special.Gamma;

public class EquationSolver {
    private SharedPreferences sp;

    public EquationSolver(Context context) {
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String solve(String str) {
        return solveBasicOperators(solveBasicOperators(solveBasicOperators(solveAdvancedOperators(str.replace("π", "3.141592653589793").replace("e", "2.718281828459045").replace("n ", "ln ( ").replace("l ", "log ( ").replace("√ ", "√ ( ").replace("s ", "sin ( ").replace("c ", "cos ( ").replace("t ", "tan ( ")), " % ", " % "), " * ", " / "), " + ", " - ");
    }

    private String solveAdvancedOperators(String str) {
        int i;
        String str2 = str;
        while (numOfOccurrences('(', str2) > numOfOccurrences(')', str2)) {
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            sb.append(") ");
            str2 = sb.toString();
        }
        while (str2.contains("(")) {
            int indexOf = str2.indexOf(40);
            int i2 = indexOf;
            int i3 = 0;
            while (true) {
                if (i2 >= str2.length()) {
                    i2 = 0;
                    break;
                }
                if (str2.charAt(i2) == '(') {
                    i3++;
                }
                if (str2.charAt(i2) == ')') {
                    i3--;
                }
                if (i3 == 0) {
                    break;
                }
                i2++;
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str2.substring(0, indexOf));
            sb2.append(solveAdvancedOperators(str2.substring(indexOf + 2, i2)));
            sb2.append(" ");
            sb2.append(str2.substring(i2 + 2));
            str2 = sb2.toString();
        }
        while (true) {
            i = 32;
            if (!str2.contains("ln")) {
                break;
            }
            int indexOf2 = str2.indexOf("ln");
            int i4 = indexOf2 + 3;
            int indexOf3 = str2.indexOf(32, i4);
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str2.substring(0, indexOf2));
            sb3.append(Math.log(Double.parseDouble(solveAdvancedOperators(str2.substring(i4, indexOf3)))));
            sb3.append(str2.substring(indexOf3));
            str2 = sb3.toString();
        }
        while (str2.contains("log")) {
            int indexOf4 = str2.indexOf("log");
            int i5 = indexOf4 + 4;
            int indexOf5 = str2.indexOf(32, i5);
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str2.substring(0, indexOf4));
            sb4.append(Math.log10(Double.parseDouble(solveAdvancedOperators(str2.substring(i5, indexOf5)))));
            sb4.append(str2.substring(indexOf5));
            str2 = sb4.toString();
        }
        while (str2.contains("sin")) {
            int indexOf6 = str2.indexOf("sin");
            int i6 = indexOf6 + 4;
            int indexOf7 = str2.indexOf(i, i6);
            double parseDouble = Double.parseDouble(solveAdvancedOperators(str2.substring(i6, indexOf7)));
            if (!this.sp.getBoolean("pref_radians", false)) {
                parseDouble *= 0.017453292519943295d;
            }
            double sin = Math.sin(parseDouble);
            if (Math.abs(sin) < 1.0E-11d) {
                sin = 0.0d;
            }
            if (Math.abs(sin) > 1.0E10d) {
                sin /= 0.0d;
            }
            StringBuilder sb5 = new StringBuilder();
            sb5.append(str2.substring(0, indexOf6));
            sb5.append(sin);
            sb5.append(str2.substring(indexOf7));
            str2 = sb5.toString();
            i = 32;
        }
        while (str2.contains("cos")) {
            int indexOf8 = str2.indexOf("cos");
            int i7 = indexOf8 + 4;
            int indexOf9 = str2.indexOf(32, i7);
            double parseDouble2 = Double.parseDouble(solveAdvancedOperators(str2.substring(i7, indexOf9)));
            if (!this.sp.getBoolean("pref_radians", false)) {
                parseDouble2 *= 0.017453292519943295d;
            }
            double cos = Math.cos(parseDouble2);
            if (Math.abs(cos) < 1.0E-11d) {
                cos = 0.0d;
            }
            if (Math.abs(cos) > 1.0E10d) {
                cos /= 0.0d;
            }
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str2.substring(0, indexOf8));
            sb6.append(cos);
            sb6.append(str2.substring(indexOf9));
            str2 = sb6.toString();
        }
        while (str2.contains("tan")) {
            int indexOf10 = str2.indexOf("tan");
            int i8 = indexOf10 + 4;
            int indexOf11 = str2.indexOf(32, i8);
            double parseDouble3 = Double.parseDouble(solveAdvancedOperators(str2.substring(i8, indexOf11)));
            if (!this.sp.getBoolean("pref_radians", false)) {
                parseDouble3 *= 0.017453292519943295d;
            }
            double tan = Math.tan(parseDouble3);
            if (Math.abs(tan) < 1.0E-11d) {
                tan = 0.0d;
            }
            if (Math.abs(tan) > 1.0E10d) {
                tan /= 0.0d;
            }
            StringBuilder sb7 = new StringBuilder();
            sb7.append(str2.substring(0, indexOf10));
            sb7.append(tan);
            sb7.append(str2.substring(indexOf11));
            str2 = sb7.toString();
        }
        while (str2.contains("√")) {
            int indexOf12 = str2.indexOf(8730);
            int i9 = indexOf12 + 2;
            int indexOf13 = str2.indexOf(32, i9);
            StringBuilder sb8 = new StringBuilder();
            sb8.append(str2.substring(0, indexOf12));
            sb8.append(Math.sqrt(Double.parseDouble(solveAdvancedOperators(str2.substring(i9, indexOf13)))));
            sb8.append(str2.substring(indexOf13));
            str2 = sb8.toString();
        }
        while (str2.contains("!")) {
            StringBuilder sb9 = new StringBuilder();
            sb9.append(" ");
            sb9.append(str2);
            sb9.append(" ");
            String sb10 = sb9.toString();
            String str3 = "";
            String substring = sb10.substring(sb10.lastIndexOf(" ", sb10.indexOf("!") - 2) + 1, sb10.indexOf("!") - 1);
            String str4 = "";
            try {
                str3 = sb10.substring(1, sb10.lastIndexOf(" ", sb10.indexOf("!") - 2));
            } catch (Exception unused) {
            }
            StringBuilder sb11 = new StringBuilder();
            sb11.append("");
            sb11.append(Gamma.gamma(Double.parseDouble(substring) + 1.0d));
            String sb12 = sb11.toString();
            try {
                str4 = sb10.substring(sb10.indexOf("!") + 2);
            } catch (Exception unused2) {
            }
            StringBuilder sb13 = new StringBuilder();
            sb13.append(str3);
            sb13.append(" ");
            sb13.append(sb12);
            sb13.append(" ");
            sb13.append(str4);
            str2 = sb13.toString();
        }
        while (str2.contains("%")) {
            StringBuilder sb14 = new StringBuilder();
            sb14.append(" ");
            sb14.append(str2);
            sb14.append(" ");
            String sb15 = sb14.toString();
            String substring2 = sb15.substring(sb15.lastIndexOf(" ", sb15.indexOf("%") - 2) + 1, sb15.indexOf("%") - 1);
            try {
                sb15.substring(1, sb15.lastIndexOf(" ", sb15.indexOf("%") / 100));
            } catch (Exception unused3) {
            }
            double parseDouble4 = Double.parseDouble(substring2) / 100.0d;
            StringBuilder sb16 = new StringBuilder();
            sb16.append(parseDouble4);
            sb16.append("");
            Log.e("percentage", sb16.toString());
            try {
                sb15.substring(sb15.indexOf("%") + 2);
            } catch (Exception unused4) {
            }
            str2 = Double.toString(parseDouble4);
        }
        return str2;
    }


    private String solveBasicOperators(String str, String str2, String str3) {
        char c;
        String str4 = str2;
        String str5 = str3;
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append(str);
        sb.append(" ");
        String sb2 = sb.toString();
        while (true) {
            if (!sb2.contains(str4) && !sb2.contains(str5)) {
                return sb2.trim();
            }
            String str6 = sb2.indexOf(str5) < sb2.indexOf(str4) ? str5 : str4;
            if (!sb2.contains(str4)) {
                str6 = str5;
            }
            if (!sb2.contains(str5)) {
                str6 = str4;
            }
            String substring = sb2.substring(0, sb2.indexOf(str6));
            String substring2 = sb2.substring(sb2.indexOf(str6) + 3);
            double parseDouble = Double.parseDouble(substring.substring(substring.lastIndexOf(32) + 1));
            double parseDouble2 = Double.parseDouble(substring2.substring(0, substring2.indexOf(32)));
            String trim = substring.substring(0, substring.lastIndexOf(32)).trim();
            String trim2 = substring2.substring(substring2.indexOf(32), substring2.length()).trim();
            double d = 0.0d;
            int hashCode = str6.hashCode();
            if (hashCode != 31931) {
                if (hashCode != 32086) {
                    if (hashCode != 32117) {
                        if (hashCode != 32179) {
                            if (hashCode == 32241 && str6.equals(" / ")) {
                                c = 0;
                                switch (c) {
                                    case 0:

                                        d = parseDouble / parseDouble2;

                                        StringBuilder sb3 = new StringBuilder();
                                        sb3.append(" ");
                                        sb3.append(trim);
                                        sb3.append(" ");
                                        sb3.append(d);
                                        sb3.append(" ");
                                        sb3.append(trim2);
                                        sb3.append(" ");
                                        sb2 = sb3.toString();
                                        break;
                                    case 1:

                                        break;
                                    case 2:

                                        break;
                                    case 3:

                                        break;
                                    case 4:

                                        break;
                                }

                            }
                        } else if (str6.equals(" - ")) {
                            c = 2;
                            switch (c) {
                                case 0:
                                    break;
                                case 1:
                                    break;
                                case 2:
                                    d = parseDouble - parseDouble2;
                                    StringBuilder sb32 = new StringBuilder();
                                    sb32.append(" ");
                                    sb32.append(trim);
                                    sb32.append(" ");
                                    sb32.append(d);
                                    sb32.append(" ");
                                    sb32.append(trim2);
                                    sb32.append(" ");
                                    sb2 = sb32.toString();
                                    break;
                                case 3:
                                    break;
                                case 4:
                                    break;
                            }

                        }
                    } else if (str6.equals(" + ")) {
                        c = 3;
                        switch (c) {
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:

                                d = parseDouble + parseDouble2;
                                StringBuilder sb322 = new StringBuilder();
                                sb322.append(" ");
                                sb322.append(trim);
                                sb322.append(" ");
                                sb322.append(d);
                                sb322.append(" ");
                                sb322.append(trim2);
                                sb322.append(" ");
                                sb2 = sb322.toString();
                                break;
                            case 4:
                                break;
                        }

                    }
                } else if (str6.equals(" * ")) {
                    c = 1;
                    switch (c) {
                        case 0:
                            break;
                        case 1:

                            d = parseDouble * parseDouble2;

                            StringBuilder sb3222 = new StringBuilder();
                            sb3222.append(" ");
                            sb3222.append(trim);
                            sb3222.append(" ");
                            sb3222.append(d);
                            sb3222.append(" ");
                            sb3222.append(trim2);
                            sb3222.append(" ");
                            sb2 = sb3222.toString();
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                    }

                }
            } else if (str6.equals(" % ")) {
                c = 4;
                switch (c) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        d = parseDouble / 100.0d;
                        StringBuilder sb32222 = new StringBuilder();
                        sb32222.append(" ");
                        sb32222.append(trim);
                        sb32222.append(" ");
                        sb32222.append(d);
                        sb32222.append(" ");
                        sb32222.append(trim2);
                        sb32222.append(" ");
                        sb2 = sb32222.toString();
                        break;
                }

            } else {

                StringBuilder sb322222 = new StringBuilder();
                sb322222.append(" ");
                sb322222.append(trim);
                sb322222.append(" ");
                sb322222.append(d);
                sb322222.append(" ");
                sb322222.append(trim2);
                sb322222.append(" ");
                sb2 = sb322222.toString();
            }


        }
    }

    public String formatNumber(String str) {
        if (str.contains("∞") || str.contains("Infinity") || str.contains("NaN")) {
            return str;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.########E0");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        String format = decimalFormat.format(Double.parseDouble(str));
        if (Math.abs(Double.parseDouble(format.substring(format.indexOf("E") + 1))) < 8.0d) {
            decimalFormat.applyPattern("#.########");
            format = decimalFormat.format(Double.parseDouble(str));
        }
        return format;
    }

    private int numOfOccurrences(char c, String str) {
        int i = 0;
        for (int i2 = 0; i2 < str.length(); i2++) {
            if (str.charAt(i2) == c) {
                i++;
            }
        }
        return i;
    }
}
