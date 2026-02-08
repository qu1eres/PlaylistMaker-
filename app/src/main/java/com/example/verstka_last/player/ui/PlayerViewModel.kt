package com.example.verstka_last.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.media.domain.favorites.FavoritesInteractor
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.media.domain.playlist.PlaylistInteractor
import com.example.verstka_last.player.domain.api.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData(PlayerScreenState())
    val screenState: LiveData<PlayerScreenState> = _screenState

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists

    private val _playlistActionResult = MutableLiveData<PlaylistActionResult?>()
    val playlistActionResult: LiveData<PlaylistActionResult?> = _playlistActionResult

    private var currentTrack: Track? = null
    private var timerJob: Job? = null

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlistsList ->
                _playlists.value = playlistsList
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            currentTrack?.let { track ->
                val isAdded = playlistInteractor.addTrack(track, playlist)
                if (isAdded) {
                    val currentList = _playlists.value
                    val index = currentList.indexOfFirst { it.id == playlist.id }
                    if (index != -1) {
                        val actualPlaylist = currentList[index]
                        val updatedPlaylist = actualPlaylist.copy(trackCount = actualPlaylist.trackCount + 1)

                        val newList = currentList.toMutableList()
                        newList[index] = updatedPlaylist
                        _playlists.value = newList
                    }

                    _playlistActionResult.postValue(PlaylistActionResult.Added(playlist.title))
                } else {
                    _playlistActionResult.postValue(PlaylistActionResult.AlreadyExists(playlist.title))
                }
            }
        }
    }

    fun setPlaylistCreated() {
        _playlistActionResult.postValue(PlaylistActionResult.Created)
    }

    fun clearPlaylistActionResult() {
        _playlistActionResult.postValue(null)
    }

    fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                val currentPosition = playerInteractor.getCurrentPosition()
                val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                delay(DELAY_MILLIS)
                updateScreenState { it.copy(currentTime = formattedTime) }
            }
        }
    }

    fun preparePlayer(track: Track) {
        currentTrack = track

        loadFavoriteState(track)

        track.previewUrl?.let { previewUrl ->
            playerInteractor.preparePlayer(
                url = previewUrl,
                onPrepared = {
                    updateScreenState { it.copy(playerState = PlayerState.Prepared) }
                },
                onCompletion = {
                    updateScreenState {
                        it.copy(
                            playerState = PlayerState.Prepared,
                            currentTime = "00:00"
                        )
                    }
                    timerJob?.cancel()
                }
            )
        }
    }

    fun playbackControl() {
        when (_screenState.value?.playerState) {
            PlayerState.Playing -> pausePlayer()
            PlayerState.Prepared, PlayerState.Paused -> startPlayer()
            else -> {}
        }
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        updateScreenState { it.copy(playerState = PlayerState.Paused) }
        timerJob?.cancel()
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        updateScreenState { it.copy(playerState = PlayerState.Playing) }
        startTimer()
    }

    private fun updateScreenState(update: (PlayerScreenState) -> PlayerScreenState) {
        val currentState = _screenState.value ?: PlayerScreenState()
        _screenState.postValue(update(currentState))
    }

    fun onFavoriteClicked() {
        currentTrack?.let { track ->
            viewModelScope.launch {
                val isFavorite = favoritesInteractor.updateFavorite(track)
                updateScreenState { it.copy(isFavorite = isFavorite) }
            }
        }
    }

    private fun loadFavoriteState(track: Track) {
        viewModelScope.launch {
            val isFavorite = favoritesInteractor.getChecked(track.trackId.toString())
            updateScreenState { it.copy(isFavorite = isFavorite) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
        timerJob?.cancel()
    }

    companion object {
        private const val DELAY_MILLIS = 300L
    }
}

sealed class PlaylistActionResult {
    data class Added(val playlistName: String) : PlaylistActionResult()
    data class AlreadyExists(val playlistName: String) : PlaylistActionResult()
    object Created : PlaylistActionResult()
}