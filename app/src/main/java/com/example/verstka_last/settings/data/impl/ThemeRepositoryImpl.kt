package com.example.verstka_last.settings.data.impl

import com.example.verstka_last.settings.data.local.ThemePreferences
import com.example.verstka_last.settings.domain.api.ThemeRepository

class ThemeRepositoryImpl(private val themePreferences: ThemePreferences) : ThemeRepository {

    override fun setDarkThemeEnabled(enabled: Boolean) {
        themePreferences.setDarkThemeEnabled(enabled)
    }

    override fun isDarkThemeEnabled(): Boolean {
        return themePreferences.isDarkThemeEnabled()
    }
}