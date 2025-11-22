package com.example.verstka_last.search.domain.api

import com.example.verstka_last.core.domain.models.Track

interface TrackRepository {
    fun searchTrack(expression: String): List<Track>
}