package com.example.verstka_last.core.domain.impl

import com.example.verstka_last.core.domain.db.FavoritesInteractor
import com.example.verstka_last.core.domain.db.FavoritesRepository
import com.example.verstka_last.core.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository) : FavoritesInteractor {
    override suspend fun insertTrack(track: Track) {
        favoritesRepository.insertTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        favoritesRepository.deleteTrack(track)
    }

    override fun getTracks(): Flow<List<Track>> {
        return favoritesRepository.getTracks()
    }
}