package com.example.verstka_last.playlist_create.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.playlist_create.domain.PlaylistCreatorInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

open class PlaylistCreatorViewModel(private val interactor: PlaylistCreatorInteractor) : ViewModel() {

    private val _playlistName = MutableStateFlow("")
    private val _description = MutableStateFlow("")
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)

    val selectedImage: StateFlow<Uri?> = _selectedImageUri.asStateFlow()
    private val _hasUnsavedChanges = MutableStateFlow(false)
    val hasUnsavedChanges: StateFlow<Boolean> = _hasUnsavedChanges.asStateFlow()
    private val _creationState = MutableStateFlow<PlaylistCreationState>(PlaylistCreationState.Idle)
    val creationState: StateFlow<PlaylistCreationState> = _creationState.asStateFlow()

    open fun setPlaylistName(name: String) {
        _playlistName.value = name
        updateHasUnsavedChanges()
    }

    open fun setDescription(description: String) {
        _description.value = description
        updateHasUnsavedChanges()
    }

    open fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
        updateHasUnsavedChanges()
    }

    fun getPlaylistName(): String = _playlistName.value
    fun getDescription(): String = _description.value

    private fun updateHasUnsavedChanges() {
        _hasUnsavedChanges.value = _playlistName.value.isNotBlank() ||
                _description.value.isNotBlank() ||
                _selectedImageUri.value != null
    }

    open suspend fun createPlaylist(imagesDir: File? = null): Long {
        val playlistId = interactor.savePlaylist(
            playlistName = _playlistName.value,
            description = _description.value,
            fileDir = ""
        )

        if (playlistId == -1L) return -1L

        _selectedImageUri.value?.let { uri ->
            imagesDir?.let { dir ->
                val savedPath = interactor.saveImage(
                    filePath = dir,
                    savePlaylist = playlistId.toString(),
                    uri = uri
                )
                if (savedPath != null) {
                    val updatedPlaylist = Playlist(
                        id = playlistId,
                        title = _playlistName.value,
                        description = _description.value,
                        imagePath = savedPath,
                        trackCount = 0,
                        tracks = mutableListOf()
                    )
                    interactor.updatePlaylist(updatedPlaylist, newImageUri = null, imagesDir = dir)
                }
            }
        }

        _creationState.value = PlaylistCreationState.Success(playlistId)
        return playlistId
    }
}

sealed class PlaylistCreationState {
    object Idle : PlaylistCreationState()
    data class Success(val playlistId: Long) : PlaylistCreationState()
}