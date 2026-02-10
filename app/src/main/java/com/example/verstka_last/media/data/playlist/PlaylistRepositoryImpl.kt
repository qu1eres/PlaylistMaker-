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

    override fun getPlaylists(): Flow<List<Playlist>> = flow { emit(convertFromEntity(appDatabase.playlistDao().getPlaylists())) }
    override suspend fun updatePlayList(playList: Playlist) { appDatabase.playlistDao().updatePlaylist(convertToEntity(playList)) }
    override suspend fun delete(playlist: Playlist) { appDatabase.playlistDao().delete(convertToEntity(playlist)) }
    override suspend fun getTrackList(playlistId: Long): List<Track> { return jsonMapper.convertToList(appDatabase.playlistDao().getTrackList(playlistId).first()) }

    override fun getPlaylist(playListId: Long): Flow<Playlist> = flow {
        val playList = appDatabase.playlistDao().getPlayList(playListId).first()
        emit(convertFromPlaylistEntity(playList))
    }

    override suspend fun addPlaylist(playlist: Playlist): Long {
        return appDatabase.playlistDao().insertPlaylist(convertToEntity(playlist))
    }

    override suspend fun addTrack(track: Track, playlist: Playlist) {
        ++playlist.trackCount
        playlist.tracks.add(track)
        val playListEntity = convertToEntity(playlist)
        appDatabase.playlistDao().updatePlaylist(playListEntity)
    }

    override suspend fun removeTrack(track: Track, playList: Playlist) {
        --playList.trackCount
        playList.tracks.remove(track)
        val playListEntity = convertToEntity(playList)
        appDatabase.playlistDao().updatePlaylist(playListEntity)
    }

    private fun convertFromPlaylistEntity(playList: PlaylistEntity): Playlist {
        val tracks = jsonMapper.convertToList(playList.tracks)
        return playlistDbConverter.map(playList,tracks)
    }
    private fun convertFromEntity(playLists: List<PlaylistEntity>): List<Playlist> {
        return playLists.map { playList ->
            val tracks = jsonMapper.convertToList(playList.tracks)
            playlistDbConverter.map(playList, tracks)
        }
    }
    private fun convertToEntity(playList: Playlist): PlaylistEntity {
        val tracks = jsonMapper.convertFromList(playList.tracks)
        return playlistDbConverter.map(playList, tracks)
    }
}