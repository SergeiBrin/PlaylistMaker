package com.example.playlistmaker.search.ui.funinterface

import com.example.playlistmaker.core.model.Track

fun interface TrackClickListener {
    fun onTrackClick(track: Track)
}