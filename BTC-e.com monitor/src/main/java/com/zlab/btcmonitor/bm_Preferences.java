package com.zlab.btcmonitor;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class bm_Preferences extends PreferenceActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            if(bm_Main.prefs_black_theme){
                setTheme(android.R.style.Theme_Holo);
            }
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            bm_Main.SETTINGS_IS_OPENED=true;



            final CheckBoxPreference prefs_black_theme = (CheckBoxPreference) getPreferenceManager().findPreference("prefs_black_theme");
            final CheckBoxPreference prefs_black_charts = (CheckBoxPreference) getPreferenceManager().findPreference("prefs_black_charts");

            prefs_black_theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals("true")) {
                        prefs_black_charts.setChecked(true);
                    } else {
                        prefs_black_charts.setChecked(false);
                    }
                    return true;
                }
            });
        }
}

