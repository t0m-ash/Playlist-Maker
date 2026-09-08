package com.practicum.playlistmaker.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.AudioPlayerFragment
import com.practicum.playlistmaker.ui.search.models.SearchState
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private val trackAdapter = TrackAdapter { track -> viewModel.onTrackClicked(track) }
    private val historyAdapter = TrackAdapter { track -> viewModel.onTrackClicked(track) }

    private var textWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRecyclerView.adapter = trackAdapter

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter = historyAdapter

        viewModel.observeState().observe(viewLifecycleOwner) { state -> render(state) }

        viewModel.observeShowPlayer().observe(viewLifecycleOwner) { track -> openPlayer(track) }

        binding.clearIcon.setOnClickListener {
            binding.searchEditText.setText("")
            hideKeyboard()
        }

        binding.clearHistoryButton.setOnClickListener { viewModel.onClearHistoryClicked() }

        binding.refreshButton.setOnClickListener { viewModel.onRefreshClicked() }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                viewModel.onSearchTextChanged(s?.toString().orEmpty())
            }

            override fun afterTextChanged(s: Editable?) = Unit
        }
        textWatcher?.let { binding.searchEditText.addTextChangedListener(it) }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onSearchFocusChanged(hasFocus)
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onSearchActionDone(binding.searchEditText.text.toString())
                true
            } else {
                false
            }
        }
    }

    override fun onDestroyView() {
        textWatcher?.let { binding.searchEditText.removeTextChangedListener(it) }
        textWatcher = null
        binding.tracksRecyclerView.adapter = null
        binding.historyRecyclerView.adapter = null
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Idle -> showIdle()

            is SearchState.Loading -> showLoading()

            is SearchState.History -> showHistory(state.tracks)

            is SearchState.Content -> showContent(state.tracks)

            is SearchState.Empty -> showPlaceholder(
                message = getString(R.string.search_nothing_found),
                image = R.drawable.ic_nothing_found,
                showRefresh = false,
            )

            is SearchState.Error -> showPlaceholder(
                message = getString(R.string.search_connection_problem),
                image = R.drawable.ic_connection_problem,
                showRefresh = true,
            )
        }
    }

    private fun showIdle() {
        binding.apply {
            progressBar.isVisible = false
            placeholderLayout.isVisible = false
            searchHistoryLayout.isVisible = false
            tracksRecyclerView.isVisible = false
        }
        trackAdapter.tracks = emptyList()
    }

    private fun showLoading() {
        binding.apply {
            placeholderLayout.isVisible = false
            searchHistoryLayout.isVisible = false
            tracksRecyclerView.isVisible = false
            progressBar.isVisible = true
        }
    }

    private fun showHistory(tracks: List<Track>) {
        historyAdapter.tracks = tracks
        binding.apply {
            progressBar.isVisible = false
            placeholderLayout.isVisible = false
            tracksRecyclerView.isVisible = false
            searchHistoryLayout.isVisible = true
        }
    }

    private fun showContent(tracks: List<Track>) {
        trackAdapter.tracks = tracks
        binding.apply {
            progressBar.isVisible = false
            placeholderLayout.isVisible = false
            searchHistoryLayout.isVisible = false
            tracksRecyclerView.isVisible = true
        }
    }

    private fun showPlaceholder(message: String, image: Int, showRefresh: Boolean) {
        trackAdapter.tracks = emptyList()
        binding.apply {
            progressBar.isVisible = false
            searchHistoryLayout.isVisible = false
            tracksRecyclerView.isVisible = false
            placeholderImage.setImageResource(image)
            placeholderMessage.text = message
            refreshButton.isVisible = showRefresh
            placeholderLayout.isVisible = true
        }
    }

    private fun openPlayer(track: Track) {
        hideKeyboard()
        findNavController().navigate(
            R.id.action_searchFragment_to_audioPlayerFragment,
            AudioPlayerFragment.createArgs(track),
        )
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }
}
