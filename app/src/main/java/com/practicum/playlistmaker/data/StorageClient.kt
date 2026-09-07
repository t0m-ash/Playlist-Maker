package com.practicum.playlistmaker.data

interface StorageClient<T> {

    fun storeData(data: T)

    fun getData(): T?
}
