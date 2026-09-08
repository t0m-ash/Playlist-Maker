package com.practicum.playlistmaker.domain.player.impl

import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.PlayerRepository
import com.practicum.playlistmaker.domain.player.models.PlayerState
import java.text.SimpleDateFormat

class PlayerInteractorImpl(
    private val repository: PlayerRepository,
    private val trackTimeFormat: SimpleDateFormat,
) : PlayerInteractor {

    override fun getState(): PlayerState = repository.getState()

    override fun preparePlayer(
        previewUrl: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
    ) {
        repository.preparePlayer(previewUrl, onPrepared, onCompletion)
    }

    override fun playbackControl(): PlayerState {
        when (repository.getState()) {
            PlayerState.PLAYING -> repository.pause()
            PlayerState.PREPARED, PlayerState.PAUSED -> repository.start()
            PlayerState.DEFAULT -> Unit
        }
        return repository.getState()
    }

    override fun pause() {
        repository.pause()
    }

    override fun release() {
        repository.release()
    }

    override fun getCurrentPosition(): String =
        trackTimeFormat.format(repository.getCurrentPositionMillis())
}
