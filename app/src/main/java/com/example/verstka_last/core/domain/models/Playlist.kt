package com.example.verstka_last.core.domain.models

data class Playlist(
    var id: Long,
    val title: String,
    val description: String,
    val imagePath: String,
    var trackCount: Long,
    var tracks: String?
)