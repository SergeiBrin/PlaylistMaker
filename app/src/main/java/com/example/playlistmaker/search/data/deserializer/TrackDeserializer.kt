package com.example.playlistmaker.search.data.deserializer

import com.example.playlistmaker.search.data.dto.TrackDto
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Locale

class TrackDeserializer : JsonDeserializer<TrackDto> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): TrackDto {
        val jsonObject = json.asJsonObject

        val trackId = jsonObject.get("trackId").asInt
        val trackName = jsonObject.get("trackName").asString
        val artistName = jsonObject.get("artistName").asString
        val trackTime = jsonObject.get("trackTimeMillis").asLong
        val artworkUrl100 = jsonObject.get("artworkUrl100").asString
        val collectionName = jsonObject.get("collectionName").asString
        val releaseDate = jsonObject.get("releaseDate").asString
        val primaryGenreName = jsonObject.get("primaryGenreName").asString
        val country = jsonObject.get("country").asString
        val previewUrl = jsonObject.get("previewUrl").asString

        val modifiedTrackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)

        return TrackDto(
            trackId,
            trackName,
            artistName,
            modifiedTrackTime,
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl
        )
    }
}