package com.practicum.playlistmaker.ui.search.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.search.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.ui.search.models.SearchState
import com.practicum.playlistmaker.util.Resource
import com.practicum.playlistmaker.util.SingleLiveEvent

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchState>(SearchState.Idle)
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val showPlayerEvent = SingleLiveEvent<Track>()
    fun observeShowPlayer(): LiveData<Track> = showPlayerEvent

    private var latestSearchText: String = ""
    private var isSearchFieldFocused: Boolean = false
    private var isClickAllowed = true

    fun onSearchTextChanged(changedText: String) {
        if (latestSearchText == changedText) return

        latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        if (changedText.isBlank()) {
            renderHistoryOrIdle()
            return
        }

        if (stateLiveData.value is SearchState.History) {
            renderState(SearchState.Idle)
        }

        val searchRunnable = Runnable { searchRequest(changedText) }
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY,
        )
    }

    fun onSearchActionDone(query: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        latestSearchText = query
        searchRequest(query)
    }

    fun onSearchFocusChanged(hasFocus: Boolean) {
        isSearchFieldFocused = hasFocus
        if (latestSearchText.isBlank()) {
            renderHistoryOrIdle()
        }
    }

    fun onRefreshClicked() {
        searchRequest(latestSearchText)
    }

    fun onTrackClicked(track: Track) {
        if (!clickDebounce()) return

        searchHistoryInteractor.addTrack(track)
        if (stateLiveData.value is SearchState.History) {
            renderHistoryOrIdle()
        }
        showPlayerEvent.setValue(track)
    }

    fun onClearHistoryClicked() {
        searchHistoryInteractor.clear()
        renderHistoryOrIdle()
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun renderHistoryOrIdle() {
        val tracks = searchHistoryInteractor.getTracks()
        val shouldShowHistory =
            isSearchFieldFocused && latestSearchText.isBlank() && tracks.isNotEmpty()

        renderState(
            if (shouldShowHistory) SearchState.History(tracks) else SearchState.Idle
        )
    }

    private fun searchRequest(query: String) {
        if (query.isBlank()) return

        renderState(SearchState.Loading)

        tracksInteractor.searchTracks(query) { result ->
            when (result) {
                is Resource.Error -> renderState(SearchState.Error)

                is Resource.Success -> renderState(
                    if (result.data.isEmpty()) {
                        SearchState.Empty
                    } else {
                        SearchState.Content(result.data)
                    }
                )
            }
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}
