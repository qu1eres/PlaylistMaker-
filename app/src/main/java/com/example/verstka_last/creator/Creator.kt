package com.example.verstka_last.creator

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.verstka_last.settings.data.local.ThemePreferences
import com.example.verstka_last.core.data.network.RetrofitNetworkClient
import com.example.verstka_last.player.data.impl.PlayerRepositoryImpl
import com.example.verstka_last.player.domain.api.PlayerInteractor
import com.example.verstka_last.player.domain.api.PlayerRepository
import com.example.verstka_last.player.domain.impl.PlayerInteractorImpl
import com.example.verstka_last.player.ui.PlayerViewModel
import com.example.verstka_last.settings.data.impl.ThemeRepositoryImpl
import com.example.verstka_last.search.data.network.TrackRepositoryImpl
import com.example.verstka_last.settings.domain.api.ThemeInteractor
import com.example.verstka_last.search.domain.api.TrackRepository
import com.example.verstka_last.search.domain.api.TracksInteractor
import com.example.verstka_last.settings.domain.impl.ThemeInteractorImpl
import com.example.verstka_last.search.domain.impl.TracksInteractorImpl
import com.example.verstka_last.search.domain.api.SearchHistoryInteractor
import com.example.verstka_last.search.domain.impl.SearchHistoryInteractorImpl
import com.example.verstka_last.search.data.network.SearchHistoryRepositoryImpl
import com.example.verstka_last.search.presentation.SearchViewModel
import com.example.verstka_last.sharing.domain.api.SharingInteractor
import com.example.verstka_last.sharing.domain.impl.SharingInteractorImpl
import com.example.verstka_last.sharing.data.impl.SharingRepositoryImpl
import com.example.verstka_last.sharing.ui.SharingViewModelFactory
import com.google.gson.Gson

object Creator {
    private fun getTracksRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        val preferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val themePreferences = ThemePreferences(preferences)
        val themeRepository = ThemeRepositoryImpl(themePreferences)
        return ThemeInteractorImpl(themeRepository)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val sharedPreferences = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
        val gson = Gson()
        val searchHistoryRepository = SearchHistoryRepositoryImpl(sharedPreferences, gson)
        return SearchHistoryInteractorImpl(searchHistoryRepository)
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    private fun getPlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        val sharingRepository = SharingRepositoryImpl(context)
        return SharingInteractorImpl(sharingRepository)
    }

    fun provideSharingViewModelFactory(context: Context): ViewModelProvider.Factory {
        return SharingViewModelFactory(provideSharingInteractor(context))
    }

    fun providePlayerViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlayerViewModel(
                    playerInteractor = getPlayerInteractor()
                ) as T
            }
        }
    }

    fun provideSearchViewModelFactory(activity: AppCompatActivity): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(
                    provideTracksInteractor(),
                    provideSearchHistoryInteractor(activity)
                ) as T
            }
        }
    }
}