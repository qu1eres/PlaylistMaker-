package com.example.verstka_last.di

import android.content.Context
import com.example.verstka_last.settings.data.impl.ThemeRepositoryImpl
import com.example.verstka_last.settings.data.local.ThemePreferences
import com.example.verstka_last.settings.domain.api.ThemeInteractor
import com.example.verstka_last.settings.domain.api.ThemeRepository
import com.example.verstka_last.settings.domain.impl.ThemeInteractorImpl
import com.example.verstka_last.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingsModule = module {
    single(named("app_settings")) {
        get<Context>().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    single { ThemePreferences(get(named("app_settings"))) }
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<ThemeInteractor> { ThemeInteractorImpl(get()) }

    viewModel { SettingsViewModel(get()) }
}