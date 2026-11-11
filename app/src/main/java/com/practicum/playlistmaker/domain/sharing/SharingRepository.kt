package com.practicum.playlistmaker.domain.sharing

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.sharing.models.EmailData

interface SharingRepository {

    fun getShareAppLink(): String

    fun getTermsLink(): String

    fun getSupportEmailData(): EmailData

    fun getPlaylistShareText(playlist: Playlist, tracks: List<Track>): String
}
