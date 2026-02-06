package com.example.verstka_last.media.data.favorites

import com.example.verstka_last.core.data.converters.TrackDbConverter
import com.example.verstka_last.core.data.db.AppDatabase
import com.example.verstka_last.core.data.db.entity.TrackEntity
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.media.domain.favorites.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(private val appDatabase: AppDatabase, private val trackDbConverter: TrackDbConverter) :
    FavoritesRepository {
    override suspend fun insertTrack(track: Track) {
        appDatabase.trackDao().insertTrack(convertFromTrack(track))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(convertFromTrack(track))
    }

    override fun getTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override fun getFavoriteChecked(): Flow<List<String>> = flow {
        val tracksId = appDatabase.trackDao().getTracksPrimaryKey()
        emit(tracksId)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }
    private fun convertFromTrack(track: Track): TrackEntity {
        return trackDbConverter.map(track)
    }
}