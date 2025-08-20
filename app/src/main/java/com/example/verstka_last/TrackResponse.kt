package com.example.verstka_last

data class ITunesSearchResponse(
    val resultCount: Int,
    val results: List<ITunesTrack>
)

data class ITunesTrack(
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?
)