package com.example.verstka_last.playlist_create.domain

import android.net.Uri
import java.io.File

interface PlaylistCreatorInteractor {
    fun saveImage(filePath: File, savePlaylist: String, uri: Uri)
    suspend fun savePlaylist(playlistName: String, description: String, fileDir: String): Long
}