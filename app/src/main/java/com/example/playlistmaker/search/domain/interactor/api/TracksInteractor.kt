package com.example.playlistmaker.search.domain.interactor.api

import com.example.playlistmaker.core.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(searchText: String): Flow<List<Track>?>
}