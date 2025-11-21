package com.example.verstka_last.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.verstka_last.settings.domain.api.ThemeInteractor

class SettingsViewModel(
    private val themeInteractor: ThemeInteractor
) : ViewModel() {
    private val _themeSettingsState = MutableLiveData<Boolean>()
    val themeSettingsState: LiveData<Boolean> = _themeSettingsState

    private val _navigationEvent = MutableLiveData<NavigationEvent?>()
    val navigationEvent: LiveData<NavigationEvent?> = _navigationEvent

    init {
        _themeSettingsState.value = themeInteractor.isDarkTheme()
    }

    fun onThemeToggled(checked: Boolean) {
        val isCurrentlyDark = themeInteractor.isDarkTheme()

        if (checked != isCurrentlyDark) {
            themeInteractor.toggleTheme()
            _themeSettingsState.value = checked
        }
    }

    fun onShareClicked() {
        _navigationEvent.value = NavigationEvent.ShareApp
    }

    fun onSupportClicked() {
        _navigationEvent.value = NavigationEvent.ContactSupport
    }

    fun onAgreementClicked() {
        _navigationEvent.value = NavigationEvent.OpenAgreement
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object ShareApp : NavigationEvent()
        object ContactSupport : NavigationEvent()
        object OpenAgreement : NavigationEvent()
    }
}