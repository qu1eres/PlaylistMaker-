package com.example.verstka_last.playlist_create.presentation.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.R
import com.example.verstka_last.databinding.FragmentPlaylistCreateBinding
import com.example.verstka_last.playlist_create.presentation.viewmodel.PlaylistCreationState
import com.example.verstka_last.playlist_create.presentation.viewmodel.PlaylistCreatorViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistCreatorFragment : Fragment() {

    private var _binding: FragmentPlaylistCreateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistCreatorViewModel by viewModel()

    private lateinit var imagesDir: File

    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            loadImageWithGlide(it.toString())
            viewModel.setSelectedImageUri(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagesDir = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist_covers"
        )

        setupImagePicker()
        setupTextWatchers()
        setupButtonListeners()
        setupBackPressHandler()
        observeViewModel()
    }

    private fun setupImagePicker() {
        binding.playListImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setupTextWatchers() {
        binding.nameET.doOnTextChanged { text, _, _, _ ->
            viewModel.setPlaylistName(text?.toString() ?: "")
            binding.createButton.isEnabled = !text.isNullOrBlank()
        }

        binding.descriptionET.doOnTextChanged { text, _, _, _ ->
            viewModel.setDescription(text?.toString() ?: "")
        }
    }

    private fun setupButtonListeners() {
        binding.createButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.createPlaylist(imagesDir)
            }
        }

        binding.backButton.setOnClickListener {
            handleBackNavigation()
        }
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackNavigation()
                }
            }
        )
    }

    private fun handleBackNavigation() {
        if (viewModel.hasUnsavedChanges.value) {
            showBackConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showBackConfirmationDialog() {
        // Я не знаю, как я ещё должен был изменить цвет конкретно этих кнопок. К сожалению у меня цветовые приколы в приложении
        // сложились так, что обыденные методы не доступны и для их использования пришлось бы полностью менять всю логику подбора
        // цветов для всего в приложении. Я это делать не собираюсь, так что пусть будет так.
        val typedArray = requireContext().theme.obtainStyledAttributes(
            intArrayOf(R.attr.settingsTextColor)
        )
        val buttonColor = typedArray.getColor(0, Color.BLACK)
        typedArray.recycle()

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.back_alert_title)
            .setMessage(R.string.back_alert_message)
            .setNegativeButton(R.string.negative_button, null)
            .setPositiveButton(R.string.positive_button) { _, _ ->
                findNavController().navigateUp()
            }
            .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(buttonColor)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(buttonColor)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.selectedImage.collect { uri ->
                        uri?.let { loadImageWithGlide(it.toString()) }
                    }
                }

                launch {
                    viewModel.creationState.collect { state ->
                        when (state) {
                            is PlaylistCreationState.Success -> {
                                showSuccessToast(state.playlistId)
                                parentFragmentManager.setFragmentResult(
                                    "playlist_created",
                                    Bundle().apply {
                                        putLong("playlist_id", state.playlistId)
                                    }
                                )
                                findNavController().navigateUp()
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun loadImageWithGlide(imageUri: String) {
        Glide.with(this)
            .load(imageUri)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .transform(CenterCrop(), RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius)))
            .into(binding.playListImage)
    }

    private fun showSuccessToast(playlistId: Long) {
        val message = getString(R.string.playlist_created, viewModel.getPlaylistName())
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}