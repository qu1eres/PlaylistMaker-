package com.example.verstka_last.core

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.verstka_last.creator.Creator

class App : Application() {

    private var currentTheme: Boolean? = null

    override fun onCreate() {
        super.onCreate()
        applyTheme()
    }

    private fun applyTheme() {
        val themeInteractor = Creator.provideThemeInteractor(this)
        val isDarkTheme = themeInteractor.isDarkTheme()

        if (currentTheme != isDarkTheme) {
            currentTheme = isDarkTheme
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}