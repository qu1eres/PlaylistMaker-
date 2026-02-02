package com.example.verstka_last.search.domain.api

import com.example.verstka_last.core.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {
    suspend fun saveTrack(track: Track)
    fun loadHistory(): Flow<List<Track>>
    suspend fun clearHistory()
    suspend fun getRecentSearches(limit: Int): List<Track>
}