package com.example.playlistmaker.medialibrary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.db.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

class PlaylistsViewModel(
    val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    val playlists: StateFlow<List<Playlist>> =
        playlistInteractor.getAllPlaylists()
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}