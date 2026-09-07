package com.practicum.playlistmaker.domain.search.impl

import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.domain.search.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression))
        }
    }
}
