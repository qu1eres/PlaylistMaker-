package com.example.verstka_last.settings.domain.api

interface ThemeRepository {
    fun setDarkThemeEnabled(enabled: Boolean)
    fun isDarkThemeEnabled(): Boolean
}