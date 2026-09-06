package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = BAD_REQUEST_CODE }
        }

        return try {
            val response = itunesService.search(dto.term).execute()
            val body = response.body() ?: Response()
            body.apply { resultCode = response.code() }
        } catch (e: Exception) {
            Response().apply { resultCode = NETWORK_ERROR_CODE }
        }
    }

    companion object {
        private const val ITUNES_BASE_URL = "https://itunes.apple.com"
        private const val BAD_REQUEST_CODE = 400
        private const val NETWORK_ERROR_CODE = -1
    }
}
