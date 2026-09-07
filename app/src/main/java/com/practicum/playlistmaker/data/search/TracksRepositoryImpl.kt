package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.search.TracksRepository
import com.practicum.playlistmaker.util.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        if (response.resultCode != SUCCESS_CODE || response !is TracksSearchResponse) {
            return Resource.Error()
        }

        return Resource.Success(response.results.orEmpty().map { TrackMapper.toDomain(it) })
    }

    companion object {
        private const val SUCCESS_CODE = 200
    }
}
