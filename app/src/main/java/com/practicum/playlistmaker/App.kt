package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        applyTheme(Creator.provideSettingsInteractor(this).isDarkThemeEnabled())
    }

    companion object {

        fun applyTheme(darkThemeEnabled: Boolean) {
            AppCompatDelegate.setDefaultNightMode(
                if (darkThemeEnabled) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }
}
