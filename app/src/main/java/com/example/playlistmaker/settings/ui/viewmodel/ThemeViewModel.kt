package com.example.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.interactor.api.ThemeInteractor
import com.example.playlistmaker.settings.ui.application.App
import java.security.Provider

class ThemeViewModel(
    private val themeInteractor: ThemeInteractor
) : ViewModel() {
    private val themeSwitcherLiveData = MutableLiveData<Boolean>()

    init {
        themeSwitcherLiveData.postValue(themeInteractor.getIsThemeDark())
    }

    fun getThemeSwitcherLiveData(): LiveData<Boolean> = themeSwitcherLiveData

    fun switchTheme(checked: Boolean) {
        themeInteractor.switchTheme(checked)
        themeSwitcherLiveData.postValue(checked)
    }

    fun saveThemeSettings() {
        themeInteractor.saveThemeSettings()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ThemeViewModel(Creator.provideThemeInteractor())
            }
        }
    }
}