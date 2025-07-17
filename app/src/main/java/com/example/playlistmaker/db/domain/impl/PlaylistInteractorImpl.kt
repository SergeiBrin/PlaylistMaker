package com.example.playlistmaker.db.domain.impl

import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.db.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.db.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun isTrackInPlaylist(trackId: Int, playlistId: Int): Boolean {
        val trackIds = playlistRepository.getPlaylistById(playlistId)?.trackIds
        return trackIds?.contains(trackId) == true
    }

    override suspend fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistRepository.insertPlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        playlistRepository.addTrackToPlaylist(playlist, track)
    }
}