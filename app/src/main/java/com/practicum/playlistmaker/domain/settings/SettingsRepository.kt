package com.practicum.playlistmaker.domain.settings

import com.practicum.playlistmaker.domain.settings.models.ThemeSettings

interface SettingsRepository {

    fun getThemeSettings(): ThemeSettings

    fun updateThemeSetting(settings: ThemeSettings)
}
