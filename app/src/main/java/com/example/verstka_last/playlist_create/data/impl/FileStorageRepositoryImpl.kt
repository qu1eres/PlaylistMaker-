package com.example.verstka_last.playlist_create.data.impl

import android.net.Uri
import com.example.verstka_last.playlist_create.data.FileStorage
import com.example.verstka_last.playlist_create.domain.FileStorageRepository
import java.io.File

class FileStorageRepositoryImpl(private val storage: FileStorage): FileStorageRepository {
    override fun saveImage(filePath: File, savePlaylist: String, uri: Uri) {
        storage.saveImageToPrivateStorage(filePath,savePlaylist,uri)    }
}