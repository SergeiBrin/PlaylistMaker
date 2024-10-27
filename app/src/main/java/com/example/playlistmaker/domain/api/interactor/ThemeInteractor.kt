package com.example.playlistmaker.domain.api.interactor

interface ThemeInteractor {

    fun getIsThemeDark(): Boolean

    fun switchTheme(checked: Boolean)

    fun saveThemeSettings()
}