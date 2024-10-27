package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.history.SearchHistory
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.AppRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.interactor.ThemeInteractor
import com.example.playlistmaker.domain.api.interactor.TracksInteractor
import com.example.playlistmaker.domain.api.repository.AppRepository
import com.example.playlistmaker.domain.api.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.api.repository.TracksRepository
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

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