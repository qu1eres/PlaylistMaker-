package com.example.verstka_last.media.presentation.playlist.fragments

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.verstka_last.R
import com.example.verstka_last.databinding.FragmentPlaylistsBinding
import com.example.verstka_last.media.presentation.playlist.PlayListAdapter
import com.example.verstka_last.media.presentation.playlist.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistsFragment : Fragment(R.layout.fragment_playlists) {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var adapter: PlayListAdapter
    private lateinit var imagesDir: File

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaylistsBinding.bind(view)

        imagesDir = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist_covers"
        )

        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        setupFragmentResultListener()
        showEmptyState()
        viewModel.loadPlaylists()
    }

    private fun setupRecyclerView() {
        adapter = PlayListAdapter(imagesDir) { playlist -> }

        binding.playlistRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistRecyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.addButton.setOnClickListener {
            openPlaylistCreator()
        }
    }

    private fun setupObservers() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            adapter.updateData(playlists)

            if (playlists.isEmpty()) {
                showEmptyState()
            } else {
                showPlaylists()
            }
        }
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener("playlist_created") { _, _ ->
            viewModel.loadPlaylists()
        }
    }

    private fun showEmptyState() {
        binding.addButton.visibility = View.VISIBLE
        binding.messageImage.visibility = View.VISIBLE
        binding.messageText.visibility = View.VISIBLE
        binding.playlistRecyclerView.visibility = View.GONE
    }

    private fun showPlaylists() {
        binding.addButton.visibility = View.VISIBLE
        binding.messageImage.visibility = View.GONE
        binding.messageText.visibility = View.GONE
        binding.playlistRecyclerView.visibility = View.VISIBLE
    }

    private fun openPlaylistCreator() {
        findNavController().navigate(R.id.action_libraryFragment_to_playlistCreatorFragment)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}