package com.practicum.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(private val context: Context) : NetworkClient {

    private val itunesService: ItunesApi = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ItunesApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION_CODE }
        }

        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = BAD_REQUEST_CODE }
        }

        return try {
            val response = itunesService.search(dto.term).execute()
            val body = response.body() ?: Response()
            body.apply { resultCode = response.code() }
        } catch (e: Exception) {
            Response().apply { resultCode = SERVER_ERROR_CODE }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    companion object {
        private const val ITUNES_BASE_URL = "https://itunes.apple.com"

        private const val NO_CONNECTION_CODE = -1
        private const val BAD_REQUEST_CODE = 400
        private const val SERVER_ERROR_CODE = 500
    }
}
