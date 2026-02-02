package com.example.verstka_last.search.domain.impl

import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.search.domain.api.SearchHistoryRepository
import com.example.verstka_last.search.domain.api.SearchHistoryInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override suspend fun saveTrack(track: Track) {
        searchHistoryRepository.saveTrack(track)
    }

    override fun loadHistory(): Flow<List<Track>> {
        return searchHistoryRepository.loadHistory()
    }

    override suspend fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }

    override suspend fun getRecentSearches(limit: Int): List<Track> {
        return searchHistoryRepository.loadHistory().first().take(limit)
    }
}