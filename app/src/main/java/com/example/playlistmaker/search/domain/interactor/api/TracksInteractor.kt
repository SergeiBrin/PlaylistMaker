package com.example.playlistmaker.search.domain.interactor.api

import com.example.playlistmaker.search.domain.common.Resource
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(searchText: String): Flow<Resource>
}