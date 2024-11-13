package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(searchText: String): List<Track>?
}