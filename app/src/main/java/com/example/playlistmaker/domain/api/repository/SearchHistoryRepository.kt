package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun downloadSearchHistory(): List<Track>

    fun saveSearchHistory(tracks: List<Track>)

    fun deleteSearchHistory()
}