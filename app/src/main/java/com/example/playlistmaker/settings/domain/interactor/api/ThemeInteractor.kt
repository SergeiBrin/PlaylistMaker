package com.example.playlistmaker.settings.domain.interactor.api

interface ThemeInteractor {

    fun getIsThemeDark(): Boolean

    fun switchTheme(checked: Boolean)

    fun saveThemeSettings()
}