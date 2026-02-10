package com.example.verstka_last.playlist_create.domain.impl

import android.net.Uri
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.media.domain.playlist.PlaylistRepository
import com.example.verstka_last.playlist_create.domain.FileStorageRepository
import com.example.verstka_last.playlist_create.domain.PlaylistCreatorInteractor
import java.io.File

class PlaylistCreatorInteractorImpl(private val repository: PlaylistRepository, private val fileRepository: FileStorageRepository) :
    PlaylistCreatorInteractor {
    override suspend fun savePlaylist(playlistName: String, description: String, fileDir: String): Long {
        val playlist = Playlist(0, playlistName, description, fileDir, 0, mutableListOf())
        return repository.addPlaylist(playlist)
    }

    override fun saveImage(filePath: File, savePlaylist: String, uri: Uri) {
        fileRepository.saveImage(filePath,savePlaylist, uri)
    }

    override suspend fun updatePlaylist(playlist: Playlist, newImageUri: Uri?, imagesDir: File?
    ) {
        repository.updatePlayList(playlist)

        newImageUri?.let { uri ->
            imagesDir?.let { dir ->
                saveImage(dir, playlist.id.toString(), uri)
            }
        }
    }
}