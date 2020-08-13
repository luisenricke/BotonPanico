package com.luisenricke.botonpanico.presentation

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.luisenricke.botonpanico.R

class SettingsPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}
