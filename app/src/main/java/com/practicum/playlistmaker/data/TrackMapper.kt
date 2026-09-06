package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.data.dto.TrackHistoryDto
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

object TrackMapper {

    fun toDomain(dto: TrackDto): Track = Track(
        trackId = dto.trackId ?: 0L,
        trackName = dto.trackName.orEmpty(),
        artistName = dto.artistName.orEmpty(),
        trackTime = formatTrackTime(dto.trackTimeMillis),
        artworkUrl = dto.artworkUrl100.orEmpty(),
        coverArtworkUrl = formatCoverArtwork(dto.artworkUrl100),
        collectionName = dto.collectionName.orEmpty(),
        releaseYear = formatReleaseYear(dto.releaseDate),
        primaryGenreName = dto.primaryGenreName.orEmpty(),
        country = dto.country.orEmpty(),
        previewUrl = dto.previewUrl.orEmpty(),
    )

    fun toDomain(dto: TrackHistoryDto): Track = Track(
        trackId = dto.trackId ?: 0L,
        trackName = dto.trackName.orEmpty(),
        artistName = dto.artistName.orEmpty(),
        trackTime = dto.trackTime.orEmpty(),
        artworkUrl = dto.artworkUrl.orEmpty(),
        coverArtworkUrl = dto.coverArtworkUrl.orEmpty(),
        collectionName = dto.collectionName.orEmpty(),
        releaseYear = dto.releaseYear.orEmpty(),
        primaryGenreName = dto.primaryGenreName.orEmpty(),
        country = dto.country.orEmpty(),
        previewUrl = dto.previewUrl.orEmpty(),
    )

    fun toHistoryDto(track: Track): TrackHistoryDto = TrackHistoryDto(
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
    )

    private fun formatTrackTime(trackTimeMillis: Long?): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis ?: 0L)

    private fun formatReleaseYear(releaseDate: String?): String =
        releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4).orEmpty()

    private fun formatCoverArtwork(artworkUrl100: String?): String =
        artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg").orEmpty()
}
