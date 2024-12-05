package com.example.playlistmaker.di.app

import com.example.playlistmaker.settings.ui.application.App
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {

    single {
        androidApplication() as App
    }

}