package com.practicum.playlistmaker.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.TrackAdapter
import com.practicum.playlistmaker.ui.player.AudioPlayerActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var placeholderLayout: View
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var refreshButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var searchHistoryLayout: View
    private lateinit var historyRecyclerView: RecyclerView

    private val tracksInteractor = Creator.provideTracksInteractor()
    private val searchHistoryInteractor by lazy { Creator.provideSearchHistoryInteractor(this) }

    private val trackAdapter = TrackAdapter { track ->
        if (clickDebounce()) {
            searchHistoryInteractor.addTrack(track)
            openPlayer(track)
        }
    }
    private val historyAdapter = TrackAdapter { track ->
        if (clickDebounce()) {
            searchHistoryInteractor.addTrack(track)
            renderHistory()
            openPlayer(track)
        }
    }

    private var searchText: String = SEARCH_VALUE
    private var lastQuery: String = ""

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search(searchEditText.text.toString()) }
    private var isClickAllowed = true

    private val searchConsumer = object : TracksInteractor.TracksConsumer {
        override fun consume(foundTracks: List<Track>?) {
            handler.post {
                when {
                    foundTracks == null -> showServerError()
                    foundTracks.isEmpty() -> showNothingFound()
                    else -> showResults(foundTracks)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        searchEditText = findViewById(R.id.searchEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        tracksRecyclerView = findViewById(R.id.tracksRecyclerView)
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = trackAdapter

        searchHistoryLayout = findViewById(R.id.searchHistoryLayout)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
        val clearHistoryButton = findViewById<Button>(R.id.clearHistoryButton)

        placeholderLayout = findViewById(R.id.placeholderLayout)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        refreshButton = findViewById(R.id.refreshButton)
        progressBar = findViewById(R.id.progressBar)

        btnBack.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
            clearResults()
            renderHistory()
        }

        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clear()
            historyAdapter.tracks = emptyList()
            searchHistoryLayout.isVisible = false
        }

        refreshButton.setOnClickListener {
            search(lastQuery)
        }

        searchEditText.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                clearButton.isVisible = !s.isNullOrEmpty()
            },
            afterTextChanged = { s ->
                searchText = s?.toString().orEmpty()
                if (searchText.isBlank()) {
                    clearResults()
                } else {
                    searchDebounce()
                }
                renderHistory()
            }
        )

        searchEditText.setOnFocusChangeListener { _, _ ->
            renderHistory()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacks(searchRunnable)
                search(searchEditText.text.toString())
                true
            } else {
                false
            }
        }

        renderHistory()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
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

    private fun search(query: String) {
        if (query.isBlank()) return

        lastQuery = query
        showLoading()

        tracksInteractor.searchTracks(query, searchConsumer)
    }

    private fun showLoading() {
        hidePlaceholder()
        searchHistoryLayout.isVisible = false
        tracksRecyclerView.isVisible = false
        progressBar.isVisible = true
    }

    private fun showResults(results: List<Track>) {
        progressBar.isVisible = false
        hidePlaceholder()
        searchHistoryLayout.isVisible = false
        trackAdapter.tracks = results
        tracksRecyclerView.isVisible = true
    }

    private fun showNothingFound() {
        showPlaceholder(
            message = getString(R.string.search_nothing_found),
            image = R.drawable.ic_nothing_found,
            showRefresh = false,
        )
    }

    private fun showServerError() {
        showPlaceholder(
            message = getString(R.string.search_connection_problem),
            image = R.drawable.ic_connection_problem,
            showRefresh = true,
        )
    }

    private fun showPlaceholder(message: String, image: Int, showRefresh: Boolean) {
        progressBar.isVisible = false
        searchHistoryLayout.isVisible = false
        trackAdapter.tracks = emptyList()
        tracksRecyclerView.isVisible = false
        placeholderImage.setImageResource(image)
        placeholderMessage.text = message
        refreshButton.isVisible = showRefresh
        placeholderLayout.isVisible = true
    }

    private fun hidePlaceholder() {
        placeholderLayout.isVisible = false
    }

    private fun clearResults() {
        handler.removeCallbacks(searchRunnable)
        progressBar.isVisible = false
        trackAdapter.tracks = emptyList()
        tracksRecyclerView.isVisible = false
        hidePlaceholder()
    }

    private fun renderHistory() {
        val tracks = searchHistoryInteractor.getTracks()
        val shouldShow = searchEditText.hasFocus() &&
            searchEditText.text.isEmpty() &&
            tracks.isNotEmpty()

        if (shouldShow) {
            historyAdapter.tracks = tracks
            hidePlaceholder()
            tracksRecyclerView.isVisible = false
            searchHistoryLayout.isVisible = true
        } else {
            searchHistoryLayout.isVisible = false
        }
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(AudioPlayerActivity.EXTRA_TRACK, track)
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchEditText.setText(savedInstanceState.getString(SEARCH_STRING_KEY, SEARCH_VALUE))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_STRING_KEY, searchText)
    }

    companion object {
        private const val SEARCH_STRING_KEY = "SEARCH_STRING"
        private const val SEARCH_VALUE = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
