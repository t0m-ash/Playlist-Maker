package com.practicum.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.favorites.FavoriteTracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.models.PlayerState
import com.practicum.playlistmaker.ui.player.models.PlayerScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    track: Track,
    private val playerInteractor: PlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
) : ViewModel() {

    private var screenState = PlayerScreenState(
        track = track,
        isPlayEnabled = false,
        isPlaying = false,
        progress = DEFAULT_PROGRESS,
    )

    private val screenStateLiveData = MutableLiveData(screenState)
    fun observeScreenState(): LiveData<PlayerScreenState> = screenStateLiveData

    private var progressJob: Job? = null
    private var favoriteJob: Job? = null

    init {
        playerInteractor.preparePlayer(
            previewUrl = track.previewUrl,
            onPrepared = {
                updateState { it.copy(isPlayEnabled = true) }
            },
            onCompletion = {
                progressJob?.cancel()
                updateState { it.copy(isPlaying = false, progress = DEFAULT_PROGRESS) }
            },
        )

        favoriteJob = viewModelScope.launch {
            val isFavorite = favoriteTracksInteractor.isFavorite(track.trackId)
            updateState { it.copy(track = it.track.copy(isFavorite = isFavorite)) }
        }
    }

    fun onPlayButtonClicked() {
        when (playerInteractor.playbackControl()) {
            PlayerState.PLAYING -> {
                updateState { it.copy(isPlaying = true) }
                startProgressUpdates()
            }

            PlayerState.PAUSED -> onPlaybackPaused()

            PlayerState.DEFAULT, PlayerState.PREPARED -> Unit
        }
    }

    fun onFavoriteClicked() {
        if (favoriteJob?.isActive == true) return

        val track = screenState.track
        favoriteJob = viewModelScope.launch {
            if (track.isFavorite) {
                favoriteTracksInteractor.deleteTrack(track)
            } else {
                favoriteTracksInteractor.addTrack(track)
            }
            updateState { it.copy(track = it.track.copy(isFavorite = !track.isFavorite)) }
        }
    }

    fun onPause() {
        if (playerInteractor.getState() != PlayerState.PLAYING) return

        playerInteractor.pause()
        onPlaybackPaused()
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (playerInteractor.getState() == PlayerState.PLAYING) {
                updateState { it.copy(progress = playerInteractor.getCurrentPosition()) }
                delay(PROGRESS_UPDATE_DELAY)
            }
        }
    }

    private fun onPlaybackPaused() {
        progressJob?.cancel()
        updateState { it.copy(isPlaying = false) }
    }

    private fun updateState(transform: (PlayerScreenState) -> PlayerScreenState) {
        screenState = transform(screenState)
        screenStateLiveData.value = screenState
    }

    companion object {
        private const val PROGRESS_UPDATE_DELAY = 300L
        private const val DEFAULT_PROGRESS = "00:00"
    }
}
