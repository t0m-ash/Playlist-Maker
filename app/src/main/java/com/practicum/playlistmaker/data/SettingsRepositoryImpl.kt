package com.practicum.playlistmaker.data

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.practicum.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
) : SettingsRepository {

    override fun isDarkThemeEnabled(): Boolean =
        sharedPreferences.getBoolean(DARK_THEME_KEY, isSystemDarkTheme())

    override fun updateDarkTheme(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, enabled)
            .apply()
    }

    private fun isSystemDarkTheme(): Boolean =
        (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES

    companion object {
        private const val DARK_THEME_KEY = "dark_theme"
    }
}
