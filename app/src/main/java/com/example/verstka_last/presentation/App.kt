package com.example.verstka_last.presentation

import android.app.Application
import com.example.verstka_last.domain.Creator


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val themeInteractor = Creator.provideThemeInteractor(this)
        themeInteractor.applyTheme(this)
    }
}