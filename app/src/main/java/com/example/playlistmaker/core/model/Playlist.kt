package com.example.playlistmaker.core.model

import android.net.Uri

data class Playlist(
    val id: Int = 0,
    val playlistName: String,
    val playlistDescription: String,
    val playlistImageUri: Uri?,
    val trackIds: MutableList<Int> = mutableListOf(),
    val trackCount: Int = 0
)