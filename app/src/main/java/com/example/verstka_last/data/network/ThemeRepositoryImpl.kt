package com.example.verstka_last.data.network

import com.example.verstka_last.data.local.ThemePreferences
import com.example.verstka_last.domain.api.ThemeRepository

class ThemeRepositoryImpl(private val themePreferences: ThemePreferences) : ThemeRepository {

    override fun setDarkThemeEnabled(enabled: Boolean) {
        themePreferences.setDarkThemeEnabled(enabled)
    }

    override fun isDarkThemeEnabled(): Boolean {
        return themePreferences.isDarkThemeEnabled()
    }
}