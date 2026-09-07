package com.practicum.playlistmaker.data.player

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.player.PlayerRepository
import com.practicum.playlistmaker.domain.player.models.PlayerState

class PlayerRepositoryImpl : PlayerRepository {

    private val mediaPlayer = MediaPlayer()

    private var playerState = PlayerState.DEFAULT

    override fun getState(): PlayerState = playerState

    override fun preparePlayer(
        previewUrl: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
    ) {
        if (previewUrl.isEmpty()) return

        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.PREPARED
            onPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.PREPARED
            onCompletion()
        }
    }

    override fun start() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
    }

    override fun pause() {
        if (playerState != PlayerState.PLAYING) return

        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
    }

    override fun release() {
        mediaPlayer.release()
        playerState = PlayerState.DEFAULT
    }

    override fun getCurrentPositionMillis(): Int =
        if (playerState == PlayerState.PLAYING || playerState == PlayerState.PAUSED) {
            mediaPlayer.currentPosition
        } else {
            0
        }
}
