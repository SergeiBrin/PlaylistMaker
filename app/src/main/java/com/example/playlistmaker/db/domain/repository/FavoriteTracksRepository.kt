package com.example.playlistmaker.db.domain.repository

import com.example.playlistmaker.core.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    fun getAllTracks(): Flow<List<Track>>

    suspend fun insertTrack(track: Track)

    suspend fun deleteTrack(track: Track)
}