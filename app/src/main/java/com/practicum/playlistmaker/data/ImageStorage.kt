package com.practicum.playlistmaker.data

interface ImageStorage {

    fun saveImage(uri: String): String?

    fun deleteImage(path: String)
}
