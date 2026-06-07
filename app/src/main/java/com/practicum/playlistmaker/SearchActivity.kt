package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var placeholderLayout: View
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var refreshButton: Button

    private val trackAdapter = TrackAdapter()

    private var searchText: String = SEARCH_VALUE
    private var lastQuery: String = ""

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        searchEditText = findViewById(R.id.searchEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        tracksRecyclerView = findViewById(R.id.tracksRecyclerView)
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = trackAdapter

        placeholderLayout = findViewById(R.id.placeholderLayout)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        refreshButton = findViewById(R.id.refreshButton)

        btnBack.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
            trackAdapter.tracks = emptyList()
            hidePlaceholder()
        }

        refreshButton.setOnClickListener {
            search(lastQuery)
        }

        searchEditText.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                clearButton.visibility = clearButtonVisibility(s)
            },
            afterTextChanged = { s ->
                searchText = s?.toString().orEmpty()
            }
        )

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(searchEditText.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun search(query: String) {
        if (query.isBlank()) return

        lastQuery = query
        hidePlaceholder()

        itunesService.search(query).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>,
            ) {
                if (response.isSuccessful) {
                    val results = response.body()?.results.orEmpty()
                    if (results.isNotEmpty()) {
                        showResults(results)
                    } else {
                        showNothingFound()
                    }
                } else {
                    showServerError()
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                showServerError()
            }
        })
    }

    private fun showResults(results: List<Track>) {
        hidePlaceholder()
        trackAdapter.tracks = results
        tracksRecyclerView.visibility = View.VISIBLE
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
        trackAdapter.tracks = emptyList()
        tracksRecyclerView.visibility = View.GONE
        placeholderImage.setImageResource(image)
        placeholderMessage.text = message
        refreshButton.visibility = if (showRefresh) View.VISIBLE else View.GONE
        placeholderLayout.visibility = View.VISIBLE
    }

    private fun hidePlaceholder() {
        placeholderLayout.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
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
        private const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }
}
