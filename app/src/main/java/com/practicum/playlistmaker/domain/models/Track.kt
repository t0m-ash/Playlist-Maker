package com.practicum.playlistmaker.domain.models

import java.io.Serializable

data class Track(
    val trackId: Long, // Идентификатор трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTime: String,
    val artworkUrl: String,
    val coverArtworkUrl: String,
    val collectionName: String,
    val releaseYear: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
) : Serializable
