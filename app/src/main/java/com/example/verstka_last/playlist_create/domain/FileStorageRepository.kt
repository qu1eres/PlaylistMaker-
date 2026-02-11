package com.example.verstka_last.playlist_create.domain

import android.net.Uri
import java.io.File

interface FileStorageRepository {
    suspend fun saveImage(targetDir: File, fileName: String, uri: Uri): String?
}