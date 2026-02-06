package com.example.verstka_last.media.domain.favorites.impl

import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.media.domain.favorites.FavoritesInteractor
import com.example.verstka_last.media.domain.favorites.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository) :
    FavoritesInteractor {
    private var isChecked = false
    override suspend fun insertTrack(track: Track) {
        favoritesRepository.insertTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        favoritesRepository.deleteTrack(track)
    }

    override suspend fun updateFavorite(track: Track): Boolean {
        favoritesRepository.getFavoriteChecked().collect{tracksId ->
            isChecked = if (tracksId.contains(track.trackId.toString())) {
                favoritesRepository.deleteTrack(track)
                false
            } else {
                favoritesRepository.insertTrack(track)
                true
            }
        }
        return isChecked
    }

    override suspend fun getChecked(tracksId: String) : Boolean {
        favoritesRepository.getFavoriteChecked().collect { id ->
            isChecked = id.contains(tracksId)
        }
        return isChecked
    }

    override fun getTracks(): Flow<List<Track>> {
        return favoritesRepository.getTracks()
    }
}