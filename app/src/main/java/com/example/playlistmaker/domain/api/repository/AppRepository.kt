package com.example.playlistmaker.domain.api.repository

interface AppRepository {

    fun getIsThemeDark(): Boolean

    fun switchTheme(checked: Boolean)

    fun saveThemeSettings()
}