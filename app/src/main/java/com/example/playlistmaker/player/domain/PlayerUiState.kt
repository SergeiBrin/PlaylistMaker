package com.example.playlistmaker.player.domain

import com.example.playlistmaker.core.model.Track

sealed class PlayerUiState {
    data object FavoriteTrackSuccessEvent : PlayerUiState()
    data object FavoriteTrackFailureEvent : PlayerUiState()

    data class CurrentTrackTimeLiveData(val trackTime: String) : PlayerUiState()

    data class TrackData(val track: Track) : PlayerUiState()
}