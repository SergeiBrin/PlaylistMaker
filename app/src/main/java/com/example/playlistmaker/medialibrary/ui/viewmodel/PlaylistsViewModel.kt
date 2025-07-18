package com.example.playlistmaker.medialibrary.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.db.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    val myPlaylistsLiveData = MutableLiveData<List<Playlist>>()
    fun getMyPlaylistsLiveData(): LiveData<List<Playlist>> = myPlaylistsLiveData

    fun getAllPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect {
                myPlaylistsLiveData.postValue(it)
            }
        }
    }
}