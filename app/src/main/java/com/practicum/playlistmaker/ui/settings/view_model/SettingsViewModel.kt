package com.practicum.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.settings.SettingsInteractor
import com.practicum.playlistmaker.domain.settings.models.ThemeSettings
import com.practicum.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private val themeSettingsLiveData =
        MutableLiveData(settingsInteractor.getThemeSettings())

    fun observeThemeSettings(): LiveData<ThemeSettings> = themeSettingsLiveData

    fun onThemeSwitchClicked(darkThemeEnabled: Boolean) {
        val settings = ThemeSettings(darkThemeEnabled)
        settingsInteractor.updateThemeSetting(settings)
        themeSettingsLiveData.postValue(settings)
    }

    fun onShareAppClicked() {
        sharingInteractor.shareApp()
    }

    fun onSupportClicked() {
        sharingInteractor.openSupport()
    }

    fun onTermsClicked() {
        sharingInteractor.openTerms()
    }

    companion object {

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    settingsInteractor = Creator.provideSettingsInteractor(),
                    sharingInteractor = Creator.provideSharingInteractor(),
                )
            }
        }
    }
}
