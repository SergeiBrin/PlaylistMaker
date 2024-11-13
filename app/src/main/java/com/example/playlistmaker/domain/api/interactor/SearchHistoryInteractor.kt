package com.example.playlistmaker.domain.api.interactor

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    var historyTrackList: MutableList<Track>

    fun downloadSearchHistory()

    fun saveTrackInHistoryTrackList(track: Track)

    fun saveSearchHistoryInPreferences()

    fun deleteSearchHistory()

}