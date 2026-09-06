package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.PlayerState

interface PlayerRepository {

    fun getState(): PlayerState

    fun preparePlayer(previewUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)

    fun start()

    fun pause()

    fun release()

    fun getCurrentPositionMillis(): Int
}
