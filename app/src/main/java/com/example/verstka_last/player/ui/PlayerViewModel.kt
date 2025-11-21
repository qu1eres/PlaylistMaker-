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

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default)
    val playerState: LiveData<PlayerState> = _playerState

    private val _currentTime = MutableLiveData<String>("00:00")
    val currentTime: LiveData<String> = _currentTime

    private var mainThreadHandler: Handler? = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (playerInteractor.isPlaying()) {
                val currentPosition = playerInteractor.getCurrentPosition()
                _currentTime.postValue(SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition))
                mainThreadHandler?.postDelayed(this, DELAY_MILLIS)
            }
        }
    }

    fun preparePlayer(previewUrl: String?) {
        if (previewUrl == null) return
        playerInteractor.preparePlayer(
            url = previewUrl,
            onPrepared = {
                _playerState.postValue(PlayerState.Prepared)
            },
            onCompletion = {
                _playerState.postValue(PlayerState.Prepared)
                _currentTime.postValue("00:00")
                mainThreadHandler?.removeCallbacks(timerRunnable)
            }
        )
    }

    fun playbackControl() {
        when (_playerState.value) {
            PlayerState.Playing -> pausePlayer()
            PlayerState.Prepared, PlayerState.Paused -> startPlayer()
            else -> {}
        }
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _playerState.postValue(PlayerState.Paused)
        mainThreadHandler?.removeCallbacks(timerRunnable)
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        _playerState.postValue(PlayerState.Playing)
        mainThreadHandler?.post(timerRunnable)
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