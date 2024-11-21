package com.example.playlistmaker.search.domain.interactor.impl

import com.example.playlistmaker.search.domain.interactor.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.core.model.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {
    override var historyTrackList: MutableList<Track> = mutableListOf()

    override fun downloadSearchHistory() {
        historyTrackList.addAll(repository.downloadSearchHistory())
    }

    override fun saveTrackInHistoryTrackList(track: Track) {
        removeDuplicate(track)
        addTrackInHistory(track)
    }

    override fun saveSearchHistoryInPreferences() {
        repository.saveSearchHistory(historyTrackList)
    }

    override fun deleteSearchHistory() {
        repository.saveSearchHistory(historyTrackList)
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
}