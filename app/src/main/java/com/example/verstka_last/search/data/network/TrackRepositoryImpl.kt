package com.example.verstka_last.search.data.network

import com.example.verstka_last.core.data.network.NetworkClient
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.search.domain.api.TrackRepository
import com.example.verstka_last.search.data.dto.ITunesSearchResponse
import com.example.verstka_last.search.data.dto.TrackRequest
import com.example.verstka_last.search.data.dto.toDomain

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTrack(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackRequest(expression))
        if (response.resultCode == 200) {
            return (response as ITunesSearchResponse).results.map { it.toDomain() }
        } else {
            return emptyList()
        }
    }
}