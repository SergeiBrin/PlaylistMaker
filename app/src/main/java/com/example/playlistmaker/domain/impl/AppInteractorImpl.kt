package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.interactor.AppInteractor
import com.example.playlistmaker.settings.App

class AppInteractorImpl : AppInteractor {

    companion object {
        val app = App.instance
    }

    override fun getTheme(): Boolean {
        return app.isDarkTheme
    }

    override fun switchTheme(checked: Boolean) {
        app.switchTheme(checked)
    }

    override fun saveThemeSettings() {
        app.saveThemeSettings()
    }
}