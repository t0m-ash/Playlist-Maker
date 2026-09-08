package com.practicum.playlistmaker.ui.library.models

sealed interface FavoriteTracksState {

    data object Empty : FavoriteTracksState
}
