package com.example.verstka_last.search.data.network

import com.example.verstka_last.core.data.db.AppDatabase
import com.example.verstka_last.core.data.network.NetworkClient
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.search.domain.api.TrackRepository
import com.example.verstka_last.search.data.dto.ITunesSearchResponse
import com.example.verstka_last.search.data.dto.TrackRequest
import com.example.verstka_last.search.data.dto.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class TrackRepositoryImpl(private val networkClient: NetworkClient, private val appDatabase: AppDatabase) : TrackRepository {
    override fun searchTrack(expression: String): Flow<List<Track>> = flow {
        val response = networkClient.doRequest(TrackRequest(expression))

        if (response.resultCode != 200) {
            throw IOException("${response.resultCode}")
        }

        val favoriteTracks = appDatabase.trackDao().getTracksPrimaryKey().toSet()
        val tracks = (response as ITunesSearchResponse).results.map { it.toDomain() }
        tracks.forEach { track ->
                 track.isFavorite = favoriteTracks.contains(track.trackId.toString()) }
        emit(tracks)
    }
}