package com.example.verstka_last.playlist_create.domain

import android.net.Uri
import com.example.verstka_last.core.domain.models.Playlist
import java.io.File

interface PlaylistCreatorInteractor {
    suspend fun saveImage(filePath: File, savePlaylist: String, uri: Uri): String?
    suspend fun savePlaylist(playlistName: String, description: String, fileDir: String): Long
    suspend fun updatePlaylist(playlist: Playlist, newImageUri: Uri?, imagesDir: File): Boolean
    suspend fun getPlaylist(id: Long): Playlist
}