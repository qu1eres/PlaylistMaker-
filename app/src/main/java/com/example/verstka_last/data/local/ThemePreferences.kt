package com.example.verstka_last.data.local

import android.content.SharedPreferences
import androidx.core.content.edit

class ThemePreferences(private var sharedPreferences: SharedPreferences) {

    fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean("dark_theme", false)
    }

    fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean("dark_theme", enabled) }
    }
}