package com.example.playlistmaker.db.domain.impl

import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.db.domain.interactor.FavoriteTracksInteractor
import com.example.playlistmaker.db.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.player.ui.result.GetTrackResult
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override suspend fun getTrackById(trackId: Int): GetTrackResult {
        return favoriteTracksRepository.getTrackById(trackId)
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.getAllTracks()
    }

    override suspend fun insertTrack(track: Track) {
        favoriteTracksRepository.insertTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        favoriteTracksRepository.deleteTrack(track)
    }
}