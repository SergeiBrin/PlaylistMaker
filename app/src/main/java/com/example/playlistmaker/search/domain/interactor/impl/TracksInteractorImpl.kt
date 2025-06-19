package com.example.playlistmaker.search.domain.interactor.impl

import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.search.domain.common.Resource
import com.example.playlistmaker.search.domain.interactor.api.TracksInteractor
import com.example.playlistmaker.search.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(
    private val database: AppDatabase,
    private val tracksRepository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(searchText: String): Flow<List<Track>?> = flow {
        val ids = database.trackDao().getAllTracksId()

        emitAll(
            tracksRepository.searchTracks(searchText).map { result ->
                when (result) {
                    is Resource.Success -> result.data?.map { it.copy(isFavorite = it.trackId in ids) }
                    is Resource.Error -> null
                }
            }
        )
    }
}