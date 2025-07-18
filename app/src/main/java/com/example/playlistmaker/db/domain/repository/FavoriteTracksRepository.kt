package com.example.playlistmaker.db.domain.repository

import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.player.domain.PlayerUiState
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    suspend fun getTrackById(trackId: Int): PlayerUiState

    suspend fun getAllTracks(): Flow<List<Track>>

    suspend fun insertTrack(track: Track)

    suspend fun deleteTrack(track: Track)
}