package com.practicum.playlistmaker.util

sealed interface Resource<T> {

    data class Success<T>(val data: T) : Resource<T>

    class Error<T> : Resource<T>
}
