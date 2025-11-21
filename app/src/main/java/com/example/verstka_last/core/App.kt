package com.example.verstka_last.core

import android.app.Application
import com.example.verstka_last.creator.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val themeInteractor = Creator.provideThemeInteractor(this)
        themeInteractor.applyTheme(this)
    }
}