package com.example.verstka_last.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
data class TrackEntity(
    @PrimaryKey @ColumnInfo(name = "track_id")
    val id: String,
    @ColumnInfo(name = "artwork_url")
    val image: String,
    val title: String,
    val artist: String,
    val collectionName: String,
    @ColumnInfo(name = "release_year")
    val releaseDate: String,
    val genreName: String,
    val country: String,
    @ColumnInfo(name = "duration_mm:ss")
    val duration: String,
    val previewUrl: String)
