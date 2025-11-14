package com.example.verstka_last.data.dto

data class ITunesSearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()