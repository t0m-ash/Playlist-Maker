package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    fun getTracks(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return arrayListOf()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type) ?: arrayListOf()
    }

    fun addTrack(track: Track) {
        val tracks = getTracks().toMutableList()
        tracks.removeAll { it.trackId == track.trackId }
        tracks.add(0, track)
        while (tracks.size > MAX_HISTORY_SIZE) {
            tracks.removeAt(tracks.lastIndex)
        }
        save(tracks)
    }

    fun clear() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

    private fun save(tracks: List<Track>) {
        sharedPreferences.edit()
            .putString(HISTORY_KEY, gson.toJson(tracks))
            .apply()
    }

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }
}
