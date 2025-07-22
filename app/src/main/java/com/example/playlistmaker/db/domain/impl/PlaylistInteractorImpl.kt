package com.example.playlistmaker.db.domain.impl

import android.net.Uri
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

    override suspend fun getPlaylistById(playlistId: Int): Playlist? {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override suspend fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override suspend fun insertPlaylist(playlist: Playlist): Long {
        return playlistRepository.insertPlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        playlistRepository.addTrackToPlaylist(playlist, track)
    }

    override suspend fun updatePlaylist(
        id: Int,
        playlistName: String,
        playlistDescription: String,
        playlistImageUri: Uri?
    ): Int {
        return playlistRepository.updatePlaylist(id, playlistName, playlistDescription, playlistImageUri)
    }

    override suspend fun getPlaylistTracksByIds(ids: List<Int>): Flow<List<Track>> {
        return playlistRepository.getPlaylistTracksByIds(ids)
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int): Int {
        val playlist = playlistRepository.getPlaylistById(playlistId)

        val isRemove = playlist?.trackIds?.remove(trackId)
        return if (isRemove == true) playlistRepository.deleteTrackFromPlaylist(playlist) else 0
    }

    override suspend fun deleteTrackIfUnused(trackId: Int) {
        val playlists = playlistRepository.getAllPlaylistsOnce()

        val exists = playlists.any { trackId in it.trackIds }
        if (!exists) playlistRepository.deletePlaylistTrackById(trackId)
    }

    override suspend fun deleteTracksIfUnused(trackIds: List<Int>) {
        val playlists = playlistRepository.getAllPlaylistsOnce()
        val allPlaylistTrackIds = playlists.flatMap { it.trackIds }.toSet()
        val trackIfUnusedIds = trackIds.filter { it !in allPlaylistTrackIds }

        if (trackIfUnusedIds.isNotEmpty()) {
            playlistRepository.deletePlaylistTracksByIds(trackIfUnusedIds)
        }
    }

    override suspend fun deletePlaylistById(playlistId: Int): Int {
        return playlistRepository.deletePlaylistById(playlistId)
    }
}