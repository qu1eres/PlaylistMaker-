package com.example.verstka_last.domain.api

interface ThemeRepository {
    fun setDarkThemeEnabled(enabled: Boolean)
    fun isDarkThemeEnabled(): Boolean
}