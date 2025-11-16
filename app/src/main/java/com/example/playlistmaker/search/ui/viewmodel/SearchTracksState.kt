package com.example.playlistmaker.search.ui.viewmodel

import com.example.playlistmaker.core.model.Track

sealed interface SearchTracksState {
    object Idle : SearchTracksState
    object Loading : SearchTracksState

    data class Success(val tracks: List<Track>) : SearchTracksState
    object Error : SearchTracksState
}