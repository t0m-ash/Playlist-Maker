package com.practicum.playlistmaker.ui.player

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.PlayerState
import com.practicum.playlistmaker.domain.models.Track

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var progressView: TextView

    private val playerInteractor = Creator.providePlayerInteractor()

    private val handler = Handler(Looper.getMainLooper())
    private val progressRunnable = object : Runnable {
        override fun run() {
            progressView.text = playerInteractor.getCurrentPosition()
            handler.postDelayed(this, PROGRESS_UPDATE_DELAY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val track = readTrack()

        if (track == null) {
            finish()
            return
        }

        findViewById<ImageButton>(R.id.playerBackButton).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.playerTrackName).text = track.trackName
        findViewById<TextView>(R.id.playerArtistName).text = track.artistName
        findViewById<TextView>(R.id.playerDurationValue).text = track.trackTime
        findViewById<TextView>(R.id.playerGenreValue).text = track.primaryGenreName
        findViewById<TextView>(R.id.playerCountryValue).text = track.country

        bindOptionalRow(
            R.id.playerAlbumLabel,
            R.id.playerAlbumValue,
            track.collectionName,
        )
        bindOptionalRow(
            R.id.playerYearLabel,
            R.id.playerYearValue,
            track.releaseYear,
        )

        val cover = findViewById<ImageView>(R.id.playerCover)
        Glide.with(this)
            .load(track.coverArtworkUrl)
            .placeholder(R.drawable.ic_player_placeholder)
            .error(R.drawable.ic_player_placeholder)
            .into(cover)

        progressView = findViewById(R.id.playerProgress)
        playButton = findViewById(R.id.playerPlayButton)
        playButton.isEnabled = false
        playButton.setOnClickListener { playbackControl() }

        preparePlayer(track.previewUrl)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(progressRunnable)
        playerInteractor.release()
    }

    private fun readTrack(): Track? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_TRACK) as? Track
        }

    private fun preparePlayer(previewUrl: String) {
        playerInteractor.preparePlayer(
            previewUrl = previewUrl,
            onPrepared = {
                playButton.isEnabled = true
            },
            onCompletion = {
                handler.removeCallbacks(progressRunnable)
                renderPlayButton()
                progressView.text = getString(R.string.player_progress_start)
            },
        )
    }

    private fun playbackControl() {
        when (playerInteractor.playbackControl()) {
            PlayerState.PLAYING -> {
                renderPlayButton()
                handler.post(progressRunnable)
            }

            PlayerState.PAUSED -> {
                renderPlayButton()
                handler.removeCallbacks(progressRunnable)
            }

            PlayerState.DEFAULT, PlayerState.PREPARED -> Unit
        }
    }

    private fun pausePlayer() {
        if (playerInteractor.getState() != PlayerState.PLAYING) return

        playerInteractor.pause()
        renderPlayButton()
        handler.removeCallbacks(progressRunnable)
    }

    private fun renderPlayButton() {
        if (playerInteractor.getState() == PlayerState.PLAYING) {
            playButton.setImageResource(R.drawable.ic_player_pause)
            playButton.contentDescription = getString(R.string.player_pause_description)
        } else {
            playButton.setImageResource(R.drawable.ic_player_play)
            playButton.contentDescription = getString(R.string.player_play_description)
        }
    }

    private fun bindOptionalRow(labelId: Int, valueId: Int, value: String) {
        val label = findViewById<TextView>(labelId)
        val valueView = findViewById<TextView>(valueId)
        if (value.isEmpty()) {
            label.isVisible = false
            valueView.isVisible = false
        } else {
            valueView.text = value
        }
    }

    companion object {
        const val EXTRA_TRACK = "extra_track"

        private const val PROGRESS_UPDATE_DELAY = 300L
    }
}
