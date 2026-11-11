package com.practicum.playlistmaker.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.common.TrackAdapter
import com.practicum.playlistmaker.ui.common.bind
import com.practicum.playlistmaker.ui.common.loadPlaylistCover
import com.practicum.playlistmaker.ui.editplaylist.EditPlaylistFragment
import com.practicum.playlistmaker.ui.player.AudioPlayerFragment
import com.practicum.playlistmaker.ui.playlist.models.PlaylistEvent
import com.practicum.playlistmaker.ui.playlist.models.PlaylistScreenState
import com.practicum.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val playlistId: Long by lazy { requireArguments().getLong(ARGS_PLAYLIST_ID) }

    private val viewModel: PlaylistViewModel by viewModel { parametersOf(playlistId) }

    private val trackAdapter = TrackAdapter(
        onTrackLongClick = { track -> showDeleteTrackDialog(track) },
        onTrackClick = { track -> viewModel.onTrackClicked(track) },
    )

    private var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    private val closeMenuCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            hideMenu()
        }
    }

    private var isEditorPending = false

    private var dialog: AlertDialog? = null

    private var isCoverRendered = false
    private var renderedCoverPath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isEditorPending = false
        isCoverRendered = false

        setupTracksBottomSheet()
        setupMenuBottomSheet()

        binding.playlistBackButton.setOnClickListener { findNavController().navigateUp() }
        binding.playlistShareButton.setOnClickListener { viewModel.onShareClicked() }
        binding.playlistMenuButton.setOnClickListener { showMenu() }

        binding.playlistMenuShare.setOnClickListener {
            hideMenu()
            viewModel.onShareClicked()
        }
        binding.playlistMenuEdit.setOnClickListener { openEditor() }
        binding.playlistMenuDelete.setOnClickListener {
            hideMenu()
            showDeletePlaylistDialog()
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state -> render(state) }
        viewModel.observeEvents().observe(viewLifecycleOwner) { event -> handleEvent(event) }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        val isMenuVisible = menuBottomSheetBehavior?.state != BottomSheetBehavior.STATE_HIDDEN
        binding.playlistMenuOverlay.alpha = if (isMenuVisible) 1f else 0f
        updateMenuOverlay(isMenuVisible)
    }

    override fun onDestroyView() {
        dialog?.dismiss()
        dialog = null
        binding.playlistTracksRecyclerView.adapter = null
        tracksBottomSheetBehavior = null
        menuBottomSheetBehavior = null
        super.onDestroyView()
        _binding = null
    }

    private fun setupTracksBottomSheet() {
        binding.playlistTracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistTracksRecyclerView.adapter = trackAdapter

        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistTracksBottomSheet)

        binding.playlistContent.addOnLayoutChangeListener { content, _, _, _, _, _, _, _, _ ->
            content.post { updateTracksPeekHeight() }
        }
    }

    private fun updateTracksPeekHeight() {
        val binding = _binding ?: return
        val behavior = tracksBottomSheetBehavior ?: return

        val sheetTop = binding.playlistShareButton.bottom +
            resources.getDimensionPixelSize(R.dimen.playlist_tracks_sheet_margin_top)
        val minPeekHeight =
            resources.getDimensionPixelSize(R.dimen.playlist_tracks_sheet_min_peek_height)
        val peekHeight = (binding.playlistContent.height - sheetTop).coerceAtLeast(minPeekHeight)
        if (behavior.peekHeight != peekHeight) {
            behavior.peekHeight = peekHeight
        }
    }

    private fun setupMenuBottomSheet() {
        val behavior = BottomSheetBehavior.from(binding.playlistMenuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val isHidden = newState == BottomSheetBehavior.STATE_HIDDEN
                updateMenuOverlay(isMenuVisible = !isHidden)
                if (isHidden && isEditorPending) {
                    isEditorPending = false
                    navigateToEditor()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                _binding?.playlistMenuOverlay?.alpha = (slideOffset + 1f).coerceIn(0f, 1f)
            }
        })
        menuBottomSheetBehavior = behavior

        binding.playlistMenuOverlay.setOnClickListener { hideMenu() }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            closeMenuCallback,
        )
    }

    private fun showMenu() {
        menuBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideMenu() {
        menuBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun updateMenuOverlay(isMenuVisible: Boolean) {
        _binding?.playlistMenuOverlay?.isVisible = isMenuVisible
        closeMenuCallback.isEnabled = isMenuVisible
    }

    private fun openEditor() {
        if (menuBottomSheetBehavior?.state == BottomSheetBehavior.STATE_HIDDEN) {
            navigateToEditor()
        } else {
            isEditorPending = true
            hideMenu()
        }
    }

    private fun navigateToEditor() {
        val isStarted = viewLifecycleOwner.lifecycle.currentState
            .isAtLeast(Lifecycle.State.STARTED)
        if (!isStarted) return

        findNavController().navigate(
            R.id.action_playlistFragment_to_editPlaylistFragment,
            EditPlaylistFragment.createArgs(playlistId),
        )
    }

    private fun render(state: PlaylistScreenState) {
        val playlist = state.playlist
        val tracksCount = playlist.tracksCount

        binding.playlistTitle.text = playlist.name
        binding.playlistDescription.text = playlist.description
        binding.playlistDescription.isVisible = playlist.description.isNotBlank()
        binding.playlistInfo.text = getString(
            R.string.playlist_info,
            resources.getQuantityString(
                R.plurals.playlist_duration_minutes,
                state.durationMinutes,
                state.durationMinutes,
            ),
            resources.getQuantityString(R.plurals.playlist_tracks_count, tracksCount, tracksCount),
        )
        renderCover(playlist.coverPath)
        binding.playlistMenuPlaylist.bind(playlist)

        trackAdapter.tracks = state.tracks
        binding.playlistTracksRecyclerView.isVisible = state.tracks.isNotEmpty()
        binding.playlistEmptyMessage.isVisible = state.tracks.isEmpty()
    }

    private fun renderCover(coverPath: String?) {
        if (isCoverRendered && coverPath == renderedCoverPath) return

        isCoverRendered = true
        renderedCoverPath = coverPath
        binding.playlistImage.loadPlaylistCover(coverPath)
    }

    private fun handleEvent(event: PlaylistEvent) {
        when (event) {
            is PlaylistEvent.OpenPlayer -> findNavController().navigate(
                R.id.action_playlistFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(event.track),
            )

            is PlaylistEvent.ShowNothingToShare -> Toast.makeText(
                requireContext(),
                R.string.playlist_nothing_to_share,
                Toast.LENGTH_SHORT,
            ).show()

            is PlaylistEvent.Close -> findNavController().navigateUp()
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        dialog?.dismiss()
        dialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.playlist_delete_track_message)
            .setNegativeButton(R.string.playlist_delete_track_cancel, null)
            .setPositiveButton(R.string.playlist_delete_track_confirm) { _, _ ->
                viewModel.onDeleteTrackConfirmed(track)
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        dialog?.dismiss()
        dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.playlist_delete_title)
            .setMessage(R.string.playlist_delete_message)
            .setNegativeButton(R.string.playlist_delete_cancel, null)
            .setPositiveButton(R.string.playlist_delete_confirm) { _, _ ->
                viewModel.onDeletePlaylistConfirmed()
            }
            .show()
    }

    companion object {
        private const val ARGS_PLAYLIST_ID = "playlistId"

        fun createArgs(playlistId: Long): Bundle = bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }
}
