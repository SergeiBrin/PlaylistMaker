package com.example.playlistmaker.settings.ui.application

import android.app.Application
import android.app.UiModeManager
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.app.appModule
import com.example.playlistmaker.di.data.dataModule
import com.example.playlistmaker.di.domain.interactorModule
import com.example.playlistmaker.di.player.mediaPlayerModule
import com.example.playlistmaker.di.repository.repositoryModule
import com.example.playlistmaker.di.viewmodel.viewModelModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.qualifier.named

class App : Application() {
    var isDarkTheme = false
    private val settings: SharedPreferences by inject(named("PlaylistMakerSettings"))

    companion object {
        const val THEME_SETTINGS_KEY = "Theme"
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                appModule,
                dataModule,
                repositoryModule,
                interactorModule,
                viewModelModule,
                mediaPlayerModule
            )
        }

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