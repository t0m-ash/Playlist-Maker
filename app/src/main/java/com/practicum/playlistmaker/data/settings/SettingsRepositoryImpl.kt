package com.practicum.playlistmaker.data.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.data.storage.appPreferences
import com.practicum.playlistmaker.domain.settings.SettingsRepository
import com.practicum.playlistmaker.domain.settings.models.ThemeSettings

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    private val prefs: SharedPreferences = appPreferences(context)

    override fun getThemeSettings(): ThemeSettings =
        ThemeSettings(prefs.getBoolean(DARK_THEME_KEY, isSystemDarkTheme()))

    override fun updateThemeSetting(settings: ThemeSettings) {
        prefs.edit()
            .putBoolean(DARK_THEME_KEY, settings.darkTheme)
            .apply()

        applyTheme(settings)
    }

    private fun applyTheme(settings: ThemeSettings) {
        AppCompatDelegate.setDefaultNightMode(
            if (settings.darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun isSystemDarkTheme(): Boolean =
        (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES

    companion object {
        private const val DARK_THEME_KEY = "dark_theme"
    }
}
