package com.example.verstka_last.playlist_create.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.verstka_last.R
import com.example.verstka_last.databinding.FragmentPlaylistCreateBinding
import com.example.verstka_last.media.presentation.playlist.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlaylistCreatorFragment : Fragment(R.layout.fragment_playlist_create) {

    private var _binding: FragmentPlaylistCreateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaylistCreateBinding.bind(view)

        setupClickListeners()
        setupTextWatchers()
    }

    private fun setupClickListeners() {
        binding.createButton.setOnClickListener {
            createPlaylist()
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupTextWatchers() {
        binding.nameET.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.createButton.isEnabled = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun createPlaylist() {
        TODO("Логика создания плейлиста")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}