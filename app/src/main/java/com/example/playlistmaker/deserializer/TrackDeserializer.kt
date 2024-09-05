package com.example.playlistmaker.deserializer

import com.example.playlistmaker.model.Track
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Locale

class TrackDeserializer : JsonDeserializer<Track> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Track {
        val jsonObject = json.asJsonObject

        val trackId = jsonObject.get("trackId").asInt
        val trackName = jsonObject.get("trackName").asString
        val artistName = jsonObject.get("artistName").asString
        val trackTime = jsonObject.get("trackTimeMillis").asLong
        val artworkUrl100 = jsonObject.get("artworkUrl100").asString

        val modifiedTrackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)

        return Track(trackId, trackName, artistName, modifiedTrackTime, artworkUrl100)
    }
}