package com.example.playlistmaker.player.ui.service

import com.example.playlistmaker.player.ui.result.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {

    fun play()

    fun pause()

    fun startForeground()

    fun stopForeground()

    fun getPlayerState(): StateFlow<PlayerState>

}