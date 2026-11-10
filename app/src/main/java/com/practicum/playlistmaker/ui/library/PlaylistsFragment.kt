package com.practicum.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.library.models.PlaylistsState
import com.practicum.playlistmaker.ui.library.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()

    private val playlistsAdapter = PlaylistsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistsRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
        binding.playlistsRecyclerView.adapter = playlistsAdapter

        binding.btnNewPlaylist.setOnClickListener { viewModel.onNewPlaylistClicked() }

        viewModel.observeState().observe(viewLifecycleOwner) { state -> render(state) }

        viewModel.observeOpenNewPlaylist().observe(viewLifecycleOwner) { openNewPlaylist() }
    }

    override fun onDestroyView() {
        binding.playlistsRecyclerView.adapter = null
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showEmpty()
            is PlaylistsState.Content -> showContent(state.playlists)
        }
    }

    private fun showEmpty() {
        playlistsAdapter.submitList(emptyList())
        binding.playlistsRecyclerView.isVisible = false
        binding.placeholderImage.isVisible = true
        binding.placeholderMessage.isVisible = true
    }

    private fun showContent(playlists: List<Playlist>) {
        playlistsAdapter.submitList(playlists)
        binding.placeholderImage.isVisible = false
        binding.placeholderMessage.isVisible = false
        binding.playlistsRecyclerView.isVisible = true
    }

    private fun openNewPlaylist() {
        findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistFragment)
    }

    companion object {
        private const val GRID_SPAN_COUNT = 2

        fun newInstance() = PlaylistsFragment()
    }
}
