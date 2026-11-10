package com.practicum.playlistmaker.domain.favorites.impl

import com.practicum.playlistmaker.domain.favorites.FavoriteTracksInteractor
import com.practicum.playlistmaker.domain.favorites.FavoriteTracksRepository
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository,
) : FavoriteTracksInteractor {

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        repository.deleteTrack(track)
    }

    override fun getTracks(): Flow<List<Track>> = repository.getTracks()

    override suspend fun isFavorite(trackId: Long): Boolean = repository.isFavorite(trackId)
}
