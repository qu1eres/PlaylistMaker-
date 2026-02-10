package com.example.verstka_last.core.data.converters

import com.example.verstka_last.core.data.db.entity.PlaylistEntity
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.core.domain.models.Track

class PlaylistDbConverter {
    fun map(playlist: PlaylistEntity, tracks: MutableList<Track>): Playlist {
        return Playlist(
            id = playlist.id,
            title = playlist.title,
            description = playlist.description,
            imagePath = playlist.imagePath,
            trackCount = playlist.trackCount,
            tracks
        )
    }

    fun map(playlist: Playlist, tracks: String): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.title,
            playlist.description,
            playlist.imagePath,
            playlist.trackCount,
            tracks
        )
    }
}