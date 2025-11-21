package com.example.verstka_last.search.presentation

import com.example.verstka_last.core.domain.models.Track

sealed class SearchState {
    object Initial : SearchState()
    object Loading : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
    data class Results(val tracks: List<Track>) : SearchState()
    object Empty : SearchState()
    object Error : SearchState()
}