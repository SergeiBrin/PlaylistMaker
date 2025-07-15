package com.example.playlistmaker.player.ui.result

sealed class GetTrackResult {
    data object Success : GetTrackResult()
    data object Failure : GetTrackResult()
}