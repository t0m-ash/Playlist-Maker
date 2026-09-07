package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryRepository {

    fun getTracks(): List<Track>

    fun saveTracks(tracks: List<Track>)

    fun clear()
}
