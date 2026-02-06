package com.example.verstka_last.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "playlist_id")
    val id: Long,
    val title: String,
    val description: String,
    val imagePath: String, // Путь к изображению обложки плейлиста
    @ColumnInfo(name = "tracks_ids")
    val tracksIds: String, // Список ID добавленных в плейлист треков
    val tracks: String)