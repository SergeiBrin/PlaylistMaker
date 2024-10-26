package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.models.Track
import com.google.gson.annotations.SerializedName

class TrackResponse(
    val resultCount: Int,
    @SerializedName("results") val tracks: MutableList<TrackDto>
) : Response()