package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.search.domain.common.Resource
import com.example.playlistmaker.search.domain.interactor.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.api.TracksInteractor
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : ViewModel() {
    private val tracksStateLiveData = MutableLiveData<SearchTracksState>(SearchTracksState.Idle)
    fun getTracksStateLiveData(): LiveData<SearchTracksState> = tracksStateLiveData

    private val historyTracksLiveData = MutableLiveData<List<Track>>()
    fun getHistoryTracksLiveData(): LiveData<List<Track>> = historyTracksLiveData

    init {
        downloadSearchHistory()
    }

    fun searchTracks(inputText: String) {
        viewModelScope.launch {
            tracksStateLiveData.postValue(SearchTracksState.Loading)

            tracksInteractor.searchTracks(inputText).collect { result ->
                when (result) {
                    is Resource.Success -> tracksStateLiveData
                        .postValue(SearchTracksState.Success(result.data))
                    is Resource.Error -> tracksStateLiveData
                        .postValue(SearchTracksState.Error)
                }
            }
        }
    }

    fun clearSearchTracksState() {
        tracksStateLiveData.postValue(SearchTracksState.Idle)
    }

    fun downloadSearchHistory() {
        viewModelScope.launch {
            historyInteractor.downloadSearchHistory()
            historyTracksLiveData.postValue(historyInteractor.historyTrackList.toList())
        }
    }

    fun saveTrackInHistoryTrackList(track: Track) {
        historyInteractor.saveTrackInHistoryTrackList(track)
        historyTracksLiveData.postValue(historyInteractor.historyTrackList.toList())
    }

    fun saveSearchHistoryInPreferences() {
        historyInteractor.saveSearchHistoryInPreferences()
    }

    fun deleteSearchHistory() {
        historyInteractor.deleteSearchHistory()
        historyTracksLiveData.postValue(listOf())
    }
}