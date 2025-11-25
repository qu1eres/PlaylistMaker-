package com.example.verstka_last.search.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.verstka_last.databinding.ActivitySearchBinding
import com.example.verstka_last.player.ui.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()
        setupClickListeners()
        setupTextWatchers()
        setupObservers()

        viewModel.loadInitialState()
    }

    private fun setupRecyclerViews() {
        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(emptyList())
        binding.tracksRecyclerView.adapter = adapter

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
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
        binding.backButton.setOnClickListener {
            finish()
        }

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
        viewModel.searchState.observe(this) { state ->
            renderState(state)
        }

        viewModel.currentSearchText.observe(this) { text ->
            if (binding.inputEditText.text?.toString() != text) {
                binding.inputEditText.setText(text)
            }
        }

        viewModel.navigationEvent.observe(this) { event ->
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

    private fun openPlayer(track: com.example.verstka_last.core.domain.models.Track) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("track", track)
        }
        startActivity(intent)
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}