package com.practicum.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.ui.library.models.PlaylistsState
import com.practicum.playlistmaker.util.ClickDebouncer
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    private val openNewPlaylistEvent = SingleLiveEvent<Unit>()
    fun observeOpenNewPlaylist(): LiveData<Unit> = openNewPlaylistEvent

    private val openPlaylistEvent = SingleLiveEvent<Long>()
    fun observeOpenPlaylist(): LiveData<Long> = openPlaylistEvent

    private val clickDebouncer = ClickDebouncer(viewModelScope)

    init {
        viewModelScope.launch {
            playlistsInteractor
                .getPlaylists()
                .collect { playlists -> processResult(playlists) }
        }
    }

    fun onNewPlaylistClicked() {
        if (!clickDebouncer.tryClick()) return

        openNewPlaylistEvent.setValue(Unit)
    }

    fun onPlaylistClicked(playlist: Playlist) {
        if (!clickDebouncer.tryClick()) return

        openPlaylistEvent.setValue(playlist.id)
    }

    private fun processResult(playlists: List<Playlist>) {
        stateLiveData.value = if (playlists.isEmpty()) {
            PlaylistsState.Empty
        } else {
            PlaylistsState.Content(playlists)
        }
    }
}
