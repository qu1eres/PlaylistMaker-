package com.example.verstka_last.media.presentation

import com.example.verstka_last.core.domain.models.Track

sealed interface FavoriteState {
    data class Content(val tracks: List<Track>) : FavoriteState
    object Empty : FavoriteState
}