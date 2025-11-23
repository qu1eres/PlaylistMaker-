package com.example.verstka_last.search.domain.impl

import com.example.verstka_last.search.domain.api.TrackRepository
import com.example.verstka_last.search.domain.api.TracksInteractor

class TracksInteractorImpl(
    private val trackRepository: TrackRepository
) : TracksInteractor {

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        try {
            val foundTracks = trackRepository.searchTrack(expression)
            consumer.consume(foundTracks)
        } catch (e: Exception) {
            consumer.consume(emptyList())
        }
    }
}