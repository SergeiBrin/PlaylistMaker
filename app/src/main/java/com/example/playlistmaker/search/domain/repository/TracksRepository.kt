package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.common.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(searchText: String): Flow<Resource>
}