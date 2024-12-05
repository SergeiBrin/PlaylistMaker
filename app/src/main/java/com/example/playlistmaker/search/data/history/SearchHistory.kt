package com.example.playlistmaker.search.data.history
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.dto.SearchHistoryTrackDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SearchHistory(
    private val searchHistoryPref: SharedPreferences,
    private val gson: Gson
) {

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

    companion object {
        const val SEARCH_HISTORY_KEY = "SEARCH_HISTORY"
    }
}