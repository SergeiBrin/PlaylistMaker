package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.interactor.ThemeInteractor
import com.example.playlistmaker.domain.api.repository.AppRepository

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