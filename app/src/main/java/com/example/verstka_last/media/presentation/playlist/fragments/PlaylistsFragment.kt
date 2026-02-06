package com.example.verstka_last.media.presentation.playlist.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.verstka_last.R
import com.example.verstka_last.databinding.FragmentPlaylistsBinding
import com.example.verstka_last.media.presentation.playlist.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment(R.layout.fragment_playlists) {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaylistsBinding.bind(view)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.addButton.setOnClickListener {
            openPlaylistCreator()
        }
    }

    private fun openPlaylistCreator() {
        findNavController().navigate(R.id.action_libraryFragment_to_playlistCreatorFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}