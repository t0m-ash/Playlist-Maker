package com.practicum.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.search.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.ui.search.models.SearchState
import com.practicum.playlistmaker.util.ClickDebouncer
import com.practicum.playlistmaker.util.Resource
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>(SearchState.Idle)
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val showPlayerEvent = SingleLiveEvent<Track>()
    fun observeShowPlayer(): LiveData<Track> = showPlayerEvent

    private val clickDebouncer = ClickDebouncer(viewModelScope)

    private var latestSearchText: String = ""
    private var isSearchFieldFocused: Boolean = false

    private var searchJob: Job? = null
    private var historyJob: Job? = null

    fun onSearchTextChanged(changedText: String) {
        if (latestSearchText == changedText) return

        latestSearchText = changedText

        if (changedText.isBlank()) {
            searchJob?.cancel()
            renderHistoryOrIdle()
            return
        }

        historyJob?.cancel()
        if (stateLiveData.value is SearchState.History) {
            renderState(SearchState.Idle)
        }

        startSearch(changedText, SEARCH_DEBOUNCE_DELAY)
    }

    fun onSearchActionDone(query: String) {
        latestSearchText = query
        startSearch(query)
    }

    fun onSearchFocusChanged(hasFocus: Boolean) {
        isSearchFieldFocused = hasFocus
        if (latestSearchText.isBlank()) {
            renderHistoryOrIdle()
        }
    }

    fun onRefreshClicked() {
        startSearch(latestSearchText)
    }

    fun onTrackClicked(track: Track) {
        if (!clickDebouncer.tryClick()) return

        searchHistoryInteractor.addTrack(track)
        if (stateLiveData.value is SearchState.History) {
            renderHistoryOrIdle()
        }
        showPlayerEvent.setValue(track)
    }

    fun onClearHistoryClicked() {
        historyJob?.cancel()
        searchHistoryInteractor.clear()
        renderState(SearchState.Idle)
    }

    private fun renderHistoryOrIdle() {
        historyJob?.cancel()

        if (!isSearchFieldFocused || latestSearchText.isNotBlank()) {
            renderState(SearchState.Idle)
            return
        }

        historyJob = viewModelScope.launch {
            val tracks = searchHistoryInteractor.getTracks()
            renderState(
                if (tracks.isEmpty()) SearchState.Idle else SearchState.History(tracks)
            )
        }
    }

    private fun startSearch(query: String, delayMillis: Long = 0L) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(delayMillis)
            searchRequest(query)
        }
    }

    private suspend fun searchRequest(query: String) {
        if (query.isBlank()) return

        renderState(SearchState.Loading)

        tracksInteractor
            .searchTracks(query)
            .collect { result -> processResult(result) }
    }

    private fun processResult(result: Resource<List<Track>>) {
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

    private fun renderState(state: SearchState) {
        stateLiveData.value = state
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
