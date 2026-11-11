package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.editplaylist.view_model.EditPlaylistViewModel
import com.practicum.playlistmaker.ui.library.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.ui.library.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.ui.newplaylist.view_model.NewPlaylistViewModel
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel { (track: Track) ->
        PlayerViewModel(track, get(), get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get(), get())
    }

    viewModel { (playlistId: Long) ->
        PlaylistViewModel(playlistId, get(), get())
    }

    viewModel { (playlistId: Long) ->
        EditPlaylistViewModel(playlistId, get(), get())
    }
}
