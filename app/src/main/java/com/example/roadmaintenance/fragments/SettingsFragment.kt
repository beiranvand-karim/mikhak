package com.example.roadmaintenance.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.roadmaintenance.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        // TODO:  set it up
    }

}