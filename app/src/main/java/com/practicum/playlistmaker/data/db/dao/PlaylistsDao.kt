package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {

    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("SELECT * FROM playlists ORDER BY id DESC")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylist(playlistId: Long): PlaylistEntity?

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun observePlaylist(playlistId: Long): Flow<PlaylistEntity?>
}
