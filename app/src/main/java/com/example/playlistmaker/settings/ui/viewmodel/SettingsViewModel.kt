package com.example.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.core.event.SingleLiveEvent
import com.example.playlistmaker.settings.domain.interactor.api.ThemeInteractor

class SettingsViewModel(
    private val themeInteractor: ThemeInteractor
) : ViewModel() {
    private val themeSwitcherLiveData = SingleLiveEvent<Boolean>()

    init {
        themeSwitcherLiveData.value = themeInteractor.getIsThemeDark()
    }

    fun getThemeSwitcherLiveData(): LiveData<Boolean> = themeSwitcherLiveData

    fun switchTheme(checked: Boolean) {
        themeInteractor.switchTheme(checked)
        themeSwitcherLiveData.value = checked
    }

    fun saveThemeSettings() {
        themeInteractor.saveThemeSettings()
    }
}