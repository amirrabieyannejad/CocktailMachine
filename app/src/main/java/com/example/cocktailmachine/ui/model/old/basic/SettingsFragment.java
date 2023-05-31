package com.example.cocktailmachine.ui.model.old.basic;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.cocktailmachine.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}