package com.practicum.playlistmaker.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.data.StorageClient
import java.lang.reflect.Type

class PrefsStorageClient<T>(
    context: Context,
    private val dataKey: String,
    private val type: Type,
) : StorageClient<T> {

    private val prefs: SharedPreferences = appPreferences(context)
    private val gson = Gson()

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
