package com.practicum.playlistmaker.ui.player.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.models.PlayerState
import com.practicum.playlistmaker.ui.player.models.PlayerScreenState

class PlayerViewModel(
    track: Track,
    private val playerInteractor: PlayerInteractor,
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private var screenState = PlayerScreenState(
        track = track,
        isPlayEnabled = false,
        isPlaying = false,
        progress = DEFAULT_PROGRESS,
    )

    private val screenStateLiveData = MutableLiveData(screenState)
    fun observeScreenState(): LiveData<PlayerScreenState> = screenStateLiveData

    private val progressRunnable = object : Runnable {
        override fun run() {
            updateState { it.copy(progress = playerInteractor.getCurrentPosition()) }
            handler.postDelayed(this, PROGRESS_UPDATE_DELAY)
        }
    }

    init {
        playerInteractor.preparePlayer(
            previewUrl = track.previewUrl,
            onPrepared = {
                updateState { it.copy(isPlayEnabled = true) }
            },
            onCompletion = {
                handler.removeCallbacks(progressRunnable)
                updateState { it.copy(isPlaying = false, progress = DEFAULT_PROGRESS) }
            },
        )
    }

    fun onPlayButtonClicked() {
        when (playerInteractor.playbackControl()) {
            PlayerState.PLAYING -> {
                updateState { it.copy(isPlaying = true) }
                handler.post(progressRunnable)
            }

            PlayerState.PAUSED -> {
                handler.removeCallbacks(progressRunnable)
                updateState { it.copy(isPlaying = false) }
            }

            PlayerState.DEFAULT, PlayerState.PREPARED -> Unit
        }
    }

    fun onPause() {
        if (playerInteractor.getState() != PlayerState.PLAYING) return

        playerInteractor.pause()
        handler.removeCallbacks(progressRunnable)
        updateState { it.copy(isPlaying = false) }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(progressRunnable)
        playerInteractor.release()
    }

    private fun updateState(transform: (PlayerScreenState) -> PlayerScreenState) {
        screenState = transform(screenState)
        screenStateLiveData.postValue(screenState)
    }

    companion object {
        private const val PROGRESS_UPDATE_DELAY = 300L
        private const val DEFAULT_PROGRESS = "00:00"
    }
}
