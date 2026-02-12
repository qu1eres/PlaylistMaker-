package com.example.verstka_last.playlist.ui.viewmodel

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.playlist_create.domain.PlaylistCreatorInteractor
import com.example.verstka_last.playlist_create.presentation.viewmodel.PlaylistCreatorViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class PlaylistEditorUiState(
    val playlistName: String = "",
    val playlistDescription: String = "",
    val coverUri: Uri? = null,
    val isSaveButtonEnabled: Boolean = false
)

class PlaylistEditorViewModel(
    private val interactor: PlaylistCreatorInteractor
) : PlaylistCreatorViewModel(interactor) {

    private var currentPlaylist: Playlist? = null
    private var originalName = ""
    private var originalDescription = ""
    private var originalImagePath: String? = null

    private val _hasChanges = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(PlaylistEditorUiState())
    val uiState: StateFlow<PlaylistEditorUiState> = _uiState.asStateFlow()

    fun initEditMode(playlistId: Long, imagesDir: File) {
        viewModelScope.launch {
            val playlist = interactor.getPlaylist(playlistId)
            currentPlaylist = playlist
            originalName = playlist.title
            originalDescription = playlist.description
            originalImagePath = playlist.imagePath

            super.setPlaylistName(playlist.title)
            super.setDescription(playlist.description)

            val coverUri = playlist.imagePath.takeIf { it.isNotBlank() }?.let { File(it).toUri() }
                ?: File(imagesDir, "${playlist.id}.jpg").takeIf { it.exists() }?.toUri()

            updateUiState(
                name = playlist.title,
                description = playlist.description,
                coverUri = coverUri
            )
            updateHasChanges()
        }
    }

    private fun hasChangesInternal(): Boolean =
        getPlaylistName() != originalName ||
                getDescription() != originalDescription ||
                selectedImage.value != null

    private fun updateHasChanges() {
        _hasChanges.value = hasChangesInternal()
    }

    override fun setPlaylistName(name: String) {
        super.setPlaylistName(name)
        updateHasChanges()
        updateUiState(name = name)
    }

    override fun setDescription(description: String) {
        super.setDescription(description)
        updateHasChanges()
        updateUiState(description = description)
    }

    override fun setSelectedImageUri(uri: Uri?) {
        super.setSelectedImageUri(uri)
        updateHasChanges()
        updateUiState(coverUri = uri)
    }

    override suspend fun createPlaylist(imagesDir: File?): Long {
        if (imagesDir == null) return -1L
        val playlist = currentPlaylist ?: return -1L

        val updatedPlaylist = playlist.copy(
            title = getPlaylistName(),
            description = getDescription()
        )

        val success = interactor.updatePlaylist(
            playlist = updatedPlaylist,
            newImageUri = selectedImage.value,
            imagesDir = imagesDir
        )
        return if (success) playlist.id else -1L
    }

    private fun updateUiState(
        name: String = _uiState.value.playlistName,
        description: String = _uiState.value.playlistDescription,
        coverUri: Uri? = _uiState.value.coverUri
    ) {
        _uiState.update {
            it.copy(
                playlistName = name,
                playlistDescription = description,
                coverUri = coverUri,
                isSaveButtonEnabled = name.isNotBlank() && hasChangesInternal()
            )
        }
    }
}