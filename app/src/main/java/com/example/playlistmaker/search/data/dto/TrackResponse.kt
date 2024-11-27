package com.example.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName

class TrackResponse(
    val resultCount: Int,
    @SerializedName("results") val tracks: MutableList<TrackDto>
) : Response()