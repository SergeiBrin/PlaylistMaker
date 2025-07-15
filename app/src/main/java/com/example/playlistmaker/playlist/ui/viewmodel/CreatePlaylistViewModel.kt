package com.example.playlistmaker.playlist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.db.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    fun insertPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.insertPlaylist(playlist)
        }
    }
}