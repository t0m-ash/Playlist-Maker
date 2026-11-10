package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.data.StorageClient
import com.practicum.playlistmaker.data.db.dao.FavoriteTracksDao
import com.practicum.playlistmaker.data.dto.TrackHistoryDto
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.search.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<TrackHistoryDto>>,
    private val trackMapper: TrackMapper,
    private val favoriteTracksDao: FavoriteTracksDao,
) : SearchHistoryRepository {

    override suspend fun getTracks(): List<Track> {
        val favoriteIds = favoriteTracksDao.getTrackIds().toSet()
        return getSavedTracks().map { it.copy(isFavorite = it.trackId in favoriteIds) }
    }

    override fun getSavedTracks(): List<Track> =
        storage.getData().orEmpty().map { trackMapper.toDomain(it) }

    override fun saveTracks(tracks: List<Track>) {
        storage.storeData(ArrayList(tracks.map { trackMapper.toHistoryDto(it) }))
    }

    override fun clear() {
        storage.storeData(arrayListOf())
    }
}
