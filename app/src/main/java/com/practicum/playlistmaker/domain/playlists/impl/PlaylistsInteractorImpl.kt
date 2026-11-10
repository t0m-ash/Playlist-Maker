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

    override fun getPlaylists(): Flow<List<Playlist>> = repository.getPlaylists()

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean =
        repository.addTrackToPlaylist(track, playlistId)
}
