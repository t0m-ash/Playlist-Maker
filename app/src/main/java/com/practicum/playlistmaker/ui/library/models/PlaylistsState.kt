package com.practicum.playlistmaker.ui.library.models

import com.practicum.playlistmaker.domain.models.Playlist

sealed interface PlaylistsState {

    data object Empty : PlaylistsState

    data class Content(val playlists: List<Playlist>) : PlaylistsState
}
