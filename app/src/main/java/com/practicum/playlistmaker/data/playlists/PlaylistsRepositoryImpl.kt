package com.practicum.playlistmaker.data.playlists

import com.practicum.playlistmaker.data.ImageStorage
import com.practicum.playlistmaker.data.db.PlaylistDbMapper
import com.practicum.playlistmaker.data.db.TrackDbMapper
import com.practicum.playlistmaker.data.db.dao.PlaylistTracksDao
import com.practicum.playlistmaker.data.db.dao.PlaylistsDao
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.playlists.PlaylistsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaylistsRepositoryImpl(
    private val playlistsDao: PlaylistsDao,
    private val playlistTracksDao: PlaylistTracksDao,
    private val playlistDbMapper: PlaylistDbMapper,
    private val trackDbMapper: TrackDbMapper,
    private val imageStorage: ImageStorage,
) : PlaylistsRepository {

    override suspend fun createPlaylist(name: String, description: String, coverUri: String?) {
        val coverPath = coverUri?.let { saveCover(it) }
        val playlist = Playlist(
            id = NEW_PLAYLIST_ID,
            name = name,
            description = description,
            coverPath = coverPath,
            trackIds = emptyList(),
            tracksCount = 0,
        )
        playlistsDao.insertPlaylist(playlistDbMapper.toEntity(playlist))
    }

    override suspend fun updatePlaylist(
        playlistId: Long,
        name: String,
        description: String,
        newCoverUri: String?,
    ) {
        val playlist = findPlaylist(playlistId) ?: return
        val newCoverPath = newCoverUri?.let { saveCover(it) }
        val updatedPlaylist = playlist.copy(
            name = name,
            description = description,
            coverPath = newCoverPath ?: playlist.coverPath,
        )
        playlistsDao.updatePlaylist(playlistDbMapper.toEntity(updatedPlaylist))

        val oldCoverPath = playlist.coverPath
        if (newCoverPath != null && oldCoverPath != null) {
            deleteCover(oldCoverPath)
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        val playlist = findPlaylist(playlistId) ?: return
        playlistsDao.deletePlaylist(playlistId)
        deleteTracksNotInAnyPlaylist(playlist.trackIds)
        playlist.coverPath?.let { deleteCover(it) }
    }

    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistsDao.getPlaylists().map { entities ->
            entities.map { playlistDbMapper.toDomain(it) }
        }

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> =
        playlistsDao.observePlaylist(playlistId).map { entity ->
            entity?.let { playlistDbMapper.toDomain(it) }
        }

    override fun getTracks(trackIds: List<Long>): Flow<List<Track>> =
        playlistTracksDao.getTracks().map { entities ->
            val tracksById = entities.associateBy { it.trackId }
            trackIds.asReversed().mapNotNull { trackId ->
                tracksById[trackId]?.let { trackDbMapper.toDomain(it) }
            }
        }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean {
        val currentPlaylist = findPlaylist(playlistId) ?: return false
        if (track.trackId in currentPlaylist.trackIds) return false

        playlistTracksDao.insertTrack(trackDbMapper.toPlaylistTrackEntity(track))
        val updatedPlaylist = currentPlaylist.copy(
            trackIds = currentPlaylist.trackIds + track.trackId,
            tracksCount = currentPlaylist.tracksCount + 1,
        )
        playlistsDao.updatePlaylist(playlistDbMapper.toEntity(updatedPlaylist))
        return true
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long) {
        val currentPlaylist = findPlaylist(playlistId) ?: return
        if (trackId !in currentPlaylist.trackIds) return

        val trackIds = currentPlaylist.trackIds - trackId
        val updatedPlaylist = currentPlaylist.copy(
            trackIds = trackIds,
            tracksCount = trackIds.size,
        )
        playlistsDao.updatePlaylist(playlistDbMapper.toEntity(updatedPlaylist))
        deleteTracksNotInAnyPlaylist(listOf(trackId))
    }

    private suspend fun findPlaylist(playlistId: Long): Playlist? =
        playlistsDao.getPlaylist(playlistId)?.let { playlistDbMapper.toDomain(it) }

    private suspend fun deleteTracksNotInAnyPlaylist(trackIds: List<Long>) {
        if (trackIds.isEmpty()) return

        val trackIdsInPlaylists = playlistsDao.getAllPlaylists()
            .flatMapTo(HashSet()) { playlistDbMapper.toDomain(it).trackIds }
        trackIds
            .filterNot { it in trackIdsInPlaylists }
            .forEach { playlistTracksDao.deleteTrack(it) }
    }

    private suspend fun saveCover(uri: String): String? =
        withContext(Dispatchers.IO) { imageStorage.saveImage(uri) }

    private suspend fun deleteCover(path: String) {
        withContext(Dispatchers.IO) { imageStorage.deleteImage(path) }
    }

    companion object {
        private const val NEW_PLAYLIST_ID = 0L
    }
}
