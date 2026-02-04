package com.example.verstka_last.media.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.verstka_last.R
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.databinding.FragmentFavoritesBinding
import com.example.verstka_last.media.presentation.FavoriteState
import com.example.verstka_last.media.presentation.viewmodel.FavoritesViewModel
import com.example.verstka_last.search.presentation.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.navigation.findNavController

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModel()
    private lateinit var adapter: TrackAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoritesBinding.bind(view)

        setupRecyclerView()
        setupObservers()

        viewModel.fillData()
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(
            tracks = mutableListOf(),
            onItemClickListener = { track ->
                navigateToPlayer(track)
            }
        )

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun navigateToPlayer(track: Track) {
        val bundle = Bundle().apply {
            putSerializable("track", track)
        }

        parentFragment?.requireView()?.findNavController()
            ?.navigate(R.id.action_libraryFragment_to_playerFragment, bundle)
    }

    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.Content -> showContent(state.tracks)
            is FavoriteState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        binding.favoritesRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE
    }

    private fun showContent(tracks: List<Track>) {
        binding.favoritesRecyclerView.visibility = View.VISIBLE
        binding.emptyStateLayout.visibility = View.GONE

        adapter.updateTracks(tracks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}