package com.example.verstka_last.core.data.converters

import com.example.verstka_last.core.data.db.entity.PlaylistEntity
import com.example.verstka_last.core.domain.models.Playlist

class PlaylistDbConverter {
    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.id,
            title = playlist.title,
            description = playlist.description,
            imagePath = playlist.imagePath,
            trackCount = playlist.trackCount,
            tracks = playlist.tracks
        )
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.title,
            playlist.description,
            playlist.imagePath,
            playlist.trackCount,
            playlist.tracks
        )
    }
}