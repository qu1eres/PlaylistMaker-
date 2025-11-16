package com.example.verstka_last.data.network

import com.example.verstka_last.domain.api.SearchHistoryInteractor
import com.example.verstka_last.domain.api.SearchHistoryRepository
import com.example.verstka_last.domain.models.Track

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun saveTrack(track: Track) {
        searchHistoryRepository.saveTrack(track)
    }

    override fun loadHistory(): List<Track> {
        return searchHistoryRepository.loadHistory()
    }

    override fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }

    override fun getRecentSearches(limit: Int): List<Track> {
        return searchHistoryRepository.loadHistory().take(limit)
    }
}