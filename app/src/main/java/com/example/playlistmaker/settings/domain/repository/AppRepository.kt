package com.example.playlistmaker.settings.domain.repository

interface AppRepository {

    fun getIsThemeDark(): Boolean

    fun switchTheme(checked: Boolean)

    fun saveThemeSettings()

}