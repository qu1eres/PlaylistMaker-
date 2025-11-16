package com.example.verstka_last.domain.api

import com.example.verstka_last.domain.models.Track

interface SearchHistoryInteractor {
    fun saveTrack(track: Track)
    fun loadHistory(): List<Track>
    fun clearHistory()
    fun getRecentSearches(limit: Int = 5): List<Track>
}