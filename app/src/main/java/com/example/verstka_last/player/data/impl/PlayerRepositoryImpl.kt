package com.example.verstka_last.player.data.impl

import android.media.MediaPlayer
import com.example.verstka_last.player.domain.api.PlayerRepository

class PlayerRepositoryImpl(private val mediaPlayerFactory: () -> MediaPlayer) : PlayerRepository {
    private var mediaPlayer: MediaPlayer? = null

    override fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        if (mediaPlayer != null) {
            releasePlayer()
        }

        mediaPlayer = mediaPlayerFactory().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener { onCompletion() }
        }
    }

    override fun startPlayer() {
        mediaPlayer?.start()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
    }

    override fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}