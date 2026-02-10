package com.example.verstka_last.playlist.ui.fragment

import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.R
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.playlist.ui.viewmodel.PlaylistEditorViewModel
import com.example.verstka_last.playlist_create.presentation.fragment.PlaylistCreatorFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistEditorFragment : PlaylistCreatorFragment() {

    private val editViewModel: PlaylistEditorViewModel by viewModel()
    private var currentPlaylist: Playlist? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currentPlaylist = arguments?.getSerializable("playlist") as? Playlist
        if (currentPlaylist == null) {
            findNavController().popBackStack()
            return
        }

        editViewModel.initEditMode(currentPlaylist!!)

        imagesDir = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist_covers"
        )

        setupUI()
        setupImagePicker()
        setupTextWatchers()
        setupButtonListeners()
        setupBackPressHandler()
        observeViewModel()
    }

    private fun setupUI() {
        binding.backButton.findViewById<TextView>(R.id.playlist_main).text = getString(R.string.edit_playlist)
        binding.createButton.text = getString(R.string.playlist_save)

        val coverFile = File(imagesDir, "${currentPlaylist!!.id}.jpg")
        if (coverFile.exists()) {
            Glide.with(requireContext())
                .load(coverFile)
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder)
                .transform(
                    CenterCrop(), RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius))
                )
                .into(binding.playListImage)
        }

        binding.nameET.setText(currentPlaylist!!.title)
        binding.descriptionET.setText(currentPlaylist!!.description)
        binding.createButton.isEnabled = currentPlaylist!!.title.isNotBlank()
    }

    override fun setupTextWatchers() {
        binding.nameET.doOnTextChanged { text, _, _, _ ->
            val name = text?.toString() ?: ""
            editViewModel.setPlaylistName(name)
            binding.createButton.isEnabled = name.isNotBlank()
        }

        binding.descriptionET.doOnTextChanged { text, _, _, _ ->
            editViewModel.setDescription(text?.toString() ?: "")
        }
    }

    override fun setupButtonListeners() {
        binding.createButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val playlistId = editViewModel.createPlaylist(imagesDir)
                    if (playlistId != -1L) {
                        findNavController().popBackStack()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        )
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            editViewModel.selectedImage.collect { uri ->
                uri?.let { loadImageWithGlide(it.toString()) }
            }
        }
    }
}