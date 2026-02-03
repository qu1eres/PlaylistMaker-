package com.example.verstka_last.core.domain.db

import com.example.verstka_last.core.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun insertTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    suspend fun updateFavorite(track: Track): Boolean
    fun getTracks(): Flow<List<Track>>
}