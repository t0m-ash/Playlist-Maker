package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var progressView: TextView

    private val mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.DEFAULT

    private val handler = Handler(Looper.getMainLooper())
    private val progressRunnable = object : Runnable {
        override fun run() {
            progressView.text = formatProgress(mediaPlayer.currentPosition)
            handler.postDelayed(this, PROGRESS_UPDATE_DELAY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val track = intent.getStringExtra(EXTRA_TRACK)
            ?.let { Gson().fromJson(it, Track::class.java) }

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
            .load(track.getCoverArtwork())
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
        mediaPlayer.release()
    }

    private fun preparePlayer(previewUrl: String?) {
        if (previewUrl.isNullOrEmpty()) return

        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.PREPARED
            playButton.isEnabled = true
        }
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.PREPARED
            handler.removeCallbacks(progressRunnable)
            renderPlayButton()
            progressView.text = getString(R.string.player_progress_start)
        }
    }

    private fun playbackControl() {
        when (playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            PlayerState.DEFAULT -> Unit
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
        renderPlayButton()
        handler.post(progressRunnable)
    }

    private fun pausePlayer() {
        if (playerState != PlayerState.PLAYING) return

        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
        renderPlayButton()
        handler.removeCallbacks(progressRunnable)
    }

    private fun renderPlayButton() {
        if (playerState == PlayerState.PLAYING) {
            playButton.setImageResource(R.drawable.ic_player_pause)
            playButton.contentDescription = getString(R.string.player_pause_description)
        } else {
            playButton.setImageResource(R.drawable.ic_player_play)
            playButton.contentDescription = getString(R.string.player_play_description)
        }
    }

    private fun formatProgress(positionMillis: Int): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(positionMillis)

    private fun bindOptionalRow(labelId: Int, valueId: Int, value: String?) {
        val label = findViewById<TextView>(labelId)
        val valueView = findViewById<TextView>(valueId)
        if (value.isNullOrEmpty()) {
            label.isVisible = false
            valueView.isVisible = false
        } else {
            valueView.text = value
        }
    }

    private enum class PlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED,
    }

    companion object {
        const val EXTRA_TRACK = "extra_track"

        private const val PROGRESS_UPDATE_DELAY = 300L
    }
}
