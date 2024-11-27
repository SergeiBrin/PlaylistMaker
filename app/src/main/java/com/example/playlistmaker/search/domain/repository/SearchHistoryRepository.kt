package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.core.model.Track

interface SearchHistoryRepository {
    fun downloadSearchHistory(): List<Track>

    fun saveSearchHistory(tracks: List<Track>)

    fun deleteSearchHistory()
}