package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.models.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {

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
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(repository.getCurrentPositionMillis())
}
