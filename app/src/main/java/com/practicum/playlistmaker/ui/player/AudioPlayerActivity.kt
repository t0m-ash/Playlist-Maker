package com.practicum.playlistmaker.ui.player

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.models.PlayerScreenState
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var viewModel: PlayerViewModel

    private var isTrackRendered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = readTrack()
        if (track == null) {
            finish()
            return
        }

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getFactory(track),
        )[PlayerViewModel::class.java]

        viewModel.observeScreenState().observe(this) { state -> render(state) }

        binding.playerBackButton.setOnClickListener { finish() }
        binding.playerPlayButton.setOnClickListener { viewModel.onPlayButtonClicked() }
    }

    override fun onPause() {
        super.onPause()
        if (::viewModel.isInitialized) {
            viewModel.onPause()
        }
    }

    private fun render(state: PlayerScreenState) {
        if (!isTrackRendered) {
            renderTrack(state.track)
            isTrackRendered = true
        }

        binding.playerPlayButton.isEnabled = state.isPlayEnabled
        binding.playerProgress.text = state.progress

        if (state.isPlaying) {
            binding.playerPlayButton.setImageResource(R.drawable.ic_player_pause)
            binding.playerPlayButton.contentDescription =
                getString(R.string.player_pause_description)
        } else {
            binding.playerPlayButton.setImageResource(R.drawable.ic_player_play)
            binding.playerPlayButton.contentDescription =
                getString(R.string.player_play_description)
        }
    }

    private fun renderTrack(track: Track) {
        binding.apply {
            playerTrackName.text = track.trackName
            playerArtistName.text = track.artistName
            playerDurationValue.text = track.trackTime
            playerGenreValue.text = track.primaryGenreName
            playerCountryValue.text = track.country
        }

        bindOptionalRow(binding.playerAlbumLabel, binding.playerAlbumValue, track.collectionName)
        bindOptionalRow(binding.playerYearLabel, binding.playerYearValue, track.releaseYear)

        Glide.with(this)
            .load(track.coverArtworkUrl)
            .placeholder(R.drawable.ic_player_placeholder)
            .error(R.drawable.ic_player_placeholder)
            .into(binding.playerCover)
    }

    private fun bindOptionalRow(label: TextView, valueView: TextView, value: String) {
        if (value.isEmpty()) {
            label.isVisible = false
            valueView.isVisible = false
        } else {
            valueView.text = value
        }
    }

    private fun readTrack(): Track? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_TRACK) as? Track
        }

    companion object {
        const val EXTRA_TRACK = "extra_track"
    }
}
