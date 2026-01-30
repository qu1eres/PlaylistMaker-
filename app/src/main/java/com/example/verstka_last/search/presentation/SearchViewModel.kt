package com.example.verstka_last.search.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.search.domain.api.SearchHistoryInteractor
import com.example.verstka_last.search.domain.api.TracksInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>(SearchState.Initial)
    val searchState: LiveData<SearchState> = _searchState

    private val _currentSearchText = MutableLiveData("")
    val currentSearchText: LiveData<String> = _currentSearchText

    private val _navigationEvent = MutableLiveData<NavigationEvent?>()
    val navigationEvent: LiveData<NavigationEvent?> = _navigationEvent

    private val executor = Executors.newSingleThreadExecutor()

    private var searchJob: Job? = null
    private var clickDebounceJob: Job? = null

    private var isClickAllowed = true

    fun onSearchTextChanged(text: String) {
        _currentSearchText.value = text
        searchDebounce(text)
    }

    fun onSearchFocusChanged(hasFocus: Boolean) {
        val currentText = _currentSearchText.value ?: ""
        if (hasFocus && currentText.isEmpty()) {
            showHistory()
        }
    }

    fun onSearchAction() {
        val query = _currentSearchText.value?.trim() ?: ""
        if (query.isNotEmpty()) {
            performSearch(query)
        }
    }

    fun onClearClick() {
        _currentSearchText.value = ""
        showHistory()
    }

    fun onClearHistoryClick() {
        searchHistoryInteractor.clearHistory()
        showHistory()
    }

    fun onTrackClick(track: Track) {
        if (clickDebounce()) {
            searchHistoryInteractor.saveTrack(track)
            _navigationEvent.value = NavigationEvent.OpenPlayer(track)
        }
    }

    fun onRetryClick() {
        val query = _currentSearchText.value?.trim() ?: ""
        if (query.isNotEmpty()) {
            performSearch(query)
        }
    }

    fun loadInitialState() {
        showHistory()
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    private fun searchDebounce(query: String) {
        if (query.isEmpty()) {
            showHistory()
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        _searchState.postValue(SearchState.Loading)

        searchJob?.cancel()
        viewModelScope.launch {
            try {
                tracksInteractor
                    .searchTracks(query)
                    .collect { results ->
                        val newState = if (results.isEmpty()) {
                            SearchState.Empty
                        } else {
                            SearchState.Results(results)
                        }
                        _searchState.postValue(newState)
                    }
            } catch (e: Exception) {
                _searchState.postValue(SearchState.Error)
            }
        }
    }

    private fun showHistory() {
        executor.execute {
            val history = searchHistoryInteractor.loadHistory()
            val newState = if (history.isNotEmpty()) {
                SearchState.History(history)
            } else {
                SearchState.Initial
            }
            _searchState.postValue(newState)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickDebounceJob?.cancel()

            clickDebounceJob = viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }

    sealed class NavigationEvent {
        data class OpenPlayer(val track: Track) : NavigationEvent()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}