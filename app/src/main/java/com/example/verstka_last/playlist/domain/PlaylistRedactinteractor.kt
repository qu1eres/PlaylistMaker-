package com.example.verstka_last.playlist.domain

import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.core.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlayListRedactInteractor {
    fun getPlayList(playlistId: Long): Flow<Playlist>
    fun getTracks(playlist: Playlist): Flow<List<Track>>
    fun getPlayListTime(tracks: List<Track>): Int
    suspend fun removeTrack(track: Track, playlist: Playlist)
    suspend fun delete(playlist: Playlist)
}