package com.example.verstka_last.presentation

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.verstka_last.data.local.ThemePreferences
import com.example.verstka_last.domain.api.ThemeInteractor
import com.example.verstka_last.domain.impl.ThemeInteractorImpl

class App : Application() {

    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate() {
        super.onCreate()

        val themePrefs = ThemePreferences(
            getSharedPreferences("app_settings", MODE_PRIVATE)
        )
        themeInteractor = ThemeInteractorImpl(themePrefs)

        applyTheme(themeInteractor.isDarkTheme())
    }

    fun getThemeInteractor(): ThemeInteractor = themeInteractor

    fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}