package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.search.data.history.SearchHistory
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.settings.data.repository.AppRepositoryImpl
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.interactor.api.SearchHistoryInteractor
import com.example.playlistmaker.settings.domain.interactor.api.ThemeInteractor
import com.example.playlistmaker.search.domain.interactor.api.TracksInteractor
import com.example.playlistmaker.settings.domain.repository.AppRepository
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.domain.interactor.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.settings.domain.interactor.impl.ThemeInteractorImpl
import com.example.playlistmaker.search.domain.interactor.impl.TracksInteractorImpl

object Creator {

    // Интерактор для поиска треков
    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    // Интерактор для истории треков
    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
         return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(SearchHistory(context))
    }

    // Интерактор для темы
    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(getAppRepository())
    }

    private fun getAppRepository(): AppRepository {
        return AppRepositoryImpl()
    }

}