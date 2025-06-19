package com.example.playlistmaker.search.domain.interactor.impl

import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.search.domain.interactor.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository

class SearchHistoryInteractorImpl(
    private val dataBase: AppDatabase,
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {
    override var historyTrackList: MutableList<Track> = mutableListOf()

    override suspend fun downloadSearchHistory() {
        historyTrackList.clear()
        val searchHistory = repository.downloadSearchHistory()
        val ids = dataBase.trackDao().getAllTracksId()
        historyTrackList.addAll(
            searchHistory.map {
                it.copy(isFavorite = it.trackId in ids)
            }
        )
    }

    override fun saveTrackInHistoryTrackList(track: Track) {
        removeDuplicate(track)
        addTrackInHistory(track)
    }

    override fun saveSearchHistoryInPreferences() {
        repository.saveSearchHistory(historyTrackList)
    }

    override fun deleteSearchHistory() {
        repository.deleteSearchHistory()
        historyTrackList.clear()
    }

    private fun addTrackInHistory(track: Track) {
        historyTrackList.add(0, track)
        if (historyTrackList.size > 10) historyTrackList.removeAt(10)
    }

    private fun removeDuplicate(track: Track) {
        historyTrackList.removeIf {
            it.trackId == track.trackId
        }
    }
}