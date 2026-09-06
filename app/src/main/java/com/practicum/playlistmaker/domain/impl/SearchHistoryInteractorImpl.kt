package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository,
) : SearchHistoryInteractor {

    override fun getTracks(): List<Track> = repository.getTracks()

    override fun addTrack(track: Track) {
        val tracks = repository.getTracks().toMutableList()
        tracks.removeAll { it.trackId == track.trackId }
        tracks.add(0, track)
        while (tracks.size > MAX_HISTORY_SIZE) {
            tracks.removeAt(tracks.lastIndex)
        }
        repository.saveTracks(tracks)
    }

    override fun clear() {
        repository.clear()
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }
}
