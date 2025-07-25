package com.example.playlistmaker.db.domain.repository

import android.net.Uri
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.core.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun getPlaylistById(playlistId: Int): Playlist?

    suspend fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun getAllPlaylistsOnce(): List<Playlist>

    suspend fun getPlaylistTracksByIds(ids: List<Int>): Flow<List<Track>>

    suspend fun insertPlaylist(playlist: Playlist): Long

    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)

    suspend fun updatePlaylist(
        id: Int,
        playlistName: String,
        playlistDescription: String,
        playlistImageUri: Uri?
    ): Int

    suspend fun deleteTrackFromPlaylist(playlist: Playlist): Int

    suspend fun deletePlaylistTrackById(trackId: Int)

    suspend fun deletePlaylistTracksByIds(trackIds: List<Int>)

    suspend fun deletePlaylistById(playlistId: Int): Int

}