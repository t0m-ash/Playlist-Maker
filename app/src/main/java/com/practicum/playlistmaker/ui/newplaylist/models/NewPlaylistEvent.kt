package com.practicum.playlistmaker.ui.newplaylist.models

sealed interface NewPlaylistEvent {

    data object ShowDiscardDialog : NewPlaylistEvent

    data object Close : NewPlaylistEvent

    data class PlaylistCreated(val playlistName: String) : NewPlaylistEvent
}
