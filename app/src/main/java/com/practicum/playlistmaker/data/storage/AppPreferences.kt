package com.practicum.playlistmaker.data.storage

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "playlist_maker_prefs"

internal fun appPreferences(context: Context): SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
