package com.example.verstka_last

import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackId: Long,
    val title: String,
    val artist: String,
    val duration: String,
    val artworkUrl: String?
)

fun ITunesTrack.toTrack(): Track {
    val duration = trackTimeMillis?.let {
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(it)
    } ?: "00:00"

    return Track(
        trackId = trackId ?: 0L,
        title = trackName ?: "",
        artist = artistName ?: "",
        duration = duration,
        artworkUrl = artworkUrl100
    )
}