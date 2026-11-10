package com.practicum.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.common.TrackAdapter
import com.practicum.playlistmaker.ui.library.models.FavoriteTracksState
import com.practicum.playlistmaker.ui.library.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.ui.player.AudioPlayerFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteTracksViewModel by viewModel()

    private val trackAdapter = TrackAdapter { track -> viewModel.onTrackClicked(track) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favoriteTracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoriteTracksRecyclerView.adapter = trackAdapter

        viewModel.observeState().observe(viewLifecycleOwner) { state -> render(state) }

        viewModel.observeShowPlayer().observe(viewLifecycleOwner) { track -> openPlayer(track) }
    }

    override fun onDestroyView() {
        binding.favoriteTracksRecyclerView.adapter = null
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: FavoriteTracksState) {
        when (state) {
            is FavoriteTracksState.Empty -> showEmpty()
            is FavoriteTracksState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmpty() {
        trackAdapter.tracks = emptyList()
        binding.favoriteTracksRecyclerView.isVisible = false
        binding.placeholderLayout.isVisible = true
    }

    private fun showContent(tracks: List<Track>) {
        trackAdapter.tracks = tracks
        binding.placeholderLayout.isVisible = false
        binding.favoriteTracksRecyclerView.isVisible = true
    }

    private fun openPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_libraryFragment_to_audioPlayerFragment,
            AudioPlayerFragment.createArgs(track),
        )
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}
