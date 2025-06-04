package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.search.domain.interactor.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.api.TracksInteractor
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : ViewModel() {
    private val tracksLiveData = MutableLiveData<List<Track>?>()
    private val historyTracksLiveData = MutableLiveData<List<Track>>(emptyList())

    fun getTracksLiveData(): LiveData<List<Track>?> = tracksLiveData

    fun getHistoryTracksLiveData(): LiveData<List<Track>> = historyTracksLiveData

    fun searchTracks(inputText: String) {
        viewModelScope.launch {
            tracksInteractor.searchTracks(inputText).collect { result ->
                tracksLiveData.postValue(result)
            }
        }
    }

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
}