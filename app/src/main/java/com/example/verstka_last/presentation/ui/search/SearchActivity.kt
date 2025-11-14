package com.example.verstka_last.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.verstka_last.presentation.ui.mediaplayer.PlayerActivity
import com.example.verstka_last.R
import com.example.verstka_last.data.local.SearchHistory
import com.example.verstka_last.presentation.presenters.TrackAdapter
import com.example.verstka_last.domain.Creator
import com.example.verstka_last.domain.api.TracksInteractor
import com.example.verstka_last.domain.models.Track

class SearchActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }
    private var isClickAllowed = true
    private var interactor: TracksInteractor = Creator.provideTracksInteractor()
    private lateinit var searchHistory: SearchHistory

    private var currentSearchText: String = ""
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var emptyState: View
    private lateinit var errorState: View
    private lateinit var retryButton: TextView
    private lateinit var historyScrollView: NestedScrollView
    private lateinit var progressBar: LinearLayout

    private lateinit var historyLayout: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistory = SearchHistory(this)

        inputEditText = findViewById(R.id.input_edit_text)
        clearButton = findViewById(R.id.clear_icon)
        backButton = findViewById(R.id.back_button)
        recyclerView = findViewById(R.id.tracks_recycler_view)
        emptyState = findViewById(R.id.empty_state)
        errorState = findViewById(R.id.error_state)
        retryButton = findViewById(R.id.retry_button)
        historyScrollView = findViewById(R.id.history_scroll_view)
        progressBar = findViewById(R.id.progress_bar)

        historyLayout = findViewById(R.id.history_layout)
        historyRecyclerView = findViewById(R.id.history_recycler_view)
        clearHistoryButton = findViewById(R.id.clear_history_button)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(emptyList())
        recyclerView.adapter = adapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter(emptyList())
        historyRecyclerView.adapter = historyAdapter

        adapter.setOnItemClickListener { track ->
            searchHistory.saveTrack(track)
            if (clickDebounce()) {
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra("track", track)
                }
                startActivity(intent)
            }
        }

        historyAdapter.setOnItemClickListener { track ->
            searchHistory.saveTrack(track)
            if(clickDebounce()) {
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra("track", track)
                }
                startActivity(intent)
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard(it)
            inputEditText.clearFocus()
            showInitialState()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            updateHistoryVisibility()
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                hideKeyboard(inputEditText)
                true
            }
            false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                currentSearchText = s.toString()
                updateHistoryVisibility()
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            updateHistoryVisibility()
        }

        retryButton.setOnClickListener {
            performSearch()
        }

        updateHistoryVisibility()
    }

    private fun updateHistoryVisibility() {
        val hasFocus = inputEditText.hasFocus()
        val isEmpty = inputEditText.text.isEmpty()
        val history = searchHistory.loadHistory()

        if (hasFocus && isEmpty && history.isNotEmpty()) {
            historyScrollView.visibility = View.VISIBLE; recyclerView.visibility = View.GONE; emptyState.visibility = View.GONE; errorState.visibility = View.GONE

            historyAdapter.updateTracks(history)
        } else {

            historyScrollView.visibility = View.GONE
        }
    }

    private fun performSearch() {
        val query = inputEditText.text.toString().trim()
        if (query.isEmpty()) {
            showInitialState()
            return
        }

        recyclerView.isVisible = false; emptyState.isVisible = false; errorState.isVisible = false; historyLayout.isVisible = false; progressBar.visibility = View.VISIBLE
        interactor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    if (foundTracks.isEmpty()) {
                        showEmptyState()
                    } else {
                        showResults(foundTracks)
                    }
                }
            }
            /*override fun onFailure(call: Call<ITunesSearchResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                showErrorState()
            }*/ // Я не стал добавлять обработку ошибок, т.к. в предидущем уроке было написано "Очень верное замечание!
                // Это вопрос обработки ошибок в чистой архитектуре.
                //Для этого существует достаточно простое и удобное решение, о котором мы поговорим в следующем спринте.
                // Пока будем считать, что нам не нужно обрабатывать ошибки каким-то особым образом."
                // Т.к. достаточно простое и удобное решение мне будет предоставлено в след. спринте, я в данный момент отказался от обработки ошибок.
        })

    }
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showInitialState() {
        recyclerView.isVisible = false; emptyState.isVisible = false; errorState.isVisible = false
        updateHistoryVisibility()
    }

    private fun showResults(tracks: List<Track>) {
        adapter.updateTracks(tracks)
        recyclerView.isVisible = true; emptyState.isVisible = false; errorState.isVisible = false; historyLayout.isVisible = false
    }

    private fun showEmptyState() {
        emptyState.isVisible = true; recyclerView.isVisible = false; errorState.isVisible = false; historyLayout.isVisible = false
    }

    private fun showErrorState() {
        errorState.isVisible = true; historyLayout.isVisible = false; recyclerView.isVisible = false; emptyState.isVisible = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, currentSearchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentSearchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        inputEditText.setText(currentSearchText)
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
        private const val SEARCH_DEBOUNCE_DELAY = 2000L; private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}