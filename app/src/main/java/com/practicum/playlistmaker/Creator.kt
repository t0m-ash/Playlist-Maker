package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.practicum.playlistmaker.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.ItunesApi
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private const val PREFS_NAME = "playlist_maker_prefs"
    private const val ITUNES_BASE_URL = "https://itunes.apple.com"

    private val itunesService: ItunesApi by lazy {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    private fun getSharedPreferences(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun getTracksRepository(): TracksRepository =
        TracksRepositoryImpl(RetrofitNetworkClient(itunesService))

    fun provideTracksInteractor(): TracksInteractor =
        TracksInteractorImpl(getTracksRepository())

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(getSharedPreferences(context))

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor =
        SearchHistoryInteractorImpl(getSearchHistoryRepository(context))

    private fun getSettingsRepository(context: Context): SettingsRepository =
        SettingsRepositoryImpl(context.applicationContext, getSharedPreferences(context))

    fun provideSettingsInteractor(context: Context): SettingsInteractor =
        SettingsInteractorImpl(getSettingsRepository(context))

    private fun getPlayerRepository(): PlayerRepository = PlayerRepositoryImpl(MediaPlayer())

    fun providePlayerInteractor(): PlayerInteractor =
        PlayerInteractorImpl(getPlayerRepository())
}
