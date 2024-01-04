package com.example.vault.calculator;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.vault.R;

public class CalculatorSetting extends AppCompatActivity {

    public static class SettingsFragment extends PreferenceFragment {
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.preferences);
            findPreference("pref_theme").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) SettingsFragment.this.findPreference("pref_theme");
                    String packageName = SettingsFragment.this.getActivity().getPackageName();
                    StringBuilder sb = new StringBuilder();
                    sb.append(packageName);
                    sb.append(".MainActivity-");
                    sb.append(listPreference.getEntry());
                    String sb2 = sb.toString();
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(packageName);
                    sb3.append(".MainActivity-");
                    sb3.append(listPreference.getEntries()[Integer.parseInt(obj.toString())]);
                    String sb4 = sb3.toString();
                    SettingsFragment.this.getActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(packageName, sb2), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    SettingsFragment.this.getActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(packageName, sb4), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    SettingsFragment.this.getActivity().finish();
                    TaskStackBuilder.create(SettingsFragment.this.getActivity()).addNextIntent(new Intent(SettingsFragment.this.getActivity(), MyCalculatorActivity.class)).addNextIntent(SettingsFragment.this.getActivity().getIntent()).startActivities();
                    return true;
                }
            });
            findPreference("pref_dark").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    SettingsFragment.this.getActivity().finish();
                    TaskStackBuilder.create(SettingsFragment.this.getActivity()).addNextIntent(new Intent(SettingsFragment.this.getActivity(), MyCalculatorActivity.class)).addNextIntent(SettingsFragment.this.getActivity().getIntent()).startActivities();
                    return true;
                }
            });
            findPreference("pref_rate").setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    RateDialog.show(SettingsFragment.this.getActivity());
                    return false;
                }
            });
        }
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        char c = 0;
        if (!defaultSharedPreferences.getBoolean("pref_dark", false)) {
            String string = defaultSharedPreferences.getString("pref_theme", "0");
            switch (string.hashCode()) {
                case 48:
                    break;
                case 49:
                    if (string.equals("1")) {
                        c = 1;
                        break;
                    }
                case 50:
                    if (string.equals("2")) {
                        c = 2;
                        break;
                    }
                case 51:
                    if (string.equals("3")) {
                        c = 3;
                        break;
                    }
                case 52:
                    if (string.equals("4")) {
                        c = 4;
                        break;
                    }
                case 53:
                    if (string.equals("5")) {
                        c = 5;
                        break;
                    }
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    setTheme(R.style.AppTheme_Light_Blue);
                    break;
                case 1:
                    setTheme(R.style.AppTheme_Light_Cyan);
                    break;
                case 2:
                    setTheme(R.style.AppTheme_Light_Gray);
                    break;
                case 3:
                    setTheme(R.style.AppTheme_Light_Green);
                    break;
                case 4:
                    setTheme(R.style.AppTheme_Light_Purple);
                    break;
                case 5:
                    setTheme(R.style.AppTheme_Light_Red);
                    break;
            }
        } else {
            String string2 = defaultSharedPreferences.getString("pref_theme", "0");
            switch (string2.hashCode()) {
                case 48:
                    break;
                case 49:
                    if (string2.equals("1")) {
                        c = 1;
                        break;
                    }
                case 50:
                    if (string2.equals("2")) {
                        c = 2;
                        break;
                    }
                case 51:
                    if (string2.equals("3")) {
                        c = 3;
                        break;
                    }
                case 52:
                    if (string2.equals("4")) {
                        c = 4;
                        break;
                    }
                case 53:
                    if (string2.equals("5")) {
                        c = 5;
                        break;
                    }
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    setTheme(R.style.AppTheme_Dark_Blue);
                    break;
                case 1:
                    setTheme(R.style.AppTheme_Dark_Cyan);
                    break;
                case 2:
                    setTheme(R.style.AppTheme_Dark_Gray);
                    break;
                case 3:
                    setTheme(R.style.AppTheme_Dark_Green);
                    break;
                case 4:
                    setTheme(R.style.AppTheme_Dark_Purple);
                    break;
                case 5:
                    setTheme(R.style.AppTheme_Dark_Red);
                    break;
            }
        }
        setContentView((int) R.layout.activity_calculator_setting);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(R.id.content, new SettingsFragment()).commit();
    }
}
