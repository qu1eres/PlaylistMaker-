package com.example.verstka_last.search.domain.impl

import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.search.domain.api.SearchHistoryRepository
import com.example.verstka_last.search.domain.api.SearchHistoryInteractor

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