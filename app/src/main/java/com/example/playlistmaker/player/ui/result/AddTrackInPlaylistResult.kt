package com.example.playlistmaker.player.ui.result

sealed class AddTrackInPlaylistResult {
    data class Success(val message: String) : AddTrackInPlaylistResult()
    data class Failure(val message: String) : AddTrackInPlaylistResult()
}