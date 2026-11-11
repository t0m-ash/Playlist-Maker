package com.practicum.playlistmaker.data.sharing

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.sharing.SharingRepository
import com.practicum.playlistmaker.domain.sharing.models.EmailData

class SharingRepositoryImpl(private val context: Context) : SharingRepository {

    override fun getShareAppLink(): String = context.getString(R.string.share_app_url)

    override fun getTermsLink(): String = context.getString(R.string.user_agreement_url)

    override fun getSupportEmailData(): EmailData = EmailData(
        address = context.getString(R.string.support_email),
        subject = context.getString(R.string.support_email_subject),
        text = context.getString(R.string.support_email_text),
    )

    override fun getPlaylistShareText(playlist: Playlist, tracks: List<Track>): String {
        val lines = buildList {
            add(playlist.name)
            if (playlist.description.isNotBlank()) add(playlist.description)
            add(
                context.resources.getQuantityString(
                    R.plurals.playlist_tracks_count,
                    tracks.size,
                    tracks.size,
                )
            )
            tracks.forEachIndexed { index, track ->
                add(
                    context.getString(
                        R.string.playlist_share_track,
                        index + 1,
                        track.artistName,
                        track.trackName,
                        track.trackTime,
                    )
                )
            }
        }
        return lines.joinToString(separator = "\n")
    }
}
