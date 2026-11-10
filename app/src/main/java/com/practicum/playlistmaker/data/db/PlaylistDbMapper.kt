package com.practicum.playlistmaker.data.db

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.domain.models.Playlist

class PlaylistDbMapper(
    private val gson: Gson,
) {

    private val trackIdsType = object : TypeToken<List<Long>>() {}.type

    fun toEntity(playlist: Playlist): PlaylistEntity = PlaylistEntity(
        id = playlist.id,
        name = playlist.name,
        description = playlist.description,
        coverPath = playlist.coverPath,
        trackIds = gson.toJson(playlist.trackIds, trackIdsType),
        tracksCount = playlist.tracksCount,
    )

    fun toDomain(entity: PlaylistEntity): Playlist = Playlist(
        id = entity.id,
        name = entity.name,
        description = entity.description,
        coverPath = entity.coverPath,
        trackIds = gson.fromJson<List<Long>>(entity.trackIds, trackIdsType).orEmpty(),
        tracksCount = entity.tracksCount,
    )
}
