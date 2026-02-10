package com.example.verstka_last.playlist.ui.fragment

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.R
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.playlist.ui.viewmodel.PlaylistEditorViewModel
import com.example.verstka_last.playlist_create.presentation.fragment.PlaylistCreatorFragment
import com.example.verstka_last.playlist_create.presentation.viewmodel.PlaylistCreationState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistEditorFragment : PlaylistCreatorFragment() {

    private val editViewModel: PlaylistEditorViewModel by viewModel()
    private var currentPlaylist: Playlist? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("PlaylistEditor", "onViewCreated called")

        currentPlaylist = arguments?.getSerializable("playlist") as? Playlist
        if (currentPlaylist == null) {
            Toast.makeText(requireContext(), "Ошибка загрузки плейлиста", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        Log.d("PlaylistEditor", "Playlist loaded: ${currentPlaylist!!.title}")

        // Инициализируем ViewModel данными плейлиста
        editViewModel.initEditMode(currentPlaylist!!)

        imagesDir = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist_covers"
        )

        Log.d("PlaylistEditor", "Setting up UI")
        setupUI()
        setupImagePicker()
        setupTextWatchers()
        setupButtonListeners()
        setupBackPressHandler()
        observeViewModel()
    }

    private fun setupUI() {
        // Меняем заголовок экрана
        binding.backButton.findViewById<TextView>(R.id.playlist_main).text = getString(R.string.edit_playlist)

        // Меняем текст кнопки создания
        binding.createButton.text = getString(R.string.playlist_save)

        // Загружаем текущую обложку плейлиста
        val coverFile = File(imagesDir, "${currentPlaylist!!.id}.jpg")
        if (coverFile.exists()) {
            Glide.with(requireContext())
                .load(coverFile)
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder)
                .transform(CenterCrop(), RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius)))
                .into(binding.playListImage)
        }

        // Заполняем поля данными плейлиста
        binding.nameET.setText(currentPlaylist!!.title)
        binding.descriptionET.setText(currentPlaylist!!.description)

        // Активируем кнопку, так как название уже есть
        binding.createButton.isEnabled = currentPlaylist!!.title.isNotBlank()
    }

    override fun setupTextWatchers() {
        Log.d("PlaylistEditor", "Setting up text watchers")

        binding.nameET.doOnTextChanged { text, _, _, _ ->
            val name = text?.toString() ?: ""
            Log.d("PlaylistEditor", "Name changed to: $name")
            editViewModel.setPlaylistName(name)
            binding.createButton.isEnabled = name.isNotBlank()
        }

        binding.descriptionET.doOnTextChanged { text, _, _, _ ->
            val description = text?.toString() ?: ""
            Log.d("PlaylistEditor", "Description changed to: $description")
            editViewModel.setDescription(description)
        }
    }

    override fun setupButtonListeners() {
        Log.d("PlaylistEditor", "Setting up button listeners")

        binding.createButton.setOnClickListener {
            Log.d("PlaylistEditor", "Save button clicked")

            val playlistName = editViewModel.getPlaylistName()
            if (playlistName.isBlank()) {
                Toast.makeText(requireContext(), "Название плейлиста не может быть пустым", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    Log.d("PlaylistEditor", "Calling editViewModel.createPlaylist()")
                    val playlistId = editViewModel.createPlaylist(imagesDir)

                    if (playlistId != -1L) {
                        Log.d("PlaylistEditor", "Playlist updated successfully with ID: $playlistId")
                        // Сразу закрываем фрагмент после успешного сохранения
                        findNavController().popBackStack()
                    } else {
                        Log.e("PlaylistEditor", "Failed to update playlist")
                        Toast.makeText(
                            requireContext(),
                            "Ошибка при обновлении плейлиста",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.e("PlaylistEditor", "Error updating playlist: ${e.message}", e)
                    Toast.makeText(
                        requireContext(),
                        "Ошибка: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.backButton.setOnClickListener {
            Log.d("PlaylistEditor", "Back button clicked")
            findNavController().navigateUp()
        }
    }

    override fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d("PlaylistEditor", "System back pressed")
                    findNavController().navigateUp()
                }
            }
        )
    }

    override fun observeViewModel() {
        Log.d("PlaylistEditor", "Setting up ViewModel observers")

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Запускаем два параллельных collect в разных корутинах
                launch {
                    editViewModel.selectedImage.collect { uri ->
                        Log.d("PlaylistEditor", "Selected image URI: $uri")
                        uri?.let {
                            loadImageWithGlide(it.toString())
                        }
                    }
                }

                launch {
                    editViewModel.creationState.collect { state ->
                        Log.d("PlaylistEditor", "Creation state changed: $state")
                        when (state) {
                            is PlaylistCreationState.Success -> {
                                Log.d("PlaylistEditor", "Success! Playlist ID: ${state.playlistId}")
                                // НЕ закрываем здесь, так как уже закрыли в setupButtonListeners
                                // findNavController().popBackStack()
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}