package com.example.playlistmaker.listeners

import com.example.playlistmaker.model.Track

fun interface TrackClickListener {
    fun onTrackClick(track: Track)
}