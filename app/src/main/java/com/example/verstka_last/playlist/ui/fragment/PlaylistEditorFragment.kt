package com.example.verstka_last.playlist.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.R
import com.example.verstka_last.playlist.ui.viewmodel.PlaylistEditorUiState
import com.example.verstka_last.playlist.ui.viewmodel.PlaylistEditorViewModel
import com.example.verstka_last.playlist_create.presentation.fragment.PlaylistCreatorFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistEditorFragment : PlaylistCreatorFragment() {

    private val editViewModel: PlaylistEditorViewModel by viewModel {
        parametersOf(requireArguments().getLong("playlistId"))
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { editViewModel.setSelectedImageUri(it) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = requireArguments().getLong("playlistId")
        editViewModel.initEditMode(playlistId, imagesDir)

        binding.backButton.findViewById<android.widget.TextView>(R.id.playlist_main).text = getString(R.string.edit_playlist)
        binding.createButton.text = getString(R.string.playlist_save)

        setupImagePicker()
        setupTextWatchers()
        setupButtonListeners()
        setupBackPressHandler()
        observeViewModel()
    }

    override fun setupImagePicker() {
        binding.playListImage.setOnClickListener {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    override fun setupTextWatchers() {
        binding.nameET.doOnTextChanged { text, _, _, _ ->
            editViewModel.setPlaylistName(text?.toString() ?: "")
        }
        binding.descriptionET.doOnTextChanged { text, _, _, _ ->
            editViewModel.setDescription(text?.toString() ?: "")
        }
    }

    override fun setupButtonListeners() {
        binding.createButton.setOnClickListener {
            lifecycleScope.launch {
                editViewModel.createPlaylist(imagesDir)
                findNavController().popBackStack()
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (editViewModel.hasUnsavedChanges.value) {
                showBackConfirmationDialog()
            } else {
                findNavController().navigateUp()
            }
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            editViewModel.uiState.collectLatest { state ->
                renderUiState(state)
            }
        }
    }

    private fun renderUiState(state: PlaylistEditorUiState) {
        if (binding.nameET.text.toString() != state.playlistName) {
            binding.nameET.setText(state.playlistName)
        }
        if (binding.descriptionET.text.toString() != state.playlistDescription) {
            binding.descriptionET.setText(state.playlistDescription)
        }

        binding.createButton.isEnabled = state.isSaveButtonEnabled

        if (state.coverUri != null) {
            Glide.with(requireContext())
                .load(state.coverUri)
                .transform(
                    CenterCrop(),
                    RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius))
                )
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.playListImage)
        } else {
            binding.playListImage.setImageResource(R.drawable.ic_placeholder)
        }
    }
}