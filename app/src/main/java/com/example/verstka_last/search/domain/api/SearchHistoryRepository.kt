package com.example.verstka_last.search.domain.api

import com.example.verstka_last.core.domain.models.Track

interface SearchHistoryRepository {
    fun saveTrack(track: Track)
    fun loadHistory(): List<Track>
    fun clearHistory()
}