package com.example.playlistmaker.player.ui.funinterface

import com.example.playlistmaker.core.model.Playlist

fun interface PlaylistClickListener {

    fun onPlaylistClick(playlist: Playlist)

}