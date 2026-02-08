package com.example.verstka_last.media.presentation.playlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.media.domain.playlist.PlaylistInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _isEmptyState = MutableStateFlow(true)
    val isEmptyState: StateFlow<Boolean> = _isEmptyState

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlistsList ->
                _playlists.value = playlistsList
                _isEmptyState.value = playlistsList.isEmpty()
            }
        }
    }
}