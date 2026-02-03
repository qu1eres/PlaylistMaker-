package com.example.verstka_last.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.verstka_last.media.domain.FavoritesInteractor
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.player.domain.api.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor, private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private val _screenState = MutableLiveData(PlayerScreenState())
    val screenState: LiveData<PlayerScreenState> = _screenState

    private val stateFavoriteData = MutableLiveData<Boolean>()
    fun observeFavoriteState(): LiveData<Boolean> = stateFavoriteData

    private var timerJob: Job? = null

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

    fun preparePlayer(previewUrl: String?) {
        if (previewUrl == null) return
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

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            renderFavoriteState(favoritesInteractor.updateFavorite(track))
        }
    }

    private fun renderFavoriteState(isChecked: Boolean) {
        stateFavoriteData.postValue(isChecked)
    }

    fun getChecked(track: Track) {
        viewModelScope.launch {
            renderFavoriteState(favoritesInteractor.getChecked(track.trackId.toString()))
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