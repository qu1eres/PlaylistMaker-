package com.example.verstka_last.playlist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.playlist.domain.PlayListRedactInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistRedactViewModel(
    private val interactor: PlayListRedactInteractor
) : ViewModel() {

    private val _playlist = MutableStateFlow<Playlist?>(null)
    val playlist: StateFlow<Playlist?> = _playlist

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks

    private val _totalDuration = MutableStateFlow("")
    val totalDuration: StateFlow<String> = _totalDuration

    private var currentPlaylistId: Long = -1

    fun initPlaylist(playlistId: Long) {
        currentPlaylistId = playlistId
        viewModelScope.launch {
            interactor.getPlayList(playlistId).collect { playlist ->
                _playlist.value = playlist
                loadTracks(playlist)
            }
        }
    }

    private suspend fun loadTracks(playlist: Playlist) {
        interactor.getTracks(playlist).collect { tracksList ->
            _tracks.value = tracksList.reversed()
        }
    }

    fun getPlaylistTime(tracks: List<Track>): Int =
        interactor.getPlayListTime(tracks)

    fun deletePlayList(playlist: Playlist) {
        viewModelScope.launch { interactor.delete(playlist) }
    }

    fun removeTrack(track: Track) {
        viewModelScope.launch {
            _playlist.value?.let { currentPlaylist ->
                interactor.removeTrack(track, currentPlaylist)
                initPlaylist(currentPlaylistId)
            }
        }
    }
}