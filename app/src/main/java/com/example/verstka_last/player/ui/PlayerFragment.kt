package com.example.verstka_last.player.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.R
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.databinding.FragmentPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var playlistsAdapter: MiniPlayListAdapter
    private var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? = null

    private val track: Track by lazy {
        arguments?.getSerializable("track") as? Track ?: throw IllegalArgumentException("Трек не передан")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayerBinding.bind(view)

        setupUI(track)
        setupBottomSheet()
        setupPlaylistsRecyclerView()

        if (savedInstanceState == null) {
            viewModel.preparePlayer(track)
            viewModel.loadPlaylists()
        }

        setupClickListeners()
        setupObservers()
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    _binding?.let { binding ->
                        when (newState) {
                            BottomSheetBehavior.STATE_HIDDEN -> {
                                binding.overlay.visibility = View.GONE
                            }
                            else -> {
                                binding.overlay.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    _binding?.let { binding ->
                        val alpha = when {
                            slideOffset < 0 -> 0f
                            slideOffset > 1 -> 1f
                            else -> slideOffset
                        }
                        binding.overlay.alpha = alpha
                    }
                }
            }

            bottomSheetCallback?.let { addBottomSheetCallback(it) }
        }

        binding.overlay.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    private fun setupPlaylistsRecyclerView() {
        playlistsAdapter = MiniPlayListAdapter { playlist ->
            viewModel.addTrackToPlaylist(playlist)
        }

        binding.playlistsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistsAdapter
        }
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
            viewModel.onFavoriteClicked()
        }

        binding.playlist.setOnClickListener {
            showPlaylistsBottomSheet()
        }

        binding.createNewPlaylistButton.setOnClickListener {
            navigateToPlaylistCreator()
        }
    }

    private fun setupObservers() {
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        lifecycleScope.launch {
            viewModel.playlists.collectLatest { playlists ->
                playlistsAdapter.updateData(playlists)
            }
        }

        viewModel.playlistActionResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                when (it) {
                    is PlaylistActionResult.Added -> {
                        showToast(getString(R.string.added_to_playlist, it.playlistName))
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        viewModel.loadPlaylists()
                    }
                    is PlaylistActionResult.AlreadyExists -> {
                        showToast(getString(R.string.already_in_playlist, it.playlistName))
                    }
                    PlaylistActionResult.Created -> {
                        showToast(getString(R.string.playlist_added_success))
                    }
                }
                viewModel.clearPlaylistActionResult()
            }
        }
    }

    private fun showPlaylistsBottomSheet() {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
    }

    private fun navigateToPlaylistCreator() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        parentFragmentManager.setFragmentResultListener(
            "playlist_created",
            viewLifecycleOwner
        ) { _, _ ->

            viewModel.setPlaylistCreated()
        }

        findNavController().navigate(R.id.action_playerFragment_to_playlistCreatorFragment)
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun render(state: PlayerScreenState) {
        updateFavoriteButton(state.isFavorite)

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
        track.isFavorite = isFavorite
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

        Glide.with(requireContext())
            .load(track.getHighResArtworkUrl())
            .placeholder(R.drawable.ic_album_placeholder)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(binding.trackImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        bottomSheetCallback?.let {
            bottomSheetBehavior.removeBottomSheetCallback(it)
        }
        bottomSheetCallback = null

        _binding = null
    }
}