package com.example.verstka_last.media.data.playlist

import com.example.verstka_last.core.data.converters.PlaylistDbConverter
import com.example.verstka_last.core.data.db.AppDatabase
import com.example.verstka_last.core.data.db.JsonMapper
import com.example.verstka_last.core.data.db.entity.PlaylistEntity
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
        ++playlist.trackCount
        playlist.tracks = jsonMapper.setTrack(track, playlist.tracks)
        val playlistEntity = convertFromPlaylist(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
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