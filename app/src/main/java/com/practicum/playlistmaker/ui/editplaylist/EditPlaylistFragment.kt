package com.practicum.playlistmaker.ui.editplaylist

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.editplaylist.models.EditPlaylistInfo
import com.practicum.playlistmaker.ui.editplaylist.view_model.EditPlaylistViewModel
import com.practicum.playlistmaker.ui.newplaylist.NewPlaylistFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment : NewPlaylistFragment() {

    private val playlistId: Long by lazy { requireArguments().getLong(ARGS_PLAYLIST_ID) }

    override val viewModel: EditPlaylistViewModel by viewModel { parametersOf(playlistId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistTitle.setText(R.string.edit_playlist_title)
        binding.newPlaylistCreateButton.setText(R.string.edit_playlist_save)

        viewModel.observePlaylistInfo().observe(viewLifecycleOwner) { info -> fillFields(info) }
    }

    private fun fillFields(info: EditPlaylistInfo) {
        binding.newPlaylistNameEditText.setText(info.name)
        binding.newPlaylistDescriptionEditText.setText(info.description)
    }

    companion object {
        private const val ARGS_PLAYLIST_ID = "playlistId"

        fun createArgs(playlistId: Long): Bundle = bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }
}
