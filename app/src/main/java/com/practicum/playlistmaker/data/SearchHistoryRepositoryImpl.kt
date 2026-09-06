package com.practicum.playlistmaker.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.dto.TrackHistoryDto
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : SearchHistoryRepository {

    private val gson = Gson()

    override fun getTracks(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<TrackHistoryDto>>() {}.type
        val dtos: List<TrackHistoryDto> = gson.fromJson(json, type) ?: return emptyList()
        return dtos.map { TrackMapper.toDomain(it) }
    }

    override fun saveTracks(tracks: List<Track>) {
        val dtos = tracks.map { TrackMapper.toHistoryDto(it) }
        sharedPreferences.edit()
            .putString(HISTORY_KEY, gson.toJson(dtos))
            .apply()
    }

    override fun clear() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

    companion object {
        private const val HISTORY_KEY = "search_history"
    }
}
