package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.interactor.api.TracksInteractor

class SearchViewModel(
    private val tracksInteractor: TracksInteractor
) : ViewModel() {
    private val tracksLiveData = MutableLiveData<List<Track>?>()

    fun getTracksLiveData(): LiveData<List<Track>?> = tracksLiveData

    fun searchTracks(inputText: String) {
        tracksInteractor.searchTracks(inputText, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?) {
                tracksLiveData.postValue(foundTracks)
            }
        })
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(Creator.provideTracksInteractor())
            }
        }
    }
}