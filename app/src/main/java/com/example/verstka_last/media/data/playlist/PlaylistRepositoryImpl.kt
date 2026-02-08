package com.example.verstka_last.media.data.playlist

import com.example.verstka_last.core.data.converters.PlaylistDbConverter
import com.example.verstka_last.core.data.db.AppDatabase
import com.example.verstka_last.core.data.db.JsonMapper
import com.example.verstka_last.core.data.db.entity.PlaylistEntity
import com.example.verstka_last.core.data.db.entity.PlaylistTrackEntity
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.media.domain.playlist.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(private val appDatabase: AppDatabase, private val playlistDbConverter: PlaylistDbConverter, private val jsonMapper: JsonMapper) :
    PlaylistRepository {
    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        emit(convertFromPlaylistEntity(appDatabase.playlistDao().getPlaylists()))
    }

    override suspend fun addPlaylist(playlist: Playlist): Long {
        return appDatabase.playlistDao().insertPlaylist(convertFromPlaylist(playlist))
    }

    override suspend fun addTrack(track: Track, playlist: Playlist) {
        val playlistTrackEntity = convertTrackToPlaylistTrackEntity(track)
        appDatabase.playlistTrackDao().insertTrack(playlistTrackEntity)

        val currentTracks = jsonMapper.convertToList(playlist.tracks)

        if (currentTracks.any { it.trackId == track.trackId }) {
            return
        }

        val updatedTracks = currentTracks.toMutableList().apply {
            add(0, track)
        }
        playlist.tracks = jsonMapper.write(updatedTracks)
        playlist.trackCount = updatedTracks.size.toLong()

        val playlistEntity = convertFromPlaylist(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    private fun convertTrackToPlaylistTrackEntity(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = track.trackId.toString(),
            trackName = track.title,
            artistName = track.artist,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            artworkUrl100 = track.artworkUrl,
            trackTimeMillis = track.duration.toLongOrNull()
        )
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<Track> {
        val playlist = appDatabase.playlistDao().getPlaylists()
            .firstOrNull { it.id == playlistId }

        return if (playlist != null && !playlist.tracks.isNullOrEmpty()) {
            jsonMapper.convertToList(playlist.tracks)
        } else {
            emptyList()
        }
    }

    override suspend fun getTrackList(playlist: Playlist): List<Track> {
        val tracks = appDatabase.playlistDao().getTrackList(playlist.id).first()
        return jsonMapper.convertToList(tracks)
    }

    private fun convertFromPlaylistEntity(playlist: List<PlaylistEntity>): List<Playlist> {
        return playlist.map { playlist -> playlistDbConverter.map(playlist) }
    }
    private fun convertFromPlaylist(playlist: Playlist): PlaylistEntity {
        return playlistDbConverter.map(playlist)
    }
}