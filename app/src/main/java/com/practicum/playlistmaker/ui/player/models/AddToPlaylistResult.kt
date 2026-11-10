package com.practicum.playlistmaker.ui.player.models

sealed interface AddToPlaylistResult {

    val playlistName: String

    data class Added(override val playlistName: String) : AddToPlaylistResult

    data class AlreadyAdded(override val playlistName: String) : AddToPlaylistResult
}
