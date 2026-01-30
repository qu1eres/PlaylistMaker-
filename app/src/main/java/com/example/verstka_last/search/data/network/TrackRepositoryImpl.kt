package com.example.verstka_last.search.data.network

import com.example.verstka_last.core.data.network.NetworkClient
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.search.domain.api.TrackRepository
import com.example.verstka_last.search.data.dto.ITunesSearchResponse
import com.example.verstka_last.search.data.dto.TrackRequest
import com.example.verstka_last.search.data.dto.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTrack(expression: String): Flow<List<Track>> = flow {
        val response = networkClient.doRequest(TrackRequest(expression))
        if (response.resultCode == 200) {
           val tracks = (response as ITunesSearchResponse).results.map { it.toDomain() }
            emit(tracks)
        } else {
            emit(emptyList())
        }
    }
}