package com.example.verstka_last.data.dto

import com.example.verstka_last.domain.models.Track
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class TrackDto(val trackId: Long?,
                    val trackName: String?,
                    val artistName: String?,
                    val trackTimeMillis: Long?,
                    val artworkUrl100: String?,
                    val collectionName: String?,
                    val releaseDate: String?,
                    val primaryGenreName: String?,
                    val country: String?,
                    val previewUrl: String?) : Serializable

fun TrackDto.toDomain(): Track {
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
        country = country ?: "",
        previewUrl = previewUrl ?: ""
    )
}