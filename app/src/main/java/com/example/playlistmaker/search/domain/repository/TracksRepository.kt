package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.core.model.Track

interface TracksRepository {
    fun searchTracks(searchText: String): List<Track>?
}