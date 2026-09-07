package com.practicum.playlistmaker.domain.player

import com.practicum.playlistmaker.domain.player.models.PlayerState

interface PlayerInteractor {

    fun getState(): PlayerState

    fun preparePlayer(previewUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)

    fun playbackControl(): PlayerState

    fun pause()

    fun release()

    fun getCurrentPosition(): String
}
