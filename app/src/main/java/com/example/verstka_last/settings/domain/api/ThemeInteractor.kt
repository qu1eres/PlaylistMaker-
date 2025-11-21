package com.example.verstka_last.settings.domain.api

import android.content.Context

interface ThemeInteractor {
    fun toggleTheme()
    fun isDarkTheme(): Boolean
    fun applyTheme(context: Context)
}