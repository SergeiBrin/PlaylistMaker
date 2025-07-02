package com.example.playlistmaker.db.domain.interactor

import com.example.playlistmaker.core.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {

    fun getAllTracks(): Flow<List<Track>>

    suspend fun insertTrack(track: Track)

    suspend fun deleteTrack(track: Track)

}