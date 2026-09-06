package com.practicum.playlistmaker.data.dto

data class TrackHistoryDto(
    val trackId: Long?,
    val trackName: String?,
    val artistName: String?,
    val trackTime: String?,
    val artworkUrl: String?,
    val coverArtworkUrl: String?,
    val collectionName: String?,
    val releaseYear: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
)
