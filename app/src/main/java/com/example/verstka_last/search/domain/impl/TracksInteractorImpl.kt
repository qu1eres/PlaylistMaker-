package com.example.verstka_last.search.domain.impl

import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.search.domain.api.TrackRepository
import com.example.verstka_last.search.domain.api.TracksInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class TracksInteractorImpl(
    private val trackRepository: TrackRepository
) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<List<Track>> {
        return trackRepository.searchTrack(expression)
            .catch { e ->
                emit(emptyList())
            }
    }
}