package com.example.playlistmaker.data.history
import android.content.Context
import com.example.playlistmaker.data.dto.SearchHistoryTrackDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(val context: Context) {
    val searchHistoryPref = com.example.playlistmaker.util.getSearchHistory(context)
    val gson = Gson()

    companion object {
        const val SEARCH_HISTORY_KEY = "SEARCH_HISTORY"
    }

    fun getSearchHistory(): List<SearchHistoryTrackDto> {
        val jsonTracks = getTracksFromHistory()
        var trackList = mutableListOf<SearchHistoryTrackDto>()

        if (jsonTracks != null) {
            val type = object : TypeToken<MutableList<SearchHistoryTrackDto>>() {}.type
            trackList = gson.fromJson(jsonTracks, type)
        }

        return trackList
    }

    fun saveSearchHistory(tracks: List<SearchHistoryTrackDto>) {
        val jsonTracks = gson.toJson(tracks)
        searchHistoryPref.edit()
            .putString(SEARCH_HISTORY_KEY, jsonTracks)
            .apply()
    }

    fun deleteSearchHistory() {
        searchHistoryPref.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }

    private fun getTracksFromHistory(): String? {
        return searchHistoryPref.getString(SEARCH_HISTORY_KEY, null)
    }
}