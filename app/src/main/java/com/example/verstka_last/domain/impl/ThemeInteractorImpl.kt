package com.example.verstka_last.domain.impl

import com.example.verstka_last.data.local.ThemePreferences
import com.example.verstka_last.domain.api.ThemeInteractor

class ThemeInteractorImpl(private val themePreferences: ThemePreferences) : ThemeInteractor {

    override fun toggleTheme() {
        val current = themePreferences.isDarkThemeEnabled()
        themePreferences.setDarkThemeEnabled(!current)
    }

    override fun isDarkTheme(): Boolean {
        return themePreferences.isDarkThemeEnabled()
    }
}