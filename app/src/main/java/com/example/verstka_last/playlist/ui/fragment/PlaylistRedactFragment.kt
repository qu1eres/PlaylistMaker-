package com.example.verstka_last.playlist.ui.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.R
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.databinding.FragmentPlaylistViewBinding
import com.example.verstka_last.playlist.ui.TracksInPlayListAdapter
import com.example.verstka_last.playlist.ui.viewmodel.PlaylistRedactViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistRedactFragment : Fragment() {

    private var _binding: FragmentPlaylistViewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistRedactViewModel by viewModel()
    private lateinit var imagesDir: File
    private lateinit var tracksAdapter: TracksInPlayListAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var playlistId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imagesDir = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist_covers"
        )

        playlistId = arguments?.getLong("playlist_id", -1) ?: -1
        if (playlistId == -1L) {
            findNavController().popBackStack()
            return
        }

        setupBottomSheet()
        setupAdapters()
        setupClickListeners()
        setupObservers()

        viewModel.initPlaylist(playlistId)
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            peekHeight = resources.displayMetrics.heightPixels / 4
        }
    }

    private fun setupAdapters() {
        tracksAdapter = TracksInPlayListAdapter(
            clickListener = { track ->
                navigateToPlayer(track)
            },
            longClickListener = { track ->
                showDeleteTrackDialog(track)
            }
        )

        binding.track.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tracksAdapter
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.shareButton.setOnClickListener {

        }

        binding.menuButton.setOnClickListener {

        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.playlist.collectLatest { playlist ->
                playlist?.let { updatePlaylistUI(it) }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tracks.collectLatest { tracks ->
                updateTracksUI(tracks)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.totalDuration.collectLatest { duration ->
                binding.tracksTime.text = duration
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.trackCount.collectLatest { count ->
                binding.trackCount.text = count
            }
        }
    }

    private fun updatePlaylistUI(playlist: Playlist) {
        binding.title.text = playlist.title
        binding.description.text = playlist.description
        binding.description.isVisible = playlist.description.isNotEmpty()
        val timeText = viewModel.getPlaylistTime(playlist.tracks)
        binding.tracksTime.text = requireActivity().resources.getQuantityString(
            R.plurals.minutes,
            timeText, timeText
        )

        val coverFile = File(imagesDir, "${playlist.id}.jpg")
        if (coverFile.exists()) {
            Glide.with(requireContext())
                .load(coverFile)
                .placeholder(R.drawable.ic_placeholder)
                .transform(
                    CenterCrop())
                .into(binding.cover)
        } else {
            binding.cover.setImageResource(R.drawable.ic_placeholder)
        }
    }

    private fun updateTracksUI(tracks: List<Track>) {
        tracksAdapter.updateData(tracks)
        val isEmpty = tracks.isEmpty()
        binding.emptyPlaylistMessage.isVisible = isEmpty
        binding.track.isVisible = !isEmpty

        if (isEmpty) {
            binding.emptyPlaylistMessage.text = getString(R.string.empty_playlist)
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        val typedArray = requireContext().theme.obtainStyledAttributes(
            intArrayOf(R.attr.settingsTextColor)
        )
        val buttonColor = typedArray.getColor(0, Color.BLACK)
        typedArray.recycle()

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_track)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.removeTrack(track)
            }
            .create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(buttonColor)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(buttonColor)
    }

    private fun navigateToPlayer(track: Track) {
        val bundle = Bundle().apply {
            putSerializable("track", track)
        }
        findNavController().navigate(R.id.action_playlistRedactFragment_to_playerFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}