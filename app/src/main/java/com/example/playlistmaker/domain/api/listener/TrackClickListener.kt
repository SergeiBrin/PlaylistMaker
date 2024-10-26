package com.example.playlistmaker.domain.api.listener

import com.example.playlistmaker.domain.models.Track

fun interface TrackClickListener {
    fun onTrackClick(track: Track)
}