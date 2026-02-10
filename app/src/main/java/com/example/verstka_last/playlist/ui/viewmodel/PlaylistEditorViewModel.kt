package com.example.verstka_last.playlist.ui.viewmodel

import android.net.Uri
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.playlist_create.domain.PlaylistCreatorInteractor
import com.example.verstka_last.playlist_create.presentation.viewmodel.PlaylistCreatorViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class PlaylistEditorViewModel(
    private val interactor: PlaylistCreatorInteractor
) : PlaylistCreatorViewModel(interactor) {

    private var currentPlaylist: Playlist? = null
    private var originalName = ""
    private var originalDescription = ""

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges.asStateFlow()

    fun initEditMode(playlist: Playlist) {
        currentPlaylist = playlist
        originalName = playlist.title
        originalDescription = playlist.description

        setPlaylistName(playlist.title)
        setDescription(playlist.description)
        updateHasChanges()
    }

    private fun hasChangesInternal(): Boolean {
        return getPlaylistName() != originalName ||
                getDescription() != originalDescription ||
                selectedImage.value != null
    }

    private fun updateHasChanges() {
        _hasChanges.value = hasChangesInternal()
    }

    override fun setPlaylistName(name: String) {
        super.setPlaylistName(name)
        updateHasChanges()
    }

    override fun setDescription(description: String) {
        super.setDescription(description)
        updateHasChanges()
    }

    override fun setSelectedImageUri(uri: Uri?) {
        super.setSelectedImageUri(uri)
        updateHasChanges()
    }

    override suspend fun createPlaylist(imagesDir: File?): Long {
        currentPlaylist?.let { playlist ->
            val updatedPlaylist = playlist.copy(
                title = getPlaylistName(),
                description = getDescription()
            )

            return try {
                interactor.updatePlaylist(
                    playlist = updatedPlaylist,
                    newImageUri = selectedImage.value,
                    imagesDir = imagesDir
                )
                playlist.id
            } catch (e: Exception) {
                -1L
            }
        }

        return super.createPlaylist(imagesDir)
    }
}