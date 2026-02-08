package com.example.verstka_last.media.domain.playlist

import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.core.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist): Long
    suspend fun addTrack(track: Track, playlist: Playlist)
    suspend fun getTrackList(playlist: Playlist): List<Track>
}