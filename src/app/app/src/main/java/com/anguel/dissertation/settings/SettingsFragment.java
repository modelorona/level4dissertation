package com.anguel.dissertation.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.anguel.dissertation.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

    }
}
