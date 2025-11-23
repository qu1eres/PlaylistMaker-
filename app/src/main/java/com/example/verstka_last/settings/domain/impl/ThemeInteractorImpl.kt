package com.example.verstka_last.settings.domain.impl

import com.example.verstka_last.settings.domain.api.ThemeInteractor
import com.example.verstka_last.settings.domain.api.ThemeRepository

class ThemeInteractorImpl(private val themeRepository: ThemeRepository) : ThemeInteractor {

    override fun toggleTheme() {
        val current = themeRepository.isDarkThemeEnabled()
        themeRepository.setDarkThemeEnabled(!current)
    }

    override fun isDarkTheme(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }
}