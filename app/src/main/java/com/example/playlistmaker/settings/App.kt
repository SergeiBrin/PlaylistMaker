package com.example.playlistmaker.settings

import android.app.Application
import android.app.UiModeManager
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.util.getThemeSettings

class App : Application() {
    var isDarkTheme = false
    lateinit var settings: SharedPreferences

    companion object {
        lateinit var instance: App
            private set
        const val THEME_SETTINGS_KEY = "Theme"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        settings = getThemeSettings(this)
        val theme = settings.getString(THEME_SETTINGS_KEY, null)

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
            .putString(THEME_SETTINGS_KEY, isDarkTheme.toString())
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