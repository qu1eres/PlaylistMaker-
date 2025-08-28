package com.example.verstka_last

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackId: Long,
    val title: String,
    val artist: String,
    val duration: String,
    val artworkUrl: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String
) : Serializable {

    fun getReleaseYear(): String? {
        return releaseDate?.take(4)
    }

    fun getHighResArtworkUrl(): String? {
        return artworkUrl?.replaceAfterLast('/', "512x512bb.jpg")
    }
}

fun ITunesTrack.toTrack(): Track {
    val duration = trackTimeMillis?.let {
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(it)
    } ?: "00:00"

    return Track(
        trackId = trackId ?: 0L,
        title = trackName ?: "",
        artist = artistName ?: "",
        duration = duration,
        artworkUrl = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName ?: "",
        country = country ?: ""
    )
}