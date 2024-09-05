package com.example.playlistmaker.sharedpref

import android.app.Application
import android.app.UiModeManager
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    var isDarkTheme = false
    lateinit var settings: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        settings = getThemeSettings(this)
        val theme = settings.getString("theme", null)

        if (theme != null) {
            switchTheme(theme.toBoolean())
        } else {
            updateSwitcher()
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        isDarkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun saveThemeSettings() {
        settings.edit()
            .putString("theme", isDarkTheme.toString())
            .apply()
    }

    fun updateSwitcher() {
        val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        val isNightMode = uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES

        if (isNightMode) {
            isDarkTheme = true
        } else {
            isDarkTheme = false
        }
    }
}