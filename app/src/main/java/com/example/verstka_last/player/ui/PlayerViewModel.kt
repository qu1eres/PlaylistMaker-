// ui/PlayerViewModel.kt
package com.example.verstka_last.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.verstka_last.player.domain.api.PlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData(PlayerScreenState())
    val screenState: LiveData<PlayerScreenState> = _screenState

    private var mainThreadHandler: Handler? = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (playerInteractor.isPlaying()) {
                val currentPosition = playerInteractor.getCurrentPosition()
                val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                updateScreenState { it.copy(currentTime = formattedTime) }
                mainThreadHandler?.postDelayed(this, DELAY_MILLIS)
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
                mainThreadHandler?.removeCallbacks(timerRunnable)
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
        mainThreadHandler?.removeCallbacks(timerRunnable)
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        updateScreenState { it.copy(playerState = PlayerState.Playing) }
        mainThreadHandler?.post(timerRunnable)
    }

    private fun updateScreenState(update: (PlayerScreenState) -> PlayerScreenState) {
        val currentState = _screenState.value ?: PlayerScreenState()
        _screenState.postValue(update(currentState))
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
        mainThreadHandler?.removeCallbacks(timerRunnable)
    }

    companion object {
        private const val DELAY_MILLIS = 300L
    }
}