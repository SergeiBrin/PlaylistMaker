package com.example.playlistmaker.db.data.repository

import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.db.data.converters.TrackDbConverter
import com.example.playlistmaker.db.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.player.domain.PlayerUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    val dataBase: AppDatabase,
    val trackConverter: TrackDbConverter
) : FavoriteTracksRepository {

    override suspend fun getTrackById(trackId: Int): PlayerUiState {
        val trackEntity = dataBase.trackDao().getTrackById(trackId)
        return if (trackEntity != null) PlayerUiState.FavoriteTrackSuccessEvent else PlayerUiState.FavoriteTrackFailureEvent
    }

    override suspend fun getAllTracks(): Flow<List<Track>> {
        val trackEntities = dataBase.trackDao().getAllTracks()
        return trackEntities.map { trackConverter.map(it) }
    }

    override suspend fun insertTrack(track: Track) {
        val trackEntity = trackConverter.map(track)
        dataBase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun deleteTrack(track: Track) {
        val trackEntity = trackConverter.map(track)
        dataBase.trackDao().deleteTrack(trackEntity)
    }
}