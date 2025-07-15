package com.example.playlistmaker.db.domain.repository

import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.player.ui.result.GetTrackResult
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    suspend fun getTrackById(trackId: Int): GetTrackResult

    fun getAllTracks(): Flow<List<Track>>

    suspend fun insertTrack(track: Track)

    suspend fun deleteTrack(track: Track)
}