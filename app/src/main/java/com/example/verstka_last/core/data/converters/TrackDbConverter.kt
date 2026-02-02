package com.example.verstka_last.core.data.converters

import com.example.verstka_last.core.data.db.entity.TrackEntity
import com.example.verstka_last.core.domain.models.Track


class TrackDbConverter {
    fun map(track: TrackEntity): Track {
        return Track(
            trackId = track.id.toLong(),
            title = track.title,
            artist = track.artist,
            duration = track.duration,
            artworkUrl = track.image,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.genreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            id = track.trackId.toString(),
            image = track.artworkUrl!!,
            title = track.title,
            artist = track.artist,
            collectionName = track.collectionName!!,
            releaseDate = track.releaseDate!!,
            genreName = track.primaryGenreName,
            country = track.country,
            duration = track.duration,
            previewUrl = track.previewUrl
        )
    }
}