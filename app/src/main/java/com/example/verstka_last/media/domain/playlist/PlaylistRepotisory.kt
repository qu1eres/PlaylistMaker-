package com.example.verstka_last.media.domain.playlist

import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.core.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylist(playListId: Long): Flow<Playlist>
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist): Long
    suspend fun addTrack(track: Track, playlist: Playlist)
    suspend fun getTrackList(playlistId: Long): List<Track>
    suspend fun delete(playlist: Playlist)
    suspend fun removeTrack(track: Track, playList: Playlist)
    suspend fun updatePlayList(playList: Playlist)

}