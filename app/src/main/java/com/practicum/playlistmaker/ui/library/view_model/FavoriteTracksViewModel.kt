package com.practicum.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.ui.library.models.FavoriteTracksState

class FavoriteTracksViewModel : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteTracksState>(FavoriteTracksState.Empty)

    fun observeState(): LiveData<FavoriteTracksState> = stateLiveData
}
