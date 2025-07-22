package com.example.playlistmaker.player.ui.result

import com.example.playlistmaker.core.model.Track

sealed class PlayerUiState {
    data class TrackData(val track: Track) : PlayerUiState()
    data class CurrentTrackTimeLiveData(val trackTime: String) : PlayerUiState()

    data object FavoriteTrackSuccessEvent : PlayerUiState()
    data object FavoriteTrackFailureEvent : PlayerUiState()
}