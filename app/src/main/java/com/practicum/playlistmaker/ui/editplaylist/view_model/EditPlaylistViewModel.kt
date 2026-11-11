package com.practicum.playlistmaker.ui.editplaylist.view_model

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.ui.editplaylist.models.EditPlaylistInfo
import com.practicum.playlistmaker.ui.newplaylist.models.NewPlaylistEvent
import com.practicum.playlistmaker.ui.newplaylist.view_model.NewPlaylistViewModel
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class EditPlaylistViewModel(
    private val playlistId: Long,
    playlistsInteractor: PlaylistsInteractor,
    savedStateHandle: SavedStateHandle,
) : NewPlaylistViewModel(playlistsInteractor, savedStateHandle) {

    private val playlistInfoEvent = SingleLiveEvent<EditPlaylistInfo>()
    fun observePlaylistInfo(): LiveData<EditPlaylistInfo> = playlistInfoEvent

    private var initialCoverUri: String? = savedStateHandle[KEY_INITIAL_COVER_URI]

    init {
        if (savedStateHandle.get<Boolean>(KEY_IS_LOADED) != true) {
            loadPlaylist()
        }
    }

    override fun onBackPressed() {
        if (isSaving()) return

        sendEvent(NewPlaylistEvent.Close)
    }

    override fun onCreateClicked() {
        val playlistName = name.trim()
        if (playlistName.isEmpty() || isSaving()) return

        val newCoverUri = coverUri.takeIf { it != initialCoverUri }
        saveJob = viewModelScope.launch {
            playlistsInteractor.updatePlaylist(
                playlistId = playlistId,
                name = playlistName,
                description = description.trim(),
                newCoverUri = newCoverUri,
            )
            sendEvent(NewPlaylistEvent.Close)
        }
    }

    private fun loadPlaylist() {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylist(playlistId).first()
            if (playlist == null) {
                sendEvent(NewPlaylistEvent.Close)
                return@launch
            }

            val playlistCoverUri = playlist.coverPath?.let { File(it).toUri().toString() }
            initialCoverUri = playlistCoverUri
            savedStateHandle[KEY_INITIAL_COVER_URI] = playlistCoverUri
            savedStateHandle[KEY_IS_LOADED] = true

            playlistCoverUri?.let { onCoverPicked(it) }
            playlistInfoEvent.setValue(EditPlaylistInfo(playlist.name, playlist.description))
        }
    }

    companion object {
        private const val KEY_IS_LOADED = "is_loaded"
        private const val KEY_INITIAL_COVER_URI = "initial_cover_uri"
    }
}
