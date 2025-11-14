package com.example.verstka_last.domain

import com.example.verstka_last.data.network.RetrofitNetworkClient
import com.example.verstka_last.data.network.TrackRepositoryImpl
import com.example.verstka_last.domain.api.TrackRepository
import com.example.verstka_last.domain.api.TracksInteractor
import com.example.verstka_last.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideMoviesInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
}