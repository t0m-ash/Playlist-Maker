package com.practicum.playlistmaker.ui.player

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.models.AddToPlaylistResult
import com.practicum.playlistmaker.ui.player.models.PlayerScreenState
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val track: Track by lazy { readTrack() }

    private val viewModel: PlayerViewModel by viewModel { parametersOf(track) }

    private val playlistsAdapter = PlaylistsBottomSheetAdapter { playlist ->
        viewModel.onPlaylistClicked(playlist)
    }

    private var playlistsBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    private val closePlaylistsSheetCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            hidePlaylistsSheet()
        }
    }

    private var isNewPlaylistPending = false

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
        isNewPlaylistPending = false

        setupPlaylistsBottomSheet()

        viewModel.observeScreenState().observe(viewLifecycleOwner) { state -> render(state) }

        viewModel.observeAddToPlaylistResult().observe(viewLifecycleOwner) { result ->
            showAddToPlaylistResult(result)
        }

        viewModel.observeOpenNewPlaylist().observe(viewLifecycleOwner) { openNewPlaylist() }

        binding.playerBackButton.setOnClickListener { findNavController().navigateUp() }
        binding.playerPlayButton.setOnClickListener { viewModel.onPlayButtonClicked() }
        binding.playerFavoriteButton.setOnClickListener { viewModel.onFavoriteClicked() }
        binding.playerAddButton.setOnClickListener { showPlaylistsSheet() }
        binding.playlistsNewPlaylistButton.setOnClickListener { viewModel.onNewPlaylistClicked() }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        val isSheetVisible =
            playlistsBottomSheetBehavior?.state != BottomSheetBehavior.STATE_HIDDEN
        binding.playlistsOverlay.alpha = if (isSheetVisible) 1f else 0f
        updateOverlay(isSheetVisible)
        if (isSheetVisible) viewModel.onPlaylistsSheetOpened()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        binding.playlistsRecyclerView.adapter = null
        playlistsBottomSheetBehavior = null
        super.onDestroyView()
        _binding = null
    }

    private fun setupPlaylistsBottomSheet() {
        binding.playlistsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistsRecyclerView.adapter = playlistsAdapter

        val behavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val isHidden = newState == BottomSheetBehavior.STATE_HIDDEN
                updateOverlay(isSheetVisible = !isHidden)
                if (isHidden && isNewPlaylistPending) {
                    isNewPlaylistPending = false
                    val isStarted = viewLifecycleOwner.lifecycle.currentState
                        .isAtLeast(Lifecycle.State.STARTED)
                    if (isStarted) {
                        findNavController().navigate(
                            R.id.action_audioPlayerFragment_to_newPlaylistFragment
                        )
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                _binding?.playlistsOverlay?.alpha = (slideOffset + 1f).coerceIn(0f, 1f)
            }
        })
        playlistsBottomSheetBehavior = behavior

        binding.playlistsOverlay.setOnClickListener { hidePlaylistsSheet() }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            closePlaylistsSheetCallback,
        )
    }

    private fun showPlaylistsSheet() {
        viewModel.onPlaylistsSheetOpened()
        playlistsBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hidePlaylistsSheet() {
        playlistsBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun updateOverlay(isSheetVisible: Boolean) {
        _binding?.playlistsOverlay?.isVisible = isSheetVisible
        closePlaylistsSheetCallback.isEnabled = isSheetVisible
    }

    private fun openNewPlaylist() {
        if (playlistsBottomSheetBehavior?.state == BottomSheetBehavior.STATE_HIDDEN) {
            findNavController().navigate(R.id.action_audioPlayerFragment_to_newPlaylistFragment)
        } else {
            isNewPlaylistPending = true
            hidePlaylistsSheet()
        }
    }

    private fun showAddToPlaylistResult(result: AddToPlaylistResult) {
        val messageRes = when (result) {
            is AddToPlaylistResult.Added -> {
                hidePlaylistsSheet()
                R.string.player_track_added_to_playlist
            }

            is AddToPlaylistResult.AlreadyAdded -> R.string.player_track_already_in_playlist
        }
        Toast.makeText(
            requireContext(),
            getString(messageRes, result.playlistName),
            Toast.LENGTH_SHORT,
        ).show()
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

        if (state.track.isFavorite) {
            binding.playerFavoriteButton.setImageResource(R.drawable.ic_player_favorite_active)
            binding.playerFavoriteButton.contentDescription =
                getString(R.string.player_favorite_remove_description)
        } else {
            binding.playerFavoriteButton.setImageResource(R.drawable.ic_player_favorite)
            binding.playerFavoriteButton.contentDescription =
                getString(R.string.player_favorite_description)
        }

        playlistsAdapter.submitList(state.playlists)
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
