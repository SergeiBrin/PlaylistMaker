package com.example.playlistmaker.settings.domain.interactor.impl

import com.example.playlistmaker.settings.domain.interactor.api.ThemeInteractor
import com.example.playlistmaker.settings.domain.repository.AppRepository

class ThemeInteractorImpl(
    private val appRepository: AppRepository
) : ThemeInteractor {

    override fun getIsThemeDark(): Boolean {
        return appRepository.getIsThemeDark()
    }

    override fun switchTheme(checked: Boolean) {
        appRepository.switchTheme(checked)
    }

    override fun saveThemeSettings() {
        appRepository.saveThemeSettings()
    }
}