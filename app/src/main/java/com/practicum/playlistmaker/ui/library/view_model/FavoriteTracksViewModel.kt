package com.practicum.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.favorites.FavoriteTracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.library.models.FavoriteTracksState
import com.practicum.playlistmaker.util.ClickDebouncer
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteTracksState>()
    fun observeState(): LiveData<FavoriteTracksState> = stateLiveData

    private val showPlayerEvent = SingleLiveEvent<Track>()
    fun observeShowPlayer(): LiveData<Track> = showPlayerEvent

    private val clickDebouncer = ClickDebouncer(viewModelScope)

    init {
        viewModelScope.launch {
            favoriteTracksInteractor
                .getTracks()
                .collect { tracks -> processResult(tracks) }
        }
    }

    fun onTrackClicked(track: Track) {
        if (!clickDebouncer.tryClick()) return

        showPlayerEvent.setValue(track)
    }

    private fun processResult(tracks: List<Track>) {
        stateLiveData.value = if (tracks.isEmpty()) {
            FavoriteTracksState.Empty
        } else {
            FavoriteTracksState.Content(tracks)
        }
    }
}
