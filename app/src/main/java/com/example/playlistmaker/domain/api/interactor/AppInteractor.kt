package com.example.playlistmaker.domain.api.interactor

interface AppInteractor {
    fun getTheme(): Boolean

    fun switchTheme(checked: Boolean)

    fun saveThemeSettings()
}