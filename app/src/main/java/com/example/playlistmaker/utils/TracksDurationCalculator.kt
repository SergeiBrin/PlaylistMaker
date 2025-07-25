package com.example.playlistmaker.utils

import com.example.playlistmaker.core.model.Track

fun calculateTotalDurationInMinutes(tracks: List<Track>): Int {
    var minutes = 0
    var seconds = 0

    tracks.forEach {
        val time = it.trackTime
        val split = time.split(":")
        if (split.size == 2) {
            val min = split[0].toIntOrNull()
            val sec = split[1].toIntOrNull()
            if (min != null && sec != null) {
                minutes += min
                seconds += sec
            }
        }
    }

    return minutes + seconds / 60
}