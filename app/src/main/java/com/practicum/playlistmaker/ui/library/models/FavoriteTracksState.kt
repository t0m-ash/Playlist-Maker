package com.practicum.playlistmaker.ui.library.models

import com.practicum.playlistmaker.domain.models.Track

sealed interface FavoriteTracksState {

    data object Empty : FavoriteTracksState

    data class Content(val tracks: List<Track>) : FavoriteTracksState
}
