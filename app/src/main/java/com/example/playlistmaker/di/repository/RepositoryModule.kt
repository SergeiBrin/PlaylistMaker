package com.example.playlistmaker.di.repository

import com.example.playlistmaker.db.data.converters.TrackDbConverter
import com.example.playlistmaker.db.data.repository.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.db.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.settings.data.repository.AppRepositoryImpl
import com.example.playlistmaker.settings.domain.repository.AppRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    single<AppRepository> {
        AppRepositoryImpl(get())
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    factory {
        TrackDbConverter()
    }

}
