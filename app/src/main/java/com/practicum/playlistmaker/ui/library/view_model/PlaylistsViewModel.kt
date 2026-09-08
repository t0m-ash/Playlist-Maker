package com.practicum.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.ui.library.models.PlaylistsState

class PlaylistsViewModel : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistsState>(PlaylistsState.Empty)

    fun observeState(): LiveData<PlaylistsState> = stateLiveData
}
