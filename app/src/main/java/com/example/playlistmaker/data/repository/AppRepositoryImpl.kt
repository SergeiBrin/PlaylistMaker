package com.example.playlistmaker.data.repository

import com.example.playlistmaker.domain.api.repository.AppRepository
import com.example.playlistmaker.settings.App

class AppRepositoryImpl : AppRepository {

    companion object {
        val app = App.instance
    }

    override fun getIsThemeDark(): Boolean {
        return app.isDarkTheme
    }

    override fun switchTheme(checked: Boolean) {
        app.switchTheme(checked)
    }

    override fun saveThemeSettings() {
        app.saveThemeSettings()
    }
}