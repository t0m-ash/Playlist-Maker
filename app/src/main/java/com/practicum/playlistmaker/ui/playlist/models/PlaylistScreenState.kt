package com.practicum.playlistmaker.ui.playlist.models

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track

data class PlaylistScreenState(
    val playlist: Playlist,
    val tracks: List<Track>,
    val durationMinutes: Int,
)
