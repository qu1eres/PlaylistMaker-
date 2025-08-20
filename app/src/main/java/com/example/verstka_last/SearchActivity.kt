package com.example.verstka_last

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
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

    private var currentSearchText: String = ""
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var emptyState: View
    private lateinit var errorState: View
    private lateinit var retryButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputEditText = findViewById(R.id.input_edit_text)
        clearButton = findViewById(R.id.clear_icon)
        backButton = findViewById(R.id.back_button)
        recyclerView = findViewById(R.id.tracks_recycler_view)
        emptyState = findViewById(R.id.empty_state)
        errorState = findViewById(R.id.error_state)
        retryButton = findViewById(R.id.retry_button)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(emptyList())
        recyclerView.adapter = adapter

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard(it)
            inputEditText.clearFocus()
            showInitialState()
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
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        retryButton.setOnClickListener {
            performSearch()
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
    }

    private fun showResults(tracks: List<Track>) {
        adapter.updateTracks(tracks)
        recyclerView.isVisible = true
        emptyState.isVisible = false
        errorState.isVisible = false
    }

    private fun showEmptyState() {
        recyclerView.isVisible = false
        emptyState.isVisible = true
        errorState.isVisible = false
    }

    private fun showErrorState() {
        recyclerView.isVisible = false
        emptyState.isVisible = false
        errorState.isVisible = true
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