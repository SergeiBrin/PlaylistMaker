package com.example.playlistmaker.di.domain

import com.example.playlistmaker.search.domain.interactor.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.api.TracksInteractor
import com.example.playlistmaker.search.domain.interactor.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.interactor.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.interactor.api.ThemeInteractor
import com.example.playlistmaker.settings.domain.interactor.impl.ThemeInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<ThemeInteractor> {
        ThemeInteractorImpl(get())
    }

}