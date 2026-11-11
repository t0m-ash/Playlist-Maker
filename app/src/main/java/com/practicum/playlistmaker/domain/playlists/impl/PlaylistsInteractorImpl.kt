package com.practicum.playlistmaker.domain.playlists.impl

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.domain.playlists.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository,
) : PlaylistsInteractor {

    override suspend fun createPlaylist(name: String, description: String, coverUri: String?) {
        repository.createPlaylist(name, description, coverUri)
    }

    override suspend fun updatePlaylist(
        playlistId: Long,
        name: String,
        description: String,
        newCoverUri: String?,
    ) {
        repository.updatePlaylist(playlistId, name, description, newCoverUri)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = repository.getPlaylists()

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> =
        repository.getPlaylist(playlistId)

    override fun getTracks(trackIds: List<Long>): Flow<List<Track>> =
        repository.getTracks(trackIds)

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean =
        repository.addTrackToPlaylist(track, playlistId)

    override suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long) {
        repository.deleteTrackFromPlaylist(trackId, playlistId)
    }
}
