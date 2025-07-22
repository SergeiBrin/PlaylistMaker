package com.example.playlistmaker.playlist.ui.result

import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.core.model.Track

data class PlaylistUiResult(
    val playlist: Playlist,
    val tracks: List<Track>
)