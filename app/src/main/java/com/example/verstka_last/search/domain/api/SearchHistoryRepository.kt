package com.example.verstka_last.search.domain.api

import com.example.verstka_last.core.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun saveTrack(track: Track)
    fun loadHistory(): Flow<List<Track>>
    fun clearHistory()
}