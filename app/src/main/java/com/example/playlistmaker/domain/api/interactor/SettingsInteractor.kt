package com.example.playlistmaker.domain.api.interactor

import android.content.Intent

interface SettingsInteractor {

    fun getShareAppIntent(url: String): Intent

    fun getSupportContactIntent(email: String, subject: String, text: String): Intent

    fun getUserAgreementIntent(url: String): Intent
}