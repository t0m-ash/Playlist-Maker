package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryRepository {

    suspend fun getTracks(): List<Track>

    fun getSavedTracks(): List<Track>

    fun saveTracks(tracks: List<Track>)

    fun clear()
}
