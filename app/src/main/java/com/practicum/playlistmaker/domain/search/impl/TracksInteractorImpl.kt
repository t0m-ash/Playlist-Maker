package com.practicum.playlistmaker.domain.search.impl

import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.domain.search.TracksRepository
import java.util.concurrent.ExecutorService

class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val executor: ExecutorService,
) : TracksInteractor {

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression))
        }
    }
}
