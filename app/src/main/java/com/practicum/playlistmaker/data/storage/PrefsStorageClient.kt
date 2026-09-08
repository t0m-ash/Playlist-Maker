package com.practicum.playlistmaker.data.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.data.StorageClient
import java.lang.reflect.Type

class PrefsStorageClient<T>(
    private val prefs: SharedPreferences,
    private val gson: Gson,
    private val dataKey: String,
    private val type: Type,
) : StorageClient<T> {

    override fun storeData(data: T) {
        prefs.edit()
            .putString(dataKey, gson.toJson(data, type))
            .apply()
    }

    override fun getData(): T? {
        val dataJson = prefs.getString(dataKey, null) ?: return null
        return gson.fromJson(dataJson, type)
    }
}
