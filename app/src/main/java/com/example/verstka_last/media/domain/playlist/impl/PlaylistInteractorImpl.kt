package com.example.verstka_last.media.domain.playlist.impl

import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.media.domain.playlist.PlaylistInteractor
import com.example.verstka_last.media.domain.playlist.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) : PlaylistInteractor {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addTrack(track: Track, playlist: Playlist): Boolean {
        val trackList = playlistRepository.getTrackList(playlist)
        return if (!trackList.contains(track)) {
            playlistRepository.addTrack(track, playlist)
            true
        } else false
    }
}