package com.practicum.playlistmaker.domain.playlists

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    suspend fun createPlaylist(name: String, description: String, coverUri: String?)

    suspend fun updatePlaylist(
        playlistId: Long,
        name: String,
        description: String,
        newCoverUri: String?,
    )

    suspend fun deletePlaylist(playlistId: Long)

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylist(playlistId: Long): Flow<Playlist?>

    fun getTracks(trackIds: List<Long>): Flow<List<Track>>

    suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean

    suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long)
}
