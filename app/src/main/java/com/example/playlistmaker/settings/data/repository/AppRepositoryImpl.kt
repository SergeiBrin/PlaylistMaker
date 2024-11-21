package com.example.playlistmaker.settings.data.repository

import com.example.playlistmaker.settings.domain.repository.AppRepository
import com.example.playlistmaker.settings.ui.application.App

class AppRepositoryImpl : AppRepository {

    override fun getIsThemeDark(): Boolean {
        return app.isDarkTheme
    }

    override fun switchTheme(checked: Boolean) {
        app.switchTheme(checked)
    }

    override fun saveThemeSettings() {
        app.saveThemeSettings()
    }

    companion object {
        val app = App.instance
    }
}