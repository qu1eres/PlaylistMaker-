package com.example.verstka_last.playlist_create.domain

import android.net.Uri
import java.io.File

interface FileStorageRepository {
    fun saveImage(filePath: File, savePlaylist: String, uri: Uri)
}