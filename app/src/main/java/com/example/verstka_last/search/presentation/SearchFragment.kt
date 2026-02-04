package com.example.verstka_last.search.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.verstka_last.R
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.databinding.FragmentSearchBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModel()

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        setupRecyclerViews()
        setupClickListeners()
        setupTextWatchers()
        setupObservers()

        viewModel.loadInitialState()
    }

    private fun setupRecyclerViews() {
        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TrackAdapter(emptyList())
        binding.tracksRecyclerView.adapter = adapter

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = TrackAdapter(emptyList())
        binding.historyRecyclerView.adapter = historyAdapter

        adapter.setOnItemClickListener { track ->
            viewModel.onTrackClick(track)
        }

        historyAdapter.setOnItemClickListener { track ->
            viewModel.onTrackClick(track)
        }
    }

    private fun setupClickListeners() {
        binding.clearIcon.setOnClickListener {
            viewModel.onClearClick()
            hideKeyboard(binding.inputEditText)
            binding.inputEditText.clearFocus()
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.onClearHistoryClick()
        }

        binding.retryButton.setOnClickListener {
            viewModel.onRetryClick()
        }

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onSearchAction()
                hideKeyboard(binding.inputEditText)
                true
            } else {
                false
            }
        }
    }

    private fun setupTextWatchers() {
        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onSearchFocusChanged(hasFocus)
        }

        binding.inputEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                viewModel.onSearchTextChanged(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun setupObservers() {
        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        viewModel.currentSearchText.observe(viewLifecycleOwner) { text ->
            if (binding.inputEditText.text?.toString() != text) {
                binding.inputEditText.setText(text)
            }
        }

        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            event?.let {
                when (it) {
                    is SearchViewModel.NavigationEvent.OpenPlayer -> openPlayer(it.track)
                }
                viewModel.onNavigationHandled()
            }
        }
    }

    private fun renderState(state: SearchState) {
        binding.progressBar.isVisible = state is SearchState.Loading
        binding.tracksRecyclerView.isVisible = state is SearchState.Results
        binding.emptyState.isVisible = state is SearchState.Empty
        binding.errorState.isVisible = state is SearchState.Error
        binding.historyScrollView.isVisible = state is SearchState.History

        when (state) {
            is SearchState.History -> historyAdapter.updateTracks(state.tracks)
            is SearchState.Results -> adapter.updateTracks(state.tracks)
            else -> {  }
        }
    }

    private fun openPlayer(track: Track) {
        val bundle = Bundle().apply {
            putSerializable("track", track)
        }
        findNavController().navigate(R.id.action_searchFragment_to_playerFragment, bundle)
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}