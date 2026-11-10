package com.practicum.playlistmaker.data.favorites

import com.practicum.playlistmaker.data.db.TrackDbMapper
import com.practicum.playlistmaker.data.db.dao.FavoriteTracksDao
import com.practicum.playlistmaker.domain.favorites.FavoriteTracksRepository
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val favoriteTracksDao: FavoriteTracksDao,
    private val trackDbMapper: TrackDbMapper,
) : FavoriteTracksRepository {

    override suspend fun addTrack(track: Track) {
        favoriteTracksDao.insertTrack(trackDbMapper.toEntity(track))
    }

    override suspend fun deleteTrack(track: Track) {
        favoriteTracksDao.deleteTrack(trackDbMapper.toEntity(track))
    }

    override fun getTracks(): Flow<List<Track>> =
        favoriteTracksDao.getTracks().map { entities ->
            entities.map { trackDbMapper.toDomain(it) }
        }

    override suspend fun isFavorite(trackId: Long): Boolean =
        favoriteTracksDao.isFavorite(trackId)
}
