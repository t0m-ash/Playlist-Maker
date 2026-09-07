package com.practicum.playlistmaker.ui.player.models

import com.practicum.playlistmaker.domain.models.Track

data class PlayerScreenState(
    val track: Track,
    val isPlayEnabled: Boolean,
    val isPlaying: Boolean,
    val progress: String,
)
