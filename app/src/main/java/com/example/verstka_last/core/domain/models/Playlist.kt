package com.example.verstka_last.core.domain.models

data class Playlist(
    var id: Long,
    var title: String,
    var description: String,
    val imagePath: String,
    var trackCount: Long,
    var tracks: MutableList<Track>
)