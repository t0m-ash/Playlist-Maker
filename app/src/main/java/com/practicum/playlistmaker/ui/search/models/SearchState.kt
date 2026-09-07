package com.practicum.playlistmaker.ui.search.models

import com.practicum.playlistmaker.domain.models.Track

sealed interface SearchState {

    data object Idle : SearchState

    data object Loading : SearchState

    data class History(val tracks: List<Track>) : SearchState

    data class Content(val tracks: List<Track>) : SearchState

    data object Empty : SearchState

    data object Error : SearchState
}
