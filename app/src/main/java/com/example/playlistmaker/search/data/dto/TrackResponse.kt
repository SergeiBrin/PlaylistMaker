package com.example.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName

class TrackResponse(
    @SerializedName("results") val tracks: MutableList<TrackDto>
) : Response()