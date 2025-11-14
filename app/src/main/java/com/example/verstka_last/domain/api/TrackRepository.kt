package com.example.verstka_last.domain.api

import com.example.verstka_last.domain.models.Track

interface TrackRepository {
    fun searchTrack(expression: String): List<Track>
}