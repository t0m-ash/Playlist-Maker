package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {

    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
}
