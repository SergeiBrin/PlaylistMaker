package com.example.playlistmaker.player.ui.result

sealed class AddTrackInPlaylistResult {
    data class Success(val playlistMame: String) : AddTrackInPlaylistResult()
    data class Failure(val playlistMame: String) : AddTrackInPlaylistResult()
}