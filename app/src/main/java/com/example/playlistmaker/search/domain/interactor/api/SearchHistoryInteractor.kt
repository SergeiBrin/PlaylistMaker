package com.example.playlistmaker.search.domain.interactor.api

import com.example.playlistmaker.core.model.Track

interface SearchHistoryInteractor {
    var historyTrackList: MutableList<Track>

    suspend fun downloadSearchHistory()

    fun saveTrackInHistoryTrackList(track: Track)

    fun saveSearchHistoryInPreferences()

    fun deleteSearchHistory()

}