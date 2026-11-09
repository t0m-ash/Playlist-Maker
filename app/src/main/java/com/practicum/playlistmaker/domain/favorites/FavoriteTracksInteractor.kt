package com.practicum.playlistmaker.domain.favorites

import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {

    suspend fun addTrack(track: Track)

    suspend fun deleteTrack(track: Track)

    fun getTracks(): Flow<List<Track>>

    suspend fun isFavorite(trackId: Long): Boolean
}
