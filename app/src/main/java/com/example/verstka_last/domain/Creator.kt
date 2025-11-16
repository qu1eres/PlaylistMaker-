package com.example.verstka_last.domain

import android.content.Context
import com.example.verstka_last.data.local.ThemePreferences
import com.example.verstka_last.data.network.RetrofitNetworkClient
import com.example.verstka_last.data.network.SearchHistoryInteractorImpl
import com.example.verstka_last.data.network.SearchHistoryRepositoryImpl
import com.example.verstka_last.data.network.ThemeRepositoryImpl
import com.example.verstka_last.data.network.TrackRepositoryImpl
import com.example.verstka_last.domain.api.SearchHistoryInteractor
import com.example.verstka_last.domain.api.ThemeInteractor
import com.example.verstka_last.domain.api.TrackRepository
import com.example.verstka_last.domain.api.TracksInteractor
import com.example.verstka_last.domain.impl.ThemeInteractorImpl
import com.example.verstka_last.domain.impl.TracksInteractorImpl
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
}