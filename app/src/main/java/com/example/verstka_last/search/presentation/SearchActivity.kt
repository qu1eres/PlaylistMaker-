package com.example.verstka_last.search.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.verstka_last.creator.Creator
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.databinding.ActivitySearchBinding
import com.example.verstka_last.player.ui.PlayerActivity
import com.example.verstka_last.search.domain.api.SearchHistoryInteractor
import com.example.verstka_last.search.domain.api.TracksInteractor

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }
    private var isClickAllowed = true
    private var interactor: TracksInteractor = Creator.provideTracksInteractor()
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    private var currentSearchText: String = ""
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)

        setupRecyclerViews()
        setupClickListeners()
        setupTextWatchers()

        updateHistoryVisibility()
    }

    private fun setupRecyclerViews() {
        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(emptyList())
        binding.tracksRecyclerView.adapter = adapter

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter(emptyList())
        binding.historyRecyclerView.adapter = historyAdapter

        adapter.setOnItemClickListener { track ->
            searchHistoryInteractor.saveTrack(track)
            if (clickDebounce()) {
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra("track", track)
                }
                startActivity(intent)
            }
        }

        historyAdapter.setOnItemClickListener { track ->
            searchHistoryInteractor.saveTrack(track)
            if (clickDebounce()) {
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra("track", track)
                }
                startActivity(intent)
            }
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            hideKeyboard(binding.inputEditText)
            binding.inputEditText.clearFocus()
            showInitialState()
        }

        binding.clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            updateHistoryVisibility()
        }

        binding.retryButton.setOnClickListener {
            performSearch()
        }

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                hideKeyboard(binding.inputEditText)
                true
            }
            false
        }
    }

    private fun setupTextWatchers() {
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = clearButtonVisibility(s)
                currentSearchText = s.toString()
                updateHistoryVisibility()
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputEditText.addTextChangedListener(simpleTextWatcher)

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            updateHistoryVisibility()
        }
    }

    private fun updateHistoryVisibility() {
        val hasFocus = binding.inputEditText.hasFocus()
        val isEmpty = binding.inputEditText.text.isEmpty()
        val history = searchHistoryInteractor.loadHistory()

        if (hasFocus && isEmpty && history.isNotEmpty()) {
            binding.historyScrollView.isVisible = true
            binding.tracksRecyclerView.isVisible = false
            binding.emptyState.isVisible = false
            binding.errorState.isVisible = false

            historyAdapter.updateTracks(history)
        } else {
            binding.historyScrollView.isVisible = false
        }
    }

    private fun performSearch() {
        val query = binding.inputEditText.text.toString().trim()
        if (query.isEmpty()) {
            showInitialState()
            return
        }

        binding.tracksRecyclerView.isVisible = false
        binding.emptyState.isVisible = false
        binding.errorState.isVisible = false
        binding.historyLayout.isVisible = false
        binding.progressBar.isVisible = true

        interactor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                runOnUiThread {
                    binding.progressBar.isVisible = false
                    if (foundTracks.isEmpty()) {
                        showEmptyState()
                    } else {
                        showResults(foundTracks)
                    }
                }
            }
        })
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showInitialState() {
        binding.tracksRecyclerView.isVisible = false
        binding.emptyState.isVisible = false
        binding.errorState.isVisible = false
        updateHistoryVisibility()
    }

    private fun showResults(tracks: List<Track>) {
        adapter.updateTracks(tracks)
        binding.tracksRecyclerView.isVisible = true
        binding.emptyState.isVisible = false
        binding.errorState.isVisible = false
        binding.historyLayout.isVisible = false
    }

    private fun showEmptyState() {
        binding.emptyState.isVisible = true
        binding.tracksRecyclerView.isVisible = false
        binding.errorState.isVisible = false
        binding.historyLayout.isVisible = false
    }

    private fun showErrorState() {
        binding.errorState.isVisible = true
        binding.historyLayout.isVisible = false
        binding.tracksRecyclerView.isVisible = false
        binding.emptyState.isVisible = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, currentSearchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentSearchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        binding.inputEditText.setText(currentSearchText)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}