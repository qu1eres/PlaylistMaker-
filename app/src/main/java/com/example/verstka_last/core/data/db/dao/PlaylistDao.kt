package com.example.verstka_last.core.data.db.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.verstka_last.core.data.db.entity.PlaylistEntity

interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playList: PlaylistEntity): Long

    @Query("SELECT * FROM playlist_table")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Update(entity = PlaylistEntity::class,onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlaylist(playList: PlaylistEntity)

    @Query("SELECT tracks FROM playlist_table WHERE playlist_id = :playListId" )
    suspend fun getTrackList(playListId: Long): List<String>
}