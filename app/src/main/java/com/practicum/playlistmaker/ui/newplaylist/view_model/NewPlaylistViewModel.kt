package com.practicum.playlistmaker.ui.newplaylist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.ui.newplaylist.models.NewPlaylistEvent
import com.practicum.playlistmaker.ui.newplaylist.models.NewPlaylistState
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var name: String = savedStateHandle[KEY_NAME] ?: ""
    private var description: String = savedStateHandle[KEY_DESCRIPTION] ?: ""
    private var coverUri: String? = savedStateHandle[KEY_COVER_URI]

    private val stateLiveData = MutableLiveData(buildState())
    fun observeState(): LiveData<NewPlaylistState> = stateLiveData

    private val eventLiveData = SingleLiveEvent<NewPlaylistEvent>()
    fun observeEvents(): LiveData<NewPlaylistEvent> = eventLiveData

    private var createJob: Job? = null

    fun onNameChanged(text: String) {
        name = text
        savedStateHandle[KEY_NAME] = text
        updateState()
    }

    fun onDescriptionChanged(text: String) {
        description = text
        savedStateHandle[KEY_DESCRIPTION] = text
    }

    fun onCoverPicked(uri: String) {
        coverUri = uri
        savedStateHandle[KEY_COVER_URI] = uri
        updateState()
    }

    fun onBackPressed() {
        if (createJob?.isActive == true) return

        val event = if (hasUnsavedData()) {
            NewPlaylistEvent.ShowDiscardDialog
        } else {
            NewPlaylistEvent.Close
        }
        eventLiveData.setValue(event)
    }

    fun onDiscardConfirmed() {
        eventLiveData.setValue(NewPlaylistEvent.Close)
    }

    fun onCreateClicked() {
        val playlistName = name.trim()
        if (playlistName.isEmpty() || createJob?.isActive == true) return

        createJob = viewModelScope.launch {
            playlistsInteractor.createPlaylist(
                name = playlistName,
                description = description.trim(),
                coverUri = coverUri,
            )
            eventLiveData.setValue(NewPlaylistEvent.PlaylistCreated(playlistName))
        }
    }

    private fun hasUnsavedData(): Boolean =
        coverUri != null || name.isNotEmpty() || description.isNotEmpty()

    private fun buildState(): NewPlaylistState = NewPlaylistState(
        coverUri = coverUri,
        isCreateEnabled = name.isNotBlank(),
    )

    private fun updateState() {
        val newState = buildState()
        if (newState != stateLiveData.value) {
            stateLiveData.value = newState
        }
    }

    companion object {
        private const val KEY_NAME = "name"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_COVER_URI = "cover_uri"
    }
}
