package com.example.playlistmaker.player.ui.result

sealed class PlayerState(val progress: String) {
    class StateDefault : PlayerState("00:00")
    class StatePrepared : PlayerState("00:00")
    class StatePlaying(progress: String) : PlayerState(progress)
    class StatePaused(progress: String) : PlayerState(progress)
}