package com.example.playlistmaker.db.data.repository

import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.db.data.converters.TrackDbConverter
import com.example.playlistmaker.db.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    val dataBase: AppDatabase,
    val trackConverter: TrackDbConverter
) : FavoriteTracksRepository {

    override fun getAllTracks(): Flow<List<Track>> = flow {
        val trackEntities = dataBase.trackDao().getAllTracks()
        val tracks = trackConverter.map(trackEntities)
        emit(tracks)
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