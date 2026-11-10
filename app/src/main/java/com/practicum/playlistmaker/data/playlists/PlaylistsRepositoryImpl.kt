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
        val coverPath = coverUri?.let { uri ->
            withContext(Dispatchers.IO) { imageStorage.saveImage(uri) }
        }
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

    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistsDao.getPlaylists().map { entities ->
            entities.map { playlistDbMapper.toDomain(it) }
        }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        val currentPlaylist = playlistsDao.getPlaylist(playlist.id)
            ?.let { playlistDbMapper.toDomain(it) }
            ?: return false
        if (track.trackId in currentPlaylist.trackIds) return false

        playlistTracksDao.insertTrack(trackDbMapper.toPlaylistTrackEntity(track))
        val updatedPlaylist = currentPlaylist.copy(
            trackIds = currentPlaylist.trackIds + track.trackId,
            tracksCount = currentPlaylist.tracksCount + 1,
        )
        playlistsDao.updatePlaylist(playlistDbMapper.toEntity(updatedPlaylist))
        return true
    }

    companion object {
        private const val NEW_PLAYLIST_ID = 0L
    }
}
