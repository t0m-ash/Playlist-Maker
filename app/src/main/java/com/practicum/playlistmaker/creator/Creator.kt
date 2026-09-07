package com.practicum.playlistmaker.creator

import android.content.Context
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.StorageClient
import com.practicum.playlistmaker.data.dto.TrackHistoryDto
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.player.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.search.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.search.TracksRepositoryImpl
import com.practicum.playlistmaker.data.settings.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.practicum.playlistmaker.data.sharing.SharingRepositoryImpl
import com.practicum.playlistmaker.data.storage.PrefsStorageClient
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.PlayerRepository
import com.practicum.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.search.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.search.SearchHistoryRepository
import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.domain.search.TracksRepository
import com.practicum.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.practicum.playlistmaker.domain.settings.SettingsInteractor
import com.practicum.playlistmaker.domain.settings.SettingsRepository
import com.practicum.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.SharingRepository
import com.practicum.playlistmaker.domain.sharing.impl.SharingInteractorImpl

object Creator {

    private const val HISTORY_KEY = "search_history"

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val networkClient: NetworkClient by lazy { RetrofitNetworkClient(appContext) }

    private fun getTracksRepository(): TracksRepository = TracksRepositoryImpl(networkClient)

    fun provideTracksInteractor(): TracksInteractor =
        TracksInteractorImpl(getTracksRepository())

    private fun getHistoryStorageClient(): StorageClient<ArrayList<TrackHistoryDto>> =
        PrefsStorageClient(
            context = appContext,
            dataKey = HISTORY_KEY,
            type = object : TypeToken<ArrayList<TrackHistoryDto>>() {}.type,
        )

    private fun getSearchHistoryRepository(): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(getHistoryStorageClient())

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor =
        SearchHistoryInteractorImpl(getSearchHistoryRepository())

    private fun getSettingsRepository(): SettingsRepository = SettingsRepositoryImpl(appContext)

    fun provideSettingsInteractor(): SettingsInteractor =
        SettingsInteractorImpl(getSettingsRepository())

    private fun getExternalNavigator(): ExternalNavigator = ExternalNavigatorImpl(appContext)

    private fun getSharingRepository(): SharingRepository = SharingRepositoryImpl(appContext)

    fun provideSharingInteractor(): SharingInteractor =
        SharingInteractorImpl(getExternalNavigator(), getSharingRepository())

    private fun getPlayerRepository(): PlayerRepository = PlayerRepositoryImpl()

    fun providePlayerInteractor(): PlayerInteractor =
        PlayerInteractorImpl(getPlayerRepository())
}
