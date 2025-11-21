package com.example.verstka_last.search.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.search.domain.api.SearchHistoryInteractor
import com.example.verstka_last.search.domain.api.TracksInteractor
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

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    private var searchRunnable: Runnable? = null
    private var clickDebounceRunnable: Runnable? = null
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
        // Отменяем предыдущий поиск
        searchRunnable?.let { handler.removeCallbacks(it) }

        if (query.isEmpty()) {
            showHistory()
            return
        }

        searchRunnable = Runnable {
            performSearch(query)
        }

        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    private fun performSearch(query: String) {
        _searchState.postValue(SearchState.Loading)

        executor.execute {
            try {
                // Используем callback-based подход вместо suspend функций
                tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>) {
                        val newState = if (foundTracks.isEmpty()) {
                            SearchState.Empty
                        } else {
                            SearchState.Results(foundTracks)
                        }
                        _searchState.postValue(newState)
                    }
                })
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

            clickDebounceRunnable = Runnable {
                isClickAllowed = true
            }

            handler.postDelayed(clickDebounceRunnable!!, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    // Очистка ресурсов при уничтожении ViewModel
    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
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