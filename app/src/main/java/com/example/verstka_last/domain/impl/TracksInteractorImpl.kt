package com.example.verstka_last.domain.impl

import com.example.verstka_last.domain.api.TrackRepository
import com.example.verstka_last.domain.api.TracksInteractor
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TrackRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTrack(expression))
        }
    }
}