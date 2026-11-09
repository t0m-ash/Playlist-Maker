package com.practicum.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val context: Context,
    private val itunesService: ItunesApi,
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION_CODE }
        }

        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = BAD_REQUEST_CODE }
        }

        return withContext(Dispatchers.IO) {
            try {
                itunesService.search(dto.term).apply { resultCode = Response.SUCCESS_CODE }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Response().apply { resultCode = SERVER_ERROR_CODE }
            }
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
        private const val NO_CONNECTION_CODE = -1
        private const val BAD_REQUEST_CODE = 400
        private const val SERVER_ERROR_CODE = 500
    }
}
