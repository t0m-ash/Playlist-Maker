package com.practicum.playlistmaker.ui.library.models

sealed interface PlaylistsState {

    data object Empty : PlaylistsState
}
