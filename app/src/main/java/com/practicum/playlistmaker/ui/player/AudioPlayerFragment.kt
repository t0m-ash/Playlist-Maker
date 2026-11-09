package com.practicum.playlistmaker.ui.player

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.models.PlayerScreenState
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val track: Track by lazy { readTrack() }

    private val viewModel: PlayerViewModel by viewModel { parametersOf(track) }

    private var isTrackRendered = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isTrackRendered = false

        viewModel.observeScreenState().observe(viewLifecycleOwner) { state -> render(state) }

        binding.playerBackButton.setOnClickListener { findNavController().navigateUp() }
        binding.playerPlayButton.setOnClickListener { viewModel.onPlayButtonClicked() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun readTrack(): Track {
        val arguments = requireArguments()
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments.getSerializable(ARGS_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments.getSerializable(ARGS_TRACK) as? Track
        }
        return requireNotNull(track) { "AudioPlayerFragment requires a track argument" }
    }

    companion object {
        private const val ARGS_TRACK = "track"

        fun createArgs(track: Track): Bundle =
            Bundle().apply { putSerializable(ARGS_TRACK, track) }
    }
}
