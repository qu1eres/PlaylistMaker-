package com.example.verstka_last.core.domain.models

import java.io.Serializable

data class Track(
    val trackId: Long,
    val title: String,
    val artist: String,
    val duration: String,
    val artworkUrl: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Serializable {

    fun getReleaseYear(): String? {
        return releaseDate?.take(4)
    }

    fun getHighResArtworkUrl(): String? {
        return artworkUrl?.replaceAfterLast('/', "512x512bb.jpg")
    }
}