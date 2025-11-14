package com.example.verstka_last.domain.api

import com.example.verstka_last.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
    }
}