package com.example.verstka_last.playlist.domain.impl

import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.media.domain.playlist.PlaylistRepository
import com.example.verstka_last.playlist.domain.PlayListRedactInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRedactInteractorImpl(private val repository: PlaylistRepository) :
    PlayListRedactInteractor {
    override fun getPlayList(playlistId: Long): Flow<Playlist> {
        return repository.getPlaylist(playlistId)
    }

    override fun getTracks(playlist: Playlist): Flow<List<Track>> = flow {
        emit(repository.getTrackList(playlist.id))
    }

    override fun getPlayListTime(tracks: List<Track>): Int {
        var totalSeconds = 0L

        for (track in tracks) {
            val durationString = track.duration
            val parts = durationString.split(":")

            val minutes = parts.getOrNull(0)?.toLongOrNull() ?: 0L
            val seconds = parts.getOrNull(1)?.toLongOrNull() ?: 0L

            totalSeconds += (minutes * 60) + seconds
        }

        val totalMinutes = totalSeconds / 60
        return totalMinutes.toInt()
    }

    override suspend fun delete(playlist: Playlist) {
        repository.delete(playlist)
    }

    override suspend fun removeTrack(track: Track, playlist: Playlist)  {
        repository.removeTrack(track,playlist)
    }


}