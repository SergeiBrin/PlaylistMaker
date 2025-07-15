package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.search.data.dto.SearchHistoryTrackDto
import com.example.playlistmaker.search.data.history.SearchHistory
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val searchHistory: SearchHistory
) : SearchHistoryRepository {
    override fun downloadSearchHistory(): List<Track> {
        return searchHistory.getSearchHistory().map {
            Track(
                it.trackId,
                it.trackName,
                it.artistName,
                it.trackTime,
                it.artworkUrl100,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
    }

    override fun saveSearchHistory(tracks: List<Track>) {
        val tracksDto = tracks.map {
            SearchHistoryTrackDto(
                it.trackId,
                it.trackName,
                it.artistName,
                it.trackTime,
                it.artworkUrl100,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
        searchHistory.saveSearchHistory(tracksDto)
    }

    override fun deleteSearchHistory() {
        searchHistory.deleteSearchHistory()
    }
}