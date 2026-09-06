package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {

    fun getTracks(): List<Track>

    fun addTrack(track: Track)

    fun clear()
}
