package com.example.verstka_last.player.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.R
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.databinding.FragmentPlayerBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel()

    private val track: Track by lazy {
        arguments?.getSerializable("track") as? Track ?: throw IllegalArgumentException("Трек не передан")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayerBinding.bind(view)
        viewModel.getChecked(track)

        setupUI(track)

        if (savedInstanceState == null) {
            viewModel.preparePlayer(track.previewUrl)
        }

        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.play.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.playButtonActive.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.toolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.favourite.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }
    }

    private fun setupObservers() {
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.observeFavoriteState().observe(viewLifecycleOwner) { isFavorite ->
            updateFavoriteButton(isFavorite)
            track.isFavorite = isFavorite
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun render(state: PlayerScreenState) {
        when (state.playerState) {
            PlayerState.Default -> {
                binding.play.isVisible = true
                binding.playButtonActive.isVisible = false
                binding.play.isEnabled = true
            }
            PlayerState.Prepared -> {
                binding.play.isVisible = true
                binding.playButtonActive.isVisible = false
                binding.play.isEnabled = true
            }
            PlayerState.Playing -> {
                binding.play.isVisible = false
                binding.playButtonActive.isVisible = true
                binding.playButtonActive.isEnabled = true
            }
            PlayerState.Paused -> {
                binding.play.isVisible = true
                binding.playButtonActive.isVisible = false
                binding.play.isEnabled = true
            }
        }

        binding.currentPlayTime.text = state.currentTime
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        val iconRes = if (isFavorite) {
            R.drawable.ic_favorites_button_active
        } else {
            R.drawable.ic_button_add_favorits
        }
        binding.favourite.setImageResource(iconRes)
    }

    private fun setupUI(track: Track) {
        binding.trackName.text = track.title
        binding.artistName.text = track.artist
        binding.trackDuration.text = track.duration

        if (track.collectionName != null) {
            binding.albumName.text = track.collectionName
            binding.albumName.isVisible = track.collectionName.isNotEmpty()
        } else {
            binding.albumName.isVisible = false
        }

        binding.year.text = track.getReleaseYear() ?: ""
        binding.genreName.text = track.primaryGenreName
        binding.country.text = track.country

        updateFavoriteButton(track.isFavorite)

        Glide.with(requireContext())
            .load(track.getHighResArtworkUrl())
            .placeholder(R.drawable.ic_album_placeholder)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(binding.trackImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}