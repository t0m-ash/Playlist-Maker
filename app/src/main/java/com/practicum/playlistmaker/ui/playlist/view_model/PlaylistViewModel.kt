package com.practicum.playlistmaker.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.domain.sharing.SharingInteractor
import com.practicum.playlistmaker.ui.playlist.models.PlaylistEvent
import com.practicum.playlistmaker.ui.playlist.models.PlaylistScreenState
import com.practicum.playlistmaker.util.ClickDebouncer
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit

class PlaylistViewModel(
    private val playlistId: Long,
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistScreenState>()
    fun observeState(): LiveData<PlaylistScreenState> = stateLiveData

    private val eventLiveData = SingleLiveEvent<PlaylistEvent>()
    fun observeEvents(): LiveData<PlaylistEvent> = eventLiveData

    private val clickDebouncer = ClickDebouncer(viewModelScope)

    private val deleteMutex = Mutex()

    private var isPlaylistDeleting = false

    init {
        observePlaylist()
    }

    fun onTrackClicked(track: Track) {
        if (!clickDebouncer.tryClick()) return

        eventLiveData.setValue(PlaylistEvent.OpenPlayer(track))
    }

    fun onShareClicked() {
        val state = stateLiveData.value ?: return
        if (state.tracks.isEmpty()) {
            eventLiveData.setValue(PlaylistEvent.ShowNothingToShare)
            return
        }
        if (!clickDebouncer.tryClick()) return

        sharingInteractor.sharePlaylist(state.playlist, state.tracks)
    }

    fun onDeleteTrackConfirmed(track: Track) {
        viewModelScope.launch {
            deleteMutex.withLock {
                playlistsInteractor.deleteTrackFromPlaylist(track.trackId, playlistId)
            }
        }
    }

    fun onDeletePlaylistConfirmed() {
        if (isPlaylistDeleting) return

        isPlaylistDeleting = true
        viewModelScope.launch {
            deleteMutex.withLock {
                playlistsInteractor.deletePlaylist(playlistId)
            }
            eventLiveData.setValue(PlaylistEvent.Close)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observePlaylist() {
        viewModelScope.launch {
            playlistsInteractor
                .getPlaylist(playlistId)
                .flatMapLatest { playlist ->
                    if (playlist == null) {
                        flowOf<PlaylistScreenState?>(null)
                    } else {
                        playlistsInteractor.getTracks(playlist.trackIds).map { tracks ->
                            PlaylistScreenState(
                                playlist = playlist,
                                tracks = tracks,
                                durationMinutes = calculateDurationMinutes(tracks),
                            )
                        }
                    }
                }
                .collect { state -> processState(state) }
        }
    }

    private fun processState(state: PlaylistScreenState?) {
        when {
            state != null -> stateLiveData.value = state
            !isPlaylistDeleting -> eventLiveData.setValue(PlaylistEvent.Close)
        }
    }

    private fun calculateDurationMinutes(tracks: List<Track>): Int {
        val durationMillis = tracks.sumOf { track -> parseTrackTimeMillis(track.trackTime) }
        return TimeUnit.MILLISECONDS.toMinutes(durationMillis).toInt()
    }

    private fun parseTrackTimeMillis(trackTime: String): Long {
        val parts = trackTime.split(TRACK_TIME_SEPARATOR)
        val minutes = parts.getOrNull(0)?.toLongOrNull() ?: 0L
        val seconds = parts.getOrNull(1)?.toLongOrNull() ?: 0L
        return TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.SECONDS.toMillis(seconds)
    }

    companion object {
        private const val TRACK_TIME_SEPARATOR = ":"
    }
}
