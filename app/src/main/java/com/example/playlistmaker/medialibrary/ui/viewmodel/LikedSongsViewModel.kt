package com.example.playlistmaker.medialibrary.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.db.domain.interactor.FavoriteTracksInteractor
import kotlinx.coroutines.launch

class LikedSongsViewModel(
    val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val favoriteTracks = MutableLiveData<List<Track>>()
    fun getFavoriteTracks(): LiveData<List<Track>> = favoriteTracks

    fun getFavoriteTracksOfDb() {
        viewModelScope.launch {
            favoriteTracksInteractor.getAllTracks().collect {
                favoriteTracks.postValue(it)
            }
        }
    }
}