    package com.example.verstka_last.core

    import android.app.Application
    import androidx.appcompat.app.AppCompatDelegate
    import com.example.verstka_last.di.coreModule
    import com.example.verstka_last.di.dataModule
    import com.example.verstka_last.di.interactorModule
    import com.example.verstka_last.di.mediaLibraryModule
    import com.example.verstka_last.di.playerModule
    import com.example.verstka_last.di.repositoryModule
    import com.example.verstka_last.di.searchModule
    import com.example.verstka_last.di.settingsModule
    import com.example.verstka_last.di.sharingModule
    import com.example.verstka_last.settings.domain.api.ThemeInteractor
    import org.koin.android.ext.android.inject
    import org.koin.android.ext.koin.androidContext
    import org.koin.core.context.startKoin
    import kotlin.getValue

    class App : Application() {

        private var currentTheme: Boolean? = null

        override fun onCreate() {
            super.onCreate()

            startKoin {
                androidContext(this@App)
                modules(coreModule,
                    dataModule,
                    settingsModule,
                    sharingModule,
                    searchModule,
                    playerModule,
                    mediaLibraryModule,
                    repositoryModule,
                    interactorModule
                ) }
                applyTheme()
        }

        private fun applyTheme() {
            val themeInteractor: ThemeInteractor by inject()
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