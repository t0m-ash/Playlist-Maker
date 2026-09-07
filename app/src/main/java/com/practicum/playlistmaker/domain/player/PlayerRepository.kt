package com.practicum.playlistmaker.domain.player

import com.practicum.playlistmaker.domain.player.models.PlayerState

interface PlayerRepository {

    fun getState(): PlayerState

    fun preparePlayer(previewUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)

    fun start()

    fun pause()

    fun release()

    fun getCurrentPositionMillis(): Int
}
