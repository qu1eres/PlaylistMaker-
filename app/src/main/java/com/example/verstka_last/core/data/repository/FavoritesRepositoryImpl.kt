package com.example.verstka_last.core.data.repository

import com.example.verstka_last.core.data.converters.TrackDbConverter
import com.example.verstka_last.core.data.db.AppDatabase
import com.example.verstka_last.core.data.db.entity.TrackEntity
import com.example.verstka_last.core.domain.db.FavoritesRepository
import com.example.verstka_last.core.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(private val appDatabase: AppDatabase, private val trackDbConverter: TrackDbConverter) : FavoritesRepository {
    override suspend fun insertTrack(track: Track) {
        appDatabase.trackDao().insertTrack(convertFromTrack(track))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(convertFromTrack(track))
    }

    override fun getTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        tracks.map { tracks ->  }
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }
    private fun convertFromTrack(track: Track): TrackEntity {
        return trackDbConverter.map(track)
    }
}