package com.example.playlistmaker.sharedpref
import android.content.SharedPreferences
import com.example.playlistmaker.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(val searchHistoryPref: SharedPreferences) {
    val gson = Gson()

    companion object {
        const val SEARCH_HISTORY_KEY = "SEARCH_HISTORY"
        var historyTrackList: MutableList<Track> = mutableListOf()
    }

    fun downloadSearchHistory() {
        val jsonTracks = getTracksFromHistory()

        if (jsonTracks != null) {
            val type = object : TypeToken<MutableList<Track>>() {}.type
            historyTrackList = gson.fromJson(jsonTracks, type)
        }
    }

    fun saveTrackInHistoryTrackList(track: Track) {
        removeDuplicate(track)
        addTrackInHistory(track)
    }

    fun saveSearchHistoryInPreferences() {
        val jsonTracks = gson.toJson(historyTrackList)
        searchHistoryPref.edit()
            .putString(SEARCH_HISTORY_KEY, jsonTracks)
            .apply()
    }

    fun deleteSearchHistory() {
        searchHistoryPref.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()

        historyTrackList.clear()
    }

    private fun addTrackInHistory(track: Track) {
        historyTrackList.add(0, track)
        if (historyTrackList.size > 10) historyTrackList.removeAt(10)
    }

    private fun removeDuplicate(track: Track) {
        if (track in historyTrackList) {
            historyTrackList.remove(track)
        }
    }

    private fun getTracksFromHistory(): String? {
        return searchHistoryPref.getString(SEARCH_HISTORY_KEY, null)
    }
}