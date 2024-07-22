package com.example.playlistmaker.model

import com.google.gson.annotations.SerializedName

class TrackResponse(
    val resultCount: Int,
    @SerializedName("results") val tracks: MutableList<Track>
)