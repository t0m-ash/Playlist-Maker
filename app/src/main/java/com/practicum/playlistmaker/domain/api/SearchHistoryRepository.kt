package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryRepository {

    fun getTracks(): List<Track>

    fun saveTracks(tracks: List<Track>)

    fun clear()
}
