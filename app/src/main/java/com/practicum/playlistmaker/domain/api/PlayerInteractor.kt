package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.PlayerState

interface PlayerInteractor {

    fun getState(): PlayerState

    fun preparePlayer(previewUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)

    fun playbackControl(): PlayerState

    fun pause()

    fun release()

    fun getCurrentPosition(): String
}
