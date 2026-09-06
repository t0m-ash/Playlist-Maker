package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): List<Track>? {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        if (response.resultCode != SUCCESS_CODE || response !is TracksSearchResponse) {
            return null
        }

        return response.results.orEmpty().map { TrackMapper.toDomain(it) }
    }

    companion object {
        private const val SUCCESS_CODE = 200
    }
}
