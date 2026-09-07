package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.util.Resource

interface TracksInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun interface TracksConsumer {

        fun consume(result: Resource<List<Track>>)
    }
}
