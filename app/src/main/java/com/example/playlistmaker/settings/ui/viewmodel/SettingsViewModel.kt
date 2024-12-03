package com.example.playlistmaker.settings.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.util.Event
import com.example.playlistmaker.settings.domain.interactor.api.ThemeInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val themeInteractor: ThemeInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {
    private val themeSwitcherLiveData = MutableLiveData<Event<Boolean>>()

    private val sharingLiveData = MutableLiveData<Event<Intent>>()

    init {
        themeSwitcherLiveData.postValue(Event(themeInteractor.getIsThemeDark()))
    }

    fun getThemeSwitcherLiveData(): LiveData<Event<Boolean>> = themeSwitcherLiveData

    fun getSharingLiveData(): LiveData<Event<Intent>> = sharingLiveData

    fun switchTheme(checked: Boolean) {
        themeInteractor.switchTheme(checked)
        themeSwitcherLiveData.postValue(Event(checked))
    }

    fun saveThemeSettings() {
        themeInteractor.saveThemeSettings()
    }

    fun createShareAppIntent(url: String) {
        val appIntent = sharingInteractor.getShareAppIntent(url)
        sharingLiveData.postValue(Event(appIntent))
    }

    fun createSupportContactIntent(email: String, subject: String, text: String) {
        val supportContactIntent = sharingInteractor.getSupportContactIntent(email, subject, text)
        sharingLiveData.postValue(Event(supportContactIntent))
    }

    fun createUserAgreementIntent(url: String) {
        val userAgreementIntent = sharingInteractor.getUserAgreementIntent(url)
        sharingLiveData.postValue(Event(userAgreementIntent))
    }
}