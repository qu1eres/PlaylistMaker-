package com.example.verstka_last.data.network

import com.example.verstka_last.data.NetworkClient
import com.example.verstka_last.data.dto.ITunesSearchResponse
import com.example.verstka_last.data.dto.TrackRequest
import com.example.verstka_last.data.dto.toDomain
import com.example.verstka_last.domain.api.TrackRepository
import com.example.verstka_last.domain.models.Track

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