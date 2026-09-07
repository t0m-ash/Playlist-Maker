package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.StorageClient
import com.practicum.playlistmaker.data.dto.TrackHistoryDto
import com.practicum.playlistmaker.data.network.ItunesApi
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.search.TrackMapper
import com.practicum.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.practicum.playlistmaker.data.storage.PrefsStorageClient
import com.practicum.playlistmaker.domain.sharing.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val ITUNES_BASE_URL = "https://itunes.apple.com"
private const val PREFS_NAME = "playlist_maker_prefs"
private const val HISTORY_KEY = "search_history"

val dataModule = module {

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single {
        androidContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    factory<ExecutorService> { Executors.newCachedThreadPool() }

    factory { MediaPlayer() }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext(), get())
    }

    single<StorageClient<ArrayList<TrackHistoryDto>>> {
        PrefsStorageClient(
            prefs = get(),
            gson = get(),
            dataKey = HISTORY_KEY,
            type = object : TypeToken<ArrayList<TrackHistoryDto>>() {}.type,
        )
    }

    single { TrackMapper() }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
}
