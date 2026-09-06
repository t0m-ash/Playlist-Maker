package com.practicum.playlistmaker.domain.api

interface SettingsInteractor {

    fun isDarkThemeEnabled(): Boolean

    fun updateDarkTheme(enabled: Boolean)
}
