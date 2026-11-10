package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.data.favorites.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.data.player.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.playlists.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.data.search.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.search.TracksRepositoryImpl
import com.practicum.playlistmaker.data.settings.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.sharing.SharingRepositoryImpl
import com.practicum.playlistmaker.domain.favorites.FavoriteTracksRepository
import com.practicum.playlistmaker.domain.player.PlayerRepository
import com.practicum.playlistmaker.domain.playlists.PlaylistsRepository
import com.practicum.playlistmaker.domain.search.SearchHistoryRepository
import com.practicum.playlistmaker.domain.search.TracksRepository
import com.practicum.playlistmaker.domain.settings.SettingsRepository
import com.practicum.playlistmaker.domain.sharing.SharingRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(androidContext(), get())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(androidContext())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get(), get(), get(), get())
    }
}
