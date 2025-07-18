package com.example.playlistmaker.db.domain.interactor

import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.core.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun isTrackInPlaylist(trackId: Int, playlistId: Int): Boolean

    suspend fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)

}