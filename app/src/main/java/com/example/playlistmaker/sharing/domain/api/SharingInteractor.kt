package com.example.playlistmaker.sharing.domain.api

import android.content.Intent

interface SharingInteractor {

    fun getShareAppIntent(url: String): Intent

    fun getSupportContactIntent(email: String, subject: String, text: String): Intent

    fun getUserAgreementIntent(url: String): Intent

}