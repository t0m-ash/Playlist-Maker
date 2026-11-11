package com.practicum.playlistmaker.ui.playlist.models

import com.practicum.playlistmaker.domain.models.Track

sealed interface PlaylistEvent {

    data class OpenPlayer(val track: Track) : PlaylistEvent

    data object ShowNothingToShare : PlaylistEvent

    data object Close : PlaylistEvent
}
