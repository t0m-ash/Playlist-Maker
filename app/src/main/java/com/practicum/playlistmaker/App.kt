package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val systemDark = (resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, systemDark)
        applyTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
            .putBoolean(DARK_THEME_KEY, darkThemeEnabled)
            .apply()
        applyTheme(darkThemeEnabled)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        const val PREFS_NAME = "playlist_maker_prefs"
        const val DARK_THEME_KEY = "dark_theme"
    }
}
