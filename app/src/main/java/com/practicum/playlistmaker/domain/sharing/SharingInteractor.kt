package com.practicum.playlistmaker.domain.sharing

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track

interface SharingInteractor {

    fun shareApp()

    fun sharePlaylist(playlist: Playlist, tracks: List<Track>)

    fun openTerms()

    fun openSupport()
}
