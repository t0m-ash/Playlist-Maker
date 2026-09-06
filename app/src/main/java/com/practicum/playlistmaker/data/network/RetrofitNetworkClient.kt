package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TracksSearchRequest

class RetrofitNetworkClient(private val itunesService: ItunesApi) : NetworkClient {

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
        private const val BAD_REQUEST_CODE = 400
        private const val NETWORK_ERROR_CODE = -1
    }
}
