package com.example.verstka_last.search.domain.api

import com.example.verstka_last.core.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(expression: String): Flow<List<Track>>
}