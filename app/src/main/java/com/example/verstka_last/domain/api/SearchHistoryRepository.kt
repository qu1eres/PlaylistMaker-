package com.example.verstka_last.domain.api

import com.example.verstka_last.domain.models.Track

interface SearchHistoryRepository {
    fun saveTrack(track: Track)
    fun loadHistory(): List<Track>
    fun clearHistory()
}