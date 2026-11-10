package com.practicum.playlistmaker.data.db

import com.practicum.playlistmaker.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.domain.models.Track

class TrackDbMapper {

    fun toEntity(
        track: Track,
        addedAt: Long = System.currentTimeMillis(),
    ): FavoriteTrackEntity = FavoriteTrackEntity(
        trackId = track.trackId,
        trackName = track.trackName,
        artistName = track.artistName,
        trackTime = track.trackTime,
        artworkUrl = track.artworkUrl,
        coverArtworkUrl = track.coverArtworkUrl,
        collectionName = track.collectionName,
        releaseYear = track.releaseYear,
        primaryGenreName = track.primaryGenreName,
        country = track.country,
        previewUrl = track.previewUrl,
        addedAt = addedAt,
    )

    fun toPlaylistTrackEntity(
        track: Track,
        addedAt: Long = System.currentTimeMillis(),
    ): PlaylistTrackEntity = PlaylistTrackEntity(
        trackId = track.trackId,
        trackName = track.trackName,
        artistName = track.artistName,
        trackTime = track.trackTime,
        artworkUrl = track.artworkUrl,
        coverArtworkUrl = track.coverArtworkUrl,
        collectionName = track.collectionName,
        releaseYear = track.releaseYear,
        primaryGenreName = track.primaryGenreName,
        country = track.country,
        previewUrl = track.previewUrl,
        addedAt = addedAt,
    )

    fun toDomain(entity: FavoriteTrackEntity): Track = Track(
        trackId = entity.trackId,
        trackName = entity.trackName,
        artistName = entity.artistName,
        trackTime = entity.trackTime,
        artworkUrl = entity.artworkUrl,
        coverArtworkUrl = entity.coverArtworkUrl,
        collectionName = entity.collectionName,
        releaseYear = entity.releaseYear,
        primaryGenreName = entity.primaryGenreName,
        country = entity.country,
        previewUrl = entity.previewUrl,
        isFavorite = true,
    )
}
