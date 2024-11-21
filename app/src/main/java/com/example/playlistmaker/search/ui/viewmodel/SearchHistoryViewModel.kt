package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.interactor.api.SearchHistoryInteractor
import com.example.playlistmaker.settings.ui.application.App

class SearchHistoryViewModel(
    private val historyInteractor: SearchHistoryInteractor
) : ViewModel() {
    private val historyTracksLiveData = MutableLiveData<List<Track>>(emptyList())

    fun getHistoryTracksLiveData(): LiveData<List<Track>> = historyTracksLiveData

    fun downloadSearchHistory() {
        historyInteractor.downloadSearchHistory()
        historyTracksLiveData.postValue(historyInteractor.historyTrackList)
    }

    fun saveTrackInHistoryTrackList(track: Track) {
        historyInteractor.saveTrackInHistoryTrackList(track)
        historyTracksLiveData.postValue(historyInteractor.historyTrackList)
    }

    fun saveSearchHistoryInPreferences() {
        historyInteractor.saveSearchHistoryInPreferences()
    }

    fun deleteSearchHistory() {
        historyInteractor.deleteSearchHistory()
        historyTracksLiveData.postValue(emptyList())
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App
                SearchHistoryViewModel(Creator.provideSearchHistoryInteractor(app))
            }
        }
    }
}