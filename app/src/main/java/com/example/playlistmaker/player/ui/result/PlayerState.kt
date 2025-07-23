package com.example.playlistmaker.player.ui.result

sealed class PlayerState {
    object StateDefault : PlayerState()
    object StatePrepared : PlayerState()
    object StatePlaying : PlayerState()
    object StatePaused : PlayerState()
}