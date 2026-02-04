package com.example.verstka_last.player.ui

data class PlayerScreenState(
    val playerState: PlayerState = PlayerState.Default,
    val currentTime: String = "00:00",
    val isFavorite: Boolean = false
)