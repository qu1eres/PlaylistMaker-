package com.example.verstka_last.playlist_create.domain.impl

import android.net.Uri
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.media.domain.playlist.PlaylistRepository
import com.example.verstka_last.playlist_create.domain.FileStorageRepository
import com.example.verstka_last.playlist_create.domain.PlaylistCreatorInteractor
import kotlinx.coroutines.flow.first
import java.io.File

class PlaylistCreatorInteractorImpl(private val repository: PlaylistRepository, private val fileRepository: FileStorageRepository) :
    PlaylistCreatorInteractor {
    override suspend fun savePlaylist(playlistName: String, description: String, fileDir: String): Long {
        val playlist = Playlist(0, playlistName, description, fileDir, 0, mutableListOf())
        return repository.addPlaylist(playlist)
    }

    override suspend fun saveImage(filePath: File, savePlaylist: String, uri: Uri): String? {
        return fileRepository.saveImage(filePath, savePlaylist, uri)
    }

    override suspend fun updatePlaylist(playlist: Playlist, newImageUri: Uri?, imagesDir: File): Boolean {
        if (newImageUri == null) {
            return try {
                repository.updatePlayList(playlist)
                true
            } catch (e: Exception) { false }
        }
        val fileName = "playlist_${playlist.id}_${System.currentTimeMillis()}.jpg"
        val savedPath = fileRepository.saveImage(imagesDir, fileName, newImageUri) ?: return false
        playlist.imagePath.let { oldPath -> File(oldPath).delete() }

        val updatedPlaylist = playlist.copy(imagePath = savedPath)
        return try {
            repository.updatePlayList(updatedPlaylist)
            true
        } catch (e: Exception) { false }
    }
    override suspend fun getPlaylist(id: Long): Playlist {
        return repository.getPlaylist(id).first()
    }
}