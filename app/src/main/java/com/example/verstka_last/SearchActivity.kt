package com.example.verstka_last

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder().baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val iTunesService = retrofit.create(iTunesAPI::class.java)
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
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("track", track)
            }
            startActivity(intent)
        }

        historyAdapter.setOnItemClickListener { track ->
            searchHistory.saveTrack(track)
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("track", track)
            }
            startActivity(intent)
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
            historyScrollView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            emptyState.visibility = View.GONE
            errorState.visibility = View.GONE

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

        recyclerView.isVisible = false
        emptyState.isVisible = false
        errorState.isVisible = false
        historyLayout.isVisible = false

        iTunesService.search(query).enqueue(object : Callback<ITunesSearchResponse> {
            override fun onResponse(call: Call<ITunesSearchResponse>, response: Response<ITunesSearchResponse>) {
                if (response.isSuccessful) {
                    val tracks = response.body()?.results?.map { it.toTrack() } ?: emptyList()
                    if (tracks.isEmpty()) {
                        showEmptyState()
                    } else {
                        showResults(tracks)
                    }
                } else {
                    showErrorState()
                }
            }

            override fun onFailure(call: Call<ITunesSearchResponse>, t: Throwable) {
                showErrorState()
            }
        })
    }

    private fun showInitialState() {
        recyclerView.isVisible = false
        emptyState.isVisible = false
        errorState.isVisible = false
        updateHistoryVisibility()
    }

    private fun showResults(tracks: List<Track>) {
        adapter.updateTracks(tracks)
        recyclerView.isVisible = true
        emptyState.isVisible = false
        errorState.isVisible = false
        historyLayout.isVisible = false
    }

    private fun showEmptyState() {
        recyclerView.isVisible = false
        emptyState.isVisible = true
        errorState.isVisible = false
        historyLayout.isVisible = false
    }

    private fun showErrorState() {
        recyclerView.isVisible = false
        emptyState.isVisible = false
        errorState.isVisible = true
        historyLayout.isVisible = false
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
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
    }
}