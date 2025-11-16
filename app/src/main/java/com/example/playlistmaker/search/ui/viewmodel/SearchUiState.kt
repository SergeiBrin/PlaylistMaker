package com.example.playlistmaker.search.ui.viewmodel

import com.example.playlistmaker.core.model.Track

data class SearchUiState(
    val tracksState: SearchTracksState,
    val historyTracks: List<Track>
)