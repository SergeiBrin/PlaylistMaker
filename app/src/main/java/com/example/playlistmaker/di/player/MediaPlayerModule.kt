package com.example.playlistmaker.di.player

import android.media.MediaPlayer
import org.koin.dsl.module

val mediaPlayerModule = module {
    factory {
        MediaPlayer()
    }
}